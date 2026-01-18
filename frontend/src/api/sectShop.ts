/**
 * 宗门商店管理 API
 */
import request from './index';
import type { SectShopItem, SectShopItemListItem, PageResult, SectOption } from '@/types/sectShop';

const sectShopApi = {
  /**
   * 分页查询商店物品列表
   */
  list: (params: {
    page: number;
    pageSize: number;
    keyword?: string;
    sectId?: number;
    itemType?: string;
  }) => {
    return request.get<PageResult<SectShopItemListItem>>('/admin/sect-shop/list', { params });
  },

  /**
   * 获取商店物品详情
   */
  getDetail: (itemId: number) => {
    return request.get<SectShopItem>(`/admin/sect-shop/${itemId}`);
  },

  /**
   * 创建商店物品
   */
  create: (data: SectShopItem) => {
    return request.post<{ itemId: number }>('/admin/sect-shop', data);
  },

  /**
   * 更新商店物品
   */
  update: (itemId: number, data: SectShopItem) => {
    return request.put(`/admin/sect-shop/${itemId}`, data);
  },

  /**
   * 删除商店物品
   */
  delete: (itemId: number) => {
    return request.delete(`/admin/sect-shop/${itemId}`);
  },

  /**
   * 批量删除商店物品
   */
  batchDelete: (itemIds: number[]) => {
    return request.delete('/admin/sect-shop/batch', { data: itemIds });
  },

  /**
   * 获取所有宗门列表
   */
  getSects: () => {
    return request.get<SectOption[]>('/admin/sect-shop/sects');
  },

  /**
   * 根据物品类型获取物品列表
   */
  getItemsByType: (itemType: string) => {
    return request.get<Array<{ itemId: number; itemName: string }>>('/admin/sect-shop/items', {
      params: { itemType }
    });
  },

  /**
   * 获取所有物品类型
   */
  getItemTypes: () => {
    return request.get<string[]>('/admin/sect-shop/item-types');
  }
};

export default sectShopApi;
