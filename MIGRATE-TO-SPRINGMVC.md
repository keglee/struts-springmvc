# Struts迁移到Spring MVC

最终目标：在不改动Action代码的前提下，由SpringMVC来处理所有的Action请求

## 1. 将action请求配置转换成RequestMapping，注册到spring mvc中

读取struts.xml文件的方式有两种

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


### 1.2 
