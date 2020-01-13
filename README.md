# 切换到springmvc环境

## pom依赖
```xml
<!-- 1. 添加spring-webmvc依赖 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>${spring.version}</version>
</dependency>

<!-- 2. 注释struts2-spring-plugin依赖 -->
<!-- <dependency>
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
</dependency> -->
```

## 添加springmvc配置
```java
@Configuration
@EnableWebMvc
@ComponentScan("com.iversonx.struts_springmvc")
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp().prefix("/").suffix(".jsp");
    }
}
```

## 变更WebApplicationInitializer的实现

1. 注释之前的`Webinitializer`类

2. 新增`WebMvcInitializer`类，并继承`AbstractAnnotationConfigDispatcherServletInitializer`, `AbstractAnnotationConfigDispatcherServletInitializer`是`WebApplicationInitializer`的另一个实现。

```java
public class WebMvcInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        // 指定spring配置
        return null;;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        // 指定spring mvc配置
        return new Class[]{WebMvcConfig.class};
    }
}
```

## 添加Controller进行验证
```java
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("show.action")
    public String show(Model model,
                       @RequestParam(required = false, defaultValue = "Hello SpringMVC") String username,
                       @RequestParam(required = false, defaultValue = "123456") String password) {
        model.addAttribute("username", username);
        return "show";
    }
}
```