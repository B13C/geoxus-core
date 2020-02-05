package com.geoxus.core.framework.service.impl;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.vo.GXBusinessStatusCode;
import com.geoxus.core.common.vo.response.GXPagination;
import com.geoxus.core.framework.entity.CoreConfigEntity;
import com.geoxus.core.framework.mapper.GXCoreConfigMapper;
import com.geoxus.core.framework.service.GXCoreConfigService;
import org.springframework.stereotype.Service;

@Service
public class GXCoreConfigServiceImpl extends ServiceImpl<GXCoreConfigMapper, CoreConfigEntity> implements GXCoreConfigService {
    @Override
    public long create(CoreConfigEntity target, Dict param) {
        save(target);
        return target.getId();
    }

    @Override
    public long update(CoreConfigEntity target, Dict param) {
        updateById(target);
        return target.getId();
    }

    @Override
    public boolean delete(Dict param) {
        final long id = param.getLong(PRIMARY_KEY);
        final CoreConfigEntity entity = getById(id);
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
