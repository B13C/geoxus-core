package com.geoxus.core.framework.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.geoxus.core.common.annotation.GXValidateDBExistsAnnotation;
import com.geoxus.core.common.annotation.GXValidateExtDataAnnotation;
import com.geoxus.core.common.entity.GXBaseEntity;
import com.geoxus.core.framework.service.GXCoreModelService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@TableName("core_config")
@EqualsAndHashCode(callSuper = false)
public class CoreConfigEntity extends GXBaseEntity {
    @TableId
    private int id;

    @NotBlank()
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String name;

    @NotBlank()
    private String showName;

    private String type;

    @GXValidateDBExistsAnnotation(service = GXCoreModelService.class, fieldName = "model_id")
    private int coreModelId;

    @GXValidateExtDataAnnotation(tableName = "core_config", fieldName = "ext")
    private String ext;

    private int status;
}
