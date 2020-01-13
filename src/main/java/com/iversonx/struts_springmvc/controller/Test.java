package com.iversonx.struts_springmvc.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/13 18:02
 */
@Component
public class Test {

    @ResponseBody

    public String test() {
        return "这样也可以???";
    }
}
