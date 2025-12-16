package com.example.nettyclientsimulator.webhandler;


import com.example.nettyclientsimulator.exception.BusinessException;
import com.example.nettyclientsimulator.exception.ClientNotFoundException;
import com.example.nettyclientsimulator.exception.ConnectionException;
import com.example.nettyclientsimulator.util.ApiResult;
import com.example.nettyclientsimulator.util.ApiResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理器
 *
 * @author sunxu
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理客户端未找到异常
     */
    @ExceptionHandler(ClientNotFoundException.class)
    public ApiResult handleClientNotFound(ClientNotFoundException e) {
        log.warn("Client not found: {}", e.getClientId());
        return ApiResultUtil.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理连接异常
     */
    @ExceptionHandler(ConnectionException.class)
    public ApiResult handleConnectionException(ConnectionException e) {
        log.error("Connection error to {}:{}", e.getHost(), e.getPort(), e);
        return ApiResultUtil.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 统一处理业务异常
     *
     * @param e 异常
     * @return API请求响应实体
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult handleBusinessException(BusinessException e) {
        log.info("请求出现业务异常：", e);
        return ApiResultUtil.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 统一处理其他异常
     *
     * @param e 异常
     * @return API请求响应实体
     */
    @ExceptionHandler(Throwable.class)
    public ApiResult handleException(Throwable e) {
        log.error("请求出现系统异常：", e);
        return ApiResultUtil.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal service error, please contact the administrator!");
    }

}
