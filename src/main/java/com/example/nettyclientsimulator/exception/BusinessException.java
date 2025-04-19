package com.example.nettyclientsimulator.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/**
 * @author sunxu
 */

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class BusinessException extends RuntimeException {

    public static final long serialVersionUID = -6735897190745766939L;

    /**
     * 异常码
     */
    private int code;

    /**
     * 具体异常信息
     */
    private String message;

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.message = message;
    }
}
