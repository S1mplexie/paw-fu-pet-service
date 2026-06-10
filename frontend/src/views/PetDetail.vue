<template>
  <div class="pet-detail-page">
    <!-- 头部导航 -->
    <div class="header">
      <div class="container">
        <div class="logo-section">
          <paw-icon :size="36" class="logo-icon"></paw-icon>
          <h1 class="logo pawfu-title">Paw福宠物服务平台</h1>
        </div>
        <el-button icon="el-icon-arrow-left" @click="$router.back()">返回</el-button>
      </div>
    </div>
    
    <div class="container">
      <el-card v-if="pet" class="detail-card" shadow="hover">
        <div class="detail-content">
          <!-- 左侧图片 -->
          <div class="pet-gallery">
            <div class="main-image" v-if="mainImage">
              <img :src="mainImage" :alt="pet.petName" @click="openGallery(0)">
              <div class="image-overlay">
                <i class="el-icon-zoom-in"></i>
              </div>
            </div>
            <div class="no-image-placeholder" v-else>
              <div class="no-image-text">
                <span>暂无</span>
                <span>图片</span>
              </div>
            </div>
            
            <div class="thumbnail-list" v-if="galleryImages.length > 0">
              <div
                v-for="(image, index) in galleryImages.slice(0, 5)"
                :key="index"
                class="thumbnail-item"
                :class="{ active: index === 0 }"
                @click="openGallery(index)"
              >
                <img :src="image" :alt="'缩略图' + (index + 1)">
              </div>
              <div class="more-btn" v-if="galleryImages.length > 5" @click="openGallery(0)">
                <span>+{{ galleryImages.length - 5 }}</span>
              </div>
            </div>
            
            <div class="gallery-tip" v-if="galleryImages.length > 0" @click="openGallery(0)">
              <i class="el-icon-picture"></i>
              <span class="gallery-tip-text">点击查看{{ galleryImages.length }}张图片</span>
              <i class="el-icon-arrow-right gallery-arrow"></i>
            </div>
          </div>
          
          <!-- 右侧信息 -->
          <div class="pet-info">
            <div class="pet-header">
              <h2>{{ pet.petName }}</h2>
              <el-tag :type="getStatusType(pet.adoptionStatus)" size="medium">
                {{ getStatusName(pet.adoptionStatus) }}
              </el-tag>
            </div>
            
            <el-divider></el-divider>
            
            <!-- 基本信息 -->
            <div class="info-section">
              <h4><i class="el-icon-info"></i> 基本信息</h4>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="宠物种类">
                  <el-tag size="small" :type="getCategoryType(pet.categoryId)">
                    {{ getCategoryName(pet.categoryId) }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="健康状态">{{ pet.petStatusName }}</el-descriptions-item>
                <el-descriptions-item label="年龄">{{ pet.age ? pet.age + '个月' : '未知' }}</el-descriptions-item>
                <el-descriptions-item label="性别">{{ getGenderName(pet.gender) }}</el-descriptions-item>
                <el-descriptions-item label="毛色">{{ pet.color || '未知' }}</el-descriptions-item>
                <el-descriptions-item label="体重">{{ pet.weight ? pet.weight + 'kg' : '未知' }}</el-descriptions-item>
              </el-descriptions>
            </div>
            
            <!-- 地区信息 -->
            <div class="info-section">
              <h4><i class="el-icon-location"></i> 地区信息</h4>
              <p class="location-text">
                <i class="el-icon-map-location"></i>
                {{ pet.province }} {{ pet.city }} {{ pet.district || '' }}
                <span v-if="pet.address" class="address">{{ pet.address }}</span>
              </p>
            </div>
            
            <!-- 详细描述 -->
            <div class="info-section">
              <h4><i class="el-icon-document"></i> 详细描述</h4>
              <p class="description">{{ pet.description || '暂无描述' }}</p>
            </div>
            
            <!-- 发布时间 -->
            <div class="info-section">
              <h4><i class="el-icon-time"></i> 发布时间</h4>
              <p class="create-time">{{ formatCreateTime(pet.createTime) }}</p>
              <p class="view-count"><i class="el-icon-view"></i> 浏览量：{{ pet.viewCount || 0 }}</p>
            </div>
            
            <el-divider></el-divider>
            
            <!-- 联系方式 -->
            <div class="info-section contact-section">
              <h4><i class="el-icon-phone"></i> 联系方式</h4>
              <div class="contact-info">
                <div class="contact-item">
                  <i class="el-icon-user"></i>
                  <span>联系人：</span>
                  <strong>{{ pet.contactName }}</strong>
                </div>
                <div class="contact-item">
                  <i class="el-icon-phone"></i>
                  <span>电话：</span>
                  <strong>{{ pet.contactPhone }}</strong>
                </div>
                <div class="contact-item" v-if="pet.contactWechat">
                  <i class="el-icon-chat-dot-round"></i>
                  <span>微信：</span>
                  <strong>{{ pet.contactWechat }}</strong>
                </div>
              </div>
              <el-button type="primary" icon="el-icon-message" class="contact-btn">
                联系领养
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
      
      <!-- 加载状态 -->
      <el-empty v-else description="加载中..." :image-size="200"></el-empty>
    </div>
    
    <image-gallery-viewer
      :visible.sync="galleryVisible"
      :images="galleryImages"
      :initial-index="galleryIndex"
      @close="galleryVisible = false"
    ></image-gallery-viewer>
  </div>
</template>

<script>
import { getPetDetail } from '@/api/pet'
import { formatFullTime } from '@/utils/timeUtil'
import PawIcon from '@/components/PawIcon.vue'
import ImageGalleryViewer from '@/components/ImageGalleryViewer.vue'

export default {
  name: 'PetDetail',
  components: {
    PawIcon,
    ImageGalleryViewer
  },
  data() {
    return {
      pet: null,
      defaultImage: 'https://images.unsplash.com/photo-1587300003381-16d9e0e4c6e8?w=600&h=400&fit=crop',
      galleryVisible: false,
      galleryIndex: 0
    }
  },
  computed: {
    isLoggedIn() {
      return this.$store.getters.isLoggedIn
    },
    galleryImages() {
      if (!this.pet) return []
      
      let images = []
      
      if (this.pet.photoUrls) {
        if (this.pet.photoUrls.startsWith('[')) {
          try {
            const parsed = JSON.parse(this.pet.photoUrls)
            images = Array.isArray(parsed) ? parsed : []
          } catch (e) {
            console.error('解析图片集失败:', e)
          }
        } else {
          images = this.pet.photoUrls.split(',').filter(url => url.trim())
        }
      }
      
      return images
    },
    mainImage() {
      if (this.pet && this.pet.coverPhotoUrl) {
        return this.pet.coverPhotoUrl
      }
      return this.galleryImages.length > 0 ? this.galleryImages[0] : null
    }
  },
  mounted() {
    this.loadPetDetail()
  },
  methods: {
    async loadPetDetail() {
      try {
        const res = await getPetDetail(this.$route.params.id)
        this.pet = res.data
      } catch (error) {
        this.$message.error('加载失败')
      }
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
    getStatusType(status) {
      const map = { 1: 'success', 2: 'info', 3: 'warning' }
      return map[status] || 'info'
    },
    getGenderName(gender) {
      const map = { 1: '公 ♂', 2: '母 ♀', 0: '未知' }
      return map[gender] || '未知'
    },
    formatCreateTime(time) {
      return formatFullTime(time)
    },
    openGallery(index) {
      this.galleryIndex = index
      this.galleryVisible = true
    }
  }
}
</script>

<style scoped>
.pet-detail-page {
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

.detail-card {
  margin: 30px auto;
  border-radius: 16px;
}

.detail-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 20px;
}

.pet-gallery {
  position: sticky;
  top: 80px;
}

.main-image {
  position: relative;
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.main-image img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  display: block;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

.image-overlay i {
  font-size: 48px;
  color: white;
}

.main-image:hover .image-overlay {
  opacity: 1;
}

.thumbnail-list {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.thumbnail-item {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.thumbnail-item:hover {
  border-color: #667eea;
}

.thumbnail-item.active {
  border-color: #667eea;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.more-btn {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 18px;
  color: #667eea;
  font-weight: bold;
}

.more-btn:hover {
  background: #e9ecef;
}

.gallery-tip {
  margin-top: 15px;
  padding: 14px 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  border-radius: 8px;
  text-align: center;
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e0e3e8;
}

.gallery-tip:hover {
  background: linear-gradient(135deg, #e9ecef 0%, #dfe3e8 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.gallery-tip i {
  color: #667eea;
}

.gallery-tip-text {
  margin: 0 8px;
  font-weight: 500;
}

.gallery-arrow {
  margin-left: 4px;
  transition: transform 0.3s;
}

.gallery-tip:hover .gallery-arrow {
  transform: translateX(4px);
}

.image-placeholder {
  margin-top: 15px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  text-align: center;
  color: #909399;
  cursor: pointer;
  transition: all 0.3s;
}

.image-placeholder:hover {
  background: #e9ecef;
}

.image-placeholder i {
  font-size: 24px;
  margin-right: 8px;
}

.no-image-placeholder {
  width: 100%;
  height: 400px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.no-image-text {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.no-image-text span {
  font-size: 48px;
  font-weight: 300;
  color: #909399;
  letter-spacing: 8px;
}

.pet-info {
  padding: 10px 0;
}

.pet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.pet-info h2 {
  font-size: 32px;
  margin: 0;
  color: #303133;
  font-weight: 600;
}

.info-section {
  margin: 25px 0;
}

.info-section h4 {
  color: #606266;
  font-size: 16px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.location-text {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #606266;
  margin: 0;
}

.address {
  color: #909399;
  font-size: 14px;
}

.description {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
  margin: 0;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.create-time {
  font-size: 15px;
  color: #606266;
  margin: 0;
  padding: 10px 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.view-count {
  font-size: 14px;
  color: #909399;
  margin: 10px 0 0 0;
  display: flex;
  align-items: center;
  gap: 5px;
}

.contact-section {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  padding: 25px;
  border-radius: 12px;
  margin-top: 30px;
}

.contact-info {
  margin: 20px 0;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 12px 0;
  font-size: 15px;
}

.contact-item i {
  font-size: 18px;
  color: #667eea;
}

.contact-item span {
  color: #909399;
}

.contact-item strong {
  color: #303133;
}

.contact-btn {
  width: 100%;
  margin-top: 20px;
  height: 45px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .detail-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .pet-gallery {
    position: relative;
    top: 0;
  }
}
</style>
