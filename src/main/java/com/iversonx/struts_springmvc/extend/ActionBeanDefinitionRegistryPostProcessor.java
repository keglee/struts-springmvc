package com.iversonx.struts_springmvc.extend;

import com.iversonx.struts_springmvc.listener.ActionMappingListener;
import com.iversonx.struts_springmvc.struts.MockServletContext;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.Dispatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;
import java.util.*;

/**
 * 读取struts配置，将ActionConfig转换为BeanDefinition
 * @author Lijie
 * @version 1.0
 * @date 2020/1/9 16:51
 */
public class ActionBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private AnnotationConfigWebApplicationContext context;
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 调用struts2的Dispatcher进行初始化，主要目的是为了使用struts2来解析struts xml文件，这样就不用自己重新写解析xml代码
        ServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = new Dispatcher(servletContext, new HashMap<>());
        dispatcher.init();

        ConfigurationManager configurationManager = dispatcher.getConfigurationManager();


        Map<String, PackageConfig> packageConfigMap = configurationManager.getConfiguration().getPackageConfigs();
        Set<Map.Entry<String, PackageConfig>> entrySet = packageConfigMap.entrySet();
        // 遍历struts2的package配置
        Map<String, Map<String, ActionConfig>> actionConfigs = context.getBean("actionConfigMap", Map.class);
        for(Map.Entry<String, PackageConfig> item : entrySet) {
            PackageConfig packageConfig = item.getValue();
            // 遍历package下的action
            Map<String, ActionConfig> actionConfigMap = packageConfig.getAllActionConfigs();

            if(actionConfigMap != null && !actionConfigMap.isEmpty()) {

                String key = null;
                for(Map.Entry<String, ActionConfig> entry : actionConfigMap.entrySet()) {
                    ActionConfig actionConfig = entry.getValue();
                    if(key == null) {
                        key = actionConfig.getClassName();
                    }
                    registerBean(registry, actionConfig);
                }

                actionConfigs.put(key, actionConfigMap);
            }

        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    /**
     * 将Action注册到Spring容器中
     * @param registry BeanDefinitionRegistry
     * @param actionConfig ActionConfig
     */
    private void registerBean(BeanDefinitionRegistry registry, ActionConfig actionConfig) {
        String className = actionConfig.getClassName();
        if(!registry.containsBeanDefinition(className)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(className);
            builder.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            BeanDefinition beanDefinition = builder.getBeanDefinition();
            registry.registerBeanDefinition(className, beanDefinition);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = (AnnotationConfigWebApplicationContext)applicationContext;
    }
}
