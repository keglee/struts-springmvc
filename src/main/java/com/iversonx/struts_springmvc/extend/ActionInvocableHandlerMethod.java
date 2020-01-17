package com.iversonx.struts_springmvc.extend;


import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.NativeWebRequest;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 作用: 调用HandlerMethodArgumentResolver解析参数解析后，调用具体的Handler进行请求处理
 */
public class ActionInvocableHandlerMethod extends ServletInvocableHandlerMethod {
    private final ConversionService conversionService;
    private final MappingJackson2HttpMessageConverter messageConverter;

    public ActionInvocableHandlerMethod(HandlerMethod handlerMethod,
                                        ConversionService conversionService,
                                        MappingJackson2HttpMessageConverter messageConverter) {
        super(handlerMethod);
        this.conversionService = conversionService;
        this.messageConverter = messageConverter;
    }

    /**
     * 调用实际的Handler之前，填充参数
     */
    @Override
    public Object invokeForRequest(NativeWebRequest request, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        Object handler = getBean();
        Class<?> handlerClass = getBeanType();
        if (ActionSupport.class.equals(handlerClass.getSuperclass())) {
            if (request instanceof ServletWebRequest) {
                ServletWebRequest webRequest = (ServletWebRequest) request;
                HttpServletRequest httpServletRequest = webRequest.getRequest();
                Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
                System.out.println("Request URL: " + httpServletRequest.getRequestURL());
                System.out.println("Request Method: " + httpServletRequest.getMethod());
                System.out.println("Request Headers: ");
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    System.out.println(name + ": " + request.getHeader(name));
                }


                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(handler);
                bw.setConversionService(conversionService);
                // 当嵌套属性为null时，自动创建嵌套属性的实例
                bw.setAutoGrowNestedPaths(true);

                // 获取查询字符串或表单数据(content-type=application/x-www-form-urlencoded)
                Enumeration<String> names = httpServletRequest.getParameterNames();
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

                // 从request body 获取数据
                long contentLength = httpServletRequest.getContentLengthLong();
                String contentType = httpServletRequest.getContentType();
                boolean isEmptyContentType = contentType == null || contentType.length() < 1;
                if (!isEmptyContentType && contentLength > 0) {

                    MediaType mediaType = MediaType.valueOf(contentType);
                    if (MediaType.APPLICATION_JSON.includes(mediaType)) {
                        // json数据
                        HttpInputMessage httpInputMessage = new ServletServerHttpRequest(httpServletRequest);
                        Object object = messageConverter.read(handlerClass, httpInputMessage);
                        BeanUtils.copyProperties(object, handler);
                    } else if (MediaType.MULTIPART_FORM_DATA.includes(mediaType)) {
                        // 文件上传
                        MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) httpServletRequest;
                        Map<String, MultipartFile> fileMap = multipart.getFileMap();
                        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
                            String key = entry.getKey();
                            MultipartFile value = entry.getValue();
                            File file = new File(value.getOriginalFilename());
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
        }

        Object value = super.invokeForRequest(request, mavContainer, providedArgs);

        if (ActionSupport.class.equals(handlerClass.getSuperclass())) {
            if (value instanceof CharSequence) {
                updateModel(handler, mavContainer);
            } else if(value == null) {
                // 当返回值null，则认为已经异步响应。避免再去寻找jsp页面进行响应
                mavContainer.setRequestHandled(true);
            }
        }

        return value;
    }

    private final List<String> EXCLUDE_FIELDS = new ArrayList<String>() {{
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

    private void updateModel(Object handler, ModelAndViewContainer mavContainer) {
        ModelMap modelMap = mavContainer.getModel();

        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(handler);
        PropertyDescriptor[] s = wrapper.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : s) {
            String name = descriptor.getName();
            Class clazz = descriptor.getPropertyType();
            if (!EXCLUDE_FIELDS.contains(name)
                    && !File.class.equals(clazz)
                    && wrapper.isReadableProperty(name)
                    && !name.endsWith("Service")
                    && !name.endsWith("Facade")) {
                System.out.println(name + ":" + descriptor.getPropertyType());
                modelMap.addAttribute(name, wrapper.getPropertyValue(name));
            }
        }
    }
}
