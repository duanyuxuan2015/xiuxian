# Research & Technology Decisions

**Feature**: 凡人修仙文字游戏
**Date**: 2026-01-13
**Status**: Complete

## Overview

本文档总结凡人修仙文字游戏实现的关键技术决策和最佳实践。由于规范已通过3轮澄清(共10个问题),所有关键技术决策已在规范阶段明确,Phase 0 研究阶段主要任务是记录这些决策及其理由。

---

## 1. 技术栈决策

### Decision: Java 17 + Spring Boot 3.x + MyBatis-Plus 3.x

**Rationale**:
- **Java 17**: LTS版本,稳定可靠,长期支持到2029年,适合企业级应用开发
- **Spring Boot 3.x**: 最新稳定版本,提供自动配置、内嵌服务器、快速开发能力,符合Java生态最佳实践
- **MyBatis-Plus 3.x**: MyBatis的增强工具,简化CRUD操作,提供代码生成、分页、性能分析等功能,大幅提升开发效率
- **MySQL 8.x**: 成熟可靠的关系型数据库,ACID事务支持,满足数据完整性要求(NFR-004),符合规范Assumption #6

**Alternatives Considered**:
- **Spring Data JPA**: 考虑过但未选择,原因:复杂查询灵活性不如MyBatis-Plus,学习曲线较陡
- **PostgreSQL**: 考虑过但未选择,原因:MySQL在小型应用中更常见,社区资源更丰富
- **NoSQL数据库(MongoDB)**: 考虑过但未选择,原因:游戏数据关系复杂,需要强事务支持

**Best Practices**:
- 使用Lombok减少样板代码
- 使用Spring Boot Actuator进行健康检查和监控
- 使用MyBatis-Plus代码生成器生成基础CRUD
- 使用HikariCP连接池(默认)提升数据库性能

---

## 2. 部署架构决策

### Decision: 本地开发环境部署

**Rationale**:
- 符合规范Assumption #18:"MVP版本部署在本地开发环境(开发者电脑上运行)"
- 适合单人开发或小团队,降低基础设施成本
- 无需容器化或云端部署配置,简化开发流程
- 满足10个并发用户需求(NFR-002)

**Alternatives Considered**:
- **Docker容器化**: 考虑过但未选择,原因:MVP阶段增加不必要的复杂性
- **云端部署(AWS/阿里云)**: 考虑过但未选择,原因:本地部署已满足需求,节省成本
- **Kubernetes**: 考虑过但未选择,原因:过度设计,MVP不需要

**Best Practices**:
- 使用Spring Boot内嵌Tomcat服务器(默认)
- 使用Spring Profiles区分开发/测试/生产环境配置
- 使用Maven Wrapper确保构建一致性

---

## 3. 性能优化策略

### Decision: 3秒响应时间 + 定时自动保存(30-60秒)

**Rationale**:
- 符合NFR-001:"所有用户操作的反馈必须在3秒内显示"
- 符合NFR-003:"定时自动保存游戏进度(每30-60秒)"
- 文字游戏特点:计算密集度低,IO密集度中等,3秒响应时间容易满足
- 定时保存平衡性能和数据安全

**Alternatives Considered**:
- **实时保存(每次操作后立即保存)**: 考虑过但未选择,原因:可能影响性能,数据库写入频繁
- **每5秒自动保存**: 考虑过但未选择,原因:保存频率过高,性能开销大
- **仅手动保存**: 考虑过但未选择,原因:用户体验差,容易丢失进度

**Best Practices**:
- 使用Spring `@Scheduled`注解实现定时任务
- 使用Spring `@Transactional`确保事务一致性
- 使用异步日志(Appender)避免IO阻塞
- 数据库索引优化:在character_id、realm、item_type等字段建立索引

---

## 4. 数据库设计策略

### Decision: 单库MySQL + 关系型设计

**Rationale**:
- 符合规范Assumption #6:"使用MySQL数据库存储所有游戏数据"
- 单角色数据量<100KB(Assumption #17),总数据<1MB,单库完全满足需求
- 关系型设计适合游戏实体关系(角色-装备-技能-宗门)
- 支持ACID事务,满足NFR-004数据完整性要求

**Alternatives Considered**:
- **分库分表**: 考虑过但未选择,原因:数据量小(1MB),无需分库分表
- **读写分离**: 考虑过但未选择,原因:10并发用户,读压力小,无需读写分离
- **缓存层(Redis)**: 考虑过但未选择,原因:增加复杂性,MySQL性能足够

**Best Practices**:
- 使用MyBatis-Plus代码生成器生成Entity、Mapper、Service
- 使用逻辑删除(而非物理删除)保留数据历史
- 使用雪花算法或UUID作为主键,避免分布式环境主键冲突
- 使用数据库触发器记录关键操作日志(NFR-006)

---

## 5. 测试策略

### Decision: JUnit 5 + Spring Boot Test + MyBatis-Plus Test

**Rationale**:
- JUnit 5是Java生态标准测试框架
- Spring Boot Test提供集成测试支持(@SpringBootTest)
- MyBatis-Plus Test提供数据访问层测试支持
- 符合规范中171个Given-When-Then验收场景的测试需求

**Alternatives Considered**:
- **TestNG**: 考虑过但未选择,原因:JUnit 5更现代,Spring Boot集成更好
- **Cucumber**: 考虑过但未选择,原因:增加学习成本,Given-When-Then格式已在规范中,可直接用JUnit实现
- **Spock框架**: 考虑过但未选择,原因:基于Groovy,团队可能不熟悉

**Best Practices**:
- 测试覆盖率目标:Service层≥80%,Controller层≥70%,Mapper层≥90%
- 使用Mockito进行单元测试(Service层)
- 使用H2内存数据库进行集成测试
- 使用@DataJpaTest或@MybatisTest进行数据层测试

---

## 6. 日志与监控策略

### Decision: SLF4J + Logback + 关键操作日志

**Rationale**:
- 符合NFR-006:"系统必须记录关键操作日志"
- SLF4J是Java日志门面标准,Logback是默认实现
- 关键操作日志:境界突破、炼丹炼器结果、战斗胜负、重要物品获取
- 支持日志级别:ERROR、WARN、INFO、DEBUG

**Alternatives Considered**:
- **Log4j2**: 考虑过但未选择,原因:Logback是Spring Boot默认,配置更简单
- **完整操作日志**: 考虑过但未选择,原因:规范明确要求仅关键操作日志(已澄清)

**Best Practices**:
- 使用结构化日志(JSON格式),便于日志分析
- 使用MDC(Mapped Diagnostic Context)记录请求追踪ID
- 使用日志滚动策略(TimeBasedRollingPolicy),按天滚动,保留30天
- 敏感信息脱敏(如密码、个人信息)

---

## 7. 安全策略

### Decision: 基本输入验证 + Spring Security(可选)

**Rationale**:
- 符合NFR-007:"系统必须对所有用户输入进行基本格式验证"
- 角色姓名验证:长度2-6字符(规范FR-001)
- 菜单选择验证:有效性检查,防止无效输入
- MVP版本单机体验,无多人在线,无需复杂权限管理(Out of Scope)

**Alternatives Considered**:
- **Spring Security**: 考虑过但未选择,原因:MVP单机版本,无需认证授权,增加复杂性
- **高级安全功能**: 考虑过但未选择,原因:Out of Scope明确排除"高级安全功能:防作弊系统、加密通信、复杂的权限管理"

**Best Practices**:
- 使用Bean Validation(JSR-380)进行输入验证(@NotNull, @Size, @Pattern)
- 使用Spring `@Validated`进行参数校验
- 使用MyBatis-Plus内置防SQL注入(参数化查询)
- 前端输入清理(防止XSS):使用Spring Boot默认的XSS防护

---

## 8. 项目结构决策

### Decision: 标准Spring Boot分层架构

**Rationale**:
- Controller层:处理HTTP请求,参数验证,调用Service
- Service层:业务逻辑,事务管理,调用Mapper
- Mapper层:数据访问,SQL执行,映射到Entity
- Entity层:数据模型,对应数据库表

**Alternatives Considered**:
- **DDD架构**: 考虑过但未选择,原因:业务相对简单,分层架构已足够,DDD增加复杂性
- **微服务架构**: 考虑过但未选择,原因:MVP阶段,单应用满足需求,无需拆分服务

**Best Practices**:
- 单一职责原则:每层职责明确,避免跨层调用
- 依赖注入:使用Spring @Autowired或构造函数注入
- 事务管理:使用Spring @Transactional,默认在Service层
- DTO转换:使用MapStruct或手动转换Entity ↔ DTO

---

## 9. 前端界面策略

### Decision: 纯文字HTML界面

**Rationale**:
- 符合规范Out of Scope:"图形界面:纯文字界面,不包含2D/3D图形渲染、动画效果、视觉特效"
- 符合Assumption #15:"纯文字界面,通过ASCII艺术或简单排版增强视觉体验"
- 降低开发成本,聚焦游戏逻辑
- 支持浏览器访问(桌面端)

**Alternatives Considered**:
- **Vue.js/React前端**: 考虑过但未选择,原因:Out of Scope明确排除图形界面
- **命令行界面(CLI)**: 考虑过但未选择,原因:浏览器访问更友好,支持跨平台

**Best Practices**:
- 使用语义化HTML标签
- 使用CSS进行简单排版(字体、颜色、间距)
- 使用JavaScript进行AJAX交互(调用REST API)
- 使用WebSocket(可选):用于挂机战斗实时推送结果

---

## 10. 数据持久化策略

### Decision: MySQL事务 + 定时自动保存(30-60秒) + 关键节点立即保存

**Rationale**:
- 符合NFR-003和NFR-004:定时自动保存 + 关键节点立即保存 + MySQL事务
- 定时保存:使用Spring @Scheduled定时任务,每30-60秒保存一次
- 关键节点立即保存:境界突破、获得重要物品时立即保存
- 事务保证:使用Spring @Transactional,确保数据一致性

**Alternatives Considered**:
- **仅依赖事务,无定时保存**: 考虑过但未选择,原因:用户可能丢失进度,体验差
- **每操作后立即保存**: 考虑过但未选择,原因:性能开销大,数据库写入频繁

**Best Practices**:
- 使用Spring `@Transactional(rollbackFor = Exception.class)`确保异常回滚
- 使用乐观锁(Version字段)防止并发更新冲突
- 使用数据库连接池(HikariCP)管理连接
- 定时保存失败时记录日志,避免影响用户体验

---

## Summary

所有关键技术决策已通过规范澄清明确,Phase 0 研究阶段完成。主要决策包括:

1. ✅ 技术栈:Java 17 + Spring Boot 3.x + MyBatis-Plus 3.x + MySQL 8.x
2. ✅ 部署:本地开发环境,无需容器化
3. ✅ 性能:3秒响应,定时保存30-60秒
4. ✅ 数据库:单库MySQL,关系型设计,数据量<1MB
5. ✅ 测试:JUnit 5 + Spring Boot Test
6. ✅ 日志:SLF4J + Logback,关键操作日志
7. ✅ 安全:基本输入验证,无复杂权限
8. ✅ 架构:标准Spring Boot分层架构
9. ✅ 前端:纯文字HTML界面
10. ✅ 持久化:MySQL事务 + 定时保存 + 关键节点立即保存

**下一步**: Phase 1 - 生成data-model.md、contracts/、quickstart.md
