package com.karenchu.smsVerificationCode.enums;

public enum ResponseEnum {

    ERROR(-1, "服務端錯誤"),

    SUCCESS(0, "成功"),

    SMS_VERIFICATION_CODE_GET_ERROR(1, "驗證碼獲取失敗"),

    SMS_VERIFICATION_CODE_VALIDATE_LIMIT(2, "超過校驗次數，有惡意訪問行為，已被系統限制訪問"),

    SMS_VERIFICATION_CODE_VALIDATE_CODE_ERROR(3, "驗證碼校驗失敗"),

    ;

    Integer code;

    String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    ResponseEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
