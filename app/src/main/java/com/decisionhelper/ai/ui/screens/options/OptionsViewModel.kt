package com.decisionhelper.ai.ui.screens.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.decisionhelper.ai.DecisionHelperApp
import com.decisionhelper.ai.data.model.Decision
import com.decisionhelper.ai.data.model.DecisionOption
import com.decisionhelper.ai.data.repository.DecisionRepository
import com.decisionhelper.ai.service.AIDecisionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * 选项页面 UI 状态
 */
data class OptionsUiState(
    val decision: Decision? = null,
    val options: List<DecisionOption> = emptyList(),
    val selectedIndex: Int? = null,
    val newOptionTitle: String = "",
    val isLoading: Boolean = true,
    val isDeciding: Boolean = false,
    val error: String? = null
)

/**
 * 选项页面 ViewModel
 */
class OptionsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OptionsUiState())
    val uiState: StateFlow<OptionsUiState> = _uiState.asStateFlow()

    private val aiService = AIDecisionService()
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
                        options = decision.options,
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

    /**
     * 选择选项
     */
    fun selectOption(index: Int) {
        _uiState.update {
            it.copy(selectedIndex = if (it.selectedIndex == index) null else index)
        }
    }

    /**
     * 删除选项
     */
    fun removeOption(index: Int) {
        _uiState.update { state ->
            val newOptions = state.options.toMutableList().apply { removeAt(index) }
            val newSelectedIndex = when {
                state.selectedIndex == null -> null
                state.selectedIndex == index -> null
                state.selectedIndex > index -> state.selectedIndex - 1
                else -> state.selectedIndex
            }
            state.copy(options = newOptions, selectedIndex = newSelectedIndex)
        }
    }

    /**
     * 更新新选项标题
     */
    fun updateNewOptionTitle(title: String) {
        _uiState.update { it.copy(newOptionTitle = title) }
    }

    /**
     * 添加自定义选项
     */
    fun addCustomOption() {
        val title = _uiState.value.newOptionTitle.trim()
        if (title.isBlank()) return

        _uiState.update { state ->
            val newOption = DecisionOption(title = title, description = "用户自定义选项")
            state.copy(
                options = state.options + newOption,
                newOptionTitle = ""
            )
        }
    }

    /**
     * 让 App 帮忙决策（随机选择）
     */
    fun makeDecision(onSuccess: (Long) -> Unit) {
        val state = _uiState.value
        if (state.options.isEmpty() || state.decision == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeciding = true) }

            // 随机选择一个选项
            val selectedIndex = Random.nextInt(state.options.size)
            val selectedOption = state.options[selectedIndex]

            // 调用 AI 解释选择理由
            val explanationResult = aiService.explainDecision(
                question = state.decision.question,
                conditions = state.decision.conditions,
                allOptions = state.options,
                selectedOption = selectedOption
            )

            val explanation = explanationResult.getOrDefault("这个选择很适合你当前的需求！")

            // 更新决策记录
            val updatedDecision = state.decision.copy(
                options = state.options,
                selectedOption = selectedOption,
                aiExplanation = explanation
            )

            repository.updateDecision(updatedDecision)

            _uiState.update { it.copy(isDeciding = false) }
            onSuccess(updatedDecision.id)
        }
    }

    /**
     * 确认用户的选择
     */
    fun confirmSelection(onSuccess: (Long) -> Unit) {
        val state = _uiState.value
        val selectedIndex = state.selectedIndex ?: return
        val decision = state.decision ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeciding = true) }

            val selectedOption = state.options[selectedIndex]

            // 调用 AI 解释选择理由
            val explanationResult = aiService.explainDecision(
                question = decision.question,
                conditions = decision.conditions,
                allOptions = state.options,
                selectedOption = selectedOption
            )

            val explanation = explanationResult.getOrDefault("这个选择很适合你当前的需求！")

            // 更新决策记录
            val updatedDecision = decision.copy(
                options = state.options,
                selectedOption = selectedOption,
                aiExplanation = explanation
            )

            repository.updateDecision(updatedDecision)

            _uiState.update { it.copy(isDeciding = false) }
            onSuccess(updatedDecision.id)
        }
    }
}
