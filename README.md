# struts-springmvc

## 这是什么

一个Struts2迁移到SpringMVC的演示项目。其特点是让开发者可以在尽可能少修改Struts2 Action类的前提下完成迁移。

> jsp中的Struts2标签还是要修改的

## 背景

我的雇主在服务器大面积被黑客入侵后，绝对将所有基于Struts2框架开发的系统，改造成SpringMVC。

> 项目情况：上千个Action处理方法，且大部分业务逻辑在Action方法里 
> 人员配置：8人
> 改造时间：两个星期
> 方案：一个个Action类进行修改

在闲时与一个同事沟通中他提到：“SpringMVC的请求是可配置，Struts2的请求也是可配置，那么只要将Struts2的配置解析成SpringMVC的配置，都不需要去修改Action类，即可完成迁移”。于是就有了这个项目。

## Spring知识点

- BeanDefinitionRegistryPostProcessor：用于在Spring加载所有BeanDefinition后但还没有实例化前，注册更多的BeanDefinition。
  
  在这个项目中通过自定义`BeanDefinitionRegistryPostProcessor`将Action类注册到Ioc容器中。可查看[StrutsBeanDefinitionRegistryPostProcessor](./src/main/java/com/iversonx/struts_springmvc/support/processor/StrutsBeanDefinitionRegistryPostProcessor.java)

- RequestMappingHandlerMapping：基于`@Controller`和`@RequestMapping`建立`HttpServletRequest`与Handler之间的映射关系。
  
  在这个项目中，通过继承`RequestMappingHandlerMapping`，来将struts的acion配置转换成SpingMVC中请求与处理器的数据结构`RequestMappingInfo`。可查看

- RequestMappingHandlerAdapter：用于调用具体的handler处理请求，并根据handler的返回值创建`ModelAndView`实例，最后返回`ModelAndView`。
  
  在这个项目中，通过继承`RequestMappingHandlerAdapter`，用来调用ActionSupport类型的handler。

- ServletInvocableHandlerMethod：主要做3件事：一是调用`HandlerMethodArgumentResolver`解析方法参数；二是调用handler处理请求；三是调用`HandlerMethodReturnValueHandler`处理返回值。由`RequestMappingHandlerAdapter`创建。
  
  在这个项目中，通过继承`ServletInvocableHandlerMethod`，使SpringMVC拥有处理Struts Action的能力。

- HandlerMethodReturnValueHandler：使用组合模式和策略模式对handler的返回值进行处理。
  
  在这个项目中，模仿`HandlerMethodReturnValueHandler`建立一套处理Struts   Action返回值的策略。

## 步骤



## 已支持功能


