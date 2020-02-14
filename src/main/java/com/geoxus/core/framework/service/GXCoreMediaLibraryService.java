package com.geoxus.core.framework.service;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import com.geoxus.core.framework.entity.GXCoreMediaLibraryEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GXCoreMediaLibraryService extends GXBaseService<GXCoreMediaLibraryEntity> {
    /**
     * 保存数据
     *
     * @param dict
     * @return
     */
    int save(Dict dict) throws Exception;

    /**
     * 更新条目所关联的模块ID
     *
     * @param param
     * @return
     * @example Dict.create().set(" id ", 1).set(" core_model_id ", 8).set(" custom_properties ", Dict.create ().set(" name ", " tom ").set(" age ", 12))
     */
    boolean updateOwner(long modelId, long coreModelId, List<JSONObject> param);

    /**
     * 保存文件
     *
     * @param file
     * @param param
     * @return
     */
    GXCoreMediaLibraryEntity saveFileInfo(MultipartFile file, Dict param);

    /**
     * 通过条件删除media
     *
     * @param param
     * @return
     */
    boolean deleteByCondition(Dict param);

    /**
     * 通过条件获取资源文件
     *
     * @param param
     * @return
     */
    List<Dict> getMediaByCondition(Dict param);
}
