package com.iversonx.struts_springmvc.config;

import com.iversonx.struts_springmvc.controller.BeanNameController;
import com.iversonx.struts_springmvc.controller.simple.SimpleController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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


    @Autowired
    private SimpleController simpleController;
    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        Map<String, Object> map = new HashMap<>();
        map.put("/simple", "simpleController");
        map.put("/simple2", simpleController);
        mapping.setUrlMap(map);
        return mapping;
    }
}
