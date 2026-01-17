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
  equipmentId: number;
  equipmentName?: string;
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
