package com.iversonx.struts_springmvc.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/14 14:28
 */
public class UserModel {
    private Integer userId;
    private String username;
    private Date date;
    private BigDecimal amt;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }
}
