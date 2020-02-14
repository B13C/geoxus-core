package com.geoxus.core.framework.builder;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.builder.GXBaseBuilder;
import org.apache.ibatis.jdbc.SQL;

@SuppressWarnings("unused")
public class GXCoreMediaLibraryBuilder implements GXBaseBuilder {
    private static final String DB_FIELDS = "id, core_model_id, model_type, model_id, collection_name, name, file_name, size, manipulations, custom_properties, responsive_images, order_column, resource_type";

    public String listOrSearch(Dict param) {
        return new SQL().SELECT("*").FROM("core_media_library").WHERE(StrUtil.format("model_id={} and core_model_id={}", param.getLong("model_id"), param.getLong("core_model_id"))).toString();
    }

    public String detail(Dict param) {
        return new SQL().SELECT("*").FROM("core_media_library")
                .WHERE(StrUtil.format("model_id={} and core_model_id={}", param.getLong("model_id"), param.getLong("core_model_id")))
                .toString();
    }

    @Override
    public Dict getDefaultSearchField() {
        return Dict.create();
    }

    @Override
    public String getModelIdentificationValue() {
        return "core_media_library";
    }

    public String deleteByCondition(Dict param) {
        final SQL sql = new SQL().DELETE_FROM("core_media_library").WHERE(StrUtil.format("model_id = {} and core_model_id = {}", param.getLong("model_id"), param.getLong("core_model_id")));
        return sql.toString();
    }

    public String baseInfoDetail(Dict param) {
        return new SQL().SELECT("id, model_id, core_model_id, file_name").FROM("core_media_library")
                .WHERE(StrUtil.format("model_id={} and core_model_id={}", param.getLong("model_id"), param.getLong("core_model_id")))
                .toString();
    }

    public String getMediaByCondition(Dict param) {
        final SQL sql = new SQL().SELECT(DB_FIELDS).FROM("core_media_library");
        if (null != param.getLong("model_id")) {
            sql.WHERE(StrUtil.format("model_id = {}", param.getLong("model_id")));
        }
        if (null != param.getLong("core_model_id")) {
            sql.WHERE(StrUtil.format("core_model_id = {}", param.getLong("core_model_id")));
        }
        if (null != param.getStr("resource_type")) {
            sql.WHERE(StrUtil.format("resource_type = '{}'", param.getStr("resource_type")));
        }
        if (null != param.getStr("order_by")) {
            sql.ORDER_BY(param.getStr("order_by"));
        }
        if (null != param.getInt("limit")) {
            sql.LIMIT(param.getInt("limit"));
        }
        return sql.toString();
    }
}
