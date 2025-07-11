<template>
  <div class="error-page">
    <el-result
      icon="error"
      title="403"
      sub-title="抱歉，您没有访问该页面的权限"
    >
      <template #extra>
        <el-button @click="goBack">返回上一页</el-button>
        <el-button type="primary" @click="$router.push('/tasks')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
// 403权限不足页面
import { useRouter } from 'vue-router';

const router = useRouter();

// 返回上一页
const goBack = () => {
  router.go(-1);
};
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}
</style> 