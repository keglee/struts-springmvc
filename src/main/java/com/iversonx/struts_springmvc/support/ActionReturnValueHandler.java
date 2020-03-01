package com.iversonx.struts_springmvc.support;

import com.iversonx.struts_springmvc.support.processor.ActionConfigManager;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

public class ActionReturnValueHandler extends WebApplicationObjectSupport implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getContainingClass().getSuperclass().equals(ActionSupport.class);
    }


    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
         if(returnValue == null) {
            // 当返回值null，则认为已经异步响应。避免再去寻找jsp页面进行响应
            mavContainer.setRequestHandled(true);
            return;
        }

        // 获取struts配置结果页
        Class<?> handlerClass = returnType.getContainingClass();
        String methodName = returnType.getMethod().getName();
        ActionConfigManager actionConfigManager = getApplicationContext().getBean(ActionConfigManager.class);
        ActionConfig actionConfig = actionConfigManager.getActionConfigByClassAndMethod(handlerClass.getName(), methodName);
        Map<String, ResultConfig> resultConfigMap = actionConfig.getResults();

        if (returnValue instanceof CharSequence) {

            ResultConfig resultConfig = resultConfigMap.get(returnValue.toString());
            // 设置viewName
            String viewName = null;
            if(resultConfig != null) {
                String resultClassName = resultConfig.getClassName();
                // 是否为重定向
                if (isRedirectResultType(resultClassName)) {
                    String resultName;
                    if(isRedirectAction(resultClassName)) {
                        resultName = resultConfig.getParams().get("actionName");
                    } else {
                        resultName = resultConfig.getParams().get("location");
                    }
                    mavContainer.setRedirectModelScenario(true);
                    if(!resultName.endsWith(".action")) {
                        resultName = resultName + ".action";
                    }
                    if(!resultName.startsWith("/")) {
                        resultName = "/" + resultName;
                    }
                    viewName = "redirect:" + resultName;
                } else {
                    String resultName = resultConfig.getParams().get("location");
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
        } else if (returnValue != null) {
            // should not happen
            throw new UnsupportedOperationException("Unexpected return type: " +
                    returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
        }
    }

    private boolean isRedirectResultType(String className) {
        // org.apache.struts2.dispatcher.ServletDispatcherResult
        // org.apache.struts2.dispatcher.ServletRedirectResult
        // org.apache.struts2.dispatcher.ServletActionRedirectResult
        return className != null &&
                (className.endsWith("ServletRedirectResult")
                        || className.endsWith("ServletActionRedirectResult"));
    }

    private boolean isRedirectAction(String className) {
        return className != null && className.endsWith("ServletActionRedirectResult");
    }
}
