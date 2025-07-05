<template>
  <el-card class="system-manager-container">
    <!-- 顶部标题 -->
    <div class="page-title">
      系统管理
    </div>

    <div class="system-content">
      <!-- 左侧菜单 -->
      <div class="left-menu">
        <el-menu
            :default-active="activeMenu"
            class="system-menu"
            @select="handleMenuSelect"
        >
          <!-- 使用v-permission指令控制菜单项的显示 -->
          <!-- v-permission="'system:user:list'" -->
          <el-menu-item index="user" >
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>

          <el-menu-item index="role" >
            <el-icon><UserFilled /></el-icon>
            <span>角色管理</span>
          </el-menu-item>

          <el-menu-item index="menu">
            <el-icon><Menu /></el-icon>
            <span>菜单管理</span>
          </el-menu-item>

          <el-menu-item index="dept">
            <el-icon><OfficeBuilding /></el-icon>
            <span>部门管理</span>
          </el-menu-item>

          <el-menu-item index="param">
            <el-icon><Setting /></el-icon>
            <span>参数配置</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容区 -->
      <div class="right-content">
        <keep-alive>
          <component :is="currentComponent" />
        </keep-alive>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, shallowRef, onMounted } from 'vue'
import { User, UserFilled, Menu, OfficeBuilding, Setting } from '@element-plus/icons-vue'
import UserManage from './UserManage.vue'
import RoleManage from './RoleManage.vue'
import MenuManage from './MenuManage.vue'
import DeptManage from './DeptManage.vue'
import { usePermissionStore } from '@/stores/permissionStore'

// 权限store
const permissionStore = usePermissionStore();

// 当前激活的菜单
const activeMenu = ref('user')

// 当前显示的组件
const currentComponent = shallowRef(UserManage)

// 处理菜单选择
const handleMenuSelect = (index) => {
  activeMenu.value = index
  switch (index) {
    case 'user':
      //if (permissionStore.hasPermission('system:user:list')) {
        currentComponent.value = UserManage
      //}
      break
    case 'role':
      //if (permissionStore.hasPermission('system:role:list')) {
        currentComponent.value = RoleManage
      //}
      break
    case 'menu':
      //if (permissionStore.hasPermission('system:menu:list')) {
        currentComponent.value = MenuManage
      //}
      break
    case 'dept':
      //if (permissionStore.hasPermission('system:dept:list')) {
        currentComponent.value = DeptManage
      //}
      break
    case 'param':
      // 暂未实现参数配置
      break
    default:
      // 找到用户有权限的第一个菜单项
      findFirstAvailableMenu();
  }
}

// 找到第一个可用的菜单
const findFirstAvailableMenu = () => {
  const menuPerms = [
    { index: 'user', perm: 'system:user:list' },
    { index: 'role', perm: 'system:role:list' },
    { index: 'menu', perm: 'system:menu:list' },
    { index: 'dept', perm: 'system:dept:list' },
    { index: 'param', perm: 'system:param:list' }
  ];

  for (const menu of menuPerms) {
    if (permissionStore.hasPermission(menu.perm)) {
      activeMenu.value = menu.index;
      handleMenuSelect(menu.index);
      return;
    }
  }

  // 如果没有任何权限，显示空页面或权限提示
  currentComponent.value = null;
}

// 初始化
onMounted(() => {
  // 查找用户有权限的第一个菜单
  findFirstAvailableMenu();
})
</script>

<style scoped>
.system-manager-container {
  padding: 20px;
  background-color: #f9f9f9;
  min-height: calc(100vh - 100px);
}

/* 顶部标题 */
.page-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: left;
  color: #333;
}

/* 系统内容区 */
.system-content {
  display: flex;
  min-height: calc(100vh - 180px);
  background-color: #fff;
  border: 1px solid #ddd;
}

/* 左侧菜单 */
.left-menu {
  width: 200px;
  border-right: 1px solid #ddd;
}

.system-menu {
  height: 100%;
}

/* 右侧内容区 */
.right-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style> 