package com.iversonx.struts_springmvc.extend;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;

/**
 * 拦截器，在请求处理前后进行额外的操作
 */
public class ActionHandlerInterceptor extends HandlerInterceptorAdapter {
    private final ConversionService conversionService;
    private final MappingJackson2HttpMessageConverter messageConverter;

    public ActionHandlerInterceptor(final ConversionService conversionService,
                                    final MappingJackson2HttpMessageConverter messageConverter) {
        this.conversionService = conversionService;
        this.messageConverter = messageConverter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*Enumeration<String> headerNames = request.getHeaderNames();
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Request Headers: ");
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            System.out.println(name + ": " + request.getHeader(name));
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(handlerMethod.getBean());
            bw.setConversionService(conversionService);
            // 当嵌套属性为null时，自动创建嵌套属性的实例
            bw.setAutoGrowNestedPaths(true);

            // 获取查询字符串或表单数据(content-type=application/x-www-form-urlencoded)
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String[] value = request.getParameterValues(name);
                if(value != null && value.length > 0) {
                    if(value.length == 1) {
                        bw.setPropertyValue(name, value[0]);
                    } else {
                        bw.setPropertyValue(name, value);
                    }
                }
            }

            // 从request body 获取数据
            long contentLength = request.getContentLengthLong();
            String contentType = request.getContentType();
            boolean isEmptyContentType = contentType == null || contentType.length() < 1;
            if(!isEmptyContentType && contentLength > 0) {

                MediaType mediaType = MediaType.valueOf(contentType);
                if(MediaType.APPLICATION_JSON.includes(mediaType)) {
                    // json数据
                    HttpInputMessage httpInputMessage = new ServletServerHttpRequest(request);
                    Object object = mappingJackson2HttpMessageConverter.read(handlerMethod.getBean().getClass(), httpInputMessage);
                    BeanUtils.copyProperties(object, handlerMethod.getBean());
                } else if (MediaType.MULTIPART_FORM_DATA.includes(mediaType)) {
                    // 文件上传
                    MultipartHttpServletRequest multipart = (MultipartHttpServletRequest)request;
                    Map<String, MultipartFile> fileMap = multipart.getFileMap();
                    for(Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
                        String key = entry.getKey();
                        MultipartFile value = entry.getValue();
                        File file = new File(value.getOriginalFilename());
                        try (InputStream input = value.getInputStream();
                             OutputStream output = new FileOutputStream(file)){
                            int bytesRead = 0;
                            byte[] buffer = new byte[2048];
                            while ((bytesRead = input.read(buffer, 0, 2048)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                        }
                        bw.setPropertyValue(key, file);
                    }
                }
            }
        }*/
        return true;
    }
}
