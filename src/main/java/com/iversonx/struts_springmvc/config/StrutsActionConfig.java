package com.iversonx.struts_springmvc.config;

import com.iversonx.struts_springmvc.listener.ActionMappingListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/10 10:05
 */
@Configuration
public class StrutsActionConfig {

    @Bean
    public ActionMappingListener actionMappingListener() {
        return new ActionMappingListener();
    }
}
