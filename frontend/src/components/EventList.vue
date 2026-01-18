<template>
  <div class="event-list">
    <div class="list-header">
      <h3>探索事件列表</h3>
      <el-button type="primary" :icon="Plus" @click="handleCreate">新增事件</el-button>
    </div>

    <div class="filter-bar">
      <el-select
        v-model="filterAreaId"
        placeholder="筛选区域"
        clearable
        @change="loadEvents"
        style="width: 200px; margin-right: 10px;"
      >
        <el-option
          v-for="area in areas"
          :key="area.areaId"
          :label="area.areaName"
          :value="area.areaId"
        />
      </el-select>

      <el-select
        v-model="filterLevel"
        placeholder="筛选级别"
        clearable
        @change="loadEvents"
        style="width: 150px;"
      >
        <el-option label="1级（常见）" :value="1" />
        <el-option label="2级（普通）" :value="2" />
        <el-option label="3级（稀有）" :value="3" />
        <el-option label="4级（史诗）" :value="4" />
      </el-select>
    </div>

    <el-table
      :data="events"
      v-loading="loading"
      stripe
      highlight-current-row
      @current-change="handleCurrentChange"
      height="calc(100vh - 420px)"
    >
      <el-table-column prop="eventId" label="ID" width="60" />
      <el-table-column prop="eventName" label="事件名称" width="120" />
      <el-table-column prop="eventType" label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="getEventTypeTag(row.eventType)">
            {{ getEventTypeName(row.eventType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="level" label="级别" width="80">
        <template #default="{ row }">
          <el-tag :type="getLevelTagType(row.level)">
            {{ row.level }}级
          </el-tag>
        </template>
      </el-table-column>
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
import { explorationEventApi, explorationAreaApi } from '@/api/exploration';
import type { ExplorationEvent, ExplorationArea } from '@/types/exploration';
import { EventTypes, EventLevels } from '@/types/exploration';

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

const events = ref<ExplorationEvent[]>([]);
const areas = ref<ExplorationArea[]>([]);
const loading = ref(false);
const filterAreaId = ref<number>();
const filterLevel = ref<number>();

const loadAreas = async () => {
  try {
    const response = await explorationAreaApi.getList();
    areas.value = response.data;
  } catch (error) {
    console.error('加载区域列表失败', error);
  }
};

const loadEvents = async () => {
  loading.value = true;
  try {
    let response;
    if (filterAreaId.value) {
      response = await explorationEventApi.getByAreaId(filterAreaId.value);
    } else {
      response = await explorationEventApi.getList();
    }

    let filteredEvents = response.data;

    if (filterLevel.value !== undefined) {
      filteredEvents = filteredEvents.filter(e => e.level === filterLevel.value);
    }

    events.value = filteredEvents;
  } catch (error) {
    ElMessage.error('加载探索事件列表失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const getEventTypeName = (type: string) => {
  const found = EventTypes.find(t => t.value === type);
  return found?.label || type;
};

const getEventTypeTag = (type: string) => {
  const tagMap: Record<string, any> = {
    'gather': 'success',
    'combat': 'danger',
    'fortune': 'warning',
    'trap': 'info',
    'none': ''
  };
  return tagMap[type] || '';
};

const getLevelTagType = (level: number) => {
  const tagMap: Record<number, any> = {
    1: 'info',
    2: '',
    3: 'warning',
    4: 'danger'
  };
  return tagMap[level] || '';
};

const handleCurrentChange = (row: ExplorationEvent | null) => {
  emit('update:selectedId', row?.eventId || null);
  emit('update:isCreating', false);
};

const handleCreate = () => {
  emit('update:selectedId', null);
  emit('update:isCreating', true);
  emit('create');
};

const handleDelete = async (row: ExplorationEvent) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除探索事件"${row.eventName}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    await explorationEventApi.delete(row.eventId);
    ElMessage.success('删除成功');
    await loadEvents();

    if (props.selectedId === row.eventId) {
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
  loadEvents();
});

defineExpose({
  loadEvents
});
</script>

<style scoped lang="scss">
.event-list {
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

  .filter-bar {
    padding: 10px 15px;
    background: #f5f7fa;
    border-bottom: 1px solid #e4e7ed;
    display: flex;
    align-items: center;
  }

  .el-table {
    flex: 1;
  }
}
</style>
