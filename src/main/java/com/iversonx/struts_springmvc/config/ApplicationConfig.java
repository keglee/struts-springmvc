package com.iversonx.struts_springmvc.config;

import com.iversonx.struts_springmvc.extend.ActionBeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/9 9:48
 */
@Configuration
@ComponentScan("com.iversonx.struts_springmvc")
public class ApplicationConfig {

    public ActionBeanDefinitionRegistryPostProcessor actionBeanDefinitionRegistryPostProcessor() {
        return new ActionBeanDefinitionRegistryPostProcessor();
    }
}
