<template>
  <div class="total-data-container">
    <div class="total-card">
      <div class="data-content">
        <div class="value-wrapper">
          <div class="value-container">
            <span class="value odometer">{{ formatNumber(totalData.totalDistance) }}</span>
            <div class="unit">km</div>
          </div>
          <div class="glow-circle"></div>
          <div class="particle-container">
            <div v-for="n in 15" :key="n" class="particle"></div>
          </div>
        </div>
        <div class="label">巡视总距离</div>
        <div class="decoration-line"></div>
      </div>
      <div class="corner-decoration top-left"></div>
      <div class="corner-decoration top-right"></div>
      <div class="corner-decoration bottom-left"></div>
      <div class="corner-decoration bottom-right"></div>
      <div class="radar-circle"></div>
    </div>
    
    <div class="divider">
      <div class="divider-dot"></div>
      <div class="divider-dot"></div>
      <div class="divider-dot"></div>
    </div>
    
    <div class="total-card">
      <div class="data-content">
        <div class="value-wrapper">
          <div class="value-container">
            <span class="value odometer">{{ formatNumber(totalData.totalInspections) }}</span>
          </div>
          <div class="glow-circle"></div>
          <div class="particle-container">
            <div v-for="n in 15" :key="n" class="particle"></div>
          </div>
        </div>
        <div class="label">巡视总次数</div>
        <div class="decoration-line"></div>
      </div>
      <div class="corner-decoration top-left"></div>
      <div class="corner-decoration top-right"></div>
      <div class="corner-decoration bottom-left"></div>
      <div class="corner-decoration bottom-right"></div>
      <div class="radar-circle"></div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted, onUnmounted } from 'vue';
import { getInspectSummary } from '../../api/visualization';

const totalData = reactive({
  totalDistance: 0,
  totalInspections: 0
});

let animationInterval = null;

onMounted(async () => {
  try {
    const response = await getInspectSummary();
    if (response && response.code === 10000 && response.data) {
      // 先设置为0，然后通过动画增长到目标值
      setTimeout(() => {
        animateValue('totalDistance', response.data.totalDistance || 0);
        animateValue('totalInspections', response.data.totalInspections || 0);
      }, 500);
      
      // 启动粒子动画
      startParticleAnimation();
    }
  } catch (error) {
    console.error('获取巡视总览数据失败:', error);
  }
});

onUnmounted(() => {
  if (animationInterval) {
    clearInterval(animationInterval);
  }
});

// 格式化数字，添加千位分隔符
const formatNumber = (num) => {
  if (num === null || num === undefined) return '0';
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

// 数值动画效果
const animateValue = (property, targetValue) => {
  const duration = 2000; // 动画持续时间（毫秒）
  const steps = 60; // 动画步数
  const stepTime = duration / steps;
  let currentValue = 0;
  const increment = targetValue / steps;
  
  const timer = setInterval(() => {
    currentValue += increment;
    if (currentValue >= targetValue) {
      totalData[property] = targetValue;
      clearInterval(timer);
    } else {
      totalData[property] = Math.floor(currentValue);
    }
  }, stepTime);
};

// 粒子动画
const startParticleAnimation = () => {
  const particles = document.querySelectorAll('.particle');
  particles.forEach(particle => {
    animateParticle(particle);
  });
};

const animateParticle = (particle) => {
  // 随机设置粒子初始位置、大小和动画时间
  const size = Math.random() * 4 + 1;
  const startPosX = Math.random() * 100 - 50;
  const startPosY = Math.random() * 100 - 50;
  const duration = Math.random() * 3 + 2;
  const delay = Math.random() * 2;
  
  particle.style.width = `${size}px`;
  particle.style.height = `${size}px`;
  particle.style.left = `calc(50% + ${startPosX}px)`;
  particle.style.top = `calc(50% + ${startPosY}px)`;
  particle.style.animationDuration = `${duration}s`;
  particle.style.animationDelay = `${delay}s`;
  
  // 随机设置粒子颜色
  const hue = Math.random() * 60 + 180; // 蓝色到青色范围
  particle.style.backgroundColor = `hsla(${hue}, 100%, 70%, 0.8)`;
  
  // 重置动画
  particle.classList.remove('particle-animate');
  void particle.offsetWidth; // 触发重排
  particle.classList.add('particle-animate');
};
</script>

<style scoped>
.total-data-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1px;
  position: relative;
  perspective: 1000px;
}

.total-card {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  transform-style: preserve-3d;
  transform: translateZ(0);
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  background: rgba(13, 25, 42, 0.5);
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(0, 149, 255, 0.1) inset;
}

.total-card:hover {
  transform: translateZ(10px) scale(1.02);
  box-shadow: 
    0 0 30px rgba(0, 149, 255, 0.2) inset,
    0 0 15px rgba(0, 149, 255, 0.3);
}

.data-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
}

.value-wrapper {
  position: relative;
  margin-bottom: 0.5rem;
}

.value-container {
  display: flex;
  align-items: baseline;
  position: relative;
  z-index: 3;
}

.value {
  font-family: 'FX-LED', sans-serif;
  font-size: 2.5rem;
  font-weight: bold;
  color: #ffffff;
  text-shadow: 
    0 0 10px rgba(100, 181, 246, 0.7),
    0 0 20px rgba(100, 181, 246, 0.5),
    0 0 30px rgba(100, 181, 246, 0.3);
  transition: all 0.3s ease;
  margin-right: 4px;
  filter: drop-shadow(0 0 8px rgba(100, 181, 246, 0.8));
  -webkit-text-fill-color: #ffffff;
  background: none;
  -webkit-background-clip: initial;
}

.unit {
  font-size: 1rem;
  color: #ffffff;
  margin-left: 4px;
}

.label {
  font-size: 1rem;
  color: #ffffff;
  margin-top: 0.25rem;
  position: relative;
  z-index: 2;
  text-shadow: 0 0 5px rgba(0, 149, 255, 0.5);
}

.glow-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, rgba(59, 130, 246, 0) 70%);
  opacity: 0.6;
  filter: blur(8px);
  animation: pulse 3s infinite ease-in-out;
  z-index: 1;
}

.divider {
  width: 1px;
  height: 70%;
  background: linear-gradient(to bottom, rgba(59, 130, 246, 0), rgba(59, 130, 246, 0.6), rgba(59, 130, 246, 0));
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.divider-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background-color: rgba(59, 130, 246, 0.8);
  box-shadow: 0 0 5px rgba(59, 130, 246, 0.8);
  animation: glow 2s infinite alternate;
}

.decoration-line {
  width: 40%;
  height: 2px;
  background: linear-gradient(to right, rgba(59, 130, 246, 0), rgba(59, 130, 246, 0.6), rgba(59, 130, 246, 0));
  margin-top: 0.75rem;
  opacity: 0.8;
  position: relative;
  overflow: hidden;
}

.decoration-line::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(to right, rgba(59, 130, 246, 0), rgba(255, 255, 255, 0.8), rgba(59, 130, 246, 0));
  animation: shimmer 3s infinite;
}

.corner-decoration {
  position: absolute;
  width: 15px;
  height: 15px;
  border-color: rgba(59, 130, 246, 0.6);
  border-style: solid;
  border-width: 0;
  z-index: 1;
}

.top-left {
  top: 5px;
  left: 5px;
  border-top-width: 2px;
  border-left-width: 2px;
}

.top-right {
  top: 5px;
  right: 5px;
  border-top-width: 2px;
  border-right-width: 2px;
}

.bottom-left {
  bottom: 5px;
  left: 5px;
  border-bottom-width: 2px;
  border-left-width: 2px;
}

.bottom-right {
  bottom: 5px;
  right: 5px;
  border-bottom-width: 2px;
  border-right-width: 2px;
}

.radar-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 150px;
  height: 150px;
  border-radius: 50%;
  border: 1px solid rgba(59, 130, 246, 0.3);
  opacity: 0.5;
  z-index: 0;
}

.radar-circle::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  border: 1px solid rgba(59, 130, 246, 0.3);
  animation: radar 3s infinite linear;
  transform-origin: center;
}

.particle-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
}

.particle {
  position: absolute;
  width: 2px;
  height: 2px;
  background-color: rgba(100, 181, 246, 0.8);
  border-radius: 50%;
  opacity: 0;
  z-index: 1;
}

.particle-animate {
  animation: float-particle 3s infinite ease-out;
}

/* 动画效果 */
@keyframes pulse {
  0% {
    opacity: 0.4;
    transform: translate(-50%, -50%) scale(0.8);
  }
  50% {
    opacity: 0.6;
    transform: translate(-50%, -50%) scale(1);
  }
  100% {
    opacity: 0.4;
    transform: translate(-50%, -50%) scale(0.8);
  }
}

@keyframes floatAnimation {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-5px);
  }
  100% {
    transform: translateY(0px);
  }
}

@keyframes glow {
  0% {
    opacity: 0.5;
    box-shadow: 0 0 5px rgba(59, 130, 246, 0.5);
  }
  100% {
    opacity: 1;
    box-shadow: 0 0 10px rgba(59, 130, 246, 1);
  }
}

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

@keyframes radar {
  0% {
    transform: scale(0.3);
    opacity: 0.9;
  }
  100% {
    transform: scale(1.2);
    opacity: 0;
  }
}

@keyframes float-particle {
  0% {
    transform: translate(0, 0) scale(1);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 0;
  }
  100% {
    transform: translate(
      calc(var(--x, 1) * 50px),
      calc(var(--y, 1) * -50px)
    ) scale(0);
    opacity: 0;
  }
}

.total-card:hover .value {
  animation: floatAnimation 2s infinite ease-in-out;
  filter: drop-shadow(0 0 12px rgba(100, 181, 246, 1));
}

.total-card:hover .glow-circle {
  animation: pulse 1.5s infinite ease-in-out;
  opacity: 0.8;
}

.total-card:hover .corner-decoration {
  box-shadow: 0 0 5px rgba(59, 130, 246, 0.8);
}

/* 初始显示动画 */
.total-card {
  animation: fadeIn 0.8s ease forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px) rotateX(10deg);
  }
  to {
    opacity: 1;
    transform: translateY(0) rotateX(0);
  }
}

.total-card:nth-child(1) {
  animation-delay: 0.2s;
}

.total-card:nth-child(3) {
  animation-delay: 0.4s;
}

/* 数字滚动效果 */
.odometer {
  transition: transform 0.3s ease;
}

.odometer.changing {
  transform: translateY(-5px);
}
</style>
