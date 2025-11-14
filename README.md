# 视频播放器 - Liquid Glass 设计语言

这是一个使用SwiftUI开发的iOS视频播放器应用，采用现代化的**Liquid Glass设计语言**，完整支持Apple的Picture in Picture（画中画）功能。

## 🎨 设计特色

### Liquid Glass 设计语言

本应用采用前沿的Liquid Glass（流动玻璃）设计风格，带来极致的视觉体验：

- **毛玻璃效果 (Frosted Glass)** - 使用 `.ultraThinMaterial` 创建半透明磨砂质感
- **动态渐变背景** - 流动的色彩渐变，增强视觉深度
- **玻璃态组件** - 所有UI元素采用透明叠加和模糊效果
- **光影效果** - 柔和的阴影和高光边框营造立体感
- **流畅动画** - 平滑的过渡效果和交互反馈
- **深色优化** - 专为深色模式设计的配色方案

### 视觉元素

- 渐变色彩：紫蓝色调营造科技感
- 圆角设计：柔和的边角增强亲和力
- 半透明层：多层次的视觉深度
- 发光效果：按钮和图标的光晕增强质感

## ✨ 核心功能

1. **视频播放**
   - 支持HTTP/HTTPS视频流
   - 支持本地和远程视频文件
   - 流畅的播放控制界面
   - 自动隐藏控制栏

2. **画中画(PIP)模式**
   - 一键切换PIP模式
   - 支持在使用其他应用时继续播放
   - 自动暂停和恢复
   - 完整的PIP生命周期管理
   - PIP状态视觉反馈

3. **智能控制**
   - 播放/暂停
   - 快进15秒
   - 快退15秒
   - 点击屏幕显示/隐藏控制栏
   - 3秒后自动隐藏控制栏

## 技术实现

### 关键组件

#### 1. VideoPlayerApp.swift
应用程序入口点，使用SwiftUI的`@main`属性标记。

#### 2. LiquidGlassComponents.swift ⭐ 新增
完整的Liquid Glass设计系统组件库：

- **LiquidGradientBackground** - 动态渐变背景（8秒循环动画）
- **GlassCard** - 毛玻璃卡片容器
- **GlassButton** - 玻璃态按钮（支持主要/次要样式）
- **GlassTextField** - 玻璃态文本输入框
- **GlassListItem** - 带按压反馈的列表项
- **FloatingGlassButton** - 浮动圆形玻璃按钮
- **GlassTitle/GlassSubtitle** - 渐变文字样式

所有组件都包含：
- `.ultraThinMaterial` 毛玻璃效果
- 渐变边框高光
- 多层阴影
- 半透明背景
- 流畅动画

#### 3. ContentView.swift
采用Liquid Glass风格的主视图界面：
- 动态渐变背景
- 玻璃态卡片布局
- 视频URL输入区
- 精选视频列表
- 使用指南卡片
- ScrollView支持长内容

#### 4. VideoPlayerView.swift
采用Liquid Glass风格的播放器：
- **VideoPlayerManager**: 管理播放器状态和PIP控制
- **AVPlayer集成**: 使用AVFoundation进行视频播放
- **PIP控制器**: AVPictureInPictureController的完整实现
- **智能控制栏**: 自动显示/隐藏（3秒定时器）
- **玻璃态控制面板**: 顶部工具栏和底部播放控制
- **流畅过渡动画**: 控制栏的滑入/滑出效果

### PIP实现细节

```swift
// 1. 设置音频会话以支持后台播放
try AVAudioSession.sharedInstance().setCategory(.playback, mode: .moviePlayback)

// 2. 创建PIP控制器
pipController = AVPictureInPictureController(playerLayer: playerLayer)
pipController?.delegate = self

// 3. 启动PIP
pipController.startPictureInPicture()
```

### 必要的配置

在 `Info.plist` 中需要添加：

```xml
<!-- 支持后台音频播放 -->
<key>UIBackgroundModes</key>
<array>
    <string>audio</string>
</array>

<!-- 允许HTTP请求（仅用于测试） -->
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

## 使用说明

### 基本使用

1. **输入视频URL**
   - 在主界面输入框中输入视频URL
   - 点击"播放视频"按钮

2. **选择示例视频**
   - 点击列表中的任一示例视频
   - 自动开始播放

3. **启用PIP模式**
   - 点击右上角的PIP图标
   - 视频将缩小到浮动窗口
   - 可以切换到其他应用继续观看

### 播放控制

- **播放/暂停**: 点击中间的播放按钮
- **快退15秒**: 点击左侧的后退按钮
- **快进15秒**: 点击右侧的前进按钮
- **关闭播放器**: 点击左上角的X按钮

## 系统要求

- iOS 14.0 或更高版本
- Xcode 12.0 或更高版本
- Swift 5.3 或更高版本

## PIP支持要求

### 设备要求
- iPhone: iPhone 7 或更新机型
- iPad: iPad Air 2 或更新机型

### 系统要求
- iOS 14.0+（PIP在iPhone上的支持）
- iOS 9.0+（PIP在iPad上的支持）

## 构建和运行

### 使用Xcode

1. 打开Xcode
2. 选择 File > New > Project
3. 选择 iOS > App
4. 将本仓库中的Swift文件添加到项目
5. 替换Info.plist内容
6. 选择目标设备或模拟器
7. 点击Run按钮 (⌘R)

### 配置要点

确保在Xcode项目设置中：
1. **Signing & Capabilities**
   - 添加 "Background Modes" capability
   - 勾选 "Audio, AirPlay, and Picture in Picture"

2. **General**
   - 设置正确的Bundle Identifier
   - 选择有效的开发团队

## 代码结构

```
player/
├── VideoPlayerApp.swift           # 应用入口
├── LiquidGlassComponents.swift    # ⭐ Liquid Glass设计组件库
├── ContentView.swift              # 主视图（Liquid Glass风格）
├── VideoPlayerView.swift          # 播放器视图（Liquid Glass风格）+ PIP实现
├── Info.plist                     # 应用配置
└── README.md                      # 说明文档
```

### 设计系统架构

```
LiquidGlassComponents.swift
├── 颜色系统
│   ├── liquidPrimary (蓝色)
│   ├── liquidSecondary (紫色)
│   ├── liquidAccent (青色)
│   └── 半透明变体
├── 背景组件
│   └── LiquidGradientBackground (动态渐变)
├── 容器组件
│   └── GlassCard (毛玻璃卡片)
├── 交互组件
│   ├── GlassButton (玻璃按钮)
│   ├── GlassTextField (玻璃输入框)
│   ├── GlassListItem (玻璃列表项)
│   └── FloatingGlassButton (浮动按钮)
└── 文字组件
    ├── GlassTitle (大标题)
    └── GlassSubtitle (副标题)
```

## 常见问题

### PIP无法启动？

1. 确认设备支持PIP功能
2. 检查Info.plist中是否添加了UIBackgroundModes
3. 确认音频会话配置正确
4. 在真机上测试（部分模拟器可能不支持）

### 视频无法播放？

1. 确认视频URL有效
2. 检查网络连接
3. 确认视频格式受支持（推荐MP4）
4. 检查NSAppTransportSecurity设置（HTTPS vs HTTP）

## 示例视频来源

应用内置的示例视频来自Google的测试视频库，包括：
- Big Buck Bunny
- Elephants Dream
- For Bigger Blazes

## 进阶功能建议

可以考虑添加以下功能：
1. 播放列表管理
2. 视频下载功能
3. 播放速度控制
4. 字幕支持
5. 投屏功能（AirPlay）
6. 播放历史记录
7. 手势控制（音量、亮度、进度）

## 许可证

MIT License

## 作者

由 Claude Code 创建

## 参考资料

- [AVFoundation Documentation](https://developer.apple.com/documentation/avfoundation)
- [AVPictureInPictureController](https://developer.apple.com/documentation/avkit/avpictureinpicturecontroller)
- [Enabling Background Audio](https://developer.apple.com/documentation/avfoundation/media_playback/configuring_your_app_for_media_playback)
