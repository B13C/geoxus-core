package com.geoxus.core.framework.builder;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.builder.GXBaseBuilder;
import org.apache.ibatis.jdbc.SQL;

public class GXCoreModelAttributesPermissionsBuilder implements GXBaseBuilder {
    @Override
    public String listOrSearch(Dict param) {
        final SQL sql = new SQL().SELECT("cmap.deny,cma.db_field_name,ca.attribute_name,cma.attribute_id,cma.default_value")
                .FROM("core_model_attributes_permission cmap");
        sql.INNER_JOIN("core_model_attributes cma ON cma.model_attributes_id=cmap.model_attributes_id");
        sql.LEFT_OUTER_JOIN("core_attributes ca ON ca.attribute_id = cma.attribute_id");
        sql.WHERE(StrUtil.format("cma.core_model_id = {core_model_id}", param));
        if (null != param.getInt("parent_id")) {
            sql.WHERE(StrUtil.format("cma.parent_id = {parent_id}", param));
        }
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
