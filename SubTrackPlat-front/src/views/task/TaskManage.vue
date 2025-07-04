<template>
  <el-card class="task-manage-container">
    <!-- 顶部标题 -->
    <div class="page-title">
      任务管理
    </div>

    <!-- 视图切换按钮 -->
    <div class="tab-buttons">
      <el-button
          class="tab-button"
          :type="activeTab === 'calendar' ? 'primary' : 'default'"
          @click="activeTab = 'calendar'"
      >
        日历视图
      </el-button>
      <el-button
          class="tab-button"
          :type="activeTab === 'list' ? 'primary' : 'default'"
          @click="activeTab = 'list'"
      >
        列表视图
      </el-button>
    </div>

    <!-- 内容区域 -->
    <div class="view-content">
      <TaskCalendar v-if="activeTab === 'calendar'" />
      <TaskList v-if="activeTab === 'list'" />
    </div>
  </el-card>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import TaskCalendar from './TaskCalendar.vue'
import TaskList from './TaskList.vue'

const activeTab = ref('calendar') // 默认日历视图
const route = useRoute()

// 监听路由参数变化
watch(
    () => route.query.view,
    (view) => {
      if (view === 'list') {
        activeTab.value = 'list'
      } else if (view === 'calendar') {
        activeTab.value = 'calendar'
      }
    },
    { immediate: true }
)
</script>

<style scoped>
.task-manage-container {
  padding: 20px;
  background-color: #f9f9f9;
}

/* 顶部标题 */
.page-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: left;
  color: #333;
}

/* 按钮区：水平一行两列 */
.tab-buttons {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.tab-button {
  flex: 1;
  margin: 0 5px;
}

/* 内容区卡片样式 */
.view-content {
  background-color: #fff;
  padding: 20px;
  min-height: 500px;
  border: 1px solid #ddd;
}
</style>
