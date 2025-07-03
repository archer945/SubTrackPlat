import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // API的基础URL，会被代理到后端服务器
  timeout: 10000 // 请求超时时间
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    console.log('发送请求:', config.method.toUpperCase(), config.url, config.params || config.data)
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 如果响应类型是blob（文件下载），直接返回响应数据
    if (response.config.responseType === 'blob') {
      // 检查是否是空的blob或错误响应
      if (response.data.size === 0) {
        ElMessage.error('导出失败：返回的文件为空')
        return Promise.reject(new Error('返回的文件为空'))
      }
      return response.data
    }
    
    const res = response.data
    console.log('接收响应:', response.config.url, JSON.stringify(res))
    
    // 检查响应中是否包含错误信息，即使状态码是200
    if (res.data && typeof res.data === 'string' && (res.data.includes('错误') || res.data.includes('失败') || res.data.includes('不能'))) {
      console.error('请求返回错误信息:', res.data)
      ElMessage.error(res.data)
      return {
        error: true,
        code: res.code,
        message: res.message || '请求失败',
        data: res.data
      }
    }
    
    // 根据后端接口规范判断请求是否成功
    if (res.code === 10000) {
      // 直接返回数据，不做过多处理，让组件自己处理数据格式
      console.log('响应成功，返回原始数据')
      
      // 如果是分页数据，返回标准格式
      if (res.data && res.data.rows !== undefined) {
        // 处理状态字段，确保是数字类型
        const records = res.data.rows.map(item => {
          // 处理状态字段
          if (item.status !== undefined && typeof item.status === 'string') {
            item.status = parseInt(item.status, 10);
          }
          // 处理visible字段
          if (item.visible !== undefined && typeof item.visible === 'string') {
            item.visible = parseInt(item.visible, 10);
          }
          // 处理创建时间字段
          if (item.createTime && typeof item.createTime === 'string') {
            // 如果是时间戳格式，进行转换
            if (/^\d+$/.test(item.createTime)) {
              const timestamp = parseInt(item.createTime);
              if (!isNaN(timestamp)) {
                const date = new Date(timestamp);
                item.createTime = date.toLocaleString();
              }
            }
            // 如果日期格式不正确或为空，保持原样
          }
          
          // 处理creatTime字段(后端字段名可能少了一个e)
          if (item.creatTime && !item.createTime) {
            // 将creatTime映射到createTime
            item.createTime = item.creatTime;
            // 如果是时间戳格式，进行转换
            if (typeof item.createTime === 'string' && /^\d+$/.test(item.createTime)) {
              const timestamp = parseInt(item.createTime);
              if (!isNaN(timestamp)) {
                const date = new Date(timestamp);
                item.createTime = date.toLocaleString();
              }
            }
          }
          
          return item;
        });
        
        // 返回兼容格式，同时提供records和rows
        return {
          records: records || [],
          rows: records || [],
          total: res.data.total || 0,
          pageSize: res.data.pageSize,
          pageIndex: res.data.pageIndex,
          pages: res.data.pages,
          data: res.data // 同时保留原始data字段
        };
      }
      
      // 特殊处理菜单数据
      if (response.config.url.includes('/systemManager/systemManager/menu') && res.data) {
        console.log('处理菜单数据:', res.data);
        // 确保返回的菜单数据格式一致
        if (res.data.rows) {
          return {
            records: res.data.rows || [],
            rows: res.data.rows || [],
            total: res.data.total || 0,
            pageSize: res.data.pageSize,
            pageIndex: res.data.pageIndex,
            pages: res.data.pages
          };
        }
      }
      
      // 特殊处理部门数据
      if (response.config.url.includes('/systemManager/systemManager/dept') && res.data) {
        console.log('处理部门数据:', res.data);
        // 确保返回的部门数据格式一致
        if (res.data.rows) {
          // 处理状态字段，确保是数字类型
          const processedRows = res.data.rows.map(item => {
            const newItem = { ...item };
            // 处理状态字段
            if (newItem.status !== undefined) {
              newItem.status = Number(newItem.status);
            } else {
              // 如果status字段不存在或为undefined，设置默认值为0（停用）
              newItem.status = 0;
            }
            // 递归处理子部门
            if (newItem.children && Array.isArray(newItem.children)) {
              newItem.children = processChildren(newItem.children);
            }
            return newItem;
          });
          
          function processChildren(children) {
            return children.map(child => {
              const newChild = { ...child };
              if (newChild.status !== undefined) {
                newChild.status = Number(newChild.status);
              } else {
                // 如果status字段不存在或为undefined，设置默认值为0（停用）
                newChild.status = 0;
              }
              if (newChild.children && Array.isArray(newChild.children)) {
                newChild.children = processChildren(newChild.children);
              }
              return newChild;
            });
          }
          
          return {
            records: processedRows || [],
            rows: processedRows || [],
            total: res.data.total || 0,
            pageSize: res.data.pageSize,
            pageIndex: res.data.pageIndex,
            pages: res.data.pages
          };
        }
      }
      
      // 处理单个对象的日期格式
      if (res.data && typeof res.data === 'object' && !Array.isArray(res.data)) {
        if (res.data.createTime && typeof res.data.createTime === 'string') {
          // 如果是时间戳格式，进行转换
          if (/^\d+$/.test(res.data.createTime)) {
            const timestamp = parseInt(res.data.createTime);
            if (!isNaN(timestamp)) {
              const date = new Date(timestamp);
              res.data.createTime = date.toLocaleString();
            }
          }
        }
      }
      
      // 直接返回数据
      return res.data;
    } else {
      console.error('请求失败，错误码:', res.code, '错误信息:', res.message, '错误数据:', res.data)
      
      // 优先使用data字段作为错误提示，如果data字段不存在则使用message字段
      const errorMessage = res.data || res.message || '请求失败'
      ElMessage.error(errorMessage)
      
      // 返回错误信息，但不中断处理流程，让组件自己处理错误
      return {
        error: true,
        code: res.code,
        message: res.message || '请求失败',
        data: res.data
      }
    }
  },
  error => {
    let message = '网络错误，请稍后重试'
    let errorData = null
    
    if (error.response) {
      console.error('响应错误状态码:', error.response.status)
      console.error('响应错误数据:', error.response.data)
      
      // 尝试从错误响应中获取更详细的错误信息
      if (error.response.data) {
        if (error.response.data.data) {
          errorData = error.response.data.data
          message = errorData || message
        } else if (error.response.data.message) {
          message = error.response.data.message
        }
      }
      
      switch (error.response.status) {
        case 401:
          message = message || '未授权，请重新登录'
          // 可以在这里处理登出逻辑
          break
        case 403:
          message = message || '拒绝访问'
          break
        case 404:
          message = message || '请求的资源不存在'
          break
        case 500:
          message = message || '服务器内部错误'
          break
        default:
          message = message || `请求失败: ${error.response.status}`
      }
    } else {
      console.error('请求错误:', error.message)
    }
    
    ElMessage.error(message)
    console.error('响应错误:', error)
    
    // 返回错误信息，但不中断处理流程，让组件自己处理错误
    return {
      error: true,
      message: message,
      data: errorData
    }
  }
)

export default request 