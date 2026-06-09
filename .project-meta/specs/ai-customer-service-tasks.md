# AI智能客服功能编码任务规划

## 1. 后端基础架构搭建

### 1.1 数据模型类创建
- [ ] 创建请求模型 `AiChatRequest.java`（`src/main/java/com/pawfu/petservice/ai/dto/AiChatRequest.java`）
  - 添加 `question` 字段，使用 `@NotBlank` 和 `@Length(max=1000)` 注解进行参数校验
  - 添加 Swagger `@ApiModel` 和 `@ApiModelProperty` 注解用于API文档生成
  
- [ ] 创建响应模型 `AiChatResponse.java`（`src/main/java/com/pawfu/petservice/ai/dto/AiChatResponse.java`）
  - 添加 `question`、`answer`、`timestamp` 字段
  - 使用 `@Builder` 注解支持构建器模式
  
- [ ] 创建DeepSeek消息模型 `Message.java`（`src/main/java/com/pawfu/petservice/ai/model/Message.java`）
  - 添加 `role`（system/user/assistant）和 `content` 字段
  
- [ ] 创建DeepSeek请求模型 `DeepSeekRequest.java`（`src/main/java/com/pawfu/petservice/ai/model/DeepSeekRequest.java`）
  - 添加 `model`、`messages`、`maxTokens` 字段
  
- [ ] 创建DeepSeek响应模型 `DeepSeekResponse.java`（`src/main/java/com/pawfu/petservice/ai/model/DeepSeekResponse.java`）
  - 添加 `id`、`object`、`choices` 字段
  - 内部类 `Choice` 包含 `index`、`message`、`finishReason`

### 1.2 异常处理类创建
- [ ] 创建AI服务异常类 `AiServiceException.java`（`src/main/java/com/pawfu/petservice/ai/exception/AiServiceException.java`）
  - 继承 `RuntimeException`
  - 提供两个构造函数：单参数（message）和双参数（message + cause）
  
- [ ] 创建全局异常处理器 `AiExceptionHandler.java`（`src/main/java/com/pawfu/petservice/ai/exception/AiExceptionHandler.java`）
  - 使用 `@RestControllerAdvice` 注解
  - 处理 `AiServiceException`、`MethodArgumentNotValidException`、`Exception` 异常
  - 返回统一的 `Result` 响应格式

### 1.3 统一响应模型完善
- [ ] 创建或确认统一响应类 `Result.java`（`src/main/java/com/pawfu/petservice/common/Result.java`）
  - 添加 `code`、`message`、`data` 字段
  - 提供 `success()` 和 `error()` 静态方法
  - 添加 Swagger 注解

## 2. DeepSeek API集成实现

### 2.1 API配置类开发
- [ ] 创建DeepSeek配置类 `DeepSeekConfig.java`（`src/main/java/com/pawfu/petservice/ai/config/DeepSeekConfig.java`）
  - 使用 `@ConfigurationProperties(prefix = "deepseek")` 注解
  - 添加配置字段：
    - `endpoint`：API端点，默认值 "https://api.deepseek.com/v1/chat/completions"
    - `apiKey`：从环境变量 `${DEEPSEEK_API_KEY}` 读取
    - `timeout`：请求超时时间，默认15000ms
    - `connectTimeout`：连接超时时间，默认5000ms
    - `model`：模型名称，默认 "deepseek-chat"
    - `maxTokens`：最大Token数，默认2000
  - 添加 getter/setter 方法
  
- [ ] 在配置文件中添加DeepSeek配置（`src/main/resources/application.yml`）
  ```yaml
  deepseek:
    endpoint: https://api.deepseek.com/v1/chat/completions
    api-key: ${DEEPSEEK_API_KEY}
    timeout: 15000
    connect-timeout: 5000
    model: deepseek-chat
    max-tokens: 2000
  ```

### 2.2 DeepSeek客户端实现
- [ ] 创建DeepSeek客户端类 `DeepSeekClient.java`（`src/main/java/com/pawfu/petservice/ai/client/DeepSeekClient.java`）
  - 使用 `@Component` 注解
  - 注入 `DeepSeekConfig` 配置类
  - 在 `@PostConstruct` 方法中初始化 `RestTemplate`：
    - 配置连接超时和读取超时
    - 使用 `HttpComponentsClientHttpRequestFactory`
  - 实现 `chat(List<Message> messages)` 方法：
    - 构建请求体（model、messages、max_tokens）
    - 设置请求头（Content-Type、Bearer Auth）
    - 发送POST请求到DeepSeek API端点
    - 解析响应并返回AI回答内容
    - 捕获并处理 `HttpClientErrorException`（API调用失败）
    - 捕获并处理 `ResourceAccessException`（超时）
    - 记录错误日志
  - 实现 `parseResponse(Map response)` 私有方法用于解析DeepSeek响应

## 3. 业务逻辑层实现

### 3.1 频率限制器开发
- [ ] 创建频率限制器 `RateLimiter.java`（`src/main/java/com/pawfu/petservice/ai/limiter/RateLimiter.java`）
  - 使用 `@Component` 注解
  - 注入 `StringRedisTemplate`
  - 定义常量：`LIMIT = 20`（限制阈值）、`WINDOW = 60`（时间窗口60秒）
  - 实现 `tryAcquire(String clientIp)` 方法：
    - 使用Redis ZSet实现滑动窗口算法
    - 移除窗口外的旧记录
    - 检查当前窗口内请求次数是否超限
    - 添加当前请求记录
    - 设置过期时间

### 3.2 上下文提示词配置
- [ ] 在配置文件中添加AI上下文提示词配置（`src/main/resources/application.yml`）
  ```yaml
  ai:
    prompt:
      context: |
        你是Paw福宠物服务平台的AI智能客服助手，专注于宠物养护、健康、训练和领养等领域的知识咨询。
        
        请遵循以下原则：
        1. 仅回答与宠物相关的问题，包括：
           - 宠物养护知识（喂养、护理、日常照料）
           - 宠物健康问题（常见疾病、症状判断、就医建议）
           - 宠物训练技巧（行为训练、社会化训练）
           - 宠物领养流程（平台功能介绍、领养指南）
           - 平台使用帮助
        
        2. 当用户询问非宠物领域问题时，请礼貌拒绝并引导：
           "我是宠物服务助手，专注于宠物领域的咨询。请询问宠物相关的问题，我会为您提供专业建议。"
        
        3. 回答应专业、准确、友好，避免过于技术化的语言。
        
        4. 当涉及宠物健康问题时，请提醒用户及时就医，不要给出确诊建议。
        
        5. 保持回答简洁明了，控制在300字以内。
    
    quick-questions:
      - 如何喂养幼犬？
      - 狗狗呕吐怎么办？
      - 如何训练猫咪使用猫砂？
      - 宠物驱虫多久一次？
      - 如何领养宠物？
  ```

### 3.3 Service层实现
- [ ] 创建AI客服服务类 `AiCustomerService.java`（`src/main/java/com/pawfu/petservice/ai/service/AiCustomerService.java`）
  - 使用 `@Service` 注解
  - 注入 `DeepSeekClient`、`RateLimiter`
  - 使用 `@Value` 注入上下文提示词和快捷问题列表
  - 实现 `chat(AiChatRequest request, String clientIp)` 方法：
    1. 调用 `rateLimiter.tryAcquire()` 进行频率限制检查，超限抛出业务异常
    2. 调用 `sanitizeInput()` 过滤输入内容（XSS防护）
    3. 调用 `buildMessages()` 构建消息列表（系统提示词 + 用户问题）
    4. 调用 `deepSeekClient.chat()` 获取AI回答
    5. 构建并返回 `AiChatResponse` 响应对象
  - 实现 `buildMessages(String question)` 私有方法：
    - 创建系统消息（role="system"，content=上下文提示词）
    - 创建用户消息（role="user"，content=用户问题）
    - 返回消息列表
  - 实现 `sanitizeInput(String input)` 私有方法：
    - 使用正则表达式移除HTML标签（XSS防护）
    - 检查长度是否超过1000字符，超限抛出异常
    - 去除首尾空白字符
  - 实现 `getQuickQuestions()` 方法返回快捷问题列表

## 4. 控制器层实现

### 4.1 Controller开发
- [ ] 创建AI客服控制器 `AiCustomerController.java`（`src/main/java/com/pawfu/petservice/ai/controller/AiCustomerController.java`）
  - 使用 `@RestController` 和 `@RequestMapping("/api/ai-customer")` 注解
  - 使用 `@Api(tags = "AI智能客服")` 注解
  - 注入 `AiCustomerService`
  - 实现智能问答接口 `@PostMapping("/chat")`：
    - 方法签名：`Result<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request, HttpServletRequest httpRequest)`
    - 从 `HttpServletRequest` 获取客户端IP地址
    - 调用 `aiCustomerService.chat()` 处理请求
    - 返回 `Result.success(response)`
    - 添加 `@ApiOperation` 注解
  - 实现获取快捷问题接口 `@GetMapping("/quick-questions")`：
    - 调用 `aiCustomerService.getQuickQuestions()`
    - 返回快捷问题列表
    - 添加 `@ApiOperation` 注解
  - 实现 `getClientIp(HttpServletRequest request)` 私有方法：
    - 从 `X-Forwarded-For`、`X-Real-IP` 等 header 获取真实IP
    - 如果没有，则使用 `request.getRemoteAddr()`

## 5. 前端组件开发

### 5.1 Vuex状态管理模块
- [ ] 创建聊天状态管理模块 `chat.js`（`src/store/modules/chat.js`）
  - 定义 `state`：
    - `isOpen`：窗口是否打开，默认false
    - `messages`：消息列表，默认空数组
    - `loading`：是否加载中，默认false
    - `unreadCount`：未读消息数，默认0
    - `iconPosition`：图标位置，默认{x: 0, y: 0}
  - 定义 `mutations`：
    - `TOGGLE_WINDOW`：切换窗口打开/关闭状态
    - `ADD_MESSAGE`：添加消息到列表（自动添加timestamp）
    - `SET_LOADING`：设置加载状态
    - `SET_TYPING_COMPLETE`：标记打字动画完成
    - `CLEAR_MESSAGES`：清空消息列表
    - `SET_ICON_POSITION`：设置图标位置
  - 定义 `actions`：
    - `sendMessage`：异步发送消息（添加用户消息 → 设置loading → 调用API → 添加AI消息）
  - 使用 `namespaced: true` 启用命名空间
  
- [ ] 在Vuex主入口文件中注册聊天模块（`src/store/index.js`）
  - 导入 `chat.js` 模块
  - 在 `modules` 中添加 `chat: chatModule`

### 5.2 API封装
- [ ] 创建聊天API封装文件 `chat.js`（`src/api/chat.js`）
  - 导入 `request` 工具（axios实例）
  - 实现 `chat(data)` 函数：
    - 发送POST请求到 `/api/ai-customer/chat`
    - 参数：`{ question: string }`
  - 实现 `getQuickQuestions()` 函数：
    - 发送GET请求到 `/api/ai-customer/quick-questions`

### 5.3 打字动画组件开发
- [ ] 创建打字动画组件 `TypingText.vue`（`src/components/ai-chat/TypingText.vue`）
  - 定义 `props`：
    - `text`：要显示的文本内容
  - 定义 `data`：
    - `displayText`：当前显示的文本，初始为空字符串
    - `currentIndex`：当前字符索引，初始为0
    - `complete`：是否完成，初始为false
  - 在 `mounted` 钩子中启动打字动画：
    - 使用 `setInterval` 每50ms添加一个字符
    - 所有字符显示完毕后清除定时器
    - 触发 `complete` 事件
  - 实现模板：
    - 显示 `displayText` 内容
    - 显示闪烁光标（未完成时）
  - 实现样式：
    - 光标闪烁动画（`@keyframes blink`）

### 5.4 悬浮图标组件开发
- [ ] 创建悬浮图标组件 `FloatingIcon.vue`（`src/components/ai-chat/FloatingIcon.vue`）
  - 定义 `data`：
    - `position`：图标位置 {x: 0, y: 0}
    - `isDragging`：是否正在拖拽，初始为false
    - `dragStart`：拖拽起始点 {x: 0, y: 0}
  - 定义 `computed`：
    - `iconStyle`：根据position计算样式（right、top）
    - `unreadCount`：从Vuex获取未读消息数
    - `isOpen`：从Vuex获取窗口打开状态
  - 在 `mounted` 钩子中：
    - 调用 `loadPosition()` 从localStorage读取位置
    - 监听 `mousemove` 和 `mouseup` 事件（document级别）
  - 实现 `methods`：
    - `handleClick()`：点击打开聊天窗口（非拖拽时）
    - `handleMouseDown(e)`：鼠标按下，记录拖拽起点
    - `handleTouchStart(e)`：触摸开始（移动端支持）
    - `handleMouseMove(e)`：鼠标移动，更新位置（移动超过5px才算拖拽）
    - `handleMouseUp(e)`：鼠标释放，结束拖拽
    - `updatePosition(dx, dy)`：更新位置（带边界检查，限制在可视区域内）
    - `savePosition()`：保存位置到localStorage
    - `loadPosition()`：从localStorage读取位置，错误时使用默认位置
  - 在 `watch` 中监听 `position` 变化，自动保存到localStorage
  - 实现模板：
    - 使用 `el-badge` 显示未读消息数
    - 显示客服图标（`el-icon-service`）
    - 绑定点击和拖拽事件
  - 实现样式：
    - 固定定位在页面右侧边缘
    - 圆形图标，渐变背景色
    - 悬停时缩放动画
    - z-index设置为999

### 5.5 聊天窗口组件开发
- [ ] 创建聊天窗口组件 `ChatWindow.vue`（`src/components/ai-chat/ChatWindow.vue`）
  - 定义 `data`：
    - `inputText`：输入框内容，初始为空
    - `quickQuestions`：快捷问题列表，初始为空数组
    - `showQuickQuestions`：是否显示快捷问题，初始为true
    - `windowPosition`：窗口位置，用于拖拽
  - 定义 `computed`：
    - `isOpen`：从Vuex获取窗口打开状态
    - `messages`：从Vuex获取消息列表
    - `loading`：从Vuex获取加载状态
    - `windowStyle`：根据窗口位置计算样式
  - 在 `mounted` 钩子中：
    - 调用 `loadQuickQuestions()` 加载快捷问题列表
  - 实现 `methods`：
    - `sendMessage()`：发送消息
      - 验证输入不为空且未在加载中
      - 清空输入框
      - 提交用户消息到Vuex
      - 隐藏快捷问题
      - 设置loading状态
      - 调用chat API
      - 添加AI消息（带打字动画标记）
      - 滚动到底部
      - 错误处理：显示错误提示
      - finally：关闭loading状态
    - `sendQuickQuestion(question)`：发送快捷问题
    - `onTypingComplete(index)`：打字动画完成，更新消息状态
    - `loadQuickQuestions()`：从后端加载快捷问题列表
    - `scrollToBottom()`：滚动消息列表到底部
    - `minimize()`：最小化窗口
    - `close()`：关闭窗口
    - `handleWindowDrag(e)`：窗口拖拽处理（标题栏拖拽）
  - 实现模板：
    - 使用 `<transition>` 实现滑入/滑出动画
    - 标题栏：显示标题、最小化按钮、关闭按钮
    - 消息列表：
      - 遍历messages显示消息气泡
      - 用户消息：右对齐、蓝色气泡
      - AI消息：左对齐、灰色气泡
      - 使用 `TypingText` 组件显示打字动画
    - 加载状态：显示loading图标和"AI正在思考..."文本
    - 快捷问题区域：显示快捷问题标签（可点击）
    - 输入框：
      - `el-input` 组件
      - 支持Enter键发送
      - 禁用状态（加载中）
      - 发送按钮（`el-icon-s-promotion`）
  - 实现样式：
    - 固定定位，窗口尺寸400px × 500px
    - 白色背景，圆角，阴影
    - z-index设置为1000
    - 标题栏渐变背景色
    - 消息气泡样式（用户蓝色、AI灰色）
    - 滑入/滑出动画（`@keyframes slide-in`、`@keyframes slide-out`）

### 5.6 主组件集成
- [ ] 创建AI客服主组件 `AiCustomerService.vue`（`src/components/ai-chat/AiCustomerService.vue`）
  - 导入 `FloatingIcon` 和 `ChatWindow` 组件
  - 实现模板：
    - 包含 `<floating-icon>` 组件
    - 包含 `<chat-window>` 组件
  
- [ ] 在App.vue或布局组件中集成AI客服组件（`src/App.vue`）
  - 导入 `AiCustomerService` 组件
  - 在模板中添加 `<ai-customer-service />` 组件

## 6. 移动端适配优化

### 6.1 响应式布局适配
- [ ] 优化聊天窗口组件的移动端样式（`ChatWindow.vue`）
  - 添加媒体查询 `@media (max-width: 768px)`
  - 移动端窗口尺寸调整为屏幕宽度80%、高度70%
  - 移动端窗口居中显示
  - 调整字体大小和间距
  
- [ ] 优化悬浮图标组件的移动端交互（`FloatingIcon.vue`）
  - 添加触摸事件支持（`@touchstart`、`@touchmove`、`@touchend`）
  - 调整图标尺寸（移动端稍小）
  - 优化拖拽响应灵敏度

## 7. 测试与验证

### 7.1 后端单元测试
- [ ] 编写DeepSeek客户端测试类 `DeepSeekClientTest.java`（`src/test/java/com/pawfu/petservice/ai/client/DeepSeekClientTest.java`）
  - 测试正常API调用和响应解析
  - 测试超时异常处理
  - 测试API错误响应处理
  
- [ ] 编写频率限制器测试类 `RateLimiterTest.java`（`src/test/java/com/pawfu/petservice/ai/limiter/RateLimiterTest.java`）
  - 测试正常请求通过
  - 测试超限请求被拒绝
  - 测试滑动窗口过期后重新计数
  
- [ ] 编写Service层测试类 `AiCustomerServiceTest.java`（`src/test/java/com/pawfu/petservice/ai/service/AiCustomerServiceTest.java`）
  - 测试输入内容过滤（XSS防护）
  - 测试消息构建（上下文提示词）
  - 测试长度限制校验
  - Mock DeepSeek客户端进行单元测试

### 7.2 后端集成测试
- [ ] 编写Controller集成测试类 `AiCustomerControllerTest.java`（`src/test/java/com/pawfu/petservice/ai/controller/AiCustomerControllerTest.java`）
  - 使用 `@SpringBootTest` 和 `@AutoConfigureMockMvc`
  - 测试智能问答接口正常调用
  - 测试参数校验（空问题、超长问题）
  - 测试频率限制生效
  - 测试快捷问题接口

### 7.3 前端组件测试
- [ ] 编写悬浮图标组件测试（可选，使用Vue Test Utils）
  - 测试点击打开聊天窗口
  - 测试拖拽功能
  - 测试位置持久化
  
- [ ] 编写聊天窗口组件测试（可选）
  - 测试消息发送和显示
  - 测试快捷问题点击
  - 测试滚动到底部

### 7.4 功能集成测试
- [ ] 执行端到端功能测试
  - 启动后端服务，验证配置加载成功
  - 启动前端应用，验证组件正常渲染
  - 测试完整对话流程：
    1. 点击悬浮图标打开聊天窗口
    2. 拖拽图标移动位置，刷新页面验证位置保持
    3. 输入问题并发送，验证AI回答显示
    4. 点击快捷问题，验证自动发送
    5. 验证打字动画效果
    6. 测试最小化和关闭功能
  - 测试错误场景：
    1. 输入超长内容，验证提示
    2. 快速连续发送，验证频率限制
    3. 模拟网络异常，验证错误提示

### 7.5 移动端测试
- [ ] 在移动设备或浏览器移动模式下测试
  - 验证悬浮图标触摸拖拽
  - 验证聊天窗口尺寸适配
  - 验证交互流畅度

## 8. 配置与部署

### 8.1 环境变量配置
- [ ] 配置DeepSeek API密钥环境变量
  - 本地开发：在IDE运行配置中添加环境变量 `DEEPSEEK_API_KEY`
  - 生产环境：在服务器环境变量或配置中心添加 `DEEPSEEK_API_KEY`
  
- [ ] 创建环境配置文件（`src/main/resources/application-prod.yml`）
  - 配置生产环境的DeepSeek端点和超时参数
  - 配置Redis连接信息（用于频率限制）

### 8.2 生产环境优化
- [ ] 配置HTTPS（如果使用）
  - 确保DeepSeek API调用使用HTTPS
  
- [ ] 配置日志级别（`application-prod.yml`）
  - 生产环境降低日志级别（ERROR或WARN）
  - 保留关键业务日志
  
- [ ] 配置监控指标接入（可选）
  - 添加API调用次数监控
  - 添加响应时间监控
  - 添加成功率监控

## 9. 文档更新

### 9.1 API文档生成
- [ ] 确认Swagger配置正确（`src/main/java/com/pawfu/petservice/config/SwaggerConfig.java`）
  - 配置API分组
  - 配置访问路径 `/swagger-ui.html`
  
- [ ] 访问Swagger UI验证API文档
  - 验证接口描述正确
  - 验证参数说明完整
  - 验证响应模型清晰

### 9.2 项目文档更新
- [ ] 更新项目README文档（如果需要）
  - 添加AI智能客服功能说明
  - 添加环境变量配置说明
  
- [ ] 创建部署文档（`docs/ai-customer-service-deployment.md`）
  - 环境变量配置说明
  - 部署步骤
  - 常见问题排查

## 10. 最终验收

### 10.1 需求验收
- [ ] 验证所有需求规格文档中的验收条件
  - 悬浮图标交互功能（点击、拖拽、位置记忆）
  - 聊天窗口管理功能（打开、关闭、最小化）
  - 聊天交互功能（消息发送、接收、滚动）
  - DeepSeek API集成（正常调用、错误处理、超时）
  - 用户体验增强（打字动画、加载状态、错误提示）
  - 知识领域限定（宠物相关问题、非宠物问题拒绝）

### 10.2 性能验收
- [ ] 验证性能指标
  - API响应时间不超过5秒
  - 界面操作响应时间不超过200毫秒
  - 消息渲染时间不超过100毫秒
  
- [ ] 验证可靠性
  - 刷新页面后图标位置保持
  - 会话历史完整保存
  - 错误场景友好提示

### 10.3 安全验收
- [ ] 验证安全措施
  - 输入内容XSS过滤生效
  - API密钥未暴露给前端（检查前端代码）
  - 频率限制生效（连续发送20条以上消息触发限制）

### 10.4 兼容性验收
- [ ] 验证浏览器兼容性
  - Chrome、Firefox、Edge、Safari最新两个版本测试通过
  
- [ ] 验证移动端适配
  - 移动设备上功能正常、样式适配良好
