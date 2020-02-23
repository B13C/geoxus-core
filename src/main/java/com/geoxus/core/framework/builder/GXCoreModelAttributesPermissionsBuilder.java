package com.geoxus.core.framework.builder;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.builder.GXBaseBuilder;
import org.apache.ibatis.jdbc.SQL;

public class GXCoreModelAttributesPermissionsBuilder implements GXBaseBuilder {
    @Override
    public String listOrSearch(Dict param) {
        final SQL sql = new SQL().SELECT("ca.field_name , ca.attribute_id").FROM("core_attributes as ca");
        sql.INNER_JOIN("core_model_attributes_permission cmap ON ca.attribute_id = cmap.attribute_id");
        sql.WHERE(StrUtil.format("cmap.core_model_id = {core_model_id}", param));
        return sql.toString();
    }

    @Override
    public String detail(Dict param) {
        return null;
    }

    @Override
    public Dict getDefaultSearchField() {
        return null;
    }

    @Override
    public String getModelIdentificationValue() {
        return null;
    }
}
