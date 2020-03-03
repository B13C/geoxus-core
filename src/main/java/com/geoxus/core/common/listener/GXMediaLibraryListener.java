package com.geoxus.core.common.listener;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONObject;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.common.event.GXMediaLibraryEvent;
import com.geoxus.core.framework.service.GXCoreMediaLibraryService;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@SuppressWarnings("unused")
public class GXMediaLibraryListener extends GXSyncBaseListener {
    @Autowired
    private GXCoreMediaLibraryService coreMediaLibraryService;

    @Subscribe
    @AllowConcurrentEvents
    public void updateMediaOwner(GXMediaLibraryEvent<?> event) {
        final Dict param = event.getParam();
        final Object o = event.getTarget();
        final long coreModelId = Convert.convert(Long.class, Optional.ofNullable(param.getInt(GXCommonConstants.CORE_MODEL_PRIMARY_NAME))
                .orElse(ReflectUtil.invoke(o, "getCoreModelId")));
        final long modelId = Optional.ofNullable(param.getLong("model_id")).orElse(0L);
        if (modelId > 0) {
            coreMediaLibraryService.updateOwner(modelId, coreModelId, Convert.convert(new TypeReference<List<JSONObject>>() {
            }, param.getObj("media")));
        }
    }
}
