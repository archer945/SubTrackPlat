import request from '../index'

// 获取角色列表
export function getRoleList(params) {
  // 确保params中包含pageIndex参数
  const newParams = { pageIndex: 1, ...params };
  
  return request({
    url: '/systemManager/systemManager/role',
    method: 'get',
    params: newParams
  }).then(response => {
    // 转换响应数据格式，使其符合前端组件的预期
    if (response && response.data && response.data.rows) {
      return {
        records: response.data.rows,
        total: response.data.total,
        pageSize: response.data.pageSize,
        pageIndex: response.data.pageIndex,
        pages: response.data.pages
      };
    }
    return { records: [], total: 0 };
  });
}

// 获取角色详情
export function getRoleDetail(id) {
  return request({
    url: `/systemManager/systemManager/role/${id}`,
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/systemManager/systemManager/role',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/systemManager/systemManager/role/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/systemManager/systemManager/role/${id}`,
    method: 'delete'
  })
}

// 获取角色菜单权限
export function getRoleMenus(roleId) {
  return request({
    url: `/systemManager/systemManager/role/${roleId}/menus`,
    method: 'get'
  }).then(response => {
    // 打印原始响应以便调试
    console.log('getRoleMenus原始响应:', response);
    
    // 确保返回的是数组
    if (response && Array.isArray(response.data)) {
      return response.data;
    }
    
    // 尝试不同的数据结构解析
    if (response && response.data && Array.isArray(response.data.menuIds)) {
      return response.data.menuIds;
    }
    
    // 尝试从data.data中获取
    if (response && response.data && response.data.data && Array.isArray(response.data.data)) {
      return response.data.data;
    }
    
    // 检查是否直接包含id属性的对象数组
    if (response && response.data && Array.isArray(response.data)) {
      // 提取所有id字段
      const ids = response.data
        .filter(item => item && (item.menuId || item.id))
        .map(item => item.menuId || item.id);
      return ids;
    }
    
    console.warn('角色菜单权限数据格式异常:', response);
    return [];
  });
}

// 分配角色菜单权限
export function assignRoleMenus(roleId, menuIds) {
  return request({
    url: `/systemManager/systemManager/role/${roleId}/menus`,
    method: 'put',
    data: { menuIds }
  })
}

// 更新角色数据权限
export function updateRoleDataScope(roleId, data) {
  return request({
    url: `/systemManager/systemManager/role/${roleId}/dataScope`,
    method: 'put',
    data
  })
}

// 获取角色关联的用户列表
export function getRoleUsers(roleId, params) {
  // 确保params中包含pageIndex参数
  const newParams = { pageIndex: 1, pageSize: 10, ...params };
  
  return request({
    url: `/systemManager/systemManager/role/${roleId}/users`,
    method: 'get',
    params: newParams
  }).then(response => {
    // 转换响应数据格式，使其符合前端组件的预期
    if (response && response.data && response.data.rows) {
      return {
        records: response.data.rows,
        total: response.data.total,
        pageSize: response.data.pageSize,
        pageIndex: response.data.pageIndex,
        pages: response.data.pages
      };
    }
    return { records: [], total: 0 };
  });
} 

// 复制角色
export function copyRole(sourceRoleId, data) {
  return request({
    url: `/systemManager/systemManager/role/copy/${sourceRoleId}`,
    method: 'post',
    data
  })
} 