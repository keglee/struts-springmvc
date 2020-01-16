package com.iversonx.struts_springmvc.config;


import com.iversonx.struts_springmvc.converter.StringToDateConverter;
import com.iversonx.struts_springmvc.extend.*;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 1. 处理器映射（HandlerMapping）用于将一个请求（Request）映射到一个处理器。
 * 2. 处理器适配器（HandlerAdapter）用于转接一个控制流到一个指定类型的处理器
 * 3. Handler | Controller
 * 4. 视图解析器(ViewResolver) 用于映射一个逻辑视图名称到一个真正的视图
 * 5. 视图(View)
 */
@Configuration
@ComponentScan("com.iversonx.struts_springmvc")
public class WebMvcConfig extends DelegatingWebMvcConfiguration {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp().prefix("/").suffix(".jsp");
    }

    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        ActionConfigManager actionConfigManager = getApplicationContext().getBean(ActionConfigManager.class);
        return new ActionRequestMappingHandlerMapping(actionConfigManager);
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        ConversionService conversionService = getApplicationContext().getBean(ConversionService.class);
        MappingJackson2HttpMessageConverter messageConverter = getApplicationContext().getBean(MappingJackson2HttpMessageConverter.class);
        registry.addInterceptor(new ActionHandlerInterceptor(conversionService, messageConverter)).addPathPatterns("/**/*.action");
    }

    @Override
    public FormattingConversionService mvcConversionService() {
        FormattingConversionService conversionService = super.mvcConversionService();
        conversionService.addConverter(new StringToDateConverter());
        return conversionService;
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        MappingJackson2HttpMessageConverter messageConverter = getApplicationContext().getBean(MappingJackson2HttpMessageConverter.class);
        converters.add(messageConverter);
    }

    @Override
    protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
        return new ActionRequestMappingHandlerAdapter();
    }

}
