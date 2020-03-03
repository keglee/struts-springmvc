package com.iversonx.struts_springmvc.support.result;

import com.iversonx.struts_springmvc.support.processor.StrutsConfigManager;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 处理常用的转发到视图
 *
 * <b>示例:</b>
 *
 * <pre>
 *     &lt;result name="success" type="dispatcher"&gt;
 *         &lt;param name="location"&gt;success.jsp&lt;/param&gt;
 *     &lt;/result&gt;
 * </pre>
 * 或者
 * <pre>
 *     &lt;result name="success"&gt;success.jsp&lt;/result&gt;
 * </pre>
 */
public class DispatcherResultValueHandler extends AbstractStrutsResultValueHandler {
    private static final String SUPPORT_CLASS = "org.apache.struts2.dispatcher.ServletDispatcherResult";
    public DispatcherResultValueHandler(StrutsConfigManager strutsConfigManager) {
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
            // 设置viewName
            String viewName = null;
            String resultName = resultConfig.getParams().get("location");
            int index = resultName.indexOf(".jsp");
            if (index > -1) {
                viewName = resultName.substring(0, index);
            }

            if (viewName == null) {
                viewName = returnValue.toString();
            }
            mavContainer.setViewName(viewName);
        } else if(returnValue != null ){
            throw new UnsupportedOperationException("Unexpected return type: " +
                    returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
        }

    }
}
