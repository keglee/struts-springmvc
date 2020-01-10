package com.iversonx.struts_springmvc.config;

import org.apache.struts2.dispatcher.Dispatcher;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/9 11:27
 */
public class WebMvcInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebMvcConfig.class};
    }

    private void struts(ServletContext servletContext) {
        FilterRegistration.Dynamic filter = servletContext.addFilter("struts2", "org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter");
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*.action");
    }

}
