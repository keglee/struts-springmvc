package com.iversonx.struts_springmvc.support.result;

import com.iversonx.struts_springmvc.support.processor.StrutsConfigManager;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

public class JSONResultHandler extends AbstractStrutsResultHandler {
    private static final String SUPPORT_CLASS = "org.apache.struts2.json.JSONResult";

    public JSONResultHandler(StrutsConfigManager strutsConfigManager) {
        super(strutsConfigManager);
    }

    @Override
    public boolean supportsResultClass(String className) {
        return SUPPORT_CLASS.equals(className);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, Object handler) throws Exception {
        Map<String,String> params = resultConfig.getParams();
        // TODO
        System.out.println();
    }
}
