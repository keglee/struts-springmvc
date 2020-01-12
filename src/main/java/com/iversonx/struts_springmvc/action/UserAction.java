package com.iversonx.struts_springmvc.action;

import com.iversonx.struts_springmvc.service.UserService;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAction extends ActionSupport {

    @Autowired
    private UserService userService;

    private static final long serialVersionUID = -1353901915599323577L;
    private String username;
    private String password;

    public String show() {
        username = userService.show();
        return "success";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
