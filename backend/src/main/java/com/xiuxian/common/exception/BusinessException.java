package com.xiuxian.common.exception;

/**
 * 业务异常类
 *
 * @author CodeGenerator
 * @date 2026-01-13
 */
public class BusinessException extends RuntimeException {
    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 构造函数（默认400错误码）
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    /**
     * 构造函数（自定义错误码）
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数（使用错误码枚举）
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 构造函数（使用错误码枚举和自定义消息）
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
