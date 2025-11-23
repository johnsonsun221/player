package com.decisionhelper.ai.ui.screens.options

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.decisionhelper.ai.data.model.DecisionOption
import com.decisionhelper.ai.ui.components.*
import com.decisionhelper.ai.ui.theme.*

/**
 * 选项列表页面
 */
@Composable
fun OptionsScreen(
    decisionId: Long,
    onNavigateToResult: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OptionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                title = "选择选项",
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 问题显示
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
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
                        }
                    }

                    // 选项列表标题
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = null,
                                    tint = AccentCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI 建议的选项",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "${uiState.options.size} 个选项",
                                color = TextTertiary,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // 选项列表
                    itemsIndexed(uiState.options) { index, option ->
                        OptionItem(
                            option = option,
                            index = index,
                            isSelected = uiState.selectedIndex == index,
                            onClick = { viewModel.selectOption(index) },
                            onDelete = { viewModel.removeOption(index) }
                        )
                    }

                    // 添加自定义选项
                    item {
                        AddOptionCard(
                            newOptionTitle = uiState.newOptionTitle,
                            onTitleChange = viewModel::updateNewOptionTitle,
                            onAdd = viewModel::addCustomOption
                        )
                    }

                    // 决策按钮
                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        GlassButton(
                            text = if (uiState.isDeciding) "正在决策..." else "帮我选一个！",
                            onClick = {
                                viewModel.makeDecision { id ->
                                    onNavigateToResult(id)
                                }
                            },
                            enabled = uiState.options.isNotEmpty() && !uiState.isDeciding,
                            isPrimary = true,
                            icon = Icons.Default.Casino
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 如果用户已选择，显示「使用我的选择」按钮
                        if (uiState.selectedIndex != null) {
                            GlassButton(
                                text = "使用我的选择",
                                onClick = {
                                    viewModel.confirmSelection { id ->
                                        onNavigateToResult(id)
                                    }
                                },
                                enabled = !uiState.isDeciding,
                                icon = Icons.Default.Check
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        // 决策遮罩
        if (uiState.isDeciding) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(modifier = Modifier.padding(40.dp)) {
                    GlassLoadingIndicator(
                        modifier = Modifier.padding(20.dp),
                        text = "正在为你做出最佳选择..."
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionItem(
    option: DecisionOption,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = if (isSelected) {
        Brush.linearGradient(colors = listOf(PrimaryBlue, PrimaryPurple))
    } else {
        Brush.linearGradient(colors = listOf(GlassBorder, GlassBorder.copy(alpha = 0.3f)))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else GlassWhite)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 序号
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isSelected)
                        Brush.linearGradient(colors = listOf(PrimaryBlue, PrimaryPurple))
                    else
                        Brush.linearGradient(colors = listOf(GlassWhite, GlassWhite))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${index + 1}",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 选项内容
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (option.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = option.description,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 选中指示 / 删除按钮
        if (isSelected) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "已选择",
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
        } else {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "删除",
                    tint = TextTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AddOptionCard(
    newOptionTitle: String,
    onTitleChange: (String) -> Unit,
    onAdd: () -> Unit
) {
    GlassCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = AccentCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "添加自定义选项",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassTextField(
                value = newOptionTitle,
                onValueChange = onTitleChange,
                placeholder = "输入选项名称",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            FloatingGlassButton(
                icon = Icons.Default.Add,
                onClick = onAdd,
                isActive = newOptionTitle.isNotBlank(),
                size = 44.dp
            )
        }
    }
}
