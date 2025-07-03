<template>
  <div class="dept-manage-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="部门名称">
          <el-input v-model="searchForm.deptName" placeholder="请输入部门名称" clearable />
        </el-form-item>
        <el-form-item label="部门编码">
          <el-input v-model="searchForm.deptCode" placeholder="请输入部门编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable  style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-area">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增
      </el-button>
      <el-button @click="refreshTable">刷新</el-button>
      <el-button type="info" @click="expandAll">展开/折叠</el-button>
    </div>

    <!-- 表格区域 -->
    <el-table
      ref="deptTableRef"
      :data="tableData"
      row-key="deptId"
      border
      default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      @row-click="handleRowClick"
      highlight-current-row
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="deptId" label="部门编号" width="120" />
      <el-table-column prop="deptName" label="部门名称" width="180" />
      <el-table-column prop="deptCode" label="部门编码" width="180" />
      <el-table-column prop="orderNum" label="排序" width="100" />
      <el-table-column prop="leader" label="负责人" width="120" />
      <el-table-column prop="tel" label="联系电话" width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="scope">
          <el-button size="small" type="primary" @click.stop="handleEdit(scope.row)">
            <el-icon><Edit /></el-icon>修改
          </el-button>
          <el-button size="small" type="danger" @click.stop="handleRemove(scope.row)">
            <el-icon><Delete /></el-icon>删除
          </el-button>
        </template>
      </el-table-column>
      <template #empty>
        <div class="empty-data">
          <p>暂无部门数据</p>
          <el-button type="primary" @click="refreshTable">刷新数据</el-button>
          <el-button type="success" @click="initTestData">初始化测试数据</el-button>
        </div>
      </template>
    </el-table>

    <!-- 部门表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增部门' : '编辑部门'"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="deptFormRef"
        :model="deptForm"
        :rules="deptRules"
        label-width="100px"
      >
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="deptForm.parentId"
            :data="deptOptions"
            placeholder="请选择上级部门"
            check-strictly
            :render-after-expand="false"
            default-expand-all
            node-key="deptId"
            :props="{
              label: 'deptName',
              children: 'children',
              value: 'deptId',
              disabled: 'disabled'
            }"
            filterable
            clearable
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="deptForm.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码" prop="deptCode">
          <el-input v-model="deptForm.deptCode" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="deptForm.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="deptForm.tel" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="deptForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="deptForm.orderNum" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="deptForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDeptList, addDept, updateDept, deleteDept, getDeptTree } from '@/api/systemManager/dept'

// 搜索表单
const searchForm = reactive({
  deptName: '',
  deptCode: '',
  status: ''
})

// 表格数据
const tableData = ref([])

// 部门树形选择器数据
const deptOptions = ref([])

// 选中行
const selectedRow = ref(null)
const deptTableRef = ref(null)
const isExpand = ref(true)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const deptFormRef = ref(null)
const deptForm = reactive({
  deptId: '',
  deptName: '',
  deptCode: '',
  parentId: 0,
  orderNum: 0,
  leader: '',
  tel: '',
  email: '',
  status: 1
})

// 表单校验规则
const deptRules = {
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  deptCode: [
    { required: true, message: '请输入部门编码', trigger: 'blur' }
  ],
  leader: [
    { required: true, message: '请输入负责人', trigger: 'blur' }
  ],
  tel: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 处理搜索
const handleSearch = () => {
  fetchDeptList()
}

// 重置搜索
const resetSearch = () => {
  // 重置搜索表单
  searchForm.deptName = ''
  searchForm.deptCode = ''
  searchForm.status = ''
  handleSearch()
}

// 获取部门列表数据
const fetchDeptList = async () => {
  try {
    const params = {
      pageIndex: 1,
      pageSize: 100, // 部门通常不需要分页，但仍需提供pageSize参数
      ...searchForm
    }
    
    console.log('发送部门列表请求参数:', params)
    const response = await getDeptList(params)
    console.log('部门列表原始响应:', JSON.stringify(response))
    
    // 确保数据格式正确
    if (response && response.records) {
      tableData.value = processDeptData(response.records)
      console.log('处理后的部门数据:', JSON.stringify(tableData.value))
    } else if (response && response.rows) {
      // 兼容后端返回rows的情况
      tableData.value = processDeptData(response.rows)
      console.log('使用rows字段的部门数据:', JSON.stringify(tableData.value))
    } else if (response && response.data && response.data.rows) {
      // 兼容后端返回data.rows的情况
      tableData.value = processDeptData(response.data.rows)
      console.log('使用data.rows字段的部门数据:', JSON.stringify(tableData.value))
    } else {
      // 尝试直接使用response，如果它是数组的话
      if (Array.isArray(response)) {
        tableData.value = processDeptData(response)
        console.log('直接使用response作为数组:', JSON.stringify(tableData.value))
      } else if (response && typeof response === 'object') {
        // 尝试从response中提取可能的数据字段
        const possibleDataFields = ['data', 'list', 'items', 'content'];
        for (const field of possibleDataFields) {
          if (response[field] && Array.isArray(response[field])) {
            tableData.value = processDeptData(response[field]);
            console.log(`使用response.${field}字段:`, JSON.stringify(tableData.value));
            break;
          } else if (response[field] && response[field].rows && Array.isArray(response[field].rows)) {
            tableData.value = processDeptData(response[field].rows);
            console.log(`使用response.${field}.rows字段:`, JSON.stringify(tableData.value));
            break;
          }
        }
        
        if (tableData.value.length === 0) {
          console.error('部门数据格式不符合预期:', response)
        }
      } else {
        console.error('部门数据格式不符合预期:', response)
        tableData.value = []
      }
    }

    // 如果没有数据，尝试初始化测试数据
    if (tableData.value.length === 0) {
      console.warn('未获取到部门数据，考虑初始化测试数据')
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
    // 如果发生错误，至少显示空数据而不是崩溃
    tableData.value = []
  }
}

// 处理部门数据，确保格式正确
const processDeptData = (data) => {
  if (!Array.isArray(data)) return []
  
  const processItem = (item) => {
    // 创建新对象，避免直接修改原始数据
    const newItem = { ...item }
    
    // 确保status字段是数字类型
    if (newItem.status !== undefined) {
      if (typeof newItem.status === 'string') {
        newItem.status = parseInt(newItem.status, 10)
      } else if (newItem.status === null) {
        newItem.status = 0
      }
    } else {
      // 如果status字段不存在或为undefined，设置默认值为0（停用）
      newItem.status = 0
    }
    
    // 递归处理子部门
    if (newItem.children && Array.isArray(newItem.children)) {
      newItem.children = newItem.children.map(child => processItem(child))
    }
    
    return newItem
  }
  
  return data.map(item => processItem(item))
}

// 获取部门树
const fetchDeptTree = async () => {
  try {
    // 使用专门的部门树接口获取部门数据
    const response = await getDeptTree()
    console.log('部门树原始响应:', JSON.stringify(response))
    
    let treeData = []
    
    // 确保数据格式正确
    if (response && Array.isArray(response)) {
      // 如果直接返回数组，则使用该数组作为树形数据
      treeData = processDeptData(response)
    } else if (response && response.data && Array.isArray(response.data)) {
      // 如果返回的是包含data字段的对象，且data是数组
      treeData = processDeptData(response.data)
    } else if (response && typeof response === 'object') {
      // 尝试从response中提取可能的数据字段
      const possibleDataFields = ['data', 'list', 'items', 'content', 'rows', 'records'];
      for (const field of possibleDataFields) {
        if (response[field] && Array.isArray(response[field])) {
          treeData = processDeptData(response[field]);
          console.log(`使用response.${field}字段获取树数据:`, JSON.stringify(treeData));
          break;
        }
      }
      
      if (treeData.length === 0) {
        console.error('部门树数据格式不符合预期:', response)
      }
    } else {
      console.error('部门树数据格式不符合预期:', response)
    }
    
    // 构造部门选择器的数据结构，直接使用树形数据，不添加虚拟根节点
    deptOptions.value = treeData
    
    // 添加一个"无上级部门"选项
    deptOptions.value.unshift({
      deptId: 0,
      deptName: '无上级部门',
      disabled: false,
      children: []
    })
    
    console.log('处理后的部门树数据:', JSON.stringify(deptOptions.value))

    // 如果没有获取到部门树数据，只保留"无上级部门"选项
    if (treeData.length === 0) {
      deptOptions.value = [
        {
          deptId: 0,
          deptName: '无上级部门',
          disabled: false,
          children: []
        }
      ]
      console.warn('未获取到部门树数据，只显示无上级部门选项')
    }
      } catch (error) {
      console.error('获取部门树失败:', error)
      ElMessage.warning('获取部门数据失败，请刷新重试')
      deptOptions.value = [
        {
          deptId: 0,
          deptName: '无上级部门',
          disabled: false,
          children: []
        }
      ]
    }
}

// 处理新增
const handleAdd = async () => {
  dialogType.value = 'add'
  deptForm.parentId = 0
  // 打开对话框前重新获取部门树
  await fetchDeptTree()
  dialogVisible.value = true
}

// 处理新增子部门
const handleAddChild = (row) => {
  dialogType.value = 'add'
  deptForm.parentId = row.deptId
  dialogVisible.value = true
}

// 刷新表格
const refreshTable = () => {
  fetchDeptList()
}

// 展开/折叠所有
const expandAll = () => {
  isExpand.value = !isExpand.value
  toggleExpand(tableData.value, isExpand.value)
}

// 递归切换展开状态
const toggleExpand = (data, expand) => {
  data.forEach(item => {
    deptTableRef.value.toggleRowExpansion(item, expand)
    if (item.children && item.children.length > 0) {
      toggleExpand(item.children, expand)
    }
  })
}

// 处理行点击
const handleRowClick = (row) => {
  selectedRow.value = row
}

// 处理编辑
const handleEdit = async (row) => {
  dialogType.value = 'edit'
  // 填充表单数据
  Object.keys(deptForm).forEach(key => {
    if (key in row) {
      deptForm[key] = row[key]
    }
  })
  // 打开对话框前重新获取部门树
  await fetchDeptTree()
  
  // 禁用当前部门及其所有子部门，防止循环引用
  disableCurrentDeptAndChildren(deptOptions.value, row.deptId)
  
  dialogVisible.value = true
}

// 禁用当前部门及其所有子部门，防止循环引用
const disableCurrentDeptAndChildren = (depts, currentDeptId) => {
  if (!depts || !Array.isArray(depts)) return
  
  for (const dept of depts) {
    if (dept.deptId === currentDeptId) {
      dept.disabled = true
      // 递归禁用所有子部门
      if (dept.children && Array.isArray(dept.children)) {
        disableAllChildren(dept.children)
      }
    } else if (dept.children && Array.isArray(dept.children)) {
      // 递归检查子部门
      disableCurrentDeptAndChildren(dept.children, currentDeptId)
    }
  }
}

// 禁用所有子部门
const disableAllChildren = (children) => {
  if (!children || !Array.isArray(children)) return
  
  for (const child of children) {
    child.disabled = true
    if (child.children && Array.isArray(child.children)) {
      disableAllChildren(child.children)
    }
  }
}

// 处理删除单条记录
const handleRemove = (row) => {
  // 检查是否有子部门
  if (row.children && row.children.length > 0) {
    ElMessage.warning('该部门下存在子部门，请先删除子部门')
    return
  }
  
  ElMessageBox.confirm(`确认删除部门"${row.deptName}"吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDept(row.deptId)
      ElMessage.success('删除成功')
      // 立即刷新数据
      await fetchDeptList()
      // 同时刷新部门树
      await fetchDeptTree()
    } catch (error) {
      console.error('删除部门失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单
const submitForm = () => {
  deptFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 确保status是数字类型
        deptForm.status = Number(deptForm.status);
        
        if (dialogType.value === 'add') {
          // 新增部门
          await addDept(deptForm)
          ElMessage.success('添加成功')
        } else {
          // 修改部门
          await updateDept(deptForm.deptId, deptForm)
          ElMessage.success('修改成功')
        }
        
        dialogVisible.value = false
        // 立即刷新数据
        await fetchDeptList()
        // 同时刷新部门树
        await fetchDeptTree()
      } catch (error) {
        console.error('保存部门失败:', error)
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  deptFormRef.value?.resetFields()
  Object.keys(deptForm).forEach(key => {
    if (key === 'orderNum') {
      deptForm[key] = 0
    } else if (key === 'parentId') {
      deptForm[key] = 0
    } else if (key === 'status') {
      deptForm[key] = 1
    } else {
      deptForm[key] = ''
    }
  })
}

// 初始化测试数据
const initTestData = () => {
  tableData.value = [
    {
      deptId: 1,
      deptName: '总部',
      deptCode: 'HQ',
      parentId: 0,
      orderNum: 1,
      leader: '张三',
      tel: '13800138000',
      email: 'admin@example.com',
      status: Number(1), // 确保是数字类型
      createTime: '2025-06-27T19:53:27',
      children: [
        {
          deptId: 4,
          deptName: '总急急部',
          deptCode: 'HQIT',
          parentId: 1,
          orderNum: 1,
          leader: '李四',
          tel: '13800138001',
          email: 'it@example.com',
          status: Number(1), // 确保是数字类型
          createTime: '2025-06-27T21:24:12',
          children: null
        }
      ]
    }
  ]
  
  // 同时更新部门树
  deptOptions.value = tableData.value.slice()
  
  // 添加"无上级部门"选项
  deptOptions.value.unshift({
    deptId: 0,
    deptName: '无上级部门',
    disabled: false,
    children: []
  })
  
  ElMessage.success('已初始化测试数据')
}

// 获取状态类型
const getStatusType = (status) => {
  if (status === 1 || status === '1') {
    return 'success'
  } else if (status === 0 || status === '0') {
    return 'danger'
  } else {
    // 对于undefined或其他值，默认显示为灰色
    return 'info'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  if (status === 1 || status === '1') {
    return '正常'
  } else if (status === 0 || status === '0') {
    return '停用'
  } else {
    // 对于undefined或其他值，显示为未知
    return '未知'
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchDeptList()
  fetchDeptTree()
})

// 组件被激活时刷新数据（从缓存中恢复时）
onActivated(() => {
  console.log('部门管理页面被激活，刷新数据')
  fetchDeptList()
  fetchDeptTree()
})
</script>

<style scoped>
.dept-manage-container {
  padding: 10px;
}

.search-area {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.action-area {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.operation-buttons {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
  justify-content: center;
}

.operation-buttons .el-button {
  margin-left: 0;
  margin-right: 0;
  margin-bottom: 5px;
}

.empty-data {
  padding: 30px 0;
  text-align: center;
}

.empty-data p {
  margin-bottom: 10px;
  color: #909399;
}

.empty-data p:first-child {
  font-size: 16px;
  font-weight: bold;
}
</style> 