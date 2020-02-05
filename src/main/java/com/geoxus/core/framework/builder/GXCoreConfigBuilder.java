package com.geoxus.core.framework.builder;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.framework.service.GXCoreConfigService;
import org.apache.ibatis.jdbc.SQL;

public class GXCoreConfigBuilder {
    public String listOrSearch(Dict param) {
        final SQL sql = new SQL().SELECT("*").FROM("core_config");
        if (null != param.getStr("type")) {
            sql.WHERE("type = '" + param.getStr("type") + "'");
        }
        return sql.toString();
    }

    public String detail(Dict param) {
        final SQL sql = new SQL().SELECT("*").FROM("core_config").WHERE(StrUtil.format("{} = {}", GXCoreConfigService.PRIMARY_KEY, param.getInt(GXCoreConfigService.PRIMARY_KEY)));
        return sql.toString();
    }
}
