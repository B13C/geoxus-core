package com.geoxus.core.framework.util;

import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.util.GXSpringContextUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
public class GXDBSchemaUtils {
    @GXFieldCommentAnnotation(zh = "日志对象")
    private static final Logger LOG = LoggerFactory.getLogger(GXDBSchemaUtils.class);

    private static final String SHOW_INDEX_SQL = "SHOW INDEX FROM `{}`";

    private static final String DROP_INDEX_SQL = "DROP INDEX `{}` on `{}`";

    private static DataSource dataSource = GXSpringContextUtils.getBean(DataSource.class);

    private GXDBSchemaUtils() {
    }

    /**
     * 获取表的列
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    @Cacheable(value = "table_column", key = "targetClass + methodName +#tableName")
    public static List<TableField> getTableColumn(String tableName) {
        final List<TableField> resultData = new ArrayList<>();
        try {
            final String sql = "SELECT column_name,data_type,column_type FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name ='{}'";
            try (final Connection connection = dataSource.getConnection();
                 final Statement statement = connection.createStatement();
                 final ResultSet resultSet = statement.executeQuery(StrUtil.format(sql, tableName))) {
                while (resultSet.next()) {
                    final TableField tableField = new TableField();
                    final String columnName = resultSet.getString("column_name");
                    final String dataType = resultSet.getString("data_type");
                    final String columnType = resultSet.getString("column_type");
                    tableField.setColumnName(columnName);
                    tableField.setColumnType(columnType);
                    tableField.setDataType(dataType);
                    resultData.add(tableField);
                }
            }
        } catch (SQLException e) {
            LOG.error("获取数据表的字段出错", e);
        }
        return resultData;
    }

    /**
     * 获取表的索引
     *
     * @param tableName 表名
     * @return
     * @throws SQLException
     */
    @Cacheable(value = "table_column", key = "targetClass + methodName +#tableName")
    public static List<TableIndexData> listTableIndex(String tableName) throws SQLException {
        Map<String, Map<String, Object>> returnList = new HashMap<>();
        List<TableIndexData> list = new ArrayList<>();
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(StrUtil.format(SHOW_INDEX_SQL, tableName))) {
            while (resultSet.next()) {
                final TableIndexData tableIndexData = new TableIndexData();
                tableIndexData.setNonUnique(resultSet.getInt("non_unique"));
                tableIndexData.setKeyName(resultSet.getString("key_name"));
                tableIndexData.setSeqInIndex(resultSet.getString("seq_in_index"));
                tableIndexData.setColumnName(resultSet.getString("column_name"));
                tableIndexData.setCardinality(resultSet.getInt("cardinality"));
                list.add(tableIndexData);
            }
        }
        for (TableIndexData data : list) {
            final Map<String, Object> map = Optional.ofNullable(returnList.get(data.keyName)).orElse(new HashMap<>());
            map.put(data.getSeqInIndex(), data.getColumnName());
            returnList.put(data.getKeyName(), map);
        }
        log.info("{}", returnList);
        return list;
    }

    /**
     * 删除表的索引
     *
     * @param tableName 表名
     * @param indexName 索引名
     * @return
     */
    @CacheEvict(value = "table_column", key = "targetClass + methodName +#tableName")
    public static boolean dropTableIndex(String tableName, String indexName) {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            if (checkTableFieldExists(tableName, indexName)) {
                return statement.execute(StrUtil.format(DROP_INDEX_SQL, indexName, tableName));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 检测表中是否有指定的字段
     *
     * @param tableName
     * @param field
     * @return
     */
    public static boolean checkTableFieldExists(String tableName, String field) {
        final List<TableField> tableFieldList = getTableColumn(tableName);
        for (TableField tableField : tableFieldList) {
            if (field.equals(tableField.getColumnName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测索引是否存在
     *
     * @param tableName
     * @param indexName
     * @return
     */
    public static boolean checkTableIndexExists(String tableName, String indexName) {
        String checkIndexExistsSQL = StrUtil.format(StrUtil.concat(true, SHOW_INDEX_SQL, " where key_name = '{}'"), tableName, indexName);
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(checkIndexExistsSQL)) {
            return resultSet.first();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 获取SQL语句的查询字段
     *
     * @param tableName
     * @param targetSet
     * @param remove
     * @return
     */
    @Cacheable(value = "table_column", key = "targetClass + methodName +#tableName")
    public static String getSqlFieldStr(String tableName, Set<String> targetSet, boolean remove) {
        final List<TableField> tableFields = GXDBSchemaUtils.getTableColumn(tableName);
        final HashSet<String> result = new HashSet<>();
        for (GXDBSchemaUtils.TableField tableField : tableFields) {
            result.add(tableField.getColumnName());
        }
        if (remove) {
            result.removeAll(targetSet);
        } else {
            result.retainAll(targetSet);
        }
        return String.join(",", result);
    }

    /**
     * 获取SQL语句的查询字段
     *
     * @param tableName
     * @param targetSet
     * @param tableAlias
     * @param remove
     * @return
     */
    public static String getSqlFieldStr(String tableName, Set<String> targetSet, String tableAlias, boolean remove) {
        if (StrUtil.isBlank(tableAlias)) {
            LOG.error("表的别名不能为空");
            return "";
        }
        final List<TableField> tableFields = GXDBSchemaUtils.getTableColumn(tableName);
        final HashSet<String> tmpResult = new HashSet<>();
        final HashSet<String> result = new HashSet<>();
        for (GXDBSchemaUtils.TableField tableField : tableFields) {
            tmpResult.add(tableField.getColumnName());
        }
        if (remove) {
            tmpResult.removeAll(targetSet);
        } else {
            tmpResult.retainAll(targetSet);
        }
        for (String field : tmpResult) {
            result.add(StrUtil.format("{}.{}", tableAlias, field));
        }
        return String.join(",", result);
    }

    @Data
    public static class TableField {
        private String columnName;
        private String dataType;
        private String columnType;
    }

    @Data
    public static class TableIndexData {
        private int nonUnique;
        private String keyName;
        private String seqInIndex;
        private String columnName;
        private int cardinality;
    }
}
