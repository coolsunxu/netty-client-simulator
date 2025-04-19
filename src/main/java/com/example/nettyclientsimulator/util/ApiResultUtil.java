package com.example.nettyclientsimulator.util;

/**
 * @author sunxu
 */
public class ApiResultUtil {

    private ApiResultUtil() {
    }

    /**
     * 请求成功
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 接口相应实体
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(ApiResult.OK, null, data);
    }

    /**
     * 请求成功
     *
     * @param <T> 数据类型
     * @return 接口相应实体
     */
    public static <T> ApiResult<T> success() {
        return success(null);
    }

    /**
     * 请求成功
     *
     * @param code    返回码
     * @param message 返回信息
     * @param <T>     数据类型
     * @return 接口相应实体
     */
    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }
}

