package com.iversonx.struts_springmvc.extend;


import org.springframework.beans.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/14 10:51
 */
@Component
public class ActionHandlerInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private FormattingConversionService conversionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Enumeration<String> headerNames = request.getHeaderNames();
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Request Headers: ");
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            System.out.println(name + ": " + request.getHeader(name));
        }

        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(handlerMethod.getBean());
            bw.setConversionService(conversionService);
            // 当嵌套属性为null时，自动创建嵌套属性的实例
            bw.setAutoGrowNestedPaths(true);

            // 从查询字符串或表单数据(content-type=application/x-www-form-urlencoded)获取参数
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String value = request.getParameter(name);
                if(bw.isWritableProperty(name)) {
                    bw.setPropertyValue(name, value);
                }
            }
        }
        return true;
    }
}
