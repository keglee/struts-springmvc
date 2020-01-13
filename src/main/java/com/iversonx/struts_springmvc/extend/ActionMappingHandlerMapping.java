package com.iversonx.struts_springmvc.extend;


import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;

public class ActionMappingHandlerMapping extends RequestMappingHandlerMapping {
    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    @Override
    protected boolean isHandler(Class<?> beanType) {
        boolean isActionSupport = beanType.getSuperclass() != null
                && beanType.getSuperclass().equals(ActionSupport.class);
        return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class)
                || AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class)
                || isActionSupport);
    }

    protected void detectHandlerMethods(final Object handler) {
        Class<?> handlerType = (handler instanceof String ?
                getApplicationContext().getType((String) handler) : handler.getClass());
        final Class<?> userType = ClassUtils.getUserClass(handlerType);
        if(userType.getSuperclass() != null && ActionSupport.class.equals(userType.getSuperclass())) {
            Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(userType,
                    new MethodIntrospector.MetadataLookup<RequestMappingInfo>() {
                        @Override
                        public RequestMappingInfo inspect(Method method) {
                            try {
                                return getActionMappingForMethod(method, userType);
                            }
                            catch (Throwable ex) {
                                throw new IllegalStateException("Invalid mapping on handler class [" +
                                        userType.getName() + "]: " + method, ex);
                            }
                        }
                    });

            if (logger.isDebugEnabled()) {
                logger.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
            }
            for (Map.Entry<Method, RequestMappingInfo> entry : methods.entrySet()) {
                Method invocableMethod = AopUtils.selectInvocableMethod(entry.getKey(), userType);
                RequestMappingInfo mapping = entry.getValue();
                registerHandlerMethod(handler, invocableMethod, mapping);
            }
        } else {
            super.detectHandlerMethods(handler);
        }
    }

    private RequestMappingInfo getActionMappingForMethod(Method method, Class<?> handlerType) {
        return null;
    }


    protected RequestMappingInfo createRequestMappingInfo(
            RequestMapping requestMapping, RequestCondition<?> customCondition) {

        return RequestMappingInfo
                .paths(resolveEmbeddedValuesInPatterns(requestMapping.path()))
                .methods(requestMapping.method())
                .params(requestMapping.params())
                .headers(requestMapping.headers())
                .consumes(requestMapping.consumes())
                .produces(requestMapping.produces())
                .mappingName(requestMapping.name())
                .customCondition(customCondition)
                .options(this.config)
                .build();
    }
}