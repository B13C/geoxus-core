package com.geoxus.core.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.geoxus.core.framework.entity.CoreAttributesEntity;

import java.util.List;

public interface GXCoreAttributesService extends IService<CoreAttributesEntity> {
    /**
     * 通过类型获取属性的列表
     *
     * @param category
     * @return
     */
    List<CoreAttributesEntity> getAttributesByCategory(String category);

    /**
     * 通过字段名字获取属性
     *
     * @param fieldName
     * @return
     */
    CoreAttributesEntity getAttributeByFieldName(String fieldName);

    /**
     * 检测字段是否存在
     * true 存在
     * false 不存在
     *
     * @param fieldName
     * @return
     */
    boolean checkFieldIsExists(String fieldName);
}
