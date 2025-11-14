//
//  VideoPlayerView.swift
//  VideoPlayer
//
//  支持PIP（画中画）的视频播放器视图
//

import SwiftUI
import AVKit
import AVFoundation

struct VideoPlayerView: View {
    let videoURL: URL
    @Environment(\.dismiss) var dismiss
    @StateObject private var playerManager = VideoPlayerManager()
    @State private var showControls = true
    @State private var controlsTimer: Timer?

    var body: some View {
        ZStack {
            // 黑色背景
            Color.black.edgesIgnoringSafeArea(.all)

            // 视频播放器
            VideoPlayerContainerView(
                videoURL: videoURL,
                playerManager: playerManager
            )
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .onTapGesture {
                withAnimation(.easeInOut(duration: 0.3)) {
                    showControls.toggle()
                }
                resetControlsTimer()
            }

            // 控制层
            if showControls {
                VStack {
                    // 顶部工具栏 - Liquid Glass风格
                    HStack {
                        FloatingGlassButton(
                            icon: "xmark",
                            isActive: false
                        ) {
                            dismiss()
                        }

                        Spacer()

                        // 标题卡片
                        HStack(spacing: 8) {
                            Image(systemName: "play.circle.fill")
                                .font(.system(size: 16))
                            Text("正在播放")
                                .font(.system(size: 16, weight: .semibold))
                        }
                        .foregroundColor(.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(
                            Capsule()
                                .fill(Color.white.opacity(0.15))
                                .background(
                                    Capsule()
                                        .fill(.ultraThinMaterial)
                                )
                        )
                        .overlay(
                            Capsule()
                                .stroke(
                                    LinearGradient(
                                        colors: [
                                            Color.white.opacity(0.3),
                                            Color.white.opacity(0.1)
                                        ],
                                        startPoint: .topLeading,
                                        endPoint: .bottomTrailing
                                    ),
                                    lineWidth: 1
                                )
                        )
                        .shadow(color: Color.black.opacity(0.3), radius: 15, x: 0, y: 5)

                        Spacer()

                        FloatingGlassButton(
                            icon: playerManager.isPiPActive ? "pip.fill" : "pip",
                            isActive: playerManager.isPiPActive
                        ) {
                            playerManager.togglePictureInPicture()
                        }
                    }
                    .padding()
                    .transition(.move(edge: .top).combined(with: .opacity))

                    Spacer()

                    // 底部控制栏 - Liquid Glass风格
                    VStack(spacing: 20) {
                        // 播放控制按钮
                        HStack(spacing: 40) {
                            // 快退按钮
                            ControlButton(
                                icon: "gobackward.15",
                                size: 50,
                                action: {
                                    playerManager.seekBackward()
                                    resetControlsTimer()
                                }
                            )

                            // 播放/暂停按钮
                            Button(action: {
                                playerManager.togglePlayPause()
                                resetControlsTimer()
                            }) {
                                Image(systemName: playerManager.isPlaying ? "pause.circle.fill" : "play.circle.fill")
                                    .font(.system(size: 70))
                                    .foregroundStyle(
                                        LinearGradient(
                                            colors: [.white, Color.white.opacity(0.8)],
                                            startPoint: .topLeading,
                                            endPoint: .bottomTrailing
                                        )
                                    )
                                    .shadow(color: Color.liquidPrimary.opacity(0.5), radius: 20, x: 0, y: 10)
                                    .shadow(color: Color.black.opacity(0.5), radius: 10, x: 0, y: 5)
                            }

                            // 快进按钮
                            ControlButton(
                                icon: "goforward.15",
                                size: 50,
                                action: {
                                    playerManager.seekForward()
                                    resetControlsTimer()
                                }
                            )
                        }
                        .padding(.vertical, 20)
                        .padding(.horizontal, 40)
                        .background(
                            RoundedRectangle(cornerRadius: 30)
                                .fill(Color.white.opacity(0.1))
                                .background(
                                    RoundedRectangle(cornerRadius: 30)
                                        .fill(.ultraThinMaterial)
                                )
                        )
                        .overlay(
                            RoundedRectangle(cornerRadius: 30)
                                .stroke(
                                    LinearGradient(
                                        colors: [
                                            Color.white.opacity(0.3),
                                            Color.white.opacity(0.1)
                                        ],
                                        startPoint: .topLeading,
                                        endPoint: .bottomTrailing
                                    ),
                                    lineWidth: 1.5
                                )
                        )
                        .shadow(color: Color.black.opacity(0.4), radius: 25, x: 0, y: 15)
                    }
                    .padding(.bottom, 30)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                }
            }
        }
        .preferredColorScheme(.dark)
        .onAppear {
            playerManager.setupPlayer(with: videoURL)
            resetControlsTimer()
        }
        .onDisappear {
            playerManager.cleanup()
            controlsTimer?.invalidate()
        }
    }

    private func resetControlsTimer() {
        controlsTimer?.invalidate()
        controlsTimer = Timer.scheduledTimer(withTimeInterval: 3.0, repeats: false) { _ in
            withAnimation(.easeInOut(duration: 0.3)) {
                showControls = false
            }
        }
    }
}

// MARK: - 控制按钮组件
struct ControlButton: View {
    let icon: String
    let size: CGFloat
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: icon)
                .font(.system(size: size * 0.5, weight: .semibold))
                .foregroundColor(.white)
                .frame(width: size, height: size)
                .background(
                    Circle()
                        .fill(Color.white.opacity(0.15))
                        .background(
                            Circle()
                                .fill(.ultraThinMaterial)
                        )
                )
                .overlay(
                    Circle()
                        .stroke(
                            LinearGradient(
                                colors: [
                                    Color.white.opacity(0.3),
                                    Color.white.opacity(0.1)
                                ],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            ),
                            lineWidth: 1
                        )
                )
                .shadow(color: Color.black.opacity(0.3), radius: 10, x: 0, y: 5)
        }
    }
}

// AVPlayerViewController 的 SwiftUI 包装器
struct VideoPlayerContainerView: UIViewControllerRepresentable {
    let videoURL: URL
    let playerManager: VideoPlayerManager

    func makeUIViewController(context: Context) -> AVPlayerViewController {
        let controller = AVPlayerViewController()
        controller.player = playerManager.player
        controller.allowsPictureInPicturePlayback = true
        controller.canStartPictureInPictureAutomaticallyFromInline = true

        // 保存引用以便管理PIP
        playerManager.playerViewController = controller

        return controller
    }

    func updateUIViewController(_ uiViewController: AVPlayerViewController, context: Context) {
        // 更新时不需要做什么
    }
}

// 视频播放器管理类
class VideoPlayerManager: NSObject, ObservableObject {
    @Published var isPlaying = false
    @Published var isPiPActive = false

    var player: AVPlayer?
    weak var playerViewController: AVPlayerViewController?
    private var pipController: AVPictureInPictureController?
    private var timeObserver: Any?

    override init() {
        super.init()
        setupAudioSession()
    }

    // 设置音频会话以支持后台播放
    private func setupAudioSession() {
        do {
            try AVAudioSession.sharedInstance().setCategory(.playback, mode: .moviePlayback)
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("设置音频会话失败: \(error)")
        }
    }

    // 设置播放器
    func setupPlayer(with url: URL) {
        let playerItem = AVPlayerItem(url: url)
        player = AVPlayer(playerItem: playerItem)

        // 自动开始播放
        player?.play()
        isPlaying = true

        // 监听播放状态
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(playerDidFinishPlaying),
            name: .AVPlayerItemDidPlayToEndTime,
            object: playerItem
        )
    }

    // 设置PIP控制器
    func setupPiPController() {
        guard let playerLayer = playerViewController?.view.layer.sublayers?.first(where: { $0 is AVPlayerLayer }) as? AVPlayerLayer else {
            return
        }

        if AVPictureInPictureController.isPictureInPictureSupported() {
            pipController = AVPictureInPictureController(playerLayer: playerLayer)
            pipController?.delegate = self
        }
    }

    // 切换播放/暂停
    func togglePlayPause() {
        if isPlaying {
            player?.pause()
        } else {
            player?.play()
        }
        isPlaying.toggle()
    }

    // 快退15秒
    func seekBackward() {
        guard let currentTime = player?.currentTime() else { return }
        let newTime = CMTimeSubtract(currentTime, CMTime(seconds: 15, preferredTimescale: 1))
        player?.seek(to: newTime)
    }

    // 快进15秒
    func seekForward() {
        guard let currentTime = player?.currentTime() else { return }
        let newTime = CMTimeAdd(currentTime, CMTime(seconds: 15, preferredTimescale: 1))
        player?.seek(to: newTime)
    }

    // 切换PIP模式
    func togglePictureInPicture() {
        // 如果还没有设置PIP控制器，先设置
        if pipController == nil {
            setupPiPController()
        }

        guard let pipController = pipController else {
            print("PIP不可用")
            return
        }

        if pipController.isPictureInPictureActive {
            pipController.stopPictureInPicture()
        } else {
            pipController.startPictureInPicture()
        }
    }

    // 播放完成
    @objc private func playerDidFinishPlaying() {
        isPlaying = false
        player?.seek(to: .zero)
    }

    // 清理资源
    func cleanup() {
        player?.pause()
        player = nil
        pipController?.stopPictureInPicture()
        pipController = nil
        NotificationCenter.default.removeObserver(self)

        if let observer = timeObserver {
            player?.removeTimeObserver(observer)
        }
    }
}

// PIP代理方法
extension VideoPlayerManager: AVPictureInPictureControllerDelegate {
    func pictureInPictureControllerWillStartPictureInPicture(_ pictureInPictureController: AVPictureInPictureController) {
        isPiPActive = true
    }

    func pictureInPictureControllerDidStopPictureInPicture(_ pictureInPictureController: AVPictureInPictureController) {
        isPiPActive = false
    }

    func pictureInPictureControllerFailedToStartPictureInPicture(_ pictureInPictureController: AVPictureInPictureController, withError error: Error) {
        print("PIP启动失败: \(error)")
        isPiPActive = false
    }
}
