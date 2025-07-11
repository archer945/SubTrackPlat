<template>
  <div class="bg-main min-h-screen w-full text-white flex flex-col">
    <!-- 顶部标题栏 -->
    <div class="h-16 flex items-center justify-between px-6 border-b border-blue-400/30 bg-black/20 w-full">
      <div class="w-24 md:block hidden">
        <!-- 左侧logo -->
        <img src="@/assets/imgs/aespadirtywork.jpg" alt="Logo" class="h-12 w-12 rounded-full object-cover border-2 border-blue-500/50" />
      </div>
      <p class="text-lg md:text-3xl font-bold text-center text-white drop-shadow-[0_2px_3px_rgba(0,0,0,0.8)]">
        地铁隧道巡线大数据仿真和分析平台
      </p>
      <div>
        <button @click="navigateToTasks" class="bg-transparent border border-blue-400 rounded-md px-3 md:px-6 py-1 md:py-1.5 text-sm md:text-base text-white hover:bg-blue-900/30 hover:border-blue-300 transition-all duration-300 backdrop-blur-sm shadow-lg shadow-blue-500/20">
          进入系统
        </button>
      </div>
    </div>

    <!-- 桌面端布局 - 默认网格布局 -->
    <div class="hidden md:grid flex-1 grid-cols-12 gap-5 p-5 overflow-hidden">
      
      <!-- 左侧栏 -->
      <div class="col-span-3 flex flex-col gap-5">
        <!-- 巡视数据统计 -->
        <div class="card-container p-4 flex-1 cursor-pointer hover:scale-[1.02] transition-transform" @click="navigateToTasks">
          <h3 class="text-center mb-4 text-white font-semibold pb-2 border-b border-blue-500/30">巡视数据统计</h3>
          <!-- 使用InspectData组件显示巡视数据 -->
          <InspectData v-if="getInspectSummaryData" />
          
          <!-- 当数据未加载完成或加载失败时显示占位动画 -->
          <div v-else class="flex items-center justify-center h-full">
            <div class="text-white/80 flex flex-col items-center">
              <div class="w-16 h-16 flex items-center justify-center mb-2">
                <div class="animate-ping absolute inline-flex h-10 w-10 rounded-full bg-blue-400 opacity-20"></div>
                <div class="relative inline-flex rounded-full h-8 w-8 bg-blue-500/50"></div>
              </div>
              <p>数据加载中...</p>
              <p v-if="loadError" class="text-red-400 text-sm mt-1">{{ loadError }}</p>
            </div>
          </div>
        </div>
        
        <!-- 缺陷类型统计 -->
        <div class="card-container p-4 flex-1 cursor-pointer hover:scale-[1.02] transition-transform" @click="navigateToDefects">
          <h3 class="text-center mb-2 text-white font-semibold pb-1 border-b border-blue-500/30">缺陷类型统计</h3>
          
          <!-- 使用DefectPieChart组件，当数据加载完成后显示 -->
          <div v-if="defectStatsData" class="h-[calc(100%-2.5rem)]">
            <DefectPieChart :data="defectStatsData" />
          </div>
          
          <!-- 当数据未加载完成或加载失败时显示占位动画 -->
          <div v-else class="flex items-center justify-center h-full">
            <div class="text-white/80 flex flex-col items-center">
              <div class="w-16 h-16 flex items-center justify-center mb-2">
                <div class="animate-ping absolute inline-flex h-10 w-10 rounded-full bg-blue-400 opacity-20"></div>
                <div class="relative inline-flex rounded-full h-8 w-8 bg-blue-500/50"></div>
              </div>
              <p>数据加载中...</p>
              <p v-if="defectStatsError" class="text-red-400 text-sm mt-1">{{ defectStatsError }}</p>
            </div>
          </div>
        </div>
        
        <!-- 人员数据统计 -->
        <div class="card-container p-4 flex-1">
          <h3 class="text-center mb-4 text-white font-semibold pb-2 border-b border-blue-500/30">人员数据统计</h3>
          
          <!-- 使用UserHorizontalBar组件，当数据加载完成后显示 -->
          <div v-if="getInspectSummaryData && getInspectSummaryData.executorDistribution" class="h-full">
            <UserHorizontalBar 
              :data="getInspectSummaryData"
              class="h-full"
            />
          </div>
          
          <!-- 当数据未加载完成或加载失败时显示占位动画 -->
          <div v-else class="flex items-center justify-center h-full">
            <div class="text-white/80 flex flex-col items-center">
              <div class="w-16 h-16 flex items-center justify-center mb-2">
                <div class="animate-pulse relative inline-flex h-12 w-3 rounded-full bg-blue-500/50 mx-0.5"></div>
                <div class="animate-pulse relative inline-flex h-8 w-3 rounded-full bg-blue-500/50 mx-0.5"></div>
                <div class="animate-pulse relative inline-flex h-14 w-3 rounded-full bg-blue-500/50 mx-0.5"></div>
                <div class="animate-pulse relative inline-flex h-10 w-3 rounded-full bg-blue-500/50 mx-0.5"></div>
              </div>
              <p>数据加载中...</p>
              <p v-if="loadError" class="text-red-400 text-sm mt-1">{{ loadError }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 中间区域 -->
      <div class="col-span-6 flex flex-col gap-5">
        <!-- 巡视总览数据 -->
        <div class="card-container p-4 h-[140px]">
          <!-- 使用TotalData组件显示巡视总览数据 -->
          <TotalData v-if="getInspectSummaryData" />
          
          <!-- 当数据未加载完成或加载失败时显示占位动画 -->
          <div v-else class="flex items-center justify-center h-full">
            <div class="text-white/80 flex flex-col items-center">
              <div class="w-16 h-16 flex items-center justify-center mb-2">
                <div class="animate-ping absolute inline-flex h-10 w-10 rounded-full bg-blue-400 opacity-20"></div>
                <div class="relative inline-flex rounded-full h-8 w-8 bg-blue-500/50"></div>
              </div>
              <p>数据加载中...</p>
              <p v-if="loadError" class="text-red-400 text-sm mt-1">{{ loadError }}</p>
            </div>
          </div>
        </div>
        
        <!-- 地图区域 -->
        <div class="card-container p-4 flex-1">
          <div class="mb-3 text-xs text-white/90 flex space-x-4">
            <div class="flex items-center"><span class="inline-block w-3 h-3 rounded-full bg-blue-500 mr-2"></span>图层1: 路线图</div>
            <div class="flex items-center"><span class="inline-block w-3 h-3 rounded-full bg-cyan-500 mr-2"></span>图层2: 地铁图</div>
            <div class="flex items-center"><span class="inline-block w-3 h-3 rounded-full bg-indigo-500 mr-2"></span>图层3: 地质分布</div>
          </div>
          <div class="h-5/6 relative overflow-hidden rounded-lg border border-blue-500/20">
            <!-- 字母站点地铁地图组件 -->
            <AlphabetMetroMap />
          </div>
        </div>
      </div>
      
      <!-- 右侧栏 -->
      <div class="col-span-3 flex flex-col gap-5">
        <!-- 缺陷数据统计 -->
        <div class="card-container p-4 flex-1 cursor-pointer hover:scale-[1.02] transition-transform" @click="navigateToDefects">
          <h3 class="text-center mb-4 text-white font-semibold pb-2 border-b border-blue-500/30">缺陷数据统计</h3>
          <!-- 使用DefectData组件，当数据加载完成后显示 -->
          <div v-if="defectStatsData" class="h-[calc(100%-3rem)]">
            <DefectData :data="defectStatsData" />
          </div>
          
          <!-- 当数据未加载完成或加载失败时显示占位动画 -->
          <div v-else class="flex items-center justify-center h-full">
            <div class="text-white/80 flex flex-col items-center">
              <div class="w-16 h-16 flex items-center justify-center mb-2">
                <div class="animate-ping absolute inline-flex h-10 w-10 rounded-full bg-blue-400 opacity-20"></div>
                <div class="relative inline-flex rounded-full h-8 w-8 bg-blue-500/50"></div>
              </div>
              <p>数据加载中...</p>
              <p v-if="defectStatsError" class="text-red-400 text-sm mt-1">{{ defectStatsError }}</p>
            </div>
          </div>
        </div>
        
        <!-- 每月巡检次数 -->
        <div class="card-container p-4 flex-1 cursor-pointer hover:scale-[1.02] transition-transform" @click="navigateToTasks">
          <h3 class="text-center mb-4 text-white font-semibold pb-2 border-b border-blue-500/30">每月巡检次数</h3>
          <div class="h-[calc(100%-3rem)]">
            <InspectLineChart v-if="getInspectSummaryData" :data="getInspectSummaryData" />
            <div v-else class="flex items-center justify-center h-full">
              <div class="text-white/80 flex flex-col items-center">
                <div class="w-24 h-16 flex items-center justify-end mb-2">
                  <div class="animate-pulse relative inline-flex h-16 w-4 rounded-t-lg bg-blue-500/50 mx-1"></div>
                  <div class="animate-pulse relative inline-flex h-12 w-4 rounded-t-lg bg-blue-500/50 mx-1"></div>
                  <div class="animate-pulse relative inline-flex h-14 w-4 rounded-t-lg bg-blue-500/50 mx-1"></div>
                  <div class="animate-pulse relative inline-flex h-10 w-4 rounded-t-lg bg-blue-500/50 mx-1"></div>
                </div>
                <p>数据加载中...</p>
                <p v-if="loadError" class="text-red-400 text-sm mt-1">{{ loadError }}</p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 缺陷数据变化 -->
        <div class="card-container p-4 flex-1 cursor-pointer hover:scale-[1.02] transition-transform" @click="navigateToDefects">
          <h3 class="text-center mb-2 text-white font-semibold pb-1 border-b border-blue-500/30">缺陷数据变化</h3>
          
          <!-- 使用DefectLineChart组件，当数据加载完成后显示 -->
          <div v-if="defectStatsData" class="h-[calc(100%-2.5rem)]">
            <DefectLineChart :data="defectStatsData" />
          </div>
          
          <!-- 当数据未加载完成或加载失败时显示占位动画 -->
          <div v-else class="flex items-center justify-center h-full">
            <div class="text-white/80 flex flex-col items-center">
              <div class="relative w-32 h-16 mb-2">
                <svg class="w-full h-full" viewBox="0 0 100 50">
                  <path d="M0,25 Q20,40 40,20 Q60,0 80,15 T100,25" fill="none" stroke="rgba(255, 255, 255, 0.5)" stroke-width="2" class="animate-pulse"></path>
                  <circle cx="0" cy="25" r="2" fill="#ffffff" class="animate-ping"></circle>
                  <circle cx="40" cy="20" r="2" fill="#ffffff" class="animate-ping"></circle>
                  <circle cx="100" cy="25" r="2" fill="#ffffff" class="animate-ping"></circle>
                </svg>
              </div>
              <p>数据加载中...</p>
              <p v-if="defectStatsError" class="text-red-400 text-sm mt-1">{{ defectStatsError }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 移动端布局 - 垂直排列所有组件 -->
    <div class="md:hidden flex flex-col gap-3 p-3 overflow-y-auto">
      <!-- 移动端头部Logo -->
      <div class="flex items-center justify-center mb-2">
        <img src="@/assets/imgs/aespadirtywork.jpg" alt="Logo" class="h-12 w-12 rounded-full object-cover border-2 border-blue-500/50" />
      </div>
      
      <!-- 巡视总览数据 -->
      <div class="card-container p-3">
        <h3 class="text-sm font-bold mb-2 text-center text-white">巡视总览数据</h3>
        <TotalData v-if="getInspectSummaryData" />
        <div v-else class="flex items-center justify-center h-20">
          <div class="text-white/80 text-xs">
            <div class="animate-pulse">数据加载中...</div>
          </div>
        </div>
      </div>
      
      <!-- 地图区域 - 最重要的内容放前面 -->
      <div class="card-container p-3">
        <h3 class="text-sm font-bold mb-2 text-center text-white">地铁隧道巡线地图</h3>
        <div class="h-[50vh] relative overflow-hidden rounded-lg border border-blue-500/20">
          <!-- 字母站点地铁地图组件 -->
          <AlphabetMetroMap />
        </div>
      </div>
      
      <!-- 巡视数据统计 -->
      <div class="card-container p-3 cursor-pointer hover:scale-[1.02] transition-transform" @click="navigateToTasks">
        <h3 class="text-sm font-bold mb-2 text-center text-white">巡视数据统计</h3>
        <InspectData v-if="getInspectSummaryData" />
        <div v-else class="flex items-center justify-center h-20">
          <div class="text-white/80 text-xs">
            <div class="animate-pulse">数据加载中...</div>
          </div>
        </div>
      </div>
      
      <!-- 缺陷类型统计 -->
      <div class="card-container p-3 cursor-pointer hover:scale-[1.02] transition-transform" @click="navigateToDefects">
        <h3 class="text-sm font-bold mb-2 text-center text-white">缺陷类型统计</h3>
        <div v-if="defectStatsData" class="h-64">
          <DefectPieChart :data="defectStatsData" />
        </div>
        <div v-else class="flex items-center justify-center h-20">
          <div class="text-white/80 text-xs">
            <div class="animate-pulse">数据加载中...</div>
          </div>
        </div>
      </div>
      
      <!-- 人员数据统计 -->
      <div class="card-container p-3">
        <h3 class="text-sm font-bold mb-2 text-center text-white">人员数据统计</h3>
        <div v-if="getInspectSummaryData && getInspectSummaryData.executorDistribution" class="h-64">
          <UserHorizontalBar 
            :data="getInspectSummaryData"
            class="h-full"
          />
        </div>
        <div v-else class="flex items-center justify-center h-20">
          <div class="text-white/80 text-xs">
            <div class="animate-pulse">数据加载中...</div>
          </div>
        </div>
      </div>
      
      <!-- 缺陷数据统计 -->
      <div class="card-container p-3 cursor-pointer hover:scale-[1.02] transition-transform" @click="navigateToDefects">
        <h3 class="text-sm font-bold mb-2 text-center text-white">缺陷数据统计</h3>
        <div v-if="defectStatsData" class="h-64">
          <DefectData :data="defectStatsData" />
        </div>
        <div v-else class="flex items-center justify-center h-20">
          <div class="text-white/80 text-xs">
            <div class="animate-pulse">数据加载中...</div>
          </div>
        </div>
      </div>
      
      <!-- 每月巡检次数 -->
      <div class="card-container p-3 cursor-pointer hover:scale-[1.02] transition-transform" @click="navigateToTasks">
        <h3 class="text-sm font-bold mb-2 text-center text-white">每月巡检次数</h3>
        <div class="h-64">
          <InspectLineChart v-if="getInspectSummaryData" :data="getInspectSummaryData" />
          <div v-else class="flex items-center justify-center h-20">
            <div class="text-white/80 text-xs">
              <div class="animate-pulse">数据加载中...</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 缺陷数据变化 -->
      <div class="card-container p-3 cursor-pointer hover:scale-[1.02] transition-transform" @click="navigateToDefects">
        <h3 class="text-sm font-bold mb-2 text-center text-white">缺陷数据变化</h3>
        <div v-if="defectStatsData" class="h-64">
          <DefectLineChart :data="defectStatsData" />
        </div>
        <div v-else class="flex items-center justify-center h-20">
          <div class="text-white/80 text-xs">
            <div class="animate-pulse">数据加载中...</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getDefectOverview, getDefectStats, getInspectSummary, getInspectTrend, getInspectionTasks } from '@/api/visualization'
import UserHorizontalBar from '@/components/charts/UserHorizontalBar.vue'
import InspectLineChart from '@/components/charts/InspectLineChart.vue'
import DefectPieChart from '@/components/charts/DefectPieChart.vue'
import DefectLineChart from '@/components/charts/DefectLineChart.vue'
import DefectData from '@/components/charts/DefectData.vue'
import InspectData from '@/components/charts/InspectData.vue'
import TotalData from '@/components/charts/TotalData.vue'
import MetroMap3D from '@/components/charts/MetroMap3D.vue'
import InspectionInfoCard from '@/components/charts/InspectionInfoCard.vue'
import AlphabetMetroMap from '@/components/charts/AlphabetMetroMap.vue'

// 获取路由实例
const router = useRouter()

// 数据状态
const getInspectSummaryData = ref(null)
const loadError = ref(null)
const isLoading = ref(false)

// 缺陷统计数据
const defectStatsData = ref(null)
const defectStatsError = ref(null)
const isLoadingDefectStats = ref(false)

// 巡视任务数据
const inspectionTasks = ref([]);
const activeInspection = ref(null);
const isLoadingInspectionTasks = ref(false);
const inspectionTasksError = ref(null);

// 加载数据函数
const loadData = async () => {
    if (isLoading.value) return
    
    isLoading.value = true
    loadError.value = null
    
    try {
        console.log('开始请求数据，时间:', new Date().toLocaleTimeString())
        const response = await getInspectSummary()
        console.log('API原始响应:', response)
        
        // 检查返回的数据结构
        if (response && response.data && response.data.executorDistribution) {
            console.log('数据结构正确，包含executorDistribution')
            getInspectSummaryData.value = response.data
        } else if (response && response.code === 10000 && response.data) {
            // 如果是标准接口返回格式 {code: 10000, message: "操作成功", data: {...}}
            console.log('标准接口返回格式，提取data字段')
            getInspectSummaryData.value = response.data
        } else {
            console.error('数据结构不符合预期:', response)
            loadError.value = '数据格式错误'
        }
    } catch (error) {
        console.error('数据加载失败:', error)
        loadError.value = `加载失败: ${error.message}`
    } finally {
        isLoading.value = false
    }
}

// 加载缺陷统计数据函数
const loadDefectStatsData = async () => {
    if (isLoadingDefectStats.value) return
    
    isLoadingDefectStats.value = true
    defectStatsError.value = null
    
    try {
        console.log('开始请求缺陷统计数据，时间:', new Date().toLocaleTimeString())
        const response = await getDefectStats()
        console.log('缺陷统计API响应:', response)
        
        // 检查返回的数据结构
        if (response && response.code === 10000 && response.data) {
            console.log('缺陷统计数据获取成功')
            defectStatsData.value = response.data
        } else {
            console.error('缺陷统计数据结构不符合预期:', response)
            defectStatsError.value = '数据格式错误'
        }
    } catch (error) {
        console.error('缺陷统计数据加载失败:', error)
        defectStatsError.value = `加载失败: ${error.message}`
    } finally {
        isLoadingDefectStats.value = false
    }
}

// 加载巡视任务数据
const loadInspectionTasks = async () => {
    if (isLoadingInspectionTasks.value) return;
    
    isLoadingInspectionTasks.value = true;
    inspectionTasksError.value = null;
    
    try {
        console.log('开始请求巡视任务数据，时间:', new Date().toLocaleTimeString());
        const response = await getInspectionTasks();
        console.log('巡视任务API响应:', response);
        
        // 检查返回的数据结构
        if (response && response.code === 10000 && response.data && response.data.tasks) {
            console.log('巡视任务数据获取成功');
            inspectionTasks.value = response.data.tasks;
            
            // 选择一个进行中的任务作为活动任务，如果没有进行中的，就选择第一个
            const inProgressTask = inspectionTasks.value.find(task => task.status === '进行中');
            activeInspection.value = inProgressTask || inspectionTasks.value[0] || null;
        } else {
            console.error('巡视任务数据结构不符合预期:', response);
            inspectionTasksError.value = '数据格式错误';
        }
    } catch (error) {
        console.error('巡视任务数据加载失败:', error);
        inspectionTasksError.value = `加载失败: ${error.message}`;
    } finally {
        isLoadingInspectionTasks.value = false;
    }
}

// 切换巡视路线
const switchInspectionRoute = () => {
    if (inspectionTasks.value.length === 0) return;
    
    // 找到当前活动任务的索引
    const currentIndex = inspectionTasks.value.findIndex(task => 
        task.id === (activeInspection.value ? activeInspection.value.id : null));
    
    // 选择下一个任务，如果已经是最后一个则选择第一个
    const nextIndex = (currentIndex + 1) % inspectionTasks.value.length;
    activeInspection.value = inspectionTasks.value[nextIndex];
    
    console.log('切换巡视路线:', activeInspection.value);
}

// 导航到任务列表
const navigateToTasks = () => {
    router.push('/tasks')
}

// 导航到缺陷列表
const navigateToDefects = () => {
    router.push('/defects')
}

// 组件挂载时初始化
onMounted(() => {
    // 加载巡检数据
    loadData()
    
    // 加载缺陷统计数据
    loadDefectStatsData()
    
    // 加载巡视任务数据
    loadInspectionTasks()
    
    // 每五秒获取新数据
    const inspectTimer = setInterval(() => {
        loadData()
    }, 5000) // 5秒刷新一次
    
    // 每10秒获取新的缺陷统计数据
    const defectTimer = setInterval(() => {
        loadDefectStatsData()
    }, 10000) // 10秒刷新一次
    
    // 每20秒切换一次巡视路线
    const routeTimer = setInterval(() => {
        switchInspectionRoute()
    }, 20000) // 20秒切换一次
    
    // 组件卸载时清除定时器
    return () => {
        clearInterval(inspectTimer)
        clearInterval(defectTimer)
        clearInterval(routeTimer)
    }
})
</script>

<style>
/* 全局样式 */
body {
  margin: 0;
  font-family: 'Arial', sans-serif;
  overflow: hidden;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
}

::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

/* 加入一些动画 */
@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
  100% { transform: translateY(0px); }
}

.float {
  animation: float 5s ease-in-out infinite;
}

/* 背景图样式 */
.bg-main {
  background-image: url('@/assets/imgs/background.gif');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  filter: none; /* 确保背景图没有模糊效果 */
}

/* 卡片容器样式 */
.card-container {
  background-color: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  border-radius: 0.5rem;
  border: 1px solid rgba(59, 130, 246, 0.2);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  transition: all 0.5s;
}

.card-container:hover {
  box-shadow: 0 20px 25px -5px rgba(59, 130, 246, 0.1), 0 10px 10px -5px rgba(59, 130, 246, 0.04);
}

/* 移动端样式优化 */
@media (max-width: 768px) {
  /* 修复滚动问题 */
  body {
    overflow: auto;
  }
  
  /* 确保内容不被截断 */
  .bg-main {
    min-height: 100vh;
    height: auto;
  }
  
  /* 增强卡片对比度 */
  .card-container {
    background-color: rgba(0, 0, 0, 0.6);
    backdrop-filter: blur(10px);
  }
  
  /* 优化滚动体验 */
  .overflow-y-auto {
    -webkit-overflow-scrolling: touch;
    scroll-behavior: smooth;
  }
  
  /* 图表容器高度修复 */
  .h-64 {
    height: 250px;
  }
  
  /* 增强移动端排版 */
  h3 {
    font-size: 1rem;
    margin-bottom: 0.5rem;
  }
  
  /* 确保地图组件可见 */
  .h-\[50vh\] {
    height: 50vh !important;
    min-height: 300px;
  }
}
</style>

