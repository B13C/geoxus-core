package com.geoxus.core.framework.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@TableName("core_attributes")
@EqualsAndHashCode(callSuper = false)
@Data
public class CoreAttributesEntity extends Model implements Serializable {
    @TableId
    private int attributeId;

    private String category;

    private String fieldName;

    private String showName;

    private String validationDesc;

    private String validationExpression;

    private String ext;

    private boolean isCore;

    private String dataType;

    private String columnType;

    private String frontType;

    @TableField(fill = FieldFill.INSERT)
    private int createdAt;
}
