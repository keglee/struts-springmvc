package com.iversonx.struts_springmvc.support.result;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/3/3 11:30
 */
public class StrutsResultValueHandlerComposite {
    private final List<AbstractStrutsResultValueHandler> returnValueHandlers = new ArrayList<>(5);

    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  Object action) throws Exception {

        AbstractStrutsResultValueHandler handler = selectHandler(returnValue, returnType);
        if (handler == null) {
            throw new IllegalArgumentException("Unknown return value type: " + returnType.getParameterType().getName());
        }
        handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest, action);
    }

    public void addHandler(AbstractStrutsResultValueHandler returnValueHandler) {
        returnValueHandlers.add(returnValueHandler);
    }

    private AbstractStrutsResultValueHandler selectHandler(Object returnValue, MethodParameter returnType) {

        for (AbstractStrutsResultValueHandler handler : this.returnValueHandlers) {
            if (handler.supportsResultType(returnValue, returnType)) {
                return handler;
            }
        }
        return null;
    }

}
