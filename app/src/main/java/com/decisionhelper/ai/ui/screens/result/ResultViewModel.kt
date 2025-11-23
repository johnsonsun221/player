package com.decisionhelper.ai.ui.screens.result

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
 * 结果页面 UI 状态
 */
data class ResultUiState(
    val decision: Decision? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * 结果页面 ViewModel
 */
class ResultViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    private val repository = DecisionRepository(
        DecisionHelperApp.instance.database.decisionDao()
    )

    /**
     * 加载决策记录
     */
    fun loadDecision(decisionId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val decision = repository.getDecisionById(decisionId)
            if (decision != null) {
                _uiState.update {
                    it.copy(
                        decision = decision,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "找不到决策记录"
                    )
                }
            }
        }
    }
}
