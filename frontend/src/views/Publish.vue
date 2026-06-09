<template>
  <div class="publish-page">
    <div class="header">
      <div class="container">
        <h1 class="logo pawfu-title"><paw-icon :size="32" class="logo-icon"></paw-icon> Paw福宠物服务平台 - {{ isEdit ? '修改领养信息' : '发布领养信息' }}</h1>
        <router-link to="/" class="back-btn">返回首页</router-link>
      </div>
    </div>
    
    <div class="container">
      <el-card class="publish-card">
        <el-form :model="form" :rules="rules" ref="form" label-width="120px">
          <el-form-item label="封面照片">
            <image-upload v-model="form.coverPhotoUrl" type="pets" placeholder="上传封面照片"></image-upload>
            <div class="upload-tip">封面照片将作为主页展示图片</div>
          </el-form-item>
          
          <el-form-item label="图片集">
            <multi-image-upload v-model="form.photoUrls" type="pets" :max-count="9" :max-size="5"></multi-image-upload>
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
          
          <el-form-item label="所在地区">
            <province-city-selector
              :province.sync="form.province"
              :city.sync="form.city"
              province-placeholder="请选择省份"
              city-placeholder="请选择城市"
            ></province-city-selector>
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
            <el-button type="primary" @click="handleSubmit" :loading="loading">{{ isEdit ? '保存' : '发布' }}</el-button>
            <el-button @click="$router.push('/')">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import { publishPet, updatePet, getPetDetail } from '@/api/pet'
import PawIcon from '@/components/PawIcon.vue'
import ImageUpload from '@/components/ImageUpload.vue'
import MultiImageUpload from '@/components/MultiImageUpload.vue'
import ProvinceCitySelector from '@/components/ProvinceCitySelector.vue'

export default {
  name: 'Publish',
  components: {
    PawIcon,
    ImageUpload,
    MultiImageUpload,
    ProvinceCitySelector
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
        coverPhotoUrl: '',
        photoUrls: '',
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
      loading: false,
      isEdit: false,
      petId: null
    }
  },
  mounted() {
    this.petId = this.$route.params.id
    if (this.petId) {
      this.isEdit = true
      this.loadPetData()
    }
  },
  methods: {
    async loadPetData() {
      try {
        const res = await getPetDetail(this.petId)
        const pet = res.data
        this.form = {
          petName: pet.petName,
          categoryId: pet.categoryId,
          petStatus: pet.petStatus,
          age: pet.age || 0,
          gender: pet.gender || 0,
          color: pet.color || '',
          description: pet.description || '',
          coverPhotoUrl: pet.coverPhotoUrl || '',
          photoUrls: pet.photoUrls || '',
          province: pet.province || '',
          city: pet.city || '',
          contactName: pet.contactName,
          contactPhone: pet.contactPhone,
          contactWechat: pet.contactWechat || ''
        }
      } catch (error) {
        this.$message.error('加载宠物信息失败')
        this.$router.push('/my-pets')
      }
    },
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            if (this.isEdit) {
              await updatePet(this.petId, this.form)
              this.$message.success('修改成功')
            } else {
              await publishPet(this.form)
              this.$message.success('发布成功')
            }
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

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
