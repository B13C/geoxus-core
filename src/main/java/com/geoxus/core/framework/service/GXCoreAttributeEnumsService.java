package com.geoxus.core.framework.service;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import com.geoxus.core.common.validator.GXValidateDBExists;
import com.geoxus.core.framework.entity.GXCoreAttributesEnumsEntity;

import java.util.List;

public interface GXCoreAttributeEnumsService extends IService<GXCoreAttributesEnumsEntity>, GXValidateDBExists {
    /**
     * 检测属性的值是否存在
     *
     * @param attributeId
     * @param coreModelId
     * @param value
     * @return
     */
    boolean isExistsAttributeValue(int attributeId, Object value, int coreModelId);

    /**
     * 检测属性是否存在
     *
     * @param attributeId
     * @param coreModelId
     * @return
     */
    boolean isExistsAttribute(int attributeId, int coreModelId);

    /**
     * 根据属性名字获取属性的枚举列表
     *
     * @param condition
     * @return
     */
    List<Dict> getAttributeEnumsByCondition(Dict condition);
}
