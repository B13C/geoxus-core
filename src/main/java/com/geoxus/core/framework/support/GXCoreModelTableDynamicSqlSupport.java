package com.geoxus.core.framework.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;

public final class GXCoreModelTableDynamicSqlSupport {
    public static final CoreModelTable coreModelTable = new CoreModelTable();
    public static final SqlColumn<Integer> modelId = coreModelTable.modelId;
    public static final SqlColumn<Integer> moduleId = coreModelTable.moduleId;
    public static final SqlColumn<String> modelName = coreModelTable.modelName;
    public static final SqlColumn<String> modelShow = coreModelTable.modelShow;
    public static final SqlColumn<String> modelIdentification = coreModelTable.modelIdentification;
    public static final SqlColumn<String> searchCondition = coreModelTable.searchCondition;
    public static final SqlColumn<String> ext = coreModelTable.ext;
    public static final SqlColumn<Integer> createdAt = coreModelTable.createdAt;

    public static final class CoreModelTable extends SqlTable {
        public final SqlColumn<Integer> modelId = column("model_id", JDBCType.INTEGER);
        public final SqlColumn<Integer> moduleId = column("module_id", JDBCType.INTEGER);
        public final SqlColumn<String> modelName = column("model_name", JDBCType.VARCHAR);
        public final SqlColumn<String> modelShow = column("model_show", JDBCType.VARCHAR);
        public final SqlColumn<String> modelIdentification = column("model_identification", JDBCType.VARCHAR);
        public final SqlColumn<String> searchCondition = column("search_condition", JDBCType.VARCHAR);
        public final SqlColumn<String> ext = column("ext", JDBCType.VARCHAR);
        public final SqlColumn<Integer> createdAt = column("created_at", JDBCType.INTEGER);

        public CoreModelTable() {
            super("core_model");
        }
    }
}