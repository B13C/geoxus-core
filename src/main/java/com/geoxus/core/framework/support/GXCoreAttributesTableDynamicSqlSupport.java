package com.geoxus.core.framework.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;

public final class GXCoreAttributesTableDynamicSqlSupport {
    public static final CoreAttributesTable coreAttributesTable = new CoreAttributesTable();
    public static final SqlColumn<Integer> attributeId = coreAttributesTable.attributeId;
    public static final SqlColumn<String> category = coreAttributesTable.category;
    public static final SqlColumn<String> fieldName = coreAttributesTable.fieldName;
    public static final SqlColumn<String> showName = coreAttributesTable.showName;
    public static final SqlColumn<String> regularDesc = coreAttributesTable.validationDesc;
    public static final SqlColumn<String> regularExpression = coreAttributesTable.validationExpression;
    public static final SqlColumn<String> ext = coreAttributesTable.ext;
    public static final SqlColumn<Boolean> isCore = coreAttributesTable.isCore;
    public static final SqlColumn<String> dataType = coreAttributesTable.dataType;
    public static final SqlColumn<String> frontType = coreAttributesTable.frontType;
    public static final SqlColumn<Integer> createdAt = coreAttributesTable.createdAt;
    public static final SqlColumn<Integer> updatedAt = coreAttributesTable.updatedAt;

    public static final class CoreAttributesTable extends SqlTable {
        public final SqlColumn<Integer> attributeId = column("attribute_id", JDBCType.INTEGER);
        public final SqlColumn<String> category = column("category", JDBCType.VARCHAR);
        public final SqlColumn<String> fieldName = column("field_name", JDBCType.VARCHAR);
        public final SqlColumn<String> showName = column("show_name", JDBCType.VARCHAR);
        public final SqlColumn<String> validationDesc = column("validation_desc", JDBCType.VARCHAR);
        public final SqlColumn<String> validationExpression = column("validation_expression", JDBCType.VARCHAR);
        public final SqlColumn<String> ext = column("ext", JDBCType.VARCHAR);
        public final SqlColumn<Boolean> isCore = column("is_core", JDBCType.BOOLEAN);
        public final SqlColumn<String> dataType = column("data_type", JDBCType.VARCHAR);
        public final SqlColumn<String> frontType = column("front_type", JDBCType.VARCHAR);
        public final SqlColumn<Integer> createdAt = column("created_at", JDBCType.INTEGER);
        public final SqlColumn<Integer> updatedAt = column("updated_at", JDBCType.INTEGER);

        public CoreAttributesTable() {
            super("core_attributes");
        }
    }
}
