# Data Model

**Feature**: 凡人修仙文字游戏
**Date**: 2026-01-13
**Status**: Draft

## Overview

本文档定义凡人修仙文字游戏的核心数据模型,包括所有实体、属性、关系、验证规则和状态转换。数据模型基于功能需求(FR-001至FR-041)和用户故事(P1-P10)提炼。

**Database**: MySQL 8.x
**ORM**: MyBatis-Plus 3.x
**Naming Convention**: snake_case (数据库字段), camelCase (Java实体)

---

## Entity Definitions

### 1. Character (角色)

**Table**: `character`

**Description**: 玩家角色实体,记录角色的所有属性、状态、进度

**Primary Key**: `character_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| character_id | BIGINT | NO | 角色唯一标识 | PK, Auto-Increment | - |
| player_name | VARCHAR(20) | NO | 角色姓名 | Length 2-6字符(FR-001) | - |
| realm_id | INT | NO | 当前境界ID | FK → realm.id | 1 |
| realm_level | TINYINT | NO | 当前境界层次(1-9) | Min 1, Max 9 | 1 |
| experience | BIGINT | NO | 当前经验值 | >= 0 | 0 |
| available_points | SMALLINT | NO | 可用加点点数 | >= 0 | 0 |
| spiritual_power | INT | NO | 当前灵气值 | >= 0, <= spiritual_power_max | 0 |
| spiritual_power_max | INT | NO | 灵气上限 | > 0 | 100 |
| stamina | SMALLINT | NO | 当前体力 | >= 0, <= stamina_max | 100 |
| stamina_max | SMALLINT | NO | 体力上限 | >= 100 | 100 |
| health | INT | NO | 当前气血值 | >= 0, <= health_max | 100 |
| health_max | INT | NO | 气血上限 | > 0 | 100 |
| mindset | SMALLINT | NO | 当前心境值 | >= 0, <= 100 | 50 |
| constitution | INT | NO | 体质属性 | Range 1-100000 | 5 |
| spirit | INT | NO | 精神属性 | Range 1-100000 | 5 |
| comprehension | INT | NO | 悟性属性 | Range 1-100000 | 5 |
| luck | INT | NO | 机缘属性 | Range 1-100000 | 5 |
| fortune | INT | NO | 气运属性 | Range 1-100000 | 5 |
| sect_id | BIGINT | YES | 所属宗门ID | FK → sect.sect_id | NULL |
| sect_position | VARCHAR(20) | YES | 宗门职位 | Enum(外门弟子, 内门弟子, 长老, 宗主) | NULL |
| contribution | INT | NO | 宗门贡献度 | >= 0 | 0 |
| reputation | INT | NO | 宗门声望 | >= 0 | 0 |
| current_state | VARCHAR(20) | NO | 当前状态 | Enum(修炼中, 探索中, 战斗中, 打坐中, 炼丹中, 炼器中, 闲置) | '闲置' |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| deleted | TINYINT | NO | 逻辑删除标记 | 0或1 | 0 |

**Indexes**:
- `idx_player_name` ON (`player_name`) - 支持按姓名查询
- `idx_realm_id` ON (`realm_id`) - 支持按境界筛选
- `idx_sect_id` ON (`sect_id`) - 支持宗门成员查询

**Relationships**:
- Many-to-One: `character` ↔ `realm` (当前境界)
- One-to-Many: `character` ↔ `character_item` (背包物品)
- One-to-Many: `character` ↔ `character_equipment` (已装备装备)
- One-to-Many: `character` ↔ `character_skill` (已学技能)
- One-to-Many: `character` ↔ `cultivation_record` (修炼记录)
- One-to-Many: `character` ↔ `combat_record` (战斗记录)
- Many-to-One: `character` ↔ `sect` (所属宗门)

**State Transitions**:
- `current_state`: 闲置 → 修炼中 → 闲置 (修炼完成)
- `current_state`: 闲置 → 探索中 → 闲置 (探索完成)
- `current_state`: 闲置 → 战斗中 → 闲置 (战斗结束)
- `current_state`: 闲置 → 打坐中 → 闲置 (打坐完成)
- `realm_level`: 1 → 2 → ... → 9 (自动升级,经验达标)
- `realm`: 炼气期9层 → 筑基期 (突破,需要满足条件)

---

### 2. Realm (境界配置)

**Table**: `realm`

**Description**: 境界配置表,定义修仙境界等级、突破要求、属性加成

**Primary Key**: `id` (INT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| id | INT | NO | 境界ID | PK, Auto-Increment | - |
| realm_name | VARCHAR(50) | NO | 境界名称 | Not Blank | - |
| realm_level | INT | NO | 境界等级(排序) | >= 1, Unique | - |
| sub_levels | INT | NO | 子层数量 | >= 1 | 9 |
| required_exp | BIGINT | NO | 突破所需经验 | >= 0 | 100 |
| breakthrough_rate | INT | NO | 基础突破成功率(%) | Range 0-100 | 50 |
| hp_bonus | INT | NO | 生命值加成 | >= 0 | 0 |
| sp_bonus | INT | NO | 灵力加成 | >= 0 | 0 |
| stamina_bonus | INT | NO | 体力上限加成 | >= 0 | 0 |
| attack_bonus | INT | NO | 攻击力加成 | >= 0 | 0 |
| defense_bonus | INT | NO | 防御力加成 | >= 0 | 0 |
| lifespan_bonus | INT | NO | 寿命加成(年) | >= 0 | 0 |
| level_up_points | INT | NO | 每层提升获得属性点数 | >= 0 | 5 |
| realm_stage | VARCHAR(20) | NO | 阶段 | Enum(MORTAL, IMMORTAL) | MORTAL |
| description | TEXT | YES | 境界描述 | - | NULL |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

**Indexes**:
- `uk_realm_level` UNIQUE (`realm_level`) - 境界等级唯一
- `idx_realm_stage` ON (`realm_stage`) - 按阶段筛选

**Relationships**:
- One-to-Many: `realm` ↔ `character` (该境界的所有角色)
- One-to-Many: `realm` ↔ `monster` (该境界的所有妖兽)

**Validation Rules**:
- `realm_level`必须唯一且连续(1, 2, 3, ...)
- `sub_levels`通常为9(每个境界9层)
- `breakthrough_rate`随境界等级提升而降低(越高级突破越难)
- `level_up_points`随境界等级提升而增加(高级境界升层奖励更多属性点)
- `realm_stage`: MORTAL(凡人境界1-9), IMMORTAL(仙人境界10+)

**Example Records**:
- 凡人(realm_level=1, sub_levels=9, required_exp=0, breakthrough_rate=100, level_up_points=5, realm_stage=MORTAL)
- 炼气期(realm_level=2, sub_levels=9, required_exp=100, breakthrough_rate=90, hp_bonus=10, level_up_points=5, realm_stage=MORTAL)
- 筑基期(realm_level=3, sub_levels=9, required_exp=1000, breakthrough_rate=70, hp_bonus=50, sp_bonus=20, level_up_points=8, realm_stage=MORTAL)
- 结丹期(realm_level=4, sub_levels=9, required_exp=10000, breakthrough_rate=50, hp_bonus=200, sp_bonus=100, attack_bonus=30, level_up_points=10, realm_stage=MORTAL)
- 元婴期(realm_level=5, sub_levels=9, required_exp=100000, breakthrough_rate=30, hp_bonus=1000, sp_bonus=500, attack_bonus=100, lifespan_bonus=100, level_up_points=15, realm_stage=MORTAL)
- 道祖之境(realm_level=14, sub_levels=9, required_exp=999999999, breakthrough_rate=1, hp_bonus=999999, sp_bonus=999999, attack_bonus=99999, lifespan_bonus=99999, level_up_points=100, realm_stage=IMMORTAL)

---

### 3. Pill (丹药)

**Table**: `pill`

**Description**: 丹药实体,记录丹药的属性、效果、炼制等级

**Primary Key**: `pill_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| pill_id | BIGINT | NO | 丹药唯一标识 | PK, Auto-Increment | - |
| pill_name | VARCHAR(50) | NO | 丹药名称 | Not Blank | - |
| pill_tier | TINYINT | NO | 丹药阶位(1-10) | Min 1, Max 10 | 1 |
| quality | VARCHAR(20) | NO | 丹药品质 | Enum(普通, 稀有, 史诗, 传说, 仙品) | '普通' |
| effect_type | VARCHAR(20) | NO | 效果类型 | Enum(恢复气血, 恢复灵力, 增加经验, 突破辅助, 属性提升, 解毒, 疗伤) | - |
| effect_value | INT | NO | 效果数值 | >= 0 | 0 |
| duration | INT | YES | 持续时间(秒) | >= 0 | NULL |
| stack_limit | INT | NO | 堆叠上限 | > 0 | 999 |
| spirit_stones | INT | NO | 出售价格(灵石) | >= 0 | 0 |
| description | TEXT | YES | 丹药描述 | - | NULL |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| deleted | TINYINT | NO | 逻辑删除标记 | 0或1 | 0 |

**Indexes**:
- `idx_pill_tier` ON (`pill_tier`) - 支持按阶位筛选
- `idx_pill_quality` ON (`quality`) - 支持按品质筛选
- `idx_effect_type` ON (`effect_type`) - 支持按效果类型查询

**Relationships**:
- One-to-Many: `pill` ↔ `character_pill` (角色背包中的丹药)

**Example Records**:
- 小还丹(pill_tier=1, quality=普通, effect_type=恢复气血, effect_value=100)
- 聚灵丹(pill_tier=2, quality=稀有, effect_type=恢复灵力, effect_value=200)
- 筑基丹(pill_tier=3, quality=史诗, effect_type=突破辅助, effect_value=50, description='增加50%筑基期突破成功率')
- 洗髓丹(pill_tier=5, quality=传说, effect_type=属性提升, effect_value=10, description='永久提升10点体质')

---

### 4. Material (材料)

**Table**: `material`

**Description**: 材料实体,包括草药、矿石等炼药、炼器材料

**Primary Key**: `material_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| material_id | BIGINT | NO | 材料唯一标识 | PK, Auto-Increment | - |
| material_name | VARCHAR(50) | NO | 材料名称 | Not Blank | - |
| material_type | VARCHAR(20) | NO | 材料类型 | Enum(草药, 矿石, 兽骨, 妖丹, 灵木, 其他) | - |
| material_tier | TINYINT | NO | 材料阶位(1-10) | Min 1, Max 10 | 1 |
| quality | VARCHAR(20) | NO | 材料品质 | Enum(普通, 稀有, 史诗, 传说, 仙品) | '普通' |
| stack_limit | INT | NO | 堆叠上限 | > 0 | 999 |
| spirit_stones | INT | NO | 出售价格(灵石) | >= 0 | 0 |
| description | TEXT | YES | 材料描述 | - | NULL |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| deleted | TINYINT | NO | 逻辑删除标记 | 0或1 | 0 |

**Indexes**:
- `idx_material_type` ON (`material_type`) - 支持按材料类型查询
- `idx_material_tier` ON (`material_tier`) - 支持按阶位筛选
- `idx_material_quality` ON (`quality`) - 支持按品质筛选

**Relationships**:
- One-to-Many: `material` ↔ `character_material` (角色背包中的材料)
- Many-to-Many: `material` ↔ `pill_recipe` (丹方需要的材料)

**Example Records**:
- 血灵草(material_type=草药, material_tier=1, quality=普通, description='炼制小还丹的主材料')
- 聚灵花(material_type=草药, material_tier=2, quality=稀有, description='炼制聚灵丹的主材料')
- 寒铁矿(material_type=矿石, material_tier=2, quality=普通, description='炼制武器的基础材料')
- 赤焰石(material_type=矿石, material_tier=3, quality=稀有, description='炼制火系装备的材料')
- 妖兽内丹(material_type=妖丹, material_tier=4, quality=史诗, description='高级丹药的珍贵材料')

---

### 5. CharacterPill (角色丹药背包)

**Table**: `character_pill`

**Description**: 角色丹药背包关联表,记录角色拥有的丹药及数量

**Primary Key**: `character_pill_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| character_pill_id | BIGINT | NO | 关联记录唯一标识 | PK, Auto-Increment | - |
| character_id | BIGINT | NO | 角色ID | FK → character.character_id | - |
| pill_id | BIGINT | NO | 丹药ID | FK → pill.pill_id | - |
| quantity | INT | NO | 拥有数量 | >= 0 | 1 |
| obtained_at | TIMESTAMP | NO | 获得时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_character_id` ON (`character_id`) - 支持查询角色丹药背包
- `uk_character_pill` UNIQUE (`character_id`, `pill_id`) - 防止重复记录

**Relationships**:
- Many-to-One: `character_pill` ↔ `character` (所属角色)
- Many-to-One: `character_pill` ↔ `pill` (丹药信息)

---

### 6. CharacterMaterial (角色材料背包)

**Table**: `character_material`

**Description**: 角色材料背包关联表,记录角色拥有的材料及数量

**Primary Key**: `character_material_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| character_material_id | BIGINT | NO | 关联记录唯一标识 | PK, Auto-Increment | - |
| character_id | BIGINT | NO | 角色ID | FK → character.character_id | - |
| material_id | BIGINT | NO | 材料ID | FK → material.material_id | - |
| quantity | INT | NO | 拥有数量 | >= 0 | 1 |
| obtained_at | TIMESTAMP | NO | 获得时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_character_id` ON (`character_id`) - 支持查询角色材料背包
- `uk_character_material` UNIQUE (`character_id`, `material_id`) - 防止重复记录

**Relationships**:
- Many-to-One: `character_material` ↔ `character` (所属角色)
- Many-to-One: `character_material` ↔ `material` (材料信息)

---

### 7. PillRecipe (丹方)

**Table**: `pill_recipe`

**Description**: 丹方实体,定义丹药的炼制配方、成功率、所需材料

**Primary Key**: `recipe_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| recipe_id | BIGINT | NO | 丹方唯一标识 | PK, Auto-Increment | - |
| recipe_name | VARCHAR(50) | NO | 丹方名称 | Not Blank | - |
| pill_id | BIGINT | NO | 产出丹药ID | FK → pill.pill_id | - |
| output_quantity | TINYINT | NO | 产出数量 | Min 1, Max 10 | 1 |
| base_success_rate | INT | NO | 基础成功率(%) | Range 0-100 | 50 |
| alchemy_level_required | TINYINT | NO | 所需炼丹等级 | Min 1, Max 99 | 1 |
| spiritual_cost | INT | NO | 消耗灵力 | >= 0 | 50 |
| stamina_cost | TINYINT | NO | 消耗体力 | >= 0 | 10 |
| duration | INT | NO | 炼制时长(秒) | > 0 | 300 |
| recipe_tier | TINYINT | NO | 丹方阶位(1-10) | Min 1, Max 10 | 1 |
| unlock_method | VARCHAR(20) | NO | 获得途径 | Enum(宗门学习, 商店购买, 任务奖励, 传承获得, 探索发现) | - |
| unlock_cost | INT | NO | 学习消耗(灵石或贡献度) | >= 0 | 0 |
| description | TEXT | YES | 丹方描述 | - | NULL |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| deleted | TINYINT | NO | 逻辑删除标记 | 0或1 | 0 |

**Indexes**:
- `idx_pill_id` ON (`pill_id`) - 支持查询丹药对应的丹方
- `idx_recipe_tier` ON (`recipe_tier`) - 支持按阶位筛选
- `idx_alchemy_level` ON (`alchemy_level_required`) - 支持按炼丹等级筛选

**Relationships**:
- Many-to-One: `pill_recipe` ↔ `pill` (产出丹药)
- One-to-Many: `pill_recipe` ↔ `pill_recipe_material` (所需材料)
- One-to-Many: `pill_recipe` ↔ `alchemy_record` (炼丹记录)

**Success Rate Formula**:
```
实际成功率 = base_success_rate + (角色炼丹等级 - alchemy_level_required) * 2%
实际成功率 = MIN(实际成功率, 95%)  // 最高95%成功率
```

**Example Records**:
- 小还丹丹方(pill_id=1, output_quantity=3, base_success_rate=80%, alchemy_level_required=1, spiritual_cost=50, stamina_cost=10, duration=300秒)
- 聚灵丹丹方(pill_id=2, output_quantity=2, base_success_rate=60%, alchemy_level_required=5, spiritual_cost=100, stamina_cost=15, duration=600秒)
- 筑基丹丹方(pill_id=3, output_quantity=1, base_success_rate=30%, alchemy_level_required=15, spiritual_cost=500, stamina_cost=30, duration=3600秒)

---

### 8. PillRecipeMaterial (丹方材料)

**Table**: `pill_recipe_material`

**Description**: 丹方材料关联表,定义每个丹方需要的材料及数量

**Primary Key**: `recipe_material_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| recipe_material_id | BIGINT | NO | 关联记录唯一标识 | PK, Auto-Increment | - |
| recipe_id | BIGINT | NO | 丹方ID | FK → pill_recipe.recipe_id | - |
| material_id | BIGINT | NO | 材料ID | FK → material.material_id | - |
| quantity_required | INT | NO | 所需数量 | > 0 | 1 |
| is_main_material | TINYINT | NO | 是否主材料 | 0或1 | 0 |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_recipe_id` ON (`recipe_id`) - 支持查询丹方所需材料
- `idx_material_id` ON (`material_id`) - 支持查询材料用于哪些丹方
- `uk_recipe_material` UNIQUE (`recipe_id`, `material_id`) - 防止重复配置

**Relationships**:
- Many-to-One: `pill_recipe_material` ↔ `pill_recipe` (所属丹方)
- Many-to-One: `pill_recipe_material` ↔ `material` (所需材料)

**Validation Rules**:
- 每个丹方必须至少有1个主材料(is_main_material=1)
- 每个丹方最多有1个主材料
- 辅材料数量建议2-5个

**Example Records**:
- 小还丹丹方需要: 血灵草×3(主材料), 凝血草×2, 清心草×1
- 聚灵丹丹方需要: 聚灵花×5(主材料), 灵露草×3, 天心石×1
- 筑基丹丹方需要: 筑基草×10(主材料), 凝神花×5, 百年灵芝×2, 五行石×3

---

### 9. AlchemyRecord (炼丹记录)

**Table**: `alchemy_record`

**Description**: 炼丹记录实体,记录角色的炼丹历史、成功/失败、产出

**Primary Key**: `alchemy_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| alchemy_id | BIGINT | NO | 炼丹记录唯一标识 | PK, Auto-Increment | - |
| character_id | BIGINT | NO | 角色ID | FK → character.character_id | - |
| recipe_id | BIGINT | NO | 丹方ID | FK → pill_recipe.recipe_id | - |
| is_success | TINYINT | NO | 是否成功 | 0或1 | 0 |
| success_rate | INT | NO | 实际成功率(%) | Range 0-100 | 50 |
| output_quantity | TINYINT | NO | 产出数量(成功时) | >= 0 | 0 |
| output_quality | VARCHAR(20) | YES | 产出品质 | Enum(普通, 稀有, 史诗, 传说, 仙品) | NULL |
| spiritual_consumed | INT | NO | 消耗灵力 | >= 0 | 0 |
| stamina_consumed | TINYINT | NO | 消耗体力 | >= 0 | 0 |
| alchemy_level_before | TINYINT | NO | 炼丹前等级 | Min 1, Max 99 | 1 |
| alchemy_level_after | TINYINT | NO | 炼丹后等级 | Min 1, Max 99 | 1 |
| exp_gained | INT | NO | 获得炼丹经验 | >= 0 | 0 |
| alchemy_time | TIMESTAMP | NO | 炼丹时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_character_id` ON (`character_id`) - 支持查询角色炼丹记录
- `idx_recipe_id` ON (`recipe_id`) - 支持查询丹方炼制记录
- `idx_alchemy_time` ON (`alchemy_time`) - 支持按时间筛选
- `idx_is_success` ON (`is_success`) - 支持查询成功/失败记录

**Relationships**:
- Many-to-One: `alchemy_record` ↔ `character` (所属角色)
- Many-to-One: `alchemy_record` ↔ `pill_recipe` (使用丹方)

**Validation Rules**:
- 失败时: `output_quantity` = 0, `output_quality` = NULL
- 成功时: `output_quantity` >= 1, `output_quality` 不为NULL
- 炼丹获得经验: 成功时基础经验 × 丹方阶位, 失败时获得50%经验

**Quality Probability (成功时品质概率)**:
- 普通: 70%
- 稀有: 20%
- 史诗: 8%
- 传说: 1.9%
- 仙品: 0.1%

**Example Records**:
- 炼制小还丹成功: is_success=1, success_rate=85%, output_quantity=3, output_quality=普通, exp_gained=100
- 炼制聚灵丹失败: is_success=0, success_rate=45%, output_quantity=0, output_quality=NULL, exp_gained=50
- 炼制筑基丹成功(极品): is_success=1, success_rate=35%, output_quantity=1, output_quality=传说, exp_gained=1500

---

### 10. Skill (技能)

**Table**: `skill`

**Description**: 技能实体,记录技能的所有属性、分类、效果

**Primary Key**: `skill_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| skill_id | BIGINT | NO | 技能唯一标识 | PK, Auto-Increment | - |
| skill_name | VARCHAR(50) | NO | 技能名称 | Not Blank | - |
| function_type | VARCHAR(20) | NO | 功能分类 | Enum(攻击, 防御, 辅助, 治疗) | - |
| element_type | VARCHAR(20) | YES | 元素分类 | Enum(物理系, 冰系, 火系, 雷系) | NULL |
| base_damage | INT | YES | 技能基础伤害(1级) | >= 0 | 0 |
| skill_multiplier | DECIMAL(5,2) | NO | 技能倍率(1级) | > 0 | 1.0 |
| spiritual_cost | SMALLINT | NO | 灵气消耗(1级) | >= 0 | 10 |
| damage_growth_rate | DECIMAL(5,2) | NO | 伤害成长率(每级增加百分比) | >= 0 | 0.10 |
| multiplier_growth | DECIMAL(4,2) | NO | 倍率成长(每级增加) | >= 0 | 0.05 |
| spiritual_cost_growth | TINYINT | NO | 灵气消耗成长(每级增加) | >= 0 | 2 |
| description | TEXT | YES | 技能描述 | - | NULL |
| tier | TINYINT | NO | 技能阶位(1-8) | Min 1, Max 8 | 1 |
| sect_id | BIGINT | YES | 宗门ID(专属技能) | FK → sect.sect_id | NULL |
| unlock_method | VARCHAR(20) | NO | 学习途径 | Enum(宗门学习, 商店购买, 任务奖励, 传承获得) | - |
| cost | INT | NO | 学习消耗(灵石或贡献度) | >= 0 | 0 |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| deleted | TINYINT | NO | 逻辑删除标记 | 0或1 | 0 |

**Indexes**:
- `idx_function_type` ON (`function_type`) - 支持按功能分类查询
- `idx_element_type` ON (`element_type`) - 支持按元素分类查询
- `idx_sect_id` ON (`sect_id`) - 支持查询宗门专属技能

**Relationships**:
- One-to-Many: `skill` ↔ `character_skill` (角色已学技能)
- Many-to-One: `skill` ↔ `sect` (所属宗门,可选)

---

### 11. CharacterSkill (角色已学技能)

**Table**: `character_skill`

**Description**: 角色技能关联表,记录角色已学习的技能及熟练度

**Primary Key**: `character_skill_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| character_skill_id | BIGINT | NO | 关联记录唯一标识 | PK, Auto-Increment | - |
| character_id | BIGINT | NO | 角色ID | FK → character.character_id | - |
| skill_id | BIGINT | NO | 技能ID | FK → skill.skill_id | - |
| proficiency | INT | NO | 技能熟练度 | >= 0 | 0 |
| skill_level | TINYINT | NO | 技能等级(1-99) | Min 1, Max 99 | 1 |
| current_damage | INT | NO | 当前等级实际伤害 | >= 0 | 0 |
| current_multiplier | DECIMAL(5,2) | NO | 当前等级实际倍率 | > 0 | 1.0 |
| current_spiritual_cost | SMALLINT | NO | 当前等级实际灵气消耗 | >= 0 | 10 |
| is_equipped | TINYINT | NO | 是否已装备 | 0或1 | 0 |
| equipment_slot | VARCHAR(20) | YES | 装备槽位 | Enum(攻击槽1, 攻击槽2, 攻击槽3, 攻击槽4, 辅助槽1, 辅助槽2) | NULL |
| learned_at | TIMESTAMP | NO | 学习时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_character_id` ON (`character_id`) - 支持查询角色技能
- `uk_character_skill` UNIQUE (`character_id`, `skill_id`) - 防止重复学习

**Relationships**:
- Many-to-One: `character_skill` ↔ `character` (所属角色)
- Many-to-One: `character_skill` ↔ `skill` (技能信息)

**Validation Rules**:
- 攻击技能只能装备到攻击槽位(攻击槽1-4)
- 防御/治疗技能只能装备到辅助槽位(辅助槽1-2)
- 每个槽位只能装备1个技能

**Skill Growth Formula (技能成长公式)**:
当技能等级提升时,根据以下公式自动计算当前属性:
- `current_damage` = `base_damage` × (1 + `damage_growth_rate` × (`skill_level` - 1))
- `current_multiplier` = `skill_multiplier` + `multiplier_growth` × (`skill_level` - 1)
- `current_spiritual_cost` = `spiritual_cost` + `spiritual_cost_growth` × (`skill_level` - 1)

**示例**: 火球术(1级: base_damage=100, skill_multiplier=2.0, spiritual_cost=20, damage_growth_rate=0.10, multiplier_growth=0.05, spiritual_cost_growth=2)
- 1级: damage=100, multiplier=2.0, cost=20
- 2级: damage=110, multiplier=2.05, cost=22
- 10级: damage=190, multiplier=2.45, cost=38
- 99级: damage=1080, multiplier=5.95, cost=216

---

### 12. Equipment (装备)

**Table**: `equipment`

**Description**: 装备实体,记录装备的所有属性、强化等级、镶嵌宝石

**Primary Key**: `equipment_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| equipment_id | BIGINT | NO | 装备唯一标识 | PK, Auto-Increment | - |
| equipment_name | VARCHAR(50) | NO | 装备名称 | Not Blank | - |
| equipment_type | VARCHAR(20) | NO | 装备类型 | Enum(武器, 头盔, 铠甲, 护手, 护腿, 靴子, 戒指, 项链) | - |
| quality | VARCHAR(20) | NO | 装备品质 | Enum(普通, 稀有, 史诗, 传说, 仙品) | '普通' |
| base_score | INT | NO | 品质基础分 | 根据品质映射(普通100, 稀有300, 史诗600, 传说1000, 仙品2000) | - |
| attack_power | INT | YES | 攻击力 | >= 0 | NULL |
| defense_power | INT | YES | 防御力 | >= 0 | NULL |
| health_bonus | INT | YES | 生命值加成 | >= 0 | NULL |
| critical_rate | TINYINT | YES | 暴击率(%) | Range 0-15 | NULL |
| speed_bonus | TINYINT | YES | 速度加成 | >= 0 | NULL |
| physical_resist | TINYINT | NO | 物理抗性(%) | Range 0-15 | 0 |
| ice_resist | TINYINT | NO | 冰系抗性(%) | Range 0-15 | 0 |
| fire_resist | TINYINT | NO | 火系抗性(%) | Range 0-15 | 0 |
| lightning_resist | TINYINT | NO | 雷系抗性(%) | Range 0-15 | 0 |
| enhancement_level | TINYINT | NO | 强化等级 | Range 0-15 | 0 |
| gem_slot_count | TINYINT | NO | 宝石槽孔数 | Range 0-5 | 0 |
| special_effects | TEXT | YES | 特殊效果(JSON格式) | - | NULL |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| deleted | TINYINT | NO | 逻辑删除标记 | 0或1 | 0 |

**Computed Attributes** (NOT stored in DB):
- `equipment_score` (装备评分) = 品质基础分 + 主属性总和×2 + 抗性总和×3 + 强化等级×50 + 特殊效果×100
- `total_physical_resist` (总物理抗性) = 所有装备物理抗性之和(上限75%)
- `total_ice_resist` (总冰系抗性) = 所有装备冰系抗性之和(上限75%)
- `total_fire_resist` (总火系抗性) = 所有装备火系抗性之和(上限75%)
- `total_lightning_resist` (总雷系抗性) = 所有装备雷系抗性之和(上限75%)

**Indexes**:
- `idx_equipment_type` ON (`equipment_type`) - 支持按类型查询
- `idx_quality` ON (`quality`) - 支持按品质筛选
- `idx_enhancement_level` ON (`enhancement_level`) - 支持按强化等级筛选

**Relationships**:
- One-to-Many: `equipment` ↔ `character_equipment` (角色已装备装备)

---

### 13. CharacterEquipment (角色已装备装备)

**Table**: `character_equipment`

**Description**: 角色装备关联表,记录角色当前装备的装备

**Primary Key**: `character_equipment_id` (BIGINT, Auto-Increment)

**Unique Key**: (`character_id`, `equipment_slot`)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| character_equipment_id | BIGINT | NO | 关联记录唯一标识 | PK, Auto-Increment | - |
| character_id | BIGINT | NO | 角色ID | FK → character.character_id | - |
| equipment_id | BIGINT | NO | 装备ID | FK → equipment.equipment_id | - |
| equipment_slot | VARCHAR(20) | NO | 装备槽位 | Enum(武器, 头盔, 铠甲, 护手, 护腿, 靴子, 戒指1, 戒指2, 项链) | - |
| equipped_at | TIMESTAMP | NO | 装备时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_character_id` ON (`character_id`) - 支持查询角色装备
- `uk_character_slot` UNIQUE (`character_id`, `equipment_slot`) - 每个槽位只能装备1件

**Relationships**:
- Many-to-One: `character_equipment` ↔ `character` (所属角色)
- Many-to-One: `character_equipment` ↔ `equipment` (装备信息)

**Validation Rules**:
- 戒指槽位有2个(戒指1、戒指2),其他槽位各1个
- 武器槽只能装备武器类型装备
- 防具槽只能装备对应类型装备
- 饰品槽只能装备戒指或项链

---

### 14. EquipmentRecipe (装备图纸)

**Table**: `equipment_recipe`

**Description**: 装备图纸实体,定义装备的锻造配方、成功率、所需材料

**Primary Key**: `recipe_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| recipe_id | BIGINT | NO | 图纸唯一标识 | PK, Auto-Increment | - |
| recipe_name | VARCHAR(50) | NO | 图纸名称 | Not Blank | - |
| equipment_id | BIGINT | NO | 产出装备ID | FK → equipment.equipment_id | - |
| base_success_rate | INT | NO | 基础成功率(%) | Range 0-100 | 50 |
| forging_level_required | TINYINT | NO | 所需炼器等级 | Min 1, Max 99 | 1 |
| spiritual_cost | INT | NO | 消耗灵力 | >= 0 | 100 |
| stamina_cost | TINYINT | NO | 消耗体力 | >= 0 | 20 |
| duration | INT | NO | 锻造时长(秒) | > 0 | 600 |
| recipe_tier | TINYINT | NO | 图纸阶位(1-10) | Min 1, Max 10 | 1 |
| unlock_method | VARCHAR(20) | NO | 获得途径 | Enum(宗门学习, 商店购买, 任务奖励, 传承获得, 探索发现) | - |
| unlock_cost | INT | NO | 学习消耗(灵石或贡献度) | >= 0 | 0 |
| description | TEXT | YES | 图纸描述 | - | NULL |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| deleted | TINYINT | NO | 逻辑删除标记 | 0或1 | 0 |

**Indexes**:
- `idx_equipment_id` ON (`equipment_id`) - 支持查询装备对应的图纸
- `idx_recipe_tier` ON (`recipe_tier`) - 支持按阶位筛选
- `idx_forging_level` ON (`forging_level_required`) - 支持按炼器等级筛选

**Relationships**:
- Many-to-One: `equipment_recipe` ↔ `equipment` (产出装备)
- One-to-Many: `equipment_recipe` ↔ `equipment_recipe_material` (所需材料)
- One-to-Many: `equipment_recipe` ↔ `forge_record` (锻造记录)

**Success Rate Formula**:
```
实际成功率 = base_success_rate + (角色炼器等级 - forging_level_required) * 2%
实际成功率 = MIN(实际成功率, 95%)  // 最高95%成功率
```

**Example Records**:
- 新手剑图纸(equipment_id=1, base_success_rate=90%, forging_level_required=1, spiritual_cost=100, stamina_cost=20, duration=600秒)
- 寒铁剑图纸(equipment_id=2, base_success_rate=70%, forging_level_required=10, spiritual_cost=300, stamina_cost=30, duration=1800秒)
- 烈焰战甲图纸(equipment_id=3, base_success_rate=50%, forging_level_required=25, spiritual_cost=800, stamina_cost=50, duration=3600秒)

---

### 15. EquipmentRecipeMaterial (装备图纸材料)

**Table**: `equipment_recipe_material`

**Description**: 装备图纸材料关联表,定义每个图纸需要的材料及数量

**Primary Key**: `recipe_material_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| recipe_material_id | BIGINT | NO | 关联记录唯一标识 | PK, Auto-Increment | - |
| recipe_id | BIGINT | NO | 图纸ID | FK → equipment_recipe.recipe_id | - |
| material_id | BIGINT | NO | 材料ID | FK → material.material_id | - |
| quantity_required | INT | NO | 所需数量 | > 0 | 1 |
| is_main_material | TINYINT | NO | 是否主材料 | 0或1 | 0 |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_recipe_id` ON (`recipe_id`) - 支持查询图纸所需材料
- `idx_material_id` ON (`material_id`) - 支持查询材料用于哪些图纸
- `uk_recipe_material` UNIQUE (`recipe_id`, `material_id`) - 防止重复配置

**Relationships**:
- Many-to-One: `equipment_recipe_material` ↔ `equipment_recipe` (所属图纸)
- Many-to-One: `equipment_recipe_material` ↔ `material` (所需材料)

**Validation Rules**:
- 每个图纸必须至少有1个主材料(is_main_material=1)
- 每个图纸最多有1个主材料
- 辅材料数量建议2-5个

**Example Records**:
- 新手剑图纸需要: 铁矿×10(主材料), 木材×5, 麻绳×2
- 寒铁剑图纸需要: 寒铁矿×15(主材料), 精铁×8, 冰晶石×3
- 烈焰战甲图纸需要: 赤焰石×20(主材料), 精钢×15, 火焰宝石×5, 龙鳞×2

---

### 16. ForgeRecord (锻造记录)

**Table**: `forge_record`

**Description**: 锻造记录实体,记录角色的锻造历史、成功/失败、产出

**Primary Key**: `forge_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| forge_id | BIGINT | NO | 锻造记录唯一标识 | PK, Auto-Increment | - |
| character_id | BIGINT | NO | 角色ID | FK → character.character_id | - |
| recipe_id | BIGINT | NO | 图纸ID | FK → equipment_recipe.recipe_id | - |
| is_success | TINYINT | NO | 是否成功 | 0或1 | 0 |
| success_rate | INT | NO | 实际成功率(%) | Range 0-100 | 50 |
| output_quality | VARCHAR(20) | YES | 产出品质 | Enum(普通, 稀有, 史诗, 传说, 仙品) | NULL |
| enhancement_level | TINYINT | NO | 产出强化等级 | Range 0-15 | 0 |
| spiritual_consumed | INT | NO | 消耗灵力 | >= 0 | 0 |
| stamina_consumed | TINYINT | NO | 消耗体力 | >= 0 | 0 |
| forging_level_before | TINYINT | NO | 锻造前等级 | Min 1, Max 99 | 1 |
| forging_level_after | TINYINT | NO | 锻造后等级 | Min 1, Max 99 | 1 |
| exp_gained | INT | NO | 获得炼器经验 | >= 0 | 0 |
| forge_time | TIMESTAMP | NO | 锻造时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_character_id` ON (`character_id`) - 支持查询角色锻造记录
- `idx_recipe_id` ON (`recipe_id`) - 支持查询图纸锻造记录
- `idx_forge_time` ON (`forge_time`) - 支持按时间筛选
- `idx_is_success` ON (`is_success`) - 支持查询成功/失败记录

**Relationships**:
- Many-to-One: `forge_record` ↔ `character` (所属角色)
- Many-to-One: `forge_record` ↔ `equipment_recipe` (使用图纸)

**Validation Rules**:
- 失败时: `output_quality` = NULL, `enhancement_level` = 0
- 成功时: `output_quality` 不为NULL, `enhancement_level` >= 0
- 锻造获得经验: 成功时基础经验 × 图纸阶位, 失败时获得50%经验

**Quality Probability (成功时品质概率)**:
- 普通: 70%
- 稀有: 20%
- 史诗: 8%
- 传说: 1.9%
- 仙品: 0.1%

**Enhancement Level Probability (成功时强化等级概率)**:
- +0: 60%
- +1~+3: 30%
- +4~+6: 8%
- +7~+9: 1.8%
- +10~+15: 0.2%

**Example Records**:
- 锻造新手剑成功: is_success=1, success_rate=92%, output_quality=普通, enhancement_level=0, exp_gained=100
- 锻造寒铁剑失败: is_success=0, success_rate=55%, output_quality=NULL, enhancement_level=0, exp_gained=50
- 锻造烈焰战甲成功(极品): is_success=1, success_rate=58%, output_quality=史诗, enhancement_level=5, exp_gained=2500

---

### 17. Monster (妖兽)

**Table**: `monster`

**Description**: 妖兽实体,记录妖兽的所有属性、AI行为、掉落奖励

**Primary Key**: `monster_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| monster_id | BIGINT | NO | 妖兽唯一标识 | PK, Auto-Increment | - |
| monster_name | VARCHAR(50) | NO | 妖兽名称 | Not Blank | - |
| realm_id | INT | NO | 妖兽境界ID | FK → realm.id | 1 |
| monster_type | VARCHAR(20) | NO | 妖兽类型 | Enum(普通, 精英, BOSS) | '普通' |
| speed | INT | NO | 速度(决定行动顺序) | > 0 | 10 |
| hp | INT | NO | 生命力(生命值) | > 0 | 100 |
| attack_power | INT | NO | 攻击力 | >= 0 | 10 |
| defense_power | INT | NO | 防御力 | >= 0 | 5 |
| attack_element | VARCHAR(20) | NO | 攻击元素类型 | Enum(物理, 冰系, 火系, 雷系) | '物理' |
| physical_resist | TINYINT | NO | 物理抗性(%) | Range 0-50 | 0 |
| ice_resist | TINYINT | NO | 冰系抗性(%) | Range 0-50 | 0 |
| fire_resist | TINYINT | NO | 火系抗性(%) | Range 0-50 | 0 |
| lightning_resist | TINYINT | NO | 雷系抗性(%) | Range 0-50 | 0 |
| stamina_cost | TINYINT | NO | 体力消耗 | Enum(10, 20, 30) | 10 |
| exp_reward | INT | NO | 经验值奖励 | > 0 | 50 |
| spirit_stones_reward | INT | NO | 灵石奖励 | >= 0 | 10 |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| deleted | TINYINT | NO | 逻辑删除标记 | 0或1 | 0 |

**Indexes**:
- `idx_realm_id` ON (`realm_id`) - 支持按境界筛选
- `idx_monster_type` ON (`monster_type`) - 支持按类型筛选
- `idx_attack_element` ON (`attack_element`) - 支持按攻击元素筛选

**Relationships**:
- Many-to-One: `monster` ↔ `realm` (所属境界)
- One-to-Many: `monster` ↔ `monster_drop` (装备掉落配置)
- One-to-Many: `monster` ↔ `combat_record` (战斗记录)

**Example Records**:
- 火焰蜥蜴(精英火系): realm_id=2(炼气期), monster_type=精英, attack_element=火系, fire_resist=50, physical_resist=20, stamina_cost=20
- 冰霜狼(精英冰系): realm_id=2(炼气期), monster_type=精英, attack_element=冰系, ice_resist=40, lightning_resist=10, stamina_cost=20
- 普通野狼(普通物理): realm_id=1(凡人), monster_type=普通, attack_element=物理, stamina_cost=10

---

### 18. MonsterDrop (怪物装备掉落配置)

**Table**: `monster_drop`

**Description**: 怪物装备掉落配置表,定义每个怪物可掉落的装备及对应掉落率

**Primary Key**: `monster_drop_id` (BIGINT, Auto-Increment)

**Unique Key**: (`monster_id`, `equipment_id`)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| monster_drop_id | BIGINT | NO | 掉落配置唯一标识 | PK, Auto-Increment | - |
| monster_id | BIGINT | NO | 怪物ID | FK → monster.monster_id | - |
| equipment_id | BIGINT | NO | 装备ID | FK → equipment.equipment_id | - |
| drop_rate | DECIMAL(5,2) | NO | 掉落率(%) | Range 0-100 | 10.0 |
| drop_quantity | TINYINT | NO | 掉落数量 | Min 1, Max 10 | 1 |
| min_quality | VARCHAR(20) | YES | 最低品质要求 | Enum(普通, 稀有, 史诗, 传说, 仙品) | NULL |
| max_quality | VARCHAR(20) | YES | 最高品质限制 | Enum(普通, 稀有, 史诗, 传说, 仙品) | NULL |
| is_guaranteed | TINYINT | NO | 是否必掉 | 0或1 | 0 |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| deleted | TINYINT | NO | 逻辑删除标记 | 0或1 | 0 |

**Indexes**:
- `idx_monster_id` ON (`monster_id`) - 支持查询怪物掉落列表
- `idx_equipment_id` ON (`equipment_id`) - 支持查询哪些怪物掉落此装备
- `uk_monster_equipment` UNIQUE (`monster_id`, `equipment_id`) - 防止重复配置

**Relationships**:
- Many-to-One: `monster_drop` ↔ `monster` (所属怪物)
- Many-to-One: `monster_drop` ↔ `equipment` (掉落装备)

**Validation Rules**:
- 同一怪物对同一装备只能有一条配置记录
- `is_guaranteed`=1时,`drop_rate`自动设为100%
- `min_quality`和`max_quality`必须符合品质等级顺序(普通<稀有<史诗<传说<仙品)
- BOSS类型怪物的掉落率通常高于普通怪物

**Example Records**:
- 火焰蜥蜴→烈焰剑(drop_rate=15.0%, is_guaranteed=0)
- 火焰蜥蜴→火之戒(drop_rate=5.0%, is_guaranteed=0)
- 冰霜狼→寒冰甲(drop_rate=12.0%, is_guaranteed=0)
- BOSS火龙→龙鳞甲(drop_rate=100.0%, is_guaranteed=1)

**掉落逻辑**:
1. 战斗胜利后,查询该怪物在`monster_drop`表中的所有掉落配置
2. 对每个配置项进行随机判定:生成1-100随机数,≤`drop_rate`则掉落
3. `is_guaranteed`=1的装备必定掉落
4. 支持同时掉落多个装备(每个配置独立判定)

---

### 19. Sect (宗门)

**Table**: `sect`

**Description**: 宗门实体,记录宗门的信息、职位体系、特色技能

**Primary Key**: `sect_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| sect_id | BIGINT | NO | 宗门唯一标识 | PK, Auto-Increment | - |
| sect_name | VARCHAR(50) | NO | 宗门名称 | Not Blank | - |
| sect_type | VARCHAR(20) | NO | 宗门类型 | Enum(青云剑宗, 丹霞谷, 天机阁, 无极魔宗, 万兽山庄) | - |
| description | TEXT | YES | 宗门描述 | - | NULL |
| skill_focus | VARCHAR(50) | NO | 特色技能类型 | Not Blank | - |
| join_requirement | VARCHAR(20) | NO | 加入要求(境界) | Enum(凡人, 炼气期, 筑基期) | '炼气期' |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| deleted | TINYINT | NO | 逻辑删除标记 | 0或1 | 0 |

**Indexes**:
- `idx_sect_type` ON (`sect_type`) - 支持按宗门类型查询
- `uk_sect_name` UNIQUE (`sect_name`) - 宗门名称唯一

**Relationships**:
- One-to-Many: `sect` ↔ `character` (宗门成员)
- One-to-Many: `sect` ↔ `skill` (宗门专属技能)

**Sect Types**:
- 青云剑宗: 剑法系技能(物理系攻击为主)
- 丹霞谷: 炼丹辅助技能(治疗、增益类技能)
- 天机阁: 法术系技能(元素攻击为主)
- 无极魔宗: 魔功技能(高伤害、有副作用)
- 万兽山庄: 驯兽技能(召唤、辅助类)

---

### 20. CombatRecord (战斗记录)

**Table**: `combat_record`

**Description**: 战斗记录实体,记录战斗的详细信息、结果、奖励

**Primary Key**: `combat_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| combat_id | BIGINT | NO | 战斗记录唯一标识 | PK, Auto-Increment | - |
| character_id | BIGINT | NO | 角色ID | FK → character.character_id | - |
| monster_id | BIGINT | NO | 妖兽ID | FK → monster.monster_id | - |
| combat_mode | VARCHAR(20) | NO | 战斗模式 | Enum(手动战斗, 自动战斗, 挂机战斗) | '手动战斗' |
| is_victory | TINYINT | NO | 是否胜利 | 0或1 | 0 |
| turns | SMALLINT | NO | 战斗回合数 | >= 0 | 0 |
| damage_dealt | INT | NO | 造成伤害总和 | >= 0 | 0 |
| damage_taken | INT | NO | 受到伤害总和 | >= 0 | 0 |
| critical_hits | SMALLINT | NO | 暴击次数 | >= 0 | 0 |
| stamina_consumed | TINYINT | NO | 体力消耗 | Enum(5, 10, 15, 20, 30) | 10 |
| exp_gained | INT | NO | 获得经验值 | >= 0 | 0 |
| items_dropped | TEXT | YES | 掉落物品(JSON数组) | - | NULL |
| combat_time | TIMESTAMP | NO | 战斗时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_character_id` ON (`character_id`) - 支持查询角色战斗记录
- `idx_monster_id` ON (`monster_id`) - 支持查询妖兽战斗记录
- `idx_combat_time` ON (`combat_time`) - 支持按时间筛选
- `idx_is_victory` ON (`is_victory`) - 支持查询胜利/失败记录

**Relationships**:
- Many-to-One: `combat_record` ↔ `character` (所属角色)
- Many-to-One: `combat_record` ↔ `monster` (战斗妖兽)

**State Transitions**:
- 战斗开始 → 记录创建
- 战斗结束 → 更新结果(is_victory, turns, damage_dealt等)
- 失败时 → stamina_consumed减半(规范Edge Cases)

---

### 21. CultivationRecord (修炼记录)

**Table**: `cultivation_record`

**Description**: 修炼记录实体,记录修炼历史、经验值获取、境界提升

**Primary Key**: `cultivation_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| cultivation_id | BIGINT | NO | 修炼记录唯一标识 | PK, Auto-Increment | - |
| character_id | BIGINT | NO | 角色ID | FK → character.character_id | - |
| start_realm | VARCHAR(20) | NO | 修炼前境界 | Enum(凡人, 炼气期, 筑基期, ...) | - |
| start_level | TINYINT | NO | 修炼前层次 | Range 1-9 | - |
| end_realm | VARCHAR(20) | NO | 修炼后境界 | Enum(凡人, 炼气期, 筑基期, ...) | - |
| end_level | TINYINT | NO | 修炼后层次 | Range 1-9 | - |
| exp_gained | INT | NO | 获得经验值 | > 0 | 0 |
| stamina_consumed | TINYINT | NO | 体力消耗 | > 0 | 5 |
| cultivation_time | TIMESTAMP | NO | 修炼时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_character_id` ON (`character_id`) - 支持查询角色修炼记录
- `idx_cultivation_time` ON (`cultivation_time`) - 支持按时间筛选

**Relationships**:
- Many-to-One: `cultivation_record` ↔ `character` (所属角色)

---

### 22. ExplorationEvent (探索事件)

**Table**: `exploration_event`

**Description**: 探索事件实体,记录探索触发的随机事件

**Primary Key**: `event_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| event_id | BIGINT | NO | 事件记录唯一标识 | PK, Auto-Increment | - |
| character_id | BIGINT | NO | 角色ID | FK → character.character_id | - |
| event_type | VARCHAR(20) | NO | 事件类型 | Enum(TREASURE, DANGER, ENCOUNTER, INHERITANCE) | - |
| event_description | TEXT | YES | 事件描述 | - | NULL |
| stamina_consumed | TINYINT | NO | 体力消耗 | >= 0 | 5 |
| exploration_time | TIMESTAMP | NO | 探索时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_character_id` ON (`character_id`) - 支持查询角色探索记录
- `idx_event_type` ON (`event_type`) - 支持按事件类型筛选
- `idx_exploration_time` ON (`exploration_time`) - 支持按时间筛选

**Relationships**:
- Many-to-One: `exploration_event` ↔ `character` (所属角色)
- One-to-Many: `exploration_event` ↔ `exploration_event_reward` (实际获得的奖励)

**Event Types**:
- **TREASURE(宝藏)**: 洞府遗迹、宝箱,奖励包含装备、材料、灵石
- **DANGER(危险)**: 强盗袭击、陷阱,需要战斗或消耗资源解除
- **ENCOUNTER(奇遇)**: 高人指点、行脚商人,奖励包含属性提升、增益、交易
- **INHERITANCE(传承)**: 上古传承,奖励包含功法、技能、属性提升,额外消耗10体力

---

### 23. ExplorationRewardConfig (探索奖励配置)

**Table**: `exploration_reward_config`

**Description**: 探索奖励配置表,定义每种探索事件类型可能的奖励及掉落率

**Primary Key**: `config_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| config_id | BIGINT | NO | 配置唯一标识 | PK, Auto-Increment | - |
| event_type | VARCHAR(20) | NO | 事件类型 | Enum(TREASURE, DANGER, ENCOUNTER, INHERITANCE) | - |
| reward_type | VARCHAR(20) | NO | 奖励类型 | Enum(材料, 丹药, 装备, 灵石, 经验, 属性提升, 技能) | - |
| item_id | BIGINT | YES | 物品ID | 根据reward_type关联不同表 | NULL |
| quantity_min | INT | NO | 数量下限 | >= 0 | 1 |
| quantity_max | INT | NO | 数量上限 | >= quantity_min | 1 |
| drop_rate | DECIMAL(5,2) | NO | 掉落率(%) | Range 0-100 | 10.0 |
| is_guaranteed | TINYINT | NO | 是否必掉 | 0或1 | 0 |
| weight | INT | NO | 权重(抽奖用) | > 0 | 100 |
| min_realm_level | INT | YES | 最低境界要求 | >= 1 | NULL |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NO | 更新时间 | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| deleted | TINYINT | NO | 逻辑删除标记 | 0或1 | 0 |

**Indexes**:
- `idx_event_type` ON (`event_type`) - 支持按事件类型查询
- `idx_reward_type` ON (`reward_type`) - 支持按奖励类型查询
- `idx_drop_rate` ON (`drop_rate`) - 支持按掉落率排序

**Relationships**:
- reward_type='材料' 时: `item_id` → `material.material_id`
- reward_type='丹药' 时: `item_id` → `pill.pill_id`
- reward_type='装备' 时: `item_id` → `equipment.equipment_id`
- reward_type='技能' 时: `item_id` → `skill.skill_id`
- reward_type='灵石'/'经验'/'属性提升' 时: `item_id` = NULL (数量通过quantity字段表示)

**Validation Rules**:
- `is_guaranteed`=1时,`drop_rate`自动设为100%
- `quantity_max` >= `quantity_min`
- 灵石奖励: `reward_type`='灵石', `item_id`=NULL, `quantity_min`/`quantity_max`表示灵石数量
- 经验奖励: `reward_type`='经验', `item_id`=NULL, `quantity_min`/`quantity_max`表示经验值
- 属性提升: `reward_type`='属性提升', `item_id`=NULL, `quantity_min`/`quantity_max`表示属性点数

**Example Records**:
- TREASURE事件掉落: 材料(寒铁矿×5-10, drop_rate=30%, weight=100)
- TREASURE事件掉落: 灵石(item_id=NULL, quantity_min=100, quantity_max=500, drop_rate=80%, weight=200)
- ENCOUNTER事件掉落: 经验(item_id=NULL, quantity_min=500, quantity_max=1000, drop_rate=100%, is_guaranteed=1)
- INHERITANCE事件掉落: 技能(item_id=某技能ID, quantity_min=1, quantity_max=1, drop_rate=20%, min_realm_level=3)

---

### 24. ExplorationEventReward (探索事件奖励记录)

**Table**: `exploration_event_reward`

**Description**: 探索事件奖励记录表,记录每次探索实际获得的奖励

**Primary Key**: `event_reward_id` (BIGINT, Auto-Increment)

**Attributes**:

| Field | Type | Nullable | Description | Validation | Default |
|-------|------|----------|-------------|------------|---------|
| event_reward_id | BIGINT | NO | 奖励记录唯一标识 | PK, Auto-Increment | - |
| event_id | BIGINT | NO | 探索事件ID | FK → exploration_event.event_id | - |
| reward_type | VARCHAR(20) | NO | 奖励类型 | Enum(材料, 丹药, 装备, 灵石, 经验, 属性提升, 技能) | - |
| item_id | BIGINT | YES | 物品ID | 根据reward_type关联不同表 | NULL |
| quantity | INT | NO | 获得数量 | > 0 | 1 |
| created_at | TIMESTAMP | NO | 创建时间 | - | CURRENT_TIMESTAMP |

**Indexes**:
- `idx_event_id` ON (`event_id`) - 支持查询探索事件的所有奖励
- `idx_reward_type` ON (`reward_type`) - 支持按奖励类型筛选

**Relationships**:
- Many-to-One: `exploration_event_reward` ↔ `exploration_event` (所属探索事件)
- reward_type='材料' 时: `item_id` → `material.material_id`
- reward_type='丹药' 时: `item_id` → `pill.pill_id`
- reward_type='装备' 时: `item_id` → `equipment.equipment_id`
- reward_type='技能' 时: `item_id` → `skill.skill_id`

**Validation Rules**:
- 灵石奖励: `reward_type`='灵石', `item_id`=NULL, `quantity`表示灵石数量
- 经验奖励: `reward_type`='经验', `item_id`=NULL, `quantity`表示经验值
- 属性提升: `reward_type`='属性提升', `item_id`=NULL, `quantity`表示属性点数
- 物品奖励: `reward_type`='材料/丹药/装备/技能', `item_id`不为NULL, `quantity`表示物品数量

**Example Records**:
- 探索事件#123获得奖励: reward_type='材料', item_id=5(寒铁矿), quantity=7
- 探索事件#123获得奖励: reward_type='灵石', item_id=NULL, quantity=350
- 探索事件#456获得奖励: reward_type='经验', item_id=NULL, quantity=800
- 探索事件#789获得奖励: reward_type='技能', item_id=15(某技能ID), quantity=1

---

## Database Schema Design

### Primary Key Strategy

- 所有表使用 `BIGINT` 自增主键
- 使用逻辑删除(`deleted TINYINT`),而非物理删除
- 使用雪花算法或UUID作为分布式ID(如果未来需要多实例部署)

### Foreign Key Constraints

```sql
-- Character Foreign Keys
ALTER TABLE character ADD CONSTRAINT fk_character_realm
  FOREIGN KEY (realm_id) REFERENCES realm(id);
ALTER TABLE character ADD CONSTRAINT fk_character_sect
  FOREIGN KEY (sect_id) REFERENCES sect(sect_id);

-- CharacterPill Foreign Keys
ALTER TABLE character_pill ADD CONSTRAINT fk_character_pill_character
  FOREIGN KEY (character_id) REFERENCES character(character_id);
ALTER TABLE character_pill ADD CONSTRAINT fk_character_pill_pill
  FOREIGN KEY (pill_id) REFERENCES pill(pill_id);

-- CharacterMaterial Foreign Keys
ALTER TABLE character_material ADD CONSTRAINT fk_character_material_character
  FOREIGN KEY (character_id) REFERENCES character(character_id);
ALTER TABLE character_material ADD CONSTRAINT fk_character_material_material
  FOREIGN KEY (material_id) REFERENCES material(material_id);

-- PillRecipe Foreign Keys
ALTER TABLE pill_recipe ADD CONSTRAINT fk_pill_recipe_pill
  FOREIGN KEY (pill_id) REFERENCES pill(pill_id);

-- PillRecipeMaterial Foreign Keys
ALTER TABLE pill_recipe_material ADD CONSTRAINT fk_pill_recipe_material_recipe
  FOREIGN KEY (recipe_id) REFERENCES pill_recipe(recipe_id);
ALTER TABLE pill_recipe_material ADD CONSTRAINT fk_pill_recipe_material_material
  FOREIGN KEY (material_id) REFERENCES material(material_id);

-- AlchemyRecord Foreign Keys
ALTER TABLE alchemy_record ADD CONSTRAINT fk_alchemy_record_character
  FOREIGN KEY (character_id) REFERENCES character(character_id);
ALTER TABLE alchemy_record ADD CONSTRAINT fk_alchemy_record_recipe
  FOREIGN KEY (recipe_id) REFERENCES pill_recipe(recipe_id);

-- CharacterSkill Foreign Keys
ALTER TABLE character_skill ADD CONSTRAINT fk_character_skill_character
  FOREIGN KEY (character_id) REFERENCES character(character_id);
ALTER TABLE character_skill ADD CONSTRAINT fk_character_skill_skill
  FOREIGN KEY (skill_id) REFERENCES skill(skill_id);

-- CharacterEquipment Foreign Keys
ALTER TABLE character_equipment ADD CONSTRAINT fk_character_equipment_character
  FOREIGN KEY (character_id) REFERENCES character(character_id);
ALTER TABLE character_equipment ADD CONSTRAINT fk_character_equipment_equipment
  FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id);

-- EquipmentRecipe Foreign Keys
ALTER TABLE equipment_recipe ADD CONSTRAINT fk_equipment_recipe_equipment
  FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id);

-- EquipmentRecipeMaterial Foreign Keys
ALTER TABLE equipment_recipe_material ADD CONSTRAINT fk_equipment_recipe_material_recipe
  FOREIGN KEY (recipe_id) REFERENCES equipment_recipe(recipe_id);
ALTER TABLE equipment_recipe_material ADD CONSTRAINT fk_equipment_recipe_material_material
  FOREIGN KEY (material_id) REFERENCES material(material_id);

-- ForgeRecord Foreign Keys
ALTER TABLE forge_record ADD CONSTRAINT fk_forge_record_character
  FOREIGN KEY (character_id) REFERENCES character(character_id);
ALTER TABLE forge_record ADD CONSTRAINT fk_forge_record_recipe
  FOREIGN KEY (recipe_id) REFERENCES equipment_recipe(recipe_id);

-- Skill Foreign Keys
ALTER TABLE skill ADD CONSTRAINT fk_skill_sect
  FOREIGN KEY (sect_id) REFERENCES sect(sect_id);

-- Monster Foreign Keys
ALTER TABLE monster ADD CONSTRAINT fk_monster_realm
  FOREIGN KEY (realm_id) REFERENCES realm(id);

-- MonsterDrop Foreign Keys
ALTER TABLE monster_drop ADD CONSTRAINT fk_monster_drop_monster
  FOREIGN KEY (monster_id) REFERENCES monster(monster_id);
ALTER TABLE monster_drop ADD CONSTRAINT fk_monster_drop_equipment
  FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id);

-- CombatRecord Foreign Keys
ALTER TABLE combat_record ADD CONSTRAINT fk_combat_record_character
  FOREIGN KEY (character_id) REFERENCES character(character_id);
ALTER TABLE combat_record ADD CONSTRAINT fk_combat_record_monster
  FOREIGN KEY (monster_id) REFERENCES monster(monster_id);

-- CultivationRecord Foreign Keys
ALTER TABLE cultivation_record ADD CONSTRAINT fk_cultivation_record_character
  FOREIGN KEY (character_id) REFERENCES character(character_id);

-- ExplorationEvent Foreign Keys
ALTER TABLE exploration_event ADD CONSTRAINT fk_exploration_event_character
  FOREIGN KEY (character_id) REFERENCES character(character_id);

-- ExplorationEventReward Foreign Keys
ALTER TABLE exploration_event_reward ADD CONSTRAINT fk_exploration_event_reward_event
  FOREIGN KEY (event_id) REFERENCES exploration_event(event_id);
```

### Indexes

```sql
-- Realm Indexes
CREATE UNIQUE INDEX uk_realm_level ON realm(realm_level);
CREATE INDEX idx_realm_stage ON realm(realm_stage);

-- Character Indexes
CREATE INDEX idx_character_player_name ON character(player_name);
CREATE INDEX idx_character_realm_id ON character(realm_id);
CREATE INDEX idx_character_sect_id ON character(sect_id);

-- Pill Indexes
CREATE INDEX idx_pill_tier ON pill(pill_tier);
CREATE INDEX idx_pill_quality ON pill(quality);
CREATE INDEX idx_pill_effect_type ON pill(effect_type);

-- Material Indexes
CREATE INDEX idx_material_type ON material(material_type);
CREATE INDEX idx_material_tier ON material(material_tier);
CREATE INDEX idx_material_quality ON material(quality);

-- CharacterPill Indexes
CREATE INDEX idx_character_pill_character ON character_pill(character_id);
CREATE UNIQUE INDEX uk_character_pill ON character_pill(character_id, pill_id);

-- CharacterMaterial Indexes
CREATE INDEX idx_character_material_character ON character_material(character_id);
CREATE UNIQUE INDEX uk_character_material ON character_material(character_id, material_id);

-- PillRecipe Indexes
CREATE INDEX idx_pill_recipe_pill ON pill_recipe(pill_id);
CREATE INDEX idx_pill_recipe_tier ON pill_recipe(recipe_tier);
CREATE INDEX idx_pill_recipe_alchemy_level ON pill_recipe(alchemy_level_required);

-- PillRecipeMaterial Indexes
CREATE INDEX idx_pill_recipe_material_recipe ON pill_recipe_material(recipe_id);
CREATE INDEX idx_pill_recipe_material_material ON pill_recipe_material(material_id);
CREATE UNIQUE INDEX uk_recipe_material ON pill_recipe_material(recipe_id, material_id);

-- AlchemyRecord Indexes
CREATE INDEX idx_alchemy_record_character ON alchemy_record(character_id);
CREATE INDEX idx_alchemy_record_recipe ON alchemy_record(recipe_id);
CREATE INDEX idx_alchemy_record_time ON alchemy_record(alchemy_time);
CREATE INDEX idx_alchemy_record_success ON alchemy_record(is_success);

-- Skill Indexes
CREATE INDEX idx_skill_function_type ON skill(function_type);
CREATE INDEX idx_skill_element_type ON skill(element_type);
CREATE INDEX idx_skill_sect_id ON skill(sect_id);

-- CharacterSkill Indexes
CREATE INDEX idx_character_skill_character ON character_skill(character_id);
CREATE UNIQUE INDEX uk_character_skill ON character_skill(character_id, skill_id);

-- Equipment Indexes
CREATE INDEX idx_equipment_type ON equipment(equipment_type);
CREATE INDEX idx_equipment_quality ON equipment(quality);
CREATE INDEX idx_equipment_enhancement ON equipment(enhancement_level);

-- CharacterEquipment Indexes
CREATE INDEX idx_character_equipment_character ON character_equipment(character_id);
CREATE UNIQUE INDEX uk_character_slot ON character_equipment(character_id, equipment_slot);

-- EquipmentRecipe Indexes
CREATE INDEX idx_equipment_recipe_equipment ON equipment_recipe(equipment_id);
CREATE INDEX idx_equipment_recipe_tier ON equipment_recipe(recipe_tier);
CREATE INDEX idx_equipment_recipe_forging_level ON equipment_recipe(forging_level_required);

-- EquipmentRecipeMaterial Indexes
CREATE INDEX idx_equipment_recipe_material_recipe ON equipment_recipe_material(recipe_id);
CREATE INDEX idx_equipment_recipe_material_material ON equipment_recipe_material(material_id);
CREATE UNIQUE INDEX uk_equipment_recipe_material ON equipment_recipe_material(recipe_id, material_id);

-- ForgeRecord Indexes
CREATE INDEX idx_forge_record_character ON forge_record(character_id);
CREATE INDEX idx_forge_record_recipe ON forge_record(recipe_id);
CREATE INDEX idx_forge_record_time ON forge_record(forge_time);
CREATE INDEX idx_forge_record_success ON forge_record(is_success);

-- Monster Indexes
CREATE INDEX idx_monster_realm_id ON monster(realm_id);
CREATE INDEX idx_monster_type ON monster(monster_type);
CREATE INDEX idx_monster_element ON monster(attack_element);

-- MonsterDrop Indexes
CREATE INDEX idx_monster_drop_monster ON monster_drop(monster_id);
CREATE INDEX idx_monster_drop_equipment ON monster_drop(equipment_id);
CREATE UNIQUE INDEX uk_monster_equipment ON monster_drop(monster_id, equipment_id);

-- CombatRecord Indexes
CREATE INDEX idx_combat_record_character ON combat_record(character_id);
CREATE INDEX idx_combat_record_monster ON combat_record(monster_id);
CREATE INDEX idx_combat_record_time ON combat_record(combat_time);
CREATE INDEX idx_combat_record_victory ON combat_record(is_victory);

-- CultivationRecord Indexes
CREATE INDEX idx_cultivation_record_character ON cultivation_record(character_id);
CREATE INDEX idx_cultivation_record_time ON cultivation_record(cultivation_time);

-- ExplorationEvent Indexes
CREATE INDEX idx_exploration_event_character ON exploration_event(character_id);
CREATE INDEX idx_exploration_event_type ON exploration_event(event_type);
CREATE INDEX idx_exploration_event_time ON exploration_event(exploration_time);

-- ExplorationRewardConfig Indexes
CREATE INDEX idx_exploration_reward_config_event_type ON exploration_reward_config(event_type);
CREATE INDEX idx_exploration_reward_config_reward_type ON exploration_reward_config(reward_type);
CREATE INDEX idx_exploration_reward_config_drop_rate ON exploration_reward_config(drop_rate);

-- ExplorationEventReward Indexes
CREATE INDEX idx_exploration_event_reward_event ON exploration_event_reward(event_id);
CREATE INDEX idx_exploration_event_reward_type ON exploration_event_reward(reward_type);
```

---

## Entity Relationship Diagram (ERD)

```
realm (境界配置)
├── character (该境界的角色)
└── monster (该境界的妖兽)

character (角色)
├── realm (当前境界) ← realm
├── character_pill (角色丹药) → pill (丹药)
├── character_material (角色材料) → material (材料)
├── character_equipment (角色装备) → equipment (装备)
├── character_skill (角色技能) → skill (技能)
├── cultivation_record (修炼记录)
├── combat_record (战斗记录) → monster (妖兽)
├── exploration_event (探索事件) → exploration_event_reward (实际奖励)
├── alchemy_record (炼丹记录) → pill_recipe (丹方)
├── forge_record (锻造记录) → equipment_recipe (装备图纸)
└── sect (宗门) ← skill (专属技能)

pill (丹药)
├── character_pill (角色丹药背包)
└── pill_recipe (丹方配方) → pill_recipe_material → material (材料)

material (材料)
├── character_material (角色材料背包)
├── pill_recipe_material (丹方材料) ← pill_recipe (丹方)
└── equipment_recipe_material (装备图纸材料) ← equipment_recipe (装备图纸)

pill_recipe (丹方)
├── pill (产出丹药) ← pill
├── pill_recipe_material (所需材料) → material (材料)
└── alchemy_record (炼丹记录)

equipment (装备)
├── monster_drop (怪物掉落) ← monster (妖兽)
└── equipment_recipe (装备图纸配方) → equipment_recipe_material → material (材料)

equipment_recipe (装备图纸)
├── equipment (产出装备) ← equipment
├── equipment_recipe_material (所需材料) → material (材料)
└── forge_record (锻造记录)

sect (宗门)
├── character (宗门成员)
└── skill (宗门专属技能)

exploration_event (探索事件)
└── exploration_event_reward (实际获得的奖励)

exploration_reward_config (探索奖励配置)
└── 定义各事件类型的掉落奖励及概率
```

---

## Validation Rules Summary

### Character (角色)
- `player_name`: 长度2-6字符(FR-001)
- `realm_level`: 1-9
- `experience`, `spiritual_power`, `stamina`, `health`: >= 0
- 五维属性(`constitution`, `spirit`, `comprehension`, `luck`, `fortune`): 1-100000
- `mindset`: 0-100

### Pill (丹药)
- `pill_tier`: 1-10 (丹药阶位)
- `effect_type`: 效果类型枚举(恢复气血, 恢复灵力, 增加经验, 突破辅助, 属性提升, 解毒, 疗伤)
- `stack_limit`: > 0

### Material (材料)
- `material_type`: 材料类型枚举(草药, 矿石, 兽骨, 妖丹, 灵木, 其他)
- `material_tier`: 1-10 (材料阶位)
- `stack_limit`: > 0

### Skill (技能)
- `tier`: 1-8 (技能阶位)
- 成长参数: `damage_growth_rate` >= 0, `multiplier_growth` >= 0, `spiritual_cost_growth` >= 0

### CharacterSkill (角色已学技能)
- `skill_level`: 1-99 (技能等级)
- `current_damage`, `current_multiplier`, `current_spiritual_cost`: 根据成长公式自动计算
- 成长公式: 参考CharacterSkill实体定义

### Equipment (装备)
- `enhancement_level`: 0-15
- 各抗性字段: 0-15 (单件装备上限)
- `critical_rate`: 0-15 (武器上限)
- 总抗性上限: 75% (所有装备叠加)

### Monster (妖兽)
- 各抗性字段: 0-50% (妖兽上限低于玩家)
- `stamina_cost`: 10(普通), 20(精英), 30(BOSS)

### MonsterDrop (怪物装备掉落配置)
- `drop_rate`: 0-100% (装备掉落率)
- `drop_quantity`: 1-10 (掉落数量)
- `is_guaranteed`: 0或1 (是否必掉)
- 唯一约束: (monster_id, equipment_id) - 同一怪物对同一装备只能配置一次

### CombatRecord (战斗记录)
- 失败时 `stamina_consumed` 减半(规范Edge Cases)

---

## Data Migration Strategy

### Initial Schema Creation

**File**: `src/main/resources/db/migration/V1__init_schema.sql`

**Content**:
- 创建所有表结构
- 创建所有索引
- 插入初始数据(妖兽、宗门、技能、物品)

### Seed Data

**Required Seed Data**:
1. **Realm Data**: 插入14个境界配置(凡人→道祖之境)
2. **Monster Data**: 插入示例妖兽(火焰蜥蜴、冰霜狼、普通野狼等)
3. **Sect Data**: 插入5个宗门(青云剑宗、丹霞谷、天机阁、无极魔宗、万兽山庄)
4. **Skill Data**: 插入初始技能库(宗门专属技能、通用技能)
5. **Pill Data**: 插入基础丹药(小还丹、聚灵丹、筑基丹等)
6. **Material Data**: 插入基础材料(血灵草、聚灵花、寒铁矿、赤焰石等)

---

## Summary

本文档定义了凡人修仙文字游戏的24个核心实体:

1. **Character** - 角色实体,核心玩家数据
2. **Realm** - 境界配置表,定义修仙境界等级、突破要求、属性加成
3. **Pill** - 丹药实体,恢复/突破/属性提升类丹药
4. **Material** - 材料实体,草药、矿石等炼药炼器材料
5. **CharacterPill** - 角色丹药背包关联
6. **CharacterMaterial** - 角色材料背包关联
7. **PillRecipe** - 丹方实体,定义丹药炼制配方、成功率、所需材料
8. **PillRecipeMaterial** - 丹方材料关联表,定义每个丹方需要的材料及数量
9. **AlchemyRecord** - 炼丹记录实体,记录角色炼丹历史、成功/失败、产出
10. **Skill** - 技能实体,攻击/防御/辅助/治疗技能
11. **CharacterSkill** - 角色已学技能关联
12. **Equipment** - 装备实体,武器、防具、饰品
13. **CharacterEquipment** - 角色已装备装备关联
14. **EquipmentRecipe** - 装备图纸实体,定义装备锻造配方、成功率、所需材料
15. **EquipmentRecipeMaterial** - 装备图纸材料关联表,定义每个图纸需要的材料及数量
16. **ForgeRecord** - 锻造记录实体,记录角色锻造历史、成功/失败、产出
17. **Monster** - 妖兽实体,普通/精英/BOSS
18. **MonsterDrop** - 怪物装备掉落配置,定义每个怪物的装备掉落率
19. **Sect** - 宗门实体,5大宗门
20. **CombatRecord** - 战斗记录
21. **CultivationRecord** - 修炼记录
22. **ExplorationEvent** - 探索事件记录
23. **ExplorationRewardConfig** - 探索奖励配置表,定义各事件类型的奖励及掉落率
24. **ExplorationEventReward** - 探索事件奖励记录表,记录每次探索实际获得的奖励

**Database**: MySQL 8.x
**ORM**: MyBatis-Plus 3.x
**Primary Key**: BIGINT Auto-Increment
**Soft Delete**: Logical delete (deleted = 0/1)
**Character Set**: utf8mb4 (支持中文)

**Next Step**: Phase 1 - 生成 contracts/ (API合约)
