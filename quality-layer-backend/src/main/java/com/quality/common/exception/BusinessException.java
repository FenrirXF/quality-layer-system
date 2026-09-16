package com.quality.common.exception;

import com.quality.common.result.ResultCode;

public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
    }

    public Integer getCode() {
        return code;
    }
}