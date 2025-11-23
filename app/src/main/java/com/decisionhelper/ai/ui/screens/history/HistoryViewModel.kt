package com.decisionhelper.ai.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.decisionhelper.ai.DecisionHelperApp
import com.decisionhelper.ai.data.model.Decision
import com.decisionhelper.ai.data.repository.DecisionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 历史记录页面 UI 状态
 */
data class HistoryUiState(
    val decisions: List<Decision> = emptyList(),
    val isLoading: Boolean = true,
    val showClearDialog: Boolean = false,
    val error: String? = null
)

/**
 * 历史记录页面 ViewModel
 */
class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val repository = DecisionRepository(
        DecisionHelperApp.instance.database.decisionDao()
    )

    init {
        loadDecisions()
    }

    /**
     * 加载历史记录
     */
    private fun loadDecisions() {
        viewModelScope.launch {
            repository.getRecentDecisions(50).collect { decisions ->
                _uiState.update {
                    it.copy(
                        decisions = decisions,
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * 删除单条记录
     */
    fun deleteDecision(decision: Decision) {
        viewModelScope.launch {
            repository.deleteDecision(decision)
        }
    }

    /**
     * 显示清空对话框
     */
    fun showClearDialog() {
        _uiState.update { it.copy(showClearDialog = true) }
    }

    /**
     * 关闭清空对话框
     */
    fun dismissClearDialog() {
        _uiState.update { it.copy(showClearDialog = false) }
    }

    /**
     * 清空所有记录
     */
    fun clearAllDecisions() {
        viewModelScope.launch {
            repository.clearAllDecisions()
        }
    }
}
