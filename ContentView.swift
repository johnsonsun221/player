//
//  ContentView.swift
//  VideoPlayer
//
//  主视图界面
//

import SwiftUI

struct ContentView: View {
    @State private var videoURL: String = ""
    @State private var showPlayer = false

    // 示例视频URL
    let sampleVideos = [
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    ]

    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Text("视频播放器")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .padding(.top)

                Text("支持画中画(PIP)功能")
                    .font(.subheadline)
                    .foregroundColor(.gray)

                Divider()
                    .padding()

                // 输入URL部分
                VStack(alignment: .leading, spacing: 10) {
                    Text("输入视频URL:")
                        .font(.headline)

                    TextField("请输入视频URL", text: $videoURL)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .autocapitalization(.none)
                        .disableAutocorrection(true)

                    Button(action: {
                        if !videoURL.isEmpty {
                            showPlayer = true
                        }
                    }) {
                        Text("播放视频")
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(videoURL.isEmpty ? Color.gray : Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(10)
                    }
                    .disabled(videoURL.isEmpty)
                }
                .padding()

                Divider()

                // 示例视频列表
                VStack(alignment: .leading, spacing: 10) {
                    Text("或选择示例视频:")
                        .font(.headline)

                    ForEach(sampleVideos, id: \.self) { url in
                        Button(action: {
                            videoURL = url
                            showPlayer = true
                        }) {
                            HStack {
                                Image(systemName: "play.circle.fill")
                                    .foregroundColor(.blue)
                                Text(videoTitle(from: url))
                                    .foregroundColor(.primary)
                                Spacer()
                                Image(systemName: "chevron.right")
                                    .foregroundColor(.gray)
                            }
                            .padding()
                            .background(Color(.systemGray6))
                            .cornerRadius(10)
                        }
                    }
                }
                .padding()

                Spacer()

                // 说明文字
                VStack(spacing: 5) {
                    Text("使用说明:")
                        .font(.headline)
                    Text("• 点击播放按钮开始播放")
                        .font(.caption)
                    Text("• 点击PIP按钮启用画中画模式")
                        .font(.caption)
                    Text("• 画中画模式下可以切换到其他应用")
                        .font(.caption)
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(10)
                .padding()
            }
            .navigationBarHidden(true)
            .sheet(isPresented: $showPlayer) {
                if let url = URL(string: videoURL) {
                    VideoPlayerView(videoURL: url)
                }
            }
        }
    }

    private func videoTitle(from url: String) -> String {
        if url.contains("BigBuckBunny") {
            return "Big Buck Bunny"
        } else if url.contains("ElephantsDream") {
            return "Elephants Dream"
        } else if url.contains("ForBiggerBlazes") {
            return "For Bigger Blazes"
        }
        return "示例视频"
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
