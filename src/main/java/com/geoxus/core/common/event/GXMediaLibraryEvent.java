package com.geoxus.core.common.event;

import cn.hutool.core.lang.Dict;
import lombok.Data;

@Data
public class GXMediaLibraryEvent<T> {
    private String modelType;
    private T target;
    private Dict param;

    public GXMediaLibraryEvent(String modelType, T target, Dict param) {
        this.modelType = modelType;
        this.target = target;
        this.param = param;
    }
}
