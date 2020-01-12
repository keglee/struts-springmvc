package com.iversonx.struts_springmvc.controller;

import com.iversonx.struts_springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/9 10:46
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@RequestMapping("/mvc")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/show.action")
    public String show(Model model,
                       @RequestParam(required = false, defaultValue = "Hello SpringMVC") String username,
                       @RequestParam(required = false, defaultValue = "123456") String password) {
        model.addAttribute("username", username);
        return "show";
    }
}
