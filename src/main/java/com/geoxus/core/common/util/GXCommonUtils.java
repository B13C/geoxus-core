package com.geoxus.core.common.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.*;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.constant.GXBaseBuilderConstants;
import com.geoxus.core.common.event.GXBaseEvent;
import com.geoxus.core.rpc.config.GXRabbitMQRPCRemoteServersConfig;
import com.geoxus.core.rpc.service.GXRabbitMQRPCClientService;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GXCommonUtils {
    @GXFieldCommentAnnotation(zh = "日志对象")
    private static final Logger LOG = LoggerFactory.getLogger(GXCommonUtils.class);

    @GXFieldCommentAnnotation(zh = "通用线程池")
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);

    @GXFieldCommentAnnotation(zh = "定时任务线程池")
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(5);

    private GXCommonUtils() {
    }

    /**
     * 根据key获取配置文件中的配置信息
     *
     * @param key
     * @param clazzType
     * @param <R>
     * @return
     * @example getEnvironmentValue(" alipay.appId ", String.class)
     */
    public static <R> R getEnvironmentValue(String key, Class<R> clazzType) {
        final R envValue = GXSpringContextUtils.getEnvironment().getProperty(key, clazzType);
        if (null == envValue) {
            return getClassDefaultValue(clazzType);
        }
        return envValue;
    }

    /**
     * 根据key获取配置文件中的配置信息
     *
     * @param key
     * @param clazzType
     * @param defaultValue
     * @param <R>
     * @return
     * @example getEnvironmentValue(" alipay.appId ", String.class, " ")
     */
    public static <R> R getEnvironmentValue(String key, Class<R> clazzType, R defaultValue) {
        final R envValue = GXSpringContextUtils.getEnvironment().getProperty(key, clazzType);
        if (null == envValue) {
            return defaultValue;
        }
        return envValue;
    }

    /**
     * JSON字符串转Dict
     *
     * @param jsonStr
     * @return
     */
    public static Dict jsonToDict(String jsonStr) {
        return jsonToAnyObject(jsonStr, Dict.class);
    }

    /**
     * JSON转换为List<Dict>
     *
     * @param jsonStr
     * @return
     */
    public static List<Dict> jsonToListDict(String jsonStr) {
        if (!JSONUtil.isJson(jsonStr)) {
            LOG.error(StrUtil.format("jsonToDict : {}", "请传递正确的JSON格式的字符串"));
            return Collections.emptyList();
        }
        if (JSONUtil.isJsonArray(jsonStr)) {
            return jsonToAnyObject(jsonStr, new TypeReference<List<Dict>>() {
            });
        }
        return Collections.emptyList();
    }

    /**
     * 获取Class的JVM默认值
     *
     * @param clazzType
     * @param <R>
     * @return
     */
    public static <R> R getClassDefaultValue(Class<R> clazzType) {
        if (ClassUtil.isBasicType(clazzType) && !ClassUtil.isPrimitiveWrapper(clazzType)) {
            return Convert.convert(clazzType, ClassUtil.getDefaultValue(clazzType));
        }
        return ReflectUtil.newInstanceIfPossible(clazzType);
    }

    /**
     * 获取Class的JVM默认值
     *
     * @param clazzType
     * @param <R>
     * @return
     */
    public static <R> R getClassDefaultValue(TypeReference<R> clazzType) {
        final Class<R> aClass = (Class<R>) TypeUtil.getClass(clazzType.getType());
        if (ClassUtil.isBasicType(aClass) && !ClassUtil.isPrimitiveWrapper(aClass)) {
            return Convert.convert(aClass, ClassUtil.getDefaultValue(aClass));
        }
        return ReflectUtil.newInstanceIfPossible(aClass);
    }

    /**
     * 通过路径获取对象中的值
     *
     * @param obj
     * @param key
     * @param clazzType
     * @param <R>
     * @return
     * @example getDataByPath(Dict.create ().set(" aaa ", " bbbb "),"aaa" ,String.class)
     */
    public static <R> R getObjectFieldValueByPath(Object obj, String key, Class<R> clazzType) {
        final JSON parse = JSONUtil.parse(obj);
        final R value = parse.getByPath(key, clazzType);
        if (null == value) {
            return getClassDefaultValue(clazzType);
        }
        return value;
    }

    /**
     * 获取一个接口实现的所有接口
     *
     * @param clazz
     * @param targetList
     * @example getInterfaces(UUserService.class, targetList)
     */
    public static void getInterfaces(Class<?> clazz, List<Class<?>> targetList) {
        for (Class<?> clz : clazz.getInterfaces()) {
            targetList.add(clz);
            if (clz.getInterfaces().length > 0) {
                getInterfaces(clz, targetList);
            }
        }
    }

    /**
     * 处理Class的字段信息
     *
     * @param clz
     * @param data
     */
    public static void clazzFields(Class<?> clz, Dict data) {
        final Field[] fields = ReflectUtil.getFields(clz);
        for (Field field : fields) {
            final GXFieldCommentAnnotation fieldAnnotation = field.getAnnotation(GXFieldCommentAnnotation.class);
            if (null == fieldAnnotation) {
                continue;
            }
            if (fieldAnnotation.show()) {
                final String fieldName = field.getName();
                final boolean fieldShow = fieldAnnotation.show();
                final String fieldComment = fieldAnnotation.value();
                final long fieldCode = fieldAnnotation.code();
                data.putIfAbsent(fieldName, Dict.create().set("code", fieldCode).set("show", fieldShow).set("comment", fieldComment));
            }
        }
    }

    /**
     * 获取当前接口的常量字段信息
     *
     * @return
     */
    public static Dict getConstantsFields(Class<?> clazz) {
        final Dict data = Dict.create();
        final ArrayList<Class<?>> clazzInterfaces = new ArrayList<>();
        clazzInterfaces.add(clazz);
        getInterfaces(clazz, clazzInterfaces);
        for (Class<?> clz : clazzInterfaces) {
            clazzFields(clz, data);
        }
        return data;
    }

    /**
     * 派发同步事件
     *
     * @param event
     */
    public static <T> void postSyncEvent(GXBaseEvent<T> event) {
        GXSyncEventBusCenterUtils.getInstance().post(event);
    }

    /**
     * 派发异步事件
     *
     * @param event
     */
    public static <T> void postAsyncEvent(GXBaseEvent<T> event) {
        GXAsyncEventBusCenterUtils.getInstance().post(event);
    }

    /**
     * 转换Map.toString()到Map
     *
     * @param mapString
     * @return
     */
    public static Map<String, Object> convertStrToMap(String mapString) {
        return Arrays.stream(mapString.replace("{", "").replace("}", "").split(","))
                .map(arrayData -> arrayData.split("="))
                .collect(Collectors.toMap(d -> d[0].trim(), d -> (String) d[1]));
    }

    /**
     * 将实体的某个JSON字段转换为一个Dict
     *
     * @param entity
     * @param mainField
     * @return
     */
    public static Dict entityJSONFieldToDict(Object entity, String mainField) {
        return Convert.convert(Dict.class, Convert.convert(Dict.class, entity).get(mainField));
    }

    /**
     * 将一个新key放入已经存在的json字符串中
     *
     * @param jsonStr
     * @param jsonPath
     * @param object
     * @param override
     * @return
     */
    public static JSONObject putDataToJSONStr(String jsonStr, String jsonPath, Object object, boolean override) {
        if (!JSONUtil.isJsonObj(jsonStr)) {
            return new JSONObject();
        }
        final JSONObject jsonObject = new JSONObject(jsonStr);
        if (override) {
            jsonObject.putByPath(jsonPath, object);
        } else {
            jsonObject.putIfAbsent(jsonPath, object);
        }
        return jsonObject;
    }

    /**
     * 将一个新key放入已经存在的json字符串中
     *
     * @param jsonObject
     * @param jsonPath
     * @param object
     * @param override
     * @return
     */
    public static JSONObject putDataToJSONStr(JSONObject jsonObject, String jsonPath, Object object, boolean override) {
        if (override) {
            jsonObject.putByPath(jsonPath, object);
        } else {
            jsonObject.putIfAbsent(jsonPath, object);
        }
        return jsonObject;
    }

    /**
     * 将json字符串转换为任意对象
     *
     * @param jsonStr
     * @param clazz
     * @param <R>
     * @return
     * @throws JsonProcessingException
     */
    public static <R> R jsonToAnyObject(String jsonStr, Class<R> clazz) {
        if (!JSONUtil.isJson(jsonStr)) {
            LOG.error("不合法的JSON字符串");
            return getClassDefaultValue(clazz);
        }
        final ObjectMapper objectMapper = GXSpringContextUtils.getBean(ObjectMapper.class);
        try {
            return objectMapper.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage(), e);
        }
        return getClassDefaultValue(clazz);
    }

    /**
     * 将json字符串转换为任意对象
     *
     * @param jsonStr
     * @param reference
     * @param <R>
     * @return
     * @throws JsonProcessingException
     */
    public static <R> R jsonToAnyObject(String jsonStr, TypeReference<R> reference) {
        if (!JSONUtil.isJson(jsonStr)) {
            LOG.error("不合法的JSON字符串");
            return getClassDefaultValue(reference);
        }
        final ObjectMapper objectMapper = GXSpringContextUtils.getBean(ObjectMapper.class);
        try {
            return objectMapper.readValue(jsonStr, reference);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage(), e);
        }
        return getClassDefaultValue(reference);
    }

    /**
     * 获取json字符串中的任意一个key的数据
     * <pre>
     *     {@code
     *     System.out.println(GXCommonUtils.getJSONValueByAnyPath(s, "data.data1.data2.name", String.class));
     *     System.out.println(GXCommonUtils.getJSONValueByAnyPath(s, "data.data1.data2", Dict.class));
     *     System.out.println(GXCommonUtils.getJSONValueByAnyPath(s, "data.data1", Dict.class));
     *     System.out.println(GXCommonUtils.getJSONValueByAnyPath(s, "data.data1.data2.name.kkkk", String.class));
     *     System.out.println(GXCommonUtils.getJSONValueByAnyPath(s, "data", Dict.class));
     *     }
     * </pre>
     *
     * @param jsonStr
     * @param path
     * @param clazz
     * @param <R>
     * @return
     */
    public static <R> R getJSONValueByAnyPath(String jsonStr, String path, Class<R> clazz) {
        if (!JSONUtil.isJson(jsonStr)) {
            LOG.error("不合法的JSON字符串");
            return getClassDefaultValue(clazz);
        }
        final ObjectMapper objectMapper = GXSpringContextUtils.getBean(ObjectMapper.class);
        try {
            Dict data = objectMapper.readValue(jsonStr, Dict.class);
            String[] paths = StrUtil.split(path, ".");
            final Object firstObj = data.getObj(paths[0]);
            if (null == firstObj) {
                return getClassDefaultValue(clazz);
            }
            Dict tempDict = Convert.convert(Dict.class, firstObj);
            int length = paths.length;
            if (length < 2) {
                return Convert.convert(clazz, tempDict);
            }
            for (int i = 1; i < length - 1; i++) {
                final Object obj = tempDict.getObj(paths[i]);
                if (null == obj) {
                    break;
                }
                if (obj instanceof Map) {
                    tempDict = Convert.convert(Dict.class, obj);
                }
            }
            if (!tempDict.isEmpty()) {
                final Object obj = tempDict.getObj(paths[length - 1]);
                if (null != obj) {
                    return Convert.convert(clazz, obj);
                }
            }
        } catch (Exception e) {
            LOG.error("从JSON字符串中获取数据出错 , 错误信息 : {}", e.getMessage());
            return getClassDefaultValue(clazz);
        }
        return getClassDefaultValue(clazz);
    }

    /**
     * 获取json字符串中的任意一个key的数据
     * <pre>
     *     {@code
     *      System.out.println(GXCommonUtils.getJSONValueByAnyPath(s, "data.data1.data2.name", new TypeReference<String>() {
     *      }));
     *     }
     * </pre>
     *
     * @param jsonStr
     * @param path
     * @param reference
     * @param <R>
     * @return
     */
    public static <R> R getJSONValueByAnyPath(String jsonStr, String path, TypeReference<R> reference) {
        return getJSONValueByAnyPath(jsonStr, path, (Class<R>) reference.getType());
    }

    /**
     * 移除JSON中任意路径的值
     *
     * @param jsonStr
     * @param path
     * @param clazz
     * @param <R>
     * @return
     */
    public static <R> R removeJSONStrAnyPath(String jsonStr, String path, Class<R> clazz) {
        final JSONObject parse = JSONUtil.parseObj(jsonStr);
        int index = StrUtil.indexOf(path, '.');
        if (index != -1) {
            String mainPath = StrUtil.sub(path, 0, StrUtil.lastIndexOfIgnoreCase(path, "."));
            String subPath = StrUtil.sub(path, StrUtil.lastIndexOfIgnoreCase(path, ".") + 1, path.length());
            final Object o = parse.get(mainPath);
            if (null != o) {
                if (JSONUtil.isJsonArray(o.toString()) && NumberUtil.isInteger(subPath)) {
                    final int delIndex = Integer.parseInt(subPath);
                    if (null != parse.getByPath(mainPath, JSONArray.class)) {
                        parse.getByPath(mainPath, JSONArray.class).remove(delIndex);
                    }
                } else if (null != parse.getByPath(mainPath, JSONObject.class)) {
                    final Object remove = parse.getByPath(mainPath, JSONObject.class).remove(subPath);
                    return Convert.convert(clazz, remove);
                }
            }
            return getClassDefaultValue(clazz);
        }
        return Convert.convert(clazz, parse.remove(path));
    }


    /**
     * 移除JSON中任意路径的值
     *
     * @param jsonStr
     * @param path
     * @return
     */
    public static void removeJSONStrAnyPath(String jsonStr, String path) {
        final JSONObject parse = JSONUtil.parseObj(jsonStr);
        int index = StrUtil.indexOf(path, '.');
        if (index != -1) {
            String mainPath = StrUtil.sub(path, 0, StrUtil.lastIndexOfIgnoreCase(path, "."));
            String subPath = StrUtil.sub(path, StrUtil.lastIndexOfIgnoreCase(path, ".") + 1, path.length());
            final Object o = parse.get(mainPath);
            if (null != o) {
                if (JSONUtil.isJsonArray(o.toString()) && NumberUtil.isInteger(subPath)) {
                    final int delIndex = Integer.parseInt(subPath);
                    if (null != parse.getByPath(mainPath, JSONArray.class)) {
                        parse.getByPath(mainPath, JSONArray.class).remove(delIndex);
                    }
                } else if (null != parse.getByPath(mainPath, JSONObject.class)) {
                    parse.getByPath(mainPath, JSONObject.class).remove(subPath);
                }
            }
            return;
        }
        parse.remove(path);
    }

    /**
     * 移除JSON中任意路径的值
     *
     * @param parse
     * @param path
     * @param clazz
     * @param <R>
     * @return
     */
    public static <R> R removeJSONObjectAnyPath(JSONObject parse, String path, Class<R> clazz) {
        int index = StrUtil.indexOf(path, '.');
        if (index != -1) {
            String mainPath = StrUtil.sub(path, 0, StrUtil.lastIndexOfIgnoreCase(path, "."));
            String subPath = StrUtil.sub(path, StrUtil.lastIndexOfIgnoreCase(path, ".") + 1, path.length());
            final Object o = parse.getByPath(mainPath);
            if (null != o) {
                if (JSONUtil.isJsonArray(o.toString()) && NumberUtil.isInteger(subPath)) {
                    final int delIndex = Integer.parseInt(subPath);
                    if (null != parse.getByPath(mainPath, JSONArray.class)) {
                        parse.getByPath(mainPath, JSONArray.class).remove(delIndex);
                    }
                } else if (null != parse.getByPath(mainPath, JSONObject.class)) {
                    final Object remove = parse.getByPath(mainPath, JSONObject.class).remove(subPath);
                    return Convert.convert(clazz, remove);
                }
            }
            return getClassDefaultValue(clazz);
        }
        return Convert.convert(clazz, parse.remove(path));
    }

    /**
     * 移除JSON中任意路径的值
     *
     * @param parse
     * @param path
     * @return
     */
    public static void removeJSONObjectAnyPath(JSONObject parse, String path) {
        int index = StrUtil.indexOf(path, '.');
        if (index != -1) {
            String mainPath = StrUtil.sub(path, 0, StrUtil.lastIndexOfIgnoreCase(path, "."));
            String subPath = StrUtil.sub(path, StrUtil.lastIndexOfIgnoreCase(path, ".") + 1, path.length());
            final Object o = parse.getByPath(mainPath);
            if (null != o) {
                if (JSONUtil.isJsonArray(o.toString()) && NumberUtil.isInteger(subPath)) {
                    final int delIndex = Integer.parseInt(subPath);
                    if (null != parse.getByPath(mainPath, JSONArray.class)) {
                        parse.getByPath(mainPath, JSONArray.class).remove(delIndex);
                    }
                } else if (null != parse.getByPath(mainPath, JSONObject.class)) {
                    parse.getByPath(mainPath, JSONObject.class).remove(subPath);
                }
            }
            return;
        }
        parse.remove(path);
    }

    /**
     * 获取通用线程池
     *
     * @return
     */
    public static ExecutorService getFixedThreadPool() {
        return EXECUTOR_SERVICE;
    }

    /**
     * 获取定时任务线程池
     *
     * @return
     */
    public static ScheduledExecutorService getScheduledExecutorService() {
        return SCHEDULED_EXECUTOR_SERVICE;
    }

    /**
     * 获取RPC远程服务的名字
     *
     * @param serverName
     * @param key
     * @return
     */
    public static String getRemoteRPCServerValueByKey(String serverName, String key) {
        final GXRabbitMQRPCRemoteServersConfig serverConfigBean = GXSpringContextUtils.getBean(GXRabbitMQRPCRemoteServersConfig.class);
        final Map<String, Map<String, Object>> servers = serverConfigBean.getServers();
        final Map<String, Object> server = servers.get(serverName);
        if (null == server) {
            GXRabbitMQRPCClientService.LOG.error("{} 远程RPC服务不存在", serverName);
            return "";
        }
        if (null != server.get(key)) {
            return (String) server.get(key);
        }
        return "";
    }

    /**
     * 获取Spring的Cache实例
     *
     * @param cacheName cache的名字
     * @return
     */
    public static Cache getSpringCacheManager(String cacheName) {
        final CacheManager cacheManager = GXSpringContextUtils.getBean(CacheManager.class);
        return cacheManager.getCache(cacheName);
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
     * @param fixedExpire 是否固定过期时间
     * @return
     */
    public static <K, V> LoadingCache<K, V> getGuavaCache(long maximumSize, long duration, TimeUnit unit, Supplier<V> supplier, boolean fixedExpire) {
        final CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().maximumSize(maximumSize);
        if (fixedExpire) {
            cacheBuilder.expireAfterWrite(duration, unit);
        } else {
            cacheBuilder.expireAfterAccess(duration, unit);
        }
        return cacheBuilder.build(CacheLoader.from(supplier));
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
     * @param fixedExpire 是否固定过期时间
     * @return
     */
    public static <K, V> LoadingCache<K, V> getGuavaCache(long maximumSize, long duration, TimeUnit unit, Function<K, V> function, boolean fixedExpire) {
        final CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().maximumSize(maximumSize);
        if (fixedExpire) {
            cacheBuilder.expireAfterWrite(duration, unit);
        } else {
            cacheBuilder.expireAfterAccess(duration, unit);
        }
        return cacheBuilder.build(CacheLoader.from(function));
    }

    /**
     * 给现有查询条件新增查询条件
     *
     * @param requestParam       请求参数
     * @param key                添加的key
     * @param value              添加的value
     * @param returnRequestParam 是否返回requestParam
     * @return
     */
    public static Dict addConditionToSearchCondition(Dict requestParam, String key, Object value, boolean returnRequestParam) {
        final Object obj = requestParam.getObj(GXBaseBuilderConstants.SEARCH_CONDITION_NAME);
        if (null == obj) {
            return requestParam;
        }
        final Dict data = Convert.convert(Dict.class, obj);
        data.set(key, value);
        if (returnRequestParam) {
            requestParam.put(GXBaseBuilderConstants.SEARCH_CONDITION_NAME, data);
            return requestParam;
        }
        return data;
    }


    /**
     * 给现有查询条件新增查询条件
     *
     * @param requestParam       请求参数
     * @param sourceData         需要添加的map
     * @param returnRequestParam 是否返回requestParam
     * @return
     */
    public static Dict addConditionToSearchCondition(Dict requestParam, Dict sourceData, boolean returnRequestParam) {
        final Object obj = requestParam.getObj(GXBaseBuilderConstants.SEARCH_CONDITION_NAME);
        if (null == obj) {
            return requestParam;
        }
        final Dict data = Convert.convert(Dict.class, obj);
        data.putAll(sourceData);
        if (returnRequestParam) {
            requestParam.put(GXBaseBuilderConstants.SEARCH_CONDITION_NAME, data);
            return requestParam;
        }
        return data;
    }
}
