package com.wyj.shiropro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wangyajing
 * @date 2019-08-09
 */
@Controller
public class HttpController {
    @RequestMapping("/")
    public String login(){
        return "login";
    }

}
