package com.geoxus.core.framework.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;

public final class GXCoreModelAttributeGroupDynamicSqlSupport {
    public static final CoreModelAttributeGroupTable coreModelAttributeGroupTable = new CoreModelAttributeGroupTable();
    public static final SqlColumn<Integer> modelAttributeGroupId = coreModelAttributeGroupTable.modelAttributeGroupId;
    public static final SqlColumn<String> modelAttributeGroupInnerName = coreModelAttributeGroupTable.modelAttributeGroupInnerName;
    public static final SqlColumn<Integer> modelId = coreModelAttributeGroupTable.modelId;
    public static final SqlColumn<Integer> attributeId = coreModelAttributeGroupTable.attributeId;
    public static final SqlColumn<Integer> required = coreModelAttributeGroupTable.required;
    public static final SqlColumn<Integer> createdAt = coreModelAttributeGroupTable.createdAt;
    public static final SqlColumn<Integer> updatedAt = coreModelAttributeGroupTable.updatedAt;

    public static final class CoreModelAttributeGroupTable extends SqlTable {
        public final SqlColumn<Integer> modelAttributeGroupId = column("model_attribute_group_id", JDBCType.INTEGER);
        public final SqlColumn<String> modelAttributeGroupInnerName = column("model_attribute_group_inner_name", JDBCType.VARCHAR);
        public final SqlColumn<Integer> modelId = column("model_id", JDBCType.INTEGER);
        public final SqlColumn<Integer> attributeId = column("attribute_id", JDBCType.INTEGER);
        public final SqlColumn<Integer> required = column("required", JDBCType.INTEGER);
        public final SqlColumn<String> ext = column("ext", JDBCType.VARCHAR);
        public final SqlColumn<String> showName = column("showName", JDBCType.VARCHAR);
        public final SqlColumn<String> validationExpression = column("validation_expression", JDBCType.VARCHAR);
        public final SqlColumn<Integer> forceValidation = column("force_validation", JDBCType.INTEGER);
        public final SqlColumn<String> fieldName = column("field_name", JDBCType.VARCHAR);
        public final SqlColumn<Integer> createdAt = column("created_at", JDBCType.INTEGER);
        public final SqlColumn<Integer> updatedAt = column("updated_at", JDBCType.INTEGER);

        public CoreModelAttributeGroupTable() {
            super("core_model_attribute_group");
        }
    }
}
