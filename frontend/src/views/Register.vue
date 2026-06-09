<template>
  <div class="register-page">
    <div class="register-container">
      <!-- 左侧装饰 -->
      <div class="register-banner">
        <div class="banner-content">
          <paw-icon :size="80" class="banner-icon"></paw-icon>
          <h2>加入我们</h2>
          <p>Paw福宠物服务平台</p>
        </div>
      </div>
      
      <!-- 右侧表单 -->
      <div class="register-form-section">
        <div class="form-header">
          <h3>用户注册</h3>
          <p>已有账号？<router-link to="/login">立即登录</router-link></p>
        </div>
        
        <el-form :model="form" :rules="rules" ref="form" class="register-form">
          <el-form-item prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="用户名（4-20位字母数字）" 
              prefix-icon="el-icon-user"
              size="large"
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="form.password" 
              :type="showPassword ? 'text' : 'password'" 
              placeholder="密码（8-20位数字+字母）" 
              prefix-icon="el-icon-lock"
              size="large"
              @input="handlePasswordChange"
            >
              <i 
                slot="suffix" 
                :class="showPassword ? 'el-icon-view' : 'el-icon-close'" 
                @click="showPassword = !showPassword"
                style="cursor: pointer; margin-right: 10px;"
              ></i>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="form.confirmPassword" 
              :type="showConfirmPassword ? 'text' : 'password'" 
              placeholder="确认密码" 
              prefix-icon="el-icon-lock"
              size="large"
            >
              <i 
                slot="suffix" 
                :class="showConfirmPassword ? 'el-icon-view' : 'el-icon-close'" 
                @click="showConfirmPassword = !showConfirmPassword"
                style="cursor: pointer; margin-right: 10px;"
              ></i>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="phone">
            <el-input 
              v-model="form.phone" 
              placeholder="手机号（选填）" 
              prefix-icon="el-icon-phone"
              size="large"
              clearable
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="email">
            <el-input 
              v-model="form.email" 
              placeholder="邮箱（选填）" 
              prefix-icon="el-icon-message"
              size="large"
              clearable
            ></el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleRegister" 
              :loading="loading" 
              size="large"
              class="register-btn"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="tips">
          <el-alert
            title="注册提示"
            type="info"
            :closable="false"
          >
            <p>• 用户名：4-20位字母、数字或下划线</p>
            <p>• 密码：8-20位且必须包含数字和字母</p>
            <p>• 手机号和邮箱为选填项</p>
          </el-alert>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/user'
import PawIcon from '@/components/PawIcon.vue'

export default {
  name: 'Register',
  components: {
    PawIcon
  },
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        phone: '',
        email: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]{4,20}$/, message: '用户名需4-20位字母、数字或下划线', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/, message: '密码需8-20位且包含数字和字母', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请输入确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        phone: [
          { pattern: /^$|^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
        ],
        email: [
          { pattern: /^$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '邮箱格式错误', trigger: 'blur' }
        ]
      },
      showPassword: false,
      showConfirmPassword: false,
      loading: false
    }
  },
  methods: {
    handlePasswordChange() {
      if (this.form.confirmPassword) {
        this.$refs.form.validateField('confirmPassword')
      }
    },
    handleRegister() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const data = {
              username: this.form.username,
              password: this.form.password,
              confirmPassword: this.form.confirmPassword,
              phone: this.form.phone || null,
              email: this.form.email || null
            }
            await register(data)
            this.$message.success('注册成功，请登录')
            this.$router.push('/login')
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
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-container {
  display: flex;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 900px;
  width: 100%;
}

.register-banner {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.register-banner::before {
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

.register-form-section {
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

.register-form {
  margin-bottom: 20px;
}

.register-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  border-radius: 8px;
}

.tips {
  margin-top: 20px;
}

.tips p {
  margin: 5px 0;
  font-size: 13px;
  color: #606266;
}

@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
  }
  
  .register-banner {
    padding: 40px 30px;
  }
  
  .register-form-section {
    padding: 40px 30px;
  }
}
</style>
