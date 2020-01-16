package com.iversonx.struts_springmvc.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iversonx.struts_springmvc.extend.ActionBeanDefinitionRegistryPostProcessor;
import com.iversonx.struts_springmvc.extend.ActionConfigManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/9 9:48
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public ActionConfigManager actionConfigManager() {
        return new ActionConfigManager();
    }

    @Bean
    public ActionBeanDefinitionRegistryPostProcessor actionBeanDefinitionRegistryPostProcessor(ActionConfigManager actionConfigManager) {
        return new ActionBeanDefinitionRegistryPostProcessor(actionConfigManager);
    }


    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        return messageConverter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略未知的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return objectMapper;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        return resolver;
    }
}
