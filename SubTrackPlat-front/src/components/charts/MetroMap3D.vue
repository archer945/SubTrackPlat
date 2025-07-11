<template>
  <div ref="container" class="w-full h-full"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import { FontLoader } from 'three/examples/jsm/loaders/FontLoader';
import { TextGeometry } from 'three/examples/jsm/geometries/TextGeometry';
import { CSS2DRenderer, CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer';
import { getMetroLines, getInspectionTasks } from '../../api/visualization';

const props = defineProps({
  activeInspection: {
    type: Object,
    default: null
  }
});

// 核心引用
const container = ref(null);
let scene, camera, renderer, labelRenderer, controls;
let metroLines = [];
let stations = {};
let inspectionPath = null;
let inspectionPathAnimation = null;
let inspectionMarker = null;
let frameId = null;
const isDataLoading = ref(false);
const dataError = ref(null);

// 地铁线路颜色映射（备用）
const lineColors = {
  '1号线': 0xff4500,  // 红色
  '2号线': 0x0000ff,  // 蓝色
  '3号线': 0xffff00,  // 黄色
  '4号线': 0x00ff00,  // 绿色
  '5号线': 0x800080,  // 紫色
  '6号线': 0xffa500,  // 橙色
  '7号线': 0x00ffff,  // 青色
  '8号线': 0xff69b4,  // 粉色
  '9号线': 0x008080,  // 青绿色
  '10号线': 0x964b00  // 棕色
};

// 初始化三维场景
const initScene = () => {
  // 创建场景
  scene = new THREE.Scene();
  scene.background = new THREE.Color(0x001122);
  
  // 创建相机
  camera = new THREE.PerspectiveCamera(
    75, 
    container.value.clientWidth / container.value.clientHeight, 
    0.1, 
    1000
  );
  camera.position.set(0, 20, 30);
  
  // 添加环境光和定向光源
  const ambientLight = new THREE.AmbientLight(0x404040, 1);
  scene.add(ambientLight);
  
  const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
  directionalLight.position.set(10, 10, 10);
  scene.add(directionalLight);
  
  // 创建渲染器
  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
  renderer.setSize(container.value.clientWidth, container.value.clientHeight);
  renderer.setPixelRatio(window.devicePixelRatio);
  container.value.appendChild(renderer.domElement);
  
  // 创建标签渲染器
  labelRenderer = new CSS2DRenderer();
  labelRenderer.setSize(container.value.clientWidth, container.value.clientHeight);
  labelRenderer.domElement.style.position = 'absolute';
  labelRenderer.domElement.style.top = '0';
  labelRenderer.domElement.style.pointerEvents = 'none';
  container.value.appendChild(labelRenderer.domElement);
  
  // 轨道控制器
  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.05;
  
  // 创建一个简单的地面
  const groundGeometry = new THREE.PlaneGeometry(100, 100);
  const groundMaterial = new THREE.MeshBasicMaterial({ 
    color: 0x001133, 
    transparent: true,
    opacity: 0.3,
    side: THREE.DoubleSide
  });
  const ground = new THREE.Mesh(groundGeometry, groundMaterial);
  ground.rotation.x = -Math.PI / 2;
  ground.position.y = -1;
  scene.add(ground);
};

// 创建地铁线路和站点
const createMetroLines = async () => {
  isDataLoading.value = true;
  dataError.value = null;
  
  try {
    // 从API获取地铁线路数据
    const response = await getMetroLines();
    
    if (response && response.code === 10000 && response.data && response.data.lines) {
      const metroData = response.data.lines.map(line => ({
        name: line.name,
        color: parseInt(line.color.replace('#', '0x'), 16), // 将十六进制颜色转换为数字
        stations: line.stations
      }));
      
      // 遍历每条线路创建3D线路和站点
      metroData.forEach(line => {
        const linePoints = [];
        
        // 为每条线路创建点
        line.stations.forEach((station, index) => {
          // 添加点到线路上
          const point = new THREE.Vector3(...station.position);
          linePoints.push(point);
          
          // 保存站点信息到全局对象
          stations[station.name] = {
            position: point.clone(),
            lines: stations[station.name] ? [...stations[station.name].lines, line.name] : [line.name],
            isTransfer: station.isTransfer
          };
          
          // 创建站点标记
          createStation(station, line.color);
        });
        
        // 创建线路几何体
        const geometry = new THREE.BufferGeometry().setFromPoints(linePoints);
        
        // 创建线路材质 - 发光效果
        const material = new THREE.LineBasicMaterial({ 
          color: line.color,
          linewidth: 3,
          transparent: true,
          opacity: 0.8,
          emissive: line.color,
          emissiveIntensity: 0.5
        });
        
        // 创建线路
        const metroLine = new THREE.Line(geometry, material);
        scene.add(metroLine);
        metroLines.push(metroLine);
        
        // 为每条线路添加隧道效果
        createTunnel(linePoints, line.color);
      });
    } else {
      dataError.value = '地铁线路数据格式错误';
      console.error('地铁线路数据格式错误:', response);
    }
  } catch (error) {
    dataError.value = `地铁线路数据加载失败: ${error.message}`;
    console.error('地铁线路数据加载失败:', error);
  } finally {
    isDataLoading.value = false;
  }
};

// 创建站点
const createStation = (station, color) => {
  // 站点几何体
  const geometry = station.isTransfer
    ? new THREE.SphereGeometry(0.4, 32, 32)
    : new THREE.SphereGeometry(0.3, 32, 32);
    
  // 站点材质
  const material = new THREE.MeshStandardMaterial({
    color: 0xffffff,
    emissive: color,
    emissiveIntensity: 0.5,
    metalness: 0.3,
    roughness: 0.4
  });
  
  // 创建站点网格
  const mesh = new THREE.Mesh(geometry, material);
  mesh.position.set(...station.position);
  
  // 添加到场景
  scene.add(mesh);
  
  // 创建站点标签
  const div = document.createElement('div');
  div.className = 'station-label';
  div.textContent = station.name;
  div.style.color = 'white';
  div.style.fontSize = '0.8em';
  div.style.textShadow = '0 0 3px rgba(0,0,0,0.8)';
  div.style.pointerEvents = 'none';
  
  const label = new CSS2DObject(div);
  label.position.set(0, 0.5, 0);
  mesh.add(label);
};

// 创建隧道效果
const createTunnel = (points, color) => {
  // 为相邻点之间创建隧道管道
  for (let i = 0; i < points.length - 1; i++) {
    // 计算方向和距离
    const start = points[i];
    const end = points[i + 1];
    const direction = new THREE.Vector3().subVectors(end, start);
    const length = direction.length();
    
    // 创建一个圆柱体代表隧道
    const geometry = new THREE.CylinderGeometry(0.25, 0.25, length, 8);
    const material = new THREE.MeshStandardMaterial({
      color: color,
      transparent: true,
      opacity: 0.15,
      side: THREE.DoubleSide,
      emissive: color,
      emissiveIntensity: 0.2,
    });
    
    const tunnel = new THREE.Mesh(geometry, material);
    
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

// 创建巡视路径动画
const createInspectionPath = (startStation, endStation) => {
  // 清除之前的动画和路径
  if (inspectionPathAnimation) {
    cancelAnimationFrame(inspectionPathAnimation);
    inspectionPathAnimation = null;
  }
  
  if (inspectionPath) {
    scene.remove(inspectionPath);
  }
  
  if (inspectionMarker) {
    scene.remove(inspectionMarker);
  }
  
  // 检查站点是否存在
  if (!stations[startStation] || !stations[endStation]) {
    console.error('站点不存在:', { startStation, endStation });
    return;
  }
  
  // 获取起点和终点位置
  const start = stations[startStation].position;
  const end = stations[endStation].position;
  
  // 创建巡视路径线
  const points = [start, end];
  const geometry = new THREE.BufferGeometry().setFromPoints(points);
  const material = new THREE.LineDashedMaterial({ 
    color: 0x00ffff, 
    dashSize: 0.5, 
    gapSize: 0.2,
    scale: 1,
    linewidth: 2
  });
  
  inspectionPath = new THREE.Line(geometry, material);
  inspectionPath.computeLineDistances();
  scene.add(inspectionPath);
  
  // 创建巡视标记
  const markerGeometry = new THREE.SphereGeometry(0.3, 16, 16);
  const markerMaterial = new THREE.MeshBasicMaterial({ 
    color: 0x00ffff,
    emissive: 0x00ffff,
    emissiveIntensity: 1
  });
  
  inspectionMarker = new THREE.Mesh(markerGeometry, markerMaterial);
  inspectionMarker.position.copy(start);
  scene.add(inspectionMarker);
  
  // 创建动画
  let progress = 0;
  const animate = () => {
    progress += 0.005;
    
    if (progress >= 1) {
      progress = 0;
    }
    
    // 计算当前位置
    const currentPos = new THREE.Vector3().lerpVectors(start, end, progress);
    inspectionMarker.position.copy(currentPos);
    
    inspectionPathAnimation = requestAnimationFrame(animate);
  };
  
  animate();
};

// 动画循环
const animate = () => {
  frameId = requestAnimationFrame(animate);
  
  if (controls) controls.update();
  
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

// 初始化3D地铁地图
const init = async () => {
  if (container.value) {
    initScene();
    await createMetroLines();
    
    // 如果已经有传入的巡视任务，则创建动画
    if (props.activeInspection && props.activeInspection.startStation && props.activeInspection.endStation) {
      createInspectionPath(props.activeInspection.startStation, props.activeInspection.endStation);
    }
    
    animate();
    window.addEventListener('resize', handleResize);
  }
};

// 监听窗口大小变化
onMounted(() => {
  init();
});

// 监听激活的巡视任务变化
watch(() => props.activeInspection, (newValue) => {
  if (newValue && newValue.startStation && newValue.endStation) {
    createInspectionPath(newValue.startStation, newValue.endStation);
  }
}, { deep: true });

// 组件卸载前清理
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  
  if (frameId !== null) {
    cancelAnimationFrame(frameId);
  }
  
  if (inspectionPathAnimation) {
    cancelAnimationFrame(inspectionPathAnimation);
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
  
  scene = null;
  camera = null;
  renderer = null;
  labelRenderer = null;
  controls = null;
});
</script>

<style scoped>
.station-label {
  padding: 2px;
  margin-top: -16px;
  pointer-events: none;
  white-space: nowrap;
}
</style> 