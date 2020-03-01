package com.iversonx.struts_springmvc.support;


import com.iversonx.struts_springmvc.support.processor.ActionConfigManager;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActionMappingHandlerMapping extends RequestMappingHandlerMapping {

    private final ActionConfigManager actionConfigManager;

    public ActionMappingHandlerMapping(ActionConfigManager actionConfigManager) {
        this.actionConfigManager = actionConfigManager;
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return beanType.getSuperclass() != null && beanType.getSuperclass().equals(ActionSupport.class);
    }

    protected void detectHandlerMethods(final Object handler) {
        Class<?> handlerType = (handler instanceof String ?
                getApplicationContext().getType((String) handler) : handler.getClass());
        final Class<?> userType = ClassUtils.getUserClass(handlerType);
        Set<ActionConfig> actionConfigs = actionConfigManager.getActionConfigsByClass(userType.getName());
        Map<Method, Set<RequestMappingInfo>> methods = new HashMap<>(actionConfigs.size());
        for(ActionConfig actionConfig : actionConfigs) {
            String namespace = actionConfigManager.getNamespace(actionConfig.getPackageName());
            String[] paths;
            if(namespace != null && !"".equals(namespace)) {
                if(namespace.endsWith("/")) {
                    paths = new String[]{namespace + actionConfig.getName() + ".action"};
                } else {
                    paths = new String[]{namespace + "/" + actionConfig.getName() + ".action"};
                }

            } else {
                paths = new String[]{"/" + actionConfig.getName() + ".action"};
            }

            // struts2无法指定POST和GET，因此这里同时支持POST和GET
            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(paths)
                    .methods(RequestMethod.GET, RequestMethod.POST)
                    .mappingName(actionConfig.getClassName() + "#" + actionConfig.getName())
                    .build();
            String methodName = actionConfig.getMethodName();
            Method targetMethod = ClassUtils.getMethod(userType, methodName);
            Set<RequestMappingInfo> requestMappingInfoSet = methods.get(targetMethod);
            if(requestMappingInfoSet == null) {
                requestMappingInfoSet = new HashSet<>(1);
            }
            requestMappingInfoSet.add(requestMappingInfo);
            methods.put(targetMethod, requestMappingInfoSet);
        }

        if (logger.isDebugEnabled()) {
            logger.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
        }
        for (Map.Entry<Method, Set<RequestMappingInfo>> entry : methods.entrySet()) {
            Method invocableMethod = AopUtils.selectInvocableMethod(entry.getKey(), userType);
            for(RequestMappingInfo mapping : entry.getValue()) {
                registerHandlerMethod(handler, invocableMethod, mapping);
            }
        }
    }
}