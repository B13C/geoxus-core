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
     * @param dict 参数
     * @return int
     */
    int save(Dict dict);

    /**
     * 更新条目所关联的模块ID
     * <pre>
     * {@code
     *      List<JSONObject> param = new ArrayList<>();
     *      Dict data1 = Dict.create()
     *      .set("id", 22)
     *      .set("core_model_id", 8)
     *      .set("custom_properties", Dict.create()
     *      .set("name", "tom")
     *      .set("age", 12));
     *      Dict data2 = Dict.create()
     *      .set("id", 24)
     *      .set("core_model_id", 8)
     *      .set("resource_type", "AABB")
     *      .set("update_old", 1)
     *      .set("custom_properties", Dict.create()
     *      .set("name", "tom")
     *      .set("age", 12));
     *     param.add(JSONUtil.parseObj(data2));
     *     param.add(JSONUtil.parseObj(data1));
     *     updateOldFile(12, 10, param, Dict.create().set("resource_type", "A"));
     *  }
     * </pre>
     *
     * @param objectId    对象记录ID
     * @param coreModelId 　核心模型ID
     * @param param       　参数
     * @param condition   条件
     */
    void updateOwner(long objectId, long coreModelId, List<JSONObject> param, Dict condition);

    /**
     * 保存文件
     *
     * @param file  文件
     * @param param 参数
     * @return GXCoreMediaLibraryEntity
     */
    GXCoreMediaLibraryEntity saveFileInfo(MultipartFile file, Dict param);

    /**
     * 通过条件删除media
     *
     * @param param 参数
     * @return boolean
     */
    boolean deleteByCondition(Dict param);

    /**
     * 通过条件获取资源文件
     *
     * @param param 参数
     * @return List
     */
    List<Dict> getMediaByCondition(Dict param);

    /**
     * 更新旧的资源
     *
     * <pre>
     * {@code
     *      List<JSONObject> objectList = new ArrayList<>();
     *      Dict data1 = Dict.create()
     *      .set("id", 22)
     *      .set("core_model_id", 8)
     *      .set("custom_properties", Dict.create()
     *      .set("name", "tom")
     *      .set("age", 12));
     *      Dict data2 = Dict.create()
     *      .set("id", 24)
     *      .set("core_model_id", 8)
     *      .set("resource_type", "AABB")
     *      .set("update_old", 1)
     *      .set("custom_properties", Dict.create()
     *      .set("name", "tom")
     *      .set("age", 12));
     *     objectList.add(JSONUtil.parseObj(data2));
     *     objectList.add(JSONUtil.parseObj(data1));
     *     Dict condition = Dict.create().set("resource_type", "A");
     *     Dict param = Dict.create().set("data" ,objectList )
     *     .set("object_id" , 100)
     *     .set("core_model_id" , 23)
     *     .set("condition" , condition);
     *     updateOldFile(param);
     *  }
     * </pre>
     *
     * @param param 参数
     */
    void updateOldFile(Dict param);
}
