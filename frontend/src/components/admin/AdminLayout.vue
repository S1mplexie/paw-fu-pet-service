<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-aside">
      <div class="logo">
        <h2>🐾 宠物平台管理</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="admin-menu"
        background-color="#001529"
        text-color="#fff"
        active-text-color="#1890ff"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <i class="el-icon-s-data"></i>
          <span slot="title">仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <i class="el-icon-user-solid"></i>
          <span slot="title">用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/pets">
          <i class="el-icon-picture-outline"></i>
          <span slot="title">宠物管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <i class="el-icon-user-solid"></i>
              {{ adminInfo.username || '管理员' }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { adminLogout } from '@/api/admin'

export default {
  name: 'AdminLayout',
  computed: {
    activeMenu() {
      return this.$route.path
    },
    adminInfo() {
      return this.$store.getters.adminInfo
    }
  },
  methods: {
    async handleCommand(command) {
      if (command === 'logout') {
        try {
          await adminLogout()
          this.$store.dispatch('adminLogout')
          this.$message.success('退出成功')
          this.$router.push('/admin/login')
        } catch (error) {
          console.error(error)
        }
      }
    }
  }
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.admin-aside {
  background-color: #001529;
  overflow: hidden;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
}

.admin-menu {
  border-right: none;
}

.admin-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #333;
}

.el-dropdown-link i {
  margin-right: 5px;
}

.admin-main {
  background: #f0f2f5;
  padding: 24px;
}
</style>
