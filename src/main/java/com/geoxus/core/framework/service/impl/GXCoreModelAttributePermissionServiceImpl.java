package com.geoxus.core.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.framework.entity.GXCoreAttributesEntity;
import com.geoxus.core.framework.entity.GXCoreModelAttributesPermissionEntity;
import com.geoxus.core.framework.mapper.GXCoreModelAttributesPermissionMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributePermissionService;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.geoxus.core.framework.support.GXCoreAttributesTableDynamicSqlSupport.coreAttributesTable;
import static com.geoxus.core.framework.support.GXCoreModelAttributesPermissionDynamicSqlSupport.coreModelAttributePermissionTable;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class GXCoreModelAttributePermissionServiceImpl extends ServiceImpl<GXCoreModelAttributesPermissionMapper, GXCoreModelAttributesPermissionEntity> implements GXCoreModelAttributePermissionService {
    @Override
    @Cacheable(value = "attribute_permission", key = "targetClass + methodName + #coreModelId")
    public List<String> getModelAttributePermissionByCoreModelId(int coreModelId) {
        final SelectStatementProvider selectStatementProvider = select(
                coreAttributesTable.fieldName,
                coreAttributesTable.attributeId
        )
                .from(coreAttributesTable)
                .join(coreModelAttributePermissionTable).on(coreAttributesTable.attributeId, equalTo(coreModelAttributePermissionTable.attributeId))
                .where(coreModelAttributePermissionTable.coreModelId, isEqualTo(coreModelId))
                .build().render(RenderingStrategies.MYBATIS3);
        final List<GXCoreAttributesEntity> attributes = baseMapper.getModelAttributePermissionByModelId(selectStatementProvider);
        final ArrayList<String> strings = new ArrayList<>();
        for (GXCoreAttributesEntity entity : attributes) {
            strings.add(entity.getFieldName());
        }
        return strings;
    }
}
