package com.geoxus.core.framework.service;

import lombok.Data;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface GXDBSchemaService {
    /**
     * 获取表的列
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    List<GXDBSchemaService.TableField> getTableColumn(String tableName);

    /**
     * 获取表的索引
     *
     * @param tableName 表名
     * @return
     * @throws SQLException
     */
    List<GXDBSchemaService.TableIndexData> listTableIndex(String tableName) throws SQLException;

    /**
     * 删除表的索引
     *
     * @param tableName 表名
     * @param indexName 索引名
     * @return
     */
    boolean dropTableIndex(String tableName, String indexName);

    /**
     * 检测表中是否有指定的字段
     *
     * @param tableName
     * @param field
     * @return
     */
    boolean checkTableFieldExists(String tableName, String field);

    /**
     * 检测索引是否存在
     *
     * @param tableName
     * @param indexName
     * @return
     */
    boolean checkTableIndexExists(String tableName, String indexName);

    /**
     * 获取SQL语句的查询字段
     *
     * @param tableName
     * @param targetSet
     * @param remove
     * @return
     */
    String getSqlFieldStr(String tableName, Set<String> targetSet, boolean remove);

    /**
     * 获取SQL语句的查询字段
     *
     * @param tableName
     * @param targetSet
     * @param tableAlias
     * @param remove
     * @return
     */
    String getSqlFieldStr(String tableName, Set<String> targetSet, String tableAlias, boolean remove);

    @Data
    class TableField {
        private String columnName;
        private String dataType;
        private String columnType;
    }

    @Data
    class TableIndexData {
        private int nonUnique;
        private String keyName;
        private String seqInIndex;
        private String columnName;
        private int cardinality;
    }
}
