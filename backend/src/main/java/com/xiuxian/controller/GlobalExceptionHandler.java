package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.xiuxian.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数验证失败");
        logger.warn("参数验证异常: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        logger.error("系统异常: ", e);
        return Result.error(500, "系统内部错误");
    }
}
