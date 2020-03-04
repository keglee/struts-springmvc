package com.iversonx.struts_springmvc.support.result;

import com.iversonx.struts_springmvc.support.processor.StrutsConfigManager;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/3/3 16:06
 */
public class StreamResultHandler extends AbstractStrutsResultHandler {
    private static final String SUPPORT_CLASS = "org.apache.struts2.dispatcher.StreamResult";

    public StreamResultHandler(StrutsConfigManager strutsConfigManager) {
        super(strutsConfigManager);
    }

    @Override
    public boolean supportsResultClass(String className) {
        return SUPPORT_CLASS.equals(className);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  Object handler) throws Exception {
        mavContainer.setRequestHandled(true);
        Map<String, String> params = resultConfig.getParams();
        String contentType = params.get("contentType");
        String inputName = params.get("inputName");
        String contentDisposition = params.get("contentDisposition");
        String bufferSize = params.get("bufferSize");

        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(handler);
        InputStream stream = (InputStream)bw.getPropertyValue(inputName);




        ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
        HttpServletResponse response = servletWebRequest.getResponse();
        response.setContentLength(stream.available());
        response.setCharacterEncoding("utf-8");
        response.setContentType(contentType);

        if(contentDisposition != null) {
            // TODO 简单替换占位符
            if(contentDisposition.contains("${")) {
                int startIndex = contentDisposition.indexOf("${");
                int endIndex = contentDisposition.indexOf("}");
                String p = contentDisposition.substring(startIndex + 2, endIndex);
                String value = (String)bw.getPropertyValue(p);
                contentDisposition = contentDisposition.replace("${" + p +"}", value);
            }

            response.setHeader("Content-Disposition", contentDisposition);
        }


        byte[] buffer = new byte[400];
        int len = 0;
        OutputStream os = response.getOutputStream();

        while((len = stream.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }

        os.close();
        stream.close();
    }
}
