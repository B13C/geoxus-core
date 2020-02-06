package com.geoxus.core.framework.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@TableName("core_model_attribute_group")
@EqualsAndHashCode(callSuper = false)
public class CoreModelAttributeGroupEntity extends Model implements Serializable {
    @TableId("model_attribute_group_id")
    private int modelAttributeGroupId;

    private String modelAttributeGroupInnerName;

    private int modelId;

    private int attributeId;

    private int required;

    private String showName;

    private String validationExpression;

    private int forceValidation;

    private String fieldName;

    private String defaultValue;

    @TableField(fill = FieldFill.INSERT)
    private int createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private int updatedAt;
}
