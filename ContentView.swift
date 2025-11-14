//
//  ContentView.swift
//  VideoPlayer
//
//  主视图界面 - Liquid Glass设计语言
//

import SwiftUI

struct ContentView: View {
    @State private var videoURL: String = ""
    @State private var showPlayer = false

    // 示例视频URL
    let sampleVideos = [
        ("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "Big Buck Bunny", "play.circle.fill"),
        ("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", "Elephants Dream", "film.fill"),
        ("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4", "For Bigger Blazes", "flame.fill")
    ]

    var body: some View {
        ZStack {
            // Liquid Glass 动态渐变背景
            LiquidGradientBackground()

            ScrollView {
                VStack(spacing: 30) {
                    // 标题区域
                    VStack(spacing: 12) {
                        GlassTitle(text: "视频播放器")

                        GlassSubtitle(text: "支持 Picture in Picture 画中画功能")

                        // 装饰性图标
                        HStack(spacing: 15) {
                            ForEach(["play.circle", "pip", "airplayvideo"], id: \.self) { icon in
                                Image(systemName: icon)
                                    .font(.system(size: 20))
                                    .foregroundColor(.white.opacity(0.6))
                            }
                        }
                        .padding(.top, 5)
                    }
                    .padding(.top, 40)

                    // URL输入卡片
                    GlassCard {
                        VStack(alignment: .leading, spacing: 15) {
                            HStack {
                                Image(systemName: "link.circle.fill")
                                    .font(.system(size: 20))
                                    .foregroundColor(.liquidAccent)
                                Text("输入视频链接")
                                    .font(.system(size: 18, weight: .semibold))
                                    .foregroundColor(.white)
                            }

                            GlassTextField(placeholder: "https://example.com/video.mp4", text: $videoURL)

                            GlassButton(
                                title: "开始播放",
                                icon: "play.fill",
                                action: {
                                    if !videoURL.isEmpty {
                                        showPlayer = true
                                    }
                                },
                                isEnabled: !videoURL.isEmpty,
                                isPrimary: true
                            )
                        }
                        .padding(20)
                    }
                    .padding(.horizontal, 20)

                    // 示例视频列表
                    GlassCard {
                        VStack(alignment: .leading, spacing: 15) {
                            HStack {
                                Image(systemName: "play.rectangle.fill")
                                    .font(.system(size: 20))
                                    .foregroundColor(.liquidAccent)
                                Text("精选示例视频")
                                    .font(.system(size: 18, weight: .semibold))
                                    .foregroundColor(.white)
                            }
                            .padding(.bottom, 5)

                            ForEach(sampleVideos, id: \.0) { video in
                                GlassListItem(
                                    icon: video.2,
                                    title: video.1
                                ) {
                                    videoURL = video.0
                                    showPlayer = true
                                }
                            }
                        }
                        .padding(20)
                    }
                    .padding(.horizontal, 20)

                    // 功能说明卡片
                    GlassCard {
                        VStack(alignment: .leading, spacing: 12) {
                            HStack {
                                Image(systemName: "info.circle.fill")
                                    .font(.system(size: 20))
                                    .foregroundColor(.liquidAccent)
                                Text("使用指南")
                                    .font(.system(size: 18, weight: .semibold))
                                    .foregroundColor(.white)
                            }
                            .padding(.bottom, 5)

                            FeatureRow(icon: "hand.tap.fill", text: "轻触播放按钮开始观看")
                            FeatureRow(icon: "pip.enter", text: "点击 PIP 图标启用画中画")
                            FeatureRow(icon: "rectangle.2.swap", text: "画中画模式下可切换应用")
                            FeatureRow(icon: "hand.draw.fill", text: "支持手势控制和快捷操作")
                        }
                        .padding(20)
                    }
                    .padding(.horizontal, 20)
                    .padding(.bottom, 30)
                }
            }
        }
        .preferredColorScheme(.dark)
        .sheet(isPresented: $showPlayer) {
            if let url = URL(string: videoURL) {
                VideoPlayerView(videoURL: url)
            }
        }
    }
}

// MARK: - 功能行组件
struct FeatureRow: View {
    let icon: String
    let text: String

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 16))
                .foregroundColor(.liquidAccent)
                .frame(width: 24)

            Text(text)
                .font(.system(size: 15))
                .foregroundColor(.white.opacity(0.8))

            Spacer()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
