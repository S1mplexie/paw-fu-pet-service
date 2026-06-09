<template>
  <div class="ai-chat-window" 
       v-if="visible"
       :style="{ 
         width: windowSize.width + 'px', 
         height: windowSize.height + 'px',
         left: windowPosition.left + 'px',
         top: windowPosition.top + 'px'
       }">
    <div class="chat-header" @mousedown="handleMoveStart">
      <span class="title">智能客服助手</span>
      <div class="header-actions">
        <i class="el-icon-minus" @click="$emit('minimize')"></i>
        <i class="el-icon-close" @click="$emit('close')"></i>
      </div>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div class="welcome-message" v-if="messages.length === 0">
        <i class="el-icon-service"></i>
        <p class="pawfu-title">你好！我是Paw福宠物服务平台的AI助手</p>
        <p>我可以帮你解答宠物养护、健康、训练等问题</p>
      </div>
      
      <div v-for="(msg, index) in messages" :key="index" 
           :class="['message', msg.type]">
        <div class="message-content">{{ msg.content }}</div>
      </div>
      
      <div class="message assistant" v-if="loading">
        <div class="message-content loading">
          <span></span><span></span><span></span>
        </div>
      </div>
    </div>
    
    <div class="quick-questions" v-if="messages.length === 0">
      <el-tag v-for="(q, i) in quickQuestions" :key="i" 
              @click="sendQuickQuestion(q)" 
              class="quick-tag">
        {{ q }}
      </el-tag>
    </div>
    
    <div class="chat-input">
      <el-input v-model="inputText" 
                placeholder="输入你的问题..." 
                @keyup.enter.native="sendMessage"
                :disabled="loading">
      </el-input>
      <el-button type="primary" @click="sendMessage" :loading="loading">
        发送
      </el-button>
    </div>
    
    <div class="resize-handle nw" @mousedown.stop="handleResizeStart($event, 'nw')"></div>
    <div class="resize-handle ne" @mousedown.stop="handleResizeStart($event, 'ne')"></div>
    <div class="resize-handle sw" @mousedown.stop="handleResizeStart($event, 'sw')"></div>
    <div class="resize-handle se" @mousedown.stop="handleResizeStart($event, 'se')"></div>
  </div>
</template>

<script>
import { aiChat, getQuickQuestions } from '@/api/ai'

export default {
  name: 'AiChatWindow',
  props: {
    visible: Boolean
  },
  data() {
    return {
      messages: [],
      inputText: '',
      loading: false,
      quickQuestions: [],
      windowSize: {
        width: 380,
        height: 500
      },
      windowPosition: {
        left: null,
        top: null
      },
      isResizing: false,
      resizeDirection: '',
      resizeStartX: 0,
      resizeStartY: 0,
      resizeStartWidth: 0,
      resizeStartHeight: 0,
      resizeStartLeft: 0,
      resizeStartTop: 0,
      isMoving: false,
      moveStartX: 0,
      moveStartY: 0,
      moveStartLeft: 0,
      moveStartTop: 0
    }
  },
  mounted() {
    this.loadQuickQuestions()
    this.loadWindowSize()
    this.loadWindowPosition()
    document.addEventListener('mousemove', this.handleMouseMove)
    document.addEventListener('mouseup', this.handleMouseUp)
  },
  beforeDestroy() {
    document.removeEventListener('mousemove', this.handleMouseMove)
    document.removeEventListener('mouseup', this.handleMouseUp)
  },
  methods: {
    async loadQuickQuestions() {
      try {
        const res = await getQuickQuestions()
        this.quickQuestions = res.data || []
      } catch (error) {
        this.quickQuestions = [
          '如何喂养幼犬？',
          '狗狗呕吐怎么办？',
          '宠物驱虫多久一次？'
        ]
      }
    },
    async sendMessage() {
      if (!this.inputText.trim() || this.loading) return
      
      const question = this.inputText.trim()
      this.messages.push({
        type: 'user',
        content: question
      })
      this.inputText = ''
      this.scrollToBottom()
      
      this.loading = true
      try {
        const res = await aiChat({ question })
        this.messages.push({
          type: 'assistant',
          content: res.data.answer
        })
      } catch (error) {
        this.messages.push({
          type: 'assistant',
          content: '抱歉，服务暂时不可用，请稍后再试。'
        })
      } finally {
        this.loading = false
        this.scrollToBottom()
      }
    },
    sendQuickQuestion(question) {
      this.inputText = question
      this.sendMessage()
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },
    loadWindowSize() {
      const saved = localStorage.getItem('ai-chat-window-size')
      if (saved) {
        this.windowSize = JSON.parse(saved)
      }
    },
    saveWindowSize() {
      localStorage.setItem('ai-chat-window-size', JSON.stringify(this.windowSize))
    },
    loadWindowPosition() {
      const saved = localStorage.getItem('ai-chat-window-position')
      if (saved) {
        this.windowPosition = JSON.parse(saved)
      } else {
        this.windowPosition = {
          left: window.innerWidth - this.windowSize.width - 30,
          top: 180
        }
      }
    },
    saveWindowPosition() {
      localStorage.setItem('ai-chat-window-position', JSON.stringify(this.windowPosition))
    },
    handleMoveStart(e) {
      if (e.target.classList.contains('header-actions') || 
          e.target.closest('.header-actions')) {
        return
      }
      this.isMoving = true
      this.moveStartX = e.clientX
      this.moveStartY = e.clientY
      this.moveStartLeft = this.windowPosition.left
      this.moveStartTop = this.windowPosition.top
      e.preventDefault()
    },
    handleResizeStart(e, direction) {
      this.isResizing = true
      this.resizeDirection = direction
      this.resizeStartX = e.clientX
      this.resizeStartY = e.clientY
      this.resizeStartWidth = this.windowSize.width
      this.resizeStartHeight = this.windowSize.height
      this.resizeStartLeft = this.windowPosition.left
      this.resizeStartTop = this.windowPosition.top
    },
    handleMouseMove(e) {
      if (this.isMoving) {
        const deltaX = e.clientX - this.moveStartX
        const deltaY = e.clientY - this.moveStartY
        
        this.windowPosition.left = Math.max(0, Math.min(window.innerWidth - this.windowSize.width, this.moveStartLeft + deltaX))
        this.windowPosition.top = Math.max(0, Math.min(window.innerHeight - this.windowSize.height, this.moveStartTop + deltaY))
      }
      
      if (this.isResizing) {
        const deltaX = e.clientX - this.resizeStartX
        const deltaY = e.clientY - this.resizeStartY
        const dir = this.resizeDirection
        
        let newWidth = this.resizeStartWidth
        let newHeight = this.resizeStartHeight
        let newLeft = this.resizeStartLeft
        let newTop = this.resizeStartTop
        
        if (dir.includes('e')) {
          newWidth = Math.max(320, Math.min(800, this.resizeStartWidth + deltaX))
        }
        if (dir.includes('w')) {
          newWidth = Math.max(320, Math.min(800, this.resizeStartWidth - deltaX))
          if (newWidth !== this.resizeStartWidth) {
            newLeft = this.resizeStartLeft + (this.resizeStartWidth - newWidth)
          }
        }
        if (dir.includes('s')) {
          newHeight = Math.max(400, Math.min(800, this.resizeStartHeight + deltaY))
        }
        if (dir.includes('n')) {
          newHeight = Math.max(400, Math.min(800, this.resizeStartHeight - deltaY))
          if (newHeight !== this.resizeStartHeight) {
            newTop = this.resizeStartTop + (this.resizeStartHeight - newHeight)
          }
        }
        
        this.windowSize.width = newWidth
        this.windowSize.height = newHeight
        this.windowPosition.left = newLeft
        this.windowPosition.top = newTop
      }
    },
    handleMouseUp(e) {
      if (this.isMoving) {
        this.saveWindowPosition()
      }
      if (this.isResizing) {
        this.saveWindowSize()
        this.saveWindowPosition()
      }
      this.isMoving = false
      this.isResizing = false
    }
  }
}
</script>

<style scoped>
.ai-chat-window {
  position: fixed;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  z-index: 1000;
}

.chat-header {
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: move;
  user-select: none;
}

.chat-header .title {
  font-size: 16px;
  font-weight: 500;
}

.header-actions i {
  margin-left: 12px;
  cursor: pointer;
  font-size: 16px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.welcome-message {
  text-align: center;
  color: #909399;
  padding: 40px 20px;
}

.welcome-message i {
  font-size: 48px;
  color: #667eea;
  margin-bottom: 16px;
}

.message {
  margin-bottom: 12px;
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.message-content {
  max-width: 80%;
  padding: 10px 14px;
  border-radius: 8px;
  word-wrap: break-word;
}

.message.user .message-content {
  background: #667eea;
  color: white;
}

.message.assistant .message-content {
  background: #f5f7fa;
  color: #303133;
}

.loading span {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #667eea;
  margin-right: 4px;
  animation: bounce 1.4s infinite ease-in-out;
}

.loading span:nth-child(1) { animation-delay: -0.32s; }
.loading span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.quick-questions {
  padding: 12px 16px;
  border-top: 1px solid #eee;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-tag {
  cursor: pointer;
}

.chat-input {
  padding: 12px 16px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 8px;
}

.chat-input .el-input {
  flex: 1;
}

.resize-handle {
  position: absolute;
  width: 15px;
  height: 15px;
  z-index: 10;
}

.resize-handle.nw {
  left: 0;
  top: 0;
  cursor: nwse-resize;
}

.resize-handle.ne {
  right: 0;
  top: 0;
  cursor: nesw-resize;
}

.resize-handle.sw {
  left: 0;
  bottom: 0;
  cursor: nesw-resize;
}

.resize-handle.se {
  right: 0;
  bottom: 0;
  cursor: nwse-resize;
}

.resize-handle::before {
  content: '';
  position: absolute;
  width: 8px;
  height: 8px;
}

.resize-handle.nw::before {
  left: 3px;
  top: 3px;
  border-left: 2px solid #dcdfe6;
  border-top: 2px solid #dcdfe6;
}

.resize-handle.ne::before {
  right: 3px;
  top: 3px;
  border-right: 2px solid #dcdfe6;
  border-top: 2px solid #dcdfe6;
}

.resize-handle.sw::before {
  left: 3px;
  bottom: 3px;
  border-left: 2px solid #dcdfe6;
  border-bottom: 2px solid #dcdfe6;
}

.resize-handle.se::before {
  right: 3px;
  bottom: 3px;
  border-right: 2px solid #dcdfe6;
  border-bottom: 2px solid #dcdfe6;
}
</style>
