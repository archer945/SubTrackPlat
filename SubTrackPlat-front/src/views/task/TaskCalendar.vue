<template>
  <div>
    <!-- 顶部导航栏 -->
    <div class="calendar-toolbar">
      <!-- 显示当前月份 -->
      <div class="toolbar-left">
        {{ currentMonth }}
      </div>

      <!-- 视图切换 + 跳转按钮 -->
      <div class="toolbar-right">
        <el-select v-model="calendarView" style="width: 120px;" @change="changeView">
          <el-option label="月视图" value="dayGridMonth" />
          <el-option label="周视图" value="timeGridWeek" />
          <el-option label="日视图" value="timeGridDay" />
        </el-select>
        <el-button @click="handlePrev">{{ prevLabel }}</el-button>
        <el-button @click="handleToday">今天</el-button>
        <el-button @click="handleNext">{{ nextLabel }}</el-button>
      </div>
    </div>

    <!-- 日历主体 -->
    <FullCalendar
      ref="calendarRef"
      :options="calendarOptions"
      class="calendar"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import { DateTime } from 'luxon'
import { ElMessageBox } from 'element-plus'

// 基础状态
const router = useRouter()
const calendarRef = ref(null)
const currentMonth = ref('')
const calendarView = ref('dayGridMonth')

// 动态按钮文字
const prevLabel = computed(() => {
  switch (calendarView.value) {
    case 'dayGridMonth': return '上月'
    case 'timeGridWeek': return '上周'
    case 'timeGridDay': return '上一天'
    default: return '上'
  }
})
const nextLabel = computed(() => {
  switch (calendarView.value) {
    case 'dayGridMonth': return '下月'
    case 'timeGridWeek': return '下周'
    case 'timeGridDay': return '下一天'
    default: return '下'
  }
})

// 颜色映射 class
function getEventClassNames(arg) {
  const status = arg.event.extendedProps.status || 'default'
  return [`task-status-${status}`]
}

// 日历配置项
const calendarOptions = ref({
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView: calendarView.value,
  locale: 'zh-cn',
  headerToolbar: false,
  events: [
    {
      title: '隧道巡检-001',
      date: '2025-06-02',
      extendedProps: { id: 1, description: '例行巡检任务', status: '进行中' }
    },
    {
      title: '隧道巡检-002',
      date: '2025-06-03',
      extendedProps: { id: 2, description: '例行巡检任务', status: '已完成' }
    },
    {
      title: '设备检查',
      date: '2025-06-03',
      extendedProps: { id: 3, description: '设备状态检查', status: '待执行' }
    },
    {
      title: '定期维护',
      date: '2025-06-09',
      extendedProps: { id: 4, description: '月度设备维护', status: '已暂停' }
    }
  ],
  eventClick: handleEventClick,
  eventClassNames: getEventClassNames
})

// 跳转导航
function handlePrev() {
  calendarRef.value.getApi().prev()
  updateCurrentMonth()
}
function handleNext() {
  calendarRef.value.getApi().next()
  updateCurrentMonth()
}
function handleToday() {
  calendarRef.value.getApi().today()
  updateCurrentMonth()
}
function changeView(view) {
  calendarRef.value.getApi().changeView(view)
  updateCurrentMonth()
}

// 事件跳转
function handleEventClick(info) {
  const { id } = info.event.extendedProps
  if (id) {
    router.push(`/tasks/${id}`)
  } else {
    ElMessageBox.alert('此任务缺少 ID，无法跳转', '无法跳转', {
      confirmButtonText: '确定',
      type: 'warning'
    })
  }
}

// 显示当前月份
function updateCurrentMonth() {
  const date = calendarRef.value.getApi().getDate()
  currentMonth.value = DateTime.fromJSDate(date).toFormat('yyyy年MM月')
}

onMounted(updateCurrentMonth)
</script>

<style scoped>
.calendar-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  background: #f9f9f9;
  padding: 12px 20px;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.toolbar-left {
  font-size: 18px;
  font-weight: bold;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.calendar {
  background: white;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

/* 状态颜色样式 */
.task-status-进行中 {
  background-color: #cce5ff !important;
  border-color: #66b1ff !important;
}
.task-status-已完成 {
  background-color: #d4edda !important;
  border-color: #67c23a !important;
}
.task-status-待执行 {
  background-color: #fff3cd !important;
  border-color: #e6a23c !important;
}
.task-status-已暂停 {
  background-color: #f8d7da !important;
  border-color: #f56c6c !important;
}
</style>
