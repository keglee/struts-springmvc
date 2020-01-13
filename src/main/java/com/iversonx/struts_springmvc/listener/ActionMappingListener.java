package com.iversonx.struts_springmvc.listener;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 在容器启动后，动态添加struts action到RequestMapping
 * @author Lijie
 * @version 1.0
 * @date 2020/1/10 9:56
 */
public class ActionMappingListener implements ApplicationListener<ContextRefreshedEvent> {
    private final Log logger = LogFactory.getLog(ActionMappingListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        // registerMapping(context);
    }

    private void registerMapping(ApplicationContext context) {
        List<ActionConfig> actionConfigs = context.getBean("actionConfigs", List.class);

        if(actionConfigs != null && !actionConfigs.isEmpty()) {
            for(ActionConfig actionConfig : actionConfigs) {
                RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
                String methodName = actionConfig.getMethodName();
                RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(methodName + "")
                        .methods(RequestMethod.GET, RequestMethod.POST)
                        .mappingName(actionConfig.getClassName())
                        .build();
                String className = actionConfig.getClassName();
                Class<?> clazz = ClassUtils.getUserClass(className);
                Method targetMethod = ClassUtils.getMethod(clazz, methodName);
                // registerMapping的第二个参数要是beanName，而不是具体bean实例，否则Action都是使用同一个实例进行请求处理
                requestMappingHandlerMapping.registerMapping(requestMappingInfo, className, targetMethod);
            }
        }
    }


}
