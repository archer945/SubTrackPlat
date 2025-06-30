<template>
  <div class="user-manage-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-card class="search-wrapper">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="手机号码">
            <el-input v-model="searchForm.tel" placeholder="请输入手机号码" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
              <el-option label="失效" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="searchForm.createTime"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-area">
      <el-button type="primary" @click="handleAdd">新增</el-button>
      <el-button type="warning" @click="handleImport">导入</el-button>
      <el-button type="info" @click="handleExport">导出</el-button>
      <el-button @click="refreshTable">刷新</el-button>
    </div>

    <!-- 表格区域 -->
    <el-table
      :data="tableData"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
      @row-click="handleRowClick"
      highlight-current-row
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="userId" label="用户编号" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="用户昵称" width="120" />
      <el-table-column prop="deptName" label="部门" width="120" />
      <el-table-column prop="tel" label="手机号码" width="120" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="scope">
          <el-tag
            :type="getStatusType(scope.row.status)"
            disable-transitions
          >
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime || scope.row.creatTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="280">
        <template #default="scope">
          <div class="operation-buttons">
            <el-button size="small" type="primary" plain @click.stop="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <el-button size="small" type="danger" plain @click.stop="handleRemove(scope.row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
            <el-dropdown trigger="click" @command="(command) => handleCommand(command, scope.row)">
              <el-button size="small" type="info" plain>
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="resetPwd">
                    <el-icon><Key /></el-icon>重置密码
                  </el-dropdown-item>
                  <el-dropdown-item command="assignRole">
                    <el-icon><UserFilled /></el-icon>分配角色
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页区域 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名称" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="用户昵称" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入用户昵称" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="userForm.password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="手机号码" prop="tel">
          <el-input v-model="userForm.tel" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-tree-select
            v-model="userForm.deptId"
            :data="deptOptions"
            placeholder="请选择部门"
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
            <el-radio :label="2">失效</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="userForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px">
      <el-form label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="selectedUser.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="selectedRoles">
            <el-checkbox
              v-for="role in roleOptions"
              :key="role.roleId"
              :label="role.roleId"
            >{{ role.roleName }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRoleAssign">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, ArrowDown, Key, UserFilled, Search, Refresh } from '@element-plus/icons-vue'
import { getUserList, addUser, updateUser, deleteUser, resetUserPassword, getUserRoles, assignUserRoles } from '@/api/user'
import { getDeptList } from '@/api/dept'
import { getRoleList } from '@/api/role'

// 搜索表单
const searchForm = reactive({
  username: '',
  tel: '',
  status: '',
  createTime: []
})

// 表格数据
const tableData = ref([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 部门树形选择器数据
const deptOptions = ref([])

// 角色选项
const roleOptions = ref([])

// 选中行
const selectedRow = ref(null)
const selectedRows = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const userFormRef = ref(null)
const userForm = reactive({
  userId: '',
  username: '',
  password: '',
  realName: '',
  email: '',
  tel: '',
  deptId: '',
  status: 1,
  remark: ''
})

// 表单校验规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  tel: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  deptId: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ]
}

// 角色分配相关
const roleDialogVisible = ref(false)
const selectedUser = ref({})
const selectedRoles = ref([])

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchUserList()
}

// 重置搜索
const resetSearch = () => {
  // 重置搜索表单
  searchForm.username = ''
  searchForm.tel = ''
  searchForm.status = ''
  searchForm.createTime = []
  handleSearch()
}

// 获取用户列表数据
const fetchUserList = async () => {
  try {
    const params = {
      pageIndex: currentPage.value,
      pageSize: pageSize.value || 10, // 确保pageSize至少为1
      ...searchForm
    }
    
    if (searchForm.createTime && searchForm.createTime.length === 2) {
      params.beginTime = searchForm.createTime[0]
      params.endTime = searchForm.createTime[1]
      delete params.createTime
    }
    
    const response = await getUserList(params)
    console.log('用户列表响应数据:', response)
    
    // 确保状态字段是数字类型
    if (response.records && response.records.length > 0) {
      response.records.forEach(item => {
        // 处理状态字段
        if (item.status !== undefined && typeof item.status === 'string') {
          item.status = parseInt(item.status, 10)
        }
        
        // 处理创建时间字段 - 注意后端返回的是creatTime而不是createTime
        if (item.creatTime) {
          // 将creatTime映射到createTime
          item.createTime = item.creatTime
          // 确保创建时间是正确的格式
          item.createTime = formatDateTime(item.createTime)
        }
        
        console.log(`用户 ${item.username} 的状态:`, item.status, typeof item.status)
      })
    }
    
    tableData.value = response.records || []
    total.value = response.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
    // 如果发生错误，至少显示空数据而不是崩溃
    tableData.value = []
    total.value = 0
  }
}

// 获取部门树
const fetchDeptTree = async () => {
  try {
    // 使用部门列表接口获取部门数据
    const response = await getDeptList({ pageIndex: 1, pageSize: 100 })
    console.log('用户管理-部门树原始响应:', response)
    
    // 确保数据格式正确
    if (response && response.records) {
      deptOptions.value = [
        {
          value: 0,
          label: '主公司',
          children: response.records.map(dept => ({
            value: dept.deptId,
            label: dept.deptName,
            children: dept.children ? dept.children.map(child => ({
              value: child.deptId,
              label: child.deptName
            })) : []
          }))
        }
      ]
      console.log('用户管理-处理后的部门树数据:', deptOptions.value)
    } else {
      console.error('用户管理-部门树数据格式不符合预期:', response)
      deptOptions.value = []
    }
  } catch (error) {
    console.error('获取部门树失败:', error)
    ElMessage.warning('获取部门数据失败，请刷新重试')
    deptOptions.value = []
  }
}

// 获取角色列表
const fetchRoleList = async () => {
  try {
    const response = await getRoleList({ pageIndex: 1, pageSize: 100 })
    roleOptions.value = response.records || []
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.warning('获取角色数据失败，请刷新重试')
    roleOptions.value = []
  }
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
}

// 处理修改
const handleModify = () => {
  if (!selectedRow.value) {
    ElMessage.warning('请先选择一条记录')
    return
  }
  dialogType.value = 'edit'
  // 填充表单数据
  Object.keys(userForm).forEach(key => {
    if (key in selectedRow.value) {
      if (key === 'status' && typeof selectedRow.value[key] === 'string') {
        userForm[key] = parseInt(selectedRow.value[key], 10)
      } else {
        userForm[key] = selectedRow.value[key]
      }
    }
  })
  dialogVisible.value = true
}

// 处理删除
const handleDelete = () => {
  if (!selectedRow.value) {
    ElMessage.warning('请先选择一条记录')
    return
  }
  handleRemove(selectedRow.value)
}

// 处理导入
const handleImport = () => {
  ElMessage.info('导入功能待实现')
}

// 处理导出
const handleExport = () => {
  ElMessage.info('导出功能待实现')
}

// 刷新表格
const refreshTable = () => {
  fetchUserList()
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
  selectedRow.value = selection.length === 1 ? selection[0] : null
}

// 处理行点击
const handleRowClick = (row) => {
  selectedRow.value = row
}

// 处理编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  // 填充表单数据
  Object.keys(userForm).forEach(key => {
    if (key in row) {
      if (key === 'status' && typeof row[key] === 'string') {
        userForm[key] = parseInt(row[key], 10)
      } else {
        userForm[key] = row[key]
      }
    }
  })
  dialogVisible.value = true
}

// 处理删除单条记录
const handleRemove = (row) => {
  ElMessageBox.confirm(`确认删除用户"${row.username}"吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.userId)
      ElMessage.success('删除成功')
      // 立即刷新数据
      await fetchUserList()
      // 同时刷新部门和角色数据
      await fetchDeptTree()
      await fetchRoleList()
    } catch (error) {
      console.error('删除用户失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

// 处理重置密码
const handleResetPwd = (row) => {
  ElMessageBox.confirm(`确认重置"${row.username}"的密码吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await resetUserPassword(row.userId)
      ElMessage.success('密码重置成功')
      // 立即刷新数据
      await fetchUserList()
    } catch (error) {
      console.error('重置密码失败:', error)
    }
  }).catch(() => {
    // 取消重置
  })
}

// 处理分配角色
const handleAssignRole = async (row) => {
  selectedUser.value = row
  try {
    const roles = await getUserRoles(row.userId)
    selectedRoles.value = roles.map(role => role.roleId)
    roleDialogVisible.value = true
  } catch (error) {
    console.error('获取用户角色失败:', error)
  }
}

// 提交表单
const submitForm = () => {
  userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 确保状态是数字类型
        if (typeof userForm.status === 'string') {
          userForm.status = parseInt(userForm.status, 10)
        }
        
        if (dialogType.value === 'add') {
          // 新增用户
          await addUser(userForm)
          ElMessage.success('添加成功')
        } else {
          // 修改用户
          await updateUser(userForm.userId, userForm)
          ElMessage.success('修改成功')
        }
        
        dialogVisible.value = false
        // 立即刷新数据
        await fetchUserList()
        // 同时刷新部门和角色数据
        await fetchDeptTree()
        await fetchRoleList()
      } catch (error) {
        console.error('保存用户失败:', error)
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  userFormRef.value?.resetFields()
  Object.keys(userForm).forEach(key => {
    if (key !== 'status') {
      userForm[key] = ''
    } else {
      userForm[key] = 1
    }
  })
}

// 提交角色分配
const submitRoleAssign = async () => {
  try {
    await assignUserRoles(selectedUser.value.userId, selectedRoles.value)
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
    // 立即刷新数据
    await fetchUserList()
    // 同时刷新角色数据
    await fetchRoleList()
  } catch (error) {
    console.error('分配角色失败:', error)
  }
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchUserList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchUserList()
}

// 添加处理下拉菜单命令的函数
const handleCommand = (command, row) => {
  if (command === 'resetPwd') {
    handleResetPwd(row)
  } else if (command === 'assignRole') {
    handleAssignRole(row)
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const statusNum = typeof status === 'string' ? parseInt(status, 10) : status
  if (statusNum === 1) return 'success'
  if (statusNum === 0) return 'danger'
  return 'info' // 状态为2或其他值
}

// 获取状态文本
const getStatusText = (status) => {
  const statusNum = typeof status === 'string' ? parseInt(status, 10) : status
  if (statusNum === 1) return '正常'
  if (statusNum === 0) return '禁用'
  return '失效' // 状态为2或其他值
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  
  // 如果是数字字符串（时间戳），转换为数字
  if (typeof dateTime === 'string' && /^\d+$/.test(dateTime)) {
    dateTime = parseInt(dateTime, 10)
  }
  
  try {
    // 如果是已经格式化好的日期字符串（如：2025-06-23T18:09:19），直接返回格式化后的结果
    if (typeof dateTime === 'string' && dateTime.includes('-') && (dateTime.includes('T') || dateTime.includes(':'))) {
      // 尝试解析日期
      const date = new Date(dateTime)
      if (!isNaN(date.getTime())) {
        // 格式化为 yyyy-MM-dd HH:mm:ss
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')
        
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      }
      
      // 如果无法解析，返回原始字符串，去掉T字符
      return dateTime.replace('T', ' ')
    }
    
    const date = new Date(dateTime)
    // 检查日期是否有效
    if (isNaN(date.getTime())) return dateTime
    
    // 格式化为 yyyy-MM-dd HH:mm:ss
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  } catch (error) {
    console.error('日期格式化错误:', error)
    return dateTime || '-'
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchUserList()
  fetchDeptTree()
  fetchRoleList()
})

// 组件被激活时刷新数据（从缓存中恢复时）
onActivated(() => {
  console.log('用户管理页面被激活，刷新数据')
  fetchUserList()
  fetchDeptTree()
  fetchRoleList()
})
</script>

<style scoped>
.user-manage-container {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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
</style> 