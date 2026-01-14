package com.xiuxian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 凡人修仙文字游戏启动类
 *
 * @author CodeGenerator
 * @date 2026-01-13
 */
@SpringBootApplication
public class XiuxianApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiuxianApplication.class, args);
        System.out.println("====================================");
        System.out.println("凡人修仙文字游戏后端启动成功！");
        System.out.println("API文档地址: http://localhost:8080/api/v1/swagger-ui.html");
        System.out.println("====================================");
    }
}
