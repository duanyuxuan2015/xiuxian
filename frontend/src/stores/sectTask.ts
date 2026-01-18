import { defineStore } from 'pinia';
import { ref } from 'vue';
import sectTaskApi from '@/api/sectTask';
import type { SectTaskTemplate, SectTaskTemplateListItem } from '@/types/sectTask';

export const useSectTaskStore = defineStore('sectTask', () => {
  const currentTemplate = ref<SectTaskTemplate | null>(null);
  const loading = ref(false);
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
    items: [] as SectTaskTemplateListItem[]
  });

  /**
   * 分页查询任务模板列表
   */
  const fetchList = async (params?: {
    page?: number;
    pageSize?: number;
    keyword?: string;
    sectId?: number;
    taskType?: string;
  }) => {
    loading.value = true;
    try {
      const response = await sectTaskApi.list({
        page: params?.page || pagination.value.page,
        pageSize: params?.pageSize || pagination.value.pageSize,
        keyword: params?.keyword,
        sectId: params?.sectId,
        taskType: params?.taskType
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
   * 获取任务模板详情
   */
  const fetchDetail = async (templateId: number) => {
    loading.value = true;
    try {
      const response = await sectTaskApi.getDetail(templateId);
      currentTemplate.value = response.data;
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 创建任务模板
   */
  const createTemplate = async (data: SectTaskTemplate) => {
    loading.value = true;
    try {
      const response = await sectTaskApi.create(data);
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 更新任务模板
   */
  const updateTemplate = async (templateId: number, data: SectTaskTemplate) => {
    loading.value = true;
    try {
      await sectTaskApi.update(templateId, data);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 删除任务模板
   */
  const deleteTemplate = async (templateId: number) => {
    loading.value = true;
    try {
      await sectTaskApi.delete(templateId);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 批量删除任务模板
   */
  const batchDeleteTemplate = async (templateIds: number[]) => {
    loading.value = true;
    try {
      await sectTaskApi.batchDelete(templateIds);
    } finally {
      loading.value = false;
    }
  };

  return {
    currentTemplate,
    loading,
    pagination,
    fetchList,
    fetchDetail,
    createTemplate,
    updateTemplate,
    deleteTemplate,
    batchDeleteTemplate
  };
});
