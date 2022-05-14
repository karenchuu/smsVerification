package com.karenchu.smsVerificationCode.service;

import org.springframework.stereotype.Service;
import vo.ResponseVo;

@Service
public interface IMobileValidateService {

    ResponseVo sendCode(String phoneNumber, String accessIP);

    ResponseVo validate(String code, String phoneNumber);
}
