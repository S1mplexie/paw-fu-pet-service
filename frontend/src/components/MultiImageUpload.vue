<template>
  <div class="multi-image-upload">
    <div class="image-list">
      <div
        v-for="(image, index) in imageList"
        :key="index"
        class="image-item"
        draggable="true"
        @dragstart="handleDragStart($event, index)"
        @dragover.prevent="handleDragOver($event, index)"
        @drop="handleDrop($event, index)"
      >
        <img :src="image" class="image">
        <div class="image-actions">
          <i class="el-icon-zoom-in" @click.stop="handlePreview(index)"></i>
          <i class="el-icon-delete" @click.stop="handleRemove(index)"></i>
        </div>
        <div class="image-index">{{ index + 1 }}</div>
      </div>
      
      <el-upload
        v-if="imageList.length < maxCount"
        class="upload-item"
        :action="uploadUrl"
        :headers="headers"
        :show-file-list="false"
        :on-success="handleSuccess"
        :on-error="handleError"
        :before-upload="beforeUpload"
        :accept="acceptTypes"
      >
        <div class="upload-placeholder">
          <i class="el-icon-plus"></i>
          <div class="upload-text">{{ imageList.length }}/{{ maxCount }}</div>
        </div>
      </el-upload>
    </div>
    
    <div class="upload-tips">
      <i class="el-icon-info"></i>
      <span>最多上传{{ maxCount }}张图片，单张不超过{{ maxSize }}MB，支持拖拽排序</span>
    </div>
    
    <el-dialog :visible.sync="previewVisible" append-to-body>
      <img width="100%" :src="previewUrl" alt="预览">
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'MultiImageUpload',
  props: {
    value: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'pets'
    },
    maxCount: {
      type: Number,
      default: 9
    },
    maxSize: {
      type: Number,
      default: 5
    }
  },
  data() {
    return {
      imageList: [],
      previewVisible: false,
      previewUrl: '',
      acceptTypes: 'image/jpeg,image/jpg,image/png',
      dragIndex: null
    }
  },
  computed: {
    uploadUrl() {
      return `${process.env.VUE_APP_BASE_URL || 'http://localhost:8080/api'}/file/upload?type=${this.type}`
    },
    headers() {
      const token = localStorage.getItem('token')
      return token ? { Authorization: `Bearer ${token}` } : {}
    }
  },
  watch: {
    value: {
      handler(val) {
        if (!val) {
          this.imageList = []
          return
        }
        
        if (val.startsWith('[')) {
          try {
            const parsed = JSON.parse(val)
            this.imageList = Array.isArray(parsed) ? parsed : []
          } catch (e) {
            console.error('解析图片集失败:', e)
            this.imageList = []
          }
        } else {
          this.imageList = val.split(',').filter(url => url.trim())
        }
      },
      immediate: true
    }
  },
  methods: {
    beforeUpload(file) {
      const isImage = this.acceptTypes.split(',').some(type => file.type === type)
      const isLtSize = file.size / 1024 / 1024 < this.maxSize

      if (!isImage) {
        this.$message.error('只能上传 JPG/PNG 格式的图片！')
        return false
      }
      if (!isLtSize) {
        this.$message.error(`图片大小不能超过 ${this.maxSize}MB！`)
        return false
      }
      if (this.imageList.length >= this.maxCount) {
        this.$message.error(`最多上传 ${this.maxCount} 张图片！`)
        return false
      }
      return true
    },
    handleSuccess(response) {
      if (response.code === 200) {
        this.imageList.push(response.data)
        this.emitChange()
        this.$message.success('上传成功')
      } else {
        this.$message.error(response.message || '上传失败')
      }
    },
    handleError() {
      this.$message.error('上传失败，请重试')
    },
    handlePreview(index) {
      this.previewUrl = this.imageList[index]
      this.previewVisible = true
    },
    handleRemove(index) {
      this.imageList.splice(index, 1)
      this.emitChange()
    },
    handleDragStart(e, index) {
      this.dragIndex = index
      e.dataTransfer.effectAllowed = 'move'
    },
    handleDragOver(e, index) {
      e.dataTransfer.dropEffect = 'move'
    },
    handleDrop(e, dropIndex) {
      if (this.dragIndex === null || this.dragIndex === dropIndex) {
        this.dragIndex = null
        return
      }
      
      const dragItem = this.imageList[this.dragIndex]
      this.imageList.splice(this.dragIndex, 1)
      this.imageList.splice(dropIndex, 0, dragItem)
      this.dragIndex = null
      
      this.emitChange()
    },
    emitChange() {
      if (this.imageList.length === 0) {
        this.$emit('input', '')
      } else {
        const value = JSON.stringify(this.imageList)
        this.$emit('input', value)
      }
    }
  }
}
</script>

<style scoped>
.multi-image-upload {
  width: 100%;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.image-item {
  width: 148px;
  height: 148px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  position: relative;
  overflow: hidden;
  cursor: move;
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-actions {
  position: absolute;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  cursor: default;
  text-align: center;
  color: #fff;
  opacity: 0;
  font-size: 20px;
  background-color: rgba(0, 0, 0, 0.5);
  transition: opacity 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.image-actions i {
  cursor: pointer;
}

.image-item:hover .image-actions {
  opacity: 1;
}

.image-index {
  position: absolute;
  top: 5px;
  left: 5px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.upload-item {
  display: inline-block;
}

.upload-placeholder {
  width: 148px;
  height: 148px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.upload-placeholder:hover {
  border-color: #409EFF;
}

.upload-placeholder i {
  font-size: 28px;
  color: #8c939d;
}

.upload-text {
  margin-top: 8px;
  color: #8c939d;
  font-size: 12px;
}

.upload-tips {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 5px;
}
</style>
