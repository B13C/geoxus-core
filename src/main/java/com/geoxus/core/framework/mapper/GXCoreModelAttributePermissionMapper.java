package com.geoxus.core.framework.mapper;

import com.geoxus.core.common.mapper.GXBaseMapper;
import com.geoxus.core.framework.entity.GXCoreAttributesEntity;
import com.geoxus.core.framework.entity.GXCoreModelAttributePermissionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Mapper
@Primary
public interface GXCoreModelAttributePermissionMapper extends GXBaseMapper<GXCoreModelAttributePermissionEntity> {
    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    List<GXCoreAttributesEntity> getModelAttributePermissionByModelId(SelectStatementProvider selectStatementProvider);
}
