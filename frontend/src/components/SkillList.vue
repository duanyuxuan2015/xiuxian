<template>
  <div class="skill-list">
    <div class="list-header">
      <h3>技能列表</h3>
      <el-button type="primary" @click="handleCreate" :icon="Plus">新增技能</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索技能名称"
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
    </div>

    <div class="filter-bar">
      <el-select
        v-model="filterSectId"
        placeholder="所属宗门"
        clearable
        @change="handleFilter"
        style="width: 100%; margin-bottom: 8px;"
      >
        <el-option
          v-for="sect in sects"
          :key="sect.sectId"
          :label="sect.sectName"
          :value="sect.sectId"
        />
      </el-select>

      <el-select
        v-model="filterFunctionType"
        placeholder="功能类型"
        clearable
        @change="handleFilter"
        style="width: 100%;"
      >
        <el-option
          v-for="type in functionTypes"
          :key="type"
          :label="type"
          :value="type"
        />
      </el-select>
    </div>

    <div class="list-content" v-loading="skillStore.loading">
      <div
        v-for="item in skillStore.pagination.items || []"
        :key="item.skillId"
        class="list-item"
        :class="{ active: selectedId === item.skillId }"
        @click="handleSelect(item.skillId)"
      >
        <div class="item-name">{{ item.skillName }}</div>
        <div class="item-info">
          <el-tag size="small" type="primary">{{ item.functionType }}</el-tag>
          <span class="item-tier">Lv.{{ item.tier }}</span>
        </div>
        <div class="item-stats">
          <span v-if="item.elementType" class="element-type">{{ item.elementType }}</span>
          <span class="damage">伤害: {{ item.baseDamage }}</span>
        </div>
        <div v-if="item.sectName" class="sect-name">{{ item.sectName }}</div>
        <div class="item-cost">学习花费: {{ item.cost }}</div>
      </div>

      <el-empty v-if="!skillStore.loading && (!skillStore.pagination.items || skillStore.pagination.items.length === 0)" description="暂无技能" />
    </div>

    <div class="list-pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="skillStore.pagination.total"
        layout="prev, pager, next"
        small
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { Plus, Search } from '@element-plus/icons-vue';
import { useSkillStore } from '@/stores/skill';
import skillApi from '@/api/skill';
import type { SectOption } from '@/types/skill';

interface Props {
  selectedId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'update:selectedId', value: number | null): void;
  (e: 'update:isCreating', value: boolean): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const skillStore = useSkillStore();

const searchKeyword = ref('');
const filterSectId = ref<number | null>(null);
const filterFunctionType = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const sects = ref<SectOption[]>([]);
const functionTypes = ref<string[]>([]);

// 加载技能列表
const loadList = async () => {
  await skillStore.fetchList({
    page: currentPage.value,
    pageSize: pageSize.value,
    keyword: searchKeyword.value || undefined,
    sectId: filterSectId.value || undefined,
    functionType: filterFunctionType.value || undefined
  });
};

// 加载宗门列表
const loadSects = async () => {
  try {
    const response = await skillApi.getSects();
    sects.value = response.data;
  } catch (error) {
    console.error('加载宗门列表失败:', error);
  }
};

// 加载功能类型列表
const loadFunctionTypes = async () => {
  try {
    const response = await skillApi.getFunctionTypes();
    functionTypes.value = response.data;
  } catch (error) {
    console.error('加载功能类型失败:', error);
  }
};

onMounted(() => {
  loadSects();
  loadFunctionTypes();
  loadList();
});

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  loadList();
};

// 筛选
const handleFilter = () => {
  currentPage.value = 1;
  loadList();
};

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadList();
};

// 选择技能
const handleSelect = (skillId: number) => {
  emit('update:selectedId', skillId);
  emit('update:isCreating', false);
};

// 新增技能
const handleCreate = () => {
  emit('update:selectedId', null);
  emit('update:isCreating', true);
};

// 监听 isCreating 变化
watch(() => props.isCreating, (newVal) => {
  if (newVal) {
    emit('update:selectedId', null);
  }
});
</script>

<style scoped lang="scss">
.skill-list {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  border-right: 1px solid #dcdfe6;

  .list-header {
    padding: 16px;
    background: white;
    border-bottom: 1px solid #dcdfe6;
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
      font-size: 18px;
    }
  }

  .search-bar {
    padding: 12px 16px;
    background: white;
    border-bottom: 1px solid #dcdfe6;
  }

  .filter-bar {
    padding: 8px 16px;
    background: white;
    border-bottom: 1px solid #dcdfe6;
  }

  .list-content {
    flex: 1;
    overflow-y: auto;
    padding: 8px;

    .list-item {
      background: white;
      border-radius: 4px;
      padding: 12px;
      margin-bottom: 8px;
      cursor: pointer;
      transition: all 0.2s;
      border: 2px solid transparent;

      &:hover {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      &.active {
        border-color: var(--el-color-primary);
        background: #f0f7ff;
      }

      .item-name {
        font-weight: 500;
        margin-bottom: 8px;
        font-size: 15px;
      }

      .item-info {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 6px;

        .item-tier {
          font-size: 12px;
          color: #909399;
        }
      }

      .item-stats {
        display: flex;
        gap: 12px;
        font-size: 12px;
        color: #606266;
        margin-bottom: 6px;

        .element-type {
          color: #409eff;
        }

        .damage {
          color: #f56c6c;
        }
      }

      .sect-name {
        font-size: 12px;
        color: #67c23a;
        margin-bottom: 4px;
      }

      .item-cost {
        font-size: 12px;
        color: #e6a23c;
        font-weight: 500;
      }
    }
  }

  .list-pagination {
    padding: 12px;
    background: white;
    border-top: 1px solid #dcdfe6;
    text: center;
  }
}
</style>
