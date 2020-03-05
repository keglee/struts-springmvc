package com.iversonx.struts_springmvc.support.result;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.iversonx.struts_springmvc.support.processor.StrutsConfigManager;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.*;

public class JSONResultHandler extends AbstractStrutsResultHandler {
    private static final String SUPPORT_CLASS = "org.apache.struts2.json.JSONResult";
    public final Set<String> EXCLUDE_FIELDS = new HashSet<String>(9) {
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

    public JSONResultHandler(StrutsConfigManager strutsConfigManager) {
        super(strutsConfigManager);
    }

    @Override
    public boolean supportsResultClass(String className) {
        return SUPPORT_CLASS.equals(className);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, Object handler) throws Exception {
        mavContainer.setRequestHandled(true);
        Map<String,String> params = resultConfig.getParams();


        ObjectMapper mapper = new ObjectMapper();
        String excludeNullProperties = params.get("excludeNullProperties");
        if("true".equals(excludeNullProperties)) {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }


        // 如果配置了指定属性
        FilterProvider filter;
        String includeProperties = params.get("includeProperties");
        if(includeProperties != null && includeProperties.length() > 0) {
            String[] properties = includeProperties.split(",");
            filter = new SimpleFilterProvider().addFilter("objectFilter", SimpleBeanPropertyFilter.filterOutAllExcept(properties));
        } else {
            // 过滤ActionSupport的属性
            filter =  new SimpleFilterProvider().addFilter("objectFilter", SimpleBeanPropertyFilter.serializeAllExcept(EXCLUDE_FIELDS));
        }

        mapper.addMixIn(handler.getClass(), ObjectFilterMixIn.class);
        mapper.setFilterProvider(filter);



        ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
        HttpServletResponse response = servletWebRequest.getResponse();
        response.setCharacterEncoding("utf-8");

        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        OutputStream outputStream = response.getOutputStream();
        mapper.writeValue(outputStream, handler);
        outputStream.flush();
        outputStream.close();
    }

    @JsonFilter("objectFilter")
    interface ObjectFilterMixIn {
    }
}
