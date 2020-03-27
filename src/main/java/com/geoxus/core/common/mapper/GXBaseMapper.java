package com.geoxus.core.common.mapper;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.geoxus.core.common.builder.GXBaseBuilder;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;
import java.util.Set;

@Mapper
public interface GXBaseMapper<T> extends BaseMapper<T> {
    @UpdateProvider(type = GXBaseBuilder.class, method = "updateFieldByCondition")
    boolean updateFieldByCondition(String tableName, Dict data, Dict whereData);

    @SelectProvider(type = GXBaseBuilder.class, method = "listOrSearch")
    List<Dict> listOrSearchPage(IPage<Dict> page, Dict param);

    @UpdateProvider(type = GXBaseBuilder.class, method = "updateStatusByCondition")
    boolean updateStatusByCondition(String tableName, int status, Dict condition);

    @SelectProvider(type = GXBaseBuilder.class, method = "getFieldValueBySQL")
    Dict getFieldValueBySQL(String tableName, Set<String> fieldSet, Dict condition);

    @SelectProvider(type = GXBaseBuilder.class, method = "listOrSearch")
    List<Dict> listOrSearch(Dict param);

    @SelectProvider(type = GXBaseBuilder.class, method = "checkRecordIsExists")
    Integer checkRecordIsExists(String tableName, Dict condition);

    @InsertProvider(type = GXBaseBuilder.class, method = "batchInsertBySQL")
    Integer batchInsertBySQL(String tableName, Set<String> fieldSet, List<Dict> dataList);

    @SelectProvider(type = GXBaseBuilder.class, method = "checkRecordIsUnique")
    Integer checkRecordIsUnique(String tableName, Dict condition);
}
