/**
 * 丹药相关类型定义
 */

export interface Pill {
  pillId?: number;
  pillName: string;
  pillTier: number;
  quality: string;
  effectType: string;
  effectValue: number;
  duration?: number;
  stackLimit: number;
  spiritStones: number;
  description?: string;
}

export interface PillListItem {
  pillId: number;
  pillName: string;
  pillTier: number;
  quality: string;
  effectType: string;
  effectValue: number;
  spiritStones: number;
}

export interface PageResult<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
}
