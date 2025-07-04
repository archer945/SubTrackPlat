import axios from 'axios'

/**
 * 获取任务列表
 * @param {Object} params 查询参数
 * @returns {Promise} 返回任务数据
 */
export const fetchTasks = (params) => {
    return axios.get('/api/tasks/tasks', { params })
}

/**
 * 导出任务数据
 * @param {Object} params 查询参数
 * @returns {Promise} 返回导出文件
 */
export const exportTasks = (params) => {
    return axios({
        url: '/api/tasks/tasks/export',
        method: 'GET',
        responseType: 'blob',
        params,
    })
}

/**
 * 删除任务
 * @param {Number} taskId 任务ID
 * @returns {Promise} 返回删除结果
 */
export const deleteTask = (taskId) => {
    return axios.delete(`/api/tasks/tasks/${taskId}`)
}



// 获取任务详情
export const getTaskById = (taskId) => {
    return axios.get(`/api/tasks/tasks/${taskId}`)
        .then(response => response.data)
        .catch(error => {
            console.error('获取任务数据失败', error)
            throw error
        })
}
