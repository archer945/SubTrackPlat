<template>
  <el-card class="detail-card">
    <!-- 顶部标题 + 返回按钮 -->
    <template #header>
      <div class="detail-header">
        <el-icon><Document /></el-icon>
        <span style="margin-left: 8px;">任务详情：{{ task.name || '-' }}</span>
        <el-button
            type="primary"
            plain
            size="small"
            style="margin-left: auto"
            @click="goCalendar"
        >
          返回日历视图
        </el-button>
        <el-button
            type="success"
            plain
            size="small"
            style="margin-left: 10px"
            @click="goList"
        >
          返回列表视图
        </el-button>
      </div>
    </template>

    <!-- 基本信息 -->
    <el-divider content-position="left">基本信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="任务编号">{{ task.id }}</el-descriptions-item>
      <el-descriptions-item label="任务名称">{{ task.name }}</el-descriptions-item>
      <el-descriptions-item label="任务类型">{{ task.type }}</el-descriptions-item>
      <el-descriptions-item label="优先级">
        <el-tag
            :type="task.priority === '高' ? 'danger' : task.priority === '中' ? 'warning' : 'success'"
            disable-transitions
        >
          {{ task.priority }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="任务描述" :span="2">{{ task.description }}</el-descriptions-item>
    </el-descriptions>

    <!-- 执行信息 -->
    <el-divider content-position="left">执行信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="执行人">{{ task.executorName }}</el-descriptions-item>
      <el-descriptions-item label="协助人">{{ task.assistantName }}</el-descriptions-item>
      <el-descriptions-item label="计划开始">{{ formatDate(task.plannedStart) }}</el-descriptions-item>
      <el-descriptions-item label="计划结束">{{ formatDate(task.plannedEnd) }}</el-descriptions-item>
      <el-descriptions-item label="实际开始">{{ formatDate(task.actualStart) }}</el-descriptions-item>
      <el-descriptions-item label="实际结束">{{ formatDate(task.actualEnd) }}</el-descriptions-item>
    </el-descriptions>

    <!-- 路线信息 -->
    <el-divider content-position="left">路线信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="巡检线路">{{ task.lineName }}</el-descriptions-item>
      <el-descriptions-item label="巡检范围">{{ task.rangeDescription }}</el-descriptions-item>
      <el-descriptions-item label="起始位置">{{ task.startPoint }}</el-descriptions-item>
      <el-descriptions-item label="结束位置">{{ task.endPoint }}</el-descriptions-item>
    </el-descriptions>

    <!-- 状态与结果 -->
    <el-divider content-position="left">任务状态与结果</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="任务状态">
        <el-tag :type="statusTagType(task.status)">
          {{ task.status }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="完成度">
        <el-progress :percentage="task.completionRate" style="width: 80%;" />
      </el-descriptions-item>
      <el-descriptions-item label="执行结果" :span="2">
        {{ task.result }}
      </el-descriptions-item>
      <el-descriptions-item label="发现问题">
        {{ task.problemsFound }} 个
        <el-button
            size="small"
            type="warning"
            plain
            style="margin-left: 10px"
            @click="viewDefects"
        >
          查看缺陷
        </el-button>
      </el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Document } from '@element-plus/icons-vue'
import { getTaskById } from '@/api/taskManager/taskService'
import { useTaskDetailStore } from '@/stores/taskDetailStore'
const taskDetailStore = useTaskDetailStore()

const route = useRoute()
const router = useRouter()
const taskId = route.params.id

const task = ref({})

// 请求后端接口获取任务数据
onMounted(async () => {
  try {
    const data = await getTaskById(taskId)
    task.value = data
  } catch (error) {
    console.error('任务数据加载失败', error)
  }
})

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 根据任务状态设置标签类型
const statusTagType = (status) => {
  switch (status) {
    case '待执行': return 'info'
    case '进行中': return 'primary'
    case '已完成': return 'success'
    case '已暂停': return 'warning'
    case '已取消': return 'danger'
    default: return ''
  }
}

const goCalendar = () => {
  router.push({ path: '/tasks', query: { view: 'calendar' } })
}

const goList = () => {
  router.push({ path: '/tasks', query: { view: 'list' } })
}
const viewDefects = () => {
  router.push({ path: '/defects', query: { taskCode: task.value.id } })
}
onMounted(async () => {
  task.value = await taskDetailStore.getTaskDetail(taskId)
})//前端缓存
</script>

<style scoped>
.detail-card {
  max-width: 1000px;
  margin: 30px auto;
}
.detail-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}
</style>
