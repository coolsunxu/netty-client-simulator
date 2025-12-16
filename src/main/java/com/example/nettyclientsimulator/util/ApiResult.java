package com.example.nettyclientsimulator.util;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author sunxu
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiResult<T> {

    /**
     * 请求成功状态码
     */
    public static final int OK = HttpStatus.OK.value();

    /**
     * 接口返回码
     */
    private int code;

    /**
     * 接口返回信息
     */
    private String message;

    /**
     * 数据
     */
    private T data;
}

