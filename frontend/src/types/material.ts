/**
 * 材料相关类型定义
 */

/**
 * 材料实体
 */
export interface Material {
  materialId?: number;
  materialName: string;
  materialType: string;
  materialTier: number;
  quality: string;
  stackLimit: number;
  spiritStones: number;
  description?: string;
}

/**
 * 材料列表项
 */
export interface MaterialListItem {
  materialId: number;
  materialName: string;
  materialType: string;
  materialTier: number;
  quality: string;
  spiritStones: number;
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
 * 创建材料请求
 */
export interface MaterialCreateRequest {
  materialName: string;
  materialType: string;
  materialTier: number;
  quality: string;
  stackLimit: number;
  spiritStones: number;
  description?: string;
}

/**
 * 更新材料请求
 */
export interface MaterialUpdateRequest {
  materialName?: string;
  materialType?: string;
  materialTier?: number;
  quality?: string;
  stackLimit?: number;
  spiritStones?: number;
  description?: string;
}
