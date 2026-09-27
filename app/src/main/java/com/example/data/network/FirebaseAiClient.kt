package com.example.data.network

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FirebaseAiClient {
    private const val TAG = "FirebaseAiClient"
    private const val MODEL_NAME = "gemini-2.5-flash"

    const val TEMPERATURE = 0.1f
    const val TOP_P = 0.9f
    const val DEFAULT_SYSTEM_INSTRUCTION =
        "Siz faqat Oʻzbek adabiy tili qoidalari va oʻquv dasturi asosida ishlaydigan professional, qisqa va aniq javob beruvchi robot-yordamchisiz. Hech qachon kirish va yakuniy gaplarni ('Salom', 'Mana tayyor boʻldi', 'Umid qilamanki yordam berdi') ishlatmang."

    /**
     * Executes prompt generation via Firebase AI SDK with seamless fallback
     * to direct Gemini REST API if Firebase is not fully provisioned.
     */
    suspend fun generateContent(
        prompt: String,
        maxOutputTokens: Int,
        customApiKey: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        // Try Firebase AI first
        try {
            val generativeModel = Firebase.ai.generativeModel(
                modelName = MODEL_NAME,
                generationConfig = generationConfig {
                    temperature = TEMPERATURE
                    topP = TOP_P
                    this.maxOutputTokens = maxOutputTokens
                },
                systemInstruction = content {
                    text(DEFAULT_SYSTEM_INSTRUCTION)
                }
            )

            val response = generativeModel.generateContent(prompt)
            val responseText = response.text?.trim()
            if (!responseText.isNullOrBlank()) {
                Log.d(TAG, "Successfully generated response via Firebase AI")
                return@withContext Result.success(responseText)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase AI call failed or not initialized: ${e.message}. Falling back to GeminiApiClient", e)
        }

        // Fallback to Gemini REST API client
        return@withContext GeminiApiClient.generateContent(
            prompt = prompt,
            maxOutputTokens = maxOutputTokens,
            customApiKey = customApiKey
        )
    }
}
