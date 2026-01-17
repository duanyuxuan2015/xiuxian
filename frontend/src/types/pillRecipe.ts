/**
 * 丹方相关类型定义
 */

/**
 * 丹方实体
 */
export interface PillRecipe {
  recipeId?: number;
  recipeName: string;
  pillId: number;
  outputQuantity: number;
  baseSuccessRate: number;
  alchemyLevelRequired: number;
  spiritualCost: number;
  staminaCost: number;
  duration: number;
  recipeTier: number;
  unlockMethod?: string;
  unlockCost?: number;
  description?: string;
  materials?: PillRecipeMaterial[];
}

/**
 * 丹方材料
 */
export interface PillRecipeMaterial {
  recipeMaterialId?: number;
  materialId: number;
  materialName?: string;
  quantityRequired: number;
  isMainMaterial: number;
}

/**
 * 丹方列表项
 */
export interface PillRecipeListItem {
  recipeId: number;
  recipeName: string;
  pillName: string;
  recipeTier: number;
  outputQuantity: number;
  baseSuccessRate: number;
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
 * 丹药选项
 */
export interface PillOption {
  pillId: number;
  pillName: string;
}

/**
 * 材料选项
 */
export interface MaterialOption {
  materialId: number;
  materialName: string;
}
