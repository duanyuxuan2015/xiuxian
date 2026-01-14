# Implementation Plan: 凡人修仙文字游戏

**Branch**: `001-xiuxian-docs` | **Date**: 2026-01-13 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-xiuxian-docs/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

开发一款基于Web的文字修仙游戏,玩家通过文字界面创建角色、修炼功法、探索世界、炼丹炼器、战斗妖兽、加入宗门,最终从凡人修炼到道祖之境。核心功能包括角色创建、修炼系统、境界突破、打坐恢复、探索系统、回合制战斗(手动/自动/挂机三种模式)、炼丹系统、炼器系统、技能系统、装备管理、宗门系统。技术栈采用Java + Spring Boot + MyBatis-Plus,MySQL数据库,支持10个并发用户,所有操作3秒内响应,定时自动保存(30-60秒)。

## Technical Context

**Language/Version**: Java 17+
**Primary Dependencies**: Spring Boot 3.x, MyBatis-Plus 3.x, MySQL 8.x, Lombok, SLF4J+Logback
**Storage**: MySQL 8.x数据库(单角色<100KB,总数据<1MB for 10并发用户)
**Testing**: JUnit 5, Spring Boot Test, MyBatis-Plus Test
**Target Platform**: 本地开发环境(开发者电脑),支持浏览器访问(桌面端)
**Project Type**: Web应用(Spring Boot后端 + 纯文字前端界面)
**Performance Goals**: 所有操作3秒内响应(NFR-001),定时自动保存30-60秒(NFR-003)
**Constraints**:
- 仅支持简体中文(Out of Scope)
- 纯文字界面,无图形渲染(Out of Scope)
- MVP版本仅单机体验,无多人在线(Out of Scope)
- 基本输入验证(NFR-007)
- 本地部署,无需云端或容器化(Assumption #18)
**Scale/Scope**:
- 小规模部署:最多10个并发用户(NFR-002)
- 数据量:单角色<100KB,总计<1MB(Assumption #17)
- 游戏内容:13个大境界,每个9层,完整体验数十到数百小时(Assumption #7)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### I. 用户故事优先 (User-Story-First)

**Status**: ✅ PASS

**Validation**:
- ✅ 所有功能基于10个可测试的用户故事定义(P1-P10优先级)
- ✅ 每个用户故事分配明确优先级(P1-P10)
- ✅ 每个用户故事可独立开发、测试和部署
- ✅ MVP通过实现P1-P4用户故事交付(角色创建、打坐恢复、探索、战斗系统)
- ✅ 避免功能蔓延 - Out of Scope章节明确排除多人在线、图形界面、实时战斗、付费功能等非MVP功能

**Evidence**: 规范包含10个优先级用户故事,共171个验收场景,MVP聚焦于P1-P4核心功能

### II. 测试驱动 (Test-Driven)

**Status**: ✅ PASS

**Validation**:
- ✅ 所有171个验收场景使用Given-When-Then格式,可转换为测试用例
- ✅ 关键路径覆盖:修炼、突破、战斗、炼丹、炼器均有完整验收场景
- ⚠️  注意:规范未明确要求TDD流程,但验收场景完整可支持测试驱动开发

**Evidence**: 171个Given-When-Then格式验收场景,可转换为自动化测试

### III. 简单优先 (Simplicity-First)

**Status**: ✅ PASS

**Validation**:
- ✅ 技术栈简单:Java + Spring Boot + MyBatis-Plus(成熟稳定,社区支持好)
- ✅ 数据库简单:单库MySQL(无分布式、无缓存层、无消息队列)
- ✅ 部署简单:本地开发环境,无需容器化或云端部署
- ✅ 界面简单:纯文字界面,无图形渲染
- ✅ 架构简单:单体Web应用(前后端不分离)

**Evidence**: 技术选型遵循YAGNI原则,无过度设计

### IV. 合同优先 (Contract-First)

**Status**: ✅ PASS

**Validation**:
- ✅ 41个功能需求(FR-001至FR-041)明确定义系统行为
- ✅ 7个非功能性需求(NFR-001至NFR-007)明确定义质量约束
- ✅ 数据模型完整:定义了角色、五维属性、境界、物品、技能、装备、妖兽、宗门等核心实体
- ✅ API接口将在Phase 1通过contracts/明确定义

**Evidence**: 功能需求、非功能性需求、数据实体均有明确定义

### V. 可追溯性 (Traceability)

**Status**: ✅ PASS

**Validation**:
- ✅ 10个用户故事映射到41个功能需求
- ✅ 41个功能需求映射到171个验收场景
- ✅ 142个边缘情况覆盖边界条件和失败处理
- ✅ 每个用户故事包含优先级(P1-P10)、独立测试标准、验收场景

**Evidence**: 需求→用户故事→验收场景→边缘情况的完整追溯链

### 章程检查结果

**Overall Status**: ✅ **ALL GATES PASSED**

**Notes**:
- 所有5项核心原则均符合章程要求
- 无需要 justify 的违规项
- Complexity Tracking 表格不适用(无违规)

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
xiuxian/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── xiuxian/
│   │   │           ├── XiuxianApplication.java      # Spring Boot主入口
│   │   │           ├── config/                      # 配置类
│   │   │           │   ├── MyBatisPlusConfig.java
│   │   │           │   └── DatabaseConfig.java
│   │   │           ├── controller/                  # REST控制器
│   │   │           │   ├── CharacterController.java
│   │   │           │   ├── CultivationController.java
│   │   │           │   ├── CombatController.java
│   │   │           │   ├── ExplorationController.java
│   │   │           │   ├── AlchemyController.java
│   │   │           │   ├── SmithingController.java
│   │   │           │   ├── SkillController.java
│   │   │           │   ├── EquipmentController.java
│   │   │           │   └── SectController.java
│   │   │           ├── service/                     # 业务逻辑层
│   │   │           │   ├── CharacterService.java
│   │   │           │   ├── CultivationService.java
│   │   │           │   ├── CombatService.java
│   │   │           │   ├── ExplorationService.java
│   │   │           │   ├── AlchemyService.java
│   │   │           │   ├── SmithingService.java
│   │   │           │   ├── SkillService.java
│   │   │           │   ├── EquipmentService.java
│   │   │           │   └── SectService.java
│   │   │           ├── mapper/                      # MyBatis-Plus数据访问层
│   │   │           │   ├── CharacterMapper.java
│   │   │           │   ├── CultivationMapper.java
│   │   │           │   ├── ItemMapper.java
│   │   │           │   ├── SkillMapper.java
│   │   │           │   ├── EquipmentMapper.java
│   │   │           │   └── MonsterMapper.java
│   │   │           ├── entity/                      # JPA实体类
│   │   │           │   ├── Character.java
│   │   │           │   ├── CultivationLevel.java
│   │   │           │   ├── Item.java
│   │   │           │   ├── Skill.java
│   │   │           │   ├── Equipment.java
│   │   │           │   ├── Monster.java
│   │   │           │   └── Sect.java
│   │   │           ├── dto/                        # 数据传输对象
│   │   │           │   ├── request/
│   │   │           │   └── response/
│   │   │           ├── enums/                      # 枚举类
│   │   │           │   ├── Realm.java               # 境界枚举
│   │   │           │   ├── ItemType.java            # 物品类型枚举
│   │   │           │   ├── SkillType.java           # 技能类型枚举
│   │   │           │   └── EquipmentQuality.java    # 装备品质枚举
│   │   │           └── util/                       # 工具类
│   │   │       ├── DamageCalculator.java
│   │   │       └── ExperienceCalculator.java
│   │   └── resources/
│   │       ├── application.yml                     # Spring Boot配置
│   │       ├── logback-spring.xml                   # 日志配置
│   │       └── db/
│   │           └── migration/                       # 数据库迁移脚本
│   │               └── V1__init_schema.sql
│   └── test/
│       └── java/
│           └── com/
│               └── xiuxian/
│                   ├── controller/                  # 控制器单元测试
│                   ├── service/                     # 服务层单元测试
│                   └── mapper/                      # 数据访问层集成测试
├── static/                                         # 静态资源(纯文字HTML界面)
│   ├── index.html
│   ├── css/
│   └── js/
└── pom.xml                                         # Maven配置文件
```

**Structure Decision**: 选择标准Spring Boot项目结构(Web应用模式),单体架构,前后端不分离,使用MyBatis-Plus作为ORM框架,遵循经典分层架构(Controller → Service → Mapper → Entity)。

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

不适用 - 章程检查无违规项,无需复杂性追踪表。

---

## Phase 0: Research & Technology Decisions

**Status**: ✅ COMPLETE

**Overview**: 由于规范已通过3轮澄清(共10个问题),所有关键技术决策已明确,无需额外研究。

### Resolved Decisions

1. **技术栈**: Java 17 + Spring Boot 3.x + MyBatis-Plus 3.x + MySQL 8.x(已明确)
2. **部署环境**: 本地开发环境(已明确)
3. **性能目标**: 所有操作3秒内响应(已明确)
4. **数据规模**: 单角色<100KB,总计<1MB(已明确)
5. **测试框架**: JUnit 5 + Spring Boot Test(已明确)
6. **日志记录**: 关键操作日志(境界突破、炼丹炼器、战斗胜负、重要物品获取)
7. **数据持久化**: 定时自动保存30-60秒,关键节点立即保存
8. **安全策略**: 基本输入验证(姓名长度2-6字符,菜单选择验证)
9. **项目边界**: 明确排除多人在线、图形界面、实时战斗、付费功能、移动端支持等

### Research Artifacts

**研究文档**: [research.md](./research.md)

由于所有关键决策已通过规范澄清明确,Phase 0 研究阶段标记为完成。research.md 将总结这些已明确的技术决策。

---

## Phase 1: Design & Contracts

**Status**: ✅ COMPLETE

**Overview**: Phase 1 完成数据模型设计、API合约定义、快速入门指南和 agent context 更新。

### Deliverables

1. **Data Model** → [data-model.md](./data-model.md)
   - 定义12个核心实体(Character, Item, CharacterItem, Skill, CharacterSkill, Equipment, CharacterEquipment, Monster, Sect, CombatRecord, CultivationRecord, ExplorationEvent)
   - 完整的字段定义、验证规则、关系映射
   - 数据库索引策略、外键约束
   - ERD关系图
   - 数据迁移策略

2. **Quickstart Guide** → [quickstart.md](./quickstart.md)
   - 环境要求(JDK 17+, Maven 3.8+, MySQL 8.x)
   - 数据库设置步骤
   - 项目搭建流程
   - 运行和测试指南
   - 常见问题解决方案

3. **Agent Context Update** → ✅ COMPLETED
   - 运行 `update-agent-context.ps1` 更新 Claude Code context
   - 添加技术栈信息: Java 17+, Spring Boot 3.x, MyBatis-Plus 3.x, MySQL 8.x
   - 更新项目类型: Web应用

4. **API Contracts** → ⏭️ SKIPPED
   - 原因: 规范已明确定义41个功能需求(FR-001至FR-041)和171个验收场景
   - API设计将在任务分解阶段(/speckit.tasks)根据具体Controller实现时定义
   - 使用标准REST模式: GET/POST/PUT/DELETE, 返回统一JSON格式

### Design Decisions

1. **数据模型设计**: 12个核心实体,MyBatis-Plus ORM,逻辑删除策略
2. **数据库设计**: MySQL 8.x, utf8mb4字符集,支持中文
3. **项目结构**: 标准Spring Boot分层架构(Controller → Service → Mapper → Entity)
4. **API设计**: RESTful API,统一JSON响应格式
5. **测试策略**: JUnit 5 + Spring Boot Test,分层测试(Controller/Service/Mapper)

---

## Phase 2: Task Breakdown

**Status**: ⏭️ PENDING

**Overview**: Phase 2 将通过 `/speckit.tasks` 命令执行任务分解,将10个用户故事(P1-P10)分解为具体的开发任务。

**Next Command**: `/speckit.tasks`

**Expected Output**: tasks.md
- 按用户故事分组的任务列表
- 任务优先级(P1-P10)
- 任务依赖关系
- 任务验收标准

---

## Summary

### Plan Status

**Phase 0 (Research)**: ✅ COMPLETE
- 所有技术决策已通过规范澄清明确
- research.md 已生成

**Phase 1 (Design)**: ✅ COMPLETE
- data-model.md 已生成(12个核心实体)
- quickstart.md 已生成(环境搭建、运行指南)
- agent context 已更新
- API contracts: 跳过(将在任务分解阶段定义)

**Phase 2 (Tasks)**: ⏭️ PENDING
- 等待 `/speckit.tasks` 命令执行

### Artifacts Generated

1. ✅ [research.md](./research.md) - 技术决策总结
2. ✅ [data-model.md](./data-model.md) - 数据模型设计
3. ✅ [quickstart.md](./quickstart.md) - 快速入门指南
4. ✅ [plan.md](./plan.md) - 本实现计划文档

### Next Steps

1. **Immediate**: `/speckit.tasks` - 分解用户故事为开发任务
2. **After Tasks**: 开始实现MVP (P1-P4用户故事)
   - P1: 角色创建与基础修炼
   - P2: 打坐恢复系统
   - P3: 探索系统
   - P4: 战斗系统

### Project Metrics

- **Lines of Spec**: ~950 lines
- **User Stories**: 10 (P1-P10)
- **Functional Requirements**: 41 (FR-001至FR-041)
- **Non-Functional Requirements**: 7 (NFR-001至NFR-007)
- **Acceptance Scenarios**: 171
- **Edge Cases**: 142
- **Core Entities**: 12
- **Constitution Checks**: 5/5 PASSED
- **Clarification Questions**: 10 (All Resolved)

---

**Implementation Plan Complete!** ✅
