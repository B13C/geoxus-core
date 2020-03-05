package com.geoxus.core.framework.entity;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("core_model")
@EqualsAndHashCode(callSuper = false)
public class GXCoreModelEntity extends Model implements Serializable {
    @TableId
    private int modelId;

    private int moduleId;

    private String tableName;

    private String modelShow;

    private String modelIdentification;

    private String searchCondition;

    private String modelType;

    @TableField(fill = FieldFill.INSERT)
    private int createdAt;

    @TableField(exist = false)
    private List<Dict> coreAttributes;
}
