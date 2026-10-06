package com.shengyi.exception;

/**
 * 业务异常，携带状态码
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(StatusCode statusCode) {
        super(statusCode.getMessage());
        this.code = statusCode.getCode();
    }

    public BusinessException(StatusCode statusCode, String message) {
        super(message);
        this.code = statusCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
