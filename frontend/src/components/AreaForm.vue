<template>
  <div class="area-form">
    <div class="form-header">
      <h3>{{ isCreating ? '新增探索区域' : '编辑探索区域' }}</h3>
    </div>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      v-loading="loading"
    >
      <el-form-item label="区域名称" prop="areaName">
        <el-input v-model="formData.areaName" placeholder="请输入区域名称" />
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入区域描述"
        />
      </el-form-item>

      <el-form-item label="所需境界等级" prop="requiredRealmLevel">
        <el-input-number
          v-model="formData.requiredRealmLevel"
          :min="1"
          :max="100"
          placeholder="境界等级"
        />
        <span class="form-tip">玩家需要达到此境界才能探索该区域</span>
      </el-form-item>

      <el-form-item label="危险等级" prop="dangerLevel">
        <el-input-number
          v-model="formData.dangerLevel"
          :min="1"
          :max="10"
          placeholder="危险等级"
        />
        <span class="form-tip">1-10，等级越高越危险</span>
      </el-form-item>

      <el-form-item label="灵力消耗" prop="spiritCost">
        <el-input-number
          v-model="formData.spiritCost"
          :min="0"
          placeholder="灵力消耗"
        />
        <span class="form-tip">每次探索消耗的灵力值</span>
      </el-form-item>

      <el-form-item label="体力消耗" prop="staminaCost">
        <el-input-number
          v-model="formData.staminaCost"
          :min="0"
          placeholder="体力消耗"
        />
        <span class="form-tip">每次探索消耗的体力值</span>
      </el-form-item>

      <el-form-item label="基础探索时间" prop="baseExploreTime">
        <el-input-number
          v-model="formData.baseExploreTime"
          :min="1"
          placeholder="探索时间"
        />
        <span class="form-tip">单位：秒</span>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { explorationAreaApi } from '@/api/exploration';
import type { ExplorationAreaFormData } from '@/types/exploration';

interface Props {
  areaId?: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'save'): void;
  (e: 'cancel'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const formRef = ref<FormInstance>();
const loading = ref(false);

const formData = ref<ExplorationAreaFormData>({
  areaId: undefined,
  areaName: '',
  description: '',
  requiredRealmLevel: 1,
  dangerLevel: 1,
  spiritCost: 10,
  staminaCost: 20,
  baseExploreTime: 30
});

const rules: FormRules = {
  areaName: [
    { required: true, message: '请输入区域名称', trigger: 'blur' }
  ],
  requiredRealmLevel: [
    { required: true, message: '请输入所需境界等级', trigger: 'blur' }
  ],
  dangerLevel: [
    { required: true, message: '请输入危险等级', trigger: 'blur' }
  ],
  spiritCost: [
    { required: true, message: '请输入灵力消耗', trigger: 'blur' }
  ],
  staminaCost: [
    { required: true, message: '请输入体力消耗', trigger: 'blur' }
  ],
  baseExploreTime: [
    { required: true, message: '请输入基础探索时间', trigger: 'blur' }
  ]
};

const loadAreaDetail = async () => {
  if (!props.areaId || props.isCreating) return;

  loading.value = true;
  try {
    const response = await explorationAreaApi.getDetail(props.areaId);
    formData.value = response.data;
  } catch (error) {
    ElMessage.error('加载区域详情失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    loading.value = true;
    try {
      if (props.isCreating) {
        await explorationAreaApi.create(formData.value);
        ElMessage.success('创建成功');
      } else {
        await explorationAreaApi.update(formData.value);
        ElMessage.success('更新成功');
      }
      emit('save');
    } catch (error) {
      ElMessage.error(props.isCreating ? '创建失败' : '更新失败');
      console.error(error);
    } finally {
      loading.value = false;
    }
  });
};

const handleCancel = () => {
  emit('cancel');
};

watch(() => props.areaId, () => {
  loadAreaDetail();
}, { immediate: true });
</script>

<style scoped lang="scss">
.area-form {
  padding: 20px;

  .form-header {
    margin-bottom: 20px;

    h3 {
      margin: 0;
      font-size: 18px;
      color: #333;
    }
  }

  .form-tip {
    margin-left: 10px;
    font-size: 12px;
    color: #999;
  }

  .el-form-item {
    margin-bottom: 20px;
  }
}
</style>
