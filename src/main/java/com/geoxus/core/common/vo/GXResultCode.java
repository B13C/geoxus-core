package com.geoxus.core.common.vo;

public enum GXResultCode {
    OK(0, "操作成功"),
    UNKNOWN_ERROR(999, "未知错误"),
    NEED_PERMISSION(1000, "需要权限"),
    URI_NOT_FOUND(1001, "资源不存在"),
    MISSING_ARGS(1002, "参数不全"),
    IMAGE_TOO_LARGE(1003, "上传的图片太大"),
    HAS_BAN_WORD(1004, "输入有违禁词"),
    INPUT_TOO_SHORT(1005, "输入为空，或者输入字数不够"),
    TARGET_NOT_FOUND(1006, "相关的对象不存在"),
    NEED_CAPTCHA(1007, "需要验证码或者验证码错误"),
    IMAGE_UNKNOWN(1008, "不支持的图片格式"),
    IMAGE_WRONG_FORMAT(1009, "照片格式有误(仅支持JPG,JPEG,GIF,PNG或BMP)"),
    IMAGE_WRONG_CK(1010, "访问私有图片ck验证错误"),
    IMAGE_CK_EXPIRED(1011, "访问私有图片ck过期"),
    TITLE_MISSING(1012, "题目为空"),
    DESC_MISSING(1013, "描述为空"),
    USER_INFO_NOT_FOUND(1014, "用户信息不存在"),
    WRONG_PHONE(1015, "手机号有误"),
    CONFIRM_WRONG_PASSWORD(1016, "两次输入的安全密码不一致"),
    COMMON_ERROR(1017, "系统错误"),
    DATA_FORMAT_ERROR(1018, "请求数据格式错误"),
    LOGIN_ERROR(1019, "登录错误"),
    TOKEN_NOT_FOUND(1020, "TOKEN不存在"),
    OBTAIN_PERMISSION_ERROR(1021, "获取权限出错"),
    USER_NAME_EXISTS(1022, "用户信息已经存在"),
    UPDATE_DATA_ERROR(1023, "数据更新错误"),
    SMS_SEND_SUCCESS(1024, "短信发送成功"),
    SMS_SEND_FAILURE(1025, "短信发送失败"),
    SMS_VERIFY_SUCCESS(1026, "短信验证成功"),
    SMS_VERIFY_FAILURE(1027, "短信验证失败"),
    USER_NAME_EXIST(1028, "用户名已存在"),
    PARAMETER_VALIDATION_ERROR(1029, "参数验证错误"),
    TOKEN_TIMEOUT_EXIT(1030, "您已长时间未进行操作,请重新登录"),
    FILE_ERROR(1031, "不正确的文件"),
    NEED_JSON_FORMAT(1032, "需要JSON格式的参数"),
    PARSE_REQUEST_JSON_ERROR(1033, "解析请求的JSON参数出错"),
    REQUEST_JSON_NOT_BODY(1034, "请求的JSON参数出错为空"),
    FREEZE(1035, "账号被冻结"),
    PASSWORD_ERROR(1036, "账号或密码错误"),
    USER_NOT_EXISTS(1037, "用户不存在"),
    STATUS_ERROR(1038, "当前状态不正确");
    private final String msg;
    private final Integer code;

    GXResultCode(Integer code, String desc) {
        this.code = code;
        this.msg = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
