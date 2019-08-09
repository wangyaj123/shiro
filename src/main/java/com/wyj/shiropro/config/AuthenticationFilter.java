package com.wyj.shiropro.config;

import com.alibaba.fastjson.JSONObject;
import com.wyj.shiropro.common.ActionResponse;
import com.wyj.shiropro.common.RespBasicCode;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author wangyajing
 * @date 2019-08-08
 */
public class AuthenticationFilter extends FormAuthenticationFilter {
    /**
     * 直接过滤可以访问的请求类型
     */
    private static final String REQUET_TYPE = "OPTIONS";


    public AuthenticationFilter() {
        super();
    }


    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (((HttpServletRequest) request).getMethod().toUpperCase().equals(REQUET_TYPE)) {
            return true;
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }


    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse res = (HttpServletResponse)response;
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setStatus(HttpServletResponse.SC_OK);
        res.setCharacterEncoding("UTF-8");
        PrintWriter writer = res.getWriter();
        ActionResponse actionResponse = new ActionResponse(RespBasicCode.ACCOUNT_NOT_LOGIN,"请先登录系统！");
        writer.write(JSONObject.toJSONString(actionResponse));
        writer.close();
        return false;
    }
}
