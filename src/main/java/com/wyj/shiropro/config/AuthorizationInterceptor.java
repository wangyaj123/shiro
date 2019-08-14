package com.wyj.shiropro.config;

import com.wyj.shiropro.model.ShiroToken;
import com.wyj.shiropro.util.LogUtils;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;

import javax.naming.AuthenticationException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wangyajing
 * @date 2019-08-13
 */
public class AuthorizationInterceptor extends BasicHttpAuthenticationFilter {
    private Logger log = LogUtils.get(this.getClass());

    /**
     * 判断用户是否想要登入
     * 检测header里面是否包含Authorization字段
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        return authorization !=null;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws AuthenticationException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("Authorization");
        ShiroToken token =new ShiroToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        try{
            getSubject(request, response).login(token);
        }catch (Exception e){
            log.info("{}没有权限",token.getCredentials() );
            throw new AuthenticationException();
        }
        // 如果没有抛出异常,反之则代表登入成功，返回true

        return true;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                executeLogin(request, response);
            }catch (Exception e) {
                log.warn("没有权限");
            }

        }

        return true;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        return super.preHandle(request, response);
    }
}
