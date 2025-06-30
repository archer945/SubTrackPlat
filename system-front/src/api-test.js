import axios from 'axios';

// 直接测试后端API
async function testBackendAPI() {
  try {
    console.log('开始测试后端API...');
    
    // 测试用户列表接口
    console.log('测试用户列表接口...');
    try {
      const userResponse = await axios.get('http://localhost:8080/systemManager/user', {
        params: { pageIndex: 1, pageSize: 10 }
      });
      console.log('用户列表响应:', userResponse.data);
    } catch (error) {
      console.error('用户列表请求失败:', error.message);
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误数据:', error.response.data);
      }
    }
    
    // 测试角色列表接口
    console.log('测试角色列表接口...');
    try {
      const roleResponse = await axios.get('http://localhost:8080/systemManager/role', {
        params: { pageIndex: 1, pageSize: 10 }
      });
      console.log('角色列表响应:', roleResponse.data);
    } catch (error) {
      console.error('角色列表请求失败:', error.message);
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误数据:', error.response.data);
      }
    }
    
    // 测试菜单列表接口
    console.log('测试菜单列表接口...');
    try {
      const menuResponse = await axios.get('http://localhost:8080/systemManager/menu', {
        params: { pageIndex: 1, pageSize: 100 }
      });
      console.log('菜单列表响应:', menuResponse.data);
    } catch (error) {
      console.error('菜单列表请求失败:', error.message);
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误数据:', error.response.data);
      }
    }
    
    // 测试部门列表接口
    console.log('测试部门列表接口...');
    try {
      const deptResponse = await axios.get('http://localhost:8080/systemManager/dept', {
        params: { pageIndex: 1, pageSize: 100 }
      });
      console.log('部门列表响应:', deptResponse.data);
    } catch (error) {
      console.error('部门列表请求失败:', error.message);
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误数据:', error.response.data);
      }
    }
    
  } catch (error) {
    console.error('API测试错误:', error);
  }
}

// 执行测试
testBackendAPI(); 