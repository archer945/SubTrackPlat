<template>
  <el-sub-menu v-if="hasChildren" :index="resolvePath(menu.path)">
    <template #title>
      <i v-if="menu.icon" :class="menu.icon"></i>
      <span>{{ menu.menuName }}</span>
    </template>
    <menu-item
      v-for="child in menu.children"
      :key="child.menuId"
      :menu="child"
      :base-path="resolvePath(menu.path)"
    />
  </el-sub-menu>

  <el-menu-item v-else :index="resolvePath(menu.path)">
    <i v-if="menu.icon" :class="menu.icon"></i>
    <template #title>{{ menu.menuName }}</template>
  </el-menu-item>
</template>

<script setup>
import { computed } from 'vue';
import path from 'path-browserify';

// 定义组件属性
const props = defineProps({
  menu: {
    type: Object,
    required: true
  },
  basePath: {
    type: String,
    default: ''
  }
});

// 是否有子菜单
const hasChildren = computed(() => {
  return props.menu.children && props.menu.children.length > 0;
});

// 解析路径
const resolvePath = (routePath) => {
  if (!routePath) return '';
  
  if (routePath.startsWith('/')) {
    return routePath;
  }
  
  return path.resolve(props.basePath || '/', routePath);
};
</script> 