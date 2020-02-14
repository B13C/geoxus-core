package com.geoxus.core.framework.builder;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.builder.GXBaseBuilder;
import org.apache.ibatis.jdbc.SQL;

public class GXCoreAttributeEnumsBuilder implements GXBaseBuilder {
    @Override
    public String listOrSearch(Dict param) {
        final SQL sql = new SQL().SELECT("cae.*").FROM("core_attributes_enums as cae");
        if (null != param.getStr("attribute_name")) {
            sql.LEFT_OUTER_JOIN(" core_attributes ca on cae.attribute_id = ca.attribute_id").WHERE(StrUtil.format("ca.field_name = '{}'", param.getStr("attribute_name")));
        }
        if (null != param.getInt("attribute_id")) {
            sql.WHERE(StrUtil.format("cae.attribute_id = {}", param.getInt("attribute_id")));
        }
        return sql.toString();
    }

    @Override
    public String detail(Dict param) {
        final SQL sql = new SQL().SELECT("core_attributes_enums.*").FROM("core_attributes_enums as cae");
        sql.WHERE(StrUtil.format("attribute_enum_id = {}", param.getInt("attribute_enum_id")));
        return sql.toString();
    }

    @Override
    public Dict getDefaultSearchField() {
        return Dict.create();
    }

    @Override
    public String getModelIdentificationValue() {
        return "";
    }

    public String exists(Dict param) {
        final SQL sql = new SQL().SELECT("count(*) as cnt").FROM("core_attributes_enums").WHERE(StrUtil.format("attribute_id = {} and core_model_id = {}", param.getInt("attribute_id"), param.getInt("core_model_id")));
        return sql.toString();
    }
}
