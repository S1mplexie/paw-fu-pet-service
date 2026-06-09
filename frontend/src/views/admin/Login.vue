<template>
  <div class="admin-login-page">
    <div class="login-container">
      <div class="login-header">
        <h2>🐾 宠物平台管理后台</h2>
        <p>管理员登录</p>
      </div>
      <el-form :model="form" :rules="rules" ref="form" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入管理员账号"
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
        <el-divider>测试管理员账号</el-divider>
        <p class="account-info">
          <i class="el-icon-info"></i>
          账号：<strong>admin</strong>
          <br>
          密码：<strong>Admin123</strong>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { adminLogin } from '@/api/admin'

export default {
  name: 'AdminLogin',
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
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
            const res = await adminLogin(this.form)
            this.$store.dispatch('adminLogin', res.data)
            this.$message.success('登录成功！')
            this.$router.push('/admin/dashboard')
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
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #001529 0%, #003a70 100%);
  padding: 20px;
}

.login-container {
  background: white;
  border-radius: 12px;
  padding: 40px 50px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 450px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #001529;
  font-size: 26px;
  margin-bottom: 10px;
}

.login-header p {
  color: #909399;
  font-size: 14px;
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
  color: #1890ff;
  margin-right: 5px;
}

.account-info strong {
  color: #1890ff;
}
</style>
