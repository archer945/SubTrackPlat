// src/directives/permission.js
import { usePermissionStore } from '@/stores/permissionStore'

/**
 * v-permission 权限控制指令
 * 用法:
 * 1. v-permission="'system:user:add'"  // 需要指定权限
 * 2. v-permission="{ or: ['system:user:add', 'system:user:edit'] }" // 需要其中一个权限
 * 3. v-permission="{ and: ['system:user:add', 'system:user:edit'] }" // 需要同时拥有所有权限
 */
export const permission = {
  mounted(el, binding) {
    const permissionStore = usePermissionStore();
    
    // 解析指令参数
    const value = binding.value;
    
    let hasPermission = false;
    
    if (typeof value === 'string') {
      // 单个权限验证
      hasPermission = permissionStore.hasPermission(value);
    } else if (typeof value === 'object') {
      if (Array.isArray(value)) {
        // 数组，当作或逻辑处理
        hasPermission = permissionStore.hasAnyPermission(value);
      } else {
        // 对象形式，支持and/or逻辑
        if (value.or && Array.isArray(value.or)) {
          hasPermission = permissionStore.hasAnyPermission(value.or);
        } else if (value.and && Array.isArray(value.and)) {
          hasPermission = permissionStore.hasAllPermissions(value.and);
        }
      }
    }
    
    // 无权限则移除元素
    if (!hasPermission) {
      // 获取父元素
      const parentNode = el.parentNode;
      if (parentNode) {
        parentNode.removeChild(el);
      } else {
        // 如果还没有挂载到DOM，延迟处理
        el.style.display = 'none';
      }
    }
  }
};

// 注册所有权限控制指令
export default {
  install(app) {
    app.directive('permission', permission);
  }
} 