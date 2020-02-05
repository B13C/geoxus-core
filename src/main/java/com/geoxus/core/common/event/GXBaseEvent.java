package com.geoxus.core.common.event;

import cn.hutool.core.lang.Dict;
import lombok.Data;

@Data
public class GXBaseEvent<T> {
    protected String eventName;

    protected Dict param;

    protected T targetEntity;
}
