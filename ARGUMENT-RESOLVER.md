# 参数解析

由于Struts2是采用成员变量的方式来接收请求参数，因此需要将请求参数填充到Action所对应的成员变量上。

通过调试spring mvc源码发现，要想在DispatcherServlet处理请求过程进行参数解析扩展，是比较困难的。
原因是spring mvc对请求参数的解析都是针对方法级别的，如果发现处理请求的方法上没有定义形参，就不会进行参数解析；
```java
public class InvocableHandlerMethod extends HandlerMethod {
    public Object invokeForRequest(NativeWebRequest request, ModelAndViewContainer mavContainer,
    			Object... providedArgs) throws Exception {

        Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);
        // 这里调用实际的Controller
        Object returnValue = doInvoke(args);
        return returnValue;
    }
    
    private Object[] getMethodArgumentValues(NativeWebRequest request, ModelAndViewContainer mavContainer,
    			Object... providedArgs) throws Exception {
        MethodParameter[] parameters = getMethodParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            // ...... 省略其他代码
        }
        return args;
    }
    
    /**
     * Return the method parameters for this handler method.
     * 此方法定义在org.springframework.web.method.HandleMethod中
     */
    public MethodParameter[] getMethodParameters() {
        return this.parameters;
    }
}

```
- 

```text
DispatcherServlet#doService 》 DispatcherServlet#doDispatch 》 RequestMappingHandlerAdapter#handle

》 RequestMappingHandlerAdapter#handleInternal 》 RequestMappingHandlerAdapter#invokeHandlerMethod

》 ServletInvocableHandlerMethod#invokeAndHandle 》 InvocableHandlerMethod#invokeForRequest

》 (获取参数)InvocableHandlerMethod#getMethodArgumentValues
```

## URL参数 & 表单参数(Content-Type=application/x-www-form-urlencoded)

## json参数(Content-Type=application/json)

## 上传文件(Content-Type=multipart/form-data)
