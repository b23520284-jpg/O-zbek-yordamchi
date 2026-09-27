package com.example.data.repository

import com.example.data.local.TeacherCacheDao
import com.example.data.local.TeacherCacheEntity
import com.example.data.model.AiProvider
import com.example.data.model.TeacherToolType
import com.example.data.network.MultiModelAiClient
import kotlinx.coroutines.flow.Flow
import java.util.Locale

data class GenerationResult(
    val content: String,
    val isFromCache: Boolean,
    val tokensSaved: Int,
    val providerUsed: String = "Google Gemini 2.5 Flash",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Room database repository that implements the 0-token caching logic:
 * When an AI prompt request is made, the repository FIRST queries Room database
 * for a cached result before initiating an API call to Gemini or ChatGPT.
 */
class TeacherRepository(
    private val cacheDao: TeacherCacheDao
) {
    val allCaches: Flow<List<TeacherCacheEntity>> = cacheDao.getAllCaches()
    val totalTokensSaved: Flow<Int> = cacheDao.getTotalTokensSaved()

    fun getCachesByTool(toolType: String): Flow<List<TeacherCacheEntity>> =
        cacheDao.getCachesByTool(toolType)

    fun searchCaches(query: String): Flow<List<TeacherCacheEntity>> =
        cacheDao.searchCaches(query)

    suspend fun getCachedResult(toolType: TeacherToolType, queryTitle: String): TeacherCacheEntity? {
        val key = normalizeCacheKey(toolType, queryTitle)
        return cacheDao.getCacheByKey(key)
    }

    suspend fun deleteCache(id: Long) = cacheDao.deleteCacheById(id)

    suspend fun clearUserCaches() = cacheDao.clearUserCaches()

    /**
     * Executes the query following the exact 0-token caching pattern:
     * 1. Query Room database first.
     * 2. If present -> return cached result immediately (0 tokens consumed, no API call initiated).
     * 3. If absent -> only then initiate a real-time API call to selected AI Provider (Gemini or ChatGPT).
     * 4. Save result to Room database cache and return to caller.
     */
    suspend fun executeTeacherQuery(
        toolType: TeacherToolType,
        queryTitle: String,
        userPrompt: String,
        provider: AiProvider = AiProvider.GEMINI_FLASH,
        geminiApiKey: String? = null,
        openAiApiKey: String? = null,
        forceRefresh: Boolean = false,
        explicitMaxTokens: Int? = null
    ): Result<GenerationResult> {
        val normalizedKey = normalizeCacheKey(toolType, queryTitle)
        val maxTokens = explicitMaxTokens ?: toolType.resolveMaxTokens(userPrompt)

        // STEP 1: Query Room database for cached result BEFORE initiating any API call
        if (!forceRefresh) {
            val cachedItem = cacheDao.getCacheByKey(normalizedKey)
            if (cachedItem != null) {
                // Returned from local Room database -> 0 tokens spent on AI!
                return Result.success(
                    GenerationResult(
                        content = cachedItem.responseContent,
                        isFromCache = true,
                        tokensSaved = cachedItem.tokensSaved.coerceAtLeast(maxTokens / 2),
                        providerUsed = "0-Token Kesh (Mahalliy Baza)",
                        timestamp = cachedItem.timestamp
                    )
                )
            }
        }

        // STEP 2: Not in Room database -> initiate real-time API call to chosen AI provider
        val apiResult = MultiModelAiClient.generate(
            prompt = userPrompt,
            provider = provider,
            maxOutputTokens = maxTokens,
            geminiApiKey = geminiApiKey,
            openAiApiKey = openAiApiKey
        )

        return if (apiResult.isSuccess) {
            val callResult = apiResult.getOrThrow()
            val generatedText = callResult.text
            val estimatedTokens = callResult.estimatedTokens

            // STEP 3: Save to Room database for instant 0-token retrieval in all future requests
            val entity = TeacherCacheEntity(
                cacheKey = normalizedKey,
                toolType = toolType.name,
                queryTitle = queryTitle.trim(),
                promptSent = userPrompt,
                responseContent = generatedText,
                timestamp = System.currentTimeMillis(),
                tokensSaved = estimatedTokens,
                isPreloaded = false
            )
            cacheDao.insertCache(entity)

            Result.success(
                GenerationResult(
                    content = generatedText,
                    isFromCache = false,
                    tokensSaved = estimatedTokens,
                    providerUsed = callResult.providerUsed.title
                )
            )
        } else {
            // If network/API error, check if a similar cached item exists in Room as fallback
            val fallbackMatch = cacheDao.getCacheByKey(normalizedKey)
            if (fallbackMatch != null) {
                Result.success(
                    GenerationResult(
                        content = fallbackMatch.responseContent,
                        isFromCache = true,
                        tokensSaved = fallbackMatch.tokensSaved,
                        providerUsed = "0-Token Kesh (Oflayn Zaxira)",
                        timestamp = fallbackMatch.timestamp
                    )
                )
            } else {
                Result.failure(apiResult.exceptionOrNull() ?: Exception("Noma'lum xatolik"))
            }
        }
    }

    /**
     * Prepares full exportable text backup of all teacher materials stored in Room.
     */
    fun formatCachesForExport(caches: List<TeacherCacheEntity>): String {
        val sb = StringBuilder()
        sb.appendLine("==================================================")
        sb.appendLine("O'ZBEK YORDAMCHI — DARSLIKLAR VA KESHLANGAN MATERIALLAR")
        sb.appendLine("Jami materiallar soni: ${caches.size}")
        sb.appendLine("Eksport vaqti: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(java.util.Date())}")
        sb.appendLine("==================================================\n")

        caches.forEachIndexed { index, item ->
            sb.appendLine("--- [${index + 1}] ${item.toolType} | ${item.queryTitle} ---")
            sb.appendLine("So'rov: ${item.promptSent}")
            sb.appendLine("Javob / Material:")
            sb.appendLine(item.responseContent)
            sb.appendLine("\n" + "-".repeat(50) + "\n")
        }

        return sb.toString()
    }

    fun normalizeCacheKey(tool: TeacherToolType, query: String): String {
        val cleanQuery = query.trim().lowercase(Locale.ROOT).replace(Regex("\\s+"), " ")
        return "${tool.name}::$cleanQuery"
    }
}
