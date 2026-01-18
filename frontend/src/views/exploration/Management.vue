<template>
  <div class="exploration-management">
    <el-page-header content="探索配置管理">
      <template #extra>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>系统管理</el-breadcrumb-item>
          <el-breadcrumb-item>探索配置</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
    </el-page-header>

    <el-tabs v-model="activeTab" class="tabs-container">
      <!-- 探索区域管理 -->
      <el-tab-pane label="探索区域" name="area">
        <el-container class="main-container">
          <!-- 左侧：区域列表 -->
          <el-aside width="400px">
            <AreaList
              v-model:selected-id="selectedAreaId"
              v-model:is-creating="isCreatingArea"
              @create="handleCreateArea"
            />
          </el-aside>

          <!-- 右侧：编辑表单 -->
          <el-main>
            <AreaForm
              v-if="selectedAreaId || isCreatingArea"
              :area-id="selectedAreaId"
              :is-creating="isCreatingArea"
              @save="handleSaveArea"
              @cancel="handleCancelArea"
            />
            <el-empty v-else description="请选择或创建探索区域" />
          </el-main>
        </el-container>
      </el-tab-pane>

      <!-- 探索事件管理 -->
      <el-tab-pane label="探索事件" name="event">
        <el-container class="main-container">
          <!-- 左侧：事件列表 -->
          <el-aside width="400px">
            <EventList
              v-model:selected-id="selectedEventId"
              v-model:is-creating="isCreatingEvent"
              @create="handleCreateEvent"
            />
          </el-aside>

          <!-- 右侧：编辑表单 -->
          <el-main>
            <EventForm
              v-if="selectedEventId || isCreatingEvent"
              :event-id="selectedEventId"
              :is-creating="isCreatingEvent"
              @save="handleSaveEvent"
              @cancel="handleCancelEvent"
            />
            <el-empty v-else description="请选择或创建探索事件" />
          </el-main>
        </el-container>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import AreaList from '@/components/AreaList.vue';
import AreaForm from '@/components/AreaForm.vue';
import EventList from '@/components/EventList.vue';
import EventForm from '@/components/EventForm.vue';

const activeTab = ref('area');

// 区域管理状态
const selectedAreaId = ref<number | null>(null);
const isCreatingArea = ref(false);

// 事件管理状态
const selectedEventId = ref<number | null>(null);
const isCreatingEvent = ref(false);

// 区域管理方法
const handleCreateArea = () => {
  selectedAreaId.value = null;
  isCreatingArea.value = true;
};

const handleSaveArea = () => {
  selectedAreaId.value = null;
  isCreatingArea.value = false;
};

const handleCancelArea = () => {
  selectedAreaId.value = null;
  isCreatingArea.value = false;
};

// 事件管理方法
const handleCreateEvent = () => {
  selectedEventId.value = null;
  isCreatingEvent.value = true;
};

const handleSaveEvent = () => {
  selectedEventId.value = null;
  isCreatingEvent.value = false;
};

const handleCancelEvent = () => {
  selectedEventId.value = null;
  isCreatingEvent.value = false;
};
</script>

<style scoped lang="scss">
.exploration-management {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;

  .el-page-header {
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
  }

  .tabs-container {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    :deep(.el-tabs__content) {
      padding: 20px;
    }

    :deep(.el-tabs__header) {
      margin: 0;
      padding: 0 20px;
    }
  }

  .main-container {
    height: calc(100vh - 300px);

    .el-aside {
      border-right: 1px solid #e4e7ed;
      padding: 0;
      overflow-y: auto;
    }

    .el-main {
      padding: 20px;
      overflow-y: auto;
    }
  }
}
</style>
