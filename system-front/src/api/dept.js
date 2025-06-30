import request from './index'

// 获取部门列表
export function getDeptList(params) {
  // 确保params中包含pageIndex参数
  const newParams = { pageIndex: 1, ...params };
  
  // 处理status参数，确保是数字或null
  if (newParams.status === '') {
    newParams.status = null;
  } else if (newParams.status !== undefined && newParams.status !== null) {
    newParams.status = Number(newParams.status);
  }
  
  return request({
    url: '/systemManager/dept',
    method: 'get',
    params: newParams
  })
}

// 获取部门树形结构
// export function getDeptTree() {
//   return request({
//     url: '/systemManager/dept/tree',
//     method: 'get'
//   })
// }

// 获取部门详情
export function getDeptDetail(id) {
  return request({
    url: `/systemManager/dept/${id}`,
    method: 'get'
  })
}

// 新增部门
export function addDept(data) {
  // 确保status是数字类型
  const newData = { ...data };
  if (newData.status !== undefined) {
    newData.status = Number(newData.status);
  }
  
  return request({
    url: '/systemManager/dept',
    method: 'post',
    data: newData
  })
}

// 更新部门
export function updateDept(id, data) {
  // 确保status是数字类型
  const newData = { ...data };
  if (newData.status !== undefined) {
    newData.status = Number(newData.status);
  }
  
  return request({
    url: `/systemManager/dept/${id}`,
    method: 'put',
    data: newData
  })
}

// 删除部门
export function deleteDept(id) {
  return request({
    url: `/systemManager/dept/${id}`,
    method: 'delete'
  })
} 