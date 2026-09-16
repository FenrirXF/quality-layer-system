package com.quality.common.exception;

import com.quality.common.result.Result;
import com.quality.common.result.ResultCode;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> businessException(BusinessException e) {
        return Result.fail(e.getMessage());
    }

    // 通用运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Result<?> runtimeException(RuntimeException e) {
        return Result.fail(e.getMessage());
    }

    // 全部未知异常兜底
    @ExceptionHandler(Exception.class)
    public Result<?> allException(Exception e) {
        e.printStackTrace();
        return Result.code(ResultCode.FAIL);
    }
}