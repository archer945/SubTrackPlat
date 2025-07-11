<template>
  <div class="inspect-data-container">
    <div class="data-row">
      <div class="data-card hover-effect">
        <div class="value-container">
          <div class="value">{{ formatNumber(inspectData.todayCount) }}</div>
          <div class="glow-effect"></div>
        </div>
        <div class="label">今日巡视数</div>
      </div>
      <div class="data-card hover-effect">
        <div class="value-container">
          <div class="value">{{ formatNumber(inspectData.yesterdayCount) }}</div>
          <div class="glow-effect"></div>
        </div>
        <div class="label">昨日巡视数</div>
      </div>
      <div class="data-card hover-effect" :class="{ 'positive': isPositive(inspectData.growthRate), 'negative': !isPositive(inspectData.growthRate) }">
        <div class="value-container">
          <div class="value">{{ formatGrowthRate(inspectData.growthRate) }}</div>
          <div class="glow-effect"></div>
        </div>
        <div class="label">环比增长</div>
      </div>
    </div>
    
    <div class="data-row">
      <div class="data-card hover-effect">
        <div class="value-container">
          <div class="value">{{ formatDistance(inspectData.todayDistance) }}</div>
          <div class="glow-effect"></div>
        </div>
        <div class="label">今日巡视距离</div>
      </div>
      <div class="data-card hover-effect">
        <div class="value-container">
          <div class="value">{{ formatDistance(inspectData.yesterdayDistance) }}</div>
          <div class="glow-effect"></div>
        </div>
        <div class="label">昨日巡视距离</div>
      </div>
      <div class="data-card hover-effect" :class="{ 'positive': isPositive(inspectData.distanceGrowthRate), 'negative': !isPositive(inspectData.distanceGrowthRate) }">
        <div class="value-container">
          <div class="value">{{ formatGrowthRate(inspectData.distanceGrowthRate) }}</div>
          <div class="glow-effect"></div>
        </div>
        <div class="label">环比增长</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { getInspectSummary } from '../../api/visualization';

const inspectData = reactive({
  todayCount: 0,
  yesterdayCount: 0,
  growthRate: 0,
  todayDistance: 0,
  yesterdayDistance: 0,
  distanceGrowthRate: 0
});

onMounted(async () => {
  try {
    const response = await getInspectSummary();
    if (response && response.code === 10000 && response.data) {
      Object.assign(inspectData, response.data);
      animateNumbers();
    }
  } catch (error) {
    console.error('获取巡视数据失败:', error);
  }
});

const formatNumber = (num) => {
  if (num === null || num === undefined) return '0';
  return num.toString();
};

const formatDistance = (distance) => {
  if (distance === null || distance === undefined) return '0km';
  return `${distance}km`;
};

const formatGrowthRate = (rate) => {
  if (rate === null || rate === undefined) return '0%';
  return `${rate >= 0 ? '+' : ''}${rate}%`;
};

const isPositive = (value) => {
  return value >= 0;
};

// 添加数字变化动画效果
const animateNumbers = () => {
  // 这里可以实现数字动画效果，如果需要更复杂的动画
  // 可以使用如CountUp.js等库
};
</script>

<style scoped>
.inspect-data-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 0.75rem;
}

.data-row {
  display: flex;
  justify-content: space-between;
  width: 100%;
  gap: 0.75rem;
}

.data-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(30, 41, 84, 0.3);
  border-radius: 8px;
  padding: 0.75rem;
  border: 1px solid rgba(59, 130, 246, 0.1);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.value-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

.glow-effect {
  position: absolute;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(100, 181, 246, 0.4) 0%, rgba(100, 181, 246, 0) 70%);
  opacity: 0;
  transform: scale(0.5);
  z-index: -1;
}

.value {
  font-family: 'FX-LED', sans-serif;
  font-size: 2rem;
  font-weight: bold;
  color: #fff;
  margin-bottom: 0.25rem;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.label {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
}

.hover-effect:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  border-color: rgba(59, 130, 246, 0.3);
}

.hover-effect:hover .value {
  text-shadow: 0 0 15px rgba(255, 255, 255, 0.8);
  transform: scale(1.05);
}

.hover-effect:hover .glow-effect {
  animation: pulse 1.5s infinite;
  opacity: 1;
}

.positive {
  background: linear-gradient(to bottom, rgba(34, 197, 94, 0.1), rgba(30, 41, 84, 0.3));
  border-bottom: 2px solid rgba(34, 197, 94, 0.5);
}

.positive:hover {
  box-shadow: 0 10px 25px rgba(34, 197, 94, 0.2);
}

.negative {
  background: linear-gradient(to bottom, rgba(239, 68, 68, 0.1), rgba(30, 41, 84, 0.3));
  border-bottom: 2px solid rgba(239, 68, 68, 0.5);
}

.negative:hover {
  box-shadow: 0 10px 25px rgba(239, 68, 68, 0.2);
}

/* 数据卡片进入动画 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0% {
    opacity: 0.6;
    transform: scale(0.8);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.2);
  }
  100% {
    opacity: 0.6;
    transform: scale(0.8);
  }
}

.data-card {
  animation: slideInUp 0.5s ease forwards;
  animation-delay: calc(var(--index, 0) * 0.1s);
}

/* 为每个卡片设置不同的动画延迟 */
.data-row:first-child .data-card:nth-child(1) {
  --index: 1;
}

.data-row:first-child .data-card:nth-child(2) {
  --index: 2;
}

.data-row:first-child .data-card:nth-child(3) {
  --index: 3;
}

.data-row:last-child .data-card:nth-child(1) {
  --index: 4;
}

.data-row:last-child .data-card:nth-child(2) {
  --index: 5;
}

.data-row:last-child .data-card:nth-child(3) {
  --index: 6;
}
</style>
