# Spring, Struts2集成

## pom依赖

版本：spring-4.3.25.RELEASE, struts2-2.3.35, servlet-3.1.0

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>${spring.version}</version>
</dependency>

<dependency>
    <groupId>org.apache.struts</groupId>
    <artifactId>struts2-core</artifactId>
    <version>${struts.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.struts</groupId>
    <artifactId>struts2-spring-plugin</artifactId>
    <version>2.3.35</version>
    <exclusions>
        <exclusion>
            <artifactId>spring-web</artifactId>
            <groupId>*</groupId>
        </exclusion>
        <exclusion>
            <groupId>org.springframework</groupId>
            <artifactId>*</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>${servlet.version}</version>
</dependency>
```

## 配置

### 1. spring配置
```java
@Configuration
@ComponentScan("com.xxx.xxx")
public class ApplicationConfig {
}

public class WebInitializer extends AbstractContextLoaderInitializer {

    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        struts(servletContext);
    }

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(ApplicationConfig.class);
        return ctx;
    }

    private void struts(ServletContext servletContext) {
        FilterRegistration.Dynamic filter = servletContext.addFilter("struts2", "org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter");
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*.action");
    }

}
```

### 2. 指定ServletContainerInitializer的实现类

在resources目录中创建`META-INF/services`目录，并在该目录中创建`java.servlet.ServletContainerInitializer`文件，在文件中指定`ServletContainerInitializer`的实现。
文件内容如下
```java.servlet.ServletContainerInitializer
org.springframework.web.SpringServletContainerInitializer
```

> 在servlet3.0中，可以使用`ServletContainerInitializer`的方式来代替`web.xml`. 
Spring中的`SpringServletContainerInitializer`是`ServletContainerInitializer`的一种实现，
并通过`@HandlesTypes(WebApplicationInitializer.class)`指定`ServletContainerInitializer`需要处理的类型。
`AbstractContextLoaderInitializer`是`WebApplicationInitializer`的一种实现

### 3. struts.xml 和 action

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 2.3//EN"
        "http://struts.apache.org/dtds/struts-2.3.dtd">
<struts>
    <!-- 指定spring使用按类型为Action进行注入 -->
    <constant name="struts.objectFactory.spring.autoWire" value="type" />
    <package name="default" namespace="/" extends="struts-default">
        <action name="show" class="com.iversonx.struts_springmvc.action.UserAction" method="show">
            <result name="success">show.jsp</result>
        </action>
    </package>
</struts>
```

```java

public class UserAction extends ActionSupport {

    @Autowired
    private UserService userService;

    private static final long serialVersionUID = -1353901915599323577L;
    private String username;
    private String password;

    public String show() {
        username = userService.show();
        return "success";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

@Service
public class UserService {
    public String show() {
        return "Hello Struts2";
    }
}

```