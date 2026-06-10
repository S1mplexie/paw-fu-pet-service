<template>
  <div class="dashboard">
    <el-row :gutter="isMobile ? 12 : 20">
      <el-col :xs="12" :sm="6" :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon pet-icon">
              <i class="el-icon-picture-outline"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalPets || 0 }}</div>
              <div class="stat-label">总宠物信息</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon waiting-icon">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.waitingForAdoption || 0 }}</div>
              <div class="stat-label">待领养数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon adopted-icon">
              <i class="el-icon-check"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.adopted || 0 }}</div>
              <div class="stat-label">已领养数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="isMobile ? 12 : 20" style="margin-top: 20px">
      <el-col :xs="24" :sm="12" :span="12">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon today-user-icon">
              <i class="el-icon-plus"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayNewUsers || 0 }}</div>
              <div class="stat-label">今日新增用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :span="12">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon today-pet-icon">
              <i class="el-icon-upload"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayNewPets || 0 }}</div>
              <div class="stat-label">今日新增发布</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getDashboardStats } from '@/api/admin'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {},
      isMobile: false
    }
  },
  created() {
    this.fetchStats()
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
    async fetchStats() {
      try {
        const res = await getDashboardStats()
        this.stats = res.data
      } catch (error) {
        console.error(error)
      }
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px 0;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #fff;
  margin-right: 20px;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.pet-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.waiting-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.adopted-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.today-user-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.today-pet-icon {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 10px 0;
  }
  
  .stat-card {
    margin-bottom: 12px;
  }
  
  .stat-content {
    padding: 8px;
  }
  
  .stat-icon {
    width: 44px;
    height: 44px;
    font-size: 22px;
    margin-right: 12px;
  }
  
  .stat-value {
    font-size: 22px;
    margin-bottom: 3px;
  }
  
  .stat-label {
    font-size: 12px;
  }
}
</style>
