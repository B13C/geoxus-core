package com.geoxus.core.common.lang;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;

public class GXDict extends Dict {
    /**
     * 获取指定Map中的JSON字段的值
     *
     * @param key 字段名字
     * @return JSON字符串
     */
    public String getJSONStr(String key) {
        Object obj = getObj(key);
        if (null == obj) {
            return "{}";
        }
        return JSONUtil.toJsonStr(obj);
    }
}
