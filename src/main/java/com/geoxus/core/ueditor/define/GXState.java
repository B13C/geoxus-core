package com.geoxus.core.ueditor.define;

/**
 * 处理状态接口
 */
public interface GXState {

    boolean isSuccess();

    void putInfo(String name, String val);

    void putInfo(String name, long val);

    String toJSONString();
}
