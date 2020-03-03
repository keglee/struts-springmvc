package com.iversonx.struts_springmvc.support.result;

import com.iversonx.struts_springmvc.support.processor.StrutsConfigManager;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;


public abstract class AbstractStrutsResultValueHandler implements HandlerMethodReturnValueHandler {
    protected final StrutsConfigManager strutsConfigManager;
    protected ResultConfig resultConfig;

    public AbstractStrutsResultValueHandler(StrutsConfigManager strutsConfigManager) {
        this.strutsConfigManager = strutsConfigManager;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 必须是Struts Action类型
        return returnType.getContainingClass().getSuperclass().equals(ActionSupport.class);
    }


    public boolean supportsResultType(Object returnValue, MethodParameter returnType) {
        if (supportsReturnType(returnType)
                && returnValue != null
                && returnValue instanceof CharSequence) {

            Class<?> handlerClass = returnType.getContainingClass();
            String methodName = returnType.getMethod().getName();
            resultConfig = strutsConfigManager
                    .getResultConfigByClassAndMethodAndReturnValue(handlerClass.getName(), methodName, returnValue.toString());
            return resultConfig != null && supportsResultClass(resultConfig.getClassName());
        }

        return false;
    }

    abstract boolean supportsResultClass(String className);

}
