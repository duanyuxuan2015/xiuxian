<!--
Sync Impact Report
==================
Version change: [INITIAL] → 1.0.0
List of modified principles: N/A (initial version)
Added sections:
  - Core Principles (5 principles defined)
  - Development Workflow
  - Quality Gates
  - Governance
Removed sections: N/A
Templates requiring updates:
  ✅ plan-template.md - Constitution Check section aligns with principles
  ✅ spec-template.md - Requirements alignment verified
  ✅ tasks-template.md - Task organization reflects principles
  ✅ All command files - Generic agent guidance verified
Follow-up TODOs: None
-->

# 修仙项目 (Xiuxian Project) Constitution

## Core Principles

### I. 用户故事优先 (User-Story-First)

每个功能必须从用户故事出发,优先级明确,可独立测试和交付:
- 所有功能必须基于可测试的用户故事定义
- 用户故事必须分配优先级(P1/P2/P3)
- 每个用户故事必须可独立开发、测试和部署
- MVP(最小可行产品)通过实现 P1 用户故事交付
- 避免功能蔓延 - 只实现明确请求的功能

**理由**: 确保开发始终聚焦于用户价值,避免过度工程化,并支持增量交付。

### II. 测试驱动 (Test-Driven)

测试必须在实现之前编写并验证失败(TDD 方法):
- 对于合同测试、集成测试:必须先编写测试,确保测试失败,然后实现
- 红-绿-重构周期严格执行
- 测试覆盖用户故事的关键路径
- 只有在规范中明确请求时才包含测试

**理由**: TDD 确保代码行为正确,防止回归,并作为可执行的规范。

### III. 简单优先 (Simplicity-First)

从最简单的解决方案开始,避免过度设计:
- 遵循 YAGNI(You Aren't Gonna Need It)原则
- 只为当前需求设计,不为假设的未来需求设计
- 避免不必要的抽象层和设计模式
- 3 行相似代码优于过早的抽象
- 复杂性必须通过章程检查证明合理

**理由**: 简单的代码更易维护、调试和扩展。复杂性应仅在实际需要时引入。

### IV. 合同优先 (Contract-First)

公共接口必须通过合同/规范明确定义:
- API 端点、服务接口和数据模型必须有明确的规范
- 合同在实现之前生成
- 合同变更需要更新测试和文档
- 使用标准模式(REST、GraphQL 等)

**理由**: 明确的合同确保组件间清晰的边界,支持并行开发和独立测试。

### V. 可追溯性 (Traceability)

每个实现元素必须可追溯到需求:
- 任务必须映射到用户故事(US1、US2、US3 等)
- 用户故事必须映射到功能需求
- 代码变更必须引用任务 ID
- 测试必须验证特定的接受标准

**理由**: 可追溯性确保每个代码变更都有明确的业务理由,并支持影响分析。

## Development Workflow

### 规范优先 (Specification-First)

所有功能工作流必须遵循以下阶段:

1. **Specification Phase** (`/speckit.specify`):
   - 从用户描述创建功能规范
   - 定义优先用户故事(P1、P2、P3)
   - 记录功能需求和成功标准

2. **Planning Phase** (`/speckit.plan`):
   - 研究技术决策
   - 设计数据模型和合同
   - 创建快速入门指南
   - 验证章程合规性

3. **Task Generation** (`/speckit.tasks`):
   - 按用户故事分解实现任务
   - 组织任务以支持并行执行
   - 定义依赖和执行顺序

4. **Checklist Creation** (`/speckit.checklist`):
   - 生成特定领域的检查清单
   - 根据领域需求验证质量标准

5. **Implementation** (`/speckit.implement`):
   - 验证所有检查清单已完成
   - 按阶段执行任务
   - 遵循 TDD 工作流
   - 标记已完成的任务

6. **Analysis** (`/speckit.analyze`):
   - 跨工件一致性检查
   - 质量验证

### 增量交付 (Incremental Delivery)

功能按优先级增量交付:
- **MVP**:完成设置 + 基础 + P1 用户故事
- **增量**:添加 P2 用户故事,独立测试和部署
- **扩展**:添加 P3 用户故事,保持先前功能正常工作

## Quality Gates

### 章程检查 (Constitution Check)

每个实现计划必须通过章程检查:
- 列出适用的原则和验证条件
- 识别违规并提供理由
- 未证明合理的违规视为错误

### 检查清单验证 (Checklist Validation)

实现前必须验证领域特定检查清单:
- **用户**:检查清单中所有项目标记为完成 ✓
- **代理**:实现前显示检查清单状态
- **决策**:用户可选择在检查清单未完成时继续,但会收到警告

### 复杂性追踪 (Complexity Tracking)

对于任何章程违规:
- 在实施计划中记录复杂性表格
- 记录违规内容、所需原因和被拒绝的简单替代方案
- 确保复杂性是有意的且必要的

## Governance

### 修订流程 (Amendment Process)

1. **提案**:通过运行 `/speckit.constitution` 提出修订
2. **文档记录**:使用语义版本记录变更(MAJOR/MINOR/PATCH)
3. **传播**:更新所有依赖模板以保持一致性
4. **批准**:由项目负责人审查

### 版本控制 (Versioning)

- **MAJOR**:向后不兼容的原则移除或重新定义
- **MINOR**:新原则或添加实质性扩展指导
- **PATCH**:澄清、措辞、拼写错误修复、非语义优化

### 合规审查 (Compliance Review)

- 所有代码审查必须验证章程合规性
- 章程检查必须在计划阶段和实施前进行
- 复杂性必须合理且记录
- 使用运行时指导文件进行特定开发决策

**Version**: 1.0.0 | **Ratified**: 2026-01-13 | **Last Amended**: 2026-01-13
