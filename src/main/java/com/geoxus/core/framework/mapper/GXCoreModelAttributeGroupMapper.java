package com.geoxus.core.framework.mapper;

import com.geoxus.core.common.mapper.GXBaseMapper;
import com.geoxus.core.framework.entity.GXCoreModelAttributeGroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

import java.util.List;

@Mapper
public interface GXCoreModelAttributeGroupMapper extends GXBaseMapper<GXCoreModelAttributeGroupEntity> {
    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    List<GXCoreModelAttributeGroupEntity> getModelAttributeByModelId(SelectStatementProvider selectStatementProvider);
}
