package com.geoxus.core.framework.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.entity.GXBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@TableName("core_media_library")
@EqualsAndHashCode(callSuper = false)
public class GXCoreMediaLibraryEntity extends GXBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @GXFieldCommentAnnotation(zh = "主键ID")
    private int id;

    @GXFieldCommentAnnotation(zh = "模型类型")
    private String modelType;

    @GXFieldCommentAnnotation(zh = "系统模型ID")
    private long coreModelId;

    @GXFieldCommentAnnotation(zh = "模型ID")
    private long objectId;

    @GXFieldCommentAnnotation(zh = "集合名字")
    private String collectionName;

    @GXFieldCommentAnnotation(zh = "文件名字")
    private String name;

    @GXFieldCommentAnnotation(zh = "带后缀的文件名字")
    private String fileName;

    @GXFieldCommentAnnotation(zh = "文件MIME")
    private String mimeType;

    @TableField(select = false)
    @GXFieldCommentAnnotation(zh = "存储方式")
    private String disk;

    @GXFieldCommentAnnotation(zh = "文件大小")
    private long size;

    @GXFieldCommentAnnotation(zh = "维护者")
    private String manipulations = "[]";

    @GXFieldCommentAnnotation(zh = "自定义属性")
    private String customProperties = "{}";

    @GXFieldCommentAnnotation(zh = "响应式图片")
    private String responsiveImages = "{}";

    @GXFieldCommentAnnotation(zh = "排序")
    private int orderColumn;

    @GXFieldCommentAnnotation(zh = "资源类型")
    private String resourceType = "";

    @GXFieldCommentAnnotation(zh = "OSS的URL地址")
    private String ossUrl;

    @TableField(exist = false)
    @GXFieldCommentAnnotation(zh = "文件存放物理地址")
    private String filePath = "";
}
