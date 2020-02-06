package com.geoxus.core.framework.service.impl;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.vo.GXBusinessStatusCode;
import com.geoxus.core.common.vo.response.GXPagination;
import com.geoxus.core.framework.entity.GXCoreConfigEntity;
import com.geoxus.core.framework.mapper.GXCoreConfigMapper;
import com.geoxus.core.framework.service.GXCoreConfigService;
import org.springframework.stereotype.Service;

@Service
public class GXCoreConfigServiceImpl extends ServiceImpl<GXCoreConfigMapper, GXCoreConfigEntity> implements GXCoreConfigService {
    @Override
    public long create(GXCoreConfigEntity target, Dict param) {
        save(target);
        return target.getConfigId();
    }

    @Override
    public long update(GXCoreConfigEntity target, Dict param) {
        updateById(target);
        return target.getConfigId();
    }

    @Override
    public boolean delete(Dict param) {
        final long id = param.getLong(PRIMARY_KEY);
        final GXCoreConfigEntity entity = getById(id);
        entity.setStatus(GXBusinessStatusCode.DELETED.getCode());
        updateById(entity);
        return false;
    }

    @Override
    public GXPagination listOrSearch(Dict param) {
        return generatePage(param);
    }

    @Override
    public Dict detail(Dict param) {
        return baseMapper.detail(param);
    }
}
