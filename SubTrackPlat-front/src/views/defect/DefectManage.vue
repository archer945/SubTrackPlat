<template>

  <div class="page-title">
    缺陷管理
  </div>

  <div class="defect-list-page">
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="query" class="search-bar" size="small">

      <el-form-item label="所属任务">
        <el-input v-model="query.taskId" placeholder="请输入所属任务ID" clearable />
      </el-form-item>

      <el-form-item label="缺陷类型">
        <el-select
            v-model="query.type"
            placeholder="请选择缺陷类型"
            clearable
            style="width: 150px"
        >
          <el-option
              v-for="item in defectTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="是否属实">
        <el-select
            v-model="query.isValid"
            placeholder="请选择是否属实"
            clearable
            style="width: 140px"
        >
          <el-option label="是" value="true" />
          <el-option label="否" value="false" />
        </el-select>
      </el-form-item>

      <!-- 缺陷状态 -->
      <el-form-item label="缺陷状态">
        <el-select v-model="query.status" placeholder="请选择状态" style="width: 120px" clearable>
          <el-option label="待确认" value="PENDING" />
          <el-option label="已确认" value="CONFIRMED" />
          <el-option label="已驳回" value="REJECTED" />
          <el-option label="处理中" value="PROCESSING" />
          <el-option label="已整改" value="FIXED" />
          <el-option label="需复查" value="REVIEW_NEEDED" />
        </el-select>
      </el-form-item>

      <!-- 严重程度 -->
      <el-form-item label="严重程度">
        <el-select v-model="query.severity" placeholder="请选择严重程度" style="width: 120px" clearable>
          <el-option label="低" value="LOW" />
          <el-option label="中" value="MEDIUM" />
          <el-option label="高" value="HIGH" />
        </el-select>
      </el-form-item>

      <!-- 上报时间范围 -->
      <el-form-item label="上报时间">
        <el-date-picker
            v-model="query.reportTimeRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
        />
      </el-form-item>

      <!-- 搜索 / 重置按钮 -->
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="success" @click="handleExport">导出数据</el-button>
      <el-button @click="handleRefresh">刷新</el-button>
      <el-button type="primary" @click="goToSystemManage">系统管理</el-button>
    </div>

    <!-- 表格数据 -->
    <el-table :data="tableData" v-loading="loading" style="width: 100%;" border @row-dblclick="openPreview">
      <el-table-column type="index" label="序号" width="60" />

      <!-- 任务名称（可点击跳转） -->
      <el-table-column label="任务ID">
        <template #default="scope">
          <el-link type="primary" @click="viewTaskDetail(scope.row)">
            {{ scope.row.taskId }}
          </el-link>
        </template>
      </el-table-column>

      <el-table-column prop="defectType" label="缺陷类型" />
      <el-table-column prop="distance" label="距离原点位置" />

      <!-- TODO 缺陷图片 -->
      <el-table-column label="缺陷图片" width="120">
        <template #default="{ row }">
          <!-- 直接点缩略图触发 openPreview -->
          <el-image
              style="width: 80px; height: 60px; cursor: pointer; border-radius: 4px"
              :src="row.thumbnailUrl"
              fit="cover"
              @error="handleImageError"
              @click="openPreview(row)"
          >
            <!-- 加载失败 -->
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
                <span>加载失败</span>
              </div>
            </template>
            <!-- 加载中 -->
            <template #placeholder>
              <div class="image-placeholder">
                <el-icon><Loading /></el-icon>
              </div>
            </template>
          </el-image>

          <!-- 多图角标（可留可删）-->
          <span v-if="row.imageCount > 1" class="image-badge">
      +{{ row.imageCount - 1 }}
    </span>
        </template>
      </el-table-column>

      <!-- 是否属实 -->
      <el-table-column label="是否属实" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isValid ? 'success' : 'danger'">
            {{ row.isValid ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="level" label="严重程度" />
      <el-table-column prop="defectLength" label="缺陷长度" />
      <el-table-column prop="area" label="缺陷面积" />
      <el-table-column prop="count" label="缺陷数量" />
      <el-table-column prop="suggestion" label="整改建议" show-overflow-tooltip />
      <el-table-column prop="reportTime" label="上报时间" />

      <!-- 状态列 -->
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">
            {{ row.statusLabel }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
          background
          layout="prev, pager, next, jumper, ->, total"
          :total="pagination.total"
          :page-size="pagination.pageSize"
          :current-page="pagination.page"
          @current-change="handlePageChange"
      />
    </div>

    <!-- 缺陷详情对话框 -->
    <el-dialog v-model="previewVisible" title="缺陷详情" width="600px">
      <DefectDetail :defect="current" />
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>


    <!-- 新增：编辑状态对话框 -->
    <el-dialog
        v-model="editDialogVisible"
        title="更新缺陷状态"
        width="400px"
        @close="editingDefect = null; selectedStatus = '';"
    >
      <el-form label-width="80px">
        <el-form-item label="缺陷ID">
          <span>{{ editingDefect?.id }}</span>
        </el-form-item>
        <el-form-item label="当前状态">
          <span>{{ editingDefect?.statusLabel }}</span>
        </el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="selectedStatus" placeholder="请选择新状态" style="width: 100%">
            <el-option
                v-for="[value, label] in Object.entries(statusLabelMap)"
                :key="value"
                :label="label"
                :value="value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmUpdateStatus">确定</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览组件 -->
    <!-- <el-dialog
      v-model="viewerVisible"
      width="auto"
      top="5vh"
      custom-class="image-viewer-dialog"
      :show-close="false"
      destroy-on-close
    >
      <template #default>
        <el-image-viewer
          :url-list="viewerUrls"
          @close="viewerVisible = false"
          :initial-index="viewerIndex"
        />
        <div class="absolute top-4 right-4 z-50">
          <el-button size="small" type="primary" @click="downloadImage(viewerUrls[viewerIndex])">
            下载图片
          </el-button>
        </div>
      </template>
    </el-dialog> -->

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { getDefectList, exportDefects, DefectStatusEnum, deleteDefect, updateDefectStatus } from '@/api/defect/defect'
import { ElMessage, ElMessageBox } from 'element-plus'
import DefectDetail from '@/components/defect/DefectDetail.vue'
import { Picture, Loading } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()

const title = '缺陷管理'
const router = useRouter()
const viewTaskDetail = (taskId) => {
  router.push(`/tasks/${taskId}`)
}

// 跳转到系统管理页面
const goToSystemManage = () => {
  router.push('/system')
}

// 新增：控制编辑状态对话框的显示
const editDialogVisible = ref(false);
// 新增：存储当前正在编辑的缺陷对象
const editingDefect = ref<any | null>(null);
// 新增：存储在对话框中选择的新状态
const selectedStatus = ref('');

/**
 * 缺陷类型选项
 */
const defectTypeOptions = [
  { label: '结构裂缝', value: 'STRUCTURAL_CRACK' },
  { label: '渗水', value: 'LEAKAGE' },
  { label: '设备故障', value: 'EQUIPMENT_FAILURE' },
  { label: '照明问题', value: 'LIGHTING_ISSUE' },
  { label: '脱落', value: 'DETACHMENT' },
  { label: '腐蚀', value: 'CORROSION' },
  { label: '渗漏', value: 'SEEPAGE' },
  { label: '设备异常', value: 'EQUIPMENT_ABNORMALITY' }
]

/**
 * 缺陷状态中文映射
 */
const statusLabelMap: Record<string, string> = {
  PENDING: '待确认',
  CONFIRMED: '已确认',
  REJECTED: '已驳回',
  PROCESSING: '处理中',
  FIXED: '已整改',
  REVIEW_NEEDED: '需复查'
}

/**
 * 严重程度中文映射
 */
const severityLabelMap: Record<string, string> = {
  LOW: '低',
  MIDDLE: '中',
  MEDIUM: '中',
  HIGH: '高',
  CRITICAL: '严重'
}

/**
 * 根据状态获取 tag 类型
 */
function statusTagType(status: string) {
  switch (status) {
    case DefectStatusEnum.CONFIRMED:
      return 'success'
    case DefectStatusEnum.REJECTED:
      return 'danger'
    case DefectStatusEnum.FIXED:
      return 'info'
    default:
      return 'warning'
  }
}

// 查询条件
const query = reactive({
  status: '',
  type: '',
  taskId: '',
  isValid: '',
  severity: '',
  reportTimeRange: ''
})

// 表格数据与加载状态
const loading = ref(false)
const tableData = ref<any[]>([])

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 5,
  total: 0
})

const totalPages = computed(() => Math.ceil((pagination.total || 1) / pagination.pageSize))

// 当前预览缺陷
const previewVisible = ref(false)
const current = ref<any | null>(null)

// 图片预览
const viewerVisible = ref(false)
const viewerUrls = ref<string[]>([])
const viewerIndex = ref(0)

// 初始化
onMounted(() => {
  fetchData()
})

async function fetchData() {
  try {
    loading.value = true

    var startTime = undefined
    var endTime = undefined

    if (query.reportTimeRange?.[0] != undefined && query.reportTimeRange?.[1] != undefined) {
      startTime = query.reportTimeRange?.[0] + "T00:00:00"
      endTime = query.reportTimeRange?.[1] + "T00:00:00"
    }

    const params = {
      pageIndex: pagination.page,
      pageSize: pagination.pageSize,
      taskId: query.taskId || undefined,
      type: query.type || undefined,
      isValid: query.isValid || undefined,
      severity: query.severity || undefined,
      status: query.status || undefined,
      startTime,
      endTime
    }

    console.log(params)

    const response = await getDefectList(params)

    if (!response.data || !response.data.data) {
      throw new Error('Invalid API response structure')
    }

    const { records, total } = response.data.data

    tableData.value = (records || []).map((item: any) => {
      const status = item.status as string

      // 处理图片数据 - 与后端字段保持一致
      const images = item.images || []
      const firstImage = images[0]?.thumbnailUrl || ''
      const previewUrls = images.map((img: any) => img.imageUrl).filter(Boolean)

      console.log(firstImage)
      console.log(previewUrls)


      return {
        ...item,
        taskId: item.taskId || '无任务',
        defectType: defectTypeOptions.find((v) => v.value === item.type)?.label || '未知类型',
        distance: item.location ?? '未知位置',
        thumbnailUrl: firstImage,
        previewUrls,
        imageCount: images.length,
        isValid: item.isValid,
        level: severityLabelMap[item.severity] || '未知',
        defectLength: item.defectLength ?? '未知',
        area: item.defectArea ?? '未知',
        count: item.defectCount ?? 0,
        suggestion: item.suggestion || '无建议',
        reportTime: item.foundTime || '未知时间',
        status,
        statusLabel: statusLabelMap[status] || '未知',
        id: item.id
      }
    })

    pagination.total = total || 0
    if ((records || []).length === 0) ElMessage.info('暂无缺陷数据')
  } catch (error) {
    console.error('API请求失败:', error)
    ElMessage.error('获取缺陷数据失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}


async function handleDelete(row: any) {
  ElMessageBox.confirm(
      `确定要删除缺陷 "${row.id}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          await deleteDefect(row.id) // 调用 api/defect.ts 中的 deleteDefect
          ElMessage.success('删除成功')
          fetchData()
        } catch (error) {
          console.error('删除失败:', error)
          ElMessage.error('删除失败')
        }
      })
      .catch(() => {
        ElMessage.info('已取消删除')
      })
}

async function handleEdit(row: any) {
  editingDefect.value = { ...row }; // 复制行数据，避免直接修改表格数据
  selectedStatus.value = row.status; // 设置当前状态为默认选中值
  editDialogVisible.value = true; // 打开对话框
}

// 新增：处理状态更新的确认逻辑
async function confirmUpdateStatus() {
  if (!editingDefect.value || !selectedStatus.value) {
    ElMessage.warning('请选择一个状态');
    return;
  }
  try {
    const userId = userStore.userInfo?.userId
    if (!userId) {
      ElMessage.error('未获取到当前登录用户ID，请重新登录')
      return
    }
    await updateDefectStatus(editingDefect.value.id, selectedStatus.value, userId)
    ElMessage.success('状态更新成功');
    editDialogVisible.value = false; // 关闭对话框
    fetchData(); // 刷新数据
  } catch (error) {
    console.error('状态更新失败:', error);
    ElMessage.error('状态更新失败');
  }
}


function handleSearch() {
  pagination.page = 1
  fetchData()
}

function handleReset() {
  Object.assign(query, {  status: '',type: '',taskId: '',isValid: '',severity: '',reportTimeRange: '' })
  handleSearch()
}

function handlePageChange(page: number) {
  pagination.page = page
  fetchData()
}

function handleSizeChange(size: number) {
  pagination.pageSize = size
  pagination.page = 1
  fetchData()
}

function handleRefresh() {
  fetchData()
}

function openPreview(row: any) {
  current.value = row
  previewVisible.value = true
}

// TODO 导出有问题
async function handleExport() {
  try {
    const response = await exportDefects({
      // 添加你的查询参数
    });

    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'defects.csv');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    ElMessage.success('导出成功');
  } catch (error) {
    console.error('导出失败:', error);
    ElMessage.error('导出失败');
  }
}

function handleImageError(e: any) {
  console.error('图片加载失败:', e)
}

function handlePreview(row: any) {
  if (row.previewUrls?.length) {
    // viewerUrls.value = row.previewUrls
    viewerUrls.value = [...row.previewUrls]
    viewerIndex.value = 0
    viewerVisible.value = true
  }
}

// 下载图片 TODO
function downloadImage(url: string) {
  if (!url) {
    ElMessage.warning('无有效图片可下载')
    return
  }
  const link = document.createElement('a')
  link.href = url
  link.download = url.split('/').pop() || 'defect-image'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
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

/* 保留原有的图片相关样式 */
.image-viewer-dialog .el-dialog__body {
  padding: 0;
}

.image-container {
  position: relative;
  display: inline-block;
}

.image-badge {
  position: absolute;
  right: -8px;
  bottom: -8px;
  background-color: var(--el-color-primary);
  color: #fff;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-error,
.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  color: var(--el-text-color-secondary);
  background-color: var(--el-fill-color-light);
}

.image-error span {
  font-size: 12px;
  margin-top: 4px;
}

/* 顶部标题 */
.page-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: left;
  color: #333;
}

.action-buttons {
  display: flex;
  gap: 0px; /* 按钮间距 */
  justify-content: center; /* 水平居中 */
}
</style>