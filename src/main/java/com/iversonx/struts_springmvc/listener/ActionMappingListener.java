package com.iversonx.struts_springmvc.listener;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.List;

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
        registerMapping(context);
        /*AnnotationConfigWebApplicationContext context = (AnnotationConfigWebApplicationContext)event.getApplicationContext();
        ServletContext servletContext = context.getServletContext();*/



        /*
        logger.info("ActionMappingListener ");
        ApplicationContext context = event.getApplicationContext();
        RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        // 获取所有的
        Map<String, ActionSupport> actionMap = context.getBeansOfType(ActionSupport.class);
        Set<Map.Entry<String, ActionSupport>> entrySet = actionMap.entrySet();
        for (Map.Entry<String, ActionSupport> item : entrySet) {
            ActionSupport action = item.getValue();
            Method targetMethod = null;
            try {
                targetMethod = action.getClass().getMethod("show");
            } catch (NoSuchMethodException e) {
                logger.info(e);
            }

            if (targetMethod != null) {
                RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths("show.action")
                        .methods(RequestMethod.GET)
                        .mappingName(action.getClass().getSimpleName())
                        .build();
                requestMappingHandlerMapping.registerMapping(requestMappingInfo, action, targetMethod);
            }

        }
*/
    }

    private void registerMapping(ApplicationContext context) {
        List<ActionConfig> actionConfigs = (List<ActionConfig>)context.getBean("actionConfigs");

        if(actionConfigs != null && !actionConfigs.isEmpty()) {
            for(ActionConfig actionConfig : actionConfigs) {
                RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
                String methodName = actionConfig.getMethodName();
                RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(methodName + "")
                        .methods(RequestMethod.GET, RequestMethod.POST)
                        .mappingName(actionConfig.getClassName())
                        .build();
                String className = actionConfig.getClassName();
                Object action = context.getBean(className);
                try {
                    Method targetMethod = action.getClass().getMethod(methodName);
                    // registerMapping的第二个参数要是beanName，而不是具体bean实例，否则Action无法使用原型
                    requestMappingHandlerMapping.registerMapping(requestMappingInfo, className, targetMethod);
                } catch (NoSuchMethodException e) {
                    logger.info(e);
                }
            }
        }
    }


}
