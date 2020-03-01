package com.iversonx.struts_springmvc.support.processor;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.Dispatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 在Spring加载所有BeanDefinition后，但还有实例化前，进行额外的处理
 * 读取struts配置，将ActionConfig转换为BeanDefinition
 * @author Lijie
 * @version 1.0
 * @date 2020/1/9 16:51
 */
public class ActionBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    private final Log logger = LogFactory.getLog(this.getClass());
    private final ActionConfigManager actionConfigManager;
    public ActionBeanDefinitionRegistryPostProcessor(ActionConfigManager actionConfigManager) {
        this.actionConfigManager = actionConfigManager;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 调用struts2的Dispatcher进行初始化，主要目的是为了使用struts2来解析struts xml文件，这样就不用自己重新写解析xml代码
        try {
            ServletContext servletContext = new MockServletContext();
            Dispatcher dispatcher = new Dispatcher(servletContext, new HashMap<>());
            dispatcher.init();

            ConfigurationManager configurationManager = dispatcher.getConfigurationManager();
            Map<String, PackageConfig> packageConfigMap = configurationManager.getConfiguration().getPackageConfigs();
            Map<String, PackageConfig> notEmptyPackageConfigMap = new HashMap<>(packageConfigMap.size());

            Set<Map.Entry<String, PackageConfig>> entrySet = packageConfigMap.entrySet();
            // 遍历struts2的package配置
            for(Map.Entry<String, PackageConfig> item : entrySet) {
                PackageConfig packageConfig = item.getValue();
                // 遍历package下的action
                Map<String, ActionConfig> actionConfigMap = packageConfig.getActionConfigs();

                if(actionConfigMap != null && !actionConfigMap.isEmpty()) {
                    notEmptyPackageConfigMap.put(item.getKey(), item.getValue());
                    for(Map.Entry<String, ActionConfig> entry : actionConfigMap.entrySet()) {
                        ActionConfig actionConfig = entry.getValue();
                        registerBean(registry, actionConfig);
                    }

                }
            }

            actionConfigManager.setPackageConfig(notEmptyPackageConfigMap);
        } catch (BeansException e) {
            throw e;
        } catch (Exception e) {
            // 为了不影响其他代码，这么只打日志
            logger.error("Action bean register failed", e);
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
            // 设置bean的作用域
            builder.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            // 设置根据类型自动装配
            builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            BeanDefinition beanDefinition = builder.getBeanDefinition();
            registry.registerBeanDefinition(className, beanDefinition);
            logger.info("Register action bean [" + className + "]");
        }
    }
}
