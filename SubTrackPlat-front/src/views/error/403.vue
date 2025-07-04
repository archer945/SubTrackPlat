<template>
  <div class="error-page">
    <el-result
      icon="error"
      title="403"
      sub-title="抱歉，您没有访问该页面的权限"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
// 403权限不足页面
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