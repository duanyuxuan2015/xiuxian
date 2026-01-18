<template>
  <div class="skill-management">
    <el-page-header content="技能配置管理">
      <template #extra>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>系统管理</el-breadcrumb-item>
          <el-breadcrumb-item>技能配置</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
    </el-page-header>

    <el-container class="main-container">
      <!-- 左侧技能列表 -->
      <el-aside width="400px">
        <SkillList
          v-model:selected-id="selectedSkillId"
          v-model:is-creating="isCreating"
        />
      </el-aside>

      <!-- 右侧编辑表单 -->
      <el-main class="main-content">
        <div v-if="isCreating || selectedSkillId" class="form-wrapper">
          <h2>{{ isCreating ? '新增技能' : '编辑技能' }}</h2>
          <SkillForm
            :skill-id="selectedSkillId"
            :is-creating="isCreating"
            @save="handleSave"
            @cancel="handleCancel"
          />
        </div>
        <el-empty v-else description="请选择技能或新增技能" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import SkillList from '@/components/SkillList.vue';
import SkillForm from '@/components/SkillForm.vue';

const selectedSkillId = ref<number | null>(null);
const isCreating = ref(false);

const handleSave = () => {
  ElMessage.success('保存成功');
  selectedSkillId.value = null;
  isCreating.value = false;
};

const handleCancel = () => {
  selectedSkillId.value = null;
  isCreating.value = false;
};
</script>

<style scoped lang="scss">
.skill-management {
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
