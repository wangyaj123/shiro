package com.wyj.shiropro.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author wangyajing
 * @Date 2019-08-07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 测试redis连接是否成功，通过key获取cmd窗口set的value
     */
    @Test
    public void getValue(){
        String value = stringRedisTemplate.opsForValue().get("key");
        System.out.println(value);
    }
}
