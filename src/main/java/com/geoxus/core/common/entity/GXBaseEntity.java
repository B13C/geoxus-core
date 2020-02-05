package com.geoxus.core.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import lombok.Data;

@Data
public class GXBaseEntity extends Model {
    @TableField(fill = FieldFill.INSERT)
    private Integer createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private Integer updatedAt;

    @TableField(exist = false)
    @GXFieldCommentAnnotation(value = "用于标识该实体是否需要被验证")
    private boolean enableValidateEntity = true;
}
