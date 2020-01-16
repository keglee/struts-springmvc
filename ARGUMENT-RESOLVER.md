# 参数解析

由于Struts2是采用成员变量的方式来接收请求参数，因此需要将请求参数填充到Action所对应的成员变量上。

1. spring mvc处理请求时会根据请求获取相应的`HandlerAdapter`，在调用`HandlerAdapter`进行请求处理之前会先调用拦截器
`HandlerInterceptor`的`preHandle`方法，进行前置处理。

2. spring mvc处理请求时会创建`InvocableHandlerMethod`实例，并调用`InvocableHandlerMethod`的`invokeForRequest`方法；
`InvocableHandlerMethod`类的作用是调用`HandlerMethodArgumentResolver`进行参数解析绑定和调用真实的`Controller`。

当目标方法没有定义参数时，`HandlerMethodArgumentResolver`就不会被调用，因此目前只能在上面两处进行自定义参数解析。

## 方式1: 扩展`HandlerInterceptorAdapter`，并重写`preHandle`方法

`HandlerInterceptorAdapter`对`HandlerInterceptor`进行了空实现，可以自定义`HandlerInterceptor`使它继承自`HandlerInterceptorAdapter`，
这样只需重写自己关注的方法即可
```java
public class ActionHandlerInterceptor extends HandlerInterceptorAdapter {
    
    public ActionHandlerInterceptor() {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 进行参数解析，并填充到Action的成员变量中
        return true;
    }
}
```

## 方式2: 扩展`InvocableHandlerMethod`，并重写`invokeForRequest`方法
`ServletInvocableHandlerMethod`是`InvocableHandlerMethod`的子类，spring mvc处理请求时默认创建的就是`ServletInvocableHandlerMethod`；
可以通过扩展`RequestMappingHandlerAdapter`进行自定义。

```java
public class WebMvcConfig extends DelegatingWebMvcConfiguration {
    @Override
        protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
            return new RequestMappingHandlerAdapter(){
                @Override
                protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
                    ConversionService conversionService = getApplicationContext().getBean(ConversionService.class);
                    MappingJackson2HttpMessageConverter messageConverter = getApplicationContext().getBean(MappingJackson2HttpMessageConverter.class);
                    return new ActionInvocableHandlerMethod(handlerMethod, conversionService, messageConverter);
                }
            };
        }
}

public class ActionInvocableHandlerMethod extends ServletInvocableHandlerMethod{
  
    public ActionInvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    @Override
    public Object invokeForRequest(NativeWebRequest request, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        // 在调super.invokerForRequest()之前进行参数解析，并填充到Action的成员变量中
        Object result = super.invokeForRequest(request, mavContainer, providedArgs);
        return result;
    }
}

```

## 针对常用的Content-Type进行解析
在开发Web应用，发起HTTP请求时请求头中常用的Content-Type有`application/x-www-form-urlencoded`, `application/json`, `multipart/form-data`

- application/x-www-form-urlencoded form表单默认的Content-Type，数据按照key1=val1&key2=val2的方式进行编码。
请求数据存放在request body中，可使用ServletRequest.getParameter获取。
- application/json 通常用于传输序列化后json字符串，请求数据存放在request body中，可使用ServletRequest.getInputStream进行读取
- multipart/form-data 通常用于文件上传，需使用`MultipartHttpServletRequest`进行接收，然后使用ServletRequest.getInputStream进行读取

## 获取处理请求的Action实例，填充成员变量

`org.springframework.beans.BeanWrapper`是spring底层的核心接口。用于操作Java Bean：获取和设置属性值；还支持嵌套属性。
后面需要使用`BeanWrapper`来将参数设置到Action的成员变量中。

```java
public class ActionInvocableHandlerMethod extends ServletInvocableHandlerMethod{
  
    public ActionInvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    @Override
    public Object invokeForRequest(NativeWebRequest request, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        // 在调super.invokerForRequest()之前进行参数解析，并填充到Action的成员变量中
        Object handler = getBean();
        Class<?> handlerClass = getBeanType();
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(handler);
        
        // 获取查询字符串或表单数据(content-type=application/x-www-form-urlencoded)
        Enumeration<String> names = httpServletRequest.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String[] value = request.getParameterValues(name);
            if(value != null && value.length > 0) {
                if(value.length == 1) {
                    bw.setPropertyValue(name, value[0]);
                } else {
                    bw.setPropertyValue(name, value);
                }
            }
        }
        
        
        MediaType mediaType = MediaType.valueOf(contentType);
        if(MediaType.APPLICATION_JSON.includes(mediaType)) {
            // json数据
            HttpInputMessage httpInputMessage = new ServletServerHttpRequest(httpServletRequest);
            Object object = messageConverter.read(handlerClass, httpInputMessage);
            BeanUtils.copyProperties(object, handler);
        } else if (MediaType.MULTIPART_FORM_DATA.includes(mediaType)) {
            // 文件上传
            MultipartHttpServletRequest multipart = (MultipartHttpServletRequest)httpServletRequest;
            Map<String, MultipartFile> fileMap = multipart.getFileMap();
            for(Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
                String key = entry.getKey();
                MultipartFile value = entry.getValue();
                File file = new File(value.getOriginalFilename());
                try (InputStream input = value.getInputStream();
                     OutputStream output = new FileOutputStream(file)){
                    int bytesRead;
                    byte[] buffer = new byte[2048];
                    while ((bytesRead = input.read(buffer, 0, 2048)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
                bw.setPropertyValue(key, file);
            }
        }
                        
        Object result = super.invokeForRequest(request, mavContainer, providedArgs);
        return result;
    }
}
```
