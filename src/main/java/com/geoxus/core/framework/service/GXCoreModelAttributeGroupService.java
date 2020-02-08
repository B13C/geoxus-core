package com.geoxus.core.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.geoxus.core.framework.entity.GXCoreModelAttributesEntity;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

import java.util.List;

public interface GXCoreModelAttributeGroupService extends IService<GXCoreModelAttributesEntity> {
    /**
     * 通过模型ID获取模型的属性
     *
     * @param selectStatementProvider
     * @return
     */
    List<GXCoreModelAttributesEntity> getModelAttributeByModelId(SelectStatementProvider selectStatementProvider);

    /**
     * 通过模型Id和属性Id获取模型的属性组
     *
     * @param modelId
     * @param attributeId
     * @return
     */
    GXCoreModelAttributesEntity getAttributeGroupByAttributeIdAndModelId(int modelId, int attributeId);
}
