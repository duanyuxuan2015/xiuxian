import { defineStore } from 'pinia';
import { ref } from 'vue';
import pillRecipeApi from '@/api/pillRecipe';
import type { PillRecipe, PillRecipeListItem } from '@/types/pillRecipe';

export const usePillRecipeStore = defineStore('pillRecipe', () => {
  const currentRecipe = ref<PillRecipe | null>(null);
  const loading = ref(false);
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
    items: [] as PillRecipeListItem[]
  });

  /**
   * 分页查询丹方列表
   */
  const fetchList = async (params?: {
    page?: number;
    pageSize?: number;
    keyword?: string;
  }) => {
    loading.value = true;
    try {
      const response = await pillRecipeApi.list({
        page: params?.page || pagination.value.page,
        pageSize: params?.pageSize || pagination.value.pageSize,
        keyword: params?.keyword
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
   * 获取丹方详情
   */
  const fetchDetail = async (recipeId: number) => {
    loading.value = true;
    try {
      const response = await pillRecipeApi.getDetail(recipeId);
      currentRecipe.value = response.data;
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 创建丹方
   */
  const createRecipe = async (data: PillRecipe) => {
    loading.value = true;
    try {
      const response = await pillRecipeApi.create(data);
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 更新丹方
   */
  const updateRecipe = async (recipeId: number, data: PillRecipe) => {
    loading.value = true;
    try {
      await pillRecipeApi.update(recipeId, data);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 删除丹方
   */
  const deleteRecipe = async (recipeId: number) => {
    loading.value = true;
    try {
      await pillRecipeApi.delete(recipeId);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 批量删除丹方
   */
  const batchDeleteRecipe = async (recipeIds: number[]) => {
    loading.value = true;
    try {
      await pillRecipeApi.batchDelete(recipeIds);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 检查名称唯一性
   */
  const checkNameUnique = async (name: string, excludeId?: number) => {
    const response = await pillRecipeApi.checkName(name, excludeId);
    return response.data;
  };

  return {
    currentRecipe,
    loading,
    pagination,
    fetchList,
    fetchDetail,
    createRecipe,
    updateRecipe,
    deleteRecipe,
    batchDeleteRecipe,
    checkNameUnique
  };
});
