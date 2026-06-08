<template>
  <el-upload
    class="image-uploader"
    :action="uploadUrl"
    :headers="headers"
    :show-file-list="false"
    :on-success="handleSuccess"
    :on-error="handleError"
    :before-upload="beforeUpload"
    :accept="acceptTypes"
  >
    <div v-if="imageUrl" class="image-preview">
      <img :src="imageUrl" class="image">
      <div class="image-actions">
        <i class="el-icon-zoom-in" @click.stop="handlePreview"></i>
        <i class="el-icon-delete" @click.stop="handleRemove"></i>
      </div>
    </div>
    <div v-else class="upload-placeholder">
      <i class="el-icon-plus"></i>
      <div class="upload-text">{{ placeholder }}</div>
    </div>
    
    <el-dialog :visible.sync="previewVisible" append-to-body>
      <img width="100%" :src="imageUrl" alt="预览">
    </el-dialog>
  </el-upload>
</template>

<script>
export default {
  name: 'ImageUpload',
  props: {
    value: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'pets'
    },
    placeholder: {
      type: String,
      default: '点击上传图片'
    }
  },
  data() {
    return {
      previewVisible: false,
      acceptTypes: 'image/jpeg,image/jpg,image/png,image/gif,image/webp'
    }
  },
  computed: {
    imageUrl() {
      return this.value
    },
    uploadUrl() {
      return `${process.env.VUE_APP_BASE_URL || 'http://localhost:8080/api'}/file/upload?type=${this.type}`
    },
    headers() {
      const token = localStorage.getItem('token')
      return token ? { Authorization: `Bearer ${token}` } : {}
    }
  },
  methods: {
    beforeUpload(file) {
      const isImage = this.acceptTypes.split(',').some(type => file.type === type)
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isImage) {
        this.$message.error('只能上传 JPG/PNG/GIF 格式的图片！')
        return false
      }
      if (!isLt10M) {
        this.$message.error('图片大小不能超过 10MB！')
        return false
      }
      return true
    },
    handleSuccess(response) {
      if (response.code === 200) {
        this.$emit('input', response.data)
        this.$message.success('上传成功')
      } else {
        this.$message.error(response.message || '上传失败')
      }
    },
    handleError() {
      this.$message.error('上传失败，请重试')
    },
    handlePreview() {
      this.previewVisible = true
    },
    handleRemove() {
      this.$emit('input', '')
    }
  }
}
</script>

<style scoped>
.image-uploader {
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

.image-preview {
  width: 148px;
  height: 148px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  position: relative;
  overflow: hidden;
}

.image {
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

.image-preview:hover .image-actions {
  opacity: 1;
}
</style>
