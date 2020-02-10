package com.iversonx.struts_springmvc.controller;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/2/10 11:26
 */
@Component("/beanName")
public class BeanNameController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("simple");
        mav.addObject("simpleName", "BeanNameController");
        return mav;
    }
}
