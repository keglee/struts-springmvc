# 参数解析

由于Struts2是采用成员变量的方式来接收请求参数，因此需要将请求参数填充到Action所对应的成员变量上。



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
