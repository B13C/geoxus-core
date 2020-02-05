package com.geoxus.core.framework.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("core_model_attribute_permission")
@EqualsAndHashCode(callSuper = false)
public class CoreModelAttributePermissionEntity {
    @TableId
    private int attributePermissionId;

    /**
     * 模型组的ID 比如: goods、order、contents
     */
    private int modelAttributeGroupId;

    /**
     * 属性ID
     */
    private int attributeId;

    /**
     * 扩展信息
     */
    private String ext;

    /**
     * 模型ID
     */
    private int coreModelId;

    /**
     * 允许的人员或者角色({"role":[],"user":[]})
     */
    private String allow;

    /**
     * 拒绝的人员或者角色({"role":[],"user":[]})
     */
    private String deny;

    @TableField(fill = FieldFill.INSERT)
    private int createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private int updatedAt;
}
