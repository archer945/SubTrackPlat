import request from '../index'

// 获取角色列表
export function getRoleList(params) {
  // 确保params中包含pageIndex参数
  const newParams = { pageIndex: 1, ...params };
  
  return request({
    url: '/systemManager/systemManager/role',
    method: 'get',
    params: newParams
  })
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
  })
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