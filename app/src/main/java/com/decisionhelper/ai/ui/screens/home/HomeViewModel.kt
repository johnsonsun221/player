package com.decisionhelper.ai.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.decisionhelper.ai.DecisionHelperApp
import com.decisionhelper.ai.data.model.Decision
import com.decisionhelper.ai.data.model.DecisionConditions
import com.decisionhelper.ai.data.repository.DecisionRepository
import com.decisionhelper.ai.service.AIDecisionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 首页 UI 状态
 */
data class HomeUiState(
    val question: String = "",
    val budget: String = "",
    val location: String = "",
    val restrictions: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 首页 ViewModel
 */
class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val aiService = AIDecisionService()
    private val repository = DecisionRepository(
        DecisionHelperApp.instance.database.decisionDao()
    )

    fun updateQuestion(value: String) {
        _uiState.update { it.copy(question = value, error = null) }
    }

    fun updateBudget(value: String) {
        _uiState.update { it.copy(budget = value) }
    }

    fun updateLocation(value: String) {
        _uiState.update { it.copy(location = value) }
    }

    fun updateRestrictions(value: String) {
        _uiState.update { it.copy(restrictions = value) }
    }

    /**
     * 生成选项
     */
    fun generateOptions(onSuccess: (Long) -> Unit) {
        val state = _uiState.value
        if (state.question.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val conditions = DecisionConditions(
                budget = state.budget,
                location = state.location,
                restrictions = state.restrictions
            )

            val result = aiService.generateOptions(state.question, conditions)

            result.fold(
                onSuccess = { options ->
                    // 保存决策记录
                    val decision = Decision(
                        question = state.question,
                        conditions = conditions,
                        options = options,
                        selectedOption = null
                    )
                    val decisionId = repository.saveDecision(decision)

                    _uiState.update {
                        HomeUiState() // 重置状态
                    }

                    onSuccess(decisionId)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "生成选项失败：${error.message}"
                        )
                    }
                }
            )
        }
    }
}
