package com.example.data.model

enum class AiProvider(
    val id: String,
    val title: String,
    val shortName: String,
    val modelCode: String,
    val isGeminiFamily: Boolean
) {
    GEMINI_FLASH(
        id = "gemini_flash",
        title = "Google Gemini 2.5 Flash (Tezkor va tavsiya etiladi)",
        shortName = "Gemini Flash",
        modelCode = "gemini-2.5-flash",
        isGeminiFamily = true
    ),
    GEMINI_PRO(
        id = "gemini_pro",
        title = "Google Gemini 1.5 Pro (Murakkab tahlil uchun)",
        shortName = "Gemini Pro",
        modelCode = "gemini-1.5-pro",
        isGeminiFamily = true
    ),
    CHATGPT_4O_MINI(
        id = "chatgpt_mini",
        title = "OpenAI ChatGPT-4o Mini (Tezkor va lo'nda)",
        shortName = "ChatGPT-4o Mini",
        modelCode = "gpt-4o-mini",
        isGeminiFamily = false
    ),
    CHATGPT_4O(
        id = "chatgpt_4o",
        title = "OpenAI ChatGPT-4o (Yuqori intellekt)",
        shortName = "ChatGPT-4o",
        modelCode = "gpt-4o",
        isGeminiFamily = false
    );

    companion object {
        fun fromId(id: String): AiProvider {
            return entries.firstOrNull { it.id == id } ?: GEMINI_FLASH
        }
    }
}
