package com.geoxus.core.common.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.*;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.constant.GXBaseBuilderConstants;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.common.event.GXBaseEvent;
import com.geoxus.core.rpc.config.GXRabbitMQRPCRemoteServersConfig;
import com.geoxus.core.rpc.service.GXRabbitMQRPCClientService;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
     * <pre>
     *     {@code
     *     getEnvironmentValue(" alipay.appId ", String.class)
     *     }
     * </pre>
     *
     * @param key       key
     * @param clazzType 返回类型
     * @param <R>       泛型类型
     * @return <R>
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
     * <pre>
     *     {@code
     *     getEnvironmentValue(" alipay.appId ", String.class, " ")
     *     }
     *     </pre>
     *
     * @param key          KEY
     * @param clazzType    返回类型
     * @param defaultValue 泛型类型
     * @param <R>          <R>
     * @return <R>
     */
    public static <R> R getEnvironmentValue(String key, Class<R> clazzType, R defaultValue) {
        final R envValue = GXSpringContextUtils.getEnvironment().getProperty(key, clazzType);
        if (null == envValue) {
            return defaultValue;
        }
        return envValue;
    }

    /**
     * 根据key获取配置文件中的配置信息
     *
     * @param key Key
     * @return String
     */
    public static String getEnvironmentValue(String key) {
        final String property = GXSpringContextUtils.getEnvironment().getProperty(key);
        if (null == property) {
            return "";
        }
        return property;
    }

    /**
     * JSON字符串转Dict
     *
     * @param jsonStr JSON字符串
     * @return Dict
     */
    public static Dict jsonToDict(String jsonStr) {
        return jsonToAnyObject(jsonStr, Dict.class);
    }

    /**
     * JSON转换为List<Dict>
     *
     * @param jsonStr JSON字符串
     * @return List
     */
    public static List<Dict> jsonToListDict(String jsonStr) {
        if (!JSONUtil.isJson(jsonStr)) {
            LOG.error("jsonToDict : {}", "请传递正确的JSON格式的字符串");
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
     * @param clazzType Class 对象
     * @param <R>       R
     * @return R
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
     * @param clazzType Class 对象
     * @param <R>       R
     * @return R
     */
    public static <R> R getClassDefaultValue(Type clazzType) {
        Class<?> aClass = TypeUtil.getClass(clazzType);
        Object o = ReflectUtil.newInstanceIfPossible(aClass);
        return Convert.convert(clazzType, o);
    }

    /**
     * 获取Class的JVM默认值
     *
     * @param clazzType Class 对象
     * @param <R>       R
     * @return R
     */
    public static <R> R getClassDefaultValue(TypeReference<R> clazzType) {
        Class<?> aClass = TypeUtil.getClass(clazzType.getType());
        Object o = ReflectUtil.newInstanceIfPossible(aClass);
        return Convert.convert(clazzType.getType(), o);
    }

    /**
     * 获取Class的JVM默认值
     *
     * @param clazzType Class 对象
     * @param <R>       R
     * @return R
     */
    public static <R> R getClassDefaultValue(TypeReference<R> clazzType, R defaultValue) {
        R classDefaultValue = getClassDefaultValue(clazzType);
        if (Objects.isNull(classDefaultValue)) {
            return defaultValue;
        }
        return classDefaultValue;
    }

    /**
     * 通过路径获取对象中的值
     * <pre>{@code
     *     getDataByPath(Dict.create ().set(" aaa ", " bbbb "),"aaa" ,String.class)
     *     }
     * </pre>
     *
     * @param obj       对象
     * @param key       键
     * @param clazzType Class对象
     * @param <R>       R
     * @return R
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
     * <pre>{@code
     * getInterfaces(UUserService.class, targetList)
     * }
     * </pre>
     *
     * @param clazz      Class 对象
     * @param targetList 目标Class列表
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
     * @param clz  Class 对象
     * @param data 数据
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
     * @param clazz Class对象
     * @return Dict
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
     * 派发事件
     *
     * @param event 事件对象
     */
    public static <T> void publishEvent(GXBaseEvent<T> event) {
        GXSpringContextUtils.getApplicationContext().publishEvent(event);
    }

    /**
     * 转换Map.toString()到Map
     *
     * @param mapString map的String表示
     * @return Map
     */
    public static Map<String, Object> convertStrToMap(String mapString) {
        return Arrays.stream(mapString.replace("{", "").replace("}", "").split(","))
                .map(arrayData -> arrayData.split("="))
                .collect(Collectors.toMap(d -> d[0].trim(), d -> d[1]));
    }

    /**
     * 将实体的某个JSON字段转换为一个Dict
     *
     * @param entity    实体对象
     * @param mainField 主字段 (数据库字段)
     * @return Dict
     */
    public static Dict entityJSONFieldToDict(Object entity, String mainField) {
        return Convert.convert(Dict.class, Convert.convert(Dict.class, entity).get(mainField));
    }

    /**
     * 将一个新key放入已经存在的json字符串中
     *
     * @param jsonStr  JSON字符串
     * @param jsonPath JSON路径
     * @param object   Object
     * @param override 是否复写已经存在的值
     * @return JSONObject
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
     * @param jsonObject JSON对象
     * @param jsonPath   JSON路径
     * @param object     Object
     * @param override   是否复写已经存在的值
     * @return JSONObject
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
     * @param jsonStr JSON字符串
     * @param clazz   Class 对象
     * @param <R>     R
     * @return R
     */
    public static <R> R jsonToAnyObject(String jsonStr, Class<R> clazz) {
        if (!JSONUtil.isJson(jsonStr)) {
            LOG.error("不合法的JSON字符串 : {}", jsonStr);
            return getClassDefaultValue(clazz);
        }
        final ObjectMapper objectMapper = GXSpringContextUtils.getBean(ObjectMapper.class);
        try {
            assert objectMapper != null;
            return objectMapper.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage(), e);
        }
        return getClassDefaultValue(clazz);
    }

    /**
     * 将json字符串转换为任意对象
     *
     * @param jsonStr   JSON字符串
     * @param reference 需要转换的到类型
     * @param <R>       R
     * @return R
     */
    public static <R> R jsonToAnyObject(String jsonStr, TypeReference<R> reference) {
        if (!JSONUtil.isJson(jsonStr)) {
            LOG.error("不合法的JSON字符串");
            return getClassDefaultValue(reference);
        }
        final ObjectMapper objectMapper = GXSpringContextUtils.getBean(ObjectMapper.class);
        try {
            assert objectMapper != null;
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
     * @param jsonStr JSON字符串
     * @param path    路径
     * @param clazz   Class 对象
     * @param <R>     R
     * @return R
     */
    public static <R> R getJSONValueByAnyPath(String jsonStr, String path, Class<R> clazz) {
        if (!JSONUtil.isJson(jsonStr)) {
            LOG.error("不合法的JSON字符串");
            return getClassDefaultValue(clazz);
        }
        final ObjectMapper objectMapper = GXSpringContextUtils.getBean(ObjectMapper.class);
        try {
            assert objectMapper != null;
            Dict data = objectMapper.readValue(jsonStr, Dict.class);
            String[] paths = StrUtil.split(path, ".");
            final Object firstObj = data.getObj(paths[0]);
            if (null == firstObj) {
                return getClassDefaultValue(clazz);
            }
            if (!(firstObj instanceof Map)) {
                return Convert.convert(clazz, firstObj);
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
                if (Objects.nonNull(obj)) {
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
     * @param jsonStr   JSON字符串
     * @param path      路径
     * @param reference 需要转换到的类型
     * @param <R>       泛型类型
     * @return R
     */
    @SuppressWarnings("unchecked")
    public static <R> R getJSONValueByAnyPath(String jsonStr, String path, TypeReference<R> reference) {
        return (R) getJSONValueByAnyPath(jsonStr, path, TypeUtil.getClass(reference.getType()));
    }

    /**
     * 移除JSON中任意路径的值
     *
     * @param jsonStr JSON字符串
     * @param path    路径
     * @param clazz   Type 对象
     * @param <R>     泛型类型
     * @return R
     */
    public static <R> R removeJSONStrAnyPath(String jsonStr, String path, Class<R> clazz) {
        final JSONObject parse = JSONUtil.parseObj(jsonStr);
        int index = StrUtil.indexOf(path, '.');
        if (index != -1) {
            String mainPath = StrUtil.sub(path, 0, StrUtil.lastIndexOfIgnoreCase(path, "."));
            String subPath = StrUtil.sub(path, StrUtil.lastIndexOfIgnoreCase(path, ".") + 1, path.length());
            final Object o = parse.get(mainPath);
            if (Objects.nonNull(o)) {
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
     * @param jsonStr JSON字符串
     * @param path    路径
     */
    public static void removeJSONStrAnyPath(String jsonStr, String path) {
        final JSONObject parse = JSONUtil.parseObj(jsonStr);
        int index = StrUtil.indexOf(path, '.');
        if (index != -1) {
            String mainPath = StrUtil.sub(path, 0, StrUtil.lastIndexOfIgnoreCase(path, "."));
            String subPath = StrUtil.sub(path, StrUtil.lastIndexOfIgnoreCase(path, ".") + 1, path.length());
            final Object o = parse.get(mainPath);
            if (Objects.nonNull(o)) {
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
     * @param parse JSON对象
     * @param path  路径
     * @param clazz Type对象
     * @param <R>   泛型
     * @return R
     */
    public static <R> R removeJSONObjectAnyPath(JSONObject parse, String path, Class<R> clazz) {
        int index = StrUtil.indexOf(path, '.');
        if (index != -1) {
            String mainPath = StrUtil.sub(path, 0, StrUtil.lastIndexOfIgnoreCase(path, "."));
            String subPath = StrUtil.sub(path, StrUtil.lastIndexOfIgnoreCase(path, ".") + 1, path.length());
            final Object o = parse.getByPath(mainPath);
            if (Objects.nonNull(o)) {
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
     * @param parse JSON对象
     * @param path  路径
     */
    public static void removeJSONObjectAnyPath(JSONObject parse, String path) {
        int index = StrUtil.indexOf(path, '.');
        if (index != -1) {
            String mainPath = StrUtil.sub(path, 0, StrUtil.lastIndexOfIgnoreCase(path, "."));
            String subPath = StrUtil.sub(path, StrUtil.lastIndexOfIgnoreCase(path, ".") + 1, path.length());
            final Object o = parse.getByPath(mainPath);
            if (Objects.nonNull(o)) {
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
     * @return ExecutorService
     */
    public static ExecutorService getFixedThreadPool() {
        return EXECUTOR_SERVICE;
    }

    /**
     * 获取定时任务线程池
     *
     * @return ScheduledExecutorService
     */
    public static ScheduledExecutorService getScheduledExecutorService() {
        return SCHEDULED_EXECUTOR_SERVICE;
    }

    /**
     * 获取RPC远程服务的名字
     *
     * @param serverName 服务器名字
     * @param key        key
     * @return String
     */
    public static String getRemoteRPCServerValueByKey(String serverName, String key) {
        final GXRabbitMQRPCRemoteServersConfig serverConfigBean = GXSpringContextUtils.getBean(GXRabbitMQRPCRemoteServersConfig.class);
        assert serverConfigBean != null;
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
     * @return Cache
     */
    public static Cache getSpringCache(String cacheName) {
        final CacheManager cacheManager = GXSpringContextUtils.getBean(CacheManager.class);
        assert cacheManager != null;
        return cacheManager.getCache(cacheName);
    }

    /**
     * 获取EhCacheCacheManager的实例
     *
     * @return EhCacheCacheManager
     */
    public static EhCacheCacheManager getEhCacheCacheManager() {
        EhCacheCacheManager ehCacheCacheManager = GXSpringContextUtils.getBean(EhCacheCacheManager.class);
        if (null != ehCacheCacheManager) {
            return ehCacheCacheManager;
        }
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        ehCacheManagerFactoryBean.setShared(true);
        net.sf.ehcache.CacheManager cacheManager = ehCacheManagerFactoryBean.getObject();
        if (null == cacheManager) {
            cacheManager = new net.sf.ehcache.CacheManager();
        }
        return new EhCacheCacheManager(cacheManager);
    }

    /**
     * 获取RedissonSpringCacheManager
     *
     * @return RedissonSpringCacheManager
     */
    public static RedissonSpringCacheManager getRedissonCacheManager() {
        final CacheManager cacheManager = GXSpringContextUtils.getBean(CacheManager.class);
        if (cacheManager instanceof RedissonSpringCacheManager) {
            return (RedissonSpringCacheManager) cacheManager;
        }
        try {
            URL resource = GXCommonUtils.class.getClassLoader().getResource("redisson.yml");
            Config config = Config.fromYAML(resource);
            RedissonClient redissonClient = Redisson.create(config);
            return new RedissonSpringCacheManager(redissonClient, "classpath:/redisson-cache-config.yml");
        } catch (IOException e) {
            getLogger(GXCommonUtils.class).error("读取redisson.yml文件失败...");
        }
        return null;
    }

    /**
     * 给现有查询条件新增查询条件
     *
     * @param requestParam       请求参数
     * @param key                添加的key
     * @param value              添加的value
     * @param returnRequestParam 是否返回requestParam
     * @return Dict
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
     * @return Dict
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

    /**
     * 获取当前登录用户的ID
     * 0  : 表示普通用户
     * >0 : 确定管理员
     *
     * @return Long
     */
    public static long getCurrentSessionUserId() {
        try {
            return GXShiroUtils.getAdminId();
        } catch (Exception e) {
            GXCommonUtils.getLogger(GXCommonUtils.class).error(e.getMessage(), e);
        }
        return 0;
    }

    /**
     * 获取实体的表名字
     *
     * @param clazz Class
     * @return String
     */
    public static String getTableName(Class<?> clazz) {
        final TableName annotation = AnnotationUtil.getAnnotation(clazz, TableName.class);
        if (null != annotation) {
            return annotation.value();
        }
        return "";
    }

    /**
     * 隐藏手机号码的指定几位为指定的字符
     *
     * @param phoneNumber  手机号码
     * @param startInclude 开始字符位置(包含,从0开始)
     * @param endExclude   结束字符位置
     * @param replacedChar 替换为的字符
     * @return String
     */
    public static String hiddenPhoneNumber(CharSequence phoneNumber, int startInclude, int endExclude, char replacedChar) {
        if (Validator.isMobile(phoneNumber)) {
            return StrUtil.replace(phoneNumber, startInclude, endExclude, replacedChar);
        }
        return "";
    }

    /**
     * 加密手机号码
     *
     * @param phoneNumber 明文手机号
     * @return String
     */
    public static String encryptedPhoneNumber(String phoneNumber) {
        final String prefix = GXCommonUtils.getEnvironmentValue("encrypted.phone.prefix", String.class);
        final String suffix = GXCommonUtils.getEnvironmentValue("encrypted.phone.suffix", String.class);
        final String key = prefix + GXCommonConstants.PHONE_ENCRYPT_KEY + suffix;
        return encryptedPhoneNumber(phoneNumber, key);
    }

    /**
     * 解密手机号码
     *
     * @param encryptPhoneNumber 加密手机号
     * @return String
     */
    public static String decryptedPhoneNumber(String encryptPhoneNumber) {
        final String prefix = GXCommonUtils.getEnvironmentValue("encrypted.phone.prefix", String.class);
        final String suffix = GXCommonUtils.getEnvironmentValue("encrypted.phone.suffix", String.class);
        final String key = prefix + GXCommonConstants.PHONE_ENCRYPT_KEY + suffix;
        return decryptedPhoneNumber(encryptPhoneNumber, key);
    }

    /**
     * 加密手机号码
     *
     * @param phoneNumber 明文手机号
     * @param key         加密KEY
     * @return String
     */
    public static String encryptedPhoneNumber(String phoneNumber, String key) {
        return GXAuthCodeUtils.authCodeEncode(phoneNumber, key);
    }

    /**
     * 解密手机号码
     *
     * @param encryptPhoneNumber 加密手机号
     * @param key                解密KEY
     * @return String
     */
    public static String decryptedPhoneNumber(String encryptPhoneNumber, String key) {
        return GXAuthCodeUtils.authCodeDecode(encryptPhoneNumber, key);
    }

    /**
     * 获取Logger对象
     *
     * @param clazz Class
     * @return Logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 获取指定缓存中的值
     *
     * @param cacheName 缓存名字
     * @param key       缓存key
     * @param clazz     返回值的类型
     * @param <R>       泛型的类型
     * @return R
     */
    public static <R> R getSpringCacheValue(String cacheName, String key, Class<R> clazz) {
        Cache cache = getSpringCache(cacheName);
        return cache.get(key, clazz);
    }

    /**
     * 获取指定缓存中的值
     *
     * @param cacheName    缓存名字
     * @param key          缓存key
     * @param clazz        返回值的类型
     * @param <R>          泛型的类型
     * @param defaultValue 默认值
     * @return R
     */
    public static <R> R getSpringCacheValue(String cacheName, String key, Class<R> clazz, R defaultValue) {
        Cache cache = getSpringCache(cacheName);
        if (cache.get(key) == null) {
            return defaultValue;
        }
        return getSpringCacheValue(cacheName, key, clazz);
    }

    /**
     * 设置缓存的值
     *
     * @param cacheName 缓存的名字
     * @param key       缓存的键
     * @param value     缓存的值
     */
    public static void setSpringCacheValue(String cacheName, String key, Object value) {
        Cache springCache = getSpringCache(cacheName);
        springCache.put(key, value);
    }
}
