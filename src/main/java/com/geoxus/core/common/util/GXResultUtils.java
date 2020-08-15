package com.geoxus.core.common.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.http.HttpStatus;
import com.geoxus.core.common.vo.GXResultCode;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 返回数据格式
 *
 * @author zj chen <britton@126.com>
 */
public class GXResultUtils extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public GXResultUtils() {
        put("code", 0);
        put("msg", "success");
        put("data", new Object[0]);
    }

    public static GXResultUtils error() {
        return error(HttpStatus.HTTP_INTERNAL_ERROR, "未知异常，请联系管理员");
    }

    public static GXResultUtils error(String msg) {
        return error(HttpStatus.HTTP_INTERNAL_ERROR, msg);
    }

    public static GXResultUtils error(int code) {
        return error(code, "");
    }

    public static GXResultUtils error(int code, String msg) {
        GXResultUtils r = new GXResultUtils();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static GXResultUtils error(GXResultCode resultCode) {
        GXResultUtils r = new GXResultUtils();
        r.put("code", resultCode.getCode());
        r.put("msg", resultCode.getMsg());
        return r;
    }

    public static GXResultUtils ok(GXResultCode resultCode) {
        GXResultUtils r = new GXResultUtils();
        r.put("code", resultCode.getCode());
        r.put("msg", resultCode.getMsg());
        return r;
    }

    public static GXResultUtils ok(String msg) {
        GXResultUtils r = new GXResultUtils();
        r.put("msg", msg);
        return r;
    }

    public static GXResultUtils ok(int code, String msg) {
        GXResultUtils r = new GXResultUtils();
        r.put("msg", msg);
        r.put("code", code);
        return r;
    }

    public static GXResultUtils ok(int code) {
        GXResultUtils r = new GXResultUtils();
        r.put("code", code);
        return r;
    }

    public static GXResultUtils ok(Map<String, Object> map) {
        GXResultUtils r = new GXResultUtils();
        r.putAll(map);
        return r;
    }

    public static GXResultUtils ok() {
        return new GXResultUtils();
    }

    public GXResultUtils put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public GXResultUtils putData(Object obj) {
        super.put("data", obj);
        return this;
    }

    public GXResultUtils addKeyValue(String key, Object value) {
        Object o = get("data");
        Dict data = Dict.create().set(key, value);
        if (o instanceof Map) {
            Dict dict = Convert.convert(Dict.class, o);
            data.putAll(dict);
        }
        super.put("data", data);
        return this;
    }
}
