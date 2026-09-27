package com.example.data.network

import com.example.data.model.AiProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AiCallResult(
    val text: String,
    val providerUsed: AiProvider,
    val estimatedTokens: Int
)

object MultiModelAiClient {
    /**
     * Unified AI generation client that connects in real-time to:
     * - Google Gemini (Gemini 2.5 Flash, Gemini 1.5 Pro) via Firebase AI & Gemini REST API
     * - OpenAI ChatGPT (ChatGPT-4o Mini, ChatGPT-4o) via OpenAI REST API
     */
    suspend fun generate(
        prompt: String,
        provider: AiProvider,
        maxOutputTokens: Int,
        geminiApiKey: String? = null,
        openAiApiKey: String? = null
    ): Result<AiCallResult> = withContext(Dispatchers.IO) {
        val rawResult = if (provider.isGeminiFamily) {
            // Route to Google Gemini / Firebase AI
            val result = FirebaseAiClient.generateContent(
                prompt = prompt,
                maxOutputTokens = maxOutputTokens,
                customApiKey = geminiApiKey
            )
            if (result.isSuccess) {
                result
            } else {
                // Secondary fallback directly to Gemini REST API with target model
                GeminiApiClient.generateContent(
                    prompt = prompt,
                    maxOutputTokens = maxOutputTokens,
                    customApiKey = geminiApiKey,
                    modelName = provider.modelCode
                )
            }
        } else {
            // Route to OpenAI ChatGPT
            val effectiveOpenAiKey = openAiApiKey?.takeIf { it.isNotBlank() }
                ?: return@withContext Result.failure(
                    IllegalStateException("ChatGPT API kaliti kiritilmagan. Sozlamalar menyusida OpenAI API kalitingizni kiriting.")
                )

            OpenAiApiClient.generateContent(
                prompt = prompt,
                modelName = provider.modelCode,
                maxOutputTokens = maxOutputTokens,
                apiKey = effectiveOpenAiKey
            )
        }

        rawResult.map { text ->
            val estimated = (text.length / 4).coerceIn(60, maxOutputTokens)
            AiCallResult(
                text = text,
                providerUsed = provider,
                estimatedTokens = estimated
            )
        }
    }
}
