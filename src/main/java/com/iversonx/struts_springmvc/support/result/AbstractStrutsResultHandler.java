package com.iversonx.struts_springmvc.support.result;

import com.iversonx.struts_springmvc.support.processor.StrutsConfigManager;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;


public abstract class AbstractStrutsResultHandler {
    protected final StrutsConfigManager strutsConfigManager;
    protected ResultConfig resultConfig;

    public AbstractStrutsResultHandler(StrutsConfigManager strutsConfigManager) {
        this.strutsConfigManager = strutsConfigManager;
    }



    public boolean supportsResultType(Object returnValue, MethodParameter returnType, String uri) {
        if(returnValue == null) {
            returnValue = "success";
        }

        if (returnType.getContainingClass().getSuperclass().equals(ActionSupport.class)) {
            resultConfig = strutsConfigManager
                    .getResultConfigByURIReturnValue(uri, returnValue.toString());
            return resultConfig != null && supportsResultClass(resultConfig.getClassName());
        }

        return false;
    }

    abstract boolean supportsResultClass(String className);

    abstract void handleReturnValue(Object returnValue, MethodParameter returnType,
                                    ModelAndViewContainer mavContainer, NativeWebRequest webRequest, Object handler) throws Exception;

}
