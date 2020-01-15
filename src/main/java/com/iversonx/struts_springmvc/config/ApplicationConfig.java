package com.iversonx.struts_springmvc.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iversonx.struts_springmvc.extend.ActionBeanDefinitionRegistryPostProcessor;
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

    @Bean("actionConfigMap")
    public Map<String, Map<String, ActionConfig>> actionConfigMap() {
        System.out.println("@Bean actionConfigMap");
        return new HashMap<>(256);
    }

    @Bean
    public ActionBeanDefinitionRegistryPostProcessor actionBeanDefinitionRegistryPostProcessor(Map<String, Map<String, ActionConfig>> actionConfigMap) {
        System.out.println("@Bean actionBeanDefinitionRegistryPostProcessor");
        return new ActionBeanDefinitionRegistryPostProcessor(actionConfigMap);
    }

    @Bean("actionConfigs")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public List<ActionConfig> actionConfigs() {
        System.out.println("@Bean actionConfigs");
        return new ArrayList<>(256);
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        System.out.println("@Bean mappingJackson2HttpMessageConverter");
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        return messageConverter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        System.out.println("@Bean objectMapper");
        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略未知的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return objectMapper;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        System.out.println("@Bean multipartResolver");
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        return resolver;
    }
}
