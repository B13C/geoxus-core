package com.geoxus.core.framework.service;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.validator.GXValidateDBExists;
import com.geoxus.core.framework.entity.GXCoreModelEntity;

import java.util.Set;

public interface GXCoreModelService extends IService<GXCoreModelEntity>, GXValidateDBExists {
    /**
     * 通过模型ID获取模型的相关信息
     *
     * @param modelId
     * @return
     */
    GXCoreModelEntity getModelDetailByModelId(int modelId, String subField);

    /**
     * 检测模型是否拥有制定的字段
     *
     * @param modelId
     * @param field
     * @return
     */
    boolean checkModelIsHasField(int modelId, String field);

    /**
     * 检测表单提交的key是否与模型的key匹配
     *
     * @param keySet
     * @param modelName
     * @return
     */
    boolean checkFormKeyMatch(Set<String> keySet, String modelName) throws GXException;

    /**
     * 通过模型名字获取模型ID
     *
     * @param modelName
     * @return
     */
    int getModelIdByModelIdentification(String modelName);

    /**
     * 通过modelId获取命名空间
     *
     * @param coreModelId
     * @param defaultValue
     * @return
     */
    String getModelTypeByModelId(long coreModelId, String defaultValue);

    /**
     * 获取模型配置的搜索条件
     *
     * @param condition
     * @return
     */
    Dict getSearchCondition(Dict condition);
}
