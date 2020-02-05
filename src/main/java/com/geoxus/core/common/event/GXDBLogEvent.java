package com.geoxus.core.common.event;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.framework.service.GXBaseService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class GXDBLogEvent extends GXBaseEvent {
    protected Dict logInfo;

    protected Class<GXBaseService> gxBaseServiceClazz;

    public GXDBLogEvent(Class<GXBaseService> gxBaseServiceClazz, Dict logInfo) {
        this.gxBaseServiceClazz = gxBaseServiceClazz;
        this.logInfo = logInfo;
    }
}
