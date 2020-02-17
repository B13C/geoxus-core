package com.geoxus.core.framework.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.constant.GXCommonConstant;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.framework.service.GXCoreModelAttributePermissionService;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.io.StringReader;
import java.sql.*;
import java.util.List;
import java.util.Map;

@MappedTypes({Map.class})
public class GXJsonToMapTypeHandler extends BaseTypeHandler<Map<String, Object>> {
    @GXFieldCommentAnnotation(zh = "标识核心模型主键名字")
    private static final String CORE_MODEL_PRIMARY_NAME = GXCommonConstant.CORE_MODEL_PRIMARY_NAME;

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType) throws SQLException {
        final String parameterString = mapToJson(parameter);
        StringReader reader = new StringReader(parameterString);
        ps.setCharacterStream(i, reader, parameterString.length());
    }

    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = "";
        Clob clob = rs.getClob(columnName);
        if (clob != null) {
            int size = (int) clob.length();
            value = clob.getSubString(1L, size);
        }
        final int coreModelId = rs.getInt(CORE_MODEL_PRIMARY_NAME);
        return jsonToMap(value, coreModelId);
    }

    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = "";
        Clob clob = rs.getClob(columnIndex);
        if (clob != null) {
            int size = (int) clob.length();
            value = clob.getSubString(1L, size);
        }
        return jsonToMap(value, rs.getInt(CORE_MODEL_PRIMARY_NAME));
    }

    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = "";
        Clob clob = cs.getClob(columnIndex);
        final int coreModelId = cs.getInt(CORE_MODEL_PRIMARY_NAME);
        if (clob != null) {
            int size = (int) clob.length();
            value = clob.getSubString(1L, size);
        }
        return jsonToMap(value, coreModelId);
    }

    private Map<String, Object> jsonToMap(String from, int coreModelId) {
        from = from.isEmpty() ? "{}" : from;
        GXCoreModelAttributePermissionService coreModelAttributePermissionService = GXSpringContextUtils.getBean(GXCoreModelAttributePermissionService.class);
        List<String> attributes = coreModelAttributePermissionService.getModelAttributePermissionByCoreModelId(coreModelId);
        final Dict map = Convert.convert(Dict.class, JSONUtil.toBean(from, Dict.class));
        for (String attribute : attributes) {
            map.remove(attribute);
        }
        return map;
    }

    private String mapToJson(Map<String, Object> from) {
        return JSONUtil.toJsonStr(from);
    }
}
