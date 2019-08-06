package com.wyj.shiropro.controller;

import com.wyj.shiropro.model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping("/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        //用户不为空，则手动登出
        if(subject !=null){
            subject.logout();
        }
        return "login";
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(){
        return "admin success";
    }

    @RequestMapping("/unauthorized")
    public String unauthorized(){
        return "unauthorized";
    }

    @RequestMapping("/edit")
    @ResponseBody
    public String edit(){
        return "edit success";
    }
    @RequestMapping("/add")
    @ResponseBody
    public String add(){
        return "add success";
    }
    @RequestMapping("/loginUser")
    public String loginUser( @RequestParam("username")String username,
                            @RequestParam("password") String password,
                            HttpSession session){
        System.out.println("username:"+username+",password:"+password);
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        //主体
        Subject subject = SecurityUtils.getSubject();
        //认证逻辑可能出现异常
        try {
            //登录
            subject.login(token);
            //获取登录用户
            User user = (User) subject.getPrincipal();
            session.setAttribute("user", user);
            return "index";
        }catch (Exception e){
            return "login";
        }
    }

}
