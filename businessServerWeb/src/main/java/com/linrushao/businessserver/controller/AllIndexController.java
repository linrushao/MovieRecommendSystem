package com.linrushao.businessserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author LRS
 * @Date 2022/9/25 21:44
 * Desc
 */
@Controller
public class AllIndexController {

    @RequestMapping("/register")
    public String register(){
        return "register";
    }
    @RequestMapping("/login")
    public String login(){
        return "login";
    }


}
