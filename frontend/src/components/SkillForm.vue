<template>
  <div class="skill-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-tabs v-model="activeTab">
        <!-- Tab 1: 基础信息 -->
        <el-tab-pane label="基础信息" name="basic">
          <el-form-item label="技能名称" prop="skillName">
            <el-input
              v-model="formData.skillName"
              placeholder="请输入技能名称"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="功能类型" prop="functionType">
            <el-select
              v-model="formData.functionType"
              placeholder="请选择功能类型"
              style="width: 100%;"
              filterable
              allow-create
            >
              <el-option
                v-for="type in functionTypes"
                :key="type"
                :label="type"
                :value="type"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="元素类型">
            <el-select
              v-model="formData.elementType"
              placeholder="请选择元素类型"
              style="width: 100%;"
              filterable
              allow-create
              clearable
            >
              <el-option
                v-for="type in elementTypes"
                :key="type"
                :label="type"
                :value="type"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="品阶" prop="tier">
            <el-input-number
              v-model="formData.tier"
              :min="1"
              :max="999"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="所属宗门">
            <el-select
              v-model="formData.sectId"
              placeholder="请选择宗门"
              style="width: 100%;"
              clearable
              filterable
            >
              <el-option
                v-for="sect in sects"
                :key="sect.sectId"
                :label="sect.sectName"
                :value="sect.sectId"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="描述">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请输入技能描述"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 2: 战斗属性 -->
        <el-tab-pane label="战斗属性" name="combat">
          <el-form-item label="基础伤害" prop="baseDamage">
            <el-input-number
              v-model="formData.baseDamage"
              :min="0"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="技能倍率" prop="skillMultiplier">
            <el-input-number
              v-model="formData.skillMultiplier"
              :min="0"
              :step="0.1"
              :precision="2"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">技能伤害倍率</span>
          </el-form-item>

          <el-form-item label="灵力消耗" prop="spiritualCost">
            <el-input-number
              v-model="formData.spiritualCost"
              :min="0"
              style="width: 100%;"
            />
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 3: 成长属性 -->
        <el-tab-pane label="成长属性" name="growth">
          <el-form-item label="伤害成长率">
            <el-input-number
              v-model="formData.damageGrowthRate"
              :min="0"
              :step="0.01"
              :precision="2"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">每级伤害成长率</span>
          </el-form-item>

          <el-form-item label="倍率成长">
            <el-input-number
              v-model="formData.multiplierGrowth"
              :min="0"
              :step="0.01"
              :precision="2"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">每级倍率成长</span>
          </el-form-item>

          <el-form-item label="灵力消耗成长">
            <el-input-number
              v-model="formData.spiritualCostGrowth"
              :min="0"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">每级灵力消耗成长</span>
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 4: 其他信息 -->
        <el-tab-pane label="其他信息" name="other">
          <el-form-item label="解锁方式">
            <el-input
              v-model="formData.unlockMethod"
              placeholder="请输入解锁方式"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="学习花费" prop="cost">
            <el-input-number
              v-model="formData.cost"
              :min="0"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">学习该技能需要的花费</span>
          </el-form-item>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <!-- 操作按钮 -->
    <div class="form-actions">
      <el-button @click="$emit('cancel')">取消</el-button>
      <el-button
        type="primary"
        :loading="saving"
        @click="handleSave"
      >
        保存
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useSkillStore } from '@/stores/skill';
import skillApi from '@/api/skill';
import type { Skill, SectOption } from '@/types/skill';

interface Props {
  skillId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'save'): void;
  (e: 'cancel'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const skillStore = useSkillStore();

const formRef = ref();
const activeTab = ref('basic');
const saving = ref(false);
const sects = ref<SectOption[]>([]);
const functionTypes = ref<string[]>([]);
const elementTypes = ref<string[]>([]);

// 表单数据默认值
const getDefaultFormData = (): Skill => ({
  skillName: '',
  functionType: '',
  elementType: '',
  baseDamage: 100,
  skillMultiplier: 1.0,
  spiritualCost: 10,
  damageGrowthRate: 0,
  multiplierGrowth: 0,
  spiritualCostGrowth: 0,
  description: '',
  tier: 1,
  sectId: null,
  unlockMethod: '',
  cost: 100
});

const formData = ref<Skill>(getDefaultFormData());

// 表单验证规则
const formRules = computed(() => ({
  skillName: [
    { required: true, message: '请输入技能名称', trigger: 'blur' }
  ],
  functionType: [
    { required: true, message: '请选择功能类型', trigger: 'change' }
  ],
  baseDamage: [
    { required: true, message: '请输入基础伤害', trigger: 'blur' }
  ],
  skillMultiplier: [
    { required: true, message: '请输入技能倍率', trigger: 'blur' }
  ],
  spiritualCost: [
    { required: true, message: '请输入灵力消耗', trigger: 'blur' }
  ],
  tier: [
    { required: true, message: '请输入品阶', trigger: 'blur' }
  ],
  cost: [
    { required: true, message: '请输入学习花费', trigger: 'blur' }
  ]
}));

const handleSave = async () => {
  try {
    const valid = await formRef.value?.validate();
    if (!valid) return;

    saving.value = true;

    if (props.isCreating) {
      await skillStore.createSkill(formData.value);
      ElMessage.success('创建成功');
    } else {
      await skillStore.updateSkill(props.skillId!, formData.value);
      ElMessage.success('更新成功');
    }

    emit('save');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  } finally {
    saving.value = false;
  }
};

// 加载宗门列表
const loadSects = async () => {
  try {
    const response = await skillApi.getSects();
    sects.value = response.data;
  } catch (error) {
    console.error('加载宗门列表失败:', error);
  }
};

// 加载功能类型列表
const loadFunctionTypes = async () => {
  try {
    const response = await skillApi.getFunctionTypes();
    functionTypes.value = response.data;
  } catch (error) {
    console.error('加载功能类型失败:', error);
  }
};

// 加载元素类型列表
const loadElementTypes = async () => {
  try {
    const response = await skillApi.getElementTypes();
    elementTypes.value = response.data;
  } catch (error) {
    console.error('加载元素类型失败:', error);
  }
};

onMounted(() => {
  loadSects();
  loadFunctionTypes();
  loadElementTypes();
});

// 加载技能详情到表单
const loadSkillDetail = async (skillId: number) => {
  const detail = await skillStore.fetchDetail(skillId);
  formData.value = {
    skillName: detail.skillName,
    functionType: detail.functionType,
    elementType: detail.elementType || '',
    baseDamage: detail.baseDamage,
    skillMultiplier: detail.skillMultiplier,
    spiritualCost: detail.spiritualCost,
    damageGrowthRate: detail.damageGrowthRate || 0,
    multiplierGrowth: detail.multiplierGrowth || 0,
    spiritualCostGrowth: detail.spiritualCostGrowth || 0,
    description: detail.description || '',
    tier: detail.tier,
    sectId: detail.sectId || null,
    unlockMethod: detail.unlockMethod || '',
    cost: detail.cost
  };
  // 重置 tab 到第一个
  activeTab.value = 'basic';
};

// 监听 skillId 变化，重新加载数据
watch(() => props.skillId, async (newSkillId) => {
  if (newSkillId) {
    await loadSkillDetail(newSkillId);
  }
});

// 监听 isCreating 变化，重置表单数据
watch(() => props.isCreating, (newVal) => {
  if (newVal) {
    // 重置为默认表单数据
    formData.value = getDefaultFormData();
    activeTab.value = 'basic';
    // 清除表单验证错误
    formRef.value?.clearValidate();
  }
});
</script>

<style scoped lang="scss">
.skill-form {
  height: 100%;
  display: flex;
  flex-direction: column;

  .el-tabs {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;

    :deep(.el-tabs__content) {
      flex: 1;
      overflow-y: auto;
    }
  }

  .form-actions {
    padding: 16px;
    border-top: 1px solid #dcdfe6;
    text-align: right;
    background: white;

    .el-button {
      margin-left: 8px;
    }
  }
}
</style>
