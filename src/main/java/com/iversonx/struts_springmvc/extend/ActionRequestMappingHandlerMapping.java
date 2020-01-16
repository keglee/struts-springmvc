package com.iversonx.struts_springmvc.extend;


import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import org.springframework.aop.support.AopUtils;
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
import java.util.Set;

/**
 * 注册Struts请求映射
 */
public class ActionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private final ActionConfigManager actionConfigManager;

    public ActionRequestMappingHandlerMapping(ActionConfigManager actionConfigManager) {
        this.actionConfigManager = actionConfigManager;
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
            Set<ActionConfig> actionConfigs = actionConfigManager.getActionConfigsByClass(userType.getName());
            methods = new HashMap<>(actionConfigs.size());
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
                RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(paths)
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