<template>
  <div class="relative w-full h-full">
    <!-- 3D渲染容器 -->
    <div ref="container" class="w-full h-full"></div>
    
    <!-- 加载状态指示器 -->
    <div v-if="isLoading" class="absolute top-4 left-4 bg-black/50 backdrop-blur-sm px-3 py-2 rounded-md text-xs text-cyan-300 flex items-center">
      <div class="animate-spin mr-2 w-4 h-4 border-2 border-cyan-300 border-t-transparent rounded-full"></div>
      正在加载数据...
    </div>
    
    <!-- 地铁线路图例 -->
    <div class="absolute top-4 right-4 bg-black/60 backdrop-blur-md p-2 rounded-md border border-blue-500/30">
      <div class="text-xs font-bold mb-2 text-white">线路图例</div>
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
    </div>
    
    <!-- 任务详情卡片 -->
    <div v-if="hoveredTask" class="absolute left-4 bottom-4 bg-black/70 backdrop-blur-lg p-4 rounded-md border border-blue-500/30 shadow-lg max-w-xs text-sm task-card">
      <div class="border-b border-blue-400/30 pb-2 mb-2">
        <div class="flex justify-between items-center">
          <h3 class="font-bold text-cyan-300">巡视任务详情</h3>
          <span :class="getStatusClass(hoveredTask.status)" class="px-2 py-0.5 rounded-full text-xs">{{ hoveredTask.status }}</span>
        </div>
      </div>
      
      <div class="grid grid-cols-3 gap-y-2">
        <div class="text-gray-400">巡视路线:</div>
        <div class="col-span-2 text-white">{{ hoveredTask.lineName }}</div>
        
        <div class="text-gray-400">起点站:</div>
        <div class="col-span-2 text-white">{{ hoveredTask.startPoint }}</div>
        
        <div class="text-gray-400">终点站:</div>
        <div class="col-span-2 text-white">{{ hoveredTask.endPoint }}</div>
        
        <div class="text-gray-400">执行人员:</div>
        <div class="col-span-2 text-white">{{ hoveredTask.name }}</div>
        
        <div class="text-gray-400">开始时间:</div>
        <div class="col-span-2 text-white">{{ formatDateTime(hoveredTask.actualStart) }}</div>
        
        <div class="text-gray-400">计划结束:</div>
        <div class="col-span-2 text-white">{{ formatDateTime(hoveredTask.plannedEnd) }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import { CSS2DRenderer, CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer';
import axios from 'axios';

// 状态变量
const container = ref(null);
const isLoading = ref(false);
const todayInspect = ref([]);
const hoveredTask = ref(null);

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

// 初始化Three.js场景
const initScene = () => {
  // 创建场景
  scene = new THREE.Scene();
  scene.background = new THREE.Color(0x000824); // 深蓝色背景
  scene.fog = new THREE.FogExp2(0x000824, 0.002); // 增加雾效果
  
  // 创建相机
  camera = new THREE.PerspectiveCamera(
    60,
    container.value.clientWidth / container.value.clientHeight,
    0.1,
    1000
  );
  camera.position.set(20, 30, 20);
  camera.lookAt(0, 0, 0);
  
  // 创建渲染器
  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
  renderer.setSize(container.value.clientWidth, container.value.clientHeight);
  renderer.setPixelRatio(window.devicePixelRatio);
  renderer.shadowMap.enabled = true;
  container.value.appendChild(renderer.domElement);
  
  // 创建标签渲染器
  labelRenderer = new CSS2DRenderer();
  labelRenderer.setSize(container.value.clientWidth, container.value.clientHeight);
  labelRenderer.domElement.style.position = 'absolute';
  labelRenderer.domElement.style.top = '0';
  labelRenderer.domElement.style.pointerEvents = 'none';
  container.value.appendChild(labelRenderer.domElement);
  
  // 创建轨道控制器
  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.05;
  controls.minDistance = 10;
  controls.maxDistance = 60;
  
  // 添加灯光
  const ambientLight = new THREE.AmbientLight(0x404040, 0.8);
  scene.add(ambientLight);
  
  const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
  directionalLight.position.set(10, 20, 10);
  directionalLight.castShadow = true;
  scene.add(directionalLight);
  
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
  
  // 添加鼠标移动事件监听器
  container.value.addEventListener('mousemove', onMouseMove);
};

// 添加装饰性光效
const addLightEffects = () => {
  // 添加环境光晕
  const sphereGeometry = new THREE.SphereGeometry(0.5, 32, 32);
  const sphereMaterial = new THREE.MeshBasicMaterial({
    color: 0x3677ff,
    transparent: true,
    opacity: 0.2
  });
  
  // 在场景中随机添加一些光晕球体
  for (let i = 0; i < 20; i++) {
    const sphere = new THREE.Mesh(sphereGeometry, sphereMaterial);
    sphere.position.x = (Math.random() - 0.5) * 50;
    sphere.position.y = Math.random() * 10;
    sphere.position.z = (Math.random() - 0.5) * 50;
    sphere.scale.setScalar(Math.random() * 3 + 1);
    scene.add(sphere);
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
    // 检查起点站和终点站是否存在
    const startStation = stations[task.startPoint.replace('站', '')];
    const endStation = stations[task.endPoint.replace('站', '')];
    
    if (!startStation || !endStation) {
      console.warn(`站点不存在: ${task.startPoint} 或 ${task.endPoint}`);
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
  const points = curve.getPoints(20);
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
  
  // 创建任务路径线
  const material = new THREE.LineBasicMaterial({ 
    color: color,
    transparent: true,
    opacity: 0.8,
    linewidth: 2
  });
  
  const path = new THREE.Line(geometry, material);
  path.userData = { type: 'task', data: taskData };
  scene.add(path);
  taskObjects.push(path);
  
  // 添加任务标记 - 在曲线中点
  const markerGeometry = new THREE.SphereGeometry(0.3, 16, 16);
  const markerMaterial = new THREE.MeshBasicMaterial({ color: color });
  const marker = new THREE.Mesh(markerGeometry, markerMaterial);
  marker.position.copy(midPoint);
  marker.userData = { type: 'task', data: taskData };
  scene.add(marker);
  taskObjects.push(marker);
  
  // 添加动画效果
  animateTaskPath(curve, color, taskData);
};

// 为任务路径添加动画效果
const animateTaskPath = (curve, color, taskData) => {
  // 创建移动的粒子
  const particleGeometry = new THREE.SphereGeometry(0.2, 8, 8);
  const particleMaterial = new THREE.MeshBasicMaterial({
    color: color,
    transparent: true,
    opacity: 0.8
  });
  
  const particle = new THREE.Mesh(particleGeometry, particleMaterial);
  particle.userData = { 
    type: 'task', 
    data: taskData,
    curve: curve,
    progress: 0,
    speed: 0.005
  };
  
  scene.add(particle);
  taskObjects.push(particle);
  
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
};

// 更新任务粒子位置
const updateTaskParticles = () => {
  taskObjects.forEach(obj => {
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
  });
};

// 鼠标移动事件处理
const onMouseMove = (event) => {
  // 计算鼠标在标准化设备坐标中的位置
  const rect = renderer.domElement.getBoundingClientRect();
  mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
  mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;
};

// 检测鼠标悬停的对象
const checkIntersections = () => {
  raycaster.setFromCamera(mouse, camera);
  
  const intersects = raycaster.intersectObjects(scene.children);
  
  // 重置鼠标样式
  container.value.style.cursor = 'default';
  
  // 如果没有交集，隐藏任务详情
  if (intersects.length === 0) {
    hoveredTask.value = null;
    return;
  }
  
  // 查找任务对象
  for (let i = 0; i < intersects.length; i++) {
    const obj = intersects[i].object;
    if (obj.userData && obj.userData.type === 'task') {
      container.value.style.cursor = 'pointer';
      hoveredTask.value = obj.userData.data;
      return;
    }
  }
  
  // 如果没有找到任务对象，隐藏任务详情
  hoveredTask.value = null;
};

// 动画循环
const animate = () => {
  frameId = requestAnimationFrame(animate);
  
  if (controls) controls.update();
  
  // 检测鼠标悬停
  checkIntersections();
  
  // 更新任务粒子位置
  updateTaskParticles();
  
  // 添加一些动态效果
  scene.children.forEach(child => {
    if (child.isMesh && child.material.emissive) {
      const time = Date.now() * 0.001;
      const pulseIntensity = 0.6 + Math.sin(time * 2) * 0.2;
      child.material.emissiveIntensity = pulseIntensity;
    }
  });
  
  renderer.render(scene, camera);
  labelRenderer.render(scene, camera);
};

// 窗口大小调整处理
const handleResize = () => {
  if (!camera || !renderer || !container.value) return;
  
  camera.aspect = container.value.clientWidth / container.value.clientHeight;
  camera.updateProjectionMatrix();
  renderer.setSize(container.value.clientWidth, container.value.clientHeight);
  labelRenderer.setSize(container.value.clientWidth, container.value.clientHeight);
};

// 获取数据
const fetchData = async () => {
  isLoading.value = true;
  
  try {
    const response = await axios.get('/dashboard/inspectSummary');
    if (response.data && response.data.code === 10000) {
      todayInspect.value = response.data.data.todayInspect || [];
      
      // 显示任务数据
      showTaskData(todayInspect.value);
    } else {
      console.error('获取数据失败:', response);
    }
  } catch (error) {
    console.error('请求错误:', error);
  } finally {
    isLoading.value = false;
  }
};

// 初始化
onMounted(() => {
  if (container.value) {
    initScene();
    createMetroSystem();
    animate();
    
    // 获取数据
    fetchData();
    
    // 监听窗口大小变化
    window.addEventListener('resize', handleResize);
  }
});

// 组件销毁时清理
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  container.value?.removeEventListener('mousemove', onMouseMove);
  
  if (frameId !== null) {
    cancelAnimationFrame(frameId);
  }
  
  if (renderer && container.value) {
    container.value.removeChild(renderer.domElement);
  }
  
  if (labelRenderer && container.value) {
    container.value.removeChild(labelRenderer.domElement);
  }
  
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
</style> 