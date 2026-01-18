/**
 * 怪物相关类型定义
 */

export interface Monster {
  monsterId: number;
  monsterName: string;
  realmId: number;
  realmName?: string;
  monsterType: string;
  speed: number;
  hp: number;
  attackPower: number;
  defensePower: number;
  attackElement: string;
  physicalResist: number;
  iceResist: number;
  fireResist: number;
  lightningResist: number;
  staminaCost: number;
  expReward: number;
  spiritStonesReward: number;
}

export interface MonsterDrop {
  monsterDropId?: number;
  monsterId: number;
  itemType: 'equipment' | 'material'; // 物品类型
  itemId: number; // 物品ID（装备ID或材料ID）
  equipmentId?: number; // @deprecated 过渡期保留
  itemName?: string; // 动态获取的物品名称
  equipmentName?: string; // @deprecated 过渡期保留
  equipmentType?: string;
  quality?: string;
  dropRate: number;
  dropQuantity: number;
  minQuality?: string;
  maxQuality?: string;
  isGuaranteed: boolean;
}

export interface MonsterDetail extends Monster {
  drops: MonsterDrop[];
}

export interface MonsterListItem {
  monsterId: number;
  monsterName: string;
  realmId: number;
  realmName: string;
  monsterType: string;
  speed: number;
  hp: number;
  attackPower: number;
  defensePower: number;
}

export interface PageResult<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp?: string;
}
