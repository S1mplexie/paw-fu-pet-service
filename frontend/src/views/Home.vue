<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="container">
        <div class="logo-section">
          <paw-icon :size="36" class="logo-icon"></paw-icon>
          <h1 class="logo">Paw福宠物服务平台</h1>
        </div>
        <div class="nav">
          <el-link :underline="false" class="nav-link" @click="$router.push('/')">
            <i class="el-icon-house"></i> 首页
          </el-link>
          <template v-if="!isLoggedIn">
            <el-link :underline="false" class="nav-link" @click="$router.push('/login')">
              <i class="el-icon-user"></i> 登录
            </el-link>
            <el-button type="primary" round @click="$router.push('/register')">
              <i class="el-icon-plus"></i> 注册
            </el-button>
          </template>
          <template v-else>
            <el-link :underline="false" class="nav-link" @click="$router.push('/publish')">
              <i class="el-icon-edit"></i> 发布领养
            </el-link>
            <el-link :underline="false" class="nav-link" @click="$router.push('/my-pets')">
              <i class="el-icon-document"></i> 我的发布
            </el-link>
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                <el-avatar :size="32" :src="user.avatar" icon="el-icon-user-solid"></el-avatar>
                <span class="username">{{ user.username }}</span>
                <i class="el-icon-arrow-down"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </div>
      </div>
    </div>
    
    <!-- 横幅区域 -->
    <div class="banner">
      <div class="banner-content">
        <h2>给流浪的毛孩子一个温暖的家</h2>
        <p>每一只宠物都值得被爱，每一次领养都是一次救赎</p>
        <el-button type="primary" size="large" round @click="scrollToPets">
          <i class="el-icon-search"></i> 寻找你的小伙伴
        </el-button>
      </div>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="container">
        <el-card class="search-card" shadow="hover">
          <div class="search-title">
            <i class="el-icon-search"></i>
            <span>搜索领养信息</span>
          </div>
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="宠物种类">
              <el-select v-model="searchForm.categoryId" placeholder="全部种类" clearable style="width: 150px">
                <el-option label="🐕 犬类" :value="1"></el-option>
                <el-option label="🐱 猫类" :value="2"></el-option>
                <el-option label="🐦 鸟类" :value="3"></el-option>
                <el-option label="🐰 兔类" :value="4"></el-option>
                <el-option label="🐹 仓鼠" :value="5"></el-option>
                <el-option label="🐾 其他" :value="6"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="所在城市">
              <el-input v-model="searchForm.city" placeholder="输入城市名" prefix-icon="el-icon-location" clearable style="width: 200px"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
              <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>
    
    <!-- 宠物列表 -->
    <div class="container" ref="petList">
      <div class="section-title">
        <i class="el-icon-star-on"></i>
        <span>待领养宠物</span>
        <span class="total-count">共 {{ total }} 只</span>
      </div>
      
      <div v-if="petList.length === 0" class="empty-state">
        <i class="el-icon-warning-outline"></i>
        <p>暂无领养信息</p>
        <el-button type="primary" @click="$router.push('/publish')" v-if="isLoggedIn">发布领养信息</el-button>
      </div>
      
      <div class="pet-grid">
        <div v-for="pet in petList" :key="pet.petId" class="pet-card" @click="viewDetail(pet.petId)">
          <div class="pet-image">
            <img :src="pet.photoUrl || defaultImage" :alt="pet.petName">
            <div class="pet-badge" :class="'status-' + pet.adoptionStatus">
              {{ getStatusName(pet.adoptionStatus) }}
            </div>
          </div>
          <div class="pet-info">
            <div class="pet-header">
              <h3>{{ pet.petName }}</h3>
              <el-tag size="small" :type="getCategoryType(pet.categoryId)">
                {{ getCategoryName(pet.categoryId) }}
              </el-tag>
            </div>
            <div class="pet-meta">
              <span><i class="el-icon-location"></i> {{ pet.province }} {{ pet.city }}</span>
              <span><i class="el-icon-view"></i> {{ pet.viewCount || 0 }}</span>
            </div>
            <div class="pet-footer">
              <span class="publish-time">{{ formatTime(pet.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="pageNum"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>
    
    <!-- 底部信息 -->
    <div class="footer">
      <div class="container">
        <paw-icon :size="24"></paw-icon> Paw福宠物服务平台 | 用爱温暖每一个生命
      </div>
    </div>
  </div>
</template>

<script>
import { getPetList } from '@/api/pet'
import PawIcon from '@/components/PawIcon.vue'

export default {
  name: 'Home',
  components: {
    PawIcon
  },
  data() {
    return {
      searchForm: {
        categoryId: '',
        city: ''
      },
      petList: [],
      pageNum: 1,
      pageSize: 12,
      total: 0,
      defaultImage: 'https://images.unsplash.com/photo-1587300003381-16d9e0e4c6e8?w=400&h=300&fit=crop'
    }
  },
  computed: {
    isLoggedIn() {
      return this.$store.getters.isLoggedIn
    },
    user() {
      return this.$store.getters.user
    }
  },
  mounted() {
    this.loadPetList()
  },
  methods: {
    async loadPetList() {
      try {
        const params = {
          categoryId: this.searchForm.categoryId,
          city: this.searchForm.city,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
        const res = await getPetList(params)
        this.petList = res.data.records || []
        this.total = res.data.total || 0
      } catch (error) {
        console.error(error)
      }
    },
    handleSearch() {
      this.pageNum = 1
      this.loadPetList()
    },
    resetSearch() {
      this.searchForm = {
        categoryId: '',
        city: ''
      }
      this.pageNum = 1
      this.loadPetList()
    },
    handlePageChange(page) {
      this.pageNum = page
      this.loadPetList()
      this.scrollToPets()
    },
    scrollToPets() {
      this.$refs.petList && this.$refs.petList.scrollIntoView({ behavior: 'smooth' })
    },
    viewDetail(id) {
      this.$router.push(`/pet/${id}`)
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.logout()
      } else if (command === 'profile') {
        this.$router.push('/profile')
      }
    },
    logout() {
      this.$store.dispatch('logout')
      this.$message.success('退出成功')
      this.$router.push('/')
    },
    getCategoryName(id) {
      const map = { 1: '犬类', 2: '猫类', 3: '鸟类', 4: '兔类', 5: '仓鼠', 6: '其他' }
      return map[id] || '其他'
    },
    getCategoryType(id) {
      const map = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info', 5: '', 6: 'info' }
      return map[id] || 'info'
    },
    getStatusName(status) {
      const map = { 1: '待领养', 2: '已领养', 3: '已下架' }
      return map[status] || '未知'
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      if (diff < 3600000) return '刚刚'
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
      if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
      return date.toLocaleDateString()
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f7fa 0%, #ffffff 100%);
}

/* 头部导航 */
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 15px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 32px;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  background: linear-gradient(to right, #fff, #ffd700);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-link {
  color: white !important;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s;
}

.nav-link:hover {
  color: #ffd700 !important;
  transform: translateY(-2px);
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: white;
}

.username {
  font-size: 14px;
  font-weight: 500;
}

/* 横幅 */
.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 20px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.banner::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  opacity: 0.3;
}

.banner-content {
  position: relative;
  z-index: 1;
}

.banner h2 {
  font-size: 36px;
  margin-bottom: 15px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
}

.banner p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 30px;
}

/* 搜索区域 */
.search-section {
  margin-top: -20px;
  padding-bottom: 30px;
  position: relative;
  z-index: 10;
}

.search-card {
  border-radius: 12px;
}

.search-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-form {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}

/* 宠物列表 */
.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 30px 0 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.total-count {
  font-size: 14px;
  color: #909399;
  font-weight: normal;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #909399;
}

.empty-state i {
  font-size: 80px;
  margin-bottom: 20px;
  color: #dcdfe6;
}

.pet-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}

.pet-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}

.pet-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.pet-image {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.pet-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.pet-card:hover .pet-image img {
  transform: scale(1.1);
}

.pet-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  backdrop-filter: blur(10px);
}

.status-1 {
  background: rgba(103, 194, 58, 0.9);
  color: white;
}

.status-2 {
  background: rgba(144, 147, 153, 0.9);
  color: white;
}

.status-3 {
  background: rgba(230, 162, 60, 0.9);
  color: white;
}

.pet-info {
  padding: 18px;
}

.pet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.pet-info h3 {
  font-size: 18px;
  margin: 0;
  color: #303133;
  font-weight: 600;
}

.pet-meta {
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 13px;
  margin-bottom: 8px;
}

.pet-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pet-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.publish-time {
  font-size: 12px;
  color: #c0c4cc;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin: 40px 0;
}

/* 底部 */
.footer {
  background: #2c3e50;
  color: white;
  padding: 30px 0;
  text-align: center;
  margin-top: 60px;
}

.footer p {
  margin: 0;
  opacity: 0.8;
}
</style>
