package com.wyj.shiropro.model;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author wangyajing
 * @date 2019-08-13
 */
public class ShiroToken implements AuthenticationToken {
    private String token;

    public ShiroToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
