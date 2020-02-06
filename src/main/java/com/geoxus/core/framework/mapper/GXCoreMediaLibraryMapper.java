package com.geoxus.core.framework.mapper;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.geoxus.core.common.mapper.GXBaseMapper;
import com.geoxus.core.framework.builder.GXCoreMediaLibraryBuilder;
import com.geoxus.core.framework.entity.GXCoreMediaLibraryEntity;
import com.geoxus.core.framework.handler.GXJsonToListTypeHandler;
import com.geoxus.core.framework.handler.GXJsonToMapTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GXCoreMediaLibraryMapper extends GXBaseMapper<GXCoreMediaLibraryEntity> {
    @Override
    @SelectProvider(type = GXCoreMediaLibraryBuilder.class, method = "listOrSearch")
    @ResultMap("mediaResult")
    List<Dict> listOrSearch(IPage<Dict> page, Dict param);

    @SelectProvider(type = GXCoreMediaLibraryBuilder.class, method = "detail")
    @Results(id = "mediaResult", value = {
            @Result(column = "custom_properties", property = "customProperties", typeHandler = GXJsonToListTypeHandler.class),
            @Result(column = "responsive_images", property = "responsiveImages", typeHandler = GXJsonToMapTypeHandler.class),
            @Result(column = "manipulations", property = "manipulations", typeHandler = GXJsonToListTypeHandler.class)
    })
    Dict detail(Dict param);

    @SelectProvider(type = GXCoreMediaLibraryBuilder.class, method = "baseInfoDetail")
    Dict baseInfoDetail(Dict param);

    @DeleteProvider(type = GXCoreMediaLibraryBuilder.class, method = "deleteByCondition")
    boolean deleteByCondition(Dict param);

    @SelectProvider(type = GXCoreMediaLibraryBuilder.class, method = "getMediaByCondition")
    List<Dict> getMediaByCondition(Dict param);
}
