package com.geoxus.core.framework.builder;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.builder.GXBaseBuilder;
import org.apache.ibatis.jdbc.SQL;

public class GXCoreModelAttributesPermissionsBuilder implements GXBaseBuilder {
    @Override
    public String listOrSearch(Dict param) {
        final SQL sql = new SQL().SELECT("ca.attribute_name,ca.attribute_id,cmap.model_attribute_field,cmap.field_name,cmap.allow,cmap.deny")
                .FROM("core_model_attributes_permission cmap");
        sql.LEFT_OUTER_JOIN("core_attributes as ca ON ca.attribute_id = cmap.attribute_id");
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
