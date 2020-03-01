package com.iversonx.struts_springmvc.support;

import com.opensymphony.xwork2.ActionSupport;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;


public class ActionMappingHandlerAdapter extends RequestMappingHandlerAdapter {
    @Override
    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
        ConversionService conversionService = getApplicationContext().getBean(ConversionService.class);
        MappingJackson2HttpMessageConverter messageConverter = getApplicationContext().getBean(MappingJackson2HttpMessageConverter.class);
        ActionReturnValueHandler returnValueHandler = getApplicationContext().getBean(ActionReturnValueHandler.class);
        return new ActionInvocableHandlerMethod(handlerMethod, conversionService, messageConverter, returnValueHandler);
    }

    @Override
    protected boolean supportsInternal(HandlerMethod handlerMethod) {
        return handlerMethod.getBeanType().getSuperclass().equals(ActionSupport.class);
    }
}
