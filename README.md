# struts-springmvc

## 这是什么

一个Struts2迁移到SpringMVC的原型项目。其特点是让开发者可以在尽可能少修改Struts2 Action类的前提下完成迁移。

之所以说是原型项目，是因为这个项目只提供了完整思路，但有些细节点处理的不是很严谨，且有些Struts场景没有考虑进去。

> jsp中的Struts2标签还是要修改的

## 背景

需要将所有基于Struts2框架开发的系统切换到SpringMVC框架。

> 项目情况：每一个系统上千个Action处理方法，且大部分业务逻辑在Action方法里 。同时存在人手不足，时间短的问题。
> 
> 方案：一个个Action类进行修改

在闲时与一个同事沟通中他提到：“SpringMVC的请求是可配置，Struts2的请求也是可配置，那么只要将Struts2的配置解析成SpringMVC的配置，都不需要去修改Action类，即可完成迁移”。于是就有了这个项目。

## Spring知识点

- BeanDefinitionRegistryPostProcessor：用于在Spring加载所有BeanDefinition后但还没有实例化前，注册更多的BeanDefinition。
  
  在这个项目中通过自定义`BeanDefinitionRegistryPostProcessor`将Action类注册到Ioc容器中。

- RequestMappingHandlerMapping：基于`@Controller`和`@RequestMapping`建立`HttpServletRequest`与Handler之间的映射关系。
  
  在这个项目中，通过扩展`RequestMappingHandlerMapping`，来将struts的acion配置转换成SpingMVC中请求与处理器的数据结构`RequestMappingInfo`。可查看

- RequestMappingHandlerAdapter：用于调用具体的handler处理请求，并根据handler的返回值创建`ModelAndView`实例，最后返回`ModelAndView`。
  
  在这个项目中，通过扩展`RequestMappingHandlerAdapter`，用来调用ActionSupport类型的handler。

- ServletInvocableHandlerMethod：主要做3件事：一是调用`HandlerMethodArgumentResolver`解析方法参数；二是调用handler处理请求；三是调用`HandlerMethodReturnValueHandler`处理返回值。由`RequestMappingHandlerAdapter`创建。
  
  在这个项目中，通过扩展`ServletInvocableHandlerMethod`，使SpringMVC拥有处理Struts Action的能力。

- HandlerMethodReturnValueHandler：使用组合模式和策略模式对handler的返回值进行处理。
  
  在这个项目中，模仿`HandlerMethodReturnValueHandler`建立一套处理Struts   Action返回值的策略。
  
  以上知识点的扩展位于：[com.iversonx.struts_springmvc.support](./src/main/java/com/iversonx/struts_springmvc/support)包中

## 已支持场景

```java
<action name="list" class="" method="list">
    <result name="success">list.jsp</result>
</action>
<!-- 重定向 -->
<action name="redirect" class="" method="redirect">
    <result name="success" type="redirect">list.jsp</result>
</action>
<!-- 重定向Action -->
<action name="redirectAction" class="" method="redirect">
    <result name="success" type="redirectAction">list.jsp</result>
</action>

<!-- 基于response的文件下载 -->
<action name="download" class="" method="download" />

<!-- 基于stream的文件下载 -->
<action name="downloadStream" class="" method="downloadStream">
    <result type="stream">
       <param name="contentType">application/vnd.ms-excel,charset=UTF-8</param>
       <param name="contentDisposition">attachment;fileName=${fileName}</param>
       <param name="inputName">inputStream</param>
       <param name="bufferSize">1024</param>
   </result>
</action>

<!-- 基于response的ajax请求 -->
<action name="ajax" class="" method="ajax" />

<!-- 基于stream的ajax请求 -->
<action name="ajaxStream" class="" method="ajaxStream">
     <result name="success" type="stream">
         <param name="contentType">application/json; charset=utf-8</param>
         <param name="inputName">ajax3Stream</param>
     </result>
</action>

<!-- 基于json-default的ajax请求 -->
<action name="ajaxJsonDefault" class="" method="ajaxJsonDefault">
    <result name="success" type="json">
        <param name="includeProperties">name,sex</param>
        <param name="excludeNullProperties">true</param>
    </result>
</action>
```
