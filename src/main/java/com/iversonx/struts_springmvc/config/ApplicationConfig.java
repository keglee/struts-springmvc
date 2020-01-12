package com.iversonx.struts_springmvc.config;

import com.iversonx.struts_springmvc.extend.ActionBeanDefinitionRegistryPostProcessor;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

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

    @Bean
    public List<ActionConfig> actionConfigs() {
        return new ArrayList<>(256);
    }
}
