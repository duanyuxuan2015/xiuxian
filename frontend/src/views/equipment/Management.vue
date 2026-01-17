<template>
  <div class="equipment-management">
    <el-container class="main-container">
      <!-- 左侧装备列表 -->
      <el-aside width="400px">
        <EquipmentList
          v-model:selected-id="selectedEquipmentId"
          v-model:is-creating="isCreating"
        />
      </el-aside>

      <!-- 右侧编辑表单 -->
      <el-main class="main-content">
        <div v-if="isCreating || selectedEquipmentId" class="form-wrapper">
          <h2>{{ isCreating ? '新增装备' : '编辑装备' }}</h2>
          <EquipmentForm
            :equipment-id="selectedEquipmentId"
            :is-creating="isCreating"
            @save="handleSave"
            @cancel="handleCancel"
          />
        </div>
        <el-empty v-else description="请选择装备或新增装备" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import EquipmentList from '@/components/EquipmentList.vue';
import EquipmentForm from '@/components/EquipmentForm.vue';

const selectedEquipmentId = ref<number | null>(null);
const isCreating = ref(false);

const handleSave = () => {
  ElMessage.success('保存成功');
  selectedEquipmentId.value = null;
  isCreating.value = false;
};

const handleCancel = () => {
  selectedEquipmentId.value = null;
  isCreating.value = false;
};
</script>

<style scoped lang="scss">
.equipment-management {
  height: 100vh;

  .main-container {
    height: 100%;

    .el-aside {
      border-right: 1px solid #dcdfe6;
    }

    .main-content {
      background: white;
      padding: 0;
      display: flex;
      flex-direction: column;

      .form-wrapper {
        height: 100%;
        display: flex;
        flex-direction: column;
        padding: 0;

        h2 {
          margin: 0;
          padding: 16px 24px;
          border-bottom: 1px solid #dcdfe6;
          font-size: 20px;
        }
      }
    }
  }
}
</style>
