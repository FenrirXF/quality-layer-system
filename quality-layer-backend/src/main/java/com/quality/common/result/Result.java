package com.quality.common.result;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    // 成功 带数据
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    // 成功 无返回数据
    public static <T> Result<T> success() {
        return success(null);
    }

    // 自定义失败提示
    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAIL.getCode());
        result.setMsg(msg);
        return result;
    }

    // 根据枚举返回
    public static <T> Result<T> code(ResultCode code) {
        Result<T> result = new Result<>();
        result.setCode(code.getCode());
        result.setMsg(code.getMsg());
        return result;
    }
}