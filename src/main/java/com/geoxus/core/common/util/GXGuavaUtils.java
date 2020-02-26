package com.geoxus.core.common.util;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

public class GXGuavaUtils {

    /**
     * 获取Guava的缓存实例
     *
     * @param <K>         key的泛型
     * @param <V>         值的泛型
     * @param maximumSize 最大的存储条目
     * @param duration    缓存时间
     * @param unit        缓存时间单位
     * @param supplier    当缓存中数据不存在时,提供数据的供应者,可以从数据库查询
     * @return
     */
    public static <K, V> LoadingCache<K, V> getGuavaCacheExpireAfterWrite(long maximumSize, long duration, TimeUnit unit, Supplier<V> supplier) {
        return getGuavaCacheBuilderExpireAfterWrite(maximumSize, duration, unit).build(CacheLoader.from(supplier));
    }

    /**
     * 获取Guava的缓存实例
     *
     * @param <K>         key的泛型
     * @param <V>         值的泛型
     * @param maximumSize 最大的存储条目
     * @param duration    缓存时间
     * @param unit        缓存时间单位
     * @param supplier    当缓存中数据不存在时,提供数据的供应者,可以从数据库查询
     * @return
     */
    public static <K, V> LoadingCache<K, V> getGuavaCacheExpireAfterAccess(long maximumSize, long duration, TimeUnit unit, Supplier<V> supplier) {
        return getGuavaCacheBuilderExpireAfterAccess(maximumSize, duration, unit).build(CacheLoader.from(supplier));
    }

    /**
     * 获取Guava的缓存实例
     *
     * @param <K>         key的泛型
     * @param <V>         值的泛型
     * @param maximumSize 最大的存储条目
     * @param duration    缓存时间
     * @param unit        缓存时间单位
     * @param function    当缓存中数据不存在时,提供数据的供应者,可以从数据库查询
     * @return
     */
    public static <K, V> LoadingCache<K, V> getGuavaCacheExpireAfterWrite(long maximumSize, long duration, TimeUnit unit, Function<K, V> function) {
        return getGuavaCacheBuilderExpireAfterWrite(maximumSize, duration, unit).build(CacheLoader.from(function));
    }

    /**
     * 获取Guava的缓存实例
     *
     * @param <K>         key的泛型
     * @param <V>         值的泛型
     * @param maximumSize 最大的存储条目
     * @param duration    缓存时间
     * @param unit        缓存时间单位
     * @param function    当缓存中数据不存在时,提供数据的供应者,可以从数据库查询
     * @return
     */
    public static <K, V> LoadingCache<K, V> getGuavaCacheExpireAfterAccess(long maximumSize, long duration, TimeUnit unit, Function<K, V> function) {
        return getGuavaCacheBuilderExpireAfterAccess(maximumSize, duration, unit).build(CacheLoader.from(function));
    }

    /**
     * 获取Guava的缓存实例
     *
     * @param <K>         key的泛型
     * @param <V>         值的泛型
     * @param maximumSize 最大的存储条目
     * @param duration    缓存时间
     * @param unit        缓存时间单位
     * @return
     */
    public static <K, V> com.google.common.cache.Cache<K, V> getGuavaCacheExpireAfterWrite(long maximumSize, long duration, TimeUnit unit) {
        return getGuavaCacheBuilderExpireAfterWrite(maximumSize, duration, unit).build();
    }

    /**
     * 获取Guava的缓存实例
     *
     * @param <K>         key的泛型
     * @param <V>         值的泛型
     * @param maximumSize 最大的存储条目
     * @param duration    缓存时间
     * @param unit        缓存时间单位
     * @return
     */
    public static <K, V> com.google.common.cache.Cache<K, V> getGuavaCacheExpireAfterAccess(long maximumSize, long duration, TimeUnit unit) {
        return getGuavaCacheBuilderExpireAfterAccess(maximumSize, duration, unit).build();
    }

    /**
     * 获取Guava的缓存实例
     *
     * @param maximumSize 最大的存储条目
     * @param duration    缓存时间
     * @param unit        缓存时间单位
     * @return
     */
    public static CacheBuilder<Object, Object> getGuavaCacheBuilderExpireAfterWrite(long maximumSize, long duration, TimeUnit unit) {
        final CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().maximumSize(maximumSize);
        return cacheBuilder.expireAfterWrite(duration, unit);
    }

    /**
     * 获取Guava的缓存实例
     *
     * @param maximumSize 最大的存储条目
     * @param duration    缓存时间
     * @param unit        缓存时间单位
     * @return
     */
    public static CacheBuilder<Object, Object> getGuavaCacheBuilderExpireAfterAccess(long maximumSize, long duration, TimeUnit unit) {
        final CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().maximumSize(maximumSize);
        return cacheBuilder.expireAfterAccess(duration, unit);
    }
}
