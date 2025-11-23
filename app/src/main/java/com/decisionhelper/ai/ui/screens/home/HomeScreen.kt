package com.decisionhelper.ai.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.decisionhelper.ai.ui.components.*
import com.decisionhelper.ai.ui.theme.*

/**
 * 首页 - 问题输入页面
 */
@Composable
fun HomeScreen(
    onNavigateToOptions: (Long) -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 顶部栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(44.dp))

                // 装饰性图标
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Psychology, null, tint = TextTertiary)
                    Icon(Icons.Default.AutoAwesome, null, tint = TextTertiary)
                    Icon(Icons.Default.Lightbulb, null, tint = TextTertiary)
                }

                FloatingGlassButton(
                    icon = Icons.Default.History,
                    onClick = onNavigateToHistory,
                    size = 44.dp
                )
            }

            // 内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // 标题
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlassTitle(text = "决策小助理")
                    Spacer(modifier = Modifier.height(8.dp))
                    GlassSubtitle(text = "让 AI 帮你做出更好的选择")
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 问题输入卡片
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
                            text = "你在犹豫什么？",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassTextField(
                        value = uiState.question,
                        onValueChange = viewModel::updateQuestion,
                        placeholder = "例如：中午要吃什么？周末要做什么？",
                        minLines = 3,
                        maxLines = 5
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 条件设定卡片
                GlassCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "条件设定（可选）",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 预算
                    ConditionRow(
                        icon = Icons.Default.AttachMoney,
                        label = "预算",
                        value = uiState.budget,
                        onValueChange = viewModel::updateBudget,
                        placeholder = "例如：200 以内"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 地点
                    ConditionRow(
                        icon = Icons.Default.LocationOn,
                        label = "地点",
                        value = uiState.location,
                        onValueChange = viewModel::updateLocation,
                        placeholder = "例如：公司附近"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 额外限制
                    ConditionRow(
                        icon = Icons.Default.Block,
                        label = "限制",
                        value = uiState.restrictions,
                        onValueChange = viewModel::updateRestrictions,
                        placeholder = "例如：不想吃辣、不想走太远"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 提交按钮
                GlassButton(
                    text = if (uiState.isLoading) "AI 正在思考..." else "帮我想选项",
                    onClick = {
                        viewModel.generateOptions { decisionId ->
                            onNavigateToOptions(decisionId)
                        }
                    },
                    enabled = uiState.question.isNotBlank() && !uiState.isLoading,
                    isPrimary = true,
                    icon = Icons.Default.AutoAwesome
                )

                // 错误提示
                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.error ?: "",
                        color = ErrorRed,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 使用指南卡片
                GlassCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "使用指南",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GuideRow(icon = Icons.Default.Edit, text = "描述你正在犹豫的问题")
                    GuideRow(icon = Icons.Default.Tune, text = "添加预算、地点等条件（可选）")
                    GuideRow(icon = Icons.Default.Psychology, text = "AI 会帮你想出几个选项")
                    GuideRow(icon = Icons.Default.TouchApp, text = "点击「帮我选一个」让 App 决定")
                    GuideRow(icon = Icons.Default.ChatBubble, text = "AI 会解释为什么这样选")
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // 加载遮罩
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(modifier = Modifier.padding(40.dp)) {
                    GlassLoadingIndicator(
                        modifier = Modifier.padding(20.dp),
                        text = "AI 正在分析你的问题..."
                    )
                }
            }
        }
    }
}

@Composable
private fun ConditionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.width(48.dp)
        )
        GlassTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GuideRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}
