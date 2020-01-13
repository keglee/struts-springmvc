package com.iversonx.struts_springmvc.config;

import com.iversonx.struts_springmvc.extend.ActionBeanDefinitionRegistryPostProcessor;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping;

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
@ComponentScan("com.iversonx.struts_springmvc")
public class ApplicationConfig {

    @Bean
    public static ActionBeanDefinitionRegistryPostProcessor actionBeanDefinitionRegistryPostProcessor() {
        return new ActionBeanDefinitionRegistryPostProcessor();
    }

    @Bean("actionConfigMap")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public Map<String, Map<String, ActionConfig>> actionConfigMap() {
        return new HashMap<>(256);
    }

    @Bean("actionConfigs")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public List<ActionConfig> actionConfigs() {
        return new ArrayList<>(256);
    }
}
