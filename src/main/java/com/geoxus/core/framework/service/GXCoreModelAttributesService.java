package com.geoxus.core.framework.service;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.framework.entity.GXCoreModelAttributesEntity;

import java.util.List;

public interface GXCoreModelAttributesService extends GXBaseService<GXCoreModelAttributesEntity> {
    /**
     * 通过模型ID获取模型的属性
     *
     * @param param 参数
     * @return
     */
    List<GXCoreModelAttributesEntity> getModelAttributesByModelId(Dict param);

    /**
     * 通过模型Id和属性Id获取模型的属性组
     *
     * @param modelId
     * @param attributeId
     * @return
     */
    GXCoreModelAttributesEntity getModelAttributeByModelIdAndAttributeId(int modelId, int attributeId);
}
