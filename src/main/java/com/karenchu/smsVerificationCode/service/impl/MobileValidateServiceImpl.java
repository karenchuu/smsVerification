package com.karenchu.smsVerificationCode.service.impl;

import com.karenchu.smsVerificationCode.enums.ResponseEnum;
import com.karenchu.smsVerificationCode.service.IMobileValidateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import vo.ResponseVo;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MobileValidateServiceImpl implements IMobileValidateService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //@Resource(name = "redisTemplate")
    //ValueOperations<String, String> redisString;


    /**手機驗證碼過期時間*/
    static long PHONE_NUMBER_CODE_EXPIRE_SECONDS = 120;
    /**保護key的過期時間 5分鐘*/
    static long PHONE_PROTECT_CODE_EXPIRE_MINUTES = 5;
    /**手機驗證碼保護模式： IP鎖定時間: 12小時*/
    static long PHONE_CODE_IP_LOCK_EXPIRE_HOURS = 12;

    /**
     * 生成驗證碼 4位（調用手機號API短信接口）
     */
    public int generatorCode() {
        int num = (int)(Math.random() * 10000);
        return num;
    }

    @Override
    public ResponseVo sendCode(String phoneNumber, String accessIP) {

        /**
         * 每個IP地址在5分鐘內只能驗證3次，且給相應訊息顯示。鎖定這個IP 12小時
         * 保護模式（手機短信資費）
         */
        ResponseVo result = this.protectPhoneCode(accessIP);
        if (!result.equals(ResponseVo.success())) {
            return result;
        }

        // 手機驗證碼 Redis KEY
        String key = "phone:code:" + phoneNumber;

        if (!redisTemplate.hasKey(key)) {
            int phoneCode = this.generatorCode();
            log.info("手機號已發送API短信接口，驗證碼是" + phoneCode);
            redisTemplate.opsForValue().set(key, String.valueOf(phoneCode));
            redisTemplate.expire(key, PHONE_NUMBER_CODE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            return ResponseVo.success("驗證碼發送成功，請查看手機簡訊");
        } else {
            long timeNum = redisTemplate.getExpire(key);
            return ResponseVo.error(ResponseEnum.SMS_VERIFICATION_CODE_GET_ERROR,
                                ": 請避免重複獲取，耐心等候，還剩時間" + timeNum + "秒");
        }
    }

    @Override
    public ResponseVo validate(String code, String phoneNumber) {
        // 手機驗證碼 Redis KEY
        String key = "phone:code:" + phoneNumber;

        if (!redisTemplate.hasKey(key)){
            return ResponseVo.error(ResponseEnum.SMS_VERIFICATION_CODE_VALIDATE_CODE_ERROR,
                    "請發送手機驗證碼");
        }

        if (!code.equals(redisTemplate.opsForValue().get(key))) {
            return ResponseVo.error(ResponseEnum.SMS_VERIFICATION_CODE_VALIDATE_CODE_ERROR);
        }

        log.info("手機號碼校驗成功，執行登入等相應業務邏輯");
        // 清空redis 節省內存空間
        redisTemplate.delete(key);
        return ResponseVo.success("手機號碼校驗成功/登入成功");
    }

    /**
     * 每個IP地址在5分鐘內只能驗證3次，且給相應訊息顯示。鎖定這個IP 12小時
     * 保護模式（手機短信資費）
     * 1. 生成保護key phone:code:ip
     * 2. 判斷保護key是否存在
     *    如果不存在，進行+1，並設定過期時間5分鐘
     *    如果存在，進行+1
     * 3. 判斷保護key是否大於3
     *    如果大於，生成一個新的鎖IP KEY phone:code:lock:ip 過期時間為12小時
     */
    public ResponseVo protectPhoneCode(String accessIP) {
        // 全局KEY判斷當前操作IP是否被鎖定
        // 要鎖定12小時
        String phoneCodeIPLockKey = "phone:code:lock" + accessIP;
        if (redisTemplate.hasKey(phoneCodeIPLockKey)) {
            return ResponseVo.error(ResponseEnum.SMS_VERIFICATION_CODE_VALIDATE_LIMIT);
        }

        String protectKey = "phone:code:" + accessIP;
        if (!redisTemplate.hasKey(protectKey)) {
            redisTemplate.opsForValue().increment(protectKey);
            redisTemplate.expire(protectKey, PHONE_PROTECT_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        } else {
            redisTemplate.opsForValue().increment(protectKey);
        }
        if (Integer.parseInt(redisTemplate.opsForValue().get(protectKey)) > 3) {
            redisTemplate.opsForValue().set(phoneCodeIPLockKey, phoneCodeIPLockKey);
            redisTemplate.expire(phoneCodeIPLockKey, PHONE_CODE_IP_LOCK_EXPIRE_HOURS, TimeUnit.HOURS);
            return ResponseVo.error(ResponseEnum.SMS_VERIFICATION_CODE_VALIDATE_LIMIT);
        }
        return ResponseVo.success();
    }
}
