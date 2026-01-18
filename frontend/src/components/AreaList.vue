<template>
  <div class="area-list">
    <div class="list-header">
      <h3>探索区域列表</h3>
      <el-button type="primary" :icon="Plus" @click="handleCreate">新增区域</el-button>
    </div>

    <el-table
      :data="areas"
      v-loading="loading"
      stripe
      highlight-current-row
      @current-change="handleCurrentChange"
      height="calc(100vh - 350px)"
    >
      <el-table-column prop="areaId" label="ID" width="60" />
      <el-table-column prop="areaName" label="区域名称" width="120" />
      <el-table-column prop="requiredRealmLevel" label="所需境界" width="80" />
      <el-table-column prop="dangerLevel" label="危险等级" width="80" />
      <el-table-column prop="staminaCost" label="体力消耗" width="80" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button
            type="danger"
            size="small"
            :icon="Delete"
            @click.stop="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Delete } from '@element-plus/icons-vue';
import { explorationAreaApi } from '@/api/exploration';
import type { ExplorationArea } from '@/types/exploration';

interface Props {
  selectedId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'update:selectedId', value: number | null): void;
  (e: 'update:isCreating', value: boolean): void;
  (e: 'create'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const areas = ref<ExplorationArea[]>([]);
const loading = ref(false);

const loadAreas = async () => {
  loading.value = true;
  try {
    const response = await explorationAreaApi.getList();
    areas.value = response.data;
  } catch (error) {
    ElMessage.error('加载探索区域列表失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleCurrentChange = (row: ExplorationArea | null) => {
  emit('update:selectedId', row?.areaId || null);
  emit('update:isCreating', false);
};

const handleCreate = () => {
  emit('update:selectedId', null);
  emit('update:isCreating', true);
  emit('create');
};

const handleDelete = async (row: ExplorationArea) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除探索区域"${row.areaName}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    await explorationAreaApi.delete(row.areaId);
    ElMessage.success('删除成功');
    await loadAreas();

    if (props.selectedId === row.areaId) {
      emit('update:selectedId', null);
      emit('update:isCreating', false);
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
      console.error(error);
    }
  }
};

onMounted(() => {
  loadAreas();
});

defineExpose({
  loadAreas
});
</script>

<style scoped lang="scss">
.area-list {
  height: 100%;
  display: flex;
  flex-direction: column;

  .list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
    border-bottom: 1px solid #e4e7ed;

    h3 {
      margin: 0;
      font-size: 16px;
      color: #333;
    }
  }

  .el-table {
    flex: 1;
  }
}
</style>
