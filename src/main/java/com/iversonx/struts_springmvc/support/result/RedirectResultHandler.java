package com.iversonx.struts_springmvc.support.result;

import com.iversonx.struts_springmvc.support.processor.StrutsConfigManager;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 处理重定向到action
 *
 * <b>示例:</b>
 *
 * <pre>
 *   &lt;result name="success" type="redirect"&gt;
 *      &lt;param name="location"&gt;foo.jsp&lt;/param&gt;
 * &lt;/result&gt;
 * </pre>
 * 或
 * <pre>
 *   &lt;result name="success" type="redirect"&gt;foo.jsp&lt;/result&gt;
 * </pre>
 */
public class RedirectResultHandler extends AbstractStrutsResultHandler {

    private static final String SUPPORT_CLASS = "org.apache.struts2.dispatcher.ServletRedirectResult";

    public RedirectResultHandler(StrutsConfigManager strutsConfigManager) {
        super(strutsConfigManager);
    }

    @Override
    public boolean supportsResultClass(String className) {
        return SUPPORT_CLASS.equals(className);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  Object handler) throws Exception{

        if (returnValue instanceof CharSequence
                && resultConfig != null) {
            String resultName = resultConfig.getParams().get("location");
            if (!resultName.endsWith(".action")) {
                resultName = resultName + ".action";
            }
            if (!resultName.startsWith("/")) {
                resultName = "/" + resultName;
            }

            String viewName = "redirect:" + resultName;
            mavContainer.setRedirectModelScenario(true);
            mavContainer.setViewName(viewName);
        } else if(returnValue != null ){
            throw new UnsupportedOperationException("Unexpected return type: " +
                    returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
        }
    }
}
