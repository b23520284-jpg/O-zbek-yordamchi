package com.example.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object OpenAiApiClient {
    private const val TAG = "OpenAiApiClient"
    private const val BASE_URL = "https://api.openai.com/v1/chat/completions"

    const val TEMPERATURE = 0.1
    const val DEFAULT_SYSTEM_INSTRUCTION =
        "Siz faqat Oʻzbek adabiy tili qoidalari va oʻquv dasturi asosida ishlaydigan professional, qisqa va aniq javob beruvchi robot-yordamchisiz. Hech qachon kirish va yakuniy gaplarni ('Salom', 'Mana tayyor boʻldi', 'Umid qilamanki yordam berdi') ishlatmang."

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(
        prompt: String,
        modelName: String = "gpt-4o-mini",
        maxOutputTokens: Int,
        apiKey: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("OpenAI ChatGPT API kaliti kiritilmagan. Sozlamalar oynasida kalitni kiriting.")
            )
        }

        try {
            val messagesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", DEFAULT_SYSTEM_INSTRUCTION)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }

            val requestJson = JSONObject().apply {
                put("model", modelName)
                put("messages", messagesArray)
                put("temperature", TEMPERATURE)
                put("max_tokens", maxOutputTokens)
            }

            val requestBody = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer $apiKey")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "OpenAI API Error: ${response.code} -> $responseBody")
                val errorMsg = try {
                    val errObj = JSONObject(responseBody).optJSONObject("error")
                    errObj?.optString("message") ?: "OpenAI HTTP xatoligi: ${response.code}"
                } catch (e: Exception) {
                    "OpenAI HTTP xatoligi: ${response.code}"
                }
                return@withContext Result.failure(IOException(errorMsg))
            }

            val jsonResponse = JSONObject(responseBody)
            val choices = jsonResponse.optJSONArray("choices")
            if (choices == null || choices.length() == 0) {
                return@withContext Result.failure(IllegalStateException("ChatGPT javob qaytarmadi."))
            }

            val firstChoice = choices.getJSONObject(0)
            val message = firstChoice.optJSONObject("message")
            val content = message?.optString("content")

            if (content.isNullOrBlank()) {
                return@withContext Result.failure(IllegalStateException("ChatGPT javobi bo'sh."))
            }

            Result.success(content.trim())
        } catch (e: Exception) {
            Log.e(TAG, "OpenAI request failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
