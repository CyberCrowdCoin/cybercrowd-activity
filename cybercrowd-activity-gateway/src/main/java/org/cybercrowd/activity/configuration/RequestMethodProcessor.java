package org.cybercrowd.activity.configuration;

import org.cybercrowd.activity.annotation.TwitterRequsetParam;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.List;

public class RequestMethodProcessor implements HandlerMethodArgumentResolver {

    private RequestResponseBodyMethodProcessor jsonResolver;
    private ServletModelAttributeMethodProcessor formResolver;

    public RequestMethodProcessor() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        RequestMessageConverter RequestMessageConverter = new RequestMessageConverter();
        messageConverters.add(RequestMessageConverter);

        jsonResolver = new RequestResponseBodyMethodProcessor(messageConverters);
        formResolver = new ServletModelAttributeMethodProcessor(true);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        TwitterRequsetParam ann = parameter.getParameterAnnotation(TwitterRequsetParam.class);
        return (ann != null);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        ServletRequest servletRequest = nativeWebRequest.getNativeRequest(ServletRequest.class);
        String contentType = servletRequest.getContentType();

        if (contentType == null) {
            return formResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }

        if (contentType.contains("application/json")) {
            return jsonResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }

        if (contentType.contains("application/x-www-form-urlencoded")) {
            return formResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }

        if (contentType.contains("multipart")) {
            return formResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }

        throw new IllegalArgumentException("不支持contentType");
    }
}
