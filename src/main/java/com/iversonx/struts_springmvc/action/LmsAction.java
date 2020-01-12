package com.iversonx.struts_springmvc.action;

import com.opensymphony.xwork2.ActionSupport;

public class LmsAction extends ActionSupport {
    private String lmsName;
    public String lms() {
        if(lmsName == null) {
            lmsName = "LMS";
        }
        return "success";
    }

    public String getLmsName() {
        return lmsName;
    }

    public void setLmsName(String lmsName) {
        this.lmsName = lmsName;
    }
}
