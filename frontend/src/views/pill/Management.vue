<template>
  <div class="pill-management">
    <el-page-header content="丹药配置管理">
      <template #extra>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>系统管理</el-breadcrumb-item>
          <el-breadcrumb-item>丹药配置</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
    </el-page-header>

    <el-container class="main-container">
      <!-- 左侧丹药列表 -->
      <el-aside width="400px">
        <PillList
          v-model:selected-id="selectedPillId"
          v-model:is-creating="isCreating"
        />
      </el-aside>

      <!-- 右侧编辑表单 -->
      <el-main class="main-content">
        <div v-if="isCreating || selectedPillId" class="form-wrapper">
          <h2>{{ isCreating ? '新增丹药' : '编辑丹药' }}</h2>
          <PillForm
            :pill-id="selectedPillId"
            :is-creating="isCreating"
            @save="handleSave"
            @cancel="handleCancel"
          />
        </div>
        <el-empty v-else description="请选择丹药或新增丹药" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import PillList from '@/components/PillList.vue';
import PillForm from '@/components/PillForm.vue';

const selectedPillId = ref<number | null>(null);
const isCreating = ref(false);

const handleSave = () => {
  ElMessage.success('保存成功');
  selectedPillId.value = null;
  isCreating.value = false;
};

const handleCancel = () => {
  selectedPillId.value = null;
  isCreating.value = false;
};
</script>

<style scoped lang="scss">
.pill-management {
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
