<template>
  <div class="role-manage-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="searchForm.roleCode" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="角色状态" clearable  style="width: 120px">
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
      <el-button type="primary" @click="handleAdd">新增</el-button>
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
      <el-table-column prop="roleId" label="角色编号" width="80" />
      <el-table-column prop="roleName" label="角色名称" width="120" />
      <el-table-column prop="roleCode" label="角色编码" width="120" />
      <el-table-column prop="dataScope" label="数据范围" width="120">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.dataScope === '1'">全部数据权限</el-tag>
          <el-tag type="warning" v-else-if="scope.row.dataScope === '2'">本部门数据权限</el-tag>
          <el-tag type="info" v-else-if="scope.row.dataScope === '3'">仅本人数据权限</el-tag>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
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
                  <el-dropdown-item command="assignPermission">
                    <el-icon><Menu /></el-icon>分配权限
                  </el-dropdown-item>
                  <el-dropdown-item command="assignDataScope">
                    <el-icon><DataLine /></el-icon>数据权限
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
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 角色表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增角色' : '编辑角色'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="roleForm.dataScope" placeholder="请选择数据范围">
            <el-option label="全部数据权限" value="1" />
            <el-option label="本部门数据权限" value="2" />
            <el-option label="仅本人数据权限" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="roleForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="roleForm.description" type="textarea" placeholder="请输入角色描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog v-model="permDialogVisible" title="分配权限" width="500px">
      <el-form label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="selectedRole.roleName" disabled />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-tree
            ref="menuTreeRef"
            :data="menuOptions"
            show-checkbox
            node-key="id"
            :props="{ label: 'label', children: 'children' }"
            default-expand-all
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="permDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPermAssign">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 数据权限对话框 -->
    <el-dialog v-model="dataScopeDialogVisible" title="分配数据权限" width="500px">
      <el-form label-width="100px" :model="dataScopeForm">
        <el-form-item label="角色名称">
          <el-input v-model="selectedRole.roleName" disabled />
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="dataScopeForm.dataScope" placeholder="请选择数据范围">
            <el-option label="全部数据权限" value="1" />
            <el-option label="本部门数据权限" value="2" />
            <el-option label="仅本人数据权限" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据权限" v-if="dataScopeForm.dataScope === '2'">
          <el-tree
            ref="deptTreeRef"
            :data="deptOptions"
            show-checkbox
            node-key="id"
            :props="{ label: 'label', children: 'children' }"
            default-expand-all
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dataScopeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitDataScopeAssign">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, ArrowDown, Menu, DataLine } from '@element-plus/icons-vue'
import { getRoleList, addRole, updateRole, deleteRole, getRoleMenus, assignRoleMenus, updateRoleDataScope } from '@/api/systemManager/role'
import { getMenuList } from '@/api/systemManager/menu'
import { getDeptList } from '@/api/systemManager/dept'

// 搜索表单
const searchForm = reactive({
  roleName: '',
  roleCode: '',
  status: ''
})

// 表格数据
const tableData = ref([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 菜单树形数据
const menuOptions = ref([])

// 部门树形数据
const deptOptions = ref([])

// 选中行
const selectedRow = ref(null)
const selectedRows = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const roleFormRef = ref(null)
const roleForm = reactive({
  roleId: '',
  roleName: '',
  roleCode: '',
  dataScope: '1',
  status: 1,
  description: ''
})

// 表单校验规则
const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  dataScope: [
    { required: true, message: '请选择数据范围', trigger: 'change' }
  ]
}

// 权限分配相关
const permDialogVisible = ref(false)
const selectedRole = ref({})
const menuTreeRef = ref(null)

// 数据权限相关
const dataScopeDialogVisible = ref(false)
const deptTreeRef = ref(null)
const dataScopeForm = reactive({
  dataScope: '1'
})

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchRoleList()
}

// 重置搜索
const resetSearch = () => {
  // 重置搜索表单
  searchForm.roleName = ''
  searchForm.roleCode = ''
  searchForm.status = ''
  handleSearch()
}

// 获取角色列表数据
const fetchRoleList = async () => {
  try {
    const params = {
      pageIndex: currentPage.value,
      pageSize: pageSize.value || 10, // 确保pageSize至少为1
      ...searchForm
    }
    
    console.log('发送角色列表请求参数:', params)
    const response = await getRoleList(params)
    console.log('角色列表原始响应:', JSON.stringify(response))
    
    // 确保数据格式正确
    if (response && response.records) {
      // 确保每个角色对象都有必要的字段
      const processedData = response.records.map(role => {
        // 如果没有状态字段，设置默认值为1（正常）
        if (role.status === undefined) {
          role.status = 1
        }
        // 确保状态是数字类型
        if (typeof role.status === 'string') {
          role.status = parseInt(role.status, 10)
        }
        return role
      })
      
      tableData.value = processedData
      total.value = response.total || 0
      console.log('处理后的角色数据:', JSON.stringify(tableData.value))
    } else {
      console.error('角色数据格式不符合预期:', response)
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
    // 如果发生错误，至少显示空数据而不是崩溃
    tableData.value = []
    total.value = 0
  }
}

// 获取菜单树
const fetchMenuTree = async () => {
  try {
    // 使用菜单列表接口获取菜单数据
    const response = await getMenuList({ pageIndex: 1, pageSize: 500 })
    console.log('角色管理-菜单树原始响应:', response)
    
    // 确保数据格式正确
    if (response && response.records) {
      menuOptions.value = response.records.map(menu => ({
        id: menu.menuId,
        label: menu.menuName,
        children: menu.children ? menu.children.map(child => ({
          id: child.menuId,
          label: child.menuName,
          children: child.children ? child.children.map(grandChild => ({
            id: grandChild.menuId,
            label: grandChild.menuName
          })) : []
        })) : []
      }))
      console.log('角色管理-处理后的菜单树数据:', menuOptions.value)
    } else {
      console.error('角色管理-菜单树数据格式不符合预期:', response)
      menuOptions.value = []
    }
  } catch (error) {
    console.error('获取菜单树失败:', error)
    ElMessage.warning('获取菜单树数据失败，请刷新重试')
    menuOptions.value = []
  }
}

// 获取部门树
const fetchDeptTree = async () => {
  try {
    // 使用部门列表接口获取部门数据
    const response = await getDeptList({ pageIndex: 1, pageSize: 100 })
    console.log('角色管理-部门树原始响应:', response)
    
    // 确保数据格式正确
    if (response && response.records) {
      deptOptions.value = response.records.map(dept => ({
        id: dept.deptId,
        label: dept.deptName,
        children: dept.children ? dept.children.map(child => ({
          id: child.deptId,
          label: child.deptName
        })) : []
      }))
      console.log('角色管理-处理后的部门树数据:', deptOptions.value)
    } else {
      console.error('角色管理-部门树数据格式不符合预期:', response)
      deptOptions.value = []
    }
  } catch (error) {
    console.error('获取部门树失败:', error)
    ElMessage.warning('获取部门数据失败，请刷新重试')
    deptOptions.value = []
  }
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
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
  fetchRoleList()
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
  Object.keys(roleForm).forEach(key => {
    if (key in row) {
      roleForm[key] = row[key]
    }
  })
  dialogVisible.value = true
}

// 处理删除单条记录
const handleRemove = (row) => {
  ElMessageBox.confirm(`确认删除角色"${row.roleName}"吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await deleteRole(row.roleId)
      
      // 检查响应是否包含错误信息
      if (response && response.error) {
        console.error('删除角色失败:', response)
        return
      }
      
      ElMessage.success('删除成功')
      // 立即刷新数据
      await fetchRoleList()
      // 同时刷新菜单和部门数据
      await fetchMenuTree()
      await fetchDeptTree()
    } catch (error) {
      console.error('删除角色失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

// 处理分配权限
const handleAssignPermission = async (row) => {
  selectedRole.value = row
  try {
    // 获取角色已分配的菜单权限
    const menuIds = await getRoleMenus(row.roleId)
    permDialogVisible.value = true
    // 在对话框打开后设置选中节点
    setTimeout(() => {
      menuTreeRef.value?.setCheckedKeys(menuIds || [])
    }, 100)
  } catch (error) {
    console.error('获取角色菜单权限失败:', error)
  }
}

// 处理分配数据权限
const handleAssignDataScope = (row) => {
  selectedRole.value = row
  dataScopeForm.dataScope = row.dataScope
  dataScopeDialogVisible.value = true
}

// 提交表单
const submitForm = () => {
  roleFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (dialogType.value === 'add') {
          // 新增角色
          response = await addRole(roleForm)
        } else {
          // 修改角色
          response = await updateRole(roleForm.roleId, roleForm)
        }
        
        // 检查响应是否包含错误信息
        if (response && response.error) {
          console.error('保存角色失败:', response)
          return
        }
        
        ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功')
        dialogVisible.value = false
        // 立即刷新数据
        await fetchRoleList()
        // 同时刷新菜单和部门数据
        await fetchMenuTree()
        await fetchDeptTree()
      } catch (error) {
        console.error('保存角色失败:', error)
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  roleFormRef.value?.resetFields()
  Object.keys(roleForm).forEach(key => {
    if (key !== 'status' && key !== 'dataScope') {
      roleForm[key] = ''
    } else if (key === 'status') {
      roleForm[key] = 1
    } else {
      roleForm[key] = '1'
    }
  })
}

// 提交权限分配
const submitPermAssign = async () => {
  const checkedKeys = menuTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
  const allKeys = [...checkedKeys, ...halfCheckedKeys]
  
  try {
    const response = await assignRoleMenus(selectedRole.value.roleId, allKeys)
    
    // 检查响应是否包含错误信息
    if (response && response.error) {
      console.error('分配权限失败:', response)
      return
    }
    
    ElMessage.success('权限分配成功')
    permDialogVisible.value = false
    // 立即刷新数据
    await fetchRoleList()
    // 同时刷新菜单数据
    await fetchMenuTree()
  } catch (error) {
    console.error('分配权限失败:', error)
  }
}

// 提交数据权限分配
const submitDataScopeAssign = async () => {
  let deptIds = []
  if (dataScopeForm.dataScope === '2') {
    deptIds = deptTreeRef.value.getCheckedKeys()
  }
  
  try {
    const response = await updateRoleDataScope(selectedRole.value.roleId, {
      dataScope: dataScopeForm.dataScope,
      deptIds
    })
    
    // 检查响应是否包含错误信息
    if (response && response.error) {
      console.error('分配数据权限失败:', response)
      return
    }
    
    ElMessage.success('数据权限分配成功')
    dataScopeDialogVisible.value = false
    // 立即刷新数据
    await fetchRoleList()
    // 同时刷新部门数据
    await fetchDeptTree()
  } catch (error) {
    console.error('分配数据权限失败:', error)
  }
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchRoleList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchRoleList()
}

// 处理命令
const handleCommand = (command, row) => {
  switch (command) {
    case 'assignPermission':
      handleAssignPermission(row)
      break
    case 'assignDataScope':
      handleAssignDataScope(row)
      break
    default:
      break
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchRoleList()
  fetchMenuTree()
  fetchDeptTree()
})

// 组件被激活时刷新数据（从缓存中恢复时）
onActivated(() => {
  console.log('角色管理页面被激活，刷新数据')
  fetchRoleList()
  fetchMenuTree()
  fetchDeptTree()
})
</script>

<style scoped>
.role-manage-container {
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