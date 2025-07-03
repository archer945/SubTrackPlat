<template>
  <div class="defect-manage p-4">
    <!-- 页面标题 -->
    <el-page-header :content="title" class="mb-4" />

    <!-- 搜索条件表单 -->
    <el-card class="mb-4" shadow="always">
      <el-form
          :inline="true"
          :model="query"
          class="flex flex-wrap items-center"
          @keyup.enter.native="handleSearch"
      >
        <el-form-item label="所属任务">
          <el-input v-model="query.task" placeholder="请输入所属任务" clearable />
        </el-form-item>

        <!-- 缺陷类型下拉框 -->
        <el-form-item label="缺陷类型">
          <el-select
              v-model="query.type"
              placeholder="请选择缺陷类型"
              clearable
              style="width: 180px"
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
              v-model="query.reality"
              placeholder="请选择是否属实"
              clearable
              style="width: 140px"
          >
            <el-option label="是" value="true" />
            <el-option label="否" value="false" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 -->
    <el-card class="mb-2" shadow="always">
      <template #header>
        <div class="flex justify-between items-center">
          <div>
            <el-button type="primary" plain @click="handleExport">导出</el-button>
          </div>
          <el-button text type="primary" @click="handleRefresh">刷新</el-button>
        </div>
      </template>

      <!-- 缺陷表格 -->
      <el-table
          :data="tableData"
          v-loading="loading"
          border
          style="width: 100%"
          @row-dblclick="openPreview"
      >
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="taskName" label="任务名称" min-width="130" />
        <el-table-column prop="defectType" label="缺陷类型" min-width="120" />
        <el-table-column prop="distance" label="距离原点位置" min-width="120" />
        <el-table-column label="缺陷图片" min-width="120">
          <template #default="{ row }">
            <div class="image-container">
              <el-image
                  style="width: 80px; height: 60px; cursor: pointer; border-radius: 4px"
                  :src="row.thumbnailUrl"
                  :preview-src-list="row.previewUrls"
                  fit="cover"
                  @error="handleImageError"
                  @click="handlePreview(row)"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                    <span>加载失败</span>
                  </div>
                </template>
                <template #placeholder>
                  <div class="image-placeholder">
                    <el-icon><Loading /></el-icon>
                  </div>
                </template>
              </el-image>
              <span v-if="row.imageCount > 1" class="image-badge">+{{ row.imageCount - 1 }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="是否属实" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.isReal ? 'success' : 'danger'">
              {{ row.isReal ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="严重程度" min-width="100" />
        <el-table-column prop="length" label="缺陷长度" min-width="100" />
        <el-table-column prop="area" label="缺陷面积" min-width="100" />
        <el-table-column prop="count" label="缺陷数量" min-width="100" />
        <el-table-column
            prop="suggestion"
            label="整改建议"
            min-width="140"
            show-overflow-tooltip
        />
        <el-table-column prop="reportTime" label="缺陷上报时间" min-width="160" />
        <!-- 状态列 -->
        <el-table-column prop="statusLabel" label="状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">
              {{ row.statusLabel }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="mt-4 flex justify-end items-center">
        <el-pagination
            size="small"
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
            :page-size="pagination.pageSize"
            :current-page="pagination.page"
            @update:page="handlePageChange"
            @update:page-size="handleSizeChange"
            :page-sizes="[5, 10, 20, 50]"
        />
        <span class="ml-4 text-sm text-gray-500">
          共 {{ totalPages }} 页，每页 {{ pagination.pageSize }} 条
        </span>
      </div>
    </el-card>

    <!-- 缺陷详情对话框 -->
    <el-dialog v-model="previewVisible" title="缺陷详情" width="600px">
      <DefectDetail :defect="current" />
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览组件 -->
    <el-dialog
        v-model="viewerVisible"
        width="auto"
        top="5vh"
        custom-class="image-viewer-dialog"
        :show-close="false"
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
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { getDefectList, exportDefects, DefectStatusEnum } from '@/api/defect'
import { ElMessage } from 'element-plus'
import DefectDetail from '@/components/defect/DefectDetail.vue'
import { Picture, Loading } from '@element-plus/icons-vue'

const title = '缺陷管理'

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
  task: '',
  type: '',
  reality: ''
})

// 表格数据与加载状态
const loading = ref(false)
const tableData = ref<any[]>([])

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 10,
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
    const params = {
      current: pagination.page,
      size: pagination.pageSize,
      task: query.task || undefined,
      type: query.type || undefined,
      reality: query.reality || undefined
    }

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

      return {
        ...item,
        taskName: item.taskname || '无任务名称',
        defectType: defectTypeOptions.find((v) => v.value === item.type)?.label || '未知类型',
        distance: item.location ?? '未知位置',
        thumbnailUrl: firstImage,
        previewUrls,
        imageCount: images.length,
        isReal: item.status === DefectStatusEnum.CONFIRMED,
        level: severityLabelMap[item.severity] || '未知',
        length: item.defectLength ?? '未知',
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

function handleSearch() {
  pagination.page = 1
  fetchData()
}

function handleReset() {
  Object.assign(query, { task: '', type: '', reality: '' })
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

async function handleExport() {
  try {
    await exportDefects({
      task: query.task,
      type: query.type,
      reality: query.reality
    })
    ElMessage.success('开始导出，请稍候查看下载列表')
  } catch {
    ElMessage.error('导出失败')
  }
}

function handleImageError(e: any) {
  console.error('图片加载失败:', e)
}

function handlePreview(row: any) {
  if (row.previewUrls?.length) {
    viewerUrls.value = row.previewUrls
    viewerIndex.value = 0
    viewerVisible.value = true
  }
}

// 下载图片
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
</style>