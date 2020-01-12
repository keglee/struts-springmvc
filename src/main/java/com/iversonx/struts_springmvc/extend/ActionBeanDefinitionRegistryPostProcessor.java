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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 读取struts配置，将ActionConfig转换为BeanDefinition
 * @author Lijie
 * @version 1.0
 * @date 2020/1/9 16:51
 */
public class ActionBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private final Log logger = LogFactory.getLog(ActionMappingListener.class);
    private AnnotationConfigWebApplicationContext context;
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 调用struts2的Dispatcher进行初始化，主要目的是为了使用struts2来解析struts xml文件，这样就不用自己重新写解析xml代码
        ServletContext servletContext = new MockServletContext();// context.getServletContext();
        Dispatcher dispatcher = new Dispatcher(servletContext, new HashMap<>());
        dispatcher.init();

        ConfigurationManager configurationManager = dispatcher.getConfigurationManager();


        Map<String, PackageConfig> packageConfigMap = configurationManager.getConfiguration().getPackageConfigs();
        Set<Map.Entry<String, PackageConfig>> entrySet = packageConfigMap.entrySet();
        // 遍历struts2的package配置
        List<ActionConfig> actionConfigs = context.getBean("actionConfigs", List.class);
        for(Map.Entry<String, PackageConfig> item : entrySet) {
            PackageConfig packageConfig = item.getValue();
            // 遍历package下的action
            Map<String, ActionConfig> actionConfigMap = packageConfig.getAllActionConfigs();
            for(Map.Entry<String, ActionConfig> entry : actionConfigMap.entrySet()) {
                ActionConfig actionConfig = entry.getValue();
                logger.info("Namespace [" + packageConfig.getNamespace() + "], Action[" + actionConfig.getClassName() + "], url [" + actionConfig.getName() + "], method [" + actionConfig.getMethodName() +"]");
                registerBean(registry, actionConfig);
                actionConfigs.add(actionConfig);
                // 此处进行注册RequestMapping，
                // 会报Factory method 'mvcUriComponentsContributor' threw exception; nested exception is java.lang.IllegalArgumentException:
                // 'uriComponentsContributors' must not be null
                // 因此将RequestMapping的注册放在容器初始化完成之后
                // registerMapping(actionConfig);
            }
        }

        logger.info("OK");
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
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(actionConfig.getClassName());
        builder.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(actionConfig.getClassName(), beanDefinition);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = (AnnotationConfigWebApplicationContext)applicationContext;
    }
}
