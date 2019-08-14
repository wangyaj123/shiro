package com.wyj.shiropro.service;

import com.wyj.shiropro.model.TokenEntiy;
import com.wyj.shiropro.util.LogUtils;
import com.wyj.shiropro.util.RedisUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author wangyajing
 */
@Service
public class TokenServiceImpl {
    private Logger log = LogUtils.get(this.getClass());
    @Resource
    private RedisUtils redisUtils;
    private final String JSESSIONID_KEY="JSESSIONID_KEY_TOEKEN_%s";
    private final Long TOKEN_EXPIRES_SEC = 60*30L;
    public TokenEntiy createToken(int userId) {

        //使用uuid作为源token
        String token = UUID.randomUUID().toString().replace("-", "");
        log.info("生成自定义token={}",token);
        TokenEntiy tokenEntity =new TokenEntiy(token,userId);
        log.info("userID={}",userId);
        //存储到redis并设置过期时间
        redisUtils.set(String.format(JSESSIONID_KEY,token),String.valueOf(userId),TOKEN_EXPIRES_SEC);
        log.info("=========将token保存在redis中===========");
        return tokenEntity;

    }
    public boolean checkToken(String token) {

        redisUtils.get(String.format(JSESSIONID_KEY,token));

        //如果验证成功，说明此用户进行了一次有效操作，延长token的过期时间

        redisUtils.expire(String.format(JSESSIONID_KEY,token),TOKEN_EXPIRES_SEC);
        return true;

    }


    public TokenEntiy getToken(String token) {

        String userId = (String) redisUtils.get(String.format(JSESSIONID_KEY, token));

        return new TokenEntiy(token,Integer.valueOf(userId));

    }


    public void deleteToken(String token) {
        redisUtils.del(String.format(JSESSIONID_KEY,token));
    }
}
