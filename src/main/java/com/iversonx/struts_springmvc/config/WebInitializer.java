package com.iversonx.struts_springmvc.config;


import javax.servlet.*;
import java.util.EnumSet;
import java.util.Set;

public class WebInitializer implements ServletContainerInitializer {

    public void onStartup(Set<Class<?>> set, ServletContext ctx) throws ServletException {
        ctx.setInitParameter("contextConfigLocation", "classpath:application.xml");
        ctx.addListener("org.springframework.web.context.ContextLoaderListener");
        FilterRegistration.Dynamic filter = ctx.addFilter("struts2", "org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter");
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*.action");
    }
}
