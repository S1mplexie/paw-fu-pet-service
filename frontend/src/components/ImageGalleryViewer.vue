<template>
  <el-dialog
    :visible.sync="dialogVisible"
    custom-class="gallery-dialog"
    :modal-append-to-body="true"
    :append-to-body="true"
    @close="handleClose"
  >
    <div class="gallery-container" @click.self="handleClose">
      <button class="nav-btn prev-btn" @click="prevImage" :disabled="currentIndex === 0">
        <i class="el-icon-arrow-left"></i>
      </button>
      
      <div class="main-image-wrapper">
        <img :src="currentImage" :alt="'图片' + (currentIndex + 1)" class="main-image">
      </div>
      
      <button class="nav-btn next-btn" @click="nextImage" :disabled="currentIndex === images.length - 1">
        <i class="el-icon-arrow-right"></i>
      </button>
      
      <button class="close-btn" @click="handleClose">
        <i class="el-icon-close"></i>
      </button>
      
      <div class="image-counter">
        {{ currentIndex + 1 }} / {{ images.length }}
      </div>
    </div>
    
    <div class="thumbnail-bar" v-if="images.length > 1">
      <div
        v-for="(image, index) in images"
        :key="index"
        class="thumbnail-item"
        :class="{ active: index === currentIndex }"
        @click="currentIndex = index"
      >
        <img :src="image" :alt="'缩略图' + (index + 1)">
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'ImageGalleryViewer',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    images: {
      type: Array,
      default: () => []
    },
    initialIndex: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      currentIndex: 0
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    },
    currentImage() {
      return this.images[this.currentIndex] || ''
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.currentIndex = this.initialIndex
        this.bindKeyEvent()
      } else {
        this.unbindKeyEvent()
      }
    }
  },
  methods: {
    prevImage() {
      if (this.currentIndex > 0) {
        this.currentIndex--
      }
    },
    nextImage() {
      if (this.currentIndex < this.images.length - 1) {
        this.currentIndex++
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.$emit('close')
    },
    handleKeydown(e) {
      switch (e.key) {
        case 'ArrowLeft':
          this.prevImage()
          break
        case 'ArrowRight':
          this.nextImage()
          break
        case 'Escape':
          this.handleClose()
          break
      }
    },
    bindKeyEvent() {
      document.addEventListener('keydown', this.handleKeydown)
    },
    unbindKeyEvent() {
      document.removeEventListener('keydown', this.handleKeydown)
    }
  },
  beforeDestroy() {
    this.unbindKeyEvent()
  }
}
</script>

<style scoped>
.gallery-container {
  position: relative;
  width: 100%;
  height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
}

.main-image-wrapper {
  width: 85%;
  height: 90%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.main-image {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  object-fit: contain;
}

.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 24px;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
}

.nav-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.prev-btn {
  left: 20px;
}

.next-btn {
  right: 20px;
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 20px;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.image-counter {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
}

.thumbnail-bar {
  display: flex;
  gap: 10px;
  padding: 15px;
  background: #f5f5f5;
  overflow-x: auto;
  justify-content: center;
}

.thumbnail-item {
  width: 80px;
  height: 60px;
  border: 2px solid transparent;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  flex-shrink: 0;
}

.thumbnail-item:hover {
  border-color: #409EFF;
}

.thumbnail-item.active {
  border-color: #409EFF;
  box-shadow: 0 0 8px rgba(64, 158, 255, 0.5);
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>

<style>
.gallery-dialog {
  max-width: 95vw !important;
  background: transparent !important;
  box-shadow: none !important;
}

.gallery-dialog .el-dialog__header {
  display: none;
}

.gallery-dialog .el-dialog__body {
  padding: 0 !important;
}
</style>
