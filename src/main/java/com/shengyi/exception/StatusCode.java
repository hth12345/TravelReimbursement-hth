package com.shengyi.exception;

/**
 * 统一状态码枚举
 */
public enum StatusCode {

    /** 成功 */
    SUCCESS(20000, "成功"),

    /** 必填项为空 */
    REQUIRED_FIELD_EMPTY(50001, "必填项为空"),

    /** 金额超标准 */
    AMOUNT_EXCEED_STANDARD(50021, "金额超标准"),

    /** 前后金额不一致 */
    AMOUNT_MISMATCH(50022, "前后金额不一致"),

    /** 日期重叠 */
    DATE_OVERLAP(50031, "日期重叠"),

    /** 系统异常 */
    SYSTEM_ERROR(60001, "系统异常");

    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
