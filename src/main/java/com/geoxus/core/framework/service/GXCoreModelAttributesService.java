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

    /**
     * 检测指定模型中是否包含指定的属性
     *
     * @param coreModelId   核心模型ID
     * @param attributeName 属性名字
     * @return
     */
    Integer checkCoreModelHasAttribute(Integer coreModelId, String attributeName);
}
