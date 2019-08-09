package com.wyj.shiropro.config;

import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wangyajing
 */
@Configuration
public class ShiroConfig {
    /**
     * 项目启动shiroFilter首先会被初始化，并且逐层传入SecurityManager，Realm, matcher
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager manager) {
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
        //无权限跳转页面
        bean.setUnauthorizedUrl("/unauthorized");
        //设置遇到未登录，返回相应的错误
        bean.setLoginUrl("/login");

        //请求-拦截器（权限配置）键值对
        LinkedHashMap<String, String> filterChainDefinitonMap = new LinkedHashMap<>();

        //首页地址index，使用authc过滤器进行处理
//        filterChainDefinitonMap.put("/index","authc");
        //登录页面不需要过滤操作
        filterChainDefinitonMap.put("/api/user/loginUser", "anon");
        //不做身份验证
        filterChainDefinitonMap.put("/loginUser", "anon");
        //只有角色为admin的才能访问/admin
        filterChainDefinitonMap.put("/admin", "roles[admin]");
        //权限过滤，拥有edit权限的才能访问
        filterChainDefinitonMap.put("/edit", "perms[edit]");
        filterChainDefinitonMap.put("/add", "perms[add]");
        //其他请求只验证是否登录过
        filterChainDefinitonMap.put("/**", "user");
        //放入Shiro过滤器
        bean.setFilterChainDefinitionMap(filterChainDefinitonMap);
        //配置不会被拦截的接口
//        bean.setFilterChainDefinitionMap()

        //自定义拦截器
        Map<String, Filter> tokenFilterMap = new LinkedHashMap<>();
        tokenFilterMap.put("AuthenticationFilter", new AuthenticationFilter());
        bean.setFilters(tokenFilterMap);
        return bean;
    }

    /**
     * 将定义好的Realm放入安全会话中心
     *
     * @param authRealm
     * @return
     */
    @Bean("securityManager")
    public SecurityManager securityManager(@Qualifier("authRealm") AuthRealm authRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        //设置自定义的realm
        manager.setRealm(authRealm);
        //自定义shiro session 缓存管理器
        manager.setSessionManager(sessionManager());
        //将缓存对象注入到securityManager中
//        manager.setCacheManager(ehCacheManager());
        return manager;
    }

    /**
     * 将自定义的校验规则放入Realm
     *
     * @param matcher
     * @return
     */
    @Bean("authRealm")
    public AuthRealm authRealm(@Qualifier("credentialmatcher") CredentialsMatcher matcher) {
        AuthRealm authRealm = new AuthRealm();
        //信息放入缓存
        authRealm.setCacheManager(new MemoryConstrainedCacheManager());
        authRealm.setCredentialsMatcher(matcher);
        return authRealm;
    }

    /**
     * 校验规则
     *
     * @return
     */
    @Bean("credentialmatcher")
    public CredentialsMatcher credentialsMatcher() {
        return new CredentialsMatcher();
    }

    /**
     * Spring和Shiro关联
     *
     * @param manager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager manager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }

    /**
     * 开启代理
     *
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    /**
     * shiro缓存管理器
     * 1 添加相关的maven支持
     * 2 注册这个bean，将缓存的配置文件导入
     * 3 在securityManager 中注册缓存管理器，之后就不会每次都会去查询数据库了，相关的权限和角色会保存在缓存中，但需要注意一点，更新了权限等操作之后，需要及时的清理缓存
     */
    @Bean
    public EhCacheManager ehCacheManager() {
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:config/ehcache.xml");
        return cacheManager;
    }

    /**
     * 自定义的 shiro session 缓存管理器，用于跨域等情况下使用 token 进行验证，不依赖于sessionId
     *
     * @return
     */
    @Bean
    public SessionManager sessionManager() {
        //将我们继承后重写的shiro session 注册
        ShiroSession shiroSession = new ShiroSession();
        //如果后续考虑多tomcat部署应用，可以使用shiro-redis开源插件来做session 的控制，或者nginx 的负载均衡
        shiroSession.setSessionDAO(new EnterpriseCacheSessionDAO());
        return shiroSession;
    }
}
