package com.geoxus.core.common.constant;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;

public class GXBaseBuilderConstants {
    @GXFieldCommentAnnotation(zh = "搜索条件的名字")
    public static final String SEARCH_CONDITION_NAME = "search_condition";

    @GXFieldCommentAnnotation(zh = "模型标识的名字")
    public static final String MODEL_IDENTIFICATION_NAME = "model_identification";

    @GXFieldCommentAnnotation(zh = "前模糊查询条件")
    public static final String BEFORE_LIKE = " like '%{}'";

    @GXFieldCommentAnnotation(zh = "后模糊查询条件")
    public static final String AFTER_LIKE = " like '{}%'";

    @GXFieldCommentAnnotation(zh = "全模糊查询")
    public static final String FULL_LIKE = " like '%{}%'";

    @GXFieldCommentAnnotation(zh = "字符串相等")
    public static final String STR_EQ = " = '{}'";

    @GXFieldCommentAnnotation(zh = "数字相等")
    public static final String NUMBER_EQ = " = {}";

    @GXFieldCommentAnnotation(zh = "数字小于")
    public static final String NUMBER_LT = " < {}";

    @GXFieldCommentAnnotation(zh = "数字小于等于")
    public static final String NUMBER_LE = " <= {}";

    @GXFieldCommentAnnotation(zh = "数字大于")
    public static final String NUMBER_GT = " > {}";

    @GXFieldCommentAnnotation(zh = "数字大于等于")
    public static final String NUMBER_GE = " >= {}";

    @GXFieldCommentAnnotation(zh = "时间区间 开始结束都带等号")
    public static final String TIME_RANGE_WITH_EQ = "{} >= {} AND {} <= {}";

    @GXFieldCommentAnnotation(zh = "时间区间 开始带等号")
    public static final String TIME_RANGE_WITH_START_EQ = "{} >= {} AND {} < {}";

    @GXFieldCommentAnnotation(zh = "时间区间 结束带等号")
    public static final String TIME_RANGE_WITH_END_EQ = "{} > {} AND {} <= {}";

    @GXFieldCommentAnnotation(zh = "时间区间 开始结束不带等号")
    public static final String TIME_RANGE_WITHOUT_EQ = "{} > {} AND {} < {}";

    @GXFieldCommentAnnotation(zh = "IN条件")
    public static final String IN = " IN ({})";

    private GXBaseBuilderConstants() {
    }
}
