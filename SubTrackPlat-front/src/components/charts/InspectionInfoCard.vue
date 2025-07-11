<template>
  <div class="absolute right-4 bottom-4 z-10 inspection-card rounded-lg border border-cyan-500/30 shadow-2xl shadow-cyan-500/20 w-72 text-sm overflow-hidden">
    <div v-if="inspection" class="text-white">
      <div class="inspection-header py-3 px-4 flex items-center justify-between border-b border-cyan-500/30">
        <h3 class="font-bold text-cyan-300 tracking-wide flex items-center">
          <span class="pulse-dot mr-2"></span>
          巡视任务详情
        </h3>
        <span :class="getStatusClass" class="px-2 py-0.5 rounded-full text-xs font-bold flex items-center">
          <span class="status-indicator mr-1" :class="getStatusIndicatorClass"></span>
          {{ inspection.status }}
        </span>
      </div>
      
      <div class="inspection-body p-4 backdrop-blur-md relative z-10">
        <div class="flex flex-col space-y-3">
          <div class="grid grid-cols-5">
            <span class="col-span-2 text-gray-300">巡视路线：</span>
            <span class="col-span-3 font-medium text-right">{{ inspection.startStation }} → {{ inspection.endStation }}</span>
          </div>
          
          <div class="grid grid-cols-5">
            <span class="col-span-2 text-gray-300">执行人员：</span>
            <span class="col-span-3 font-medium text-right flex justify-end items-center">
              <span class="inspector-avatar mr-2" :style="getInspectorAvatarStyle">
                {{ getInspectorInitial }}
              </span>
              {{ inspection.inspector || '未分配' }}
            </span>
          </div>
          
          <div class="grid grid-cols-5">
            <span class="col-span-2 text-gray-300">开始时间：</span>
            <span class="col-span-3 font-medium text-right">{{ formatTime(inspection.startTime) }}</span>
          </div>
          
          <div class="grid grid-cols-5">
            <span class="col-span-2 text-gray-300">预计完成：</span>
            <span class="col-span-3 font-medium text-right">{{ formatTime(inspection.estimatedEndTime) }}</span>
          </div>
          
          <div v-if="inspection.progress !== undefined" class="mt-1">
            <div class="flex justify-between mb-2">
              <span class="text-gray-300">完成进度：</span>
              <span class="font-medium">{{ Math.round(inspection.progress * 100) }}%</span>
            </div>
            <div class="w-full bg-gray-800/60 rounded-full h-2 overflow-hidden relative">
              <div 
                class="h-2 rounded-full progress-bar relative" 
                :class="getProgressBarClass"
                :style="{ width: `${inspection.progress * 100}%` }"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="text-white/70 text-center py-8 backdrop-blur-md">
      <div class="flex flex-col items-center">
        <div class="w-12 h-12 rounded-full border-2 border-cyan-500/30 flex items-center justify-center mb-2">
          <i class="text-2xl text-cyan-500/50">?</i>
        </div>
        暂无巡视任务
      </div>
    </div>
    
    <!-- 背景装饰 -->
    <div class="absolute inset-0 bg-pattern opacity-5 z-0"></div>
    <div class="absolute top-0 right-0 w-32 h-32 bg-cyan-500/10 rounded-full blur-3xl -mr-10 -mt-10 z-0"></div>
    <div class="absolute bottom-0 left-0 w-24 h-24 bg-blue-500/10 rounded-full blur-2xl -ml-10 -mb-10 z-0"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  inspection: {
    type: Object,
    default: null
  }
});

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return '未设置';
  
  try {
    const date = new Date(timeString);
    return date.toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    });
  } catch (e) {
    return timeString;
  }
};

// 获取执行人员首字母
const getInspectorInitial = computed(() => {
  if (!props.inspection || !props.inspection.inspector) return '';
  return props.inspection.inspector.charAt(0);
});

// 生成执行人头像样式
const getInspectorAvatarStyle = computed(() => {
  if (!props.inspection || !props.inspection.inspector) return {};
  
  // 根据名字生成一个一致的颜色
  const str = props.inspection.inspector;
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  
  // 将哈希值转换为HSL色彩
  const h = hash % 360;
  return {
    backgroundColor: `hsl(${h}, 70%, 60%)`
  };
});

// 任务状态样式
const getStatusClass = computed(() => {
  if (!props.inspection) return '';
  
  switch (props.inspection.status) {
    case '进行中':
      return 'bg-green-900/30 text-green-400 border border-green-500/30';
    case '待开始':
      return 'bg-yellow-900/30 text-yellow-400 border border-yellow-500/30';
    case '已完成':
      return 'bg-blue-900/30 text-blue-400 border border-blue-500/30';
    default:
      return 'bg-gray-900/30 text-gray-400 border border-gray-500/30';
  }
});

// 状态指示器样式
const getStatusIndicatorClass = computed(() => {
  if (!props.inspection) return '';
  
  switch (props.inspection.status) {
    case '进行中':
      return 'bg-green-400 pulse-animation';
    case '待开始':
      return 'bg-yellow-400';
    case '已完成':
      return 'bg-blue-400';
    default:
      return 'bg-gray-400';
  }
});

// 进度条样式
const getProgressBarClass = computed(() => {
  if (!props.inspection) return '';
  
  const progress = props.inspection.progress || 0;
  
  if (progress >= 0.8) {
    return 'bg-gradient-to-r from-cyan-500 to-green-500';
  } else if (progress >= 0.4) {
    return 'bg-gradient-to-r from-blue-500 to-cyan-500';
  } else {
    return 'bg-gradient-to-r from-indigo-500 to-blue-500';
  }
});
</script>

<style scoped>
.inspection-card {
  background: rgba(0, 15, 30, 0.6);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.4);
}

.inspection-header {
  background: rgba(0, 20, 40, 0.6);
}

.inspector-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: bold;
  color: white;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #22d3ee;
  position: relative;
}

.pulse-dot::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  border-radius: 50%;
  background-color: rgba(34, 211, 238, 0.4);
  animation: pulse 2s infinite ease-out;
}

.pulse-animation {
  animation: pulse 2s infinite ease-out;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }
  50% {
    transform: scale(1.5);
    opacity: 0.4;
  }
  100% {
    transform: scale(1);
    opacity: 0.8;
  }
}

.bg-pattern {
  background-image: radial-gradient(rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 10px 10px;
}

.progress-bar {
  position: relative;
  overflow: hidden;
}

.progress-bar::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.3),
    transparent
  );
  animation: progress-shine 2s infinite linear;
}

@keyframes progress-shine {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}
</style> 