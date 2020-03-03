package com.iversonx.struts_springmvc.support;

import com.iversonx.struts_springmvc.support.result.StrutsResultValueHandlerComposite;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class StrutsInvocableHandlerMethod extends ServletInvocableHandlerMethod {
    private final ConversionService conversionService;
    private final MappingJackson2HttpMessageConverter messageConverter;
    private StrutsResultValueHandlerComposite strutsReturnValueHandlers;

    public StrutsInvocableHandlerMethod(HandlerMethod handlerMethod,
                                        ConversionService conversionService,
                                        MappingJackson2HttpMessageConverter messageConverter) {
        super(handlerMethod);
        this.conversionService = conversionService;
        this.messageConverter = messageConverter;
    }

    public void setStrutsReturnValueHandlers(StrutsResultValueHandlerComposite returnValueHandlers) {
        this.strutsReturnValueHandlers = returnValueHandlers;
    }


    @Override
    public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(getBean());
        bw.setConversionService(conversionService);
        // 当嵌套属性为null时，自动创建嵌套属性的实例
        bw.setAutoGrowNestedPaths(true);

        // 解析参数
        argumentResolver(webRequest.getRequest(), bw);

        // 调用handler
        Object returnValue = super.invokeForRequest(webRequest, mavContainer, providedArgs);
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        if (returnValue instanceof CharSequence) {
            addAttribute(mavContainer, bw);
        }

        this.strutsReturnValueHandlers.handleReturnValue(
                returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
    }

    private void argumentResolver(HttpServletRequest request, BeanWrapper bw) throws Exception{
        resolveRequestParameter(request, bw);
        resolveRequestBody(request, bw);
    }

    /**
     * 解析获取查询字符串或表单数据(content-type=application/x-www-form-urlencoded)
     * @param request HttpServletRequest
     * @param bw BeanWrapper
     */
    private void resolveRequestParameter(HttpServletRequest request, BeanWrapper bw) {
        // 获取查询字符串或表单数据(content-type=application/x-www-form-urlencoded)
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String[] value = request.getParameterValues(name);
            if (value != null && value.length > 0) {
                if (value.length == 1) {
                    bw.setPropertyValue(name, value[0]);
                } else {
                    bw.setPropertyValue(name, value);
                }
            }
        }
    }

    /**
     *
     * @param request
     * @param bw
     * @throws Exception
     */
    public void resolveRequestBody(HttpServletRequest request, BeanWrapper bw) throws Exception{
        long contentLength = request.getContentLengthLong();
        String contentType = request.getContentType();
        boolean isEmptyContentType = contentType == null || contentType.length() < 1;
        if (!isEmptyContentType && contentLength > 0) {

            MediaType mediaType = MediaType.valueOf(contentType);
            if (MediaType.APPLICATION_JSON.includes(mediaType)) {
                // json数据
                HttpInputMessage httpInputMessage = new ServletServerHttpRequest(request);
                Object object = messageConverter.read(getBeanType(), httpInputMessage);
                BeanUtils.copyProperties(object, getBean());
            } else if (MediaType.MULTIPART_FORM_DATA.includes(mediaType)) {
                // 文件上传
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
                for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
                    String key = entry.getKey();
                    MultipartFile value = entry.getValue();
                    bw.setPropertyValue("fileName", value.getOriginalFilename());
                    File file = File.createTempFile("upload_",".tmp");

                    try (InputStream input = value.getInputStream();
                         OutputStream output = new FileOutputStream(file)) {
                        int bytesRead;
                        byte[] buffer = new byte[2048];
                        while ((bytesRead = input.read(buffer, 0, 2048)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                    }
                    bw.setPropertyValue(key, file);
                }
            }
        }
    }

    private final List<String> EXCLUDE_FIELDS = new ArrayList<String>(9) {
        private static final long serialVersionUID = 5499251093524235288L;
        {
        add("actionErrors");
        add("actionMessages");
        add("errorMessages");
        add("class");
        add("container");
        add("errors");
        add("fieldErrors");
        add("locale");
        add("texts");
    }};

    private void addAttribute(ModelAndViewContainer mavContainer, BeanWrapper wrapper) {
        ModelMap modelMap = mavContainer.getModel();

        //BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(handler);
        PropertyDescriptor[] s = wrapper.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : s) {
            String name = descriptor.getName();
            Class clazz = descriptor.getPropertyType();
            if (!EXCLUDE_FIELDS.contains(name)
                    && !File.class.equals(clazz)
                    && wrapper.isReadableProperty(name)
                    && !name.endsWith("Service")
                    && !name.endsWith("Facade")) {
                modelMap.addAttribute(name, wrapper.getPropertyValue(name));
            }
        }
    }
}
