package com.geoxus.core.framework.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.config.UploadConfig;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.util.GXUploadUtils;
import com.geoxus.core.framework.entity.GXCoreMediaLibraryEntity;
import com.geoxus.core.framework.mapper.GXCoreMediaLibraryMapper;
import com.geoxus.core.framework.service.GXCoreMediaLibraryService;
import com.geoxus.core.framework.service.GXCoreModelService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service(value = "coreMediaLibraryService")
public class GXCoreMediaLibraryServiceImpl extends ServiceImpl<GXCoreMediaLibraryMapper, GXCoreMediaLibraryEntity> implements GXCoreMediaLibraryService {
    @Autowired
    private UploadConfig uploadConfig;

    @Autowired
    private GXCoreModelService coreModelService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(Dict dict) {
        final GXCoreMediaLibraryEntity entity = dict.toBean(GXCoreMediaLibraryEntity.class);
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOwner(long modelId, long coreModelId, List<JSONObject> param) {
        if (param.isEmpty()) {
            return true;
        }
        final ArrayList<GXCoreMediaLibraryEntity> newMediaList = new ArrayList<>();
        final List<GXCoreMediaLibraryEntity> oldMediaList = list(new QueryWrapper<GXCoreMediaLibraryEntity>().allEq(Dict.create().set("model_id", modelId).set("core_model_id", coreModelId)));
        for (JSONObject dict : param) {
            if (null != dict.getLong("id")) {
                final long targetModelId = Optional.ofNullable(dict.getLong("model_id")).orElse(modelId);
                final long itemCoreModelId = Optional.ofNullable(dict.getLong("core_model_id")).orElse(coreModelId);
                final String resourceType = Optional.ofNullable(dict.getStr("resource_type")).orElse("");
                final GXCoreMediaLibraryEntity entity = getOne(new QueryWrapper<GXCoreMediaLibraryEntity>().eq("id", dict.getLong("id")));
                final String customProperties = StrUtil.format("[{}]", Optional.ofNullable(dict.getStr("custom_properties")).orElse(""));
                if (null != entity) {
                    entity.setModelId(targetModelId);
                    entity.setModelType(coreModelService.getModelTypeByModelId(itemCoreModelId, "defaultModelType"));
                    entity.setCoreModelId(itemCoreModelId);
                    entity.setCustomProperties(JSONUtil.toJsonStr(customProperties));
                    entity.setResourceType(resourceType);
                    newMediaList.add(entity);
                }
            }
        }
        final LinkedHashSet<GXCoreMediaLibraryEntity> newListHashSet = Sets.newLinkedHashSet(newMediaList);
        final LinkedHashSet<GXCoreMediaLibraryEntity> oldListHashSet = Sets.newLinkedHashSet(oldMediaList);
        final ArrayList<Integer> deleteMediaIds = new ArrayList<>();
        Sets.difference(oldListHashSet, newListHashSet).forEach(item -> deleteMediaIds.add(item.getId()));
        if (!newListHashSet.isEmpty()) {
            boolean b = true;
            if (!deleteMediaIds.isEmpty()) {
                // TODO
                // 此处可以加入删除策略
                // 例如 : 软删除  硬删除等...
                b = removeByIds(deleteMediaIds);
            }
            return updateBatchById(newListHashSet) && b;
        }
        return true;
    }

    @Override
    public GXCoreMediaLibraryEntity saveFileInfo(MultipartFile file, Dict param) {
        String filePath = uploadConfig.getDepositPath().trim();
        try {
            String fileName = GXUploadUtils.singleUpload(file, filePath);
            GXCoreMediaLibraryEntity entity = new GXCoreMediaLibraryEntity();
            entity.setSize(file.getSize());
            entity.setFileName(fileName);
            entity.setDisk(filePath);
            entity.setMimeType(file.getContentType());
            entity.setName(file.getOriginalFilename());
            entity.setFilePath(fileName);
            entity.setCollectionName(Optional.ofNullable(param.getStr("collection_name")).orElse("default"));
            entity.setResourceType(Optional.ofNullable(param.getStr("resource_type")).orElse("defaultResourceType"));
            entity.setModelType(Optional.ofNullable(param.getStr("model_type")).orElse("defaultModelType"));
            entity.setModelId(Optional.ofNullable(param.getLong("model_id")).orElse(0L));
            entity.setCoreModelId(Optional.ofNullable(param.getLong("core_model_id")).orElse(0L));
            save(entity);
            return entity;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new GXException("文件上传失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByCondition(Dict param) {
        return baseMapper.deleteByCondition(param);
    }

    @Override
    public List<Dict> getMediaByCondition(Dict param) {
        return baseMapper.getMediaByCondition(param);
    }
}
