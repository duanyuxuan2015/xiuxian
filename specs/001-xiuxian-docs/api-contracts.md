# API Contracts

**Feature**: 凡人修仙文字游戏
**Date**: 2026-01-13
**Status**: Draft

## Overview

本文档定义凡人修仙文字游戏的RESTful API接口规范，基于data-model.md中定义的24个核心实体。

**Base URL**: `/api/v1`
**Protocol**: HTTP/HTTPS
**Content-Type**: `application/json`
**Character Set**: UTF-8

---

## API Design Principles

### 1. RESTful 设计规范
- 使用HTTP方法语义：GET(查询)、POST(创建)、PUT(更新)、DELETE(删除)
- 使用名词复数形式：`/characters`、`/pills`、`/skills`
- 使用层级路径表示资源关系：`/characters/{id}/equipment`

### 2. 统一响应格式

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": { /* 业务数据 */ },
  "timestamp": "2026-01-13T10:30:00Z"
}
```

**分页响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [ /* 数据列表 */ ],
    "total": 100,
    "page": 1,
    "pageSize": 20,
    "totalPages": 5
  },
  "timestamp": "2026-01-13T10:30:00Z"
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "参数验证失败",
  "errors": [
    {
      "field": "playerName",
      "message": "角色姓名长度必须在2-6个字符之间"
    }
  ],
  "timestamp": "2026-01-13T10:30:00Z"
}
```

### 3. HTTP状态码规范

| 状态码 | 说明 | 使用场景 |
|-------|------|---------|
| 200 | OK | 请求成功 |
| 201 | Created | 资源创建成功 |
| 204 | No Content | 删除成功 |
| 400 | Bad Request | 参数验证失败 |
| 401 | Unauthorized | 未认证 |
| 403 | Forbidden | 无权限 |
| 404 | Not Found | 资源不存在 |
| 409 | Conflict | 资源冲突(如重复创建) |
| 500 | Internal Server Error | 服务器内部错误 |

### 4. 参数验证规范

所有API请求参数必须符合以下验证规则：
- 必填参数不能为空
- 数值范围必须符合data-model.md中定义的validation规则
- 枚举值必须在允许的范围内
- 字符串长度必须符合限制

---

## 1. 角色管理 (Character Management)

### 1.1 创建角色

**Endpoint**: `POST /characters`

**描述**: 创建新的角色

**请求体**:
```json
{
  "playerName": "云天河",
  "constitution": 5,
  "spirit": 5,
  "comprehension": 5,
  "luck": 5,
  "fortune": 5
}
```

**请求参数验证**:
- `playerName`: 必填, 长度2-6字符
- `constitution`: 可选, 默认5, 范围1-100000
- `spirit`: 可选, 默认5, 范围1-100000
- `comprehension`: 可选, 默认5, 范围1-100000
- `luck`: 可选, 默认5, 范围1-100000
- `fortune`: 可选, 默认5, 范围1-100000

**响应 (201 Created)**:
```json
{
  "code": 201,
  "message": "角色创建成功",
  "data": {
    "characterId": 1001,
    "playerName": "云天河",
    "realmId": 1,
    "realmName": "凡人",
    "realmLevel": 1,
    "experience": 0,
    "availablePoints": 0,
    "spiritualPower": 0,
    "spiritualPowerMax": 100,
    "stamina": 100,
    "staminaMax": 100,
    "health": 100,
    "healthMax": 100,
    "mindset": 50,
    "constitution": 5,
    "spirit": 5,
    "comprehension": 5,
    "luck": 5,
    "fortune": 5,
    "currentState": "闲置",
    "createdAt": "2026-01-13T10:30:00Z"
  },
  "timestamp": "2026-01-13T10:30:00Z"
}
```

**错误响应**:
- `400`: 参数验证失败(姓名长度不符合要求)
- `409`: 角色姓名已存在

---

### 1.2 获取角色详情

**Endpoint**: `GET /characters/{characterId}`

**描述**: 获取指定角色的详细信息

**路径参数**:
- `characterId`: 角色ID

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "characterId": 1001,
    "playerName": "云天河",
    "realm": {
      "id": 2,
      "name": "炼气期",
      "level": 2,
      "subLevel": 3,
      "stage": "MORTAL"
    },
    "experience": 1500,
    "nextLevelExp": 2000,
    "availablePoints": 5,
    "spiritualPower": 150,
    "spiritualPowerMax": 200,
    "stamina": 80,
    "staminaMax": 120,
    "health": 180,
    "healthMax": 200,
    "mindset": 65,
    "attributes": {
      "constitution": 15,
      "spirit": 12,
      "comprehension": 8,
      "luck": 10,
      "fortune": 7
    },
    "sect": {
      "sectId": 1,
      "sectName": "青云剑宗",
      "position": "外门弟子",
      "contribution": 100,
      "reputation": 50
    },
    "currentState": "闲置",
    "createdAt": "2026-01-13T10:30:00Z",
    "updatedAt": "2026-01-13T12:00:00Z"
  },
  "timestamp": "2026-01-13T12:00:00Z"
}
```

**错误响应**:
- `404`: 角色不存在

---

### 1.3 属性加点

**Endpoint**: `POST /characters/{characterId}/attributes`

**描述**: 为角色分配可用属性点

**路径参数**:
- `characterId`: 角色ID

**请求体**:
```json
{
  "constitution": 2,
  "spirit": 1,
  "comprehension": 0,
  "luck": 1,
  "fortune": 1
}
```

**请求参数验证**:
- 总点数必须 <= 角色的`availablePoints`
- 每个属性加点数 >= 0
- 加点后属性值不能超过100000

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "属性加点成功",
  "data": {
    "availablePoints": 0,
    "attributes": {
      "constitution": 17,
      "spirit": 13,
      "comprehension": 8,
      "luck": 11,
      "fortune": 8
    }
  },
  "timestamp": "2026-01-13T12:05:00Z"
}
```

**错误响应**:
- `400`: 可用点数不足
- `400`: 属性值超过上限
- `404`: 角色不存在

---

### 1.4 获取角色列表

**Endpoint**: `GET /characters`

**描述**: 获取角色列表(分页)

**查询参数**:
- `page`: 页码, 默认1
- `pageSize`: 每页数量, 默认20, 最大100
- `realmId`: 境界ID筛选(可选)
- `sectId`: 宗门ID筛选(可选)
- `sortBy`: 排序字段(experience, realmLevel), 默认createdAt
- `sortOrder`: 排序方向(asc, desc), 默认desc

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "characterId": 1001,
        "playerName": "云天河",
        "realmName": "炼气期",
        "realmLevel": 3,
        "experience": 1500,
        "sectName": "青云剑宗",
        "currentState": "闲置"
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 20,
    "totalPages": 3
  },
  "timestamp": "2026-01-13T12:10:00Z"
}
```

---

## 2. 修炼系统 (Cultivation System)

### 2.1 开始修炼

**Endpoint**: `POST /characters/{characterId}/cultivation/start`

**描述**: 角色开始修炼,消耗体力获得经验值

**路径参数**:
- `characterId`: 角色ID

**请求体**:
```json
{
  "duration": 3600
}
```

**请求参数验证**:
- `duration`: 修炼时长(秒), 可选值: 300, 600, 1800, 3600, 7200
- 当前状态必须为"闲置"
- 体力必须 >= 5

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "开始修炼",
  "data": {
    "cultivationId": 5001,
    "startTime": "2026-01-13T12:15:00Z",
    "endTime": "2026-01-13T13:15:00Z",
    "staminaCost": 5,
    "expectedExp": 100,
    "currentState": "修炼中"
  },
  "timestamp": "2026-01-13T12:15:00Z"
}
```

**错误响应**:
- `400`: 体力不足
- `400`: 角色当前不是闲置状态
- `404`: 角色不存在

---

### 2.2 完成修炼

**Endpoint**: `POST /characters/{characterId}/cultivation/{cultivationId}/complete`

**描述**: 完成修炼,获得经验值和潜在升级

**路径参数**:
- `characterId`: 角色ID
- `cultivationId`: 修炼记录ID

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "修炼完成",
  "data": {
    "cultivationId": 5001,
    "expGained": 100,
    "levelUp": true,
    "newRealmLevel": 4,
    "availablePoints": 5,
    "currentExp": 200,
    "nextLevelExp": 2500,
    "currentState": "闲置"
  },
  "timestamp": "2026-01-13T13:15:00Z"
}
```

**错误响应**:
- `400`: 修炼尚未完成
- `404`: 修炼记录不存在

---

### 2.3 境界突破

**Endpoint**: `POST /characters/{characterId}/breakthrough`

**描述**: 尝试突破到下一个境界

**路径参数**:
- `characterId`: 角色ID

**请求体**:
```json
{
  "usePill": true,
  "pillId": 3
}
```

**请求参数验证**:
- `usePill`: 是否使用突破丹药(可选)
- `pillId`: 突破丹药ID(usePill=true时必填)
- 当前境界层数必须 = 9
- 经验值必须达到突破要求

**响应 (200 OK - 成功)**:
```json
{
  "code": 200,
  "message": "突破成功",
  "data": {
    "success": true,
    "oldRealm": {
      "id": 2,
      "name": "炼气期",
      "level": 9
    },
    "newRealm": {
      "id": 3,
      "name": "筑基期",
      "level": 1
    },
    "successRate": 85,
    "bonusAttributes": {
      "healthMax": 50,
      "spiritualPowerMax": 20
    }
  },
  "timestamp": "2026-01-13T14:00:00Z"
}
```

**响应 (200 OK - 失败)**:
```json
{
  "code": 200,
  "message": "突破失败",
  "data": {
    "success": false,
    "successRate": 70,
    "penalty": "损失50%当前经验值"
  },
  "timestamp": "2026-01-13T14:00:00Z"
}
```

**错误响应**:
- `400`: 境界层数不足9层
- `400`: 经验值未达到突破要求
- `404`: 突破丹药不存在或不足

---

## 3. 战斗系统 (Combat System)

### 3.1 发起战斗

**Endpoint**: `POST /characters/{characterId}/combat/start`

**描述**: 角色向妖兽发起战斗

**路径参数**:
- `characterId`: 角色ID

**请求体**:
```json
{
  "monsterId": 101,
  "combatMode": "手动战斗"
}
```

**请求参数验证**:
- `monsterId`: 妖兽ID(必填)
- `combatMode`: 战斗模式, Enum(手动战斗, 自动战斗, 挂机战斗), 默认"手动战斗"
- 当前状态必须为"闲置"
- 体力必须 >= 妖兽的stamina_cost

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "战斗开始",
  "data": {
    "combatId": 8001,
    "monster": {
      "monsterId": 101,
      "monsterName": "火焰蜥蜴",
      "hp": 500,
      "attackPower": 80,
      "defensePower": 40,
      "attackElement": "火系"
    },
    "character": {
      "hp": 200,
      "attackPower": 100,
      "defensePower": 50,
      "speed": 70
    },
    "turnOrder": ["character", "monster"],
    "currentState": "战斗中"
  },
  "timestamp": "2026-01-13T15:00:00Z"
}
```

**错误响应**:
- `400`: 体力不足
- `400`: 角色不是闲置状态
- `404`: 妖兽不存在

---

### 3.2 执行战斗回合

**Endpoint**: `POST /characters/{characterId}/combat/{combatId}/action`

**描述**: 手动战斗模式下执行战斗行动

**路径参数**:
- `characterId`: 角色ID
- `combatId`: 战斗ID

**请求体**:
```json
{
  "actionType": "attack",
  "skillId": 5
}
```

**请求参数验证**:
- `actionType`: 行动类型, Enum(attack, defend, useItem, escape)
- `skillId`: 使用技能ID(actionType=attack时可选)
- `itemId`: 使用物品ID(actionType=useItem时必填)

**响应 (200 OK - 战斗继续)**:
```json
{
  "code": 200,
  "message": "行动执行成功",
  "data": {
    "combatId": 8001,
    "turn": 3,
    "action": {
      "actor": "character",
      "type": "attack",
      "skillName": "火球术",
      "damage": 120,
      "isCritical": true,
      "targetHp": 380
    },
    "monsterAction": {
      "actor": "monster",
      "type": "attack",
      "damage": 60,
      "characterHp": 140
    },
    "combatStatus": "ongoing"
  },
  "timestamp": "2026-01-13T15:01:00Z"
}
```

**响应 (200 OK - 战斗结束/胜利)**:
```json
{
  "code": 200,
  "message": "战斗胜利",
  "data": {
    "combatId": 8001,
    "isVictory": true,
    "turns": 5,
    "damageDealt": 500,
    "damageTaken": 180,
    "criticalHits": 2,
    "expGained": 200,
    "spiritStonesGained": 50,
    "itemsDropped": [
      {
        "itemType": "equipment",
        "itemId": 15,
        "itemName": "烈焰剑",
        "quality": "稀有"
      }
    ],
    "currentState": "闲置"
  },
  "timestamp": "2026-01-13T15:05:00Z"
}
```

**错误响应**:
- `400`: 不是角色的回合
- `404`: 战斗不存在

---

### 3.3 获取战斗记录

**Endpoint**: `GET /characters/{characterId}/combat/history`

**描述**: 获取角色的战斗历史记录

**路径参数**:
- `characterId`: 角色ID

**查询参数**:
- `page`: 页码, 默认1
- `pageSize`: 每页数量, 默认20
- `isVictory`: 筛选胜利/失败(可选, true/false)
- `startDate`: 开始日期(可选)
- `endDate`: 结束日期(可选)

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "combatId": 8001,
        "monsterName": "火焰蜥蜴",
        "combatMode": "手动战斗",
        "isVictory": true,
        "turns": 5,
        "expGained": 200,
        "itemsCount": 1,
        "combatTime": "2026-01-13T15:00:00Z"
      }
    ],
    "total": 30,
    "page": 1,
    "pageSize": 20,
    "totalPages": 2
  },
  "timestamp": "2026-01-13T15:10:00Z"
}
```

---

## 4. 炼丹系统 (Alchemy System)

### 4.1 获取丹方列表

**Endpoint**: `GET /pill-recipes`

**描述**: 获取所有可用的丹方列表

**查询参数**:
- `page`: 页码, 默认1
- `pageSize`: 每页数量, 默认20
- `tier`: 丹方阶位筛选(可选)
- `alchemyLevel`: 炼丹等级筛选(可选)

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "recipeId": 1,
        "recipeName": "小还丹丹方",
        "pill": {
          "pillId": 1,
          "pillName": "小还丹",
          "tier": 1,
          "quality": "普通",
          "effectType": "恢复气血",
          "effectValue": 100
        },
        "outputQuantity": 3,
        "baseSuccessRate": 80,
        "alchemyLevelRequired": 1,
        "spiritualCost": 50,
        "staminaCost": 10,
        "duration": 300,
        "materials": [
          {
            "materialId": 1,
            "materialName": "血灵草",
            "quantityRequired": 3,
            "isMainMaterial": true
          },
          {
            "materialId": 2,
            "materialName": "凝血草",
            "quantityRequired": 2,
            "isMainMaterial": false
          }
        ]
      }
    ],
    "total": 15,
    "page": 1,
    "pageSize": 20,
    "totalPages": 1
  },
  "timestamp": "2026-01-13T16:00:00Z"
}
```

---

### 4.2 开始炼丹

**Endpoint**: `POST /characters/{characterId}/alchemy/start`

**描述**: 开始炼制丹药

**路径参数**:
- `characterId`: 角色ID

**请求体**:
```json
{
  "recipeId": 1,
  "quantity": 1
}
```

**请求参数验证**:
- `recipeId`: 丹方ID(必填)
- `quantity`: 炼制次数, 默认1, 范围1-10
- 角色炼丹等级必须 >= 丹方要求等级
- 灵力必须 >= 丹方消耗 × quantity
- 体力必须 >= 丹方消耗 × quantity
- 材料数量必须足够

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "开始炼丹",
  "data": {
    "alchemyId": 6001,
    "recipeId": 1,
    "recipeName": "小还丹丹方",
    "startTime": "2026-01-13T16:05:00Z",
    "endTime": "2026-01-13T16:10:00Z",
    "spiritualConsumed": 50,
    "staminaConsumed": 10,
    "materialsConsumed": [
      {
        "materialId": 1,
        "materialName": "血灵草",
        "quantity": 3
      }
    ],
    "currentState": "炼丹中"
  },
  "timestamp": "2026-01-13T16:05:00Z"
}
```

**错误响应**:
- `400`: 炼丹等级不足
- `400`: 灵力不足
- `400`: 体力不足
- `400`: 材料不足
- `404`: 丹方不存在

---

### 4.3 完成炼丹

**Endpoint**: `POST /characters/{characterId}/alchemy/{alchemyId}/complete`

**描述**: 完成炼丹,获得炼丹结果

**路径参数**:
- `characterId`: 角色ID
- `alchemyId`: 炼丹记录ID

**响应 (200 OK - 成功)**:
```json
{
  "code": 200,
  "message": "炼丹成功",
  "data": {
    "alchemyId": 6001,
    "isSuccess": true,
    "successRate": 85,
    "outputQuantity": 3,
    "outputQuality": "稀有",
    "pill": {
      "pillId": 1,
      "pillName": "小还丹"
    },
    "expGained": 100,
    "alchemyLevelUp": false,
    "currentState": "闲置"
  },
  "timestamp": "2026-01-13T16:10:00Z"
}
```

**响应 (200 OK - 失败)**:
```json
{
  "code": 200,
  "message": "炼丹失败",
  "data": {
    "alchemyId": 6001,
    "isSuccess": false,
    "successRate": 45,
    "outputQuantity": 0,
    "expGained": 50,
    "currentState": "闲置"
  },
  "timestamp": "2026-01-13T16:10:00Z"
}
```

---

### 4.4 获取炼丹记录

**Endpoint**: `GET /characters/{characterId}/alchemy/history`

**描述**: 获取角色的炼丹历史记录

**查询参数**:
- `page`: 页码
- `pageSize`: 每页数量
- `isSuccess`: 筛选成功/失败(可选)

**响应**: (分页格式同战斗记录)

---

## 5. 装备锻造系统 (Forging System)

### 5.1 获取装备图纸列表

**Endpoint**: `GET /equipment-recipes`

**描述**: 获取所有可用的装备图纸列表

**查询参数**:
- `page`: 页码
- `pageSize`: 每页数量
- `tier`: 图纸阶位筛选(可选)
- `equipmentType`: 装备类型筛选(可选)

**响应格式**: (类似丹方列表)

---

### 5.2 开始锻造

**Endpoint**: `POST /characters/{characterId}/forge/start`

**描述**: 开始锻造装备

**请求体**:
```json
{
  "recipeId": 10,
  "quantity": 1
}
```

**响应格式**: (类似炼丹开始)

---

### 5.3 完成锻造

**Endpoint**: `POST /characters/{characterId}/forge/{forgeId}/complete`

**描述**: 完成锻造,获得锻造结果

**响应 (200 OK - 成功)**:
```json
{
  "code": 200,
  "message": "锻造成功",
  "data": {
    "forgeId": 7001,
    "isSuccess": true,
    "successRate": 70,
    "equipment": {
      "equipmentId": 25,
      "equipmentName": "寒铁剑",
      "equipmentType": "武器",
      "quality": "史诗",
      "enhancementLevel": 3,
      "attackPower": 85,
      "criticalRate": 5
    },
    "expGained": 200,
    "forgingLevelUp": true,
    "currentState": "闲置"
  },
  "timestamp": "2026-01-13T17:00:00Z"
}
```

---

## 6. 背包管理 (Inventory Management)

### 6.1 获取背包物品

**Endpoint**: `GET /characters/{characterId}/inventory`

**描述**: 获取角色背包中的所有物品

**查询参数**:
- `itemType`: 物品类型, Enum(pill, material, equipment), 可选

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "pills": [
      {
        "pillId": 1,
        "pillName": "小还丹",
        "tier": 1,
        "quality": "普通",
        "quantity": 15,
        "effectType": "恢复气血",
        "effectValue": 100
      }
    ],
    "materials": [
      {
        "materialId": 1,
        "materialName": "血灵草",
        "materialType": "草药",
        "tier": 1,
        "quantity": 50
      }
    ],
    "equipment": [
      {
        "equipmentId": 20,
        "equipmentName": "新手剑",
        "equipmentType": "武器",
        "quality": "普通",
        "attackPower": 30,
        "isEquipped": true
      }
    ]
  },
  "timestamp": "2026-01-13T18:00:00Z"
}
```

---

### 6.2 使用丹药

**Endpoint**: `POST /characters/{characterId}/inventory/pills/{pillId}/use`

**描述**: 使用背包中的丹药

**路径参数**:
- `characterId`: 角色ID
- `pillId`: 丹药ID

**请求体**:
```json
{
  "quantity": 1
}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "使用丹药成功",
  "data": {
    "pillName": "小还丹",
    "effectType": "恢复气血",
    "effectValue": 100,
    "healthBefore": 80,
    "healthAfter": 180,
    "remainingQuantity": 14
  },
  "timestamp": "2026-01-13T18:05:00Z"
}
```

**错误响应**:
- `400`: 丹药数量不足
- `404`: 丹药不存在

---

### 6.3 装备装备

**Endpoint**: `POST /characters/{characterId}/equipment/{equipmentId}/equip`

**描述**: 装备指定装备

**路径参数**:
- `characterId`: 角色ID
- `equipmentId`: 装备ID

**请求体**:
```json
{
  "slot": "武器"
}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "装备成功",
  "data": {
    "equipment": {
      "equipmentId": 25,
      "equipmentName": "寒铁剑",
      "slot": "武器",
      "attackPower": 85
    },
    "previousEquipment": {
      "equipmentId": 20,
      "equipmentName": "新手剑"
    },
    "attributeChanges": {
      "attackPower": "+55"
    }
  },
  "timestamp": "2026-01-13T18:10:00Z"
}
```

---

### 6.4 卸下装备

**Endpoint**: `POST /characters/{characterId}/equipment/{equipmentId}/unequip`

**描述**: 卸下指定装备

**响应格式**: (类似装备装备)

---

## 7. 技能系统 (Skill System)

### 7.1 获取技能列表

**Endpoint**: `GET /skills`

**描述**: 获取所有可学习的技能列表

**查询参数**:
- `functionType`: 功能分类(攻击, 防御, 辅助, 治疗)
- `elementType`: 元素分类(物理系, 冰系, 火系, 雷系)
- `tier`: 技能阶位

**响应格式**: (分页列表)

---

### 7.2 学习技能

**Endpoint**: `POST /characters/{characterId}/skills/{skillId}/learn`

**描述**: 学习新技能

**路径参数**:
- `characterId`: 角色ID
- `skillId`: 技能ID

**请求体**:
```json
{
  "paymentType": "spirit_stones",
  "cost": 1000
}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "技能学习成功",
  "data": {
    "skillId": 5,
    "skillName": "火球术",
    "functionType": "攻击",
    "elementType": "火系",
    "tier": 2,
    "skillLevel": 1,
    "currentDamage": 100,
    "currentMultiplier": 2.0,
    "currentSpiritualCost": 20
  },
  "timestamp": "2026-01-13T19:00:00Z"
}
```

**错误响应**:
- `400`: 灵石不足
- `409`: 技能已学习

---

### 7.3 获取已学技能

**Endpoint**: `GET /characters/{characterId}/skills`

**描述**: 获取角色已学习的所有技能

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "skills": [
      {
        "skillId": 5,
        "skillName": "火球术",
        "functionType": "攻击",
        "elementType": "火系",
        "tier": 2,
        "skillLevel": 10,
        "proficiency": 5000,
        "currentDamage": 190,
        "currentMultiplier": 2.45,
        "currentSpiritualCost": 38,
        "isEquipped": true,
        "equipmentSlot": "攻击槽1"
      }
    ],
    "totalSkills": 8,
    "equippedSkills": 4
  },
  "timestamp": "2026-01-13T19:05:00Z"
}
```

---

### 7.4 装备技能

**Endpoint**: `POST /characters/{characterId}/skills/{skillId}/equip`

**描述**: 将技能装备到技能槽

**请求体**:
```json
{
  "slot": "攻击槽1"
}
```

**响应格式**: (类似装备装备)

---

## 8. 探索系统 (Exploration System)

### 8.1 开始探索

**Endpoint**: `POST /characters/{characterId}/exploration/start`

**描述**: 开始探索,触发随机事件

**请求体**:
```json
{
  "duration": 1800
}
```

**请求参数验证**:
- `duration`: 探索时长(秒), 可选值: 600, 1800, 3600
- 体力必须 >= 5

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "开始探索",
  "data": {
    "startTime": "2026-01-13T20:00:00Z",
    "endTime": "2026-01-13T20:30:00Z",
    "staminaCost": 5,
    "currentState": "探索中"
  },
  "timestamp": "2026-01-13T20:00:00Z"
}
```

---

### 8.2 完成探索

**Endpoint**: `POST /characters/{characterId}/exploration/complete`

**描述**: 完成探索,获得随机事件奖励

**响应 (200 OK - TREASURE事件)**:
```json
{
  "code": 200,
  "message": "探索完成 - 发现宝藏",
  "data": {
    "eventId": 9001,
    "eventType": "TREASURE",
    "eventDescription": "你在山洞中发现了一个古老的宝箱",
    "rewards": [
      {
        "rewardType": "材料",
        "itemId": 10,
        "itemName": "寒铁矿",
        "quantity": 7
      },
      {
        "rewardType": "灵石",
        "quantity": 350
      }
    ],
    "currentState": "闲置"
  },
  "timestamp": "2026-01-13T20:30:00Z"
}
```

**响应 (200 OK - INHERITANCE事件)**:
```json
{
  "code": 200,
  "message": "探索完成 - 获得传承",
  "data": {
    "eventId": 9002,
    "eventType": "INHERITANCE",
    "eventDescription": "你触发了上古修士的传承",
    "rewards": [
      {
        "rewardType": "技能",
        "itemId": 15,
        "itemName": "天雷诀",
        "quantity": 1
      },
      {
        "rewardType": "属性提升",
        "attribute": "comprehension",
        "quantity": 5
      }
    ],
    "currentState": "闲置"
  },
  "timestamp": "2026-01-13T20:30:00Z"
}
```

---

### 8.3 获取探索记录

**Endpoint**: `GET /characters/{characterId}/exploration/history`

**描述**: 获取角色的探索历史记录

**响应格式**: (分页列表)

---

## 9. 宗门系统 (Sect System)

### 9.1 获取宗门列表

**Endpoint**: `GET /sects`

**描述**: 获取所有宗门信息

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sects": [
      {
        "sectId": 1,
        "sectName": "青云剑宗",
        "sectType": "青云剑宗",
        "description": "以剑道闻名天下的正派宗门",
        "skillFocus": "剑法系技能",
        "joinRequirement": "炼气期",
        "memberCount": 150
      }
    ],
    "total": 5
  },
  "timestamp": "2026-01-13T21:00:00Z"
}
```

---

### 9.2 加入宗门

**Endpoint**: `POST /characters/{characterId}/sect/{sectId}/join`

**描述**: 角色加入指定宗门

**路径参数**:
- `characterId`: 角色ID
- `sectId`: 宗门ID

**请求参数验证**:
- 角色境界必须 >= 宗门加入要求
- 角色当前未加入任何宗门

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "加入宗门成功",
  "data": {
    "sectId": 1,
    "sectName": "青云剑宗",
    "position": "外门弟子",
    "contribution": 0,
    "reputation": 0
  },
  "timestamp": "2026-01-13T21:05:00Z"
}
```

**错误响应**:
- `400`: 境界不满足要求
- `409`: 已加入其他宗门

---

### 9.3 宗门贡献

**Endpoint**: `POST /characters/{characterId}/sect/contribute`

**描述**: 向宗门贡献资源获得贡献度

**请求体**:
```json
{
  "contributionType": "spirit_stones",
  "amount": 1000
}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "贡献成功",
  "data": {
    "contributionGained": 100,
    "totalContribution": 1100,
    "reputationGained": 10,
    "totalReputation": 60
  },
  "timestamp": "2026-01-13T21:10:00Z"
}
```

---

### 9.4 宗门商店

**Endpoint**: `GET /characters/{characterId}/sect/shop`

**描述**: 获取宗门商店可兑换的物品

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "itemType": "skill",
        "itemId": 20,
        "itemName": "青云剑诀",
        "contributionCost": 500,
        "reputationRequired": 100
      },
      {
        "itemType": "pill_recipe",
        "itemId": 5,
        "itemName": "聚灵丹丹方",
        "contributionCost": 300
      }
    ]
  },
  "timestamp": "2026-01-13T21:15:00Z"
}
```

---

### 9.5 宗门兑换

**Endpoint**: `POST /characters/{characterId}/sect/exchange`

**描述**: 使用贡献度兑换宗门物品

**请求体**:
```json
{
  "itemType": "skill",
  "itemId": 20
}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "兑换成功",
  "data": {
    "itemName": "青云剑诀",
    "contributionCost": 500,
    "remainingContribution": 600
  },
  "timestamp": "2026-01-13T21:20:00Z"
}
```

---

## 10. 统计查询 (Statistics)

### 10.1 角色统计

**Endpoint**: `GET /characters/{characterId}/statistics`

**描述**: 获取角色的统计信息

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "combat": {
      "totalBattles": 50,
      "victories": 42,
      "defeats": 8,
      "winRate": 84.0,
      "totalDamageDealt": 25000,
      "totalDamageTaken": 8000,
      "criticalHits": 120
    },
    "cultivation": {
      "totalCultivationTime": 36000,
      "totalExpGained": 15000,
      "currentRealmDays": 10
    },
    "alchemy": {
      "totalAttempts": 30,
      "successCount": 24,
      "successRate": 80.0,
      "alchemyLevel": 15
    },
    "forging": {
      "totalAttempts": 15,
      "successCount": 10,
      "successRate": 66.7,
      "forgingLevel": 12
    },
    "exploration": {
      "totalExplorations": 20,
      "treasureFound": 8,
      "inheritanceGained": 2
    }
  },
  "timestamp": "2026-01-13T22:00:00Z"
}
```

---

## 11. 系统配置 (System Configuration)

### 11.1 获取境界配置

**Endpoint**: `GET /config/realms`

**描述**: 获取所有境界配置信息

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "realms": [
      {
        "id": 1,
        "realmName": "凡人",
        "realmLevel": 1,
        "subLevels": 9,
        "requiredExp": 0,
        "breakthroughRate": 100,
        "levelUpPoints": 5,
        "realmStage": "MORTAL"
      },
      {
        "id": 2,
        "realmName": "炼气期",
        "realmLevel": 2,
        "subLevels": 9,
        "requiredExp": 100,
        "breakthroughRate": 90,
        "hpBonus": 10,
        "spBonus": 0,
        "levelUpPoints": 5,
        "realmStage": "MORTAL"
      }
    ],
    "total": 14
  },
  "timestamp": "2026-01-13T22:05:00Z"
}
```

---

### 11.2 获取妖兽列表

**Endpoint**: `GET /config/monsters`

**描述**: 获取所有妖兽配置信息

**查询参数**:
- `realmId`: 境界ID筛选
- `monsterType`: 妖兽类型(普通, 精英, BOSS)

**响应格式**: (分页列表)

---

## 12. WebSocket实时通知

### 12.1 连接WebSocket

**Endpoint**: `ws://host/ws/characters/{characterId}`

**描述**: 建立WebSocket连接接收实时通知

**连接参数**:
- `characterId`: 角色ID

**消息格式**:
```json
{
  "type": "cultivation_complete",
  "timestamp": "2026-01-13T13:15:00Z",
  "data": {
    "cultivationId": 5001,
    "expGained": 100,
    "levelUp": true
  }
}
```

**通知类型**:
- `cultivation_complete`: 修炼完成
- `alchemy_complete`: 炼丹完成
- `forge_complete`: 锻造完成
- `combat_action`: 战斗回合
- `combat_complete`: 战斗完成
- `exploration_complete`: 探索完成
- `stamina_recovered`: 体力恢复
- `spiritual_power_recovered`: 灵力恢复

---

## Summary

本API合约文档定义了凡人修仙文字游戏的完整RESTful API接口规范，涵盖：

1. **角色管理** - 创建、查询、属性加点
2. **修炼系统** - 修炼、境界突破
3. **战斗系统** - 发起战斗、执行回合、查询记录
4. **炼丹系统** - 查询丹方、炼丹、查询记录
5. **锻造系统** - 查询图纸、锻造装备
6. **背包管理** - 查询物品、使用丹药、装备管理
7. **技能系统** - 学习技能、装备技能
8. **探索系统** - 探索事件、奖励获取
9. **宗门系统** - 加入宗门、贡献、兑换
10. **统计查询** - 角色数据统计
11. **系统配置** - 境界、妖兽配置查询
12. **实时通知** - WebSocket推送

**技术栈**:
- RESTful API设计
- JSON数据格式
- WebSocket实时通信
- 分页查询
- 参数验证

**Next Step**: Phase 2 - 生成数据库Schema DDL
