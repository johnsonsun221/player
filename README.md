# 视频播放器 - 支持PIP画中画功能

这是一个使用SwiftUI开发的iOS视频播放器应用，完整支持Apple的Picture in Picture（画中画）功能。

## 主要功能

### ✨ 核心特性

1. **视频播放**
   - 支持HTTP/HTTPS视频流
   - 支持本地和远程视频文件
   - 流畅的播放控制界面

2. **画中画(PIP)模式**
   - 一键切换PIP模式
   - 支持在使用其他应用时继续播放
   - 自动暂停和恢复
   - 完整的PIP生命周期管理

3. **播放控制**
   - 播放/暂停
   - 快进15秒
   - 快退15秒
   - 进度跟踪

## 技术实现

### 关键组件

#### 1. VideoPlayerApp.swift
应用程序入口点，使用SwiftUI的`@main`属性标记。

#### 2. ContentView.swift
主视图界面，包含：
- 视频URL输入框
- 示例视频列表
- 播放控制入口

#### 3. VideoPlayerView.swift
核心播放器视图，包含：
- **VideoPlayerManager**: 管理播放器状态和PIP控制
- **AVPlayer集成**: 使用AVFoundation进行视频播放
- **PIP控制器**: AVPictureInPictureController的完整实现

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
├── VideoPlayerApp.swift        # 应用入口
├── ContentView.swift           # 主视图
├── VideoPlayerView.swift       # 播放器视图和PIP实现
├── Info.plist                  # 应用配置
└── README.md                   # 说明文档
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
