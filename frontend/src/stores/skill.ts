import { defineStore } from 'pinia';
import { ref } from 'vue';
import skillApi from '@/api/skill';
import type { Skill, SkillListItem } from '@/types/skill';

export const useSkillStore = defineStore('skill', () => {
  const currentSkill = ref<Skill | null>(null);
  const loading = ref(false);
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
    items: [] as SkillListItem[]
  });

  /**
   * 分页查询技能列表
   */
  const fetchList = async (params?: {
    page?: number;
    pageSize?: number;
    keyword?: string;
    sectId?: number;
    functionType?: string;
  }) => {
    loading.value = true;
    try {
      const response = await skillApi.list({
        page: params?.page || pagination.value.page,
        pageSize: params?.pageSize || pagination.value.pageSize,
        keyword: params?.keyword,
        sectId: params?.sectId,
        functionType: params?.functionType
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
   * 获取技能详情
   */
  const fetchDetail = async (skillId: number) => {
    loading.value = true;
    try {
      const response = await skillApi.getDetail(skillId);
      currentSkill.value = response.data;
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 创建技能
   */
  const createSkill = async (data: Skill) => {
    loading.value = true;
    try {
      const response = await skillApi.create(data);
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 更新技能
   */
  const updateSkill = async (skillId: number, data: Skill) => {
    loading.value = true;
    try {
      await skillApi.update(skillId, data);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 删除技能
   */
  const deleteSkill = async (skillId: number) => {
    loading.value = true;
    try {
      await skillApi.delete(skillId);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 批量删除技能
   */
  const batchDeleteSkill = async (skillIds: number[]) => {
    loading.value = true;
    try {
      await skillApi.batchDelete(skillIds);
    } finally {
      loading.value = false;
    }
  };

  return {
    currentSkill,
    loading,
    pagination,
    fetchList,
    fetchDetail,
    createSkill,
    updateSkill,
    deleteSkill,
    batchDeleteSkill
  };
});
