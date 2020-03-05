package com.iversonx.struts_springmvc.support;

import com.iversonx.struts_springmvc.support.processor.StrutsConfigManager;
import com.iversonx.struts_springmvc.support.result.*;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;


public class StrutsMappingHandlerAdapter extends RequestMappingHandlerAdapter {

    @Override
    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
        ConversionService conversionService = getApplicationContext().getBean(ConversionService.class);
        MappingJackson2HttpMessageConverter messageConverter = getApplicationContext().getBean(MappingJackson2HttpMessageConverter.class);

        StrutsInvocableHandlerMethod strutsInvocableHandlerMethod = new StrutsInvocableHandlerMethod(handlerMethod, conversionService, messageConverter);
        StrutsConfigManager strutsConfigManager = getApplicationContext().getBean(StrutsConfigManager.class);

        StrutsResultHandlerComposite strutsReturnValueHandlers = new StrutsResultHandlerComposite();
        strutsReturnValueHandlers.addHandler(new DispatcherResultHandler(strutsConfigManager));
        strutsReturnValueHandlers.addHandler(new RedirectResultHandler(strutsConfigManager));
        strutsReturnValueHandlers.addHandler(new ActionRedirectResultHandler(strutsConfigManager));
        strutsReturnValueHandlers.addHandler(new StreamResultHandler(strutsConfigManager));
        strutsReturnValueHandlers.addHandler(new JSONResultHandler(strutsConfigManager));

        strutsInvocableHandlerMethod.setStrutsReturnValueHandlers(strutsReturnValueHandlers);

        return strutsInvocableHandlerMethod;
    }

    @Override
    protected boolean supportsInternal(HandlerMethod handlerMethod) {
        return handlerMethod.getBeanType().getSuperclass().equals(ActionSupport.class);
    }

}
