<template>
  <div class="relative w-full h-full">
    <!-- 移动端版本 - 只在小屏幕上显示 -->
    <div class="md:hidden flex flex-col w-full h-full">
      <!-- 3D渲染容器 - 移动端高度较小 -->
      <div ref="containerMobile" class="w-full h-[50vh]"></div>
      
      <!-- 移动端UI - 垂直排列 -->
      <div class="w-full flex-1 overflow-y-auto p-3 bg-gray-900 space-y-3">
        <!-- 加载状态指示器 -->
        <div v-if="isLoading" class="bg-black/50 backdrop-blur-sm px-3 py-2 rounded-md text-xs text-cyan-300 flex items-center">
          <div class="animate-spin mr-2 w-4 h-4 border-2 border-cyan-300 border-t-transparent rounded-full"></div>
          正在加载数据...
        </div>
        
        <!-- 任务统计数据 -->
        <div class="w-full bg-black/60 backdrop-blur-md p-3 rounded-md border border-blue-500/30 shadow-lg">
          <div class="grid grid-cols-3 gap-3">
            <div class="text-center">
              <div class="text-xs text-gray-400">当前任务</div>
              <div class="text-lg font-bold text-white">{{ todayInspect.length || 0 }} <span class="text-xs text-blue-300">个</span></div>
            </div>
            <div class="text-center">
              <div class="text-xs text-gray-400">进行中</div>
              <div class="text-lg font-bold text-green-400">{{ inProgressCount }} <span class="text-xs text-green-200">个</span></div>
            </div>
            <div class="text-center">
              <div class="text-xs text-gray-400">已完成</div>
              <div class="text-lg font-bold text-blue-400">{{ completedCount }} <span class="text-xs text-blue-200">个</span></div>
            </div>
          </div>
        </div>
        
        <!-- 地铁线路图例 -->
        <div class="w-full bg-black/60 backdrop-blur-md p-3 rounded-md border border-blue-500/30 shadow-lg">
          <div class="flex flex-wrap gap-3">
            <div class="w-full">
              <div class="text-xs font-bold mb-2 text-cyan-300">线路图例</div>
              <div class="flex flex-wrap gap-3">
                <div class="flex items-center">
                  <span class="inline-block w-3 h-3 rounded-full bg-red-500 mr-2"></span>
                  <span class="text-xs text-white">1号线</span>
                </div>
                <div class="flex items-center">
                  <span class="inline-block w-3 h-3 rounded-full bg-blue-500 mr-2"></span>
                  <span class="text-xs text-white">2号线</span>
                </div>
                <div class="flex items-center">
                  <span class="inline-block w-3 h-3 rounded-full bg-green-500 mr-2"></span>
                  <span class="text-xs text-white">3号线</span>
                </div>
              </div>
            </div>
            
            <div class="w-full mt-2 pt-2 border-t border-blue-500/30">
              <div class="text-xs font-bold mb-2 text-cyan-300">任务状态</div>
              <div class="flex flex-wrap gap-3">
                <div class="flex items-center">
                  <span class="inline-block w-3 h-3 rounded-full bg-green-500 mr-2"></span>
                  <span class="text-xs text-white">进行中</span>
                </div>
                <div class="flex items-center">
                  <span class="inline-block w-3 h-3 rounded-full bg-blue-500 mr-2"></span>
                  <span class="text-xs text-white">已完成</span>
                </div>
                <div class="flex items-center">
                  <span class="inline-block w-3 h-3 rounded-full bg-red-500 mr-2"></span>
                  <span class="text-xs text-white">已取消</span>
                </div>
                <div class="flex items-center">
                  <span class="inline-block w-3 h-3 rounded-full bg-yellow-500 mr-2"></span>
                  <span class="text-xs text-white">待执行</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 交互提示 -->
        <div class="w-full bg-black/40 backdrop-blur-sm px-3 py-2 rounded-md text-xs text-white/70 text-center">
          <span>点击轨道可查看任务详情</span>
        </div>
        
        <!-- 任务详情卡片 -->
        <div v-if="hoveredTask" class="w-full bg-black/70 backdrop-blur-lg p-4 rounded-md border border-blue-500/30 shadow-lg text-sm task-card">
          <div class="border-b border-blue-400/30 pb-2 mb-3">
            <div class="flex justify-between items-center">
              <h3 class="font-bold text-cyan-300">巡视任务详情</h3>
              <span :class="getStatusClass(hoveredTask.status)" class="px-2 py-0.5 rounded-full text-xs">{{ hoveredTask.status }}</span>
            </div>
          </div>
          
          <div class="grid grid-cols-3 gap-y-3">
            <div class="text-gray-400">任务ID:</div>
            <div class="col-span-2 text-white">#{{ hoveredTask.id }}</div>
            
            <div class="text-gray-400">巡视路线:</div>
            <div class="col-span-2 text-white font-medium">{{ hoveredTask.lineName }}</div>
            
            <div class="text-gray-400">起点站:</div>
            <div class="col-span-2 text-white">{{ hoveredTask.startPoint }}</div>
            
            <div class="text-gray-400">终点站:</div>
            <div class="col-span-2 text-white">{{ hoveredTask.endPoint }}</div>
            
            <div class="text-gray-400">执行人员:</div>
            <div class="col-span-2 text-white font-medium">{{ hoveredTask.name }}</div>
            
            <div class="text-gray-400">开始时间:</div>
            <div class="col-span-2 text-white">{{ formatDateTime(hoveredTask.actualStart) }}</div>
            
            <div class="text-gray-400">计划结束:</div>
            <div class="col-span-2 text-white">{{ formatDateTime(hoveredTask.plannedEnd) }}</div>
          </div>
          
          <!-- 关闭按钮 -->
          <div class="mt-3 text-center">
            <button @click="hoveredTask = null" class="bg-blue-500/30 hover:bg-blue-500/50 text-white px-4 py-1 rounded-full text-xs">
              关闭详情
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 桌面端版本 - 只在中等及以上屏幕显示 -->
    <div class="hidden md:block w-full h-full">
      <!-- 3D渲染容器 -->
      <div ref="container" class="w-full h-full"></div>
      
      <!-- 加载状态指示器 -->
      <div v-if="isLoading" class="absolute top-4 left-4 bg-black/50 backdrop-blur-sm px-3 py-2 rounded-md text-xs text-cyan-300 flex items-center">
        <div class="animate-spin mr-2 w-4 h-4 border-2 border-cyan-300 border-t-transparent rounded-full"></div>
        正在加载数据...
      </div>
      
      <!-- 地铁线路图例 -->
      <div class="absolute top-4 right-4 bg-black/60 backdrop-blur-md p-3 rounded-md border border-blue-500/30 shadow-lg">
        <div class="text-xs font-bold mb-2 text-cyan-300">线路图例</div>
        <div class="flex flex-col space-y-2">
          <div class="flex items-center">
            <span class="inline-block w-3 h-3 rounded-full bg-red-500 mr-2"></span>
            <span class="text-xs text-white">1号线</span>
          </div>
          <div class="flex items-center">
            <span class="inline-block w-3 h-3 rounded-full bg-blue-500 mr-2"></span>
            <span class="text-xs text-white">2号线</span>
          </div>
          <div class="flex items-center">
            <span class="inline-block w-3 h-3 rounded-full bg-green-500 mr-2"></span>
            <span class="text-xs text-white">3号线</span>
          </div>
        </div>
        <div class="mt-4 pt-2 border-t border-blue-500/30">
          <div class="text-xs font-bold mb-2 text-cyan-300">任务状态</div>
          <div class="flex flex-col space-y-2">
            <div class="flex items-center">
              <span class="inline-block w-3 h-3 rounded-full bg-green-500 mr-2"></span>
              <span class="text-xs text-white">进行中</span>
            </div>
            <div class="flex items-center">
              <span class="inline-block w-3 h-3 rounded-full bg-blue-500 mr-2"></span>
              <span class="text-xs text-white">已完成</span>
            </div>
            <div class="flex items-center">
              <span class="inline-block w-3 h-3 rounded-full bg-red-500 mr-2"></span>
              <span class="text-xs text-white">已取消</span>
            </div>
            <div class="flex items-center">
              <span class="inline-block w-3 h-3 rounded-full bg-yellow-500 mr-2"></span>
              <span class="text-xs text-white">待执行</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 任务统计数据 -->
      <div class="absolute top-4 left-1/2 -translate-x-1/2 bg-black/60 backdrop-blur-md p-3 rounded-md border border-blue-500/30 shadow-lg">
        <div class="grid grid-cols-3 gap-6">
          <div class="text-center">
            <div class="text-xs text-gray-400">当前任务</div>
            <div class="text-xl font-bold text-white">{{ todayInspect.length || 0 }} <span class="text-xs text-blue-300">个</span></div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-400">进行中</div>
            <div class="text-xl font-bold text-green-400">{{ inProgressCount }} <span class="text-xs text-green-200">个</span></div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-400">已完成</div>
            <div class="text-xl font-bold text-blue-400">{{ completedCount }} <span class="text-xs text-blue-200">个</span></div>
          </div>
        </div>
      </div>
      
      <!-- 悬浮提示 -->
      <div class="absolute bottom-20 left-1/2 -translate-x-1/2 bg-black/40 backdrop-blur-sm px-3 py-1 rounded-full text-xs text-white/70">
        <span>鼠标悬停在轨道上可查看任务详情</span>
      </div>
      
      <!-- 任务详情卡片 -->
      <div v-if="hoveredTask" class="absolute left-4 bottom-4 bg-black/70 backdrop-blur-lg p-4 rounded-md border border-blue-500/30 shadow-lg max-w-xs text-sm task-card">
        <div class="border-b border-blue-400/30 pb-2 mb-3">
          <div class="flex justify-between items-center">
            <h3 class="font-bold text-cyan-300">巡视任务详情</h3>
            <span :class="getStatusClass(hoveredTask.status)" class="px-2 py-0.5 rounded-full text-xs">{{ hoveredTask.status }}</span>
          </div>
        </div>
        
        <div class="grid grid-cols-3 gap-y-3">
          <div class="text-gray-400">任务ID:</div>
          <div class="col-span-2 text-white">#{{ hoveredTask.id }}</div>
          
          <div class="text-gray-400">巡视路线:</div>
          <div class="col-span-2 text-white font-medium">{{ hoveredTask.lineName }}</div>
          
          <div class="text-gray-400">起点站:</div>
          <div class="col-span-2 text-white">{{ hoveredTask.startPoint }}</div>
          
          <div class="text-gray-400">终点站:</div>
          <div class="col-span-2 text-white">{{ hoveredTask.endPoint }}</div>
          
          <div class="text-gray-400">执行人员:</div>
          <div class="col-span-2 text-white font-medium">{{ hoveredTask.name }}</div>
          
          <div class="text-gray-400">开始时间:</div>
          <div class="col-span-2 text-white">{{ formatDateTime(hoveredTask.actualStart) }}</div>
          
          <div class="text-gray-400">计划结束:</div>
          <div class="col-span-2 text-white">{{ formatDateTime(hoveredTask.plannedEnd) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import { CSS2DRenderer, CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer';
import request from '../../utils/request';

// 状态变量
const container = ref(null);
const containerMobile = ref(null);
const isLoading = ref(false);
const todayInspect = ref([]);
const hoveredTask = ref(null);

// 计算属性：进行中的任务数量
const inProgressCount = computed(() => {
  return todayInspect.value.filter(task => task.status === '进行中').length;
});

// 计算属性：已完成的任务数量
const completedCount = computed(() => {
  return todayInspect.value.filter(task => task.status === '已完成').length;
});

// Three.js 变量
let scene, camera, renderer, labelRenderer, controls;
let stations = {}; // 存储站点信息
let metroLines = []; // 存储地铁线路
let taskObjects = []; // 存储任务对象
let raycaster = new THREE.Raycaster();
let mouse = new THREE.Vector2();
let frameId = null;

// 地铁线路定义
const metroLineData = [
  {
    id: 1,
    name: '1号线',
    color: 0xff0000, // 红色
    stations: [
      { name: 'A', position: [-15, 0, 0] },
      { name: 'B', position: [-10, 0, 0] },
      { name: 'C', position: [-5, 0, 0] },
      { name: 'D', position: [0, 0, 0] }, // 中心转乘站
      { name: 'E', position: [5, 0, 0] },
      { name: 'F', position: [10, 0, 0] },
      { name: 'G', position: [15, 0, 0] },
    ]
  },
  {
    id: 2,
    name: '2号线',
    color: 0x0000ff, // 蓝色
    stations: [
      { name: 'H', position: [0, 0, -15] },
      { name: 'I', position: [0, 0, -10] },
      { name: 'J', position: [0, 0, -5] },
      { name: 'D', position: [0, 0, 0] }, // 中心转乘站
      { name: 'K', position: [0, 0, 5] },
      { name: 'L', position: [0, 0, 10] },
      { name: 'M', position: [0, 0, 15] },
    ]
  },
  {
    id: 3,
    name: '3号线',
    color: 0x00ff00, // 绿色
    stations: [
      { name: 'N', position: [-15, 0, 15] },
      { name: 'O', position: [-10, 0, 10] },
      { name: 'P', position: [-5, 0, 5] },
      { name: 'D', position: [0, 0, 0] }, // 中心转乘站
      { name: 'Q', position: [5, 0, -5] },
      { name: 'R', position: [10, 0, -10] },
      { name: 'S', position: [15, 0, -15] },
    ]
  }
];

// 获取任务状态样式
const getStatusClass = (status) => {
  switch(status) {
    case '进行中': 
      return 'bg-green-700/60 text-green-300';
    case '已完成': 
      return 'bg-blue-700/60 text-blue-300';
    case '待执行': 
      return 'bg-yellow-700/60 text-yellow-300';
    case '已取消': 
      return 'bg-red-700/60 text-red-300';
    case '已暂停': 
      return 'bg-gray-700/60 text-gray-300';
    default: 
      return 'bg-gray-700/60 text-gray-300';
  }
};

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '未设置';
  
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
  } catch (e) {
    return dateStr;
  }
};

// 初始化
onMounted(() => {
  if (container.value || containerMobile.value) {
    // 根据当前渲染容器初始化场景
    const renderContainer = isMobileView() ? containerMobile.value : container.value;
    initScene(renderContainer);
    createMetroSystem();
    animate();
    
    // 获取数据
    fetchData();
    
    // 监听窗口大小变化
    window.addEventListener('resize', handleResize);
  }
});

// 检测是否为移动视图
const isMobileView = () => {
  return window.innerWidth < 768;
};

// 初始化Three.js场景
const initScene = (renderContainer) => {
  // 创建场景
  scene = new THREE.Scene();
  scene.background = new THREE.Color(0x000824); // 深蓝色背景
  scene.fog = new THREE.FogExp2(0x000824, 0.002); // 增加雾效果
  
  // 创建相机
  camera = new THREE.PerspectiveCamera(
    60,
    renderContainer.clientWidth / renderContainer.clientHeight,
    0.1,
    1000
  );
  
  // 根据视图类型调整相机位置
  if (isMobileView()) {
    camera.position.set(25, 35, 25); // 移动端相机位置调整
  } else {
    camera.position.set(20, 30, 20);
  }
  
  camera.lookAt(0, 0, 0);
  
  // 创建渲染器
  renderer = new THREE.WebGLRenderer({ 
    antialias: !isMobileView(), // 移动端禁用抗锯齿以提高性能
    alpha: true,
    powerPreference: isMobileView() ? 'low-power' : 'high-performance'
  });
  renderer.setSize(renderContainer.clientWidth, renderContainer.clientHeight);
  
  // 移动端降低像素比以提高性能
  if (isMobileView()) {
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 1.5));
  } else {
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  }
  
  renderer.shadowMap.enabled = true;
  renderer.shadowMap.type = isMobileView() ? THREE.BasicShadowMap : THREE.PCFSoftShadowMap;
  renderer.outputEncoding = THREE.sRGBEncoding;
  renderer.toneMapping = THREE.ACESFilmicToneMapping;
  renderer.toneMappingExposure = 1.2;
  renderContainer.appendChild(renderer.domElement);
  
  // 创建标签渲染器
  labelRenderer = new CSS2DRenderer();
  labelRenderer.setSize(renderContainer.clientWidth, renderContainer.clientHeight);
  labelRenderer.domElement.style.position = 'absolute';
  labelRenderer.domElement.style.top = '0';
  labelRenderer.domElement.style.pointerEvents = 'none';
  renderContainer.appendChild(labelRenderer.domElement);
  
  // 创建轨道控制器
  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  
  if (isMobileView()) {
    // 移动端触摸控制设置
    controls.dampingFactor = 0.1; // 增加阻尼系数
    controls.rotateSpeed = 0.6; // 减慢旋转速度
    controls.zoomSpeed = 0.8; // 减慢缩放速度
    controls.minDistance = 15; // 增加最小距离
    controls.maxDistance = 50; // 减小最大距离
    controls.autoRotate = true;
    controls.autoRotateSpeed = 0.3; // 减慢自动旋转速度
  } else {
    // 桌面端控制设置
    controls.dampingFactor = 0.05;
    controls.minDistance = 10;
    controls.maxDistance = 60;
    controls.maxPolarAngle = Math.PI / 2 - 0.1; // 限制垂直旋转角度
    controls.minAzimuthAngle = -Math.PI / 2; // 限制水平旋转角度
    controls.maxAzimuthAngle = Math.PI / 2;
    controls.autoRotate = true; // 自动旋转
    controls.autoRotateSpeed = 0.5; // 旋转速度
  }
  
  // 添加灯光
  const ambientLight = new THREE.AmbientLight(0x404040, 0.8);
  scene.add(ambientLight);
  
  const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
  directionalLight.position.set(10, 20, 10);
  directionalLight.castShadow = true;
  directionalLight.shadow.mapSize.width = 2048;
  directionalLight.shadow.mapSize.height = 2048;
  directionalLight.shadow.camera.near = 0.5;
  directionalLight.shadow.camera.far = 50;
  directionalLight.shadow.bias = -0.0001;
  scene.add(directionalLight);
  
  // 添加点光源
  const pointLight1 = new THREE.PointLight(0x3677ff, 1, 20);
  pointLight1.position.set(5, 10, 5);
  scene.add(pointLight1);
  
  const pointLight2 = new THREE.PointLight(0xff3677, 1, 20);
  pointLight2.position.set(-5, 10, -5);
  scene.add(pointLight2);
  
  // 添加底面网格
  const gridHelper = new THREE.GridHelper(50, 50, 0x444444, 0x222222);
  scene.add(gridHelper);
  
  // 创建地面
  const groundGeometry = new THREE.PlaneGeometry(60, 60);
  const groundMaterial = new THREE.MeshStandardMaterial({
    color: 0x111122,
    roughness: 0.8,
    metalness: 0.2,
    transparent: true,
    opacity: 0.5
  });
  const ground = new THREE.Mesh(groundGeometry, groundMaterial);
  ground.rotation.x = -Math.PI / 2;
  ground.position.y = -0.5;
  ground.receiveShadow = true;
  scene.add(ground);
  
  // 添加装饰性光效
  addLightEffects();
  
  // 初始化射线检测器
  raycaster = new THREE.Raycaster();
  mouse = new THREE.Vector2();
  
  // 添加鼠标和触摸事件监听器
  renderContainer.addEventListener('mousemove', onMouseMove);
  renderContainer.addEventListener('touchstart', onTouchStart, { passive: false });
  renderContainer.addEventListener('touchmove', onTouchMove, { passive: false });
  renderContainer.addEventListener('touchend', onTouchEnd);
  
  // 为相机添加动画效果
  animateCamera();
};

// 添加装饰性光效
const addLightEffects = () => {
  // 移除环境光晕球体，保留有意义的效果
  
  // 添加大气氛围
  const atmosphereGeometry = new THREE.SphereGeometry(40, 32, 32);
  const atmosphereMaterial = new THREE.MeshBasicMaterial({
    color: 0x0a1a3a,
    side: THREE.BackSide,
    transparent: true,
    opacity: 0.3
  });
  const atmosphere = new THREE.Mesh(atmosphereGeometry, atmosphereMaterial);
  scene.add(atmosphere);
  
  // 添加星星
  const starsGeometry = new THREE.BufferGeometry();
  const starsMaterial = new THREE.PointsMaterial({
    color: 0xffffff,
    size: 0.1,
    transparent: true,
    opacity: 0.8
  });
  
  const starsVertices = [];
  for (let i = 0; i < 1000; i++) {
    const x = (Math.random() - 0.5) * 100;
    const y = (Math.random() - 0.5) * 100;
    const z = (Math.random() - 0.5) * 100;
    starsVertices.push(x, y, z);
  }
  
  starsGeometry.setAttribute('position', new THREE.Float32BufferAttribute(starsVertices, 3));
  const stars = new THREE.Points(starsGeometry, starsMaterial);
  scene.add(stars);
  
  // 添加光柱效果 - 保留，因为有视觉意义
  addLightPillars();
  
  // 添加体积光效果 - 保留，因为增强了视觉效果
  addVolumetricLight();
  
  // 添加脉冲波效果 - 保留，因为标识重要站点
  addPulseWaves();
};

// 添加光柱效果
const addLightPillars = () => {
  // 减少光柱数量，仅在关键位置添加
  const pillarColors = [0x3677ff, 0xff3677, 0x36ff77];
  const keyPositions = [
    [-15, 0, 0], // 1号线起点
    [15, 0, 0],  // 1号线终点
    [0, 0, -15], // 2号线起点
    [0, 0, 15],  // 2号线终点
  ];
  
  // 只在关键位置添加光柱，而不是随机位置
  keyPositions.forEach((pos, index) => {
    const height = 15;
    const radius = 0.2;
    
    const geometry = new THREE.CylinderGeometry(radius, radius, height, 16, 1, true);
    const material = new THREE.MeshBasicMaterial({
      color: pillarColors[index % pillarColors.length],
      transparent: true,
      opacity: 0.2,
      side: THREE.DoubleSide
    });
    
    const pillar = new THREE.Mesh(geometry, material);
    pillar.position.set(pos[0], height / 2 - 0.5, pos[2]);
    
    // 添加光晕
    const glowGeometry = new THREE.SphereGeometry(radius * 2, 16, 16);
    const glowMaterial = new THREE.MeshBasicMaterial({
      color: material.color,
      transparent: true,
      opacity: 0.1,
      side: THREE.BackSide
    });
    const glow = new THREE.Mesh(glowGeometry, glowMaterial);
    glow.position.y = height - 1;
    pillar.add(glow);
    
    scene.add(pillar);
    
    // 添加动画数据
    pillar.userData = {
      pulseSpeed: 0.008,
      originalOpacity: material.opacity
    };
    
    taskObjects.push(pillar); // 添加到更新列表中
  });
};

// 添加体积光效果
const addVolumetricLight = () => {
  const light1 = new THREE.PointLight(0x3677ff, 2, 20);
  light1.position.set(10, 15, 10);
  scene.add(light1);
  
  const light2 = new THREE.PointLight(0xff3677, 2, 20);
  light2.position.set(-10, 15, -10);
  scene.add(light2);
  
  // 添加光源标记
  const addLightMarker = (light, size = 0.5) => {
    const markerGeometry = new THREE.SphereGeometry(size, 16, 16);
    const markerMaterial = new THREE.MeshBasicMaterial({
      color: light.color,
      transparent: true,
      opacity: 0.8
    });
    const marker = new THREE.Mesh(markerGeometry, markerMaterial);
    light.add(marker);
    
    // 添加光晕
    const glowGeometry = new THREE.SphereGeometry(size * 2, 16, 16);
    const glowMaterial = new THREE.MeshBasicMaterial({
      color: light.color,
      transparent: true,
      opacity: 0.4,
      side: THREE.BackSide
    });
    const glow = new THREE.Mesh(glowGeometry, glowMaterial);
    marker.add(glow);
    
    // 添加动画数据
    light.userData = {
      pulseSpeed: Math.random() * 0.02 + 0.01,
      originalIntensity: light.intensity
    };
    
    taskObjects.push(light); // 添加到更新列表中
  };
  
  addLightMarker(light1);
  addLightMarker(light2);
};

// 添加脉冲波效果
const addPulseWaves = () => {
  // 在主要交叉站点添加脉冲波
  const transferStation = stations['D'];
  if (transferStation) {
    const position = transferStation.position;
    
    // 创建脉冲波材质
    const pulseMaterial = new THREE.MeshBasicMaterial({
      color: 0x00ffff,
      transparent: true,
      opacity: 0.3,
      side: THREE.DoubleSide
    });
    
    // 创建多个波环
    for (let i = 0; i < 3; i++) {
      const pulseGeometry = new THREE.RingGeometry(0.1, 0.2, 32);
      const pulse = new THREE.Mesh(pulseGeometry, pulseMaterial.clone());
      pulse.position.copy(position);
      pulse.rotation.x = -Math.PI / 2; // 水平放置
      pulse.userData = {
        type: 'pulse',
        scale: 1,
        scaleSpeed: 0.02,
        opacity: 0.3,
        opacitySpeed: 0.01,
        delay: i * 40 // 错开启动时间
      };
      scene.add(pulse);
      taskObjects.push(pulse);
    }
  }
};

// 创建地铁线路和站点
const createMetroSystem = () => {
  metroLineData.forEach(line => {
    createMetroLine(line);
  });
};

// 创建地铁线路
const createMetroLine = (line) => {
  const linePoints = [];
  
  // 遍历该线路的所有站点
  line.stations.forEach(station => {
    const point = new THREE.Vector3(...station.position);
    linePoints.push(point);
    
    // 存储站点信息
    stations[station.name] = {
      position: point.clone(),
      lines: stations[station.name] 
        ? [...stations[station.name].lines, line.name] 
        : [line.name]
    };
    
    // 创建站点
    createStation(station, line.color, line.name);
  });
  
  // 创建线路几何体
  const geometry = new THREE.BufferGeometry().setFromPoints(linePoints);
  
  // 创建线路材质
  const material = new THREE.LineBasicMaterial({
    color: line.color,
    linewidth: 3,
  });
  
  // 创建线路
  const metroLine = new THREE.Line(geometry, material);
  scene.add(metroLine);
  metroLines.push(metroLine);
  
  // 为线路添加隧道效果
  createTunnel(linePoints, line.color);
};

// 创建站点
const createStation = (station, color, lineName) => {
  // 判断是否为中转站
  const isTransfer = station.name === 'D';
  
  // 站台基座
  const baseGeometry = new THREE.CylinderGeometry(0.6, 0.8, 0.4, 16);
  const baseMaterial = new THREE.MeshStandardMaterial({
    color: 0x333333,
    metalness: 0.7,
    roughness: 0.4
  });
  const baseRing = new THREE.Mesh(baseGeometry, baseMaterial);
  baseRing.position.set(...station.position);
  baseRing.position.y -= 0.2;
  baseRing.castShadow = true;
  baseRing.receiveShadow = true;
  scene.add(baseRing);
  
  // 站点几何体
  const stationGeometry = isTransfer
    ? new THREE.SphereGeometry(0.5, 32, 32)
    : new THREE.SphereGeometry(0.35, 32, 32);
    
  const stationMaterial = new THREE.MeshStandardMaterial({
    color: 0xffffff,
    emissive: color,
    emissiveIntensity: 0.6,
    metalness: 0.7,
    roughness: 0.2
  });
  
  const stationMesh = new THREE.Mesh(stationGeometry, stationMaterial);
  stationMesh.position.set(...station.position);
  stationMesh.userData = { type: 'station', name: station.name };
  stationMesh.castShadow = true;
  scene.add(stationMesh);
  
  // 发光环
  const ringGeometry = new THREE.TorusGeometry(0.5, 0.05, 16, 32);
  const ringMaterial = new THREE.MeshStandardMaterial({
    color: color,
    emissive: color,
    emissiveIntensity: 0.8,
    metalness: 0.9,
    roughness: 0.2
  });
  const ring = new THREE.Mesh(ringGeometry, ringMaterial);
  ring.position.set(...station.position);
  ring.rotation.x = Math.PI / 2;
  scene.add(ring);
  
  // 为中转站添加额外的标识
  if (isTransfer) {
    // 添加十字标识
    const crossGeometry1 = new THREE.BoxGeometry(1.5, 0.2, 0.2);
    const crossGeometry2 = new THREE.BoxGeometry(0.2, 0.2, 1.5);
    const crossMaterial = new THREE.MeshStandardMaterial({
      color: 0xffffff,
      emissive: 0xffffff,
      emissiveIntensity: 0.5
    });
    
    const cross1 = new THREE.Mesh(crossGeometry1, crossMaterial);
    const cross2 = new THREE.Mesh(crossGeometry2, crossMaterial);
    
    cross1.position.set(...station.position);
    cross2.position.set(...station.position);
    cross1.position.y += 0.7;
    cross2.position.y += 0.7;
    
    scene.add(cross1);
    scene.add(cross2);
  }
  
  // 创建站点标签
  const div = document.createElement('div');
  div.className = 'station-label';
  div.textContent = `${station.name}站`;
  div.style.color = 'white';
  div.style.fontSize = '12px';
  div.style.fontWeight = 'bold';
  div.style.background = 'rgba(0,0,0,0.6)';
  div.style.padding = '2px 5px';
  div.style.borderRadius = '3px';
  div.style.userSelect = 'none';
  
  const label = new CSS2DObject(div);
  label.position.set(0, 1, 0);
  stationMesh.add(label);
};

// 创建隧道效果
const createTunnel = (points, color) => {
  for (let i = 0; i < points.length - 1; i++) {
    const start = points[i];
    const end = points[i + 1];
    const direction = new THREE.Vector3().subVectors(end, start);
    const length = direction.length();
    
    // 创建隧道几何体
    const tunnelGeometry = new THREE.CylinderGeometry(0.3, 0.3, length, 16, 4, true);
    const tunnelMaterial = new THREE.MeshStandardMaterial({
      color: color,
      transparent: true,
      opacity: 0.15,
      side: THREE.DoubleSide,
      emissive: color,
      emissiveIntensity: 0.2,
    });
    
    const tunnel = new THREE.Mesh(tunnelGeometry, tunnelMaterial);
    
    // 定位和旋转隧道
    const directionNormalized = direction.clone().normalize();
    tunnel.position.copy(start).add(directionNormalized.multiplyScalar(length / 2));
    
    // 旋转隧道指向正确方向
    tunnel.quaternion.setFromUnitVectors(
      new THREE.Vector3(0, 1, 0), 
      new THREE.Vector3().subVectors(end, start).normalize()
    );
    
    scene.add(tunnel);
  }
};

// 清除任务对象
const clearTaskObjects = () => {
  taskObjects.forEach(obj => {
    scene.remove(obj);
  });
  taskObjects = [];
};

// 显示任务数据
const showTaskData = (tasks) => {
  // 清除之前的任务对象
  clearTaskObjects();
  
  // 遍历任务数据创建可视化
  tasks.forEach(task => {
    // 从任务中提取线路号
    const lineNumberMatch = task.lineName.match(/(\d+)/);
    const lineNumber = lineNumberMatch ? parseInt(lineNumberMatch[0]) : null;
    
    // 根据线路号找到对应的线路
    const metroLine = metroLineData.find(line => line.id === lineNumber);
    
    // 如果找不到对应的线路，尝试通过站点名称匹配
    if (!metroLine) {
      // 提取起点站和终点站字母
      const startLetter = task.startPoint.replace('站', '');
      const endLetter = task.endPoint.replace('站', '');
      
      // 遍历所有线路寻找包含这些站点的线路
      for (const line of metroLineData) {
        const hasStartStation = line.stations.some(station => station.name === startLetter);
        const hasEndStation = line.stations.some(station => station.name === endLetter);
        
        if (hasStartStation && hasEndStation) {
          // 找到匹配的起点和终点站
          const startStation = stations[startLetter];
          const endStation = stations[endLetter];
          
          if (startStation && endStation) {
            // 创建任务路径
            createTaskPath(startStation.position, endStation.position, task);
            break;
          }
        }
      }
      
      return;
    }
    
    // 提取起点站和终点站字母
    const startLetter = task.startPoint.replace('站', '');
    const endLetter = task.endPoint.replace('站', '');
    
    // 获取起点和终点站的位置
    const startStation = stations[startLetter];
    const endStation = stations[endLetter];
    
    // 如果起点或终点不存在，尝试使用线路的第一个和最后一个站点
    if (!startStation || !endStation) {
      // 如果线路存在，使用线路的第一个和最后一个站点
      if (metroLine) {
        const firstStation = metroLine.stations[0];
        const lastStation = metroLine.stations[metroLine.stations.length - 1];
        
        const startPos = startStation ? startStation.position : 
          new THREE.Vector3(...firstStation.position);
        const endPos = endStation ? endStation.position : 
          new THREE.Vector3(...lastStation.position);
        
        createTaskPath(startPos, endPos, task);
      }
      
      console.warn(`站点不存在: ${task.startPoint} 或 ${task.endPoint}，使用线路的首尾站点`);
      return;
    }
    
    // 创建任务路径
    createTaskPath(startStation.position, endStation.position, task);
  });
};

// 创建任务路径
const createTaskPath = (start, end, taskData) => {
  // 创建一个弧形路径
  const midPoint = new THREE.Vector3().addVectors(start, end).multiplyScalar(0.5);
  midPoint.y += 5; // 弧高
  
  // 创建贝塞尔曲线
  const curve = new THREE.QuadraticBezierCurve3(
    start,
    midPoint,
    end
  );
  
  // 生成曲线上的点
  const points = curve.getPoints(50); // 增加点数使曲线更平滑
  const geometry = new THREE.BufferGeometry().setFromPoints(points);
  
  // 获取颜色
  let color;
  switch (taskData.status) {
    case '进行中':
      color = 0x00ff00; // 绿色
      break;
    case '已完成':
      color = 0x0088ff; // 蓝色
      break;
    case '已取消':
      color = 0xff0000; // 红色
      break;
    case '已暂停':
      color = 0x888888; // 灰色
      break;
    default:
      color = 0xffbb00; // 黄色（待执行）
  }
  
  // 创建任务路径线 - 使用虚线材质
  const dashSize = 0.2;
  const gapSize = 0.1;
  const material = new THREE.LineDashedMaterial({ 
    color: color,
    dashSize: dashSize,
    gapSize: gapSize,
    transparent: true,
    opacity: 0.8,
    linewidth: 2
  });
  
  const path = new THREE.Line(geometry, material);
  path.computeLineDistances(); // 计算虚线
  path.userData = { type: 'task', data: taskData };
  scene.add(path);
  taskObjects.push(path);
  
  // 添加任务标记 - 在曲线中点
  const markerGeometry = new THREE.SphereGeometry(0.3, 16, 16);
  const markerMaterial = new THREE.MeshBasicMaterial({ 
    color: color,
    emissive: color,
    emissiveIntensity: 0.8
  });
  const marker = new THREE.Mesh(markerGeometry, markerMaterial);
  marker.position.copy(midPoint);
  marker.userData = { type: 'task', data: taskData };
  scene.add(marker);
  taskObjects.push(marker);
  
  // 添加光晕效果
  const glowGeometry = new THREE.SphereGeometry(0.5, 32, 32);
  const glowMaterial = new THREE.MeshBasicMaterial({
    color: color,
    transparent: true,
    opacity: 0.3,
    side: THREE.BackSide
  });
  
  const glow = new THREE.Mesh(glowGeometry, glowMaterial);
  glow.scale.setScalar(3);
  marker.add(glow);
  
  // 添加起点和终点标记
  const createEndpointMarker = (position, isStart) => {
    const markerGeo = new THREE.CylinderGeometry(0.2, 0, 0.6, 8);
    const markerMat = new THREE.MeshStandardMaterial({
      color: isStart ? 0x00ff88 : 0xff8800,
      emissive: isStart ? 0x00ff88 : 0xff8800,
      emissiveIntensity: 0.6
    });
    
    const endMarker = new THREE.Mesh(markerGeo, markerMat);
    endMarker.position.copy(position);
    endMarker.position.y += 0.7;
    endMarker.userData = { type: 'task', data: taskData };
    scene.add(endMarker);
    taskObjects.push(endMarker);
  };
  
  createEndpointMarker(start, true); // 起点
  createEndpointMarker(end, false); // 终点
  
  // 添加动画效果
  animateTaskPath(curve, color, taskData);
};

// 为任务路径添加动画效果
const animateTaskPath = (curve, color, taskData) => {
  // 创建移动的粒子
  const particleGeometry = new THREE.SphereGeometry(0.2, 12, 12);
  const particleMaterial = new THREE.MeshBasicMaterial({
    color: color,
    transparent: true,
    opacity: 0.8
  });
  
  // 根据状态决定粒子速度和数量
  let speed = 0.005;
  let particles = 1;
  
  if (taskData.status === '进行中') {
    speed = 0.01;
    particles = 3; // 进行中的任务有多个粒子
  } else if (taskData.status === '已完成') {
    speed = 0.007;
  } else if (taskData.status === '已取消') {
    speed = 0.003;
  }
  
  // 创建多个粒子
  for (let i = 0; i < particles; i++) {
    const particle = new THREE.Mesh(particleGeometry, particleMaterial);
    // 均匀分布粒子在曲线上
    const initialProgress = i / particles;
    
    particle.userData = { 
      type: 'task', 
      data: taskData,
      curve: curve,
      progress: initialProgress,
      speed: speed
    };
    
    scene.add(particle);
    taskObjects.push(particle);
    
    // 添加拖尾效果
    const trailGeometry = new THREE.SphereGeometry(0.15, 8, 8);
    const trailMaterial = new THREE.MeshBasicMaterial({
      color: color,
      transparent: true,
      opacity: 0.4
    });
    
    for (let j = 0; j < 5; j++) {
      const trail = new THREE.Mesh(trailGeometry, trailMaterial.clone());
      trail.material.opacity = 0.4 - (j * 0.08);
      trail.scale.set(1 - (j * 0.15), 1 - (j * 0.15), 1 - (j * 0.15));
      trail.userData = {
        type: 'trail',
        parent: particle,
        offset: j * 0.05
      };
      scene.add(trail);
      taskObjects.push(trail);
    }
    
    // 添加光晕
    const glowGeometry = new THREE.SphereGeometry(0.3, 16, 16);
    const glowMaterial = new THREE.MeshBasicMaterial({
      color: color,
      transparent: true,
      opacity: 0.4,
      side: THREE.BackSide
    });
    
    const glow = new THREE.Mesh(glowGeometry, glowMaterial);
    glow.scale.set(1.5, 1.5, 1.5);
    particle.add(glow);
  }
};

// 更新任务粒子位置
const updateTaskObjects = () => {
  const time = Date.now();
  
  taskObjects.forEach(obj => {
    // 更新粒子位置
    if (obj.userData && obj.userData.curve) {
      // 更新进度
      obj.userData.progress += obj.userData.speed;
      if (obj.userData.progress > 1) {
        obj.userData.progress = 0;
      }
      
      // 更新位置
      const position = obj.userData.curve.getPointAt(obj.userData.progress);
      obj.position.copy(position);
    } 
    // 更新拖尾位置
    else if (obj.userData && obj.userData.type === 'trail' && obj.userData.parent) {
      const parent = obj.userData.parent;
      const offset = obj.userData.offset;
      
      let trailProgress = parent.userData.progress - offset;
      if (trailProgress < 0) trailProgress += 1;
      
      const position = parent.userData.curve.getPointAt(trailProgress);
      obj.position.copy(position);
    }
    // 更新光柱脉冲效果
    else if (obj.userData && obj.userData.pulseSpeed && obj.material) {
      const pulse = Math.sin(time * obj.userData.pulseSpeed) * 0.5 + 0.5;
      if (obj.material.opacity !== undefined) {
        obj.material.opacity = obj.userData.originalOpacity * (0.7 + pulse * 0.3);
      }
      if (obj.intensity !== undefined) {
        obj.intensity = obj.userData.originalIntensity * (0.7 + pulse * 0.5);
      }
    }
    // 更新脉冲波效果
    else if (obj.userData && obj.userData.type === 'pulse') {
      // 检查是否需要延迟
      if (obj.userData.delay > 0) {
        obj.userData.delay--;
        return;
      }
      
      // 更新缩放
      obj.userData.scale += obj.userData.scaleSpeed;
      obj.scale.set(obj.userData.scale, obj.userData.scale, 1);
      
      // 更新透明度
      obj.userData.opacity -= obj.userData.opacitySpeed;
      obj.material.opacity = Math.max(0, obj.userData.opacity);
      
      // 重置
      if (obj.userData.scale > 10 || obj.userData.opacity <= 0) {
        obj.userData.scale = 1;
        obj.userData.opacity = 0.3;
        obj.scale.set(1, 1, 1);
        obj.material.opacity = 0.3;
      }
    }
  });
};

// 鼠标移动事件处理
const onMouseMove = (event) => {
  // 计算鼠标在标准化设备坐标中的位置
  const rect = renderer.domElement.getBoundingClientRect();
  mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
  mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;
  
  // 检测是否有任务对象被悬停
  const isHoveringTask = checkHovering();
  
  // 如果鼠标悬停在任务对象上，暂停相机自动旋转
  if (controls && isHoveringTask) {
    controls.autoRotate = false;
  } else if (controls) {
    controls.autoRotate = true;
  }
};

// 检测鼠标悬停的对象并返回是否悬停在任务上
const checkHovering = () => {
  raycaster.setFromCamera(mouse, camera);
  
  const intersects = raycaster.intersectObjects(scene.children);
  
  // 重置鼠标样式
  container.value.style.cursor = 'default';
  
  // 如果没有交集，隐藏任务详情
  if (intersects.length === 0) {
    hoveredTask.value = null;
    return false;
  }
  
  // 查找任务对象
  for (let i = 0; i < intersects.length; i++) {
    const obj = intersects[i].object;
    if (obj.userData && obj.userData.type === 'task') {
      container.value.style.cursor = 'pointer';
      hoveredTask.value = obj.userData.data;
      return true;
    }
  }
  
  // 如果没有找到任务对象，隐藏任务详情
  hoveredTask.value = null;
  return false;
};

// 检测鼠标悬停的对象
const checkIntersections = () => {
  // 使用checkHovering代替直接检测
  checkHovering();
};

// 动画循环
const animate = () => {
  frameId = requestAnimationFrame(animate);
  
  if (controls) controls.update();
  
  // 检测鼠标悬停
  checkIntersections();
  
  // 更新任务粒子位置和其他动态效果
  updateTaskObjects();
  
  // 添加一些动态效果
  scene.children.forEach(child => {
    if (child.isMesh && child.material && child.material.emissive) {
      const time = Date.now() * 0.001;
      const pulseIntensity = 0.6 + Math.sin(time * 2) * 0.2;
      child.material.emissiveIntensity = pulseIntensity;
    }
  });
  
  renderer.render(scene, camera);
  labelRenderer.render(scene, camera);
};

// 触摸开始事件处理
const onTouchStart = (event) => {
  // 防止默认行为，避免滚动干扰
  event.preventDefault();
  
  // 单指触摸用于检测点击
  if (event.touches.length === 1) {
    const touch = event.touches[0];
    const rect = renderer.domElement.getBoundingClientRect();
    mouse.x = ((touch.clientX - rect.left) / rect.width) * 2 - 1;
    mouse.y = -((touch.clientY - rect.top) / rect.height) * 2 + 1;
    
    // 检测是否点击到任务对象
    checkHovering();
  }
};

// 触摸移动事件处理
const onTouchMove = (event) => {
  // 防止默认行为，避免滚动干扰
  event.preventDefault();
  
  // 单指触摸移动用于检测悬停
  if (event.touches.length === 1) {
    const touch = event.touches[0];
    const rect = renderer.domElement.getBoundingClientRect();
    mouse.x = ((touch.clientX - rect.left) / rect.width) * 2 - 1;
    mouse.y = -((touch.clientY - rect.top) / rect.height) * 2 + 1;
  }
};

// 触摸结束事件处理
const onTouchEnd = (event) => {
  if (event.touches.length === 0) {
    // 如果没有剩余的触摸点，检查是否选中了任务
    checkHovering();
  }
};

// 窗口大小调整处理
const handleResize = () => {
  if (!camera || !renderer) return;
  
  // 确定当前使用的渲染容器
  const renderContainer = isMobileView() ? containerMobile.value : container.value;
  if (!renderContainer) return;
  
  // 调整相机宽高比
  camera.aspect = renderContainer.clientWidth / renderContainer.clientHeight;
  camera.updateProjectionMatrix();
  
  // 调整渲染器尺寸
  renderer.setSize(renderContainer.clientWidth, renderContainer.clientHeight);
  labelRenderer.setSize(renderContainer.clientWidth, renderContainer.clientHeight);
};

// 获取数据
const fetchData = async () => {
  isLoading.value = true;
  
  try {
    // 直接请求后端服务，不使用代理
    const response = await request.get('/dashboard/inspectSummary');
    console.log('获取到地铁巡视数据:', response);
    
    // 由于response.data已处理过code判断，直接使用数据
    if (response && response.data) {
      todayInspect.value = response.data.todayInspect || [];
      
      // 显示任务数据
      showTaskData(todayInspect.value);
    }
  } catch (error) {
    console.error('请求地铁巡视数据失败:', error);
    // 如果请求失败，可以使用模拟数据
    try {
      const mockData = [
        {
          "id": 20,
          "lineName": "20号线",
          "startPoint": "L站",
          "endPoint": "M站",
          "status": "已取消",
          "actualStart": "2025-07-04T09:05:00",
          "plannedEnd": "2025-07-29T11:00:00",
          "name": "俺是一个美国人"
        },
        {
          "id": 12,
          "lineName": "12号线",
          "startPoint": "V站",
          "endPoint": "W站",
          "status": "已完成",
          "actualStart": "2025-07-04T08:05:00",
          "plannedEnd": "2025-07-21T10:00:00",
          "name": "深情男大于永勋"
        },
        {
          "id": 15,
          "lineName": "3号线",
          "startPoint": "P站",
          "endPoint": "Q站",
          "status": "进行中",
          "actualStart": "2025-07-04T10:15:00",
          "plannedEnd": "2025-07-04T15:30:00",
          "name": "帅气总裁姬凯然"
        }
      ];
      todayInspect.value = mockData;
      showTaskData(mockData);
      console.log('使用模拟数据');
    } catch (e) {
      console.error('使用模拟数据失败:', e);
    }
  } finally {
    isLoading.value = false;
  }
};

// 为相机添加动画效果
const animateCamera = () => {
  // 相机位置动画
  const cameraAnimation = () => {
    const time = Date.now() * 0.0005;
    const radius = 30 + Math.sin(time * 0.2) * 5;
    const angle = time * 0.2;
    
    // 只有当用户不在交互时才自动移动相机
    if (!controls.active) {
      camera.position.x = Math.cos(angle) * radius;
      camera.position.z = Math.sin(angle) * radius;
      camera.position.y = 20 + Math.sin(time * 0.5) * 5;
      camera.lookAt(0, 0, 0);
    }
  };
  
  // 每5秒钟切换相机视角
  let viewIndex = 0;
  const views = [
    { position: [20, 30, 20], target: [0, 0, 0] },
    { position: [0, 40, 0], target: [0, 0, 0] },
    { position: [30, 15, 0], target: [0, 5, 0] },
    { position: [-20, 20, -20], target: [0, 0, 0] }
  ];
  
  const switchCameraView = () => {
    if (controls.active) return; // 用户正在交互，不切换视角
    
    viewIndex = (viewIndex + 1) % views.length;
    const view = views[viewIndex];
    
    // 使用GSAP或简单动画逐渐移动相机
    const duration = 3000; // 3秒
    const startTime = Date.now();
    const startPosition = camera.position.clone();
    const startTarget = controls.target.clone();
    const endPosition = new THREE.Vector3(...view.position);
    const endTarget = new THREE.Vector3(...view.target);
    
    const animateView = () => {
      const elapsed = Date.now() - startTime;
      const progress = Math.min(elapsed / duration, 1);
      
      // 使用缓动函数
      const easeProgress = easeInOutCubic(progress);
      
      // 插值计算新位置和目标点
      camera.position.lerpVectors(startPosition, endPosition, easeProgress);
      controls.target.lerpVectors(startTarget, endTarget, easeProgress);
      
      if (progress < 1) {
        requestAnimationFrame(animateView);
      }
    };
    
    animateView();
  };
  
  // 缓动函数
  const easeInOutCubic = (t) => {
    return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
  };
  
  // 设置定时器，每10秒切换一次视角
  setInterval(switchCameraView, 10000);
};

// 组件销毁时清理
onBeforeUnmount(() => {
  // 移除事件监听器
  window.removeEventListener('resize', handleResize);
  
  // 清理容器上的事件监听器
  const cleanupContainer = (container) => {
    if (container) {
      container.removeEventListener('mousemove', onMouseMove);
      container.removeEventListener('touchstart', onTouchStart);
      container.removeEventListener('touchmove', onTouchMove);
      container.removeEventListener('touchend', onTouchEnd);
    }
  };
  
  cleanupContainer(container.value);
  cleanupContainer(containerMobile.value);
  
  // 取消动画
  if (frameId !== null) {
    cancelAnimationFrame(frameId);
  }
  
  // 移除渲染器DOM元素
  const removeRendererFromContainer = (container) => {
    if (renderer && container) {
      try {
        container.removeChild(renderer.domElement);
      } catch (e) {
        console.warn('移除渲染器DOM元素失败', e);
      }
    }
    
    if (labelRenderer && container) {
      try {
        container.removeChild(labelRenderer.domElement);
      } catch (e) {
        console.warn('移除标签渲染器DOM元素失败', e);
      }
    }
  };
  
  removeRendererFromContainer(container.value);
  removeRendererFromContainer(containerMobile.value);
  
  // 清理Three.js对象
  if (scene) {
    scene.traverse(object => {
      if (object.geometry) object.geometry.dispose();
      if (object.material) {
        if (Array.isArray(object.material)) {
          object.material.forEach(material => material.dispose());
        } else {
          object.material.dispose();
        }
      }
    });
  }
});
</script>

<style scoped>
.task-card {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 移动端样式优化 */
@media (max-width: 768px) {
  /* 提高移动端触摸区域的大小 */
  .task-card button {
    min-height: 40px;
    padding: 8px 16px;
    font-size: 14px;
  }
  
  /* 优化滚动体验 */
  .overflow-y-auto {
    -webkit-overflow-scrolling: touch;
    scroll-behavior: smooth;
  }
  
  /* 减小图标间距，提高空间利用率 */
  .gap-3 {
    gap: 0.5rem;
  }
  
  /* 减小内边距，提高空间利用率 */
  .p-3 {
    padding: 0.5rem;
  }
  
  /* 确保UI元素不会被截断 */
  .flex-1 {
    min-height: 250px;
  }
}
</style> 