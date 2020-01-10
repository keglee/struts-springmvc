package com.iversonx.struts_springmvc.listener;

import com.iversonx.struts_springmvc.action.UserAction;
import com.iversonx.struts_springmvc.struts.StrutsXmlConfigurationProvider;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.inject.Container;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.iterator;

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
        ServletContext servletContext = ((WebApplicationContext)event.getApplicationContext()).getServletContext();
        Dispatcher dispatcher = new Dispatcher(servletContext, new HashMap<>());
        dispatcher.init();
        Container container = dispatcher.getContainer();
        ConfigurationManager configurationManager = dispatcher.getConfigurationManager();
        Map<String, PackageConfig> packageConfigMap = configurationManager.getConfiguration().getPackageConfigs();

        Set<Map.Entry<String, PackageConfig>> entrySet = packageConfigMap.entrySet();
        for(Map.Entry<String, PackageConfig> item : entrySet) {
            PackageConfig packageConfig = item.getValue();
            Map<String, ActionConfig> actionConfigMap = packageConfig.getAllActionConfigs();
            logger.info(actionConfigMap);
        }
        // ActionMapper actionMapper = container.getInstance(ActionMapper.class);
        // ActionMapping actionMapping = actionMapper.getMappingFromActionName("show");

        logger.info("OK");
        // Dispatcher dispatcher = new Dispatcher(servletContext, new HashMap<>());
        // dispatcher.init();
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
}
