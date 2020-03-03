package com.iversonx.struts_springmvc.support.result;

import com.iversonx.struts_springmvc.support.processor.StrutsConfigManager;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 处理void或String类型的返回值
 */
public class ActionRedirectResultValueHandler extends AbstractStrutsResultValueHandler {

    private static final String SUPPORT_CLASS = "org.apache.struts2.dispatcher.ServletActionRedirectResult";

    public ActionRedirectResultValueHandler(StrutsConfigManager strutsConfigManager) {
        super(strutsConfigManager);
    }


    @Override
    boolean supportsResultClass(String className) {
        return SUPPORT_CLASS.equals(className);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {

        if (returnValue instanceof CharSequence
                && resultConfig != null) {
            // 获取struts配置结果页
            String resultName = resultConfig.getParams().get("actionName");
            if (!resultName.endsWith(".action")) {
                resultName = resultName + ".action";
            }
            if (!resultName.startsWith("/")) {
                resultName = "/" + resultName;
            }

            String viewName = "redirect:" + resultName;
            mavContainer.setRedirectModelScenario(true);
            mavContainer.setViewName(viewName);
        } else {
            throw new UnsupportedOperationException("Unexpected return type: " +
                    returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
        }
    }
}
