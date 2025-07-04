<template>
  <div class="menu-sidebar">
    <el-menu
      :default-active="activeMenu"
      router
      :collapse="isCollapse"
      :unique-opened="true"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
    >
      <menu-item v-for="menu in menus" :key="menu.menuId" :menu="menu" />
    </el-menu>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { usePermissionStore } from '@/stores/permissionStore';
import MenuItem from './MenuItem.vue';

// 是否折叠
const isCollapse = ref(false);
// 路由对象
const route = useRoute();
// 权限存储
const permissionStore = usePermissionStore();

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path;
});

// 菜单列表
const menus = computed(() => {
  return permissionStore.menus || [];
});

// 组件挂载时加载菜单
onMounted(async () => {
  if (permissionStore.menus.length === 0) {
    try {
      await permissionStore.loadUserMenus();
    } catch (error) {
      console.error('加载菜单失败', error);
    }
  }
});

// 切换折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

// 对外暴露方法
defineExpose({
  toggleCollapse
});
</script>

<style scoped>
.menu-sidebar {
  height: 100%;
  background-color: #304156;
}

.menu-sidebar :deep(.el-menu) {
  border-right: none;
}

.menu-sidebar :deep(.el-menu--collapse) {
  width: 64px;
}
</style>