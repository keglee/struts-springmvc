package com.iversonx.struts_springmvc.support.result;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/3/3 11:30
 */
public class StrutsResultHandlerComposite {
    private final List<AbstractStrutsResultHandler> returnValueHandlers = new ArrayList<>(5);

    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  Object action) throws Exception {

        ServletWebRequest servletWebRequest = (ServletWebRequest)webRequest;
        String uri = servletWebRequest.getRequest().getRequestURI();
        if(uri.endsWith(".do")) {
            int index = uri.indexOf(".do");
            uri = uri.substring(0, index);
        }

        if(uri.endsWith(".action")) {
            int index = uri.indexOf(".action");
            uri = uri.substring(0, index);
        }
        AbstractStrutsResultHandler handler = selectHandler(returnValue, returnType, uri);
        if (handler == null) {
            throw new IllegalArgumentException("Unknown return value type: " + returnType.getParameterType().getName());
        }
        handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest, action);
    }

    public void addHandler(AbstractStrutsResultHandler returnValueHandler) {
        returnValueHandlers.add(returnValueHandler);
    }

    private AbstractStrutsResultHandler selectHandler(Object returnValue, MethodParameter returnType, String uri) {

        for (AbstractStrutsResultHandler handler : this.returnValueHandlers) {
            if (handler.supportsResultType(returnValue, returnType, uri)) {
                return handler;
            }
        }
        return null;
    }

}
