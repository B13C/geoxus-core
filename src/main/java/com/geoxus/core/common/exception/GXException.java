package com.geoxus.core.common.exception;

import com.geoxus.core.common.vo.GXResultCode;

/**
 * 自定义异常
 *
 * @author zj chen <britton@126.com>
 */
public class GXException extends RuntimeException {
    public static final int NORMAL_STATUS = 0;

    private static final long serialVersionUID = 1L;

    private String msg;

    private int code = 500;

    public GXException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public GXException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public GXException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public GXException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public GXException(GXResultCode resultCode) {
        super(resultCode.getMsg());
        this.msg = resultCode.getMsg();
        this.code = resultCode.getCode();
    }

    public GXException(GXResultCode resultCode, Throwable e) {
        super(resultCode.getMsg(), e);
        this.msg = resultCode.getMsg();
        this.code = resultCode.getCode();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
