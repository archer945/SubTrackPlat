import request from '../index'

// 获取菜单列表
export function getMenuList(params) {
  // 确保params中包含pageIndex参数
  const newParams = { pageIndex: 1, ...params };
  
  // 处理visible参数，确保是数字或null
  if (newParams.visible === '') {
    newParams.visible = null;
  } else if (newParams.visible !== undefined && newParams.visible !== null) {
    newParams.visible = Number(newParams.visible);
  }
  
  return request({
    url: '/systemManager/systemManager/menu',
    method: 'get',
    params: newParams
  })
}

// 获取菜单树形结构
// export function getMenuTree() {
//   return request({
//     url: '/systemManager/menu/tree',
//     method: 'post'
//   })
// }

// 获取菜单详情
export function getMenuDetail(id) {
  return request({
    url: `/systemManager/systemManager/menu/${id}`,
    method: 'get'
  })
}

// 新增菜单
export function addMenu(data) {
  return request({
    url: '/systemManager/systemManager/menu',
    method: 'post',
    data
  })
}

// 更新菜单
export function updateMenu(id, data) {
  return request({
    url: `/systemManager/systemManager/menu/${id}`,
    method: 'put',
    data
  })
}

// 删除菜单
export function deleteMenu(id) {
  return request({
    url: `/systemManager/systemManager/menu/${id}`,
    method: 'delete'
  })
} 

// 获取当前用户的菜单
export function getCurrentUserMenus() {
  return request({
    url: '/systemManager/systemManager/menu/user/menus',
    method: 'get'
  })
}

// 根据用户ID获取菜单
export function getMenusByUserId(userId) {
  return request({
    url: `/systemManager/systemManager/menu/user/${userId}`,
    method: 'get'
  })
} 