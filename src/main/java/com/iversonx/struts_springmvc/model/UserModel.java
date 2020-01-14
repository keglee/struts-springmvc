package com.iversonx.struts_springmvc.model;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/14 22:22
 */
public class UserModel {
    private Integer userId;
    private String username;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
