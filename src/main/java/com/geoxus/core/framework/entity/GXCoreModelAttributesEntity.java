package com.geoxus.core.framework.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.geoxus.core.common.entity.GXBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@TableName("core_model_attributes")
@EqualsAndHashCode(callSuper = false)
public class GXCoreModelAttributesEntity extends GXBaseEntity implements Serializable {
    @TableId("model_attributes_id")
    private int modelAttributesId;

    private String modelAttributeField;

    private int parentId;

    private int modelId;

    private int attributeId;

    private int required;

    private String showName;

    private String validationExpression;

    private int forceValidation;

    private String fieldName;

    private String defaultValue;
}
