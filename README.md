# 决策小助理 - AI Decision Helper

一个轻量级 AI 决策助手 Android 应用，帮助用户在生活中做出简单但频繁的选择。

## 功能特点

### 核心功能

1. **问题输入与条件设定**
   - 输入你正在犹豫的问题
   - 可选设定：预算、地点、额外限制

2. **AI 产生候选选项**
   - AI 根据问题和条件生成 3-5 个具体可执行的选项
   - 支持删除不想要的选项
   - 支持添加自定义选项

3. **智能决策模式**
   - 「帮我选一个」- 随机抽选
   - 「使用我的选择」- 手动确认
   - 未来支持 AI 加权抽签

4. **AI 解释选择理由**
   - AI 用 2-3 句话解释为什么这个选择适合你
   - 友善、自然的语气

5. **历史记录**
   - 本地存储最近的决策记录
   - 可查看历史详情
   - 支持删除和清空

## 技术栈

### 前端 (Android)
- **语言**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **架构**: MVVM + Clean Architecture
- **导航**: Navigation Compose
- **本地存储**: Room Database

### 后端服务
- **AI 服务**: OpenAI GPT-3.5-turbo
- **网络请求**: Retrofit + OkHttp
- **JSON 解析**: Gson

### 设计语言
- Liquid Glass 风格
- 动态渐变背景
- 毛玻璃效果
- 流畅动画

## 项目结构

```
app/src/main/java/com/decisionhelper/ai/
├── DecisionHelperApp.kt          # Application 类
├── MainActivity.kt               # 主 Activity
├── data/
│   ├── model/
│   │   ├── Decision.kt           # 决策数据模型
│   │   └── AIResponse.kt         # AI 响应模型
│   ├── local/
│   │   ├── DecisionDao.kt        # Room DAO
│   │   └── DecisionDatabase.kt   # Room Database
│   └── repository/
│       └── DecisionRepository.kt # 数据仓库
├── service/
│   ├── OpenAIService.kt          # OpenAI API 接口
│   └── AIDecisionService.kt      # AI 决策服务
├── ui/
│   ├── theme/
│   │   ├── Color.kt              # 颜色定义
│   │   ├── Theme.kt              # 主题配置
│   │   └── Type.kt               # 字体样式
│   ├── components/
│   │   └── GlassComponents.kt    # 玻璃效果组件库
│   ├── navigation/
│   │   └── NavHost.kt            # 导航配置
│   └── screens/
│       ├── home/                 # 首页（问题输入）
│       ├── options/              # 选项列表页
│       ├── result/               # 结果展示页
│       └── history/              # 历史记录页
```

## 设置与运行

### 1. 克隆项目
```bash
git clone <repository-url>
cd player
```

### 2. 配置 OpenAI API Key

在 `gradle.properties` 文件中添加：
```properties
OPENAI_API_KEY=your_openai_api_key_here
```

或者在 Android Studio 中：
1. 打开 `local.properties` (如果没有则创建)
2. 添加：`OPENAI_API_KEY=your_api_key`

### 3. 使用 Android Studio 打开项目
1. 打开 Android Studio
2. 选择 "Open an existing project"
3. 选择项目根目录
4. 等待 Gradle 同步完成

### 4. 运行应用
1. 连接 Android 设备或启动模拟器
2. 点击 Run 按钮或按 Shift+F10

## API 设计

### Prompt 设计

#### 生成选项
```
使用者目前有一个犹豫的问题，请根据使用者提供的描述与限制，产生 3-5 个具体且可执行的选项。
请以 JSON 格式回传：
{ "options": [ { "title": "...", "description": "..." }, ... ] }
```

#### 解释选择
```
使用者已经做出了选择，请用轻松友善的语气，说明为什么这个选择是合理或不错的决定。
回覆 2-3 句话即可。
```

## 界面预览

### 首页
- 动态渐变背景
- 问题输入框
- 条件设定（预算、地点、限制）
- 使用指南

### 选项列表
- 显示 AI 生成的选项
- 支持选择、删除、添加
- 「帮我选一个」按钮

### 结果页
- 庆祝动画
- 选中的选项
- AI 解释理由
- 原始问题回顾

### 历史记录
- 时间排序的决策列表
- 快速查看详情
- 删除和清空功能

## 目标族群

1. **一般上班族 / 学生** - 常常犹豫「吃什么」、「周末做什么」
2. **选择困难症患者** - 做决定压力大，想把决策外包
3. **科技爱好者** - 想试试 AI 帮忙做日常小决策

## 用户价值

- **节省思考时间** - 不用自己列选项、逐一分析
- **减少选择焦虑** - 交给 App 帮你「分析＋抽签」
- **有陪伴感** - AI 用自然语言说明选择理由，像朋友给建议

## 未来规划

- [ ] AI 加权抽签功能
- [ ] 更多 AI 模型支持
- [ ] 分类标签功能
- [ ] 云端同步
- [ ] 小组件支持
- [ ] 深色/浅色主题切换

## 系统要求

- Android 8.0 (API 26) 或更高版本
- 网络连接（用于 AI 功能）

## 许可证

MIT License

## 作者

由 Claude Code 创建
