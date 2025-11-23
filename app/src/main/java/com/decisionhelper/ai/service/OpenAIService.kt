package com.decisionhelper.ai.service

import com.decisionhelper.ai.data.model.OpenAIRequest
import com.decisionhelper.ai.data.model.OpenAIResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * OpenAI API 接口
 */
interface OpenAIService {

    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: OpenAIRequest
    ): OpenAIResponse
}
