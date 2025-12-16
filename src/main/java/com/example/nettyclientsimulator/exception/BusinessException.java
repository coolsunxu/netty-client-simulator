package com.example.nettyclientsimulator.exception;

/**
 * 业务异常基类
 *
 * @author sunxu
 */
public class BusinessException extends RuntimeException {

    private final int errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = 500;
    }

    public BusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 500;
    }

    public BusinessException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
