<template>
  <div class="task-list-page">
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="searchForm" class="search-bar" size="small">
      <el-form-item label="任务编号">
        <el-input v-model="searchForm.taskCode" placeholder="请输入任务编号" />
      </el-form-item>
      <el-form-item label="任务名称">
        <el-input v-model="searchForm.taskName" placeholder="请输入任务名称" />
      </el-form-item>
      <el-form-item label="执行人">
        <el-input v-model="searchForm.executor" placeholder="请输入执行人" />
      </el-form-item>

      <!-- 任务状态 -->
      <el-form-item label="任务状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" style="width: 120px">
          <el-option label="待执行" value="待执行" />
          <el-option label="进行中" value="进行中" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已暂停" value="已暂停" />
          <el-option label="已取消" value="已取消" />
        </el-select>
      </el-form-item>

      <!-- 创建时间范围 -->
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="searchForm.createTimeRange"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 240px"
        />
      </el-form-item>

      <!-- 任务类型 -->
      <el-form-item label="任务类型">
        <el-select v-model="searchForm.taskType" placeholder="请选择类型" style="width: 120px">
          <el-option label="巡检" value="巡检" />
          <el-option label="检修" value="检修" />
          <el-option label="维护" value="维护" />
        </el-select>
      </el-form-item>

      <!-- 优先级 -->
      <el-form-item label="优先级">
        <el-select v-model="searchForm.priority" placeholder="请选择优先级" style="width: 120px">
          <el-option label="高" value="高" />
          <el-option label="中" value="中" />
          <el-option label="低" value="低" />
        </el-select>
      </el-form-item>

      <!-- 搜索 / 重置按钮 -->
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="success" @click="exportData">导出数据</el-button>
      <el-button @click="fetchData">刷新</el-button>
    </div>

    <!-- 表格数据 -->
    <el-table :data="taskList" style="width: 100%;" border>
      <el-table-column prop="index" label="序号" width="60" />

      <!-- 任务编号（可点击跳转） -->
      <el-table-column label="任务编号">
        <template #default="scope">
          <el-link type="primary" @click="viewDetail(scope.row)">
            {{ scope.row.taskCode }}
          </el-link>
        </template>
      </el-table-column>

      <el-table-column prop="taskName" label="任务名称" />
      <el-table-column prop="startLocation" label="起始地点" />
      <el-table-column prop="distance" label="任务距离" />
      <el-table-column prop="creator" label="创建人" />
      <el-table-column prop="executor" label="执行人" />
      <el-table-column prop="startTime" label="最后执行时间" />
      <el-table-column prop="endTime" label="巡视完成时间" />
      <el-table-column prop="uploadTime" label="数据上传时间" />

      <!-- 操作列：仅保留删除 -->
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button size="small" type="danger" @click="deleteTask(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        background
        layout="prev, pager, next, jumper, ->, total"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const searchForm = ref({
  taskCode: '',
  taskName: '',
  executor: '',
  status: '',
  createTimeRange: [],
  taskType: '',
  priority: ''
})

const taskList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchData = () => {
  taskList.value = [
    {
      index: 1,
      taskCode: 'TASK-20240301-001',
      taskName: '1号线隧道巡检',
      startLocation: '起点站',
      distance: '2.5km',
      creator: '张三',
      executor: '李四',
      startTime: '2024-03-01 09:00',
      endTime: '2024-03-01 11:30',
      uploadTime: '2024-03-01 12:00'
    }
  ]
  total.value = 50
}

const handleSearch = () => {
  const params = {
    ...searchForm.value,
    createTimeStart: searchForm.value.createTimeRange?.[0] || '',
    createTimeEnd: searchForm.value.createTimeRange?.[1] || ''
  }
  delete params.createTimeRange
  console.log('搜索参数：', params)
  fetchData()
}

const handleReset = () => {
  searchForm.value = {
    taskCode: '',
    taskName: '',
    executor: '',
    status: '',
    createTimeRange: [],
    taskType: '',
    priority: ''
  }
  fetchData()
}

const handlePageChange = (newPage) => {
  pageNum.value = newPage
  fetchData()
}

const exportData = () => {
  console.log('导出数据')
}

const viewDetail = (row) => {
  console.log('跳转详情页:', row.taskCode)
  router.push({ name: 'TaskDetail', params: { id: row.taskCode } })
}

const deleteTask = (row) => {
  console.log('删除任务:', row.taskCode)
}

onMounted(fetchData)
</script>

<style scoped>
.search-bar {
  padding: 10px;
  margin-bottom: 10px;
  background-color: #fff;
  border-radius: 4px;
}
.action-bar {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
}
.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}
</style>
