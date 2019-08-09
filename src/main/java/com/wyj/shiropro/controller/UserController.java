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

import java.io.Serializable;

/**
 * @author wangyajing
 * @date
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    Logger log = LogUtils.get(UserController.class);

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ActionResponse logout(){
        Subject subject = SecurityUtils.getSubject();
        //用户不为空，则手动登出
        if(subject !=null){
            subject.logout();
        }
        return ActionResponse.success();
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)

    public ActionResponse admin(){
        return ActionResponse.success();
    }


    @RequestMapping("/edit")

    public ActionResponse edit(){
        return ActionResponse.success();
    }
    @RequestMapping(value = "/unauthorized", method = RequestMethod.POST)
    public ActionResponse auth(){
        return ActionResponse.failed(RespBasicCode.ACCOUNT_NOT_AUTH);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
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
            //设置session的时间
            SecurityUtils.getSubject().getSession().setTimeout(5*60*1000);
            //token信息
            Serializable tokenId = subject.getSession().getId();
            log.info("====={}登录成功\n,tokenId={},token={}",userRequest.toString(),tokenId,token);
            return ActionResponse.success();
        }catch (Exception e){
            log.warn("=====登录成功，{}",userRequest.toString());
            return ActionResponse.failed(RespBasicCode.BUSINESS_EXCEPTION);
        }
    }

}
