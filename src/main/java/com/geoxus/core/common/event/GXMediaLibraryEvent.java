package com.geoxus.core.common.event;

import cn.hutool.core.lang.Dict;
import lombok.Data;

@Data
public class GXMediaLibraryEvent<T> {
    private T target;

    private Dict param;

    public GXMediaLibraryEvent(T target, Dict param) {
        this.target = target;
        this.param = param;
    }
}
