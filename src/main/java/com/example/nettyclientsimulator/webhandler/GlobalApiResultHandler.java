package com.example.nettyclientsimulator.webhandler;


import com.example.nettyclientsimulator.util.ApiResult;
import com.example.nettyclientsimulator.util.ApiResultUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;


/**
 * @author sunxu
 */
@ControllerAdvice
public class GlobalApiResultHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert sra != null;
        HttpServletRequest request = sra.getRequest();
        String requestUrl = request.getRequestURI();
        return matchUrl(requestUrl);
    }


    /**
     * 对接口路径做校验,此处默认返回true
     * @param url url
     * @return 是否匹配
     */
    private boolean matchUrl(String url) {
        return !url.contains("prometheus");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResult) {
            return body;
        }
        return ApiResultUtil.success(body);
    }
}
