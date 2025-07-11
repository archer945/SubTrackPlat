<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { getDefectDetail } from '@/api/defect/defect'
import { ElMessage } from 'element-plus'

const props = defineProps<{ defect?: any; id?: string | number }>()

const loading = ref(false)
const detail = ref<any | null>(props.defect ?? null)


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
 * 严重程度中文映射
 */
const severityLabelMap: Record<string, string> = {
  LOW: '低',
  MIDDLE: '中',
  MEDIUM: '中',
  HIGH: '高',
  CRITICAL: '严重'
}

function normalize(src: any) {
  if (!src) return null
  return {
    ...src,
    defectType: defectTypeOptions.find((v) => v.value === src.type)?.label || '未知类型',
    taskName: src.taskName ?? src.title,
    distance: src.location,
    level: severityLabelMap[src.severity] || '未知',
    defectLength: src.defectLength,
    area: src.defectArea,
    count: src.defectCount,
    suggestion: src.suggestion,
    images: src.images ?? (src.imageUrl ? [{ imageUrl: src.imageUrl, thumbnailUrl: src.thumbnailUrl }] : []),
  }
}

async function fetchDetail(id: string | number) {
  try {
    loading.value = true
    const res = await getDefectDetail(Number(id))
    const raw = res.data?.data ?? res.data
    detail.value = normalize(raw)
  } catch (err) {
    console.error(err)
    ElMessage.error('获取缺陷详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!detail.value && props.id) fetchDetail(props.id)
})

watch(
    () => props.defect,
    (v) => {
      if (v) detail.value = normalize(v)
    }
)

/* 图片查看 */
const viewerVisible = ref(false)
const viewerUrls = ref<string[]>([])
const viewerIndex = ref(0)
function openViewer(idx = 0) {
  viewerUrls.value = (detail.value?.images ?? []).map((i: any) => i.imageUrl)
  viewerIndex.value = idx
  if (viewerUrls.value.length) viewerVisible.value = true
}
function downloadImage(url: string) {
  if (!url) return
  const a = document.createElement('a')
  a.href = url
  a.download = url.split('/').pop() || 'image'
  a.click()
}
</script>

<template>
  <el-skeleton :loading="loading" animated>
    <template #template>
      <el-skeleton-item variant="image" style="width:100%;height:260px" />
      <el-skeleton-item variant="text" style="width:100%;height:180px" />
    </template>

    <template #default>
      <el-empty v-if="!detail" description="未找到缺陷详情" />

      <div v-else>
        <div v-if="detail.images?.length" class="mb-4 flex flex-wrap gap-2">
          <el-image v-for="(img,idx) in detail.images" :key="idx" :src="img.imageUrl" fit="cover" style="width:88px;height:66px;cursor:pointer;border-radius:4px" @click="openViewer(idx)">
            <template #error><div class="el-image__placeholder">暂无图片</div></template>
          </el-image>
        </div>
        <el-empty v-else description="暂无缺陷图片" class="mb-4" />

        <el-descriptions :column="2" border>
          <el-descriptions-item label="缺陷类型">{{ detail.defectType }}</el-descriptions-item>
          <el-descriptions-item label="所属任务ID">{{ detail.taskId }}</el-descriptions-item>
          <el-descriptions-item label="距离位置">{{ detail.distance ?? '未知位置' }}</el-descriptions-item>
          <el-descriptions-item label="严重程度">{{ detail.level ?? '未知' }}</el-descriptions-item>
          <el-descriptions-item label="缺陷长度">{{ detail.defectLength ?? '未知' }}</el-descriptions-item>
          <el-descriptions-item label="缺陷面积">{{ detail.area ?? '未知' }}</el-descriptions-item>
          <el-descriptions-item label="缺陷数量">{{ detail.count ?? '未知' }}</el-descriptions-item>
          <el-descriptions-item label="整改建议">{{ detail.suggestion ?? '未知' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </template>
  </el-skeleton>

  <el-dialog v-model="viewerVisible" width="80%" top="5vh" custom-class="image-viewer-dialog" :show-close="false">
    <el-image-viewer :url-list="viewerUrls" :initial-index="viewerIndex" class="flex-1" @close="viewerVisible=false" />
    <!-- <div class="absolute top-4 right-4 z-50"><el-button size="small" type="primary" @click="downloadImage(viewerUrls[viewerIndex])">下载图片</el-button></div> -->
  </el-dialog>
</template>

<style scoped>
.image-viewer-dialog .el-dialog__body{padding:0;height:80vh}
.el-image__placeholder{display:flex;justify-content:center;align-items:center;background:#f2f2f2;color:#999;font-size:14px}
</style>