package com.iversonx.struts_springmvc.extend;


import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ActionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private final  Map<String, Map<String, ActionConfig>> actionConfigMap;

    public ActionRequestMappingHandlerMapping(Map<String, Map<String, ActionConfig>> actionConfigMap) {
        this.actionConfigMap = actionConfigMap;
    }

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
        Map<Method, RequestMappingInfo> methods;
        // 如果是Struts Action，执行自定义的逻辑
        if(userType.getSuperclass() != null && ActionSupport.class.equals(userType.getSuperclass())) {
            Map<String, ActionConfig> actionMap = actionConfigMap.get(userType.getName());
            methods = new HashMap<>(actionMap.size());
            for(Map.Entry<String, ActionConfig> entry : actionMap.entrySet()) {
                ActionConfig actionConfig = entry.getValue();
                RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(actionConfig.getName() + ".action")
                        .methods(RequestMethod.GET, RequestMethod.POST)
                        .mappingName(actionConfig.getClassName() + "#" + actionConfig.getName())
                        .build();
                String methodName = actionConfig.getMethodName();
                Method targetMethod = ClassUtils.getMethod(userType, methodName);
                methods.put(targetMethod, requestMappingInfo);
            }
        } else {
            // 否则，走原先的逻辑
            methods = MethodIntrospector.selectMethods(userType,
                    new MethodIntrospector.MetadataLookup<RequestMappingInfo>() {
                        @Override
                        public RequestMappingInfo inspect(Method method) {
                            try {
                                return getMappingForMethod(method, userType);
                            }
                            catch (Throwable ex) {
                                throw new IllegalStateException("Invalid mapping on handler class [" +
                                        userType.getName() + "]: " + method, ex);
                            }
                        }
                    });
        }

        if (logger.isDebugEnabled()) {
            logger.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
        }
        for (Map.Entry<Method, RequestMappingInfo> entry : methods.entrySet()) {
            Method invocableMethod = AopUtils.selectInvocableMethod(entry.getKey(), userType);
            RequestMappingInfo mapping = entry.getValue();
            registerHandlerMethod(handler, invocableMethod, mapping);
        }
    }
}