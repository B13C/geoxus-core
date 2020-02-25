package com.geoxus.core.framework.builder;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.builder.GXBaseBuilder;
import com.geoxus.core.common.constant.GXBaseBuilderConstants;
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
        mergeSearchConditionToSQL(sql, param);
        return sql.toString();
    }

    @Override
    public Dict getDefaultSearchField() {
        return Dict.create()
                .set("model_id", GXBaseBuilderConstants.NUMBER_EQ)
                .set("model_name", GXBaseBuilderConstants.AFTER_LIKE)
                .set("model_identification", GXBaseBuilderConstants.AFTER_LIKE)
                .set("model_type", GXBaseBuilderConstants.AFTER_LIKE);
    }

    @Override
    public String getModelIdentificationValue() {
        return "core_model";
    }
}
