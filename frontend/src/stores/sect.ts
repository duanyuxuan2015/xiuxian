import { defineStore } from 'pinia';
import { ref } from 'vue';
import sectApi from '@/api/sect';
import type { Sect, SectListItem } from '@/types/sect';

export const useSectStore = defineStore('sect', () => {
  const currentSect = ref<Sect | null>(null);
  const loading = ref(false);
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
    items: [] as SectListItem[]
  });

  /**
   * 分页查询宗门列表
   */
  const fetchList = async (params?: {
    page?: number;
    pageSize?: number;
    keyword?: string;
    sectType?: string;
  }) => {
    loading.value = true;
    try {
      const response = await sectApi.list({
        page: params?.page || pagination.value.page,
        pageSize: params?.pageSize || pagination.value.pageSize,
        keyword: params?.keyword,
        sectType: params?.sectType
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
   * 获取宗门详情
   */
  const fetchDetail = async (sectId: number) => {
    loading.value = true;
    try {
      const response = await sectApi.getDetail(sectId);
      currentSect.value = response.data;
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 创建宗门
   */
  const createSect = async (data: Sect) => {
    loading.value = true;
    try {
      const response = await sectApi.create(data);
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 更新宗门
   */
  const updateSect = async (sectId: number, data: Sect) => {
    loading.value = true;
    try {
      await sectApi.update(sectId, data);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 删除宗门
   */
  const deleteSect = async (sectId: number) => {
    loading.value = true;
    try {
      await sectApi.delete(sectId);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 批量删除宗门
   */
  const batchDeleteSect = async (sectIds: number[]) => {
    loading.value = true;
    try {
      await sectApi.batchDelete(sectIds);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 检查名称唯一性
   */
  const checkNameUnique = async (name: string, excludeId?: number) => {
    const response = await sectApi.checkName(name, excludeId);
    return response.data;
  };

  return {
    currentSect,
    loading,
    pagination,
    fetchList,
    fetchDetail,
    createSect,
    updateSect,
    deleteSect,
    batchDeleteSect,
    checkNameUnique
  };
});
