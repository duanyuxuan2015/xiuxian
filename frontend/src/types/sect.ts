/**
 * 宗门相关类型定义
 */

/**
 * 宗门实体
 */
export interface Sect {
  sectId?: number;
  sectName: string;
  sectType: string;
  description?: string;
  specialty?: string;
  requiredRealmLevel: number;
  skillFocus?: string;
  joinRequirement?: string;
}

/**
 * 宗门列表项
 */
export interface SectListItem {
  sectId: number;
  sectName: string;
  sectType: string;
  requiredRealmLevel: number;
  specialty: string;
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
}
