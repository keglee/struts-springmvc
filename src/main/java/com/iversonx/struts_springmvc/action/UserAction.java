package com.iversonx.struts_springmvc.action;

import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport {
    private static final long serialVersionUID = -1353901915599323577L;
    private String username;
    private String password;

    public String show() {
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
