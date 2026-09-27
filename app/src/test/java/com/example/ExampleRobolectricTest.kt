package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Oʻzbek Yordamchi", appName)
  }

  @Test
  fun `verify teacher tools configuration`() {
    val tools = com.example.data.model.TeacherToolType.entries
    assertEquals(6, tools.size)
    val konspekt = tools.first { it.id == "konspekt" }
    assertEquals(2, konspekt.numericId)
    assertEquals(600, konspekt.defaultMaxTokens)

    val tahlil = tools.first { it.id == "tahlil" }
    assertEquals(3, tahlil.numericId)
    assertEquals(400, tahlil.defaultMaxTokens)

    val testTool = tools.first { it.id == "test" }
    assertEquals(4, testTool.numericId)
    assertEquals(350, testTool.defaultMaxTokens)

    val lugat = tools.first { it.id == "lugat" }
    assertEquals(5, lugat.numericId)
    assertEquals(200, lugat.defaultMaxTokens)

    val diktant = tools.first { it.id == "diktant" }
    assertEquals(7, diktant.numericId)
    assertEquals(1000, diktant.defaultMaxTokens)

    val mentor = tools.first { it.id == "mentor" }
    assertEquals(10, mentor.numericId)
    assertEquals(400, mentor.defaultMaxTokens)
  }

  @Test
  fun `verify Room repository 0-token caching logic queries database first`() = kotlinx.coroutines.test.runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.AppDatabase::class.java
    ).allowMainThreadQueries().build()

    val dao = db.teacherCacheDao()
    val repository = com.example.data.repository.TeacherRepository(dao)

    // Insert a known item into Room
    val cachedEntity = com.example.data.local.TeacherCacheEntity(
      cacheKey = repository.normalizeCacheKey(com.example.data.model.TeacherToolType.KONSPEKT, "Ot soʻz turkumi (7-sinf)"),
      toolType = "KONSPEKT",
      queryTitle = "Ot soʻz turkumi (7-sinf)",
      promptSent = "test prompt",
      responseContent = "1. Dars maqsadi: test",
      tokensSaved = 380,
      isPreloaded = true
    )
    dao.insertCache(cachedEntity)

    // When an AI prompt request is made, repository queries Room first
    val result = repository.executeTeacherQuery(
      toolType = com.example.data.model.TeacherToolType.KONSPEKT,
      queryTitle = "Ot soʻz turkumi (7-sinf)",
      userPrompt = "dummy prompt without internet",
      forceRefresh = false
    )

    org.junit.Assert.assertTrue(result.isSuccess)
    val generationResult = result.getOrNull()
    org.junit.Assert.assertNotNull(generationResult)
    org.junit.Assert.assertTrue("Should return from Room cache with 0 tokens consumed", generationResult!!.isFromCache)
    assertEquals("1. Dars maqsadi: test", generationResult.content)
    org.junit.Assert.assertTrue(generationResult.tokensSaved > 0)

    // Test export formatting for downloading backend data
    val exportString = repository.formatCachesForExport(listOf(cachedEntity))
    org.junit.Assert.assertTrue(exportString.contains("O'ZBEK YORDAMCHI"))
    org.junit.Assert.assertTrue(exportString.contains("Ot soʻz turkumi (7-sinf)"))
    org.junit.Assert.assertTrue(exportString.contains("1. Dars maqsadi: test"))

    db.close()
  }

  @Test
  fun `verify multi-provider AI options include Gemini and ChatGPT`() {
    val providers = com.example.data.model.AiProvider.entries
    assertEquals(4, providers.size)

    val geminiFlash = providers.first { it.id == "gemini_flash" }
    org.junit.Assert.assertTrue(geminiFlash.isGeminiFamily)
    assertEquals("gemini-2.5-flash", geminiFlash.modelCode)

    val chatGpt = providers.first { it.id == "chatgpt_mini" }
    org.junit.Assert.assertFalse(chatGpt.isGeminiFamily)
    assertEquals("gpt-4o-mini", chatGpt.modelCode)
  }
}

