# 凡人修仙文字游戏后端

一款基于Spring Boot的文字修仙RPG游戏后端服务。

## 技术栈

- **Java 17+** - 编程语言
- **Spring Boot 3.2.1** - 应用框架
- **MyBatis-Plus 3.5.5** - ORM框架
- **MySQL 8.x** - 数据库
- **SLF4J + Logback** - 日志框架
- **Spring Boot Actuator** - 健康检查

## 项目结构

```
backend/
├── src/main/java/com/xiuxian/
│   ├── controller/     # 控制器层
│   ├── service/        # 服务层
│   │   └── impl/       # 服务实现
│   ├── mapper/         # 数据访问层
│   ├── entity/         # 实体类
│   ├── dto/            # 数据传输对象
│   │   ├── request/    # 请求DTO
│   │   └── response/   # 响应DTO
│   ├── config/         # 配置类
│   ├── common/         # 通用类
│   │   ├── exception/  # 异常处理
│   │   └── response/   # 统一响应
│   └── util/           # 工具类
├── src/main/resources/
│   ├── application.yml # 应用配置
│   ├── logback-spring.xml # 日志配置
│   └── db/migration/   # 数据库脚本
└── pom.xml             # Maven配置
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.x

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE xiuxian CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xiuxian
    username: your_username
    password: your_password
```

### 启动项目

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 验证启动

访问健康检查接口：
```
GET http://localhost:8080/api/v1/actuator/health
```

返回：
```json
{
  "status": "UP"
}
```

## API文档

启动项目后访问：http://localhost:8080/swagger-ui.html

## 游戏功能

- **角色创建** - 创建修仙角色，分配初始属性
- **修炼系统** - 打坐修炼，提升经验和境界
- **境界突破** - 挑战更高境界，获得属性奖励
- **战斗系统** - 与妖兽战斗，获取经验和装备
- **装备系统** - 管理装备，提升战斗属性
- **炼丹系统** - 炼制丹药，获得增益效果
- **锻造系统** - 锻造装备，提升装备品质
- **技能系统** - 学习技能，战斗中使用
- **宗门系统** - 加入宗门，获取专属资源
- **探索系统** - 探索未知，获得随机奖励

## 开发规范

- 不使用Lombok，手动编写getter/setter
- 统一使用Result<T>响应格式
- 使用BusinessException处理业务异常
- 日志使用SLF4J + Logback

## 许可证

MIT License
