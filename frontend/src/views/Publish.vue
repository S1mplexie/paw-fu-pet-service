<template>
  <div class="publish-page">
    <div class="header">
      <div class="container">
        <h1 class="logo"><paw-icon :size="32" class="logo-icon"></paw-icon> Paw福宠物服务平台 - 发布领养信息</h1>
        <router-link to="/" class="back-btn">返回首页</router-link>
      </div>
    </div>
    
    <div class="container">
      <el-card class="publish-card">
        <el-form :model="form" :rules="rules" ref="form" label-width="120px">
          <el-form-item label="宠物照片">
            <image-upload v-model="form.photoUrl" type="pets" placeholder="上传宠物照片"></image-upload>
          </el-form-item>
          
          <el-form-item label="宠物名称" prop="petName">
            <el-input v-model="form.petName" placeholder="请输入宠物名称"></el-input>
          </el-form-item>
          
          <el-form-item label="宠物种类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择">
              <el-option label="犬类" :value="1"></el-option>
              <el-option label="猫类" :value="2"></el-option>
              <el-option label="鸟类" :value="3"></el-option>
              <el-option label="兔类" :value="4"></el-option>
              <el-option label="仓鼠" :value="5"></el-option>
              <el-option label="其他" :value="6"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="宠物状态" prop="petStatus">
            <el-select v-model="form.petStatus" placeholder="请选择">
              <el-option label="健康" :value="1"></el-option>
              <el-option label="已绝育" :value="2"></el-option>
              <el-option label="已免疫" :value="3"></el-option>
              <el-option label="有特殊护理需求" :value="4"></el-option>
              <el-option label="其他" :value="5"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="年龄（月）">
            <el-input-number v-model="form.age" :min="0"></el-input-number>
          </el-form-item>
          
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender">
              <el-radio :label="1">公</el-radio>
              <el-radio :label="2">母</el-radio>
              <el-radio :label="0">未知</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="毛色">
            <el-input v-model="form.color"></el-input>
          </el-form-item>
          
          <el-form-item label="详细描述">
            <el-input type="textarea" v-model="form.description" :rows="4"></el-input>
          </el-form-item>
          
          <el-form-item label="省份">
            <el-input v-model="form.province"></el-input>
          </el-form-item>
          
          <el-form-item label="城市">
            <el-input v-model="form.city"></el-input>
          </el-form-item>
          
          <el-form-item label="联系人" prop="contactName">
            <el-input v-model="form.contactName"></el-input>
          </el-form-item>
          
          <el-form-item label="联系电话" prop="contactPhone">
            <el-input v-model="form.contactPhone"></el-input>
          </el-form-item>
          
          <el-form-item label="微信号">
            <el-input v-model="form.contactWechat"></el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="loading">发布</el-button>
            <el-button @click="$router.push('/')">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import { publishPet } from '@/api/pet'
import PawIcon from '@/components/PawIcon.vue'
import ImageUpload from '@/components/ImageUpload.vue'

export default {
  name: 'Publish',
  components: {
    PawIcon,
    ImageUpload
  },
  data() {
    return {
      form: {
        petName: '',
        categoryId: '',
        petStatus: '',
        age: 0,
        gender: 0,
        color: '',
        description: '',
        photoUrl: '',
        province: '',
        city: '',
        contactName: '',
        contactPhone: '',
        contactWechat: ''
      },
      rules: {
        petName: [{ required: true, message: '请输入宠物名称', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择宠物种类', trigger: 'change' }],
        petStatus: [{ required: true, message: '请选择宠物状态', trigger: 'change' }],
        contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
        contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            await publishPet(this.form)
            this.$message.success('发布成功')
            this.$router.push('/my-pets')
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
.publish-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 24px;
  font-weight: bold;
}

.back-btn {
  color: white;
  text-decoration: none;
}

.publish-card {
  margin: 30px auto;
}
</style>
