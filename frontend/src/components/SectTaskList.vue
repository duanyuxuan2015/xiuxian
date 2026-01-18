<template>
  <div class="sect-task-list">
    <div class="list-header">
      <h3>任务模板列表</h3>
      <el-button type="primary" @click="handleCreate" :icon="Plus">新增任务</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索任务名称"
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
        v-model="filterTaskType"
        placeholder="任务类型"
        clearable
        @change="handleFilter"
        style="width: 100%;"
      >
        <el-option
          v-for="type in taskTypes"
          :key="type"
          :label="type"
          :value="type"
        />
      </el-select>
    </div>

    <div class="list-content" v-loading="sectTaskStore.loading">
      <div
        v-for="item in sectTaskStore.pagination.items || []"
        :key="item.templateId"
        class="list-item"
        :class="{ active: selectedId === item.templateId }"
        @click="handleSelect(item.templateId)"
      >
        <div class="item-name">{{ item.taskName }}</div>
        <div class="item-info">
          <el-tag size="small" type="primary">{{ item.taskType }}</el-tag>
          <el-tag size="small" :type="item.isActive ? 'success' : 'info'">
            {{ item.isActive ? '启用' : '禁用' }}
          </el-tag>
        </div>
        <div class="item-target">
          <span class="target-type">{{ item.targetType }}</span>
          <span class="target-count">x{{ item.targetCount }}</span>
        </div>
        <div class="item-stats">
          <span class="sect-name">{{ item.sectName }}</span>
          <span class="position">职位要求: Lv.{{ item.requiredPosition }}</span>
        </div>
        <div class="item-rewards">
          <span class="reward">贡献: {{ item.contributionReward }}</span>
          <span class="reward">声望: {{ item.reputationReward }}</span>
        </div>
      </div>

      <el-empty v-if="!sectTaskStore.loading && (!sectTaskStore.pagination.items || sectTaskStore.pagination.items.length === 0)" description="暂无任务" />
    </div>

    <div class="list-pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="sectTaskStore.pagination.total"
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
import { useSectTaskStore } from '@/stores/sectTask';
import sectTaskApi from '@/api/sectTask';
import type { SectOption } from '@/types/sectTask';

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

const sectTaskStore = useSectTaskStore();

const searchKeyword = ref('');
const filterSectId = ref<number | null>(null);
const filterTaskType = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const sects = ref<SectOption[]>([]);
const taskTypes = ref<string[]>([]);

// 加载任务模板列表
const loadList = async () => {
  await sectTaskStore.fetchList({
    page: currentPage.value,
    pageSize: pageSize.value,
    keyword: searchKeyword.value || undefined,
    sectId: filterSectId.value || undefined,
    taskType: filterTaskType.value || undefined
  });
};

// 加载宗门列表
const loadSects = async () => {
  try {
    const response = await sectTaskApi.getSects();
    sects.value = response.data;
  } catch (error) {
    console.error('加载宗门列表失败:', error);
  }
};

// 加载任务类型列表
const loadTaskTypes = async () => {
  try {
    const response = await sectTaskApi.getTaskTypes();
    taskTypes.value = response.data;
  } catch (error) {
    console.error('加载任务类型失败:', error);
  }
};

onMounted(() => {
  loadSects();
  loadTaskTypes();
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

// 选择任务
const handleSelect = (templateId: number) => {
  emit('update:selectedId', templateId);
  emit('update:isCreating', false);
};

// 新增任务
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
.sect-task-list {
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
      }

      .item-target {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 6px;
        font-size: 13px;

        .target-type {
          color: #606266;
        }

        .target-count {
          color: #409eff;
          font-weight: 500;
        }
      }

      .item-stats {
        display: flex;
        gap: 12px;
        font-size: 12px;
        color: #606266;
        margin-bottom: 6px;

        .sect-name {
          color: #409eff;
        }

        .position {
          color: #909399;
        }
      }

      .item-rewards {
        display: flex;
        gap: 12px;
        font-size: 12px;

        .reward {
          color: #f56c6c;
          font-weight: 500;
        }
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
