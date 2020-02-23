package com.geoxus.core.framework.builder;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.builder.GXBaseBuilder;
import org.apache.ibatis.jdbc.SQL;

public class GXCoreModelAttributesBuilder implements GXBaseBuilder {
    @Override
    public String listOrSearch(Dict param) {
        final SQL sql = new SQL().SELECT("ca.field_name", "ca.attribute_id", "ca.show_name",
                "ca.category", "ca.data_type",
                "ca.front_type", "ca.validation_desc", "ca.validation_expression", "cma.model_attribute_field", "cma.required")
                .FROM("core_model_attributes cma");
        sql.INNER_JOIN("core_model ON cma.model_id = core_model.model_id");
        sql.INNER_JOIN("core_attributes ca ON cma.attribute_id = ca.attribute_id");
        sql.WHERE(StrUtil.format("core_model.model_id = {model_id}", param));
        if (null != param.getStr("model_attribute_field")) {
            sql.WHERE(StrUtil.format("cma.model_attribute_field = '{model_attribute_field}'", param));
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
