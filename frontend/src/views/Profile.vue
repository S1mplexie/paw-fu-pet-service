<template>
  <div class="profile-page">
    <!-- 头部 -->
    <div class="header">
      <div class="container">
        <div class="logo-section">
          <paw-icon :size="36" class="logo-icon"></paw-icon>
          <h1 class="logo">Paw福宠物服务平台</h1>
        </div>
        <el-button icon="el-icon-arrow-left" @click="$router.push('/')">返回首页</el-button>
      </div>
    </div>
    
    <div class="container">
      <el-row :gutter="20">
        <!-- 左侧用户信息卡片 -->
        <el-col :span="8">
          <el-card class="user-card" shadow="hover">
            <div class="user-avatar">
              <image-upload v-model="form.avatar" type="avatars" placeholder="上传头像"></image-upload>
            </div>
            <div class="user-info">
              <h3>{{ user.username }}</h3>
              <p class="nickname">{{ user.nickname }}</p>
              <el-tag type="success" size="small">正常</el-tag>
            </div>
            <el-divider></el-divider>
            <div class="stats">
              <div class="stat-item">
                <span class="stat-num">{{ stats.published }}</span>
                <span class="stat-label">发布数</span>
              </div>
              <div class="stat-item">
                <span class="stat-num">{{ stats.views }}</span>
                <span class="stat-label">浏览量</span>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <!-- 右侧信息编辑 -->
        <el-col :span="16">
          <el-card class="edit-card" shadow="hover">
            <div slot="header">
              <i class="el-icon-edit"></i>
              <span>个人信息设置</span>
            </div>
            
            <el-form :model="form" :rules="rules" ref="form" label-width="100px">
              <el-form-item label="用户名">
                <el-input v-model="user.username" disabled></el-input>
              </el-form-item>
              
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="form.nickname" placeholder="请输入昵称"></el-input>
              </el-form-item>
              
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" placeholder="选填，用于联系方式" clearable>
                  <template slot="prepend">+86</template>
                </el-input>
              </el-form-item>
              
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="form.email" placeholder="选填，用于接收通知" clearable>
                  <template slot="prepend">
                    <i class="el-icon-message"></i>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleUpdate" :loading="loading">
                  <i class="el-icon-check"></i> 保存修改
                </el-button>
                <el-button @click="resetForm">
                  <i class="el-icon-refresh"></i> 重置
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
          
          <!-- 安全设置 -->
          <el-card class="security-card" shadow="hover" style="margin-top: 20px">
            <div slot="header">
              <i class="el-icon-lock"></i>
              <span>安全设置</span>
            </div>
            <el-alert
              title="密码安全"
              type="info"
              description="如需修改密码，请联系系统管理员或通过忘记密码功能重置。"
              :closable="false"
              show-icon
            ></el-alert>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { getUserInfo, updateUserInfo } from '@/api/user'
import { getMyPets } from '@/api/pet'
import PawIcon from '@/components/PawIcon.vue'
import ImageUpload from '@/components/ImageUpload.vue'

export default {
  name: 'Profile',
  components: {
    PawIcon,
    ImageUpload
  },
  data() {
    return {
      user: {},
      form: {
        nickname: '',
        phone: '',
        email: '',
        avatar: ''
      },
      stats: {
        published: 0,
        views: 0
      },
      rules: {
        phone: [
          { pattern: /^$|^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
        ],
        email: [
          { pattern: /^$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '邮箱格式错误', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  mounted() {
    this.loadUserInfo()
    this.loadStats()
  },
  methods: {
    async loadUserInfo() {
      try {
        const res = await getUserInfo()
        this.user = res.data
        this.form = {
          nickname: this.user.nickname,
          phone: this.user.phone || '',
          email: this.user.email || '',
          avatar: this.user.avatar || ''
        }
      } catch (error) {
        this.$message.error('加载用户信息失败')
      }
    },
    async loadStats() {
      try {
        const res = await getMyPets(1, 100)
        this.stats.published = res.data.total || 0
        this.stats.views = (res.data.records || []).reduce((sum, pet) => sum + (pet.viewCount || 0), 0)
      } catch (error) {
        console.error(error)
      }
    },
    handleUpdate() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const data = {
              nickname: this.form.nickname,
              phone: this.form.phone || null,
              email: this.form.email || null,
              avatar: this.form.avatar || null
            }
            await updateUserInfo(data)
            this.$message.success('修改成功')
            this.loadUserInfo()
          } catch (error) {
            console.error(error)
          } finally {
            this.loading = false
          }
        }
      })
    },
    resetForm() {
      this.form = {
        nickname: this.user.nickname,
        phone: this.user.phone || '',
        email: this.user.email || '',
        avatar: this.user.avatar || ''
      }
    }
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f7fa 0%, #ffffff 100%);
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 15px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
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
}

.user-card {
  margin-top: 30px;
  text-align: center;
}

.user-avatar {
  padding: 20px 0;
}

.user-info h3 {
  margin: 10px 0;
  color: #303133;
}

.nickname {
  color: #909399;
  font-size: 14px;
}

.stats {
  display: flex;
  justify-content: space-around;
}

.stat-item {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.edit-card, .security-card {
  margin-top: 30px;
}
</style>
