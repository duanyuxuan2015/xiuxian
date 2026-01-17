import { defineStore } from 'pinia';
import { ref } from 'vue';
import materialApi from '@/api/material';
import type { Material, MaterialListItem } from '@/types/material';

export const useMaterialStore = defineStore('material', () => {
  const currentMaterial = ref<Material | null>(null);
  const loading = ref(false);
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
    items: [] as MaterialListItem[]
  });

  /**
   * 分页查询材料列表
   */
  const fetchList = async (params?: {
    page?: number;
    pageSize?: number;
    keyword?: string;
    quality?: string;
    materialType?: string;
  }) => {
    loading.value = true;
    try {
      const response = await materialApi.list({
        page: params?.page || pagination.value.page,
        pageSize: params?.pageSize || pagination.value.pageSize,
        keyword: params?.keyword,
        quality: params?.quality,
        materialType: params?.materialType
      });

      pagination.value.items = response.data.items;
      pagination.value.total = response.data.total;
      pagination.value.page = response.data.page;
      pagination.value.pageSize = response.data.pageSize;

      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 获取材料详情
   */
  const fetchDetail = async (materialId: number) => {
    loading.value = true;
    try {
      const response = await materialApi.getDetail(materialId);
      currentMaterial.value = response.data;
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 创建材料
   */
  const createMaterial = async (data: Material) => {
    loading.value = true;
    try {
      const response = await materialApi.create(data);
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 更新材料
   */
  const updateMaterial = async (materialId: number, data: Material) => {
    loading.value = true;
    try {
      await materialApi.update(materialId, data);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 删除材料
   */
  const deleteMaterial = async (materialId: number) => {
    loading.value = true;
    try {
      await materialApi.delete(materialId);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 批量删除材料
   */
  const batchDeleteMaterial = async (materialIds: number[]) => {
    loading.value = true;
    try {
      await materialApi.batchDelete(materialIds);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 检查名称唯一性
   */
  const checkNameUnique = async (name: string, excludeId?: number) => {
    const response = await materialApi.checkNameUnique(name, excludeId);
    return response.data;
  };

  return {
    currentMaterial,
    loading,
    pagination,
    fetchList,
    fetchDetail,
    createMaterial,
    updateMaterial,
    deleteMaterial,
    batchDeleteMaterial,
    checkNameUnique
  };
});
