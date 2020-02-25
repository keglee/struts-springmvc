package com.iversonx.struts_springmvc.action;

import com.iversonx.struts_springmvc.bean.User;
import com.iversonx.struts_springmvc.service.UserService;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

public class UserAction extends ActionSupport {

    @Autowired
    private UserService userService;

    private static final long serialVersionUID = -1353901915599323577L;

    private Integer id;
    private List<User> users;
    private User user;
    /**
     * 列表
     */
    public String list() {
        users = userService.list();
        return "success";
    }

    /**
     * 详情
     */
    public String detail() {
        user = userService.detail(id);
        return "detail";
    }

    public String add() {
        userService.add(user);
        return "add";
    }

    public String update() {
        userService.update(user);
        return "update";
    }

    public String delete() {
        userService.delete(id);
        return "success";
    }

    // 文件上传
    // 文件下载
    // ajax请求

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
