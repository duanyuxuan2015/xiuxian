/**
 * 探索区域类型定义
 */
export interface ExplorationArea {
  areaId: number
  areaName: string
  description?: string
  requiredRealmLevel: number
  dangerLevel: number
  spiritCost: number
  staminaCost: number
  baseExploreTime?: number
}

/**
 * 探索区域表单数据
 */
export interface ExplorationAreaFormData {
  areaId?: number
  areaName: string
  description: string
  requiredRealmLevel: number
  dangerLevel: number
  spiritCost: number
  staminaCost: number
  baseExploreTime: number
}

/**
 * 探索事件类型定义
 */
export interface ExplorationEvent {
  eventId: number
  areaId: number
  eventType: string
  eventName: string
  description?: string
  level: number
  rewardType?: string
  rewardId?: number
  rewardQuantityMin?: number
  rewardQuantityMax?: number
  monsterId?: number
}

/**
 * 探索事件表单数据
 */
export interface ExplorationEventFormData {
  eventId?: number
  areaId: number
  eventType: string
  eventName: string
  description: string
  level: number
  rewardType: string
  rewardId?: number
  rewardQuantityMin?: number
  rewardQuantityMax?: number
  monsterId?: number
}

/**
 * 事件类型枚举
 */
export const EventTypes = [
  { label: '采集', value: 'gather' },
  { label: '战斗', value: 'combat' },
  { label: '机缘', value: 'fortune' },
  { label: '陷阱', value: 'trap' },
  { label: '无事', value: 'none' }
] as const

export type EventType = typeof EventTypes[number]['value']

/**
 * 奖励类型枚举
 */
export const RewardTypes = [
  { label: '材料', value: 'material' },
  { label: '丹药', value: 'pill' },
  { label: '装备', value: 'equipment' }
] as const

export type RewardType = typeof RewardTypes[number]['value']

/**
 * 事件级别枚举
 */
export const EventLevels = [
  { label: '1级（常见）', value: 1 },
  { label: '2级（普通）', value: 2 },
  { label: '3级（稀有）', value: 3 },
  { label: '4级（史诗）', value: 4 }
] as const
