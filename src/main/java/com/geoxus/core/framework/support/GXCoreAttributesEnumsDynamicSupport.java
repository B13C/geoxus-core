package com.geoxus.core.framework.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;

public final class GXCoreAttributesEnumsDynamicSupport {
    public static final CoreAttributesEnumsTable coreAttributesEnumsTable = new CoreAttributesEnumsTable();
    public static final SqlColumn<Integer> attributeEnumId = coreAttributesEnumsTable.attributeEnumId;
    public static final SqlColumn<Integer> attributeId = coreAttributesEnumsTable.attributeId;
    public static final SqlColumn<String> valueEnum = coreAttributesEnumsTable.valueEnum;
    public static final SqlColumn<String> showName = coreAttributesEnumsTable.showName;
    public static final SqlColumn<Integer> createdAt = coreAttributesEnumsTable.createdAt;

    public static final class CoreAttributesEnumsTable extends SqlTable {
        public final SqlColumn<Integer> attributeEnumId = column("attribute_enum_id", JDBCType.INTEGER);
        public final SqlColumn<Integer> attributeId = column("attribute_id", JDBCType.VARCHAR);
        public final SqlColumn<String> valueEnum = column("value_enum", JDBCType.VARCHAR);
        public final SqlColumn<String> showName = column("show_name", JDBCType.VARCHAR);
        public final SqlColumn<Integer> createdAt = column("created_at", JDBCType.VARCHAR);

        public CoreAttributesEnumsTable() {
            super("core_attributes_enums");
        }
    }
}
