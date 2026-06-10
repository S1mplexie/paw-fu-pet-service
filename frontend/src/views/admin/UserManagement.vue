<template>
  <div class="user-management">
    <el-card>
      <div slot="header" class="card-header">
        <span>用户管理</span>
      </div>
      <div class="filter-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名/昵称/手机号"
          prefix-icon="el-icon-search"
          style="width: 300px; margin-right: 10px"
          @keyup.enter.native="fetchUsers"
        ></el-input>
        <el-select v-model="statusFilter" placeholder="用户状态" clearable style="width: 150px; margin-right: 10px">
          <el-option label="正常" value="1"></el-option>
          <el-option label="禁用" value="0"></el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="fetchUsers">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetFilter">重置</el-button>
      </div>
      <el-table :data="userList" v-loading="loading" border stripe>
        <el-table-column prop="userId" label="用户ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="nickname" label="昵称" width="120"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="130"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160"></el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-view" @click="viewDetail(scope.row)">详情</el-button>
            <el-button
              v-if="scope.row.status === 1"
              type="text"
              icon="el-icon-lock"
              @click="handleDisable(scope.row)"
            >禁用</el-button>
            <el-button
              v-else
              type="text"
              icon="el-icon-unlock"
              @click="handleEnable(scope.row)"
            >启用</el-button>
            <el-button type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" style="color: #f56c6c">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px; text-align: right"
      ></el-pagination>
    </el-card>
    <el-dialog title="用户详情" :visible.sync="detailVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ currentUser.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'" size="small">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentUser.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList, getUserDetail, disableUser, enableUser, deleteUser } from '@/api/admin'

export default {
  name: 'UserManagement',
  data() {
    return {
      userList: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      searchKeyword: '',
      statusFilter: '',
      detailVisible: false,
      currentUser: {}
    }
  },
  created() {
    this.fetchUsers()
  },
  methods: {
    async fetchUsers() {
      this.loading = true
      try {
        const res = await getUserList({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          keyword: this.searchKeyword,
          status: this.statusFilter
        })
        this.userList = res.data.records || res.data.list || []
        this.total = res.data.total || 0
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    resetFilter() {
      this.searchKeyword = ''
      this.statusFilter = ''
      this.pageNum = 1
      this.fetchUsers()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchUsers()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.fetchUsers()
    },
    async viewDetail(row) {
      try {
        const res = await getUserDetail(row.userId)
        this.currentUser = res.data
        this.detailVisible = true
      } catch (error) {
        console.error(error)
      }
    },
    handleDisable(row) {
      this.$confirm('确定要禁用该用户吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await disableUser(row.userId)
          this.$message.success('禁用成功')
          this.fetchUsers()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    handleEnable(row) {
      this.$confirm('确定要启用该用户吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await enableUser(row.userId)
          this.$message.success('启用成功')
          this.fetchUsers()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    handleDelete(row) {
      this.$confirm('确定要删除该用户吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(async () => {
        try {
          await deleteUser(row.userId)
          this.$message.success('删除成功')
          this.fetchUsers()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px 0;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.filter-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}
</style>
