package com.iversonx.struts_springmvc.controller;

import com.iversonx.struts_springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/9 10:46
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/show")
    public String show(String username,
                       @RequestParam(required = false, defaultValue = "123456") String password) {
        Model model = new BindingAwareModelMap();
        model.addAttribute("username", username);
        return "show";
    }

    @RequestMapping("/ajax")
    public void ajax(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String json = "{\"name\": \"iveronsx\"}";
        response.getWriter().write(json);
        response.getWriter().flush();
    }

    @RequestMapping("/ajax2")
    @ResponseBody
    public String ajax2() {
        String json = "{\"name\": \"iveronsx\"}";
        return json;
    }
}
