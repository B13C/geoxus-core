package com.geoxus.core.common.controller;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.annotation.GXRequestBodyToBeanAnnotation;
import com.geoxus.core.common.constant.GXControllerConstants;
import com.geoxus.core.common.entity.GXBaseEntity;
import com.geoxus.core.common.util.GXHttpContextUtils;
import com.geoxus.core.common.util.GXResultUtils;
import com.geoxus.core.common.validator.group.GXCreateGroup;
import com.geoxus.core.common.validator.group.GXUpdateGroup;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

public interface GXController<T extends GXBaseEntity> {
    /**
     * 创建数据
     */
    default GXResultUtils create(@Validated(value = {GXCreateGroup.class}) @GXRequestBodyToBeanAnnotation() T target) {
        return GXResultUtils.ok(GXControllerConstants.DEFAULT_DATA);
    }

    /**
     * 更新数据
     *
     * @param target
     * @return
     */
    default GXResultUtils update(@Valid @GXRequestBodyToBeanAnnotation(groups = {GXUpdateGroup.class}) T target) {
        return GXResultUtils.ok(GXControllerConstants.DEFAULT_DATA);
    }

    /**
     * 删除数据
     */
    default GXResultUtils delete(Dict param) {
        return GXResultUtils.ok(GXControllerConstants.DEFAULT_DATA);
    }

    /**
     * 列表或者搜索
     */
    default GXResultUtils listOrSearch(Dict param) {
        return GXResultUtils.ok(GXControllerConstants.DEFAULT_DATA);
    }

    /**
     * 内容详情
     */
    default GXResultUtils detail(Dict param) {
        return GXResultUtils.ok(GXControllerConstants.DEFAULT_DATA);
    }

    /**
     * 从token中获取用户ID
     *
     * @param tokenName
     * @param tokenIdName
     * @return
     */
    default long getUserIdFromToken(String tokenName, String tokenIdName) {
        return GXHttpContextUtils.getUserIdFromToken(tokenName, tokenIdName);
    }
}
