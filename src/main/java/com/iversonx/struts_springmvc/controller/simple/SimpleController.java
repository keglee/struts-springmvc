package com.iversonx.struts_springmvc.controller.simple;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/2/10 10:41
 */
@Component("simpleController")
public class SimpleController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("simple");
        mav.addObject("simpleName", "SimpleController");
        return mav;
    }
}
