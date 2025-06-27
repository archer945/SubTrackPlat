<template>
  <el-card class="detail-card">
    <!-- 顶部标题 + 返回按钮 -->
    <template #header>
      <div class="detail-header">
        <el-icon><Document /></el-icon>
        <span style="margin-left: 8px;">任务详情：{{ task.taskName || '-' }}</span>
        <el-button
          type="primary"
          plain
          size="small"
          style="margin-left: auto"
          @click="goBack"
        >
          返回任务列表
        </el-button>
      </div>
    </template>

    <!-- 基本信息 -->
    <el-divider content-position="left">基本信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="任务编号">{{ task.taskCode }}</el-descriptions-item>
      <el-descriptions-item label="任务名称">{{ task.taskName }}</el-descriptions-item>
      <el-descriptions-item label="任务类型">{{ task.taskType }}</el-descriptions-item>
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
      <el-descriptions-item label="执行人">{{ task.executor }}</el-descriptions-item>
      <el-descriptions-item label="协助人">{{ task.assistant }}</el-descriptions-item>
      <el-descriptions-item label="计划开始">{{ task.planStart }}</el-descriptions-item>
      <el-descriptions-item label="计划结束">{{ task.planEnd }}</el-descriptions-item>
      <el-descriptions-item label="实际开始">{{ task.startTime }}</el-descriptions-item>
      <el-descriptions-item label="实际结束">{{ task.endTime }}</el-descriptions-item>
    </el-descriptions>

    <!-- 路线信息 -->
    <el-divider content-position="left">路线信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="巡检线路">{{ task.line }}</el-descriptions-item>
      <el-descriptions-item label="巡检范围">{{ task.scope }}</el-descriptions-item>
      <el-descriptions-item label="起始位置">{{ task.startLocation }}</el-descriptions-item>
      <el-descriptions-item label="结束位置">{{ task.endLocation }}</el-descriptions-item>
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
        <el-progress :percentage="task.progress" style="width: 80%;" />
      </el-descriptions-item>
      <el-descriptions-item label="执行结果" :span="2">
        {{ task.result }}
      </el-descriptions-item>
      <el-descriptions-item label="发现问题">
        {{ task.problemCount }} 个
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
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Document } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const taskId = route.params.id

// 模拟任务数据，可换成 API 请求
const task = ref({
  taskCode: taskId,
  taskName: '1号线隧道巡检',
  taskType: '巡检',
  priority: '高',
  description: '对地铁1号线区间进行例行巡检，记录异常点。',
  executor: '李四',
  assistant: '王五',
  planStart: '2024-03-01 08:00',
  planEnd: '2024-03-01 12:00',
  startTime: '2024-03-01 09:00',
  endTime: '2024-03-01 11:30',
  line: '1号线',
  scope: '区间隧道（起点至终点）',
  startLocation: '起点站',
  endLocation: '终点站',
  status: '进行中',
  progress: 75,
  result: '部分检查完成，发现小问题',
  problemCount: 3
})

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

const goBack = () => {
  router.push('/tasks')
}

const viewDefects = () => {
  router.push({ path: '/defects', query: { taskCode: task.value.taskCode } })
}
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
