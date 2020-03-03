package com.iversonx.struts_springmvc.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iversonx.struts_springmvc.support.StrutsMappingHandlerAdapter;
import com.iversonx.struts_springmvc.support.StrutsMappingHandlerMapping;
import com.iversonx.struts_springmvc.support.processor.StrutsBeanDefinitionRegistryPostProcessor;
import com.iversonx.struts_springmvc.support.processor.StrutsConfigManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.text.SimpleDateFormat;

@Configuration
public class ActionSupportConfig {

    @Bean
    public StrutsConfigManager actionConfigManager() {
        return new StrutsConfigManager();
    }

    @Bean
    public StrutsBeanDefinitionRegistryPostProcessor actionBeanDefinitionRegistryPostProcessor(StrutsConfigManager actionConfigManager) {
        return new StrutsBeanDefinitionRegistryPostProcessor(actionConfigManager);
    }

    @Bean
    public StrutsMappingHandlerMapping actionMappingHandlerMapping(StrutsConfigManager actionConfigManager) {
        return new StrutsMappingHandlerMapping(actionConfigManager);
    }

    @Bean
    public StrutsMappingHandlerAdapter actionMappingHandlerAdapter() {
        StrutsMappingHandlerAdapter adapter = new StrutsMappingHandlerAdapter();
        adapter.setOrder(0);
        return adapter;
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
