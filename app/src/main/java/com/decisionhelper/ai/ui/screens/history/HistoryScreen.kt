package com.decisionhelper.ai.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.decisionhelper.ai.data.model.Decision
import com.decisionhelper.ai.ui.components.*
import com.decisionhelper.ai.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 历史记录页面
 */
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Long) -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 顶部栏
            GlassTopBar(
                title = "历史记录",
                onBackClick = onNavigateBack,
                actions = {
                    if (uiState.decisions.isNotEmpty()) {
                        FloatingGlassButton(
                            icon = Icons.Default.DeleteSweep,
                            onClick = { viewModel.showClearDialog() },
                            size = 44.dp
                        )
                    }
                }
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    GlassLoadingIndicator()
                }
            } else if (uiState.decisions.isEmpty()) {
                // 空状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(40.dp)
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "暂无历史记录",
                            color = TextSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "你的决策记录将显示在这里",
                            color = TextTertiary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // 统计信息
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "共 ${uiState.decisions.size} 条记录",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }

                    items(uiState.decisions) { decision ->
                        HistoryItem(
                            decision = decision,
                            onClick = {
                                if (decision.selectedOption != null) {
                                    onNavigateToResult(decision.id)
                                }
                            },
                            onDelete = { viewModel.deleteDecision(decision) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // 清空确认对话框
        if (uiState.showClearDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissClearDialog() },
                title = {
                    Text(
                        text = "清空历史记录",
                        color = TextPrimary
                    )
                },
                text = {
                    Text(
                        text = "确定要清空所有历史记录吗？此操作无法撤销。",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAllDecisions()
                            viewModel.dismissClearDialog()
                        }
                    ) {
                        Text("清空", color = ErrorRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissClearDialog() }) {
                        Text("取消", color = TextSecondary)
                    }
                },
                containerColor = GradientMiddle,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
private fun HistoryItem(
    decision: Decision,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()) }
    val dateString = remember(decision.createdAt) {
        dateFormat.format(Date(decision.createdAt))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GlassBorder, GlassBorder.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图标
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (decision.selectedOption != null)
                        Brush.linearGradient(colors = listOf(PrimaryBlue, PrimaryPurple))
                    else
                        Brush.linearGradient(colors = listOf(GlassWhite, GlassWhite))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (decision.selectedOption != null)
                    Icons.Default.CheckCircle
                else
                    Icons.Default.HourglassEmpty,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 内容
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = decision.question,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (decision.selectedOption != null) {
                    Icon(
                        Icons.Default.Done,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = decision.selectedOption.title,
                        color = SuccessGreen,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = dateString,
                    color = TextTertiary,
                    fontSize = 12.sp
                )
            }
        }

        // 删除按钮
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "删除",
                tint = TextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
