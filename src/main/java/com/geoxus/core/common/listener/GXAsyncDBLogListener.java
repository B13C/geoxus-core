package com.geoxus.core.common.listener;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.event.GXDBLogEvent;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.framework.service.GXBaseService;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GXAsyncDBLogListener extends GXAsyncBaseListener {
    @Subscribe
    @AllowConcurrentEvents
    public void logInfoToDB(GXDBLogEvent event) {
        final Class<GXBaseService> clazz = event.getGxBaseServiceClazz();
        final GXBaseService service = GXSpringContextUtils.getBean(clazz);
        if (null != service) {
            final Dict logInfo = event.getLogInfo();
            service.logInfoToDB(logInfo);
        }
    }
}
