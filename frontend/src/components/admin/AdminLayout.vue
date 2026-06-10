<template>
  <el-container class="admin-layout">
    <!-- 移动端顶部导航栏 -->
    <div class="mobile-header" v-if="isMobile">
      <div class="mobile-header-content">
        <el-button icon="el-icon-menu" circle @click="drawerVisible = true" class="menu-btn"></el-button>
        <div class="mobile-title-wrapper">
          <img src="@/assets/images/paw.png" class="mobile-logo-icon" alt="Paw福">
          <h2 class="mobile-title pawfu-title">Paw福管理后台</h2>
        </div>
        <el-dropdown @command="handleCommand" trigger="click">
          <el-avatar icon="el-icon-user-solid" size="small"></el-avatar>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item disabled>{{ adminInfo.username || '管理员' }}</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!-- 移动端抽屉菜单 -->
    <el-drawer
      v-if="isMobile"
      :visible.sync="drawerVisible"
      direction="ltr"
      size="260px"
      :show-close="false"
      class="mobile-drawer"
    >
      <div class="drawer-header">
        <img src="@/assets/images/paw.png" class="drawer-logo" alt="Paw福">
        <h3 class="pawfu-title">Paw福宠物平台</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="mobile-menu"
        @select="drawerVisible = false"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <i class="el-icon-s-data"></i>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <i class="el-icon-user-solid"></i>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/pets">
          <i class="el-icon-picture-outline"></i>
          <span>宠物管理</span>
        </el-menu-item>
      </el-menu>
    </el-drawer>

    <!-- PC端侧边栏 -->
    <el-aside v-if="!isMobile" width="220px" class="admin-aside">
      <div class="logo">
        <img src="@/assets/images/paw.png" class="logo-icon" alt="Paw福">
        <h2 class="pawfu-title">Paw福宠物平台管理</h2>
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
      <el-header v-if="!isMobile" class="admin-header">
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
      <el-main class="admin-main" :class="{ 'mobile-main': isMobile }">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { adminLogout } from '@/api/admin'

export default {
  name: 'AdminLayout',
  data() {
    return {
      isMobile: false,
      drawerVisible: false
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path
    },
    adminInfo() {
      return this.$store.getters.adminInfo
    }
  },
  mounted() {
    this.checkMobile()
    window.addEventListener('resize', this.checkMobile)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkMobile)
  },
  methods: {
    checkMobile() {
      this.isMobile = window.innerWidth <= 768
    },
    async handleCommand(command) {
      if (command === 'logout') {
        try {
          await adminLogout()
          this.$store.dispatch('adminLogout')
          this.$message.success('退出成功')
          this.$router.push('/login')
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
  padding: 0 10px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.logo h2 {
  margin: 0;
  font-size: 16px;
  white-space: nowrap;
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

/* 移动端样式 */
.mobile-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  z-index: 1000;
}

.mobile-header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  height: 100%;
}

.menu-btn {
  border: none;
  background: transparent;
}

.mobile-title-wrapper {
  display: flex;
  align-items: center;
  flex: 1;
  justify-content: center;
}

.mobile-logo-icon {
  width: 24px;
  height: 24px;
  margin-right: 8px;
}

.mobile-title {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: #333;
}

.mobile-main {
  padding-top: 80px;
  padding-left: 16px;
  padding-right: 16px;
  padding-bottom: 24px;
}

.mobile-drawer ::v-deep .el-drawer__body {
  padding: 0;
}

.drawer-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 0 24px;
  background: linear-gradient(135deg, #001529 0%, #1890ff 100%);
}

.drawer-logo {
  width: 48px;
  height: 48px;
  margin-bottom: 12px;
}

.drawer-header h3 {
  margin: 0;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.mobile-menu {
  border-right: none;
}

.mobile-menu .el-menu-item {
  height: 56px;
  line-height: 56px;
  font-size: 15px;
}

.mobile-menu .el-menu-item i {
  margin-right: 12px;
  font-size: 18px;
}
</style>
