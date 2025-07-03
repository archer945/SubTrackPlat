<template>
  <div class="task-list-page">
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="searchForm" class="search-bar" size="small">
      <div class="search-row">
        <el-form-item label="任务名称">
          <el-input v-model="searchForm.taskName" placeholder="请输入任务名称" style="width: 100px"/>
        </el-form-item>
        <el-form-item label="执行人">
          <el-autocomplete
              v-model="searchForm.executorName"
              :fetch-suggestions="querySearchExecutor"
              placeholder="请输入执行人"
              @select="handleExecutorSelect"
              style="width: 100px"
          />
        </el-form-item>
        <el-form-item label="创建人">
          <el-autocomplete
              v-model="searchForm.creatorName"
              :fetch-suggestions="querySearchCreator"
              placeholder="请输入创建人"
              @select="handleCreatorSelect"
              style="width: 100px"
          />
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" style="width: 100px">
            <el-option label="待执行" value="待执行" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已暂停" value="已暂停" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
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
        <el-form-item label="任务类型">
          <el-select v-model="searchForm.taskType" placeholder="请选择类型"style="width: 100px">
            <el-option label="巡检" value="巡检" />
            <el-option label="检修" value="检修" />
            <el-option label="维护" value="维护" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="searchForm.priority" placeholder="请选择优先级" style="width: 110px">
            <el-option label="高" value="高" />
            <el-option label="中" value="中" />
            <el-option label="低" value="低" />
          </el-select>
        </el-form-item>
      </div>

      <!-- 按钮 -->
      <div class="button-row">
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </div>
    </el-form>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="success" @click="exportData">导出数据</el-button>
      <el-button @click="fetchData">刷新</el-button>
    </div>

    <!-- 表格数据 -->
    <el-table :data="taskList" style="width: 100%;" border>
      <el-table-column
          type="index"
          label="序号"
          width="60"
          :index="indexMethod"
      />
      <el-table-column label="任务编号">
        <template #default="scope">
          <el-link type="primary" @click="viewDetail(scope.row)">
            {{ 'TASK-' + scope.row.id }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="任务名称" />
      <el-table-column prop="startPoint" label="起始地点" />
      <el-table-column prop="endPoint" label="结束地点" />
      <el-table-column prop="lineName" label="线路" />
      <el-table-column prop="creatorName" label="创建人" />
      <el-table-column prop="executorName" label="执行人" />
      <el-table-column label="最后执行时间">
        <template #default="scope">
          {{ formatTime(scope.row.actualStart) }}
        </template>
      </el-table-column>
      <el-table-column label="巡视完成时间">
        <template #default="scope">
          {{ formatTime(scope.row.actualEnd) }}
        </template>
      </el-table-column>
      <el-table-column label="数据上传时间">
        <template #default="scope">
          {{ formatTime(scope.row.updateTime) }}
        </template>
      </el-table-column>
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
          layout="sizes, prev, pager, next, jumper, ->, total"
          :total="total"
          :page-size="pageSize"
          :current-page="pageNum"
          :page-sizes="[10, 20, 30, 50]"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fetchTasks, exportTasks, deleteTask as apiDeleteTask } from '@/services/taskService'
import { fetchUserList } from '@/services/userService'
import { useTaskStore } from '@/stores/taskStore'
const router = useRouter()
const taskStore = useTaskStore()

const indexMethod = (index) => (pageNum.value - 1) * pageSize.value + index + 1
const fetchData = async () => {
  const params = {
    page: pageNum.value,
    size: pageSize.value,
    name: searchForm.value.taskName,
    executorId: searchForm.value.executorId,
    creatorId: searchForm.value.creatorId,
    type: searchForm.value.taskType,
    priority: searchForm.value.priority,
    status: searchForm.value.status,
    createTimeStart: searchForm.value.createTimeRange?.[0] || '',
    createTimeEnd: searchForm.value.createTimeRange?.[1] || ''
  }
  try {
    const { list, total: totalCount } = await taskStore.loadTasks(params)
    taskList.value = list
    total.value = totalCount
  } catch (e) {
    ElMessage.error('加载任务数据失败')
  }
}
const searchForm = ref({
  taskName: '',
  executorName: '', // 用户输入的名字
  executorId: '',   // 选中的用户ID
  creatorName: '',  // 新增
  creatorId: '',    // 新增
  status: '',
  createTimeRange: [],
  taskType: '',
  priority: ''
})
const taskList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)



const handleSearch = () => {
  pageNum.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.value = {
    taskName: '',
    executorName: '',
    executorId: '',
    creatorName: '',
    creatorId: '',
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

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  pageNum.value = 1
  fetchData()
}

const exportData = () => {
  const params = {
    ...searchForm.value,
    createTimeStart: searchForm.value.createTimeRange?.[0] || '',
    createTimeEnd: searchForm.value.createTimeRange?.[1] || ''
  }

  exportTasks(params)
      .then(response => {
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', '任务数据.xlsx')
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
      })
      .catch(() => {
        ElMessage.error('导出失败')
      })
}

const deleteTask = (row) => {
  ElMessageBox.confirm(`确定删除任务【${row.name}】吗？`, '提示', { type: 'warning' })
      .then(() => {
        apiDeleteTask(row.id) // 使用API服务进行删除
            .then(() => {
              ElMessage.success('删除成功')
              fetchData()
            })
            .catch(() => {
              ElMessage.error('删除失败')
            })
      })
}

const querySearchExecutor = (queryString, cb) => {
  if (!queryString) {
    cb([])
    return
  }
  fetchUserList({ name: queryString }).then(res => {
    cb((res.data.list || []).map(user => ({
      value: user.realName,
      id: user.userId
    })))
  })
}

const handleExecutorSelect = (item) => {
  searchForm.value.executorId = item.id
  searchForm.value.executorName = item.value
}

const querySearchCreator = (queryString, cb) => {
  if (!queryString) {
    cb([])
    return
  }
  fetchUserList({ name: queryString }).then(res => {
    cb((res.data.list || []).map(user => ({
      value: user.realName,
      id: user.userId
    })))
  })
}

const handleCreatorSelect = (item) => {
  searchForm.value.creatorId = item.id
  searchForm.value.creatorName = item.value
}
const viewDetail = (row) => {
  router.push({ name: 'TaskDetail', params: { id: row.id } })
}//路由跳转
onMounted(fetchData)
function formatTime(time) {
  if (!time) return '';
  // 你可以根据需要格式化时间
  return new Date(time).toLocaleString();
}


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
