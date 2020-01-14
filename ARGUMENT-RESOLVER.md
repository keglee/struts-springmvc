# 参数解析

请求处理流程

DispatcherServlet#doService 》 DispatcherServlet#doDispatch 》 RequestMappingHandlerAdapter#handle

》 RequestMappingHandlerAdapter#handleInternal 》 RequestMappingHandlerAdapter#invokeHandlerMethod

》 ServletInvocableHandlerMethod#invokeAndHandle 》 InvocableHandlerMethod#invokeForRequest

》 (获取参数)InvocableHandlerMethod#getMethodArgumentValues