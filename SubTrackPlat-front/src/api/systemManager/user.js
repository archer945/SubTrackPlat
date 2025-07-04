import request from '../index'

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/systemManager/systemManager/user',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserDetail(id) {
  return request({
    url: `/systemManager/systemManager/user/${id}`,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/systemManager/systemManager/user',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/systemManager/systemManager/user/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/systemManager/systemManager/user/${id}`,
    method: 'delete'
  })
}

// 重置用户密码
export function resetUserPassword(id, data) {
  return request({
    url: `/systemManager/systemManager/user/${id}/password/reset`,
    method: 'put',
    data
  })
}

// 获取用户角色
export function getUserRoles(id) {
  return request({
    url: `/systemManager/systemManager/user/${id}/roles`,
    method: 'get'
  })
}

// 分配用户角色
export function assignUserRoles(id, roleIds) {
  return request({
    url: `/systemManager/systemManager/user/roles`,
    method: 'put',
    data: { userId: id, roleIds }
  })
}

// 批量删除用户
export function batchDeleteUsers(userIds) {
  return request({
    url: '/systemManager/systemManager/user/batch',
    method: 'delete',
    data: { userIds }
  })
}

// 导入用户
export function importUsers(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/systemManager/systemManager/user/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出用户
export function exportUsers(params) {
  return request({
    url: '/systemManager/systemManager/user/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
} 