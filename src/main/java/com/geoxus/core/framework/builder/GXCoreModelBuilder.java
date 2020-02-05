package com.geoxus.core.framework.builder;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.builder.GXBaseBuilder;
import org.apache.ibatis.jdbc.SQL;

public class GXCoreModelBuilder implements GXBaseBuilder {
    @Override
    public String listOrSearch(Dict param) {
        return null;
    }

    @Override
    public String detail(Dict param) {
        return null;
    }

    public String getSearchCondition(Dict param) {
        final SQL sql = new SQL().SELECT("model_id , search_condition").FROM("core_model");
        if (null != param.getInt("model_id")) {
            sql.WHERE(StrUtil.format("model_id = {}", param.getStr("model_id")));
        }
        if (null != param.getStr("model_name")) {
            sql.WHERE(StrUtil.format("model_name = '{}'", param.getStr("model_name")));
        }
        if (null != param.getStr("model_identification")) {
            sql.WHERE(StrUtil.format("model_identification = '{}'", param.getStr("model_identification")));
        }
        if (null != param.getStr("model_type")) {
            sql.WHERE(StrUtil.format("model_type = '{}'", param.getStr("model_type")));
        }
        return sql.toString();
    }
}
