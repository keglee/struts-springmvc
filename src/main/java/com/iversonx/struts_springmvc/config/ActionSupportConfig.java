package com.iversonx.struts_springmvc.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iversonx.struts_springmvc.support.ActionMappingHandlerAdapter;
import com.iversonx.struts_springmvc.support.ActionMappingHandlerMapping;
import com.iversonx.struts_springmvc.support.ActionReturnValueHandler;
import com.iversonx.struts_springmvc.support.processor.ActionBeanDefinitionRegistryPostProcessor;
import com.iversonx.struts_springmvc.support.processor.ActionConfigManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.text.SimpleDateFormat;

@Configuration
public class ActionSupportConfig {

    @Bean
    public ActionConfigManager actionConfigManager() {
        return new ActionConfigManager();
    }

    @Bean
    public ActionBeanDefinitionRegistryPostProcessor actionBeanDefinitionRegistryPostProcessor(ActionConfigManager actionConfigManager) {
        return new ActionBeanDefinitionRegistryPostProcessor(actionConfigManager);
    }

    @Bean
    public ActionMappingHandlerMapping actionMappingHandlerMapping(ActionConfigManager actionConfigManager) {
        return new ActionMappingHandlerMapping(actionConfigManager);
    }

    @Bean
    public ActionMappingHandlerAdapter actionMappingHandlerAdapter() {
        ActionMappingHandlerAdapter adapter = new ActionMappingHandlerAdapter();
        adapter.setOrder(0);
        return adapter;
    }

    @Bean
    public ActionReturnValueHandler actionReturnValueHandler() {
        return new ActionReturnValueHandler();
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
