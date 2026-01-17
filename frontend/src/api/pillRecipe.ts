/**
 * 丹方管理 API
 */
import request from './index';
import type { PillRecipe, PillRecipeListItem, PageResult, PillOption, MaterialOption } from '@/types/pillRecipe';

const pillRecipeApi = {
  /**
   * 分页查询丹方列表
   */
  list: (params: {
    page: number;
    pageSize: number;
    keyword?: string;
  }) => {
    return request.get<PageResult<PillRecipeListItem>>('/admin/pill-recipe/list', { params });
  },

  /**
   * 获取丹方详情
   */
  getDetail: (recipeId: number) => {
    return request.get<PillRecipe>(`/admin/pill-recipe/${recipeId}`);
  },

  /**
   * 创建丹方
   */
  create: (data: PillRecipe) => {
    return request.post<{ recipeId: number }>('/admin/pill-recipe', data);
  },

  /**
   * 更新丹方
   */
  update: (recipeId: number, data: PillRecipe) => {
    return request.put(`/admin/pill-recipe/${recipeId}`, data);
  },

  /**
   * 删除丹方
   */
  delete: (recipeId: number) => {
    return request.delete(`/admin/pill-recipe/${recipeId}`);
  },

  /**
   * 批量删除丹方
   */
  batchDelete: (recipeIds: number[]) => {
    return request.delete('/admin/pill-recipe/batch', { data: recipeIds });
  },

  /**
   * 检查名称唯一性
   */
  checkName: (name: string, excludeId?: number) => {
    return request.get<{ isUnique: boolean }>('/admin/pill-recipe/check-name', {
      params: { name, excludeId }
    });
  },

  /**
   * 获取所有丹药列表
   */
  getPills: () => {
    return request.get<PillOption[]>('/admin/pill-recipe/pills');
  },

  /**
   * 获取所有材料列表
   */
  getMaterials: () => {
    return request.get<MaterialOption[]>('/admin/pill-recipe/materials');
  }
};

export default pillRecipeApi;
