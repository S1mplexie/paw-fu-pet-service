<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧装饰 -->
      <div class="login-banner">
        <div class="banner-content">
          <paw-icon :size="80" class="banner-icon"></paw-icon>
          <h2>欢迎回来</h2>
          <p>Paw福宠物服务平台</p>
        </div>
      </div>
      
      <!-- 右侧表单 -->
      <div class="login-form-section">
        <div class="form-header">
          <h3>用户登录</h3>
          <p>还没有账号？<router-link to="/register">立即注册</router-link></p>
        </div>
        
        <el-form :model="form" :rules="rules" ref="form" class="login-form">
          <el-form-item prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="请输入用户名" 
              prefix-icon="el-icon-user"
              size="large"
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码" 
              prefix-icon="el-icon-lock"
              size="large"
              show-password
              @keyup.enter.native="handleLogin"
            ></el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleLogin" 
              :loading="loading" 
              size="large"
              class="login-btn"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="demo-account">
          <el-divider>测试账号</el-divider>
          <p class="account-info">
            <i class="el-icon-info"></i>
            用户名：<strong>zhangsan</strong> 或 <strong>lisi</strong>
            <br>
            密码：<strong>Test1234</strong>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/user'
import PawIcon from '@/components/PawIcon.vue'

export default {
  name: 'Login',
  components: {
    PawIcon
  },
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const res = await login(this.form)
            this.$store.dispatch('login', res.data)
            this.$message.success('登录成功，欢迎回来！')
            this.$router.push('/')
          } catch (error) {
            console.error(error)
          } finally {
            this.loading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  display: flex;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 900px;
  width: 100%;
}

.login-banner {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.login-banner::before {
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
  text-align: center;
}

.banner-icon {
  font-size: 80px;
  margin-bottom: 20px;
  display: block;
}

.login-form-section {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: 30px;
}

.form-header h3 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.form-header p {
  color: #909399;
  font-size: 14px;
}

.form-header a {
  color: #667eea;
  font-weight: 500;
  text-decoration: none;
}

.login-form {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  border-radius: 8px;
}

.demo-account {
  margin-top: 20px;
}

.account-info {
  text-align: center;
  color: #606266;
  font-size: 13px;
  line-height: 1.8;
}

.account-info i {
  color: #667eea;
  margin-right: 5px;
}

.account-info strong {
  color: #667eea;
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  
  .login-banner {
    padding: 40px 30px;
  }
  
  .login-form-section {
    padding: 40px 30px;
  }
}
</style>
