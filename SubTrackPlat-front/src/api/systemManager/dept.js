import request from '../index'

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
    url: '/systemManager/systemManager/dept',
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

// 获取部门树
export function getDeptTree() {
  return request({
    url: '/systemManager/systemManager/dept/tree',
    method: 'get'
  }).then(response => {
    // 直接返回部门树数据
    if (response && response.data) {
      return response.data;
    }
    return [];
  });
}

// 获取部门详情
export function getDeptDetail(id) {
  return request({
    url: `/systemManager/systemManager/dept/${id}`,
    method: 'get'
  })
}

// 获取部门人员数量
export function getDeptUserCount(deptId) {
  return request({
    url: `/systemManager/systemManager/dept/${deptId}/user/count`,
    method: 'get'
  }).then(response => {
    if (response && response.data !== undefined) {
      return response.data;
    }
    return 0;
  });
}

// 获取部门人员列表
export function getDeptUsers(deptId, params) {
  const newParams = { pageIndex: 1, pageSize: 10, ...params };
  
  return request({
    url: `/systemManager/systemManager/dept/${deptId}/users`,
    method: 'get',
    params: newParams
  }).then(response => {
    // 转换响应数据格式
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

// 新增部门
export function addDept(data) {
  // 确保status是数字类型
  const newData = { ...data };
  if (newData.status !== undefined) {
    newData.status = Number(newData.status);
  }
  
  return request({
    url: '/systemManager/systemManager/dept',
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
    url: `/systemManager/systemManager/dept/${id}`,
    method: 'put',
    data: newData
  })
}

// 删除部门
export function deleteDept(id) {
  return request({
    url: `/systemManager/systemManager/dept/${id}`,
    method: 'delete'
  })
} 