/**
 * 装备相关类型定义
 */

export interface Equipment {
  equipmentId?: number;
  equipmentName: string;
  equipmentType: string;
  quality: string;
  baseScore: number;
  attackPower?: number;
  defensePower?: number;
  healthBonus?: number;
  criticalRate?: number;
  speedBonus?: number;
  physicalResist?: number;
  iceResist?: number;
  fireResist?: number;
  lightningResist?: number;
  enhancementLevel?: number;
  gemSlotCount?: number;
  specialEffects?: string;
}

export interface EquipmentListItem {
  equipmentId: number;
  equipmentName: string;
  equipmentType: string;
  quality: string;
  baseScore: number;
  attackPower?: number;
  defensePower?: number;
}

export interface PageResult<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
}
