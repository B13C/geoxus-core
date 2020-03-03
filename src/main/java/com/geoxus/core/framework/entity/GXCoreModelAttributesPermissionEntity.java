package com.geoxus.core.framework.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.entity.GXBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("core_model_attributes_permission")
@EqualsAndHashCode(callSuper = false)
public class GXCoreModelAttributesPermissionEntity extends GXBaseEntity {
    @TableId
    private Integer attributePermissionId;

    @GXFieldCommentAnnotation(zh = "模型的字段名字,只能是JSON类型的字段名字 比如: ext、info、other")
    private Integer modelAttributeField;

    @GXFieldCommentAnnotation(zh = "属性ID")
    private Integer attributeId;

    @GXFieldCommentAnnotation(zh = "核心模型ID")
    private int coreModelId;

    @GXFieldCommentAnnotation(zh = "允许的人员或者角色({\"role\":[],\"user\":[]})")
    private String allow;

    @GXFieldCommentAnnotation(zh = "拒绝的人员或者角色({\"role\":[],\"user\":[]})")
    private String deny;

    @GXFieldCommentAnnotation(zh = "是否是JSON字段 0: 不是 1: 是")
    private Integer isJsonField = 1;
}
