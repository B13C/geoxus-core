package com.geoxus.core.framework.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.framework.service.GXDBSchemaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Service
public class GXDBSchemaServiceImpl implements GXDBSchemaService {
    @GXFieldCommentAnnotation(zh = "获取索引SQL模板")
    private static final String SHOW_INDEX_SQL = "SHOW INDEX FROM `{}`";

    @GXFieldCommentAnnotation(zh = "删除索引SQL模板")
    private static final String DROP_INDEX_SQL = "DROP INDEX `{}` on `{}`";

    @GXFieldCommentAnnotation(zh = "数据源对象")
    private static DataSource dataSource = GXSpringContextUtils.getBean(DataSource.class);

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName +#tableName")
    public List<GXDBSchemaService.TableField> getTableColumn(String tableName) {
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
            log.error("获取数据表的字段出错", e);
        }
        return resultData;
    }

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName +#tableName")
    public List<GXDBSchemaService.TableIndexData> listTableIndex(String tableName) throws SQLException {
        Map<String, Map<String, Object>> returnList = new HashMap<>();
        List<GXDBSchemaService.TableIndexData> list = new ArrayList<>();
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(StrUtil.format(SHOW_INDEX_SQL, tableName))) {
            while (resultSet.next()) {
                final TableIndexData tableIndexData = new GXDBSchemaService.TableIndexData();
                tableIndexData.setNonUnique(resultSet.getInt("non_unique"));
                tableIndexData.setKeyName(resultSet.getString("key_name"));
                tableIndexData.setSeqInIndex(resultSet.getString("seq_in_index"));
                tableIndexData.setColumnName(resultSet.getString("column_name"));
                tableIndexData.setCardinality(resultSet.getInt("cardinality"));
                list.add(tableIndexData);
            }
        }
        for (GXDBSchemaService.TableIndexData data : list) {
            final Map<String, Object> map = Optional.ofNullable(returnList.get(data.getKeyName())).orElse(new HashMap<>());
            map.put(data.getSeqInIndex(), data.getColumnName());
            returnList.put(data.getKeyName(), map);
        }
        log.info("{}", returnList);
        return list;
    }

    @Override
    @CacheEvict(value = "table_column", key = "targetClass + methodName +#tableName")
    public boolean dropTableIndex(String tableName, String indexName) {
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

    @Override
    public boolean checkTableFieldExists(String tableName, String field) {
        final List<TableField> tableFieldList = getTableColumn(tableName);
        for (TableField tableField : tableFieldList) {
            if (field.equals(tableField.getColumnName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkTableIndexExists(String tableName, String indexName) {
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

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName +#tableName")
    public String getSqlFieldStr(String tableName, Set<String> targetSet, boolean remove) {
        final List<TableField> tableFields = getTableColumn(tableName);
        final HashSet<String> result = new HashSet<>();
        for (GXDBSchemaServiceImpl.TableField tableField : tableFields) {
            result.add(tableField.getColumnName());
        }
        final Dict tmpTargetDict = Dict.create();
        for (String key : targetSet) {
            if (StrUtil.contains(key, " ")) {
                final String[] split = StrUtil.split(StrUtil.replace(key, " ", "::"), "::");
                String tmpKey = String.join("", ArrayUtil.sub(split, 0, 1));
                tmpTargetDict.set(tmpKey, key);
            } else {
                tmpTargetDict.set(key, key);
            }
        }
        if (remove) {
            result.removeAll(tmpTargetDict.keySet());
        } else {
            result.retainAll(tmpTargetDict.keySet());
        }
        final HashSet<String> lastResult = CollUtil.newHashSet();
        for (String field : result) {
            lastResult.add(StrUtil.format("{}", tmpTargetDict.getStr(field)));
        }
        return String.join(",", lastResult);
    }

    @Override
    public String getSqlFieldStr(String tableName, Set<String> targetSet, String tableAlias, boolean remove) {
        if (StrUtil.isBlank(tableAlias)) {
            log.error("表的别名不能为空");
            return "";
        }
        final List<TableField> tableFields = getTableColumn(tableName);
        final HashSet<String> tmpResult = new HashSet<>();
        final HashSet<String> result = new HashSet<>();
        for (GXDBSchemaServiceImpl.TableField tableField : tableFields) {
            tmpResult.add(tableField.getColumnName());
        }
        final Dict tmpTargetDict = Dict.create();
        for (String key : targetSet) {
            if (StrUtil.contains(key, " ")) {
                final String[] split = StrUtil.split(StrUtil.replace(key, " ", "::"), "::");
                String tmpKey = String.join("", ArrayUtil.sub(split, 0, 1));
                tmpTargetDict.set(tmpKey, key);
            } else {
                tmpTargetDict.set(key, key);
            }
        }
        if (remove) {
            tmpResult.removeAll(tmpTargetDict.keySet());
        } else {
            tmpResult.retainAll(tmpTargetDict.keySet());
        }
        for (String field : tmpResult) {
            result.add(StrUtil.format("{}.{}", tableAlias, tmpTargetDict.getStr(field)));
        }
        return String.join(",", result);
    }
}
