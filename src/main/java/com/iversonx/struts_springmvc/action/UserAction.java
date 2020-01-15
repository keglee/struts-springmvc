package com.iversonx.struts_springmvc.action;

import com.iversonx.struts_springmvc.model.UserModel;
import com.iversonx.struts_springmvc.service.UserService;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

public class UserAction extends ActionSupport {

    private UserService userService;

    private static final long serialVersionUID = -1353901915599323577L;
    private String username;
    private String password;
    private File docFile;

    private UserModel userModel;

    private String list;

    private String[] array;

    public String show() {
        username = userService.show();
        return "success";
    }

    public String add() {
        return "success";
    }

    public String testForm() {
        return "testForm";
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

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public File getDocFile() {
        return docFile;
    }

    public void setDocFile(File docFile) {
        this.docFile = docFile;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String[] getArray() {
        return array;
    }

    public void setArray(String[] array) {
        this.array = array;
    }
}
