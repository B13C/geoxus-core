package com.geoxus.core.framework.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;

public final class GXCoreModelAttributesDynamicSqlSupport {
    public static final CoreModelAttributeGroupTable coreModelAttributeGroupTable = new CoreModelAttributeGroupTable();
    public static final SqlColumn<Integer> modelAttributesId = coreModelAttributeGroupTable.modelAttributesId;
    public static final SqlColumn<String> modelAttributeGroupInnerName = coreModelAttributeGroupTable.modelAttributeField;
    public static final SqlColumn<Integer> modelId = coreModelAttributeGroupTable.modelId;
    public static final SqlColumn<Integer> attributeId = coreModelAttributeGroupTable.attributeId;
    public static final SqlColumn<Integer> required = coreModelAttributeGroupTable.required;
    public static final SqlColumn<Integer> createdAt = coreModelAttributeGroupTable.createdAt;
    public static final SqlColumn<Integer> updatedAt = coreModelAttributeGroupTable.updatedAt;

    public static final class CoreModelAttributeGroupTable extends SqlTable {
        public final SqlColumn<Integer> modelAttributesId = column("model_attributes_id", JDBCType.INTEGER);
        public final SqlColumn<String> modelAttributeField = column("model_attribute_field", JDBCType.VARCHAR);
        public final SqlColumn<Integer> parentId = column("parent_id", JDBCType.INTEGER);
        public final SqlColumn<Integer> modelId = column("model_id", JDBCType.INTEGER);
        public final SqlColumn<Integer> attributeId = column("attribute_id", JDBCType.INTEGER);
        public final SqlColumn<Integer> required = column("required", JDBCType.INTEGER);
        public final SqlColumn<String> ext = column("ext", JDBCType.VARCHAR);
        public final SqlColumn<String> showName = column("showName", JDBCType.VARCHAR);
        public final SqlColumn<String> validationExpression = column("validation_expression", JDBCType.VARCHAR);
        public final SqlColumn<Integer> forceValidation = column("force_validation", JDBCType.INTEGER);
        public final SqlColumn<String> fieldName = column("field_name", JDBCType.VARCHAR);
        public final SqlColumn<String> defaultValue = column("default_value", JDBCType.VARCHAR);
        public final SqlColumn<Integer> createdAt = column("created_at", JDBCType.INTEGER);
        public final SqlColumn<Integer> updatedAt = column("updated_at", JDBCType.INTEGER);

        public CoreModelAttributeGroupTable() {
            super("core_model_attributes");
        }
    }
}
