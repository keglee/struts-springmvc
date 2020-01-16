package com.iversonx.struts_springmvc.extend;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

/**
 * 处理Action的返回值
 */
public class ActionViewMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final ActionConfigManager actionConfigManager;
    public ActionViewMethodReturnValueHandler(ActionConfigManager actionConfigManager) {
        this.actionConfigManager = actionConfigManager;
    }

    /**
     * 设置支持的返回类型
     * @param returnType
     * @return
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        Class<?> handlerClass = returnType.getContainingClass();
        return handlerClass != null
                && handlerClass.getSuperclass().equals(ActionSupport.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue instanceof CharSequence) {
            Class<?> handlerClass = returnType.getContainingClass();
            String methodName = returnType.getMethod().getName();
            ActionConfig actionConfig = actionConfigManager.getActionConfigByClassAndMethod(handlerClass.getName(), methodName);
            Map<String, ResultConfig> resultConfigMap = actionConfig.getResults();
            ResultConfig resultConfig = resultConfigMap.get(returnValue.toString());
            String viewName = null;
            if(resultConfig != null) {
                String resultName = resultConfig.getParams().get("location");
                if(resultName != null) {
                    int index = resultName.indexOf(".jsp");
                    if(index > -1) {
                        viewName = resultName.substring(0, index);
                    }
                }
            }

            if(viewName == null) {
                viewName = returnValue.toString();
            }

            mavContainer.setViewName(viewName);

            /*// 是否为重定向
            if (isRedirectViewName(viewName)) {
                mavContainer.setRedirectModelScenario(true);
            }*/
        }
        else if (returnValue != null) {
            // should not happen
            throw new UnsupportedOperationException("Unexpected return type: " +
                    returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
        }
    }
}
