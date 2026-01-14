package com.xiuxian.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HTTP客户端工具类
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/v1";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    // 配置Gson支持LocalDateTime
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                    try {
                        return LocalDateTime.parse(json.getAsString(), DATE_TIME_FORMATTER);
                    } catch (Exception e) {
                        // 如果解析失败，返回null而不是抛出异常
                        return null;
                    }
                })
            .registerTypeAdapter(LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> {
                    try {
                        return context.serialize(src.format(DATE_TIME_FORMATTER));
                    } catch (Exception e) {
                        return null;
                    }
                })
            .setLenient() // 宽松模式，允许一些格式问题
            .create();

    // 是否开启详细日志（通过环境变量 DEBUG=true 控制）
    private static final boolean DEBUG = "true".equalsIgnoreCase(System.getenv("DEBUG")) ||
                                   "true".equalsIgnoreCase(System.getProperty("debug"));

    /**
     * 发送GET请求
     */
    public static String get(String endpoint) throws IOException, InterruptedException {
        String fullUrl = BASE_URL + endpoint;
        if (DEBUG) {
            System.out.println("[API] GET 请求: " + fullUrl);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (DEBUG) {
                System.out.println("[API] 响应状态: " + response.statusCode());
            }

            if (response.statusCode() >= 400) {
                System.err.println("[API] 错误响应: " + response.body());
                throw new RuntimeException("HTTP Error: " + response.statusCode() + " - " + response.body());
            }

            return response.body();
        } catch (ConnectException e) {
            System.err.println("[API] 连接失败: 无法连接到服务器 " + BASE_URL);
            System.err.println("[API] 请确认后端服务是否已启动");
            throw new IOException("无法连接到服务器: " + BASE_URL, e);
        } catch (HttpTimeoutException e) {
            System.err.println("[API] 请求超时: 服务器响应时间过长");
            throw new IOException("请求超时", e);
        } catch (InterruptedException e) {
            System.err.println("[API] 请求被中断");
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    /**
     * 发送POST请求
     */
    public static String post(String endpoint, Object body) throws IOException, InterruptedException {
        String fullUrl = BASE_URL + endpoint;
        String jsonBody = gson.toJson(body);

        if (DEBUG) {
            System.out.println("[API] POST 请求: " + fullUrl);
            System.out.println("[API] 请求体: " + jsonBody);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (DEBUG) {
                System.out.println("[API] 响应状态: " + response.statusCode());
            }

            if (response.statusCode() >= 400) {
                System.err.println("[API] 错误响应: " + response.body());
                throw new RuntimeException("HTTP Error: " + response.statusCode() + " - " + response.body());
            }

            return response.body();
        } catch (ConnectException e) {
            System.err.println("[API] 连接失败: 无法连接到服务器 " + BASE_URL);
            System.err.println("[API] 请确认后端服务是否已启动");
            throw new IOException("无法连接到服务器: " + BASE_URL, e);
        } catch (HttpTimeoutException e) {
            System.err.println("[API] 请求超时: 服务器响应时间过长");
            throw new IOException("请求超时", e);
        } catch (InterruptedException e) {
            System.err.println("[API] 请求被中断");
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    /**
     * 解析响应，提取data字段
     */
    public static <T> T parseResponse(String jsonResponse, Class<T> classOfT) {
        try {
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
            if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
                if (DEBUG) {
                    System.out.println("[API] 解析响应成功: " + classOfT.getSimpleName());
                }
                return gson.fromJson(jsonObject.get("data"), classOfT);
            }
            return null;
        } catch (Exception e) {
            System.err.println("[API] 解析响应失败: " + e.getMessage());
            if (DEBUG) {
                System.err.println("[API] 响应内容: " + jsonResponse);
            }
            return null;
        }
    }

    /**
     * 获取Gson实例
     */
    public static Gson getGson() {
        return gson;
    }
}
