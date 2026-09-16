package com.quality.common.result;

public enum ResultCode {
    SUCCESS(200, "操作成功"),
    UNAUTHORIZED(401, "登录失效，请重新登录"),
    FORBIDDEN(403, "当前账号无操作权限"),
    PARAM_ERROR(400, "请求参数错误"),
    FAIL(500, "服务器业务异常");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}