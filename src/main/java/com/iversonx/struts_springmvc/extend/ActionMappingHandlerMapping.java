package com.iversonx.struts_springmvc.extend;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class ActionMappingHandlerMapping extends RequestMappingHandlerMapping {

    /*@Override
    protected boolean isHandler(Class<?> beanType) {
        boolean isActionSupport = beanType.getSuperclass() != null
                && beanType.getSuperclass().equals(ActionSupport.class);
        return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class)
                || AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class)
                || isActionSupport);
    }*/

    /*protected void detectHandlerMethods(final Object handler) {
        Class<?> handlerType = (handler instanceof String ?
                getApplicationContext().getType((String) handler) : handler.getClass());
        final Class<?> userType = ClassUtils.getUserClass(handlerType);
        if(userType.getSuperclass() != null && ActionSupport.class.equals(userType.getSuperclass())) {
            Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(userType,
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
    }*/

    private RequestMappingInfo getActionMappingForMethod(Method method, Class<?> handlerType) {
        return createActionRequestMappingInfo(method);
    }

    private RequestMappingInfo createActionRequestMappingInfo(AnnotatedElement element) {

        RequestMapping requestMapping = null;// AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = (element instanceof Class ?
                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null);
    }
}