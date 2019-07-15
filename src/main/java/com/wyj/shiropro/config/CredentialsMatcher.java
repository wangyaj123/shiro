package com.wyj.shiropro.config;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * 自定义密码校验规则
 */
public class CredentialsMatcher extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo authenticationInfo) {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        //获取session中的密码
        String password = new String(usernamePasswordToken.getPassword());
        //从Realm中传递过来的数据库中密码
        String dbPassword = (String) authenticationInfo.getCredentials();
        return this.equals(password,dbPassword);
    }
}
