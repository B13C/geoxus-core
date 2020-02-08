package com.geoxus.core.framework.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.constant.GXBaseBuilderConstants;
import com.geoxus.core.framework.entity.GXCoreModelAttributesEntity;
import com.geoxus.core.framework.entity.GXCoreModelEntity;
import com.geoxus.core.framework.mapper.GXCoreModelMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributeGroupService;
import com.geoxus.core.framework.service.GXCoreModelService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.geoxus.core.framework.support.GXCoreAttributesTableDynamicSqlSupport.coreAttributesTable;
import static com.geoxus.core.framework.support.GXCoreModelAttributesDynamicSqlSupport.coreModelAttributeGroupTable;
import static com.geoxus.core.framework.support.GXCoreModelTableDynamicSqlSupport.coreModelTable;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
@Slf4j
public class GXCoreModelServiceImpl extends ServiceImpl<GXCoreModelMapper, GXCoreModelEntity> implements GXCoreModelService {
    @Autowired
    private GXCoreModelAttributeGroupService coreModelAttributeGroupService;

    @Override
    @Cacheable(value = "core_model", key = "targetClass + methodName + #modelId + #subField")
    public GXCoreModelEntity getModelDetailByModelId(int modelId, String subField) {
        final GXCoreModelEntity entity = getById(modelId);
        if (null == entity) {
            return null;
        }
        if (StrUtil.isBlank(subField)) {
            subField = null;
        }
        final SelectStatementProvider selectStatementProvider = select(
                coreAttributesTable.fieldName,
                coreAttributesTable.attributeId,
                coreAttributesTable.showName,
                coreAttributesTable.category,
                coreAttributesTable.dataType,
                coreAttributesTable.frontType,
                coreAttributesTable.validationDesc,
                coreAttributesTable.validationExpression,
                coreModelAttributeGroupTable.modelAttributeField,
                coreModelAttributeGroupTable.required
        )
                .from(coreModelTable)
                .join(coreModelAttributeGroupTable).on(coreModelAttributeGroupTable.modelId, equalTo(coreModelTable.modelId))
                .join(coreAttributesTable).on(coreModelAttributeGroupTable.attributeId, equalTo(coreAttributesTable.attributeId))
                .where(coreModelTable.modelId, isEqualTo(modelId))
                .and(coreModelAttributeGroupTable.modelAttributeField, isEqualToWhenPresent(subField))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        final List<GXCoreModelAttributesEntity> attributes = coreModelAttributeGroupService.getModelAttributeByModelId(selectStatementProvider);
        entity.setCoreAttributesEntities(attributes);
        return entity;
    }

    @Override
    public boolean checkModelIsHasField(int modelId, String field) {
        final GXCoreModelEntity entity = getModelDetailByModelId(modelId, null);
        if (null == entity) {
            return false;
        }
        final List<GXCoreModelAttributesEntity> attributesEntities = entity.getCoreAttributesEntities();
        for (GXCoreModelAttributesEntity attribute : attributesEntities) {
            if (field.equals(attribute.getFieldName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkFormKeyMatch(Set<String> keySet, String modelName) {
        final GXCoreModelEntity modelEntity = getModelDetailByModelId(getModelIdByModelIdentification(modelName), null);
        if (null == modelEntity) {
            return false;
        }
        final List<GXCoreModelAttributesEntity> attributesEntities = modelEntity.getCoreAttributesEntities();
        final Set<String> keys = new HashSet<>();
        for (GXCoreModelAttributesEntity attribute : attributesEntities) {
            keys.add(attribute.getFieldName());
        }
        return keys.retainAll(keySet);
    }

    @Override
    @Cacheable(value = "core_model", key = "targetClass + methodName + #p0")
    public int getModelIdByModelIdentification(String modelName) {
        final GXCoreModelEntity entity = getOne(new QueryWrapper<GXCoreModelEntity>().select("model_id").eq("model_identification", modelName));
        return null == entity ? 0 : entity.getModelId();
    }

    @Override
    @Cacheable(value = "core_model", key = "targetClass + methodName + #p0")
    public String getModelTypeByModelId(long coreModelId, String defaultValue) {
        final GXCoreModelEntity entity = getOne(new QueryWrapper<GXCoreModelEntity>().select("model_type").eq("model_id", coreModelId));
        if (null == entity) {
            return defaultValue + "Type";
        }
        return entity.getModelType();
    }

    @Override
    public Dict getSearchCondition(Dict condition) {
        final Dict searchCondition = baseMapper.getSearchCondition(condition);
        if (null == searchCondition) {
            return Dict.create();
        }
        return Convert.convert(new TypeReference<Dict>() {
        }, JSONUtil.parse(Optional.ofNullable(searchCondition.getObj(GXBaseBuilderConstants.SEARCH_CONDITION_NAME)).orElse("{}")));
    }

    @Override
    public boolean validateExists(Object value, String field, ConstraintValidatorContext constraintValidatorContext, Dict param) throws UnsupportedOperationException {
        log.info("validateExists : {} , field : {}", value, field);
        return null != getById(Convert.toInt(value));
    }
}
