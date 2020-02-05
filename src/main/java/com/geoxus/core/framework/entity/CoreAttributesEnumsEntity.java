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
@TableName("core_attributes_enums")
@EqualsAndHashCode(callSuper = false)
public class CoreAttributesEnumsEntity extends Model implements Serializable {
    @TableId
    private int attributeEnumId;

    private int attributeId;

    private int coreModelId;

    private String valueEnum;

    private String showName;

    @TableField(fill = FieldFill.INSERT)
    private int createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private int updatedAt;
}
