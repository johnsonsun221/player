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

    var body: some View {
        ZStack {
            Color.black.edgesIgnoringSafeArea(.all)

            VStack {
                // 顶部工具栏
                HStack {
                    Button(action: {
                        dismiss()
                    }) {
                        Image(systemName: "xmark")
                            .foregroundColor(.white)
                            .padding()
                            .background(Color.black.opacity(0.5))
                            .clipShape(Circle())
                    }

                    Spacer()

                    Text("视频播放器")
                        .foregroundColor(.white)
                        .font(.headline)

                    Spacer()

                    Button(action: {
                        playerManager.togglePictureInPicture()
                    }) {
                        Image(systemName: playerManager.isPiPActive ? "pip.fill" : "pip")
                            .foregroundColor(.white)
                            .padding()
                            .background(Color.black.opacity(0.5))
                            .clipShape(Circle())
                    }
                }
                .padding()

                // 视频播放器
                VideoPlayerContainerView(
                    videoURL: videoURL,
                    playerManager: playerManager
                )
                .frame(maxWidth: .infinity, maxHeight: .infinity)

                // 底部控制栏
                HStack(spacing: 20) {
                    Button(action: {
                        playerManager.seekBackward()
                    }) {
                        Image(systemName: "gobackward.15")
                            .font(.title2)
                            .foregroundColor(.white)
                    }

                    Button(action: {
                        playerManager.togglePlayPause()
                    }) {
                        Image(systemName: playerManager.isPlaying ? "pause.circle.fill" : "play.circle.fill")
                            .font(.largeTitle)
                            .foregroundColor(.white)
                    }

                    Button(action: {
                        playerManager.seekForward()
                    }) {
                        Image(systemName: "goforward.15")
                            .font(.title2)
                            .foregroundColor(.white)
                    }
                }
                .padding()
            }
        }
        .onAppear {
            playerManager.setupPlayer(with: videoURL)
        }
        .onDisappear {
            playerManager.cleanup()
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
