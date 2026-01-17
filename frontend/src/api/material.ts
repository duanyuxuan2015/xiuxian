/**
 * 材料管理 API
 */
import request from './index';
import type { Material, MaterialListItem, PageResult } from '@/types/material';

const materialApi = {
  /**
   * 分页查询材料列表
   */
  list: (params: {
    page: number;
    pageSize: number;
    keyword?: string;
    quality?: string;
    materialType?: string;
  }) => {
    return request.get<PageResult<MaterialListItem>>('/admin/material/list', { params });
  },

  /**
   * 获取材料详情
   */
  getDetail: (materialId: number) => {
    return request.get<Material>(`/admin/material/${materialId}`);
  },

  /**
   * 创建材料
   */
  create: (data: Material) => {
    return request.post<{ materialId: number }>('/admin/material', data);
  },

  /**
   * 更新材料
   */
  update: (materialId: number, data: Material) => {
    return request.put(`/admin/material/${materialId}`, data);
  },

  /**
   * 删除材料
   */
  delete: (materialId: number) => {
    return request.delete(`/admin/material/${materialId}`);
  },

  /**
   * 批量删除材料
   */
  batchDelete: (materialIds: number[]) => {
    return request.delete('/admin/material/batch', { data: materialIds });
  },

  /**
   * 检查名称唯一性
   */
  checkNameUnique: (name: string, excludeId?: number) => {
    return request.get<{ isUnique: boolean }>('/admin/material/check-name', {
      params: { name, excludeId }
    });
  },

  /**
   * 获取所有材料类型
   */
  getMaterialTypes: () => {
    return request.get<string[]>('/admin/material/material-types');
  }
};

export default materialApi;
