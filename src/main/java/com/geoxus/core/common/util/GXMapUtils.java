package com.geoxus.core.common.util;

import cn.hutool.core.map.MapUtil;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Map;

/**
 * Map工具类
 */
public class GXMapUtils extends MapUtil {
    /**
     * 将map转换为指定的对象
     *
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = beanClass.newInstance();
        BeanUtils.populate(obj, map);
        return obj;
    }

    /**
     * 将指定的对象兑换为map
     *
     * @param obj
     * @return
     */
    public static Map objectToMap(Object obj) {
        if (null == obj) {
            return null;
        }
        return new org.apache.commons.beanutils.BeanMap(obj);
    }
}
