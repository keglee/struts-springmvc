# 注册处理方法(Register Handler Method)

## 1. 将action请求配置转换成RequestMapping，注册到spring mvc中

在将struts action转换为RequestMapping前，首先需要读取解析struts.xml文件。读取struts.xml文件的方式有两种

- 自己编写代码读取struts.xml
- 使用struts自身功能读取struts.xml

在简单思考过后，我才用了第二种，其原因是：不重新造轮子。为此就需要阅读一下struts源码，以便获知

1. struts配置对应的数据结构是什么，在哪里
  
  struts使用`xwork-core.jar`文件的`com.opensymphony.xwork2.config.entities`包中的类来对应配置文件的元素，以下只列出我关注的类
  - `PackageConfig`对应`package`元素
  - `ActionConfig`对应`action`元素
  - `ResultConfig`对应`action`元素下的`result元素`

2. struts配置的解析类是哪一个

  struts提供了`org.apache.struts2.config.StrutsXmlConfigurationProvider`接口来解析`struts.xml`文件，主要实现由其父类`org.apache.struts2.config.XmlConfigurationProvider`完成

3. 什么时候触发配置文件解析以及如何触发
  
   struts在初始化`StrutsPrepareAndExecuteFilter`时，会创建`org.apache.struts2.dispatcher.Dispatcher`实例，然后调用`Dispatcher.init()`；
   在`init`方法中会初始化`org.apache.struts2.config.StrutsXmlConfigurationProvider`，并在初始化struts容器时，调用`StrutsXmlConfigurationProvider`加载配置

4. 加载完配置文件后，如何获得元素对应的数据结构实例

    struts的`com.opensymphony.xwork2.config.ConfigurationManager`负责配置管理。它的`Configuration`属性使用Map缓存了PackageConfig。
    
以上的描述时简化之后，省略了很多不关注的内容。

在获得上面的信息后，就可以在spring容器启动时获取struts配置，然后转换成RequestMapping，注册到spring mvc中。
```java
public class Sample {
    public static void main(String[] args) {
        // 创建一个ServletContext实例
        ServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = new Dispatcher(servletContext, new HashMap<>());
        dispatcher.init();
        ConfigurationManager configurationManager = dispatcher.getConfigurationManager();
        Map<String, PackageConfig> packageConfigMap = configurationManager.getConfiguration().getPackageConfigs();
        Set<Map.Entry<String, PackageConfig>> entrySet = packageConfigMap.entrySet();
        // 遍历struts2的package配置
        for(Map.Entry<String, PackageConfig> item : entrySet) {
            PackageConfig packageConfig = item.getValue();
            // 遍历package下的action
            Map<String, ActionConfig> actionConfigMap = packageConfig.getAllActionConfigs();
            for(Map.Entry<String, ActionConfig> entry : actionConfigMap.entrySet()) {
                ActionConfig actionConfig = entry.getValue();
                logger.info("Namespace [" + packageConfig.getNamespace() + "], Action[" 
                + actionConfig.getClassName() + "], url [" + actionConfig.getName() + "], method [" + actionConfig.getMethodName() +"]");
            }
        }
    }
}
```

### 1.1 根据配置文件中action元素的class属性，生成BeanDefinition，并注册到Ioc容器中

在将`ActionConfig`注册到Ioc容器中前，先需要知道`ActionConfig`有哪些属性:

```java
public class ActionConfig extends Located implements Serializable {
    
    protected List<InterceptorMapping> interceptors;
    // params 对应 <param>
    protected Map<String,String> params;
    // results 对应 <result>
    protected Map<String, ResultConfig> results;
    protected List<ExceptionMappingConfig> exceptionMappings;
    // className 对应<action>的class属性，也就是具体的Action类
    protected String className;
    // 具体的要执行的方法名称
    protected String methodName;
    protected String packageName;
    protected String name;
    protected Set<String> allowedMethods;
    // 省略其他方法
}
```

其次是如何通过编码形式构造`BeanDefinition`: 

- `BeanDefinition`是Bean定义被加载到Ioc容器后的数据结构，其描述了Bean的信息。
- Spring提供了`BeanDefinitionBuilder`通过编码形式来构造`BeanDefinition`。

接着就是在哪个时间点，注册由ActionConfig转换的`BeanDefinition`到Ioc容器中:

1. 在Ioc容器初始化完成之后，进行注册
2. 在Ioc容器初始化时，且所有Bean尚未实例化前，进行注册

第一种方式不足的地方是注册完`BeanDefinition`后，还需手动去注入它们依赖。所有采用了第二种方式。

Spring提供了`BeanDefinitionRegistryPostProcessor`接口允许开发者在所有的Bean定义加载到容器，但还没有实例化前添加更多的Bean定义。

```java
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
```
因此只要自定义一个`BeanDefinitionRegistryPostProcessor`的实现，即可将ActionConfig注册到Ioc容器中

```java
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
```

### 1.2 将action请求配置转换成RequestMapping，注册到springmvc

要将action请求交由springmvc来处理，首先就需要知道以下信息：

1. springmvc中RequestMapping对应的数据结构是什么

springmvc在创建`HandlerMapping`对象时，会将`Controller`中被`@RequestMapping`注解的方法解析成`RequestMappingInfo`对象。
由此得知springmvc的每个请求处理方法都会对应一个`RequestMappingInfo`对象。

2. springmvc是什么时候创建这些数据结构并注册springmvc

容器在初始化时会创建一个`RequestMappingHandlerMapping`实例，`RequestMappingHandlerMapping`在初始化时会扫描容器中所有的Bean，
然后将所有`@RequestMapping`方法解析为`RequestMappingInfo`对象，并进行注册。而这些处理逻辑都在`protected void detectHandlerMethods(final Object handler)`
方法中。

`RequestMappingHandlerMapping`还提供`isHandler`方法来判断Bean的类型是否为Controller。

3. 如何扩展

通过源码发现`RequestMappingHandlerMapping`是可扩展的。很多方法都是`protected`方法。

通过spring文档得知，在配置spring mvc时可以通过扩展[DelegatingWebMvcConfiguration](https://docs.spring.io/spring/docs/4.3.25.RELEASE/spring-framework-reference/htmlsingle/#mvc-config-advanced-java)类，
来进行更细粒度的配置。

这时需要修改`WebMvcConfig`配置类，一是移除`@EnableWebMvc`注解，二是继承`DelegatingWebMvcConfiguration`类
```java
@Configuration
@ComponentScan("com.iversonx.struts_springmvc")
public class WebMvcConfig extends DelegatingWebMvcConfiguration {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp().prefix("/").suffix(".jsp");
    }


    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new ActionRequestMappingHandlerMapping();
    }
}
```

接着在这个配置类中指明`RequestMappingHandlerMapping`的扩展。

创建`ActionRequestMappingHandlerMapping`类，并使它扩展自`RequestMappingHandlerMapping`，然后重写`isHandler`方法和`detectHandlerMethods`方法。

- 重写`isHandler`方法的目的是让容器将Action Bean当作Controller来处理；
- 重写`detectHandlerMethods`方法是为了将Action请求转换为`RequestMappingInfo`，然后注册到容器中

```java
public class ActionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    @Autowired
    private Map<String, Map<String, ActionConfig>> actionConfigMap;

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
```