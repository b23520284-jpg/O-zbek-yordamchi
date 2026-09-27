package com.example.data.network

import android.util.Log
import com.example.BuildConfig
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

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    // Global AI Settings strictly as specified
    const val TEMPERATURE = 0.1f
    const val TOP_P = 0.9f
    const val PRESENCE_PENALTY = 0.0f
    const val FREQUENCY_PENALTY = 0.1f
    const val DEFAULT_SYSTEM_INSTRUCTION =
        "Siz faqat Oʻzbek adabiy tili qoidalari va oʻquv dasturi asosida ishlaydigan professional, qisqa va aniq javob beruvchi robot-yordamchisiz. Hech qachon kirish va yakuniy gaplarni ('Salom', 'Mana tayyor boʻldi', 'Umid qilamanki yordam berdi') ishlatmang."

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") "" else key
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun generateContent(
        prompt: String,
        maxOutputTokens: Int,
        customApiKey: String? = null,
        modelName: String = "gemini-2.5-flash"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = customApiKey?.takeIf { it.isNotBlank() } ?: getApiKey()

        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API kaliti topilmadi. AI Studio Secrets paneliga yoki ilova sozlamalariga GEMINI_API_KEY kiriting.")
            )
        }

        try {
            val url = "$BASE_URL/$modelName:generateContent?key=$apiKey"

            // Construct strictly configured JSON payload
            val rootJson = JSONObject().apply {
                // Contents
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })

                // System Instruction
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", DEFAULT_SYSTEM_INSTRUCTION)
                        })
                    })
                })

                // Generation Config with user constraints
                put("generationConfig", JSONObject().apply {
                    put("temperature", TEMPERATURE)
                    put("topP", TOP_P)
                    put("maxOutputTokens", maxOutputTokens)
                })
            }

            val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "API Error: ${response.code} -> $responseBody")
                val errorMessage = try {
                    val errorObj = JSONObject(responseBody).optJSONObject("error")
                    errorObj?.optString("message") ?: "API so'rovida xatolik: HTTP ${response.code}"
                } catch (e: Exception) {
                    "API so'rovida xatolik: HTTP ${response.code}"
                }
                return@withContext Result.failure(IOException(errorMessage))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(IllegalStateException("AI modeli javob qaytarmadi."))
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.getJSONObject(0)?.optString("text")

            if (text.isNullOrBlank()) {
                return@withContext Result.failure(IllegalStateException("AI javobi bo'sh."))
            }

            Result.success(text.trim())
        } catch (e: Exception) {
            Log.e(TAG, "Request failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
