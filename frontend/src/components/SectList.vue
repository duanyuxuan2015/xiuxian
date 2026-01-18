<template>
  <div class="sect-list">
    <div class="list-header">
      <h3>宗门列表</h3>
      <el-button type="primary" @click="handleCreate" :icon="Plus">新增宗门</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索宗门名称"
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
    </div>

    <div class="filter-bar">
      <el-select
        v-model="filterSectType"
        placeholder="宗门类型"
        clearable
        @change="handleFilter"
        style="width: 100%;"
      >
        <el-option
          v-for="type in sectTypes"
          :key="type"
          :label="type"
          :value="type"
        />
      </el-select>
    </div>

    <div class="list-content" v-loading="sectStore.loading">
      <div
        v-for="item in sectStore.pagination.items || []"
        :key="item.sectId"
        class="list-item"
        :class="{ active: selectedId === item.sectId }"
        @click="handleSelect(item.sectId)"
      >
        <div class="item-name">{{ item.sectName }}</div>
        <div class="item-info">
          <el-tag size="small" type="primary">{{ item.sectType }}</el-tag>
          <span class="item-level">要求等级: Lv.{{ item.requiredRealmLevel }}</span>
        </div>
        <div class="item-specialty" v-if="item.specialty">
          <span>专长: {{ item.specialty }}</span>
        </div>
      </div>

      <el-empty v-if="!sectStore.loading && (!sectStore.pagination.items || sectStore.pagination.items.length === 0)" description="暂无宗门" />
    </div>

    <div class="list-pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="sectStore.pagination.total"
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
import { useSectStore } from '@/stores/sect';
import sectApi from '@/api/sect';

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

const sectStore = useSectStore();

const searchKeyword = ref('');
const filterSectType = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const sectTypes = ref<string[]>([]);

// 加载宗门列表
const loadList = async () => {
  await sectStore.fetchList({
    page: currentPage.value,
    pageSize: pageSize.value,
    keyword: searchKeyword.value || undefined,
    sectType: filterSectType.value || undefined
  });
};

// 加载宗门类型列表
const loadSectTypes = async () => {
  try {
    const response = await sectApi.getSectTypes();
    sectTypes.value = response.data;
  } catch (error) {
    console.error('加载宗门类型失败:', error);
  }
};

onMounted(() => {
  loadSectTypes();
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

// 选择宗门
const handleSelect = (sectId: number) => {
  emit('update:selectedId', sectId);
  emit('update:isCreating', false);
};

// 新增宗门
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
.sect-list {
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

        .item-level {
          font-size: 12px;
          color: #909399;
        }
      }

      .item-specialty {
        font-size: 12px;
        color: #606266;
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
