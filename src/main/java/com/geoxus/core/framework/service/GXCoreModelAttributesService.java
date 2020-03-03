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
    List<Dict> getModelAttributesByModelId(Dict param);

    /**
     * 通过模型Id和属性Id获取模型的属性组
     *
     * @param modelId
     * @param attributeId
     * @return
     */
    Dict getModelAttributeByModelIdAndAttributeId(int modelId, int attributeId);

    /**
     * 检测指定模型中是否包含指定的属性
     *
     * @param coreModelId   核心模型ID
     * @param attributeName 属性名字
     * @return Integer
     */
    Integer checkCoreModelHasAttribute(Integer coreModelId, String attributeName);

    /**
     * 检测指定模型的指定字段是否包含响应的字段(全部匹配相应的字段)
     *
     * @param coreModelId 核心模型ID
     * @param modelField  需要检测的模型字段
     * @param json        需要验证的数据
     * @return boolean
     * @example checkCoreModelFieldAttribute(8, " ext ", JSON)
     */
    boolean checkCoreModelFieldAttributes(Integer coreModelId, String modelField, String json);

    /**
     * 获取指定模型指定字段的默认值列表
     *
     * @param coreModelId         核心模型ID
     * @param modelAttributeField 模型字段名字
     * @param jsonStr             JSON字符串
     * @return Dict
     */
    Dict getModelAttributesDefaultValue(int coreModelId, String modelAttributeField, String jsonStr);
}
