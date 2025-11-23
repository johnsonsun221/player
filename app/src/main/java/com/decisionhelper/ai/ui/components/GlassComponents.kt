package com.decisionhelper.ai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.decisionhelper.ai.ui.theme.*

/**
 * 渐变背景
 */
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        GradientStart,
                        GradientMiddle,
                        GradientEnd,
                        GradientMiddle
                    ),
                    start = Offset(0f, offset * 1000),
                    end = Offset(1000f, (1 - offset) * 1000)
                )
            ),
        content = content
    )
}

/**
 * 玻璃卡片
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = modifier
        .fillMaxWidth()
        .shadow(
            elevation = 20.dp,
            shape = RoundedCornerShape(20.dp),
            ambientColor = Color.Black.copy(alpha = 0.3f),
            spotColor = Color.Black.copy(alpha = 0.3f)
        )
        .clip(RoundedCornerShape(20.dp))
        .background(GlassWhite)
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.3f),
                    Color.White.copy(alpha = 0.1f),
                    Color.Transparent
                )
            ),
            shape = RoundedCornerShape(20.dp)
        )
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
            } else Modifier
        )
        .padding(20.dp)

    Column(
        modifier = cardModifier,
        content = content
    )
}

/**
 * 玻璃按钮
 */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = false,
    icon: ImageVector? = null
) {
    val backgroundColor = if (isPrimary) {
        Brush.linearGradient(
            colors = listOf(PrimaryBlue, PrimaryPurple)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(GlassWhite, GlassWhite)
        )
    }

    val shadowColor = if (isPrimary) PrimaryBlue.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (isPrimary) 15.dp else 10.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) TextPrimary else TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (enabled) TextPrimary else TextTertiary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * 玻璃输入框
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    minLines: Int = 1,
    maxLines: Int = 5
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GlassWhite)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        textStyle = TextStyle(
            color = TextPrimary,
            fontSize = 16.sp
        ),
        minLines = minLines,
        maxLines = maxLines,
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = TextTertiary,
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

/**
 * 浮动玻璃按钮（圆形）
 */
@Composable
fun FloatingGlassButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    size: Dp = 50.dp
) {
    val backgroundColor = if (isActive) {
        Brush.linearGradient(colors = listOf(PrimaryBlue, PrimaryPurple))
    } else {
        Brush.linearGradient(colors = listOf(GlassWhite, GlassWhite))
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (isActive) 15.dp else 10.dp,
                shape = CircleShape,
                ambientColor = if (isActive) PrimaryBlue.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.3f)
            )
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(size * 0.45f)
        )
    }
}

/**
 * 玻璃顶部栏
 */
@Composable
fun GlassTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            FloatingGlassButton(
                icon = Icons.Default.ArrowBack,
                onClick = onBackClick,
                size = 44.dp
            )
        } else {
            Spacer(modifier = Modifier.width(44.dp))
        }

        // 标题卡片
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(GlassWhite)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(content = actions)
    }
}

/**
 * 玻璃标题
 */
@Composable
fun GlassTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            brush = Brush.linearGradient(
                colors = listOf(TextPrimary, TextSecondary)
            ),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

/**
 * 玻璃副标题
 */
@Composable
fun GlassSubtitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = TextSecondary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    )
}

/**
 * 加载指示器
 */
@Composable
fun GlassLoadingIndicator(
    modifier: Modifier = Modifier,
    text: String = "AI 正在思考中..."
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = AccentCyan,
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}
