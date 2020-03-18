package com.geoxus.core.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class GXBaseEntity extends Model implements Serializable {
    @TableField(fill = FieldFill.INSERT)
    private Integer createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private Integer updatedAt;
}
