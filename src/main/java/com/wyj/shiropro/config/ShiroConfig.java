package com.wyj.shiropro.config;

import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;

@Configuration
public class ShiroConfig {
    /**
     * 项目启动shiroFilter首先会被初始化，并且逐层传入SecurityManager，Realm, matcher
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter (@Qualifier("securityManager") SecurityManager manager){
        /**
         * authc：所有已登陆用户可访问
         * roles：有指定角色的用户可访问，通过[ ]指定具体角色，这里的角色名称与数据库中配置一致
         * perms：有指定权限的用户可访问，通过[ ]指定具体权限，这里的权限名称与数据库中配置一致
         * anon：所有用户可访问
         */
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(manager);
        //登录页面
        bean.setLoginUrl("/login");
        //登录成功跳转页面
        bean.setSuccessUrl("/index");
        //无权限跳转页面
        bean.setUnauthorizedUrl("/unauthorized");
        //请求-拦截器（权限配置）键值对
        LinkedHashMap<String,String> filterChainDefinitonMap = new LinkedHashMap<>();

        //首页地址index，使用authc过滤器进行处理
        filterChainDefinitonMap.put("/index","authc");
        //登录页面不需要过滤操作
        filterChainDefinitonMap.put("/login","anon");
        //不做身份验证
        filterChainDefinitonMap.put("/loginUser","anon");
        //只有角色为admin的才能访问/admin
        filterChainDefinitonMap.put("/admin","roles[admin]");
        //权限过滤，拥有edit权限的才能访问
        filterChainDefinitonMap.put("/edit","perms[edit]");
        filterChainDefinitonMap.put("/add","perms[add]");
        //其他请求只验证是否登录过
        filterChainDefinitonMap.put("/**","user");
        //放入Shiro过滤器
        bean.setFilterChainDefinitionMap(filterChainDefinitonMap);
        return bean;
    }

    /**
     * 将定义好的Realm放入安全会话中心
     * @param authRealm
     * @return
     */
    @Bean("securityManager")
    public SecurityManager securityManager (@Qualifier("authRealm") AuthRealm authRealm){
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(authRealm);
        return manager;
    }

    /**
     * 将自定义的校验规则放入Realm
     * @param matcher
     * @return
     */
    @Bean("authRealm")
    public AuthRealm authRealm(@Qualifier("credentialmatcher") CredentialsMatcher matcher){
        AuthRealm authRealm = new AuthRealm();
        //信息放入缓存
        authRealm.setCacheManager(new MemoryConstrainedCacheManager());
        authRealm.setCredentialsMatcher(matcher);
        return authRealm;
    }

    /**
     * 校验规则
     * @return
     */
    @Bean("credentialmatcher")
    public CredentialsMatcher credentialsMatcher(){
        return new CredentialsMatcher() ;
    }

    /**
     * Spring和Shiro关联
     * @param manager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager manager){
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }

    /**
     * 开启代理
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }
}
