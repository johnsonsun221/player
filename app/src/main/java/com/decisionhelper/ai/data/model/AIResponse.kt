package com.decisionhelper.ai.data.model

/**
 * OpenAI API 请求体
 */
data class OpenAIRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<ChatMessage>,
    val temperature: Float = 0.7f,
    val max_tokens: Int = 1000
)

/**
 * 聊天消息
 */
data class ChatMessage(
    val role: String,  // "system", "user", "assistant"
    val content: String
)

/**
 * OpenAI API 响应体
 */
data class OpenAIResponse(
    val id: String?,
    val `object`: String?,
    val created: Long?,
    val model: String?,
    val choices: List<Choice>?,
    val usage: Usage?,
    val error: OpenAIError?
)

data class Choice(
    val index: Int,
    val message: ChatMessage,
    val finish_reason: String?
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

data class OpenAIError(
    val message: String?,
    val type: String?,
    val code: String?
)

/**
 * AI 生成选项的响应格式
 */
data class GeneratedOptionsResponse(
    val options: List<GeneratedOption>
)

data class GeneratedOption(
    val title: String,
    val description: String
)

/**
 * AI 评分响应格式
 */
data class ScoredOption(
    val title: String,
    val score: Float,
    val reason: String
)
