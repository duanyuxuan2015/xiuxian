import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: {
      title: '首页',
      requiresAuth: false
    }
  },
  {
    path: '/monster',
    name: 'MonsterManagement',
    component: () => import('@/views/monster/Management.vue'),
    meta: {
      title: '怪物配置管理',
      requiresAuth: false
    }
  },
  {
    path: '/equipment',
    name: 'EquipmentManagement',
    component: () => import('@/views/equipment/Management.vue'),
    meta: {
      title: '装备配置管理',
      requiresAuth: false
    }
  },
  {
    path: '/pill',
    name: 'PillManagement',
    component: () => import('@/views/pill/Management.vue'),
    meta: {
      title: '丹药配置管理',
      requiresAuth: false
    }
  },
  {
    path: '/material',
    name: 'MaterialManagement',
    component: () => import('@/views/material/Management.vue'),
    meta: {
      title: '材料配置管理',
      requiresAuth: false
    }
  },
  {
    path: '/pill-recipe',
    name: 'PillRecipeManagement',
    component: () => import('@/views/pillRecipe/Management.vue'),
    meta: {
      title: '丹方配置管理',
      requiresAuth: false
    }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || '修仙游戏管理后台'} - 修仙游戏管理后台`;
  next();
});

export default router;
