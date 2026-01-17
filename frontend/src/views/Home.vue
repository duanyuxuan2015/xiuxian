<template>
  <div class="home-container">
    <div class="header">
      <h1>修仙游戏管理后台</h1>
      <p class="subtitle">系统配置管理中心</p>
    </div>

    <div class="cards-container">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="config-card" shadow="hover" @click="navigateTo('/monster')">
            <div class="card-icon monster-icon">
              <el-icon :size="60">
                <Star />
              </el-icon>
            </div>
            <div class="card-content">
              <h2>怪物配置</h2>
              <p>管理游戏中的怪物信息、属性和掉落配置</p>
              <div class="card-stats">
                <span class="stat-item">
                  <el-icon><List /></el-icon>
                  怪物列表管理
                </span>
              </div>
            </div>
            <div class="card-action">
              <el-button type="primary" :icon="ArrowRight">进入配置</el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="config-card" shadow="hover" @click="navigateTo('/equipment')">
            <div class="card-icon equipment-icon">
              <el-icon :size="60">
                <PriceTag />
              </el-icon>
            </div>
            <div class="card-content">
              <h2>装备配置</h2>
              <p>管理游戏中的装备信息、属性和品质配置</p>
              <div class="card-stats">
                <span class="stat-item">
                  <el-icon><List /></el-icon>
                  装备列表管理
                </span>
              </div>
            </div>
            <div class="card-action">
              <el-button type="success" :icon="ArrowRight">进入配置</el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="config-card" shadow="hover" @click="navigateTo('/pill')">
            <div class="card-icon pill-icon">
              <el-icon :size="60">
                <Box />
              </el-icon>
            </div>
            <div class="card-content">
              <h2>丹药配置</h2>
              <p>管理游戏中的丹药信息、效果和品质配置</p>
              <div class="card-stats">
                <span class="stat-item">
                  <el-icon><List /></el-icon>
                  丹药列表管理
                </span>
              </div>
            </div>
            <div class="card-action">
              <el-button type="warning" :icon="ArrowRight">进入配置</el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="config-card" shadow="hover" @click="navigateTo('/material')">
            <div class="card-icon material-icon">
              <el-icon :size="60">
                <Box />
              </el-icon>
            </div>
            <div class="card-content">
              <h2>材料配置</h2>
              <p>管理游戏中的材料信息、类型和品质配置</p>
              <div class="card-stats">
                <span class="stat-item">
                  <el-icon><List /></el-icon>
                  材料列表管理
                </span>
              </div>
            </div>
            <div class="card-action">
              <el-button type="danger" :icon="ArrowRight">进入配置</el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="config-card" shadow="hover" @click="navigateTo('/pill-recipe')">
            <div class="card-icon recipe-icon">
              <el-icon :size="60">
                <Star />
              </el-icon>
            </div>
            <div class="card-content">
              <h2>丹方配置</h2>
              <p>管理游戏中的丹方、材料配置和炼制规则</p>
              <div class="card-stats">
                <span class="stat-item">
                  <el-icon><List /></el-icon>
                  丹方列表管理
                </span>
              </div>
            </div>
            <div class="card-action">
              <el-button type="info" :icon="ArrowRight">进入配置</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="quick-actions">
      <h3>快捷操作</h3>
      <div class="action-buttons">
        <el-button type="primary" :icon="Plus" @click="navigateTo('/monster')">新增怪物</el-button>
        <el-button type="success" :icon="Plus" @click="navigateTo('/equipment')">新增装备</el-button>
        <el-button type="warning" :icon="Plus" @click="navigateTo('/pill')">新增丹药</el-button>
        <el-button type="danger" :icon="Plus" @click="navigateTo('/material')">新材料</el-button>
        <el-button type="info" :icon="Plus" @click="navigateTo('/pill-recipe')">新丹方</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { Star, PriceTag, Box, List, ArrowRight, Plus } from '@element-plus/icons-vue';

const router = useRouter();

const navigateTo = (path: string) => {
  router.push(path);
};
</script>

<style scoped lang="scss">
.home-container {
  min-height: 100vh;
  padding: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .header {
    text-align: center;
    color: white;
    margin-bottom: 50px;

    h1 {
      font-size: 48px;
      margin: 0 0 10px 0;
      font-weight: bold;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
    }

    .subtitle {
      font-size: 20px;
      margin: 0;
      opacity: 0.9;
    }
  }

  .cards-container {
    margin-bottom: 40px;

    .config-card {
      cursor: pointer;
      transition: all 0.3s ease;
      border-radius: 12px;
      overflow: hidden;

      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
      }

      .card-icon {
        text-align: center;
        padding: 30px 0 20px;

        .el-icon {
          color: white;
        }

        &.monster-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.equipment-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.pill-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }

        &.material-icon {
          background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        }

        &.recipe-icon {
          background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
        }
      }

      .card-content {
        padding: 0 30px 20px;
        text-align: center;

        h2 {
          font-size: 24px;
          margin: 0 0 10px 0;
          color: #333;
        }

        p {
          font-size: 14px;
          color: #666;
          margin: 0 0 15px 0;
          line-height: 1.6;
        }

        .card-stats {
          display: flex;
          justify-content: center;
          gap: 20px;

          .stat-item {
            display: flex;
            align-items: center;
            gap: 5px;
            font-size: 13px;
            color: #999;
          }
        }
      }

      .card-action {
        padding: 0 30px 30px;
        text-align: center;

        .el-button {
          width: 100%;
        }
      }
    }
  }

  .quick-actions {
    background: white;
    border-radius: 12px;
    padding: 30px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

    h3 {
      margin: 0 0 20px 0;
      font-size: 20px;
      color: #333;
      text-align: center;
    }

    .action-buttons {
      display: flex;
      justify-content: center;
      gap: 15px;
      flex-wrap: wrap;

      .el-button {
        min-width: 140px;
      }
    }
  }
}

@media (max-width: 768px) {
  .home-container {
    padding: 20px;

    .header {
      h1 {
        font-size: 32px;
      }

      .subtitle {
        font-size: 16px;
      }
    }

    .cards-container {
      .el-col {
        margin-bottom: 20px;
      }
    }

    .quick-actions {
      .action-buttons {
        flex-direction: column;

        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>
