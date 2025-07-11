import request from '@/utils/request'

/**
 * 缺陷概览数据
 */
export const getDefectOverview = () => {
  console.log('调用API: /dashboard/defect-overview')
  return request({
    url: '/dashboard/defect-overview',
    method: 'get'
  })
}

/**
 * 缺陷统计
 */
export const getDefectStats = (params) => {
  console.log('调用API: /dashboard/defect-stats')
  return request({
    url: '/dashboard/defect-stats',
    method: 'get',
    params // GET查询参数
  })
}

/**
 * 巡检汇总数据
 */
export const getInspectSummary = async () => {
  console.log('调用API: /dashboard/inspectSummary')
  try {
    console.log('开始请求巡检汇总数据')
    const response = await request({
      url: '/dashboard/inspectSummary',
      method: 'get'
    })
    console.log('巡检汇总数据请求成功:', response)
    
    // 检查是否是标准接口返回格式
    if (response && response.code === 10000 && response.data) {
      console.log('返回标准接口格式数据')
      return response
    }
    
    console.log('返回非标准格式数据')
    return response
  } catch (error) {
    console.error('巡检汇总数据请求失败:', error)
    throw error
  }
}

/**
 * 巡检趋势数据
 * @param {Object} params - 可传时间范围参数
 */
export const getInspectTrend = (params) => {
  console.log('调用API: /dashboard/inspectTrend')
  return request({
    url: '/dashboard/inspectTrend',
    method: 'get',
    params // 例如: { startTime: '2023-01-01', endTime: '2023-12-31' }
  })
}

/**
 * 获取地铁路线数据
 * @returns {Promise<Object>} 地铁路线数据
 */
export function getMetroLines() {
  return new Promise((resolve) => {
    // 模拟网络请求延迟
    setTimeout(() => {
      // 返回沈阳地铁路线数据（简化版）
      resolve({
        code: 10000,
        message: "操作成功",
        data: {
          lines: [
            {
              id: 1,
              name: '1号线',
              color: '#FF4500',
              stations: [
                { id: 101, name: '沈阳站', position: [-10, 0, 5], isTransfer: true, transferLines: [2] },
                { id: 102, name: '青年大街', position: [-5, 0, 5], isTransfer: true, transferLines: [2] },
                { id: 103, name: '中街', position: [0, 0, 5], isTransfer: false },
                { id: 104, name: '东中街', position: [5, 0, 5], isTransfer: true, transferLines: [3] },
                { id: 105, name: '铁西广场', position: [10, 0, 5], isTransfer: false }
              ]
            },
            {
              id: 2,
              name: '2号线',
              color: '#0000FF',
              stations: [
                { id: 201, name: '北陵公园', position: [0, 0, 10], isTransfer: false },
                { id: 202, name: '沈阳北站', position: [0, 0, 5], isTransfer: false },
                { id: 203, name: '青年大街', position: [-5, 0, 5], isTransfer: true, transferLines: [1] },
                { id: 204, name: '市府广场', position: [-5, 0, 0], isTransfer: true, transferLines: [3] },
                { id: 205, name: '南湖公园', position: [-5, 0, -5], isTransfer: false }
              ]
            },
            {
              id: 3,
              name: '3号线',
              color: '#FFFF00',
              stations: [
                { id: 301, name: '辽大东门', position: [10, 0, -5], isTransfer: false },
                { id: 302, name: '东中街', position: [5, 0, 5], isTransfer: true, transferLines: [1] },
                { id: 303, name: '市府广场', position: [-5, 0, 0], isTransfer: true, transferLines: [2] },
                { id: 304, name: '工业大学', position: [-10, 0, -5], isTransfer: false }
              ]
            }
          ]
        }
      });
    }, 800);
  });
}

/**
 * 获取巡视任务数据
 * @returns {Promise<Object>} 巡视任务数据
 */
export function getInspectionTasks() {
  console.log('调用API: /dashboard/inspection-tasks')
  return request({
    url: '/dashboard/inspection-tasks',
    method: 'get'
  })
}