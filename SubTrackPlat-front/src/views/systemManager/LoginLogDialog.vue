<template>
  <el-dialog
    v-model="dialogVisible"
    title="登录日志"
    width="80%"
    class="login-log-dialog"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="登录状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 150px">
            <el-option label="登录成功" :value="1" />
            <el-option label="登录失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="登录时间">
          <el-date-picker
            v-model="searchForm.loginTime"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD HH:mm:ss"
            :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <el-table
      :data="tableData"
      border
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column prop="logId" label="日志编号" width="100" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="username" label="用户名" width="140" />
      <el-table-column prop="status" label="登录状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="msg" label="操作信息" min-width="200" show-overflow-tooltip />
      <el-table-column prop="loginTime" label="登录时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.loginTime) }}
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页区域 -->
    <div class="pagination-container">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { getUserLoginLogs, getUserLoginLogsByUserId } from '@/api/systemManager/user';
import { formatDateTime } from '@/utils/format';
import { Search, Refresh } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  userId: {
    type: [String, Number],
    default: null
  }
});

const emit = defineEmits(['update:visible']);

const dialogVisible = ref(false);
const loading = ref(false);
const tableData = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const searchForm = reactive({
  status: null,
  loginTime: null
});

watch(() => props.visible, (val) => {
  dialogVisible.value = val;
});

watch(dialogVisible, (val) => {
  emit('update:visible', val);
  if (val) {
    fetchData();
  }
});

const handleSearch = () => {
  currentPage.value = 1;
  fetchData();
};

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = null;
  });
  currentPage.value = 1;
  fetchData();
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchData();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  fetchData();
};

const handleClosed = () => {
  resetSearch();
};

const fetchData = async () => {
  loading.value = true;
  try {
    const params = {
      pageIndex: currentPage.value,
      pageSize: pageSize.value,
      status: searchForm.status,
    };

    if (searchForm.loginTime && searchForm.loginTime.length === 2) {
      params.startTime = searchForm.loginTime[0];
      params.endTime = searchForm.loginTime[1];
    }

    let response;
    if (props.userId) {
      response = await getUserLoginLogsByUserId(props.userId, params);
    } else {
      response = await getUserLoginLogs(params);
    }

    tableData.value = response.records;
    total.value = response.total;
  } catch (error) {
    ElMessage.error('获取登录日志失败');
    console.error('获取登录日志失败', error);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-log-dialog .search-area {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 