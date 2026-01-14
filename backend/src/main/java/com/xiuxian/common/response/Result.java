package com.xiuxian.common.response;

import java.time.LocalDateTime;

/**
 * 统一响应对象
 *
 * @author CodeGenerator
 * @date 2026-01-13
 */
public class Result<T> {
    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 成功响应（仅消息）
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(200, message, null);
    }

    /**
     * 错误响应
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 错误响应（默认500）
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    // Getter and Setter methods
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
