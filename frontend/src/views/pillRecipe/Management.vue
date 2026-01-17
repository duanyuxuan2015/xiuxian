<template>
  <div class="pill-recipe-management">
    <el-page-header content="丹方配置管理">
      <template #extra>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>系统管理</el-breadcrumb-item>
          <el-breadcrumb-item>丹方配置</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
    </el-page-header>

    <el-container class="main-container">
      <!-- 左侧丹方列表 -->
      <el-aside width="400px">
        <PillRecipeList
          v-model:selected-id="selectedRecipeId"
          v-model:is-creating="isCreating"
        />
      </el-aside>

      <!-- 右侧编辑表单 -->
      <el-main class="main-content">
        <div v-if="isCreating || selectedRecipeId" class="form-wrapper">
          <h2>{{ isCreating ? '新增丹方' : '编辑丹方' }}</h2>
          <PillRecipeForm
            :recipe-id="selectedRecipeId"
            :is-creating="isCreating"
            @save="handleSave"
            @cancel="handleCancel"
          />
        </div>
        <el-empty v-else description="请选择丹方或新增丹方" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import PillRecipeList from '@/components/PillRecipeList.vue';
import PillRecipeForm from '@/components/PillRecipeForm.vue';

const selectedRecipeId = ref<number | null>(null);
const isCreating = ref(false);

const handleSave = () => {
  ElMessage.success('保存成功');
  selectedRecipeId.value = null;
  isCreating.value = false;
};

const handleCancel = () => {
  selectedRecipeId.value = null;
  isCreating.value = false;
};
</script>

<style scoped lang="scss">
.pill-recipe-management {
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
