/**
 * 宗门商店相关类型定义
 */

/**
 * 商店物品实体
 */
export interface SectShopItem {
  itemId?: number;
  sectId: number;
  itemType: string;
  refItemId: number;
  itemName: string;
  itemTier: number;
  description?: string;
  price: number;
  stockLimit: number;
  currentStock: number;
  requiredPosition: number;
}

/**
 * 商店物品列表项
 */
export interface SectShopItemListItem {
  itemId: number;
  sectName: string;
  itemType: string;
  itemName: string;
  itemTier: number;
  price: number;
  currentStock: number;
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
