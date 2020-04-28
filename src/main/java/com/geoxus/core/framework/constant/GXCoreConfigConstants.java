package com.geoxus.core.framework.constant;

import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;

public class GXCoreConfigConstants {
    @GXFieldCommentAnnotation(zh = "主键ID")
    public static final String PRIMARY_KEY = "config_id";

    @GXFieldCommentAnnotation(zh = "数据表名")
    public static final String TABLE_NAME = "core_config";

    @GXFieldCommentAnnotation(zh = "数据表的别名")
    public static final String TABLE_ALIAS_NAME = "core_config";

    @GXFieldCommentAnnotation(zh = "模型的值")
    public static final String MODEL_IDENTIFICATION_VALUE = "core_config";

    private GXCoreConfigConstants() {
    }
}
