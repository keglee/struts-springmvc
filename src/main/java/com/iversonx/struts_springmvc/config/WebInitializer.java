package com.iversonx.struts_springmvc.config;

import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.*;
import java.util.EnumSet;

public class WebInitializer extends AbstractContextLoaderInitializer {

    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        struts(servletContext);
    }

    //@Override
    protected WebApplicationContext createRootApplicationContext() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(ApplicationConfig.class);
        return ctx;
    }

    private void struts(ServletContext servletContext) {
        FilterRegistration.Dynamic filter = servletContext.addFilter("struts2", "org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter");
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*.action");
    }
}
