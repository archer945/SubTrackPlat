<template>
  <div class="menu-manage-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="菜单名称">
          <el-input v-model="searchForm.menuName" placeholder="请输入菜单名称" clearable />
        </el-form-item>
        <el-form-item label="菜单类型">
          <el-select v-model="searchForm.menuType" placeholder="菜单类型" clearable>
            <el-option label="目录" value="M" />
            <el-option label="菜单" value="C" />
            <el-option label="按钮" value="F" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.visible" placeholder="菜单状态" clearable>
            <el-option label="显示" :value="1" />
            <el-option label="隐藏" :value="0" />
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
      <el-button type="info" @click="expandAll">展开/折叠</el-button>
    </div>

    <!-- 表格区域 -->
    <el-table
      ref="menuTableRef"
      :data="tableData"
      border
      row-key="menuId"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      style="width: 100%"
      @row-click="handleRowClick"
      highlight-current-row
    >
      <el-table-column prop="menuId" label="菜单编号" width="80" />
      <el-table-column prop="menuName" label="菜单名称" width="180" />
      <el-table-column prop="icon" label="图标" width="80">
        <template #default="scope">
          <el-icon v-if="scope.row.icon && isValidIcon(scope.row.icon)">
            <component :is="scope.row.icon" />
          </el-icon>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" width="60" />
      <el-table-column prop="path" label="路由地址" min-width="120" />
      <el-table-column prop="component" label="组件路径" min-width="120" />
      <el-table-column prop="perms" label="权限标识" min-width="120" />
      <el-table-column prop="menuType" label="类型" width="80">
        <template #default="scope">
          <el-tag type="primary" v-if="scope.row.menuType === 'M'">目录</el-tag>
          <el-tag type="success" v-else-if="scope.row.menuType === 'C'">菜单</el-tag>
          <el-tag type="warning" v-else-if="scope.row.menuType === 'F'">按钮</el-tag>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column prop="visible" label="可见" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.visible === 1 ? 'success' : 'info'">
            {{ scope.row.visible === 1 ? '显示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="scope">
          <div class="operation-buttons">
            <el-button size="small" type="primary" plain @click.stop="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <el-button size="small" type="success" plain @click.stop="handleAddChild(scope.row)">
              <el-icon><Plus /></el-icon>新增
            </el-button>
            <el-button size="small" type="danger" plain @click.stop="handleRemove(scope.row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </div>
        </template>
      </el-table-column>
      <template #empty>
        <div class="empty-data">
          <p>暂无菜单数据</p>
          <el-button type="primary" @click="refreshTable">刷新数据</el-button>
          <el-button type="success" @click="initTestData">初始化测试数据</el-button>
        </div>
      </template>
    </el-table>

    <!-- 菜单表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增菜单' : '编辑菜单'"
      width="700px"
      @close="resetForm"
    >
      <el-form
        ref="menuFormRef"
        :model="menuForm"
        :rules="menuRules"
        label-width="100px"
      >
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="menuForm.parentId"
            :data="menuOptions"
            :props="{ label: 'menuName', value: 'menuId', children: 'children' }"
            placeholder="请选择上级菜单"
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="菜单类型">
          <el-radio-group v-model="menuForm.menuType">
            <el-radio label="M">目录</el-radio>
            <el-radio label="C">菜单</el-radio>
            <el-radio label="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="menuForm.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="menuForm.orderNum" :min="0" :max="999" />
        </el-form-item>

        <el-form-item label="图标" v-if="menuForm.menuType !== 'F'">
          <el-select v-model="menuForm.icon" placeholder="请选择图标" clearable>
            <el-option v-for="icon in validIconOptions" :key="icon.value" :label="icon.label" :value="icon.value">
              <div style="display: flex; align-items: center;">
                <el-icon><component :is="icon.value" /></el-icon>
                <span style="margin-left: 8px;">{{ icon.label }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="路由地址" prop="path" v-if="menuForm.menuType !== 'F'">
          <el-input v-model="menuForm.path" placeholder="请输入路由地址" />
        </el-form-item>

        <el-form-item label="组件路径" prop="component" v-if="menuForm.menuType === 'C'">
          <el-input v-model="menuForm.component" placeholder="请输入组件路径" />
        </el-form-item>

        <el-form-item label="权限标识" prop="perms" v-if="menuForm.menuType !== 'M'">
          <el-input v-model="menuForm.perms" placeholder="请输入权限标识" />
        </el-form-item>

        <el-form-item label="显示状态" v-if="menuForm.menuType !== 'F'">
          <el-radio-group v-model="menuForm.visible">
            <el-radio :label="1">显示</el-radio>
            <el-radio :label="0">隐藏</el-radio>
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
import { ref, reactive, onMounted, onActivated, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Setting, Menu as MenuIcon, Document, Grid, List, Edit, Delete, Plus } from '@element-plus/icons-vue'
import { getMenuList, addMenu, updateMenu, deleteMenu } from '@/api/systemManager/menu'

// 搜索表单
const searchForm = reactive({
  menuName: '',
  menuType: '',
  visible: ''
})

// 表格数据
const tableData = ref([])

// 菜单树形选择器数据
const menuOptions = ref([])

// 选中行
const selectedRow = ref(null)
const menuTableRef = ref(null)
const isExpand = ref(true)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const menuFormRef = ref(null)
const menuForm = reactive({
  menuId: '',
  menuName: '',
  parentId: 0,
  orderNum: 0,
  path: '',
  component: '',
  perms: '',
  icon: '',
  menuType: 'M',
  visible: 1
})

// 表单校验规则
const menuRules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '请输入路由地址', trigger: 'blur' }
  ],
  component: [
    { required: true, message: '请输入组件路径', trigger: 'blur' }
  ]
}

// 监听菜单类型变化，动态调整校验规则
watch(() => menuForm.menuType, (newVal) => {
  if (newVal === 'F') {
    menuForm.path = ''
    menuForm.component = ''
    menuForm.icon = ''
  }
})

// 处理搜索
const handleSearch = () => {
  fetchMenuList()
}

// 重置搜索
const resetSearch = () => {
  // 重置搜索表单
  searchForm.menuName = ''
  searchForm.menuType = ''
  searchForm.visible = ''
  handleSearch()
}

// 获取菜单列表数据
const fetchMenuList = async () => {
  try {
    const params = {
      pageIndex: 1,
      pageSize: 100, // 菜单通常不需要分页，但仍需提供pageSize参数
      ...searchForm
    }
    
    console.log('发送菜单列表请求参数:', params)
    const response = await getMenuList(params)
    console.log('菜单列表原始响应:', JSON.stringify(response))
    
    // 确保数据格式正确
    if (response && response.records) {
      tableData.value = processMenuData(response.records)
      console.log('处理后的菜单数据:', JSON.stringify(tableData.value))
    } else if (response && response.rows) {
      // 兼容后端返回rows的情况
      tableData.value = processMenuData(response.rows)
      console.log('使用rows字段的菜单数据:', JSON.stringify(tableData.value))
    } else if (response && response.data && response.data.rows) {
      // 兼容后端返回data.rows的情况
      tableData.value = processMenuData(response.data.rows)
      console.log('使用data.rows字段的菜单数据:', JSON.stringify(tableData.value))
    } else {
      // 尝试直接使用response，如果它是数组的话
      if (Array.isArray(response)) {
        tableData.value = processMenuData(response)
        console.log('直接使用response作为数组:', JSON.stringify(tableData.value))
      } else if (response && typeof response === 'object') {
        // 尝试从response中提取可能的数据字段
        const possibleDataFields = ['data', 'list', 'items', 'content'];
        for (const field of possibleDataFields) {
          if (response[field] && Array.isArray(response[field])) {
            tableData.value = processMenuData(response[field]);
            console.log(`使用response.${field}字段:`, JSON.stringify(tableData.value));
            break;
          } else if (response[field] && response[field].rows && Array.isArray(response[field].rows)) {
            tableData.value = processMenuData(response[field].rows);
            console.log(`使用response.${field}.rows字段:`, JSON.stringify(tableData.value));
            break;
          }
        }
        
        if (tableData.value.length === 0) {
          console.error('菜单数据格式不符合预期:', response)
        }
      } else {
        console.error('菜单数据格式不符合预期:', response)
        tableData.value = []
      }
    }

    // 如果没有数据，尝试初始化测试数据
    if (tableData.value.length === 0) {
      console.warn('未获取到菜单数据，考虑初始化测试数据')
    }
  } catch (error) {
    console.error('获取菜单列表失败:', error)
    // 如果发生错误，至少显示空数据而不是崩溃
    tableData.value = []
  }
}

// 处理菜单数据，确保格式正确
const processMenuData = (data) => {
  if (!Array.isArray(data)) return []
  
  const processItem = (item) => {
    // 创建新对象，避免直接修改原始数据
    const newItem = { ...item }
    
    // 处理图标字段
    if (newItem.icon && !isValidIcon(newItem.icon)) {
      newItem.icon = ''
    }
    
    // 确保visible字段是数字类型
    if (newItem.visible !== undefined) {
      if (typeof newItem.visible === 'string') {
        newItem.visible = parseInt(newItem.visible, 10) || 0
      } else if (newItem.visible === null) {
        newItem.visible = 0
      }
    }
    
    // 递归处理子菜单
    if (newItem.children && Array.isArray(newItem.children)) {
      newItem.children = newItem.children.map(child => processItem(child))
    }
    
    return newItem
  }
  
  return data.map(item => processItem(item))
}

// 获取菜单树
const fetchMenuTree = async () => {
  try {
    // 使用菜单列表接口获取菜单数据
    const response = await getMenuList({ pageIndex: 1, pageSize: 500 })
    console.log('菜单树原始响应:', response)
    
    // 确保数据格式正确
    if (response && response.records) {
      menuOptions.value = [
        {
          menuId: 0,
          menuName: '主目录',
          children: processMenuData(response.records)
        }
      ]
      console.log('处理后的菜单树数据:', menuOptions.value)
    } else if (response && response.rows) {
      // 兼容后端返回rows的情况
      menuOptions.value = [
        {
          menuId: 0,
          menuName: '主目录',
          children: processMenuData(response.rows)
        }
      ]
      console.log('使用rows字段的菜单树数据:', menuOptions.value)
    } else if (response && response.data && response.data.rows) {
      // 兼容后端返回data.rows的情况
      menuOptions.value = [
        {
          menuId: 0,
          menuName: '主目录',
          children: processMenuData(response.data.rows)
        }
      ]
      console.log('使用data.rows字段的菜单树数据:', menuOptions.value)
    } else {
      // 尝试直接使用response，如果它是数组的话
      if (Array.isArray(response)) {
        menuOptions.value = [
          {
            menuId: 0,
            menuName: '主目录',
            children: processMenuData(response)
          }
        ]
        console.log('直接使用response作为数组构建树:', menuOptions.value)
      } else if (response && typeof response === 'object') {
        // 尝试从response中提取可能的数据字段
        const possibleDataFields = ['data', 'list', 'items', 'content'];
        for (const field of possibleDataFields) {
          if (response[field] && Array.isArray(response[field])) {
            menuOptions.value = [
              {
                menuId: 0,
                menuName: '主目录',
                children: processMenuData(response[field])
              }
            ];
            console.log(`使用response.${field}字段构建树:`, menuOptions.value);
            break;
          } else if (response[field] && response[field].rows && Array.isArray(response[field].rows)) {
            menuOptions.value = [
              {
                menuId: 0,
                menuName: '主目录',
                children: processMenuData(response[field].rows)
              }
            ];
            console.log(`使用response.${field}.rows字段构建树:`, menuOptions.value);
            break;
          }
        }
        
        if (menuOptions.value.length === 0) {
          console.error('菜单树数据格式不符合预期:', response)
          menuOptions.value = []
        }
      } else {
        console.error('菜单树数据格式不符合预期:', response)
        menuOptions.value = []
      }
    }

    // 如果没有获取到菜单树数据，创建一个默认的空树
    if (menuOptions.value.length === 0) {
      menuOptions.value = [
        {
          menuId: 0,
          menuName: '主目录',
          children: []
        }
      ]
      console.warn('未获取到菜单树数据，使用默认空树')
    }
  } catch (error) {
    console.error('获取菜单树失败:', error)
    ElMessage.warning('获取菜单树数据失败，请刷新重试')
    menuOptions.value = [
      {
        menuId: 0,
        menuName: '主目录',
        children: []
      }
    ]
  }
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  menuForm.parentId = 0
  dialogVisible.value = true
}

// 处理新增子菜单
const handleAddChild = (row) => {
  dialogType.value = 'add'
  menuForm.parentId = row.menuId
  
  // 根据父菜单类型设置默认的子菜单类型
  if (row.menuType === 'M') {
    menuForm.menuType = 'C'
  } else if (row.menuType === 'C') {
    menuForm.menuType = 'F'
  }
  
  dialogVisible.value = true
}

// 刷新表格
const refreshTable = () => {
  fetchMenuList()
}

// 展开/折叠所有
const expandAll = () => {
  isExpand.value = !isExpand.value
  toggleExpand(tableData.value, isExpand.value)
}

// 递归切换展开状态
const toggleExpand = (data, expand) => {
  data.forEach(item => {
    menuTableRef.value.toggleRowExpansion(item, expand)
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
const handleEdit = (row) => {
  dialogType.value = 'edit'
  // 填充表单数据
  Object.keys(menuForm).forEach(key => {
    if (key in row) {
      menuForm[key] = row[key]
    }
  })
  dialogVisible.value = true
}

// 处理删除单条记录
const handleRemove = (row) => {
  // 检查是否有子菜单
  if (row.children && row.children.length > 0) {
    ElMessage.warning('该菜单下存在子菜单，请先删除子菜单')
    return
  }
  
  ElMessageBox.confirm(`确认删除菜单"${row.menuName}"吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMenu(row.menuId)
      ElMessage.success('删除成功')
      // 立即刷新数据
      await fetchMenuList()
      // 同时刷新菜单树
      await fetchMenuTree()
    } catch (error) {
      console.error('删除菜单失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单
const submitForm = () => {
  menuFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          // 新增菜单
          await addMenu(menuForm)
          ElMessage.success('添加成功')
        } else {
          // 修改菜单
          await updateMenu(menuForm.menuId, menuForm)
          ElMessage.success('修改成功')
        }
        
        dialogVisible.value = false
        // 立即刷新数据
        await fetchMenuList()
        // 同时刷新菜单树
        await fetchMenuTree()
      } catch (error) {
        console.error('保存菜单失败:', error)
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  menuFormRef.value?.resetFields()
  Object.keys(menuForm).forEach(key => {
    if (key === 'orderNum') {
      menuForm[key] = 0
    } else if (key === 'parentId') {
      menuForm[key] = 0
    } else if (key === 'menuType') {
      menuForm[key] = 'M'
    } else if (key === 'visible') {
      menuForm[key] = 1
    } else {
      menuForm[key] = ''
    }
  })
}

// 初始化测试数据
const initTestData = () => {
  tableData.value = [
    {
      menuId: 1,
      menuName: '系统管理',
      parentId: 0,
      orderNum: 1,
      path: '/system',
      component: null,
      perms: null,
      icon: 'Setting',
      menuType: 'M',
      visible: 1,
      children: [
        {
          menuId: 2,
          menuName: '用户管理',
          parentId: 1,
          orderNum: 1,
          path: 'user',
          component: 'system/user/index',
          perms: 'system:user:list',
          icon: 'User',
          menuType: 'C',
          visible: 1,
          children: [
            {
              menuId: 7,
              menuName: '用户查询',
              parentId: 2,
              orderNum: 1,
              path: null,
              component: null,
              perms: 'system:user:query',
              icon: null,
              menuType: 'F',
              visible: 1,
              children: null
            }
          ]
        },
        {
          menuId: 3,
          menuName: '角色管理',
          parentId: 1,
          orderNum: 2,
          path: 'role',
          component: 'system/role/index',
          perms: 'system:role:list',
          icon: 'UserFilled',
          menuType: 'C',
          visible: 1,
          children: null
        }
      ]
    }
  ]
  
  // 同时更新菜单树
  menuOptions.value = [
    {
      menuId: 0,
      menuName: '主目录',
      children: tableData.value
    }
  ]
  
  ElMessage.success('已初始化测试数据')
}

// 组件挂载时获取数据
onMounted(() => {
  fetchMenuList()
  fetchMenuTree()
})

// 组件被激活时刷新数据（从缓存中恢复时）
onActivated(() => {
  console.log('菜单管理页面被激活，刷新数据')
  fetchMenuList()
  fetchMenuTree()
})

// 验证图标是否有效
const isValidIcon = (iconName) => {
  if (!iconName || typeof iconName !== 'string') return false
  
  // 检查是否包含无效字符，如斜杠
  if (iconName.includes('/') || iconName.includes('\\')) return false
  
  // 检查是否为已导入的图标组件
  const validIcons = [
    'User', 'Setting', 'Menu', 'Document', 'Grid', 'List',
    'Edit', 'Delete', 'Plus', 'MenuIcon'
  ]
  
  return validIcons.includes(iconName)
}

// 有效图标选项
const validIconOptions = [
  { label: '用户', value: 'User' },
  { label: '设置', value: 'Setting' },
  { label: '菜单', value: 'MenuIcon' },
  { label: '文档', value: 'Document' },
  { label: '表格', value: 'Grid' },
  { label: '列表', value: 'List' }
]
</script>

<style scoped>
.menu-manage-container {
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