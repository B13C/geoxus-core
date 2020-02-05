package com.geoxus.core.framework.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;

public final class GXCoreModelAttributePermissionSqlSupport {
    public static final CoreModelAttributePermissionTable coreModelAttributePermissionTable = new CoreModelAttributePermissionTable();
    public static final SqlColumn<Integer> attributePermissionId = coreModelAttributePermissionTable.attributePermissionId;
    public static final SqlColumn<Integer> modelAttributeGroupId = coreModelAttributePermissionTable.modelAttributeGroupId;
    public static final SqlColumn<Integer> attributeId = coreModelAttributePermissionTable.attributeId;
    public static final SqlColumn<String> ext = coreModelAttributePermissionTable.ext;
    public static final SqlColumn<Integer> coreModelId = coreModelAttributePermissionTable.coreModelId;
    public static final SqlColumn<String> allow = coreModelAttributePermissionTable.allow;
    public static final SqlColumn<String> deny = coreModelAttributePermissionTable.deny;
    public static final SqlColumn<Integer> createdAt = coreModelAttributePermissionTable.createdAt;
    public static final SqlColumn<Integer> updatedAt = coreModelAttributePermissionTable.updatedAt;

    public static final class CoreModelAttributePermissionTable extends SqlTable {
        public final SqlColumn<Integer> attributePermissionId = column("attribute_permission_id", JDBCType.INTEGER);
        public final SqlColumn<Integer> modelAttributeGroupId = column("model_attribute_group_id", JDBCType.INTEGER);
        public final SqlColumn<Integer> attributeId = column("attribute_id", JDBCType.INTEGER);
        public final SqlColumn<String> ext = column("ext", JDBCType.VARCHAR);
        public final SqlColumn<Integer> coreModelId = column("core_model_id", JDBCType.INTEGER);
        public final SqlColumn<String> allow = column("allow", JDBCType.VARCHAR);
        public final SqlColumn<String> deny = column("deny", JDBCType.VARCHAR);
        public final SqlColumn<Integer> createdAt = column("created_at", JDBCType.INTEGER);
        public final SqlColumn<Integer> updatedAt = column("updated_at", JDBCType.INTEGER);

        public CoreModelAttributePermissionTable() {
            super("core_model_attribute_permission");
        }
    }
}