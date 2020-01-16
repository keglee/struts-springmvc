package com.iversonx.struts_springmvc.extend;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/16 15:56
 */
public class ActionRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {

    @Override
    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
        // 每次请求，都会调用这个方法
        ConversionService conversionService = getApplicationContext().getBean(ConversionService.class);
        MappingJackson2HttpMessageConverter messageConverter = getApplicationContext().getBean(MappingJackson2HttpMessageConverter.class);
        return new ActionInvocableHandlerMethod(handlerMethod, conversionService, messageConverter);
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        List<HandlerMethodReturnValueHandler> returnValueHandlers = getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>(returnValueHandlers);
        handlers.add(0, new ActionViewMethodReturnValueHandler(getApplicationContext().getBean(ActionConfigManager.class)));
        setReturnValueHandlers(handlers);
    }
}
