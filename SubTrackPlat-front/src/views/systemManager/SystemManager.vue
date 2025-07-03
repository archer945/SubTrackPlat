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
          <el-menu-item index="user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="role">
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
import { User, UserFilled, Menu, OfficeBuilding, Setting, Warning } from '@element-plus/icons-vue'
import UserManage from './UserManage.vue'
import RoleManage from './RoleManage.vue'
import MenuManage from './MenuManage.vue'
import DeptManage from './DeptManage.vue'

// 当前激活的菜单
const activeMenu = ref('user')

// 当前显示的组件
const currentComponent = shallowRef(UserManage)

// 处理菜单选择
const handleMenuSelect = (index) => {
  activeMenu.value = index
  switch (index) {
    case 'user':
      currentComponent.value = UserManage
      break
    case 'role':
      currentComponent.value = RoleManage
      break
    case 'menu':
      currentComponent.value = MenuManage
      break
    case 'dept':
      currentComponent.value = DeptManage
      break
    case 'param':
      // 暂未实现参数配置
      break
    default:
      currentComponent.value = UserManage
  }
}

// 初始化
onMounted(() => {
  // 默认显示用户管理
  handleMenuSelect('user')
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