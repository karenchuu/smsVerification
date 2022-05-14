package com.karenchu.smsVerificationCode.controller;

import com.karenchu.smsVerificationCode.service.IMobileValidateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vo.ResponseVo;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class MobileValidateController {


    @Autowired
    private IMobileValidateService mobileValidateService;

    /**
     *
     * 獲取訪客IP地址
     */
    public String getAccessIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    /** *  Send mobile phone verification code  */
    @ResponseBody
    @GetMapping("sendCode")
    public ResponseVo sendCode(@RequestParam("phoneNumber") String phoneNumber, HttpServletRequest request) {
        String accessIP = this.getAccessIP(request);
        return mobileValidateService.validate(phoneNumber, accessIP);
    }

    /**
     * 驗證手機驗證碼
     */
    @GetMapping("validate")
    @ResponseBody
    public ResponseVo validate(@RequestParam("code") String code,
                           @RequestParam("phoneNumber") String phoneNumber) {
        return mobileValidateService.validate(code, phoneNumber);
    }
}
