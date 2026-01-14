# 代码生成指南

**Feature**: 凡人修仙文字游戏
**Date**: 2026-01-13
**Status**: Draft

## Overview

本文档提供基于已完成的设计文档自动生成后端代码的指南和模板。

---

## 设计文档总览

| 文档 | 状态 | 描述 |
|------|------|------|
| data-model.md | ✅ 已完成 | 24个核心实体的数据模型定义 |
| api-contracts.md | ✅ 已完成 | RESTful API接口规范(12个模块) |
| database-schema.sql | ✅ 已完成 | MySQL DDL建表语句和种子数据 |
| backend-architecture.md | ✅ 已完成 | 后端技术架构和项目结构 |

---

## 代码生成顺序

### Phase 1: 基础设施代码
1. ✅ Maven配置 (pom.xml)
2. ✅ 应用配置 (application.yml)
3. ✅ 启动类 (XiuxianApplication.java)
4. ✅ 通用组件 (Result, PageResult, 异常处理等)

### Phase 2: 实体层代码 (Entity)
1. Character.java
2. Realm.java
3. Pill.java
4. Material.java
5. Skill.java
6. Equipment.java
7. Monster.java
8. Sect.java
9. 其他16个实体类

### Phase 3: 数据访问层 (Mapper)
1. CharacterMapper.java
2. RealmMapper.java
3. PillMapper.java
4. ... (对应所有实体)

### Phase 4: 服务层 (Service)
1. ICharacterService.java + CharacterServiceImpl.java
2. ICultivationService.java + CultivationServiceImpl.java
3. ICombatService.java + CombatServiceImpl.java
4. IAlchemyService.java + AlchemyServiceImpl.java
5. ... (12个业务服务)

### Phase 5: 控制器层 (Controller)
1. CharacterController.java
2. CultivationController.java
3. CombatController.java
4. AlchemyController.java
5. ... (12个控制器)

### Phase 6: DTO和VO
1. Request DTO (请求对象)
2. Response DTO (响应对象)
3. VO (视图对象)

---

## 代码生成模板

### 1. Entity实体类模板

```java
package com.xiuxian.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * {实体中文名}实体类
 *
 * @author CodeGenerator
 * @date {生成日期}
 */
@Data
@TableName("{表名}")
public class {实体类名} {

    /**
     * {字段注释}
     */
    @TableId(value = "{主键字段名}", type = IdType.AUTO)
    private Long {主键属性名};

    /**
     * {字段注释}
     */
    @TableField("{字段名}")
    private {类型} {属性名};

    // ... 其他字段

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
```

### 2. Mapper接口模板

```java
package com.xiuxian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiuxian.domain.entity.{实体类名};
import org.apache.ibatis.annotations.Mapper;

/**
 * {实体中文名}Mapper接口
 *
 * @author CodeGenerator
 * @date {生成日期}
 */
@Mapper
public interface {实体类名}Mapper extends BaseMapper<{实体类名}> {

    // MyBatis-Plus提供了基础的CRUD方法
    // 复杂查询在这里添加自定义方法

}
```

### 3. Service接口模板

```java
package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.domain.entity.{实体类名};

/**
 * {实体中文名}服务接口
 *
 * @author CodeGenerator
 * @date {生成日期}
 */
public interface I{实体类名}Service extends IService<{实体类名}> {

    // 在这里定义业务方法

}
```

### 4. Service实现类模板

```java
package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.domain.entity.{实体类名};
import com.xiuxian.mapper.{实体类名}Mapper;
import com.xiuxian.service.I{实体类名}Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {实体中文名}服务实现类
 *
 * @author CodeGenerator
 * @date {生成日期}
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class {实体类名}ServiceImpl extends ServiceImpl<{实体类名}Mapper, {实体类名}>
        implements I{实体类名}Service {

    // 实现业务方法

}
```

### 5. Controller模板

```java
package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.service.I{实体类名}Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * {模块中文名}控制器
 *
 * @author CodeGenerator
 * @date {生成日期}
 */
@Slf4j
@RestController
@RequestMapping("/{路径}")
@RequiredArgsConstructor
@Tag(name = "{模块中文名}", description = "{模块描述}")
public class {控制器名}Controller {

    private final I{实体类名}Service {实体类名小写}Service;

    @Operation(summary = "{操作描述}")
    @PostMapping
    public Result<?> {方法名}(@Valid @RequestBody {RequestDTO} request) {
        // 实现业务逻辑
        return Result.success(data);
    }

}
```

---

## 实体类映射规则

### 数据库类型 → Java类型映射

| MySQL类型 | Java类型 | 说明 |
|-----------|---------|------|
| BIGINT | Long | 主键、大数值 |
| INT | Integer | 普通整数 |
| TINYINT | Integer | 小整数、布尔值 |
| SMALLINT | Integer | 小整数 |
| VARCHAR | String | 字符串 |
| TEXT | String | 长文本 |
| DECIMAL | BigDecimal | 精确小数 |
| TIMESTAMP | LocalDateTime | 时间戳 |
| DATETIME | LocalDateTime | 日期时间 |

### 命名转换规则

| 数据库命名 | Java命名 | 示例 |
|-----------|---------|------|
| snake_case | camelCase | character_id → characterId |
| 表名 | 首字母大写 | character → Character |
| 字段名 | 首字母小写 | player_name → playerName |

---

## 24个核心实体清单

| # | 表名 | 实体类名 | 中文名 | 优先级 |
|---|------|---------|--------|--------|
| 1 | character | Character | 角色 | P0 |
| 2 | realm | Realm | 境界配置 | P0 |
| 3 | pill | Pill | 丹药 | P1 |
| 4 | material | Material | 材料 | P1 |
| 5 | character_pill | CharacterPill | 角色丹药背包 | P1 |
| 6 | character_material | CharacterMaterial | 角色材料背包 | P1 |
| 7 | pill_recipe | PillRecipe | 丹方 | P1 |
| 8 | pill_recipe_material | PillRecipeMaterial | 丹方材料 | P1 |
| 9 | alchemy_record | AlchemyRecord | 炼丹记录 | P1 |
| 10 | skill | Skill | 技能 | P1 |
| 11 | character_skill | CharacterSkill | 角色已学技能 | P1 |
| 12 | equipment | Equipment | 装备 | P1 |
| 13 | character_equipment | CharacterEquipment | 角色已装备 | P1 |
| 14 | equipment_recipe | EquipmentRecipe | 装备图纸 | P1 |
| 15 | equipment_recipe_material | EquipmentRecipeMaterial | 装备图纸材料 | P1 |
| 16 | forge_record | ForgeRecord | 锻造记录 | P1 |
| 17 | monster | Monster | 妖兽 | P0 |
| 18 | monster_drop | MonsterDrop | 怪物掉落配置 | P1 |
| 19 | sect | Sect | 宗门 | P0 |
| 20 | combat_record | CombatRecord | 战斗记录 | P1 |
| 21 | cultivation_record | CultivationRecord | 修炼记录 | P1 |
| 22 | exploration_event | ExplorationEvent | 探索事件 | P1 |
| 23 | exploration_reward_config | ExplorationRewardConfig | 探索奖励配置 | P1 |
| 24 | exploration_event_reward | ExplorationEventReward | 探索事件奖励 | P1 |

---

## 代码生成工具配置

### MyBatis-Plus代码生成器

```java
package com.xiuxian.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class CodeGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/xiuxian", "root", "root")
                .globalConfig(builder -> {
                    builder.author("CodeGenerator")
                            .outputDir(System.getProperty("user.dir") + "/src/main/java")
                            .commentDate("yyyy-MM-dd");
                })
                .packageConfig(builder -> {
                    builder.parent("com.xiuxian")
                            .entity("domain.entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(
                                    OutputFile.xml,
                                    System.getProperty("user.dir") + "/src/main/resources/mapper"
                            ));
                })
                .strategyConfig(builder -> {
                    builder.addInclude("character", "realm", "pill", "material") // 需要生成的表
                            .entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .logicDeleteColumnName("deleted")
                            .addTableFills(
                                    new Column("created_at", FieldFill.INSERT),
                                    new Column("updated_at", FieldFill.INSERT_UPDATE)
                            )
                            .controllerBuilder()
                            .enableRestStyle()
                            .mapperBuilder()
                            .enableMapperAnnotation();
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
```

---

## 下一步开发建议

### 第一阶段：核心功能（1-2周）
1. ✅ 创建数据库并执行DDL
2. 生成24个实体类
3. 生成24个Mapper接口
4. 实现角色管理功能
5. 实现修炼系统
6. 实现战斗系统基础版

### 第二阶段：扩展功能（2-3周）
1. 实现炼丹系统
2. 实现装备锻造系统
3. 实现背包管理
4. 实现技能系统
5. 实现探索系统
6. 实现宗门系统

### 第三阶段：优化完善（1-2周）
1. 性能优化
2. 安全加固
3. 单元测试
4. 集成测试
5. API文档完善
6. 部署上线

---

## 质量检查清单

### 代码质量
- [ ] 所有类都有完整的JavaDoc注释
- [ ] 所有方法都有参数说明和返回值说明
- [ ] 异常处理完善
- [ ] 日志记录合理
- [ ] 代码格式统一

### 功能完整性
- [ ] 所有API接口都已实现
- [ ] 参数验证完整
- [ ] 错误处理完善
- [ ] 业务逻辑正确

### 测试覆盖
- [ ] Service层单元测试覆盖率 > 80%
- [ ] Controller层集成测试完整
- [ ] 关键业务流程有端到端测试

### 性能标准
- [ ] 查询接口响应时间 < 100ms
- [ ] 写入接口响应时间 < 200ms
- [ ] 支持并发用户数 > 1000

---

## Summary

本代码生成指南提供了：

✅ **生成顺序**: 从基础到业务的清晰生成路径
✅ **代码模板**: Entity/Mapper/Service/Controller完整模板
✅ **映射规则**: 数据库类型到Java类型的映射规范
✅ **实体清单**: 24个核心实体的优先级划分
✅ **工具配置**: MyBatis-Plus代码生成器配置示例
✅ **开发计划**: 分三个阶段的实施建议
✅ **质量标准**: 代码质量和测试覆盖的检查清单

所有设计文档已完成，可以开始进入代码实现阶段！
