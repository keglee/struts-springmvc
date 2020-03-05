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
public class StrutsResultHandlerComposite {
    private final List<AbstractStrutsResultHandler> returnValueHandlers = new ArrayList<>(5);

    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  Object action) throws Exception {

        AbstractStrutsResultHandler handler = selectHandler(returnValue, returnType);
        if (handler == null) {
            throw new IllegalArgumentException("Unknown return value type: " + returnType.getParameterType().getName());
        }
        handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest, action);
    }

    public void addHandler(AbstractStrutsResultHandler returnValueHandler) {
        returnValueHandlers.add(returnValueHandler);
    }

    private AbstractStrutsResultHandler selectHandler(Object returnValue, MethodParameter returnType) {

        for (AbstractStrutsResultHandler handler : this.returnValueHandlers) {
            if (handler.supportsResultType(returnValue, returnType)) {
                return handler;
            }
        }
        return null;
    }

}
