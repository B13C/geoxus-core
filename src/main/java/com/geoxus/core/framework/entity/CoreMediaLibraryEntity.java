package com.geoxus.core.framework.entity;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.geoxus.core.common.entity.GXBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("core_media_library")
@EqualsAndHashCode(callSuper = false)
public class CoreMediaLibraryEntity extends GXBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    private int id;

    /**
     * 模型类型
     */
    private String modelType;

    /**
     * 系统模型ID
     */
    private long coreModelId;

    /**
     * 模型ID
     */
    private long modelId;

    /**
     * 集合名字
     */
    private String collectionName;

    /**
     * 文件名字
     */
    private String name;

    /**
     * 带后缀的文件名字
     */
    private String fileName;

    /**
     * 文件mime
     */
    private String mimeType;

    /**
     * 存储方式
     */
    @TableField(select = false)
    private String disk;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 维护者
     */
    private String manipulations = "[]";

    /**
     * 自定义属性
     */
    private String customProperties = "{}";

    /**
     * 响应式图片
     */
    private String responsiveImages = "{}";

    /**
     * 排序
     */
    private int orderColumn;

    /**
     * 资源类型
     */
    private String resourceType = "";

    /**
     * 文件存放物理地址
     */
    @TableField(exist = false)
    private String filePath;

    @TableField(exist = false)
    private List<Dict> media;
}
