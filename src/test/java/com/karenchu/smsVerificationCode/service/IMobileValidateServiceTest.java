package com.karenchu.smsVerificationCode.service;


import com.karenchu.smsVerificationCode.SmsVerificationCodeApplicationTests;
import com.karenchu.smsVerificationCode.enums.ResponseEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import vo.ResponseVo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class IMobileValidateServiceTest extends SmsVerificationCodeApplicationTests {

    @Autowired
    private IMobileValidateService mobileValidateService;

    public static final String IP = "10.53.147.53";
    public static final String CODE = "6462";
    public static final String PHONE_NUMBER = "0912345678";

    @Test
    public void sendCode() {
        ResponseVo responseVo = mobileValidateService.sendCode(PHONE_NUMBER, IP);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void validate() {
        ResponseVo responseVo = mobileValidateService.validate(CODE, PHONE_NUMBER);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }
}