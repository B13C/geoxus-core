package com.geoxus.core.common.util;

import cn.hutool.core.convert.Convert;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GXRedisUtils {
    @GXFieldCommentAnnotation(zh = "Logger对象")
    private static final Logger logger;

    static {
        logger = GXCommonUtils.getLogger(GXRedisUtils.class);
    }

    private GXRedisUtils() {
    }

    /**
     * 设置数据
     *
     * @param key      KEY
     * @param value    数据
     * @param expire   过期时间
     * @param timeUnit 时间单位
     * @return Object
     */
    public static Object set(String key, String value, int expire, TimeUnit timeUnit) {
        final RMap<Object, Object> rMap = getRedissonClient().getMap(key);
        if (expire > 0) {
            rMap.expire(expire, timeUnit);
        }
        return rMap.put(key, value);
    }

    /**
     * 获取数据
     *
     * @param key   数据key
     * @param clazz 返回数据的类型
     * @return Object
     */
    public static <R> R get(String key, Class<R> clazz) {
        final RMap<Object, Object> rMap = getRedissonClient().getMap(key);
        return Convert.convert(clazz, rMap.get(key));
    }

    /**
     * 删除数据
     *
     * @param key 数据key
     * @return boolean
     */
    public static boolean delete(String key) {
        final RMap<Object, Object> rMap = getRedissonClient().getMap(key);
        return null != rMap.remove(key);
    }

    /**
     * 计数器
     *
     * @param key      数据key
     * @param expire   过期时间
     * @param timeUnit 时间单位
     * @return long
     */
    public static long getCounter(String key, int expire, TimeUnit timeUnit) {
        final String MAP_CACHE_NAME = "counter_key";
        RMapCache<Object, Object> rMapCache = getRedissonClient().getMapCache(MAP_CACHE_NAME);
        Object oldCount = rMapCache.get(key);
        if (null == oldCount) {
            int counter = 1;
            rMapCache.put(key, counter, expire, timeUnit);
            return counter;
        }
        int counter = (int) oldCount + 1;
        rMapCache.put(key, counter);
        return counter;
    }

    /**
     * 获取Redis锁
     *
     * @param lockName 锁的名字
     * @return RLock
     */
    public static RLock getLock(String lockName) {
        return getRedissonClient().getLock("lock:" + lockName);
    }

    /**
     * 获取RedissonClient对象
     *
     * @return RedissonClient
     */
    private static RedissonClient getRedissonClient() {
        return GXSpringContextUtils.getBean(RedissonClient.class);
    }
}