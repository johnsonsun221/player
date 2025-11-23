package com.decisionhelper.ai.ui.screens.result

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.decisionhelper.ai.ui.components.*
import com.decisionhelper.ai.ui.theme.*

/**
 * 结果展示页面
 */
@Composable
fun ResultScreen(
    decisionId: Long,
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ResultViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 动画效果
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(decisionId) {
        viewModel.loadDecision(decisionId)
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 顶部栏
            GlassTopBar(
                title = "决策结果",
                onBackClick = onNavigateBack
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    GlassLoadingIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // 庆祝图标
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryBlue, PrimaryPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 标题
                    Text(
                        text = "今天的选择",
                        color = TextSecondary,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 选中的选项
                    Text(
                        text = uiState.decision?.selectedOption?.title ?: "未知选项",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    if (uiState.decision?.selectedOption?.description?.isNotBlank() == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.decision?.selectedOption?.description ?: "",
                            color = TextSecondary,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // AI 解释卡片
                    GlassCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "AI 小助理说",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = uiState.decision?.aiExplanation
                                ?: "这个选择很适合你当前的需求！",
                            color = TextSecondary,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 原始问题卡片
                    GlassCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.QuestionMark,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "你的问题",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = uiState.decision?.question ?: "",
                            color = TextSecondary,
                            fontSize = 15.sp
                        )

                        // 条件显示
                        val conditions = uiState.decision?.conditions
                        if (conditions != null) {
                            val hasConditions = conditions.budget.isNotBlank() ||
                                    conditions.location.isNotBlank() ||
                                    conditions.restrictions.isNotBlank()

                            if (hasConditions) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = GlassBorder, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(12.dp))

                                if (conditions.budget.isNotBlank()) {
                                    ConditionRow(label = "预算", value = conditions.budget)
                                }
                                if (conditions.location.isNotBlank()) {
                                    ConditionRow(label = "地点", value = conditions.location)
                                }
                                if (conditions.restrictions.isNotBlank()) {
                                    ConditionRow(label = "限制", value = conditions.restrictions)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 其他选项卡片
                    val otherOptions = uiState.decision?.options?.filter {
                        it.title != uiState.decision?.selectedOption?.title
                    } ?: emptyList()

                    if (otherOptions.isNotEmpty()) {
                        GlassCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = null,
                                    tint = AccentCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "其他选项",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            otherOptions.forEach { option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(TextTertiary)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = option.title,
                                        color = TextTertiary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 操作按钮
                    GlassButton(
                        text = "再来一次",
                        onClick = onNavigateHome,
                        isPrimary = true,
                        icon = Icons.Default.Refresh
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun ConditionRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label：",
            color = TextTertiary,
            fontSize = 14.sp,
            modifier = Modifier.width(48.dp)
        )
        Text(
            text = value,
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}
