<template>
  <div class="monster-management">
    <el-page-header content="怪物配置管理">
      <template #extra>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>系统管理</el-breadcrumb-item>
          <el-breadcrumb-item>怪物配置</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
    </el-page-header>

    <el-container class="main-container">
      <!-- 左侧：怪物列表 -->
      <el-aside width="400px">
        <MonsterList
          v-model:selected-id="selectedMonsterId"
          v-model:is-creating="isCreating"
          @create="handleCreate"
        />
      </el-aside>

      <!-- 右侧：编辑表单 -->
      <el-main>
        <MonsterForm
          v-if="selectedMonsterId || isCreating"
          :monster-id="selectedMonsterId"
          :is-creating="isCreating"
          @save="handleSave"
          @cancel="handleCancel"
        />
        <el-empty v-else description="请选择或创建怪物" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import MonsterList from '@/components/MonsterList.vue';
import MonsterForm from '@/components/MonsterForm.vue';

const selectedMonsterId = ref<number | null>(null);
const isCreating = ref(false);

// 监听状态变化
watch([selectedMonsterId, isCreating], ([newId, newCreating]) => {
  console.log('Management: state changed', { selectedMonsterId: newId, isCreating: newCreating });
});

const handleCreate = () => {
  console.log('Management: handleCreate called');
  selectedMonsterId.value = null;
  isCreating.value = true;
};

const handleSave = () => {
  console.log('Management: handleSave called');
  isCreating.value = false;
  selectedMonsterId.value = null;
};

const handleCancel = () => {
  console.log('Management: handleCancel called');
  isCreating.value = false;
  selectedMonsterId.value = null;
};
</script>

<style scoped lang="scss">
.monster-management {
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
  }

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
  }
}
</style>
