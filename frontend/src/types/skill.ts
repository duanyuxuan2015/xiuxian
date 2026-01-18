/**
 * 技能相关类型定义
 */

/**
 * 技能实体
 */
export interface Skill {
  skillId?: number;
  skillName: string;
  functionType: string;
  elementType?: string;
  baseDamage: number;
  skillMultiplier: number;
  spiritualCost: number;
  damageGrowthRate?: number;
  multiplierGrowth?: number;
  spiritualCostGrowth?: number;
  description?: string;
  tier: number;
  sectId?: number | null;
  unlockMethod?: string;
  cost: number;
}

/**
 * 技能列表项
 */
export interface SkillListItem {
  skillId: number;
  skillName: string;
  functionType: string;
  elementType?: string;
  baseDamage: number;
  skillMultiplier: number;
  tier: number;
  sectId?: number | null;
  sectName?: string;
  cost: number;
}

/**
 * 技能详情
 */
export interface SkillDetail extends Skill {
  skillId: number;
  sectName?: string;
  createdAt?: string;
  updatedAt?: string;
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

/**
 * 宗门选项
 */
export interface SectOption {
  sectId: number;
  sectName: string;
}
