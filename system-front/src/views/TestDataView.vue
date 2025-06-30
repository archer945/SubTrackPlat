<template>
  <div class="test-data-container">
    <h1>API数据测试</h1>
    
    <div class="api-section">
      <h2>用户数据</h2>
      <el-button @click="fetchUserData" type="primary">获取用户数据</el-button>
      <pre v-if="userData">{{ JSON.stringify(userData, null, 2) }}</pre>
      <div v-else>暂无数据</div>
    </div>
    
    <div class="api-section">
      <h2>角色数据</h2>
      <el-button @click="fetchRoleData" type="primary">获取角色数据</el-button>
      <pre v-if="roleData">{{ JSON.stringify(roleData, null, 2) }}</pre>
      <div v-else>暂无数据</div>
    </div>
    
    <div class="api-section">
      <h2>菜单数据</h2>
      <el-button @click="fetchMenuData" type="primary">获取菜单数据</el-button>
      <pre v-if="menuData">{{ JSON.stringify(menuData, null, 2) }}</pre>
      <div v-else>暂无数据</div>
    </div>
    
    <div class="api-section">
      <h2>部门数据</h2>
      <el-button @click="fetchDeptData" type="primary">获取部门数据</el-button>
      <pre v-if="deptData">{{ JSON.stringify(deptData, null, 2) }}</pre>
      <div v-else>暂无数据</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const userData = ref(null)
const roleData = ref(null)
const menuData = ref(null)
const deptData = ref(null)

// 直接使用axios获取数据，绕过封装的请求函数
const fetchUserData = async () => {
  try {
    const response = await axios.get('/api/systemManager/user', {
      params: { pageIndex: 1, pageSize: 10 }
    })
    userData.value = response.data
  } catch (error) {
    console.error('获取用户数据失败:', error)
    ElMessage.error('获取用户数据失败')
  }
}

const fetchRoleData = async () => {
  try {
    const response = await axios.get('/api/systemManager/role', {
      params: { pageIndex: 1, pageSize: 10 }
    })
    roleData.value = response.data
  } catch (error) {
    console.error('获取角色数据失败:', error)
    ElMessage.error('获取角色数据失败')
  }
}

const fetchMenuData = async () => {
  try {
    console.log('开始获取菜单数据...')
    const response = await axios.get('/api/systemManager/menu', {
      params: { pageIndex: 1, pageSize: 100 }
    })
    console.log('菜单数据原始响应:', response)
    menuData.value = response.data
    
    // 添加更多调试信息
    if (response.data && response.data.code === 10000) {
      console.log('菜单数据响应成功，数据结构:', {
        hasData: !!response.data.data,
        dataType: typeof response.data.data,
        hasRows: response.data.data && Array.isArray(response.data.data.rows),
        rowsLength: response.data.data && response.data.data.rows ? response.data.data.rows.length : 0
      })
    }
  } catch (error) {
    console.error('获取菜单数据失败:', error)
    ElMessage.error('获取菜单数据失败')
  }
}

const fetchDeptData = async () => {
  try {
    console.log('开始获取部门数据...')
    const response = await axios.get('/api/systemManager/dept', {
      params: { pageIndex: 1, pageSize: 100 }
    })
    console.log('部门数据原始响应:', response)
    deptData.value = response.data
    
    // 添加更多调试信息
    if (response.data && response.data.code === 10000) {
      console.log('部门数据响应成功，数据结构:', {
        hasData: !!response.data.data,
        dataType: typeof response.data.data,
        hasRows: response.data.data && Array.isArray(response.data.data.rows),
        rowsLength: response.data.data && response.data.data.rows ? response.data.data.rows.length : 0
      })
    }
  } catch (error) {
    console.error('获取部门数据失败:', error)
    ElMessage.error('获取部门数据失败')
  }
}
</script>

<style scoped>
.test-data-container {
  padding: 20px;
}

.api-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

pre {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  max-height: 300px;
  overflow: auto;
  margin-top: 10px;
}
</style> 