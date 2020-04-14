package com.geoxus.core.common.listener;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONObject;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.common.event.GXMediaLibraryEvent;
import com.geoxus.core.framework.service.GXCoreMediaLibraryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class GXMediaLibraryListener<T> implements ApplicationListener<GXMediaLibraryEvent<T>> {
    @Autowired
    private GXCoreMediaLibraryService coreMediaLibraryService;

    @Override
    public void onApplicationEvent(@NonNull GXMediaLibraryEvent<T> event) {
        dealData(event);
    }

    public void dealData(GXMediaLibraryEvent<T> event) {
        final Dict param = event.getParam();
        final long coreModelId = param.getInt(GXCommonConstants.CORE_MODEL_PRIMARY_NAME);
        final long objectId = param.getLong("object_id");
        if (objectId > 0) {
            Dict condition = Dict.create();
            if (null != param.getObj("condition")) {
                condition = Convert.convert(Dict.class, param.getObj("condition"));
            }
            coreMediaLibraryService.updateOwner(objectId, coreModelId, Convert.convert(new TypeReference<List<JSONObject>>() {
            }, param.getObj("media")), condition);
        }
    }
}
