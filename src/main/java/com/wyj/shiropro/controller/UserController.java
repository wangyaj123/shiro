package com.wyj.shiropro.controller;

import com.wyj.shiropro.common.ActionResponse;
import com.wyj.shiropro.common.RespBasicCode;
import com.wyj.shiropro.model.User;
import com.wyj.shiropro.pojo.request.UserRequest;
import com.wyj.shiropro.service.TokenServiceImpl;
import com.wyj.shiropro.service.UserService;
import com.wyj.shiropro.util.LogUtils;
import com.wyj.shiropro.util.RedisUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * @author wangyajing
 * @date
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private TokenServiceImpl tokenService;
    private RedisUtils redisUtils;

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
    @RequestMapping(value = "/unauthorized")
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
        token.setRememberMe(true);
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
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ActionResponse login(@RequestBody UserRequest userRequest) {
        User user = userService.login(userRequest.getUsername(), userRequest.getPassword());
        if (user != null){
            if(userRequest.getPassword().equals(user.getPassword())){
                //生成一个token
                tokenService.createToken(user.getUid());
                return ActionResponse.success("登录成功");
            }
        }
        return ActionResponse.failed(RespBasicCode.LOGIN_FAIL);
    }

}
