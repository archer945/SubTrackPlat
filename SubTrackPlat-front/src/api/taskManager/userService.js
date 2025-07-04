import axios from 'axios'

/**
 * 根据姓名模糊查询用户列表
 * @param {Object} params { name: '张' }
 * @returns {Promise}
 */
export const fetchUserList = (params) => {
    return axios.get('/api/tasks/tasks/users', { params })
}