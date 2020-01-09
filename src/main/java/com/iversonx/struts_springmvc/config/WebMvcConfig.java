package com.iversonx.struts_springmvc.config;

import com.iversonx.struts_springmvc.extend.ActionMappingHandlerMapping;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

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
        return new ActionMappingHandlerMapping();
    }
}
