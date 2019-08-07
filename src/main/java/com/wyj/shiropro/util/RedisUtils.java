package com.wyj.shiropro.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Component
public class RedisUtils {
    private static final Logger logger = LogUtils.get();

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     * TimeUnit.DAYS          //天
     * TimeUnit.HOURS         //小时
     * TimeUnit.MINUTES       //分钟
     * TimeUnit.SECONDS       //秒
     * TimeUnit.MILLISECONDS  //毫秒
     */
    public boolean expire(String key, long time) {
        if (time > 0) {
            //expire(key, time, 时间单位)
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
        return true;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 批量删除缓存
     *
     * @param keyList key集合
     */
    public void del(List<String> keyList) {
        redisTemplate.delete(keyList);
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
     *
     * @param key
     * @param value
     * @param timeout 过期时间
     * @param unit    时间单位, 天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES 秒:TimeUnit.SECONDS
     *                毫秒:TimeUnit.MILLISECONDS
     */
    public void setEx(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return 0;
        }
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key    键
     * @param value  值
     * @param repeat 是否允许value值重复
     * @return
     */
    public boolean lSet(String key, Object value, boolean repeat) {
        try {
            // 如果不允许重复，则先把value值删除
            if (repeat == false) {
                redisTemplate.opsForList().remove(key, 0, value);
            }
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time, boolean repeat) {
        try {
            // 如果不允许重复，则先把value值删除
            if (repeat == false) {
                redisTemplate.opsForList().remove(key, 0, value);
            }
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value, boolean repeat) {
        try {
            // 如果不允许重复，则先把value值删除
            if (repeat == false) {
                redisTemplate.opsForList().remove(key, 0, value);
            }
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time, boolean repeat) {
        try {
            // 如果不允许重复，则先把value值删除
            if (repeat == false) {
                redisTemplate.opsForList().remove(key, 0, value);
            }
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return 0;
        }
    }

    // ===============================Object,对类的操作=================================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @param t   类.class
     * @return 值
     */
    public <T> T getObject(String key, Class<T> t) throws Exception {
        Object object = redisTemplate.opsForValue().get(key);
        T ob = null;
        if (object != null) {
            String json = object.toString();
            ob = this.jsonStr2Object(json, t);
        }

        return ob;
    }

    /**
     * 普通缓存放入
     *
     * @param key    键
     * @param object 对象
     * @return true成功 false失败
     */
    public boolean setObject(String key, Object object) {
        try {
            redisTemplate.opsForValue().set(key, object2JsonStr(object));
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key    键
     * @param object 对象
     * @param time   时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean setObject(String key, Object object, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, object2JsonStr(object), time, TimeUnit.SECONDS);
            } else {
                set(key, object);
            }
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    // ===============================list<Object>=================================

    /**
     * 获取list缓存的内容，并转化成对应的list对象
     *
     * @param key 键
     * @return
     */
    public <T> List<T> getObjectList(String key, Class<T> t) throws Exception {
        List<Object> objectList = redisTemplate.opsForList().range(key, 0, -1);
        return this.parseList(objectList, t);
    }

    /**
     * 将list对象放入缓存
     *
     * @param key 键
     * @return
     */
    public boolean setObjectList(String key, List<?> objectList) {
        try {
            if (!CollectionUtils.isEmpty(objectList)) {
                // 先移除之前的数据
                redisTemplate.opsForList().trim(key, 0, 0);
                redisTemplate.opsForList().rightPop(key);
                // 重新放入
                for (Object object : objectList) {
                    redisTemplate.opsForList().rightPush(key, object2JsonStr(object));
                }
            }
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean setObjectList(String key, List<?> objectList, long time) {
        try {
            if (!CollectionUtils.isEmpty(objectList)) {
                // 先移除之前的数据
                redisTemplate.opsForList().trim(key, 0, 0);
                redisTemplate.opsForList().rightPop(key);
                // 重新放入
                for (Object object : objectList) {
                    redisTemplate.opsForList().rightPush(key, object2JsonStr(object));
                }
                if (time > 0) {
                    expire(key, time);
                }
            }
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 在list中追加元素
     *
     * @param key    键
     * @param object 需要追加到list的对象
     * @return
     */
    public boolean setObjectToList(String key, Object object) {
        try {
            // 追加
            redisTemplate.opsForList().rightPush(key, object2JsonStr(object));
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 在list中追加对象
     *
     * @param key    键
     * @param object 需要追加到list的对象
     * @param time   多长时间失效
     * @return
     */
    public boolean setObjectToList(String key, Object object, long time) {
        try {
            // 追加
            redisTemplate.opsForList().rightPush(key, object2JsonStr(object));
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 更改list中的某个对象
     *
     * @param key    键
     * @param object 需要更改的对象
     * @return
     */
    public boolean updateObjectInList(String key, Object object) {
        try {
            // 先删除之前的对象
            List<Object> objectList = redisTemplate.opsForList().range(key, 0, -1);
            if (!CollectionUtils.isEmpty(objectList)) {
                for (Object ob : objectList) {
                    if (!StringUtils.isEmpty(this.getFieldValueByName("id", ob))) {
                        if (this.getFieldValueByName("id", ob).equals(this.getFieldValueByName("id", object))) {
                            redisTemplate.opsForList().remove(key, 0, ob);
                            break;
                        }
                    }
                }
            }
            // 再追加
            redisTemplate.opsForList().rightPush(key, object2JsonStr(object));
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    /**
     * 更改list中的某个对象
     *
     * @param key    键
     * @param object 需要更改的对象
     * @param time   多长时间失效
     * @return
     */
    public boolean updateObjectInList(String key, Object object, long time) {
        try {
            // 先删除之前的对象
            List<Object> objectList = redisTemplate.opsForList().range(key, 0, -1);
            if (!CollectionUtils.isEmpty(objectList)) {
                for (Object ob : objectList) {
                    if (this.getFieldValueByName("id", ob) != null
                            && this.getFieldValueByName("id", ob)
                            .equals(this.getFieldValueByName("id", object))) {
                        redisTemplate.opsForList().remove(key, 0, ob);
                        break;
                    }
                }
            }
            // 再追加
            redisTemplate.opsForList().rightPush(key, object2JsonStr(object));
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    // ===============================String（StringRedisTemplate操作string）=================================
    public boolean setString(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        }catch (Exception e){
            LogUtils.info(logger, e.getMessage());
            return false;
        }
    }

    public String getString(String key) throws Exception{
        return stringRedisTemplate.opsForValue().get(key);
    }

    // ===============================模糊查询=================================

    /**
     * 根据关键字模糊查询缓存并转化成相应的结果集
     *
     * @param Keyword
     * @param t
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> getObjectsByKeyword(String Keyword, Class<T> t) throws Exception {
        Set<String> keyList = redisTemplate.keys(Keyword + "*");
        List<T> tList = new ArrayList<T>();
        if (!CollectionUtils.isEmpty(keyList)) {
            for (String key : keyList) {
                tList.add(jsonStr2Object(redisTemplate.opsForValue().get(key).toString(), t));
            }
        } else {
            tList = null;
        }
        return tList;
    }

    // ===============================模糊删除=================================

    /**
     * 根据关键字模糊删除缓存
     *
     * @param Keyword
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> void delObjectsByKeyword(String Keyword) throws Exception {
        Set<String> keys = redisTemplate.keys(Keyword + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    // ===============================公共方法=================================

    /**
     * 根据属性名获取属性值
     */
    private String getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            String value = "";
            if (method.invoke(o, new Object[]{}) != null) {
                value = method.invoke(o, new Object[]{}).toString();
            }
            return value;
        } catch (Exception e) {
            LogUtils.info(logger, e.getMessage());
            return "";
        }
    }

    private <T> T parse(Object s, Class<T> t) throws Exception {
        return null == s ? null : this.jsonStr2Object(s.toString(), t);
    }

    private <T> List<T> parseList(List<?> objects, Class<T> t) throws Exception {
        List<T> tList = new ArrayList<T>();
        if (objects.size() > 0) {
            for (Object object : objects) {
                tList.add(parse(object, t));
            }
        }
        return tList;
    }

    /**
     * 实体类转json字符串，空值忽略
     *
     * @param object
     * @return
     */
    private String object2JsonStr(Object object) throws Exception {
        return JSON.toJSONString(object);
    }

    /**
     * json字符串转实体类
     *
     * @param json
     * @param clazz
     * @return
     */
    private <T> T jsonStr2Object(String json, Class<T> clazz) throws Exception {
        T t = JSON.parseObject(json, clazz);
        return t;
    }

    /**
     * 切换redis 的database
     *
     * @param bd
     * @throws Exception
     */
    public void changDatabase(Integer bd) throws Exception {
        LettuceConnectionFactory jedisConnectionFactory =
                (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        jedisConnectionFactory.setDatabase(bd);
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        jedisConnectionFactory.resetConnection();
    }

    /**
     * 获取能按固定步长增长的有过期时间的ID
     *
     * @param key
     * @param increment
     * @return
     */
    public Integer generate(String key, int increment) {
        RedisAtomicInteger counter = new RedisAtomicInteger(key, redisTemplate.getConnectionFactory());
        return counter.addAndGet(increment);
    }
}
