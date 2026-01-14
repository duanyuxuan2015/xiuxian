# 后端架构设计

**Feature**: 凡人修仙文字游戏
**Date**: 2026-01-13
**Status**: Draft

## Overview

本文档定义凡人修仙文字游戏后端的技术架构、项目结构、核心组件设计。

**技术栈**:
- Java 17+
- Spring Boot 3.x
- MyBatis-Plus 3.x
- MySQL 8.x
- Lombok
- SLF4J + Logback
- Maven 3.8+

---

## 项目结构

```
xiuxian-backend/
├── pom.xml                                 # Maven配置
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── xiuxian/
│   │   │           ├── XiuxianApplication.java          # 启动类
│   │   │           ├── common/                          # 通用组件
│   │   │           │   ├── config/                      # 配置类
│   │   │           │   │   ├── MyBatisPlusConfig.java   # MyBatis-Plus配置
│   │   │           │   │   ├── WebConfig.java           # Web配置
│   │   │           │   │   └── SwaggerConfig.java       # Swagger配置
│   │   │           │   ├── constant/                    # 常量定义
│   │   │           │   │   ├── RealmConstant.java       # 境界常量
│   │   │           │   │   ├── QualityConstant.java     # 品质常量
│   │   │           │   │   └── StateConstant.java       # 状态常量
│   │   │           │   ├── enums/                       # 枚举类
│   │   │           │   │   ├── RealmStageEnum.java      # 境界阶段枚举
│   │   │           │   │   ├── QualityEnum.java         # 品质枚举
│   │   │           │   │   ├── ElementTypeEnum.java     # 元素类型枚举
│   │   │           │   │   └── EventTypeEnum.java       # 事件类型枚举
│   │   │           │   ├── exception/                   # 异常处理
│   │   │           │   │   ├── BusinessException.java   # 业务异常
│   │   │           │   │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   │           │   │   └── ErrorCode.java           # 错误码
│   │   │           │   └── response/                    # 响应封装
│   │   │           │       ├── Result.java              # 统一响应对象
│   │   │           │       └── PageResult.java          # 分页响应对象
│   │   │           ├── domain/                          # 领域模型
│   │   │           │   ├── entity/                      # 实体类
│   │   │           │   │   ├── Character.java           # 角色实体
│   │   │           │   │   ├── Realm.java               # 境界实体
│   │   │           │   │   ├── Pill.java                # 丹药实体
│   │   │           │   │   ├── Material.java            # 材料实体
│   │   │           │   │   ├── Skill.java               # 技能实体
│   │   │           │   │   ├── Equipment.java           # 装备实体
│   │   │           │   │   ├── Monster.java             # 妖兽实体
│   │   │           │   │   ├── Sect.java                # 宗门实体
│   │   │           │   │   ├── PillRecipe.java          # 丹方实体
│   │   │           │   │   ├── EquipmentRecipe.java     # 装备图纸实体
│   │   │           │   │   ├── AlchemyRecord.java       # 炼丹记录实体
│   │   │           │   │   ├── ForgeRecord.java         # 锻造记录实体
│   │   │           │   │   ├── CombatRecord.java        # 战斗记录实体
│   │   │           │   │   ├── CultivationRecord.java   # 修炼记录实体
│   │   │           │   │   ├── ExplorationEvent.java    # 探索事件实体
│   │   │           │   │   └── ...                      # 其他实体
│   │   │           │   ├── dto/                         # 数据传输对象
│   │   │           │   │   ├── request/                 # 请求DTO
│   │   │           │   │   │   ├── CharacterCreateRequest.java
│   │   │           │   │   │   ├── AttributeAssignRequest.java
│   │   │           │   │   │   ├── CombatStartRequest.java
│   │   │           │   │   │   ├── AlchemyStartRequest.java
│   │   │           │   │   │   └── ...
│   │   │           │   │   └── response/                # 响应DTO
│   │   │           │   │       ├── CharacterDetailResponse.java
│   │   │           │   │       ├── CombatResultResponse.java
│   │   │           │   │       ├── AlchemyResultResponse.java
│   │   │           │   │       └── ...
│   │   │           │   └── vo/                          # 视图对象
│   │   │           │       ├── CharacterVO.java
│   │   │           │       ├── SkillVO.java
│   │   │           │       └── ...
│   │   │           ├── mapper/                          # MyBatis Mapper
│   │   │           │   ├── CharacterMapper.java
│   │   │           │   ├── RealmMapper.java
│   │   │           │   ├── PillMapper.java
│   │   │           │   ├── MaterialMapper.java
│   │   │           │   ├── SkillMapper.java
│   │   │           │   ├── EquipmentMapper.java
│   │   │           │   ├── MonsterMapper.java
│   │   │           │   ├── SectMapper.java
│   │   │           │   ├── AlchemyRecordMapper.java
│   │   │           │   ├── ForgeRecordMapper.java
│   │   │           │   ├── CombatRecordMapper.java
│   │   │           │   └── ...
│   │   │           ├── service/                         # 服务层
│   │   │           │   ├── ICharacterService.java
│   │   │           │   ├── ICultivationService.java     # 修炼服务
│   │   │           │   ├── ICombatService.java          # 战斗服务
│   │   │           │   ├── IAlchemyService.java         # 炼丹服务
│   │   │           │   ├── IForgeService.java           # 锻造服务
│   │   │           │   ├── IInventoryService.java       # 背包服务
│   │   │           │   ├── ISkillService.java           # 技能服务
│   │   │           │   ├── IExplorationService.java     # 探索服务
│   │   │           │   ├── ISectService.java            # 宗门服务
│   │   │           │   └── impl/                        # 服务实现
│   │   │           │       ├── CharacterServiceImpl.java
│   │   │           │       ├── CultivationServiceImpl.java
│   │   │           │       ├── CombatServiceImpl.java
│   │   │           │       ├── AlchemyServiceImpl.java
│   │   │           │       ├── ForgeServiceImpl.java
│   │   │           │       └── ...
│   │   │           ├── controller/                      # 控制器层
│   │   │           │   ├── CharacterController.java     # 角色管理
│   │   │           │   ├── CultivationController.java   # 修炼系统
│   │   │           │   ├── CombatController.java        # 战斗系统
│   │   │           │   ├── AlchemyController.java       # 炼丹系统
│   │   │           │   ├── ForgeController.java         # 锻造系统
│   │   │           │   ├── InventoryController.java     # 背包管理
│   │   │           │   ├── SkillController.java         # 技能系统
│   │   │           │   ├── ExplorationController.java   # 探索系统
│   │   │           │   ├── SectController.java          # 宗门系统
│   │   │           │   └── ConfigController.java        # 系统配置
│   │   │           └── util/                            # 工具类
│   │   │               ├── RandomUtil.java              # 随机数工具
│   │   │               ├── CalculateUtil.java           # 计算工具
│   │   │               └── ValidationUtil.java          # 验证工具
│   │   └── resources/
│   │       ├── application.yml                          # 主配置文件
│   │       ├── application-dev.yml                      # 开发环境配置
│   │       ├── application-prod.yml                     # 生产环境配置
│   │       ├── mapper/                                  # MyBatis XML
│   │       │   ├── CharacterMapper.xml
│   │       │   ├── CombatRecordMapper.xml
│   │       │   └── ...
│   │       └── db/
│   │           └── migration/
│   │               └── V1__init_schema.sql              # 数据库初始化脚本
│   └── test/
│       └── java/
│           └── com/
│               └── xiuxian/
│                   ├── service/                         # 服务层测试
│                   │   ├── CharacterServiceTest.java
│                   │   ├── CombatServiceTest.java
│                   │   └── ...
│                   └── controller/                      # 控制器测试
│                       ├── CharacterControllerTest.java
│                       └── ...
```

---

## 核心配置

### 1. Maven依赖 (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.1</version>
        <relativePath/>
    </parent>

    <groupId>com.xiuxian</groupId>
    <artifactId>xiuxian-backend</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>xiuxian-backend</name>
    <description>凡人修仙文字游戏后端</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <lombok.version>1.18.30</lombok.version>
        <swagger.version>2.2.20</swagger.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- MySQL Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- Swagger/OpenAPI -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${swagger.version}</version>
        </dependency>

        <!-- JSON处理 -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>

        <!-- 工具类 -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.24</version>
        </dependency>

        <!-- 测试依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 2. 应用配置 (application.yml)

```yaml
spring:
  application:
    name: xiuxian-backend
  profiles:
    active: dev

  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/xiuxian?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root

    # 连接池配置
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      auto-commit: true
      idle-timeout: 30000
      pool-name: XiuxianHikariCP
      max-lifetime: 1800000
      connection-timeout: 30000

  # Jackson配置
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
    default-property-inclusion: non_null

# MyBatis-Plus配置
mybatis-plus:
  # Mapper XML文件位置
  mapper-locations: classpath*:/mapper/**/*.xml
  # 实体类包路径
  type-aliases-package: com.xiuxian.domain.entity

  # 全局配置
  global-config:
    db-config:
      # 主键策略：自增
      id-type: auto
      # 逻辑删除字段
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
      # 表名前缀
      table-prefix: ''

  # 配置
  configuration:
    # 驼峰转下划线
    map-underscore-to-camel-case: true
    # 打印SQL日志
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    # 二级缓存
    cache-enabled: true
    # 延迟加载
    lazy-loading-enabled: true

# Swagger/OpenAPI配置
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha

# 日志配置
logging:
  level:
    root: INFO
    com.xiuxian: DEBUG
    com.xiuxian.mapper: DEBUG
  pattern:
    console: '%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{50} - %msg%n'

# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /api/v1
```

---

## 核心组件设计

### 1. 统一响应对象

```java
package com.xiuxian.common.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
```

### 2. 分页响应对象

```java
package com.xiuxian.common.response;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;

    public PageResult(List<T> items, Long total, Integer page, Integer pageSize) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }
}
```

### 3. 全局异常处理

```java
package com.xiuxian.common.exception;

import com.xiuxian.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数验证异常处理
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        FieldError fieldError = e.getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数验证失败";
        log.error("参数验证异常: {}", message);
        return Result.error(400, message);
    }

    /**
     * 约束违反异常处理
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.isEmpty() ? "参数验证失败" :
                        violations.iterator().next().getMessage();
        log.error("约束违反异常: {}", message);
        return Result.error(400, message);
    }

    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(500, "系统内部错误");
    }
}
```

### 4. 业务异常类

```java
package com.xiuxian.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}
```

### 5. 错误码枚举

```java
package com.xiuxian.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 通用错误
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),

    // 角色相关
    CHARACTER_NOT_FOUND(1001, "角色不存在"),
    CHARACTER_NAME_EXISTS(1002, "角色姓名已存在"),
    INSUFFICIENT_POINTS(1003, "可用点数不足"),

    // 修炼相关
    INSUFFICIENT_STAMINA(2001, "体力不足"),
    INVALID_STATE(2002, "当前状态不允许此操作"),
    REALM_LEVEL_NOT_READY(2003, "境界层数不足,无法突破"),
    INSUFFICIENT_EXP(2004, "经验值不足"),

    // 战斗相关
    MONSTER_NOT_FOUND(3001, "妖兽不存在"),
    COMBAT_NOT_FOUND(3002, "战斗不存在"),

    // 炼丹相关
    RECIPE_NOT_FOUND(4001, "丹方不存在"),
    INSUFFICIENT_ALCHEMY_LEVEL(4002, "炼丹等级不足"),
    INSUFFICIENT_MATERIALS(4003, "材料不足"),
    INSUFFICIENT_SPIRITUAL_POWER(4004, "灵力不足"),

    // 锻造相关
    EQUIPMENT_RECIPE_NOT_FOUND(5001, "装备图纸不存在"),
    INSUFFICIENT_FORGING_LEVEL(5002, "炼器等级不足"),

    // 背包相关
    ITEM_NOT_FOUND(6001, "物品不存在"),
    INSUFFICIENT_QUANTITY(6002, "物品数量不足"),

    // 技能相关
    SKILL_NOT_FOUND(7001, "技能不存在"),
    SKILL_ALREADY_LEARNED(7002, "技能已学习"),

    // 宗门相关
    SECT_NOT_FOUND(8001, "宗门不存在"),
    ALREADY_IN_SECT(8002, "已加入其他宗门"),
    INSUFFICIENT_CONTRIBUTION(8003, "贡献度不足");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

---

## 分层架构设计

### 1. Controller层职责
- 接收HTTP请求
- 参数验证(@Valid)
- 调用Service层
- 返回统一响应格式(Result)
- 不包含业务逻辑

### 2. Service层职责
- 业务逻辑处理
- 事务管理(@Transactional)
- 调用Mapper层
- 抛出业务异常
- 计算和验证核心逻辑

### 3. Mapper层职责
- 数据库CRUD操作
- 继承BaseMapper<T>
- 复杂查询使用XML配置
- 不包含业务逻辑

### 4. Entity层职责
- 数据库表映射
- 使用@TableName等注解
- 使用Lombok简化代码
- 只包含属性,不包含业务方法

### 5. DTO层职责
- 数据传输对象
- Request: 接收请求参数,包含验证注解
- Response: 返回响应数据
- VO: 视图对象,用于展示

---

## 核心业务流程

### 1. 修炼系统流程

```
开始修炼
  ↓
验证角色状态(必须闲置)
  ↓
验证体力是否充足
  ↓
创建修炼记录
  ↓
更新角色状态为"修炼中"
  ↓
扣除体力
  ↓
计算修炼时长和经验值
  ↓
返回修炼信息

完成修炼
  ↓
验证修炼记录存在
  ↓
验证修炼时间已到
  ↓
增加角色经验值
  ↓
检查是否升级(层次升级)
  ↓
如果升级,增加可用点数
  ↓
更新修炼记录
  ↓
更新角色状态为"闲置"
  ↓
返回修炼结果
```

### 2. 战斗系统流程

```
发起战斗
  ↓
验证角色状态(必须闲置)
  ↓
验证妖兽存在
  ↓
验证体力是否充足
  ↓
创建战斗记录
  ↓
更新角色状态为"战斗中"
  ↓
初始化战斗数据
  ↓
计算速度决定行动顺序
  ↓
返回战斗开始信息

战斗回合
  ↓
验证是否角色回合
  ↓
执行角色行动(攻击/防御/使用物品)
  ↓
计算伤害(考虑暴击、抗性、元素克制)
  ↓
扣除目标生命值
  ↓
检查是否战斗结束
  ↓
如果未结束,执行妖兽行动
  ↓
返回回合结果

战斗结束
  ↓
判断胜利/失败
  ↓
如果胜利:
  - 增加经验值
  - 增加灵石
  - 计算装备掉落
  - 将奖励加入背包
  ↓
更新战斗记录
  ↓
更新角色状态为"闲置"
  ↓
返回战斗结果
```

### 3. 炼丹系统流程

```
开始炼丹
  ↓
验证角色状态(必须闲置)
  ↓
验证丹方存在
  ↓
验证炼丹等级是否满足
  ↓
验证灵力、体力是否充足
  ↓
验证材料是否充足
  ↓
扣除材料
  ↓
创建炼丹记录
  ↓
更新角色状态为"炼丹中"
  ↓
扣除灵力、体力
  ↓
返回炼丹开始信息

完成炼丹
  ↓
验证炼丹记录存在
  ↓
验证炼丹时间已到
  ↓
计算实际成功率
  ↓
随机判定是否成功
  ↓
如果成功:
  - 随机品质
  - 生成丹药
  - 加入背包
  - 获得全额经验
  ↓
如果失败:
  - 获得50%经验
  ↓
检查炼丹等级是否提升
  ↓
更新炼丹记录
  ↓
更新角色状态为"闲置"
  ↓
返回炼丹结果
```

---

## 数据库访问优化

### 1. MyBatis-Plus自动填充

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
```

### 2. 分页插件配置

```java
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
```

---

## 事务管理

### 1. 事务注解使用规范

```java
@Service
@Transactional(rollbackFor = Exception.class)
public class CombatServiceImpl implements ICombatService {

    // 查询方法设置为只读事务
    @Transactional(readOnly = true)
    public CombatRecord getCombatById(Long combatId) {
        // ...
    }

    // 涉及多表操作的方法使用事务
    public CombatResultResponse completeCombat(Long characterId, Long combatId) {
        // 1. 更新战斗记录
        // 2. 增加角色经验
        // 3. 增加灵石
        // 4. 添加掉落物品到背包
        // 5. 更新角色状态
        // 任何一步失败都会回滚
    }
}
```

---

## 性能优化建议

### 1. 缓存策略
- 使用Spring Cache缓存境界配置、宗门配置等静态数据
- 缓存妖兽配置、装备配置等基础数据
- 角色数据不缓存,保证数据一致性

### 2. 查询优化
- 使用索引优化常用查询
- 避免N+1查询问题
- 分页查询使用limit优化
- 复杂查询使用联表查询而非多次查询

### 3. 批量操作
- 批量插入使用MyBatis-Plus的saveBatch
- 批量更新使用updateBatchById
- 减少数据库交互次数

---

## 安全设计

### 1. 参数验证
- 所有API入参使用@Valid验证
- 使用@NotNull、@Min、@Max等注解
- 自定义验证器处理复杂验证逻辑

### 2. 防御性编程
- 所有可能为null的对象进行判空
- 数值计算考虑溢出问题
- 使用Optional避免NPE

### 3. 日志记录
- 记录所有业务异常
- 记录关键业务操作(修炼、战斗、炼丹等)
- 敏感信息脱敏

---

## 测试策略

### 1. 单元测试
- Service层方法必须有单元测试
- 测试覆盖率目标: 80%+
- 使用Mockito模拟依赖

### 2. 集成测试
- Controller层集成测试
- 使用@SpringBootTest
- 测试完整业务流程

### 3. 性能测试
- 压测关键接口
- 监控响应时间
- 优化慢查询

---

## 部署架构

### 开发环境
```
开发者本地
  ↓
MySQL 8.x (本地)
  ↓
Spring Boot应用 (localhost:8080)
```

### 生产环境
```
负载均衡器 (Nginx)
  ↓
Spring Boot应用集群 (多实例)
  ↓
MySQL主从集群
  ↓
Redis缓存集群
```

---

## Summary

本文档定义了凡人修仙文字游戏后端的完整技术架构:

✅ **技术栈**: Java 17 + Spring Boot 3.x + MyBatis-Plus 3.x + MySQL 8.x
✅ **项目结构**: 清晰的分层架构(Controller/Service/Mapper/Entity)
✅ **核心组件**: 统一响应、全局异常处理、错误码管理
✅ **业务流程**: 修炼、战斗、炼丹等核心系统流程设计
✅ **性能优化**: 缓存、查询优化、批量操作
✅ **安全设计**: 参数验证、防御性编程、日志记录
✅ **测试策略**: 单元测试、集成测试、性能测试

**Next Step**: 生成核心实体类和Mapper接口代码
