# Quickstart Guide

**Feature**: 凡人修仙文字游戏
**Date**: 2026-01-13

## Overview

本文档提供凡人修仙文字游戏项目的快速入门指南,包括环境搭建、项目运行、开发流程。

---

## Prerequisites

### Required Software

1. **JDK 17+**
   - Download: https://adoptium.net/
   - Install: Set `JAVA_HOME` environment variable

2. **Maven 3.8+**
   - Download: https://maven.apache.org/download.cgi
   - Install: Add to PATH

3. **MySQL 8.x**
   - Download: https://dev.mysql.com/downloads/mysql/
   - Install: Set root password, create database `xiuxian`

4. **IDE** (Recommended)
   - IntelliJ IDEA 2023+ (推荐)或 Eclipse 2023+
   - 安装Lombok插件

### Database Setup

```sql
-- 1. 创建数据库
CREATE DATABASE xiuxian CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 创建用户(可选)
CREATE USER 'xiuxian_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON xiuxian.* TO 'xiuxian_user'@'localhost';
FLUSH PRIVILEGES;
```

---

## Project Setup

### 1. Clone Repository

```bash
git clone <repository-url>
cd xiuxian
```

### 2. Configure Database

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xiuxian?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  sql:
    init:
      mode: always
      schema-locations: classpath:db/migration/V1__init_schema.sql

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

### 3. Install Dependencies

```bash
mvn clean install
```

---

## Running the Application

### Development Mode

```bash
mvn spring-boot:run
```

Application will start on `http://localhost:8080`

### Access the Game

Open browser and navigate to:
```
http://localhost:8080/index.html
```

### Default Test Data

1. **Create Character**:
   - Name: 张三
   - Initial Attributes: 体质5, 精神5, 悟性5, 机缘5, 气运5

2. **First Cultivation**:
   - Click "修炼"
   - Gain experience, promote to 炼气期一层

3. **First Combat**:
   - Click "探索"
   - Encounter monster (火焰蜥蜴)
   - Choose "手动战斗"
   - Select "普通攻击"
   - Victory!

---

## Development Workflow

### 1. Code Structure

```
xiuxian/
├── src/main/java/com/xiuxian/
│   ├── controller/       # REST API endpoints
│   ├── service/          # Business logic
│   ├── mapper/           # Data access layer
│   ├── entity/           # JPA entities
│   ├── dto/              # Request/Response DTOs
│   ├── enums/            # Enumerations
│   └── util/             # Utility classes
├── src/main/resources/
│   ├── application.yml    # Spring Boot configuration
│   └── db/migration/     # Database schema
└── static/               # Static HTML/CSS/JS
```

### 2. Add New Feature

**Example**: Add new skill

1. **Define Skill Entity** (in database):
```sql
INSERT INTO skill (skill_name, function_type, element_type, base_damage, skill_multiplier, spiritual_cost, tier, unlock_method, cost)
VALUES ('火球术', '攻击', '火系', 100, 2.0, 20, 1, '商店购买', 100);
```

2. **Add Controller Method**:
```java
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @GetMapping("/list")
    public List<SkillDTO> listSkills() {
        return skillService.getAllSkills();
    }

    @PostMapping("/learn/{skillId}")
    public Result learnSkill(@PathVariable Long skillId) {
        return skillService.learnSkill(skillId);
    }
}
```

3. **Write Unit Test**:
```java
@SpringBootTest
public class SkillServiceTest {

    @Test
    public void testLearnSkill_Success() {
        // Given
        Long characterId = 1L;
        Long skillId = 1L;

        // When
        Result result = skillService.learnSkill(characterId, skillId);

        // Then
        assertEquals("success", result.getStatus());
    }
}
```

### 3. Run Tests

```bash
mvn test
```

---

## Key Dependencies

```xml
<!-- Spring Boot Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- MyBatis-Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3</version>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <runtime</runtime>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Spring Boot Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Common Issues

### Issue 1: Database Connection Failed

**Solution**:
1. Check MySQL service is running
2. Verify database URL and credentials in `application.yml`
3. Ensure database `xiuxian` exists

### Issue 2: Chinese Characters Display as ???

**Solution**:
1. Verify database charset: `utf8mb4`
2. Add to JDBC URL: `?useUnicode=true&characterEncoding=utf8`
3. Set table charset: `CHARACTER SET utf8mb4`

### Issue 3: Port 8080 Already in Use

**Solution**:
Change port in `application.yml`:
```yaml
server:
  port: 8081
```

---

## Debugging

### Enable Debug Logging

Edit `src/main/resources/application.yml`:
```yaml
logging:
  level:
    com.xiuxian: DEBUG
    org.springframework.web: INFO
    com.baomidou.mybatisplus: DEBUG
```

### View SQL Statements

MyBatis-Plus will log all SQL statements to console when set to DEBUG level.

---

## Deployment

### Build JAR Package

```bash
mvn clean package
```

JAR file: `target/xiuxian-0.0.1-SNAPSHOT.jar`

### Run JAR

```bash
java -jar target/xiuxian-0.0.1-SNAPSHOT.jar
```

---

## Next Steps

1. ✅ Complete Phase 0: Research & Technology Decisions
2. ✅ Complete Phase 1: Design & Contracts
   - data-model.md
   - contracts/ (API documentation)
   - quickstart.md
3. ⏭️ Phase 2: Task Breakdown (`/speckit.tasks`)

---

## Resources

- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **MyBatis-Plus Documentation**: https://baomidou.com/
- **MySQL Documentation**: https://dev.mysql.com/doc/
- **Project Specification**: [spec.md](./spec.md)
- **Data Model**: [data-model.md](./data-model.md)
