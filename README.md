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

在闲时与一个同事沟通时，他提到：SpringMVC的请求是可配置，Struts2的请求也是可配置，那么只要将Struts2的配置解析成SpringMVC的配置，都不需要去修改Action类，即可完成迁移。

## 已支持功能

第一步: 将Action bean 注册到Ioc容器


第二步: [切换到springmvc环境](../../tree/feature-springmvc)

第三步: [struts2迁移到springmvc](../../tree/feature-migrate-springmvc)



