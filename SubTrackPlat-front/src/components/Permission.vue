<template>
  <div v-if="hasPermission">
    <slot></slot>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { usePermissionStore } from '@/stores/permissionStore';

const props = defineProps({
  // 权限标识
  permission: {
    type: String,
    default: ''
  },
  // 权限标识数组(任一权限)
  anyPermission: {
    type: Array,
    default: () => []
  },
  // 权限标识数组(所有权限)
  allPermission: {
    type: Array,
    default: () => []
  }
});

const permissionStore = usePermissionStore();

// 判断是否有权限
const hasPermission = computed(() => {
  if (props.permission) {
    return permissionStore.hasPermission(props.permission);
  }
  
  if (props.anyPermission && props.anyPermission.length > 0) {
    return permissionStore.hasAnyPermission(props.anyPermission);
  }
  
  if (props.allPermission && props.allPermission.length > 0) {
    return permissionStore.hasAllPermissions(props.allPermission);
  }
  
  return false;
});
</script> 