package com.decisionhelper.ai.service

import com.decisionhelper.ai.BuildConfig
import com.decisionhelper.ai.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * AI 决策服务 - 封装 OpenAI API 调用
 */
class AIDecisionService {

    private val gson = Gson()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val openAIService = retrofit.create(OpenAIService::class.java)

    private val apiKey: String
        get() = BuildConfig.OPENAI_API_KEY

    /**
     * 生成决策选项
     */
    suspend fun generateOptions(
        question: String,
        conditions: DecisionConditions
    ): Result<List<DecisionOption>> {
        return try {
            val prompt = buildGenerateOptionsPrompt(question, conditions)

            val request = OpenAIRequest(
                messages = listOf(
                    ChatMessage("system", SYSTEM_PROMPT_GENERATE),
                    ChatMessage("user", prompt)
                )
            )

            val response = openAIService.createChatCompletion(
                "Bearer $apiKey",
                request
            )

            if (response.error != null) {
                return Result.failure(Exception(response.error.message ?: "API Error"))
            }

            val content = response.choices?.firstOrNull()?.message?.content
                ?: return Result.failure(Exception("No response from AI"))

            val options = parseOptionsResponse(content)
            Result.success(options)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 为选项评分（进阶功能）
     */
    suspend fun scoreOptions(
        question: String,
        conditions: DecisionConditions,
        options: List<DecisionOption>
    ): Result<List<DecisionOption>> {
        return try {
            val prompt = buildScoreOptionsPrompt(question, conditions, options)

            val request = OpenAIRequest(
                messages = listOf(
                    ChatMessage("system", SYSTEM_PROMPT_SCORE),
                    ChatMessage("user", prompt)
                )
            )

            val response = openAIService.createChatCompletion(
                "Bearer $apiKey",
                request
            )

            if (response.error != null) {
                return Result.failure(Exception(response.error.message ?: "API Error"))
            }

            val content = response.choices?.firstOrNull()?.message?.content
                ?: return Result.failure(Exception("No response from AI"))

            val scoredOptions = parseScoredOptionsResponse(content, options)
            Result.success(scoredOptions)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 解释选择理由
     */
    suspend fun explainDecision(
        question: String,
        conditions: DecisionConditions,
        allOptions: List<DecisionOption>,
        selectedOption: DecisionOption
    ): Result<String> {
        return try {
            val prompt = buildExplainPrompt(question, conditions, allOptions, selectedOption)

            val request = OpenAIRequest(
                messages = listOf(
                    ChatMessage("system", SYSTEM_PROMPT_EXPLAIN),
                    ChatMessage("user", prompt)
                ),
                max_tokens = 300
            )

            val response = openAIService.createChatCompletion(
                "Bearer $apiKey",
                request
            )

            if (response.error != null) {
                return Result.failure(Exception(response.error.message ?: "API Error"))
            }

            val content = response.choices?.firstOrNull()?.message?.content
                ?: return Result.failure(Exception("No response from AI"))

            Result.success(content.trim())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== Prompt 构建 ==========

    private fun buildGenerateOptionsPrompt(question: String, conditions: DecisionConditions): String {
        val sb = StringBuilder()
        sb.append("使用者的问题：$question\n")

        if (conditions.budget.isNotBlank()) {
            sb.append("预算：${conditions.budget}\n")
        }
        if (conditions.location.isNotBlank()) {
            sb.append("地点/范围：${conditions.location}\n")
        }
        if (conditions.restrictions.isNotBlank()) {
            sb.append("额外限制：${conditions.restrictions}\n")
        }

        return sb.toString()
    }

    private fun buildScoreOptionsPrompt(
        question: String,
        conditions: DecisionConditions,
        options: List<DecisionOption>
    ): String {
        val sb = StringBuilder()
        sb.append("使用者的问题：$question\n")

        if (conditions.budget.isNotBlank()) {
            sb.append("预算：${conditions.budget}\n")
        }
        if (conditions.location.isNotBlank()) {
            sb.append("地点/范围：${conditions.location}\n")
        }
        if (conditions.restrictions.isNotBlank()) {
            sb.append("额外限制：${conditions.restrictions}\n")
        }

        sb.append("\n候选选项：\n")
        options.forEachIndexed { index, option ->
            sb.append("${index + 1}. ${option.title}")
            if (option.description.isNotBlank()) {
                sb.append(" - ${option.description}")
            }
            sb.append("\n")
        }

        return sb.toString()
    }

    private fun buildExplainPrompt(
        question: String,
        conditions: DecisionConditions,
        allOptions: List<DecisionOption>,
        selectedOption: DecisionOption
    ): String {
        val sb = StringBuilder()
        sb.append("使用者的问题：$question\n")

        if (conditions.budget.isNotBlank()) {
            sb.append("预算：${conditions.budget}\n")
        }
        if (conditions.location.isNotBlank()) {
            sb.append("地点/范围：${conditions.location}\n")
        }
        if (conditions.restrictions.isNotBlank()) {
            sb.append("额外限制：${conditions.restrictions}\n")
        }

        sb.append("\n所有选项：\n")
        allOptions.forEach { option ->
            sb.append("- ${option.title}\n")
        }

        sb.append("\n最终选择：${selectedOption.title}\n")

        return sb.toString()
    }

    // ========== 响应解析 ==========

    private fun parseOptionsResponse(content: String): List<DecisionOption> {
        return try {
            // 尝试解析 JSON
            val cleanedContent = content
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val response = gson.fromJson(cleanedContent, GeneratedOptionsResponse::class.java)
            response.options.map { DecisionOption(it.title, it.description) }
        } catch (e: Exception) {
            // 如果 JSON 解析失败，尝试解析纯文本
            parseTextOptions(content)
        }
    }

    private fun parseTextOptions(content: String): List<DecisionOption> {
        val lines = content.lines().filter { it.isNotBlank() }
        return lines.mapNotNull { line ->
            // 尝试解析 "1. 标题 - 描述" 格式
            val cleaned = line.replace(Regex("^\\d+\\.?\\s*"), "").trim()
            if (cleaned.isNotBlank()) {
                val parts = cleaned.split(" - ", " – ", "：", ": ", limit = 2)
                DecisionOption(
                    title = parts[0].trim(),
                    description = parts.getOrNull(1)?.trim() ?: ""
                )
            } else null
        }
    }

    private fun parseScoredOptionsResponse(
        content: String,
        originalOptions: List<DecisionOption>
    ): List<DecisionOption> {
        return try {
            val cleanedContent = content
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val type = object : TypeToken<List<ScoredOption>>() {}.type
            val scoredOptions: List<ScoredOption> = gson.fromJson(cleanedContent, type)

            originalOptions.map { original ->
                val scored = scoredOptions.find {
                    it.title.contains(original.title, ignoreCase = true) ||
                            original.title.contains(it.title, ignoreCase = true)
                }
                original.copy(
                    score = scored?.score ?: 0.5f,
                    reason = scored?.reason ?: ""
                )
            }
        } catch (e: Exception) {
            // 解析失败时返回平均分数
            originalOptions.map { it.copy(score = 0.5f) }
        }
    }

    companion object {
        private const val SYSTEM_PROMPT_GENERATE = """你是一个友善的决策助理。使用者目前有一个犹豫的问题，请根据使用者提供的描述与限制，产生 3-5 个具体且可执行的选项。

请以 JSON 格式回传，格式如下：
{
  "options": [
    { "title": "选项标题", "description": "简短说明" },
    ...
  ]
}

注意：
- 选项要具体、可执行
- 描述要简洁明了
- 考虑用户给出的所有限制条件
- 只返回 JSON，不要包含其他文字"""

        private const val SYSTEM_PROMPT_SCORE = """你是一个分析型决策助理。请针对每个选项给一个 0~1 之间的推荐分数，并简短说明原因。

请以 JSON 格式回传，格式如下：
[
  { "title": "选项标题", "score": 0.8, "reason": "推荐原因" },
  ...
]

注意：
- 分数范围 0-1，1 表示最推荐
- 原因要简洁（10-20字）
- 综合考虑所有条件
- 只返回 JSON，不要包含其他文字"""

        private const val SYSTEM_PROMPT_EXPLAIN = """你是一个温暖友善的决策助理。使用者已经做出了选择，请用轻松友善的语气，说明为什么这个选择是合理或不错的决定。

注意：
- 用 2-3 句话即可
- 语气要友善、像朋友给建议
- 强调正面的理由
- 可以加一点小幽默
- 直接回复文字，不需要 JSON 格式"""
    }
}
