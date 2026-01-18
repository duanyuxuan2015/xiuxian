<template>
  <div class="sect-task-management">
    <el-page-header content="宗门任务配置管理">
      <template #extra>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>系统管理</el-breadcrumb-item>
          <el-breadcrumb-item>宗门任务配置</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
    </el-page-header>

    <el-container class="main-container">
      <!-- 左侧任务模板列表 -->
      <el-aside width="400px">
        <SectTaskList
          v-model:selected-id="selectedTemplateId"
          v-model:is-creating="isCreating"
        />
      </el-aside>

      <!-- 右侧编辑表单 -->
      <el-main class="main-content">
        <div v-if="isCreating || selectedTemplateId" class="form-wrapper">
          <h2>{{ isCreating ? '新增任务模板' : '编辑任务模板' }}</h2>
          <SectTaskForm
            :template-id="selectedTemplateId"
            :is-creating="isCreating"
            @save="handleSave"
            @cancel="handleCancel"
          />
        </div>
        <el-empty v-else description="请选择任务或新增任务" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import SectTaskList from '@/components/SectTaskList.vue';
import SectTaskForm from '@/components/SectTaskForm.vue';

const selectedTemplateId = ref<number | null>(null);
const isCreating = ref(false);

const handleSave = () => {
  ElMessage.success('保存成功');
  selectedTemplateId.value = null;
  isCreating.value = false;
};

const handleCancel = () => {
  selectedTemplateId.value = null;
  isCreating.value = false;
};
</script>

<style scoped lang="scss">
.sect-task-management {
  height: 100%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;

  .main-container {
    flex: 1;
    margin-top: 20px;
    background: white;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    overflow: hidden;

    .el-aside {
      border-right: 1px solid #dcdfe6;
      padding: 16px;
      overflow: hidden;
      display: flex;
      flex-direction: column;
    }

    .el-main {
      padding: 20px;
      overflow-y: auto;

      .form-wrapper {
        height: 100%;
        display: flex;
        flex-direction: column;
        padding: 0;

        h2 {
          margin: 0 0 16px 0;
          font-size: 20px;
        }
      }
    }
  }
}
</style>
