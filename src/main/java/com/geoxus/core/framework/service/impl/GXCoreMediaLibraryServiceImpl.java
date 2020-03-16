package com.geoxus.core.framework.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.config.UploadConfig;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.util.GXUploadUtils;
import com.geoxus.core.framework.entity.GXCoreMediaLibraryEntity;
import com.geoxus.core.framework.mapper.GXCoreMediaLibraryMapper;
import com.geoxus.core.framework.service.GXCoreMediaLibraryService;
import com.geoxus.core.framework.service.GXCoreModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public boolean updateOwner(long objectId, long coreModelId, List<JSONObject> param) {
        if (param.isEmpty()) {
            return true;
        }
        final ArrayList<GXCoreMediaLibraryEntity> newMediaList = new ArrayList<>();
        final List<GXCoreMediaLibraryEntity> oldMediaList = list(new QueryWrapper<GXCoreMediaLibraryEntity>().allEq(Dict.create().set("object_id", objectId).set(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, coreModelId)));
        Set<Integer> saveOldData = CollUtil.newHashSet();
        for (JSONObject dict : param) {
            Integer mediaId = dict.getInt("id");
            if (null != mediaId) {
                if (null != dict.getLong("object_id")) {
                    objectId = dict.getLong("object_id");
                }
                final long itemCoreModelId = Optional.ofNullable(dict.getLong(GXCommonConstants.CORE_MODEL_PRIMARY_NAME)).orElse(coreModelId);
                final String resourceType = Optional.ofNullable(dict.getStr("resource_type")).orElse("");
                final GXCoreMediaLibraryEntity entity = getOne(new QueryWrapper<GXCoreMediaLibraryEntity>().eq("id", mediaId));
                String customProperties = StrUtil.format("{}", Optional.ofNullable(dict.getStr("custom_properties")).orElse("{}"));
                Integer saveOld = dict.getInt("save_old");
                if (null != saveOld) {
                    saveOldData.add(mediaId);
                    if (JSONUtil.isJson(customProperties)) {
                        Dict bean = JSONUtil.toBean(customProperties, Dict.class);
                        bean.put("save_old", saveOld);
                        customProperties = JSONUtil.toJsonStr(bean);
                    }
                }
                if (null != entity) {
                    entity.setObjectId(objectId);
                    entity.setModelType(coreModelService.getModelTypeByModelId(itemCoreModelId, "defaultModelType"));
                    entity.setCoreModelId(itemCoreModelId);
                    entity.setCustomProperties(JSONUtil.toJsonStr(customProperties));
                    entity.setResourceType(resourceType);
                    newMediaList.add(entity);
                }
            }
        }
        List<GXCoreMediaLibraryEntity> deleteMedia = CollUtil.filter(oldMediaList, (GXCoreMediaLibraryEntity t) -> !saveOldData.contains(t.getId()) && !newMediaList.contains(t));
        if (!newMediaList.isEmpty()) {
            boolean b = true;
            if (!deleteMedia.isEmpty()) {
                // TODO : 此处可以加入删除策略
                // TODO : 例如 : 软删除  硬删除等...
                b = removeByIds(deleteMedia.stream().map(GXCoreMediaLibraryEntity::getId).collect(Collectors.toList()));
            }
            return updateBatchById(newMediaList) && b;
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
            entity.setCollectionName((String) param.getOrDefault("collection_name", "default"));
            entity.setResourceType((String) param.getOrDefault("resource_type", "defaultResourceType"));
            entity.setModelType((String) param.getOrDefault("model_type", "defaultModelType"));
            entity.setObjectId((Long) param.getOrDefault("object_id", 0L));
            entity.setCoreModelId((Long) param.getOrDefault(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, 0L));
            entity.setCustomProperties((String) param.getOrDefault("custom_properties", "{}"));
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
