package com.geoxus.core.framework.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;

public final class GXCoreModelAttributesDynamicSqlSupport {
    public static final CoreModelAttributesTable coreModelAttributesTable = new CoreModelAttributesTable();
    public static final SqlColumn<Integer> modelAttributesId = coreModelAttributesTable.modelAttributesId;
    public static final SqlColumn<String> modelAttributeGroupInnerName = coreModelAttributesTable.modelAttributeField;
    public static final SqlColumn<Integer> modelId = coreModelAttributesTable.modelId;
    public static final SqlColumn<Integer> attributeId = coreModelAttributesTable.attributeId;
    public static final SqlColumn<Integer> required = coreModelAttributesTable.required;
    public static final SqlColumn<Integer> createdAt = coreModelAttributesTable.createdAt;
    public static final SqlColumn<Integer> updatedAt = coreModelAttributesTable.updatedAt;

    public static final class CoreModelAttributesTable extends SqlTable {
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

        public CoreModelAttributesTable() {
            super("core_model_attributes");
        }
    }
}
