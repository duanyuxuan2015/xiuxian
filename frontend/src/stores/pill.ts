import { defineStore } from 'pinia';
import { ref } from 'vue';
import { pillApi } from '@/api/pill';
import type { Pill, PillListItem } from '@/types/pill';

export const usePillStore = defineStore('pill', () => {
  const currentPill = ref<Pill | null>(null);
  const loading = ref(false);
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
    items: [] as PillListItem[]
  });

  /**
   * 分页查询丹药列表
   */
  const fetchList = async (params?: {
    page?: number;
    pageSize?: number;
    keyword?: string;
    quality?: string;
    effectType?: string;
  }) => {
    loading.value = true;
    try {
      const response = await pillApi.getList({
        page: params?.page || pagination.value.page,
        pageSize: params?.pageSize || pagination.value.pageSize,
        keyword: params?.keyword,
        quality: params?.quality,
        effectType: params?.effectType
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
   * 获取丹药详情
   */
  const fetchDetail = async (pillId: number) => {
    loading.value = true;
    try {
      const response = await pillApi.getDetail(pillId);
      currentPill.value = response.data;
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 创建丹药
   */
  const createPill = async (data: Pill) => {
    loading.value = true;
    try {
      const response = await pillApi.create(data);
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 更新丹药
   */
  const updatePill = async (pillId: number, data: Pill) => {
    loading.value = true;
    try {
      await pillApi.update(pillId, data);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 删除丹药
   */
  const deletePill = async (pillId: number) => {
    loading.value = true;
    try {
      await pillApi.delete(pillId);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 批量删除丹药
   */
  const batchDeletePill = async (pillIds: number[]) => {
    loading.value = true;
    try {
      await pillApi.batchDelete(pillIds);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 检查名称唯一性
   */
  const checkNameUnique = async (name: string, excludeId?: number) => {
    const response = await pillApi.checkName(name, excludeId);
    return response.data.isUnique;
  };

  return {
    currentPill,
    loading,
    pagination,
    fetchList,
    fetchDetail,
    createPill,
    updatePill,
    deletePill,
    batchDeletePill,
    checkNameUnique
  };
});
