//
//  LiquidGlassComponents.swift
//  VideoPlayer
//
//  Liquid Glass设计语言组件库
//

import SwiftUI

// MARK: - 颜色定义
extension Color {
    // Liquid Glass 渐变色
    static let liquidPrimary = Color(red: 0.4, green: 0.6, blue: 1.0)
    static let liquidSecondary = Color(red: 0.6, green: 0.4, blue: 1.0)
    static let liquidAccent = Color(red: 0.3, green: 0.8, blue: 0.9)

    // 半透明颜色
    static let glassWhite = Color.white.opacity(0.15)
    static let glassDark = Color.black.opacity(0.3)
}

// MARK: - 渐变背景
struct LiquidGradientBackground: View {
    @State private var animateGradient = false

    var body: some View {
        LinearGradient(
            colors: [
                Color(red: 0.1, green: 0.1, blue: 0.3),
                Color(red: 0.2, green: 0.1, blue: 0.4),
                Color(red: 0.1, green: 0.2, blue: 0.5),
                Color(red: 0.15, green: 0.15, blue: 0.35)
            ],
            startPoint: animateGradient ? .topLeading : .bottomLeading,
            endPoint: animateGradient ? .bottomTrailing : .topTrailing
        )
        .ignoresSafeArea()
        .onAppear {
            withAnimation(.easeInOut(duration: 8).repeatForever(autoreverses: true)) {
                animateGradient.toggle()
            }
        }
    }
}

// MARK: - 毛玻璃卡片
struct GlassCard<Content: View>: View {
    let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        content
            .background(
                RoundedRectangle(cornerRadius: 20)
                    .fill(Color.white.opacity(0.1))
                    .background(
                        RoundedRectangle(cornerRadius: 20)
                            .fill(.ultraThinMaterial)
                    )
                    .shadow(color: Color.black.opacity(0.2), radius: 20, x: 0, y: 10)
                    .shadow(color: Color.white.opacity(0.1), radius: 1, x: 0, y: 1)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 20)
                    .stroke(
                        LinearGradient(
                            colors: [
                                Color.white.opacity(0.3),
                                Color.white.opacity(0.1),
                                Color.clear
                            ],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        ),
                        lineWidth: 1.5
                    )
            )
    }
}

// MARK: - 玻璃按钮
struct GlassButton: View {
    let title: String
    let icon: String?
    let action: () -> Void
    var isEnabled: Bool = true
    var isPrimary: Bool = false

    var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                if let icon = icon {
                    Image(systemName: icon)
                        .font(.system(size: 16, weight: .semibold))
                }
                Text(title)
                    .font(.system(size: 16, weight: .semibold))
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
            .padding(.horizontal, 20)
            .background(
                ZStack {
                    if isPrimary {
                        LinearGradient(
                            colors: [Color.liquidPrimary, Color.liquidSecondary],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                        .opacity(isEnabled ? 1 : 0.5)
                    } else {
                        Color.white.opacity(isEnabled ? 0.15 : 0.05)
                    }

                    if isEnabled {
                        RoundedRectangle(cornerRadius: 16)
                            .fill(.ultraThinMaterial)
                    }
                }
            )
            .foregroundColor(isEnabled ? .white : .gray)
            .cornerRadius(16)
            .shadow(color: isPrimary ? Color.liquidPrimary.opacity(0.5) : Color.black.opacity(0.2),
                    radius: isPrimary ? 15 : 10,
                    x: 0,
                    y: isPrimary ? 8 : 5)
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(
                        LinearGradient(
                            colors: [
                                Color.white.opacity(0.4),
                                Color.white.opacity(0.1)
                            ],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        ),
                        lineWidth: 1
                    )
            )
        }
        .disabled(!isEnabled)
    }
}

// MARK: - 玻璃文本输入框
struct GlassTextField: View {
    let placeholder: String
    @Binding var text: String

    var body: some View {
        TextField(placeholder, text: $text)
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color.white.opacity(0.1))
                    .background(
                        RoundedRectangle(cornerRadius: 12)
                            .fill(.ultraThinMaterial)
                    )
            )
            .overlay(
                RoundedRectangle(cornerRadius: 12)
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
            .foregroundColor(.white)
            .autocapitalization(.none)
            .disableAutocorrection(true)
    }
}

// MARK: - 玻璃列表项
struct GlassListItem: View {
    let icon: String
    let title: String
    let action: () -> Void
    @State private var isPressed = false

    var body: some View {
        Button(action: {
            withAnimation(.spring(response: 0.3, dampingFraction: 0.6)) {
                isPressed = true
            }
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                withAnimation(.spring(response: 0.3, dampingFraction: 0.6)) {
                    isPressed = false
                }
                action()
            }
        }) {
            HStack(spacing: 15) {
                Image(systemName: icon)
                    .font(.system(size: 24))
                    .foregroundColor(.liquidAccent)
                    .frame(width: 40, height: 40)
                    .background(
                        Circle()
                            .fill(Color.liquidAccent.opacity(0.15))
                    )

                Text(title)
                    .font(.system(size: 17, weight: .medium))
                    .foregroundColor(.white)

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(.white.opacity(0.5))
            }
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .fill(Color.white.opacity(isPressed ? 0.15 : 0.08))
                    .background(
                        RoundedRectangle(cornerRadius: 16)
                            .fill(.ultraThinMaterial)
                    )
            )
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(
                        LinearGradient(
                            colors: [
                                Color.white.opacity(0.25),
                                Color.white.opacity(0.05)
                            ],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        ),
                        lineWidth: 1
                    )
            )
            .scaleEffect(isPressed ? 0.98 : 1.0)
            .shadow(color: Color.black.opacity(0.15), radius: 10, x: 0, y: 5)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

// MARK: - 浮动控制按钮
struct FloatingGlassButton: View {
    let icon: String
    let isActive: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: icon)
                .font(.system(size: 20, weight: .semibold))
                .foregroundColor(.white)
                .frame(width: 50, height: 50)
                .background(
                    ZStack {
                        if isActive {
                            LinearGradient(
                                colors: [Color.liquidPrimary, Color.liquidSecondary],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        } else {
                            Color.white.opacity(0.15)
                        }

                        Circle()
                            .fill(.ultraThinMaterial)
                    }
                )
                .clipShape(Circle())
                .shadow(color: isActive ? Color.liquidPrimary.opacity(0.5) : Color.black.opacity(0.3),
                        radius: isActive ? 15 : 10,
                        x: 0,
                        y: isActive ? 8 : 5)
                .overlay(
                    Circle()
                        .stroke(
                            LinearGradient(
                                colors: [
                                    Color.white.opacity(0.4),
                                    Color.white.opacity(0.1)
                                ],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            ),
                            lineWidth: 1.5
                        )
                )
        }
    }
}

// MARK: - 标题样式
struct GlassTitle: View {
    let text: String

    var body: some View {
        Text(text)
            .font(.system(size: 36, weight: .bold, design: .rounded))
            .foregroundStyle(
                LinearGradient(
                    colors: [.white, Color.white.opacity(0.8)],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
            )
            .shadow(color: Color.black.opacity(0.3), radius: 10, x: 0, y: 5)
    }
}

// MARK: - 副标题样式
struct GlassSubtitle: View {
    let text: String

    var body: some View {
        Text(text)
            .font(.system(size: 16, weight: .medium))
            .foregroundColor(.white.opacity(0.7))
    }
}
