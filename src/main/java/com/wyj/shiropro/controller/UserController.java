package com.wyj.shiropro.controller;

import com.wyj.shiropro.common.ActionResponse;
import com.wyj.shiropro.common.RespBasicCode;
import com.wyj.shiropro.model.User;
import com.wyj.shiropro.pojo.request.UserRequest;
import com.wyj.shiropro.util.LogUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangyajing
 * @date
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    Logger log = LogUtils.get(UserController.class);

    @RequestMapping("/logout")
    public ActionResponse logout(){
        Subject subject = SecurityUtils.getSubject();
        //用户不为空，则手动登出
        if(subject !=null){
            subject.logout();
        }
        return ActionResponse.success();
    }

    @RequestMapping("/admin")

    public ActionResponse admin(){
        return ActionResponse.success();
    }


    @RequestMapping("/edit")

    public ActionResponse edit(){
        return ActionResponse.success();
    }
    @RequestMapping("/unauthorized")
    public ActionResponse auth(){
        return ActionResponse.failed(RespBasicCode.ACCOUNT_NOT_LOGIN);
    }

    @RequestMapping("/add")
    public ActionResponse add(){
        return ActionResponse.success();
    }

    @RequestMapping(value = "/loginUser", method = RequestMethod.POST)
    public ActionResponse loginUser(@RequestBody UserRequest userRequest){
        UsernamePasswordToken token = new UsernamePasswordToken(userRequest.getUsername(),userRequest.getPassword());
        //主体
        Subject subject = SecurityUtils.getSubject();
        //认证逻辑可能出现异常
        try {
            //登录
            subject.login(token);
            //获取登录用户
            User user = (User) subject.getPrincipal();
            log.info("=====登录成功，{}",userRequest.toString());
            return ActionResponse.success();
        }catch (Exception e){
            log.warn("=====登录成功，{}",userRequest.toString());
            return ActionResponse.failed(RespBasicCode.BUSINESS_EXCEPTION);
        }
    }

}
