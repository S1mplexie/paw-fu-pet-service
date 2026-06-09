<template>
  <div class="ai-float-icon" 
       :style="{ right: position.right + 'px', bottom: position.bottom + 'px' }"
       @click="handleClick"
       @mousedown="handleMouseDown"
       @touchstart="handleTouchStart">
    <div class="icon-wrapper">
      <i class="el-icon-service"></i>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AiFloatIcon',
  data() {
    return {
      position: {
        right: 30,
        bottom: 100
      },
      isDragging: false,
      startX: 0,
      startY: 0,
      startRight: 0,
      startBottom: 0
    }
  },
  mounted() {
    this.loadPosition()
    document.addEventListener('mousemove', this.handleMouseMove)
    document.addEventListener('mouseup', this.handleMouseUp)
    document.addEventListener('touchmove', this.handleTouchMove)
    document.addEventListener('touchend', this.handleTouchEnd)
  },
  beforeDestroy() {
    document.removeEventListener('mousemove', this.handleMouseMove)
    document.removeEventListener('mouseup', this.handleMouseUp)
    document.removeEventListener('touchmove', this.handleTouchMove)
    document.removeEventListener('touchend', this.handleTouchEnd)
  },
  methods: {
    loadPosition() {
      const saved = localStorage.getItem('ai-float-icon-position')
      if (saved) {
        this.position = JSON.parse(saved)
      }
    },
    savePosition() {
      localStorage.setItem('ai-float-icon-position', JSON.stringify(this.position))
    },
    handleClick(e) {
      if (!this.isDragging) {
        this.$emit('click')
      }
    },
    handleMouseDown(e) {
      this.isDragging = false
      this.startX = e.clientX
      this.startY = e.clientY
      this.startRight = this.position.right
      this.startBottom = this.position.bottom
      e.preventDefault()
    },
    handleMouseMove(e) {
      if (this.startX === 0 && this.startY === 0) return
      
      const deltaX = e.clientX - this.startX
      const deltaY = e.clientY - this.startY
      
      if (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5) {
        this.isDragging = true
      }
      
      this.position.right = Math.max(0, Math.min(window.innerWidth - 60, this.startRight - deltaX))
      this.position.bottom = Math.max(0, Math.min(window.innerHeight - 60, this.startBottom - deltaY))
    },
    handleMouseUp(e) {
      if (this.isDragging) {
        this.snapToEdge()
        this.savePosition()
      }
      this.startX = 0
      this.startY = 0
      setTimeout(() => {
        this.isDragging = false
      }, 100)
    },
    handleTouchStart(e) {
      const touch = e.touches[0]
      this.startX = touch.clientX
      this.startY = touch.clientY
      this.startRight = this.position.right
      this.startBottom = this.position.bottom
      this.isDragging = false
    },
    handleTouchMove(e) {
      if (this.startX === 0 && this.startY === 0) return
      e.preventDefault()
      
      const touch = e.touches[0]
      const deltaX = touch.clientX - this.startX
      const deltaY = touch.clientY - this.startY
      
      if (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5) {
        this.isDragging = true
      }
      
      this.position.right = Math.max(0, Math.min(window.innerWidth - 60, this.startRight - deltaX))
      this.position.bottom = Math.max(0, Math.min(window.innerHeight - 60, this.startBottom - deltaY))
    },
    handleTouchEnd(e) {
      if (this.isDragging) {
        this.snapToEdge()
        this.savePosition()
      }
      this.startX = 0
      this.startY = 0
      setTimeout(() => {
        this.isDragging = false
      }, 100)
    },
    snapToEdge() {
      const iconWidth = 56
      const currentLeft = window.innerWidth - this.position.right - iconWidth
      const currentRight = this.position.right
      
      if (currentLeft < currentRight) {
        this.position.right = window.innerWidth - iconWidth - 10
      } else {
        this.position.right = 30
      }
    }
  }
}
</script>

<style scoped>
.ai-float-icon {
  position: fixed;
  z-index: 999;
  cursor: pointer;
  user-select: none;
}

.icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: transform 0.3s, box-shadow 0.3s;
}

.icon-wrapper:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.6);
}

.icon-wrapper i {
  font-size: 28px;
  color: white;
}
</style>
