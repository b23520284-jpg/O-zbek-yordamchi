package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.TeacherCacheEntity
import com.example.data.model.TeacherToolType
import com.example.data.network.GeminiApiClient
import com.example.data.repository.GenerationResult
import com.example.data.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavDestination {
    HISTORICAL_JOURNEY,
    AI_STUDIO,
    CACHE_LIBRARY
}

class TeacherViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = TeacherRepository(database.teacherCacheDao())

    // Navigation state
    private val _currentDestination = MutableStateFlow(AppNavDestination.HISTORICAL_JOURNEY)
    val currentDestination: StateFlow<AppNavDestination> = _currentDestination.asStateFlow()

    // Selected Tool
    private val _selectedTool = MutableStateFlow(TeacherToolType.KONSPEKT)
    val selectedTool: StateFlow<TeacherToolType> = _selectedTool.asStateFlow()

    // Input States for each tool
    // 2-Tugma: Konspekt
    var konspektMavzu = MutableStateFlow("Ot soʻz turkumi")
    var konspektSinf = MutableStateFlow("7-sinf")

    // 3-Tugma: Tahlil
    var tahlilMatn = MutableStateFlow("Gʻurbatda gʻarib shodmon boʻlmas emish,\nEl anga necha mehribon boʻlmas emish.\nOltun qafas ichra gar qizil gul butsa,\nBulbulgʻa tikandek oshyon boʻlmas emish.")

    // 4-Tugma: Test
    var testMavzu = MutableStateFlow("Fe'l zamonlari (7-sinf)")

    // 5-Tugma: Lug'at
    var lugatSoz = MutableStateFlow("Ma'rifat")

    // 7-Tugma: Diktant
    var diktantXomMatn = MutableStateFlow("bugun maktabimizda alisher navoiy tavaludiga bagishlangan ajoyib tadbir bolib otdi u yerda oquvchilar gazallar aytdilar")

    // 10-Tugma: Mentor
    var mentorMuammo = MutableStateFlow("O'quvchilar uyga vazifa bajarishga qiziqmayapti")

    // Output & Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentResult = MutableStateFlow<GenerationResult?>(null)
    val currentResult: StateFlow<GenerationResult?> = _currentResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _lastPromptSent = MutableStateFlow<String?>(null)
    val lastPromptSent: StateFlow<String?> = _lastPromptSent.asStateFlow()

    // Cache management
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>("BARCHASI")
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    val totalTokensSaved: StateFlow<Int> = repository.totalTokensSaved
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1540)

    val cachedItems: StateFlow<List<TeacherCacheEntity>> = combine(
        repository.allCaches,
        _searchQuery,
        _selectedCategoryFilter
    ) { list, query, filter ->
        list.filter { item ->
            val matchesCategory = filter == "BARCHASI" || filter == null || item.toolType == filter
            val matchesQuery = query.isBlank() ||
                item.queryTitle.contains(query, ignoreCase = true) ||
                item.responseContent.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Custom API Key setting (Gemini & OpenAI ChatGPT)
    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _openAiApiKey = MutableStateFlow("")
    val openAiApiKey: StateFlow<String> = _openAiApiKey.asStateFlow()

    private val _selectedProvider = MutableStateFlow(com.example.data.model.AiProvider.GEMINI_FLASH)
    val selectedProvider: StateFlow<com.example.data.model.AiProvider> = _selectedProvider.asStateFlow()

    init {
        // Load initial state for the default tool
        executeTool(forceRefresh = false)
    }

    fun setDestination(dest: AppNavDestination) {
        _currentDestination.value = dest
    }

    fun selectTool(tool: TeacherToolType) {
        _selectedTool.value = tool
        _errorMessage.value = null
        _currentResult.value = null
        // Trigger auto check cache for pre-filled sample
        executeTool(forceRefresh = false)
    }

    fun setSelectedProvider(provider: com.example.data.model.AiProvider) {
        _selectedProvider.value = provider
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(filter: String?) {
        _selectedCategoryFilter.value = filter
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
    }

    fun setOpenAiApiKey(key: String) {
        _openAiApiKey.value = key
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun deleteCache(id: Long) {
        viewModelScope.launch {
            repository.deleteCache(id)
        }
    }

    fun clearAllUserCache() {
        viewModelScope.launch {
            repository.clearUserCaches()
        }
    }

    fun exportAllCachesAsText(): String {
        return repository.formatCachesForExport(cachedItems.value)
    }

    fun executeTool(forceRefresh: Boolean = false) {
        val tool = _selectedTool.value
        val (queryTitle, prompt) = buildPromptForTool(tool)
        val calculatedMaxTokens = tool.resolveMaxTokens(prompt)

        _isLoading.value = true
        _errorMessage.value = null
        _lastPromptSent.value = prompt

        viewModelScope.launch {
            val result = repository.executeTeacherQuery(
                toolType = tool,
                queryTitle = queryTitle,
                userPrompt = prompt,
                provider = _selectedProvider.value,
                geminiApiKey = _customApiKey.value.takeIf { it.isNotBlank() },
                openAiApiKey = _openAiApiKey.value.takeIf { it.isNotBlank() },
                forceRefresh = forceRefresh,
                explicitMaxTokens = calculatedMaxTokens
            )

            _isLoading.value = false
            result.onSuccess {
                _currentResult.value = it
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Noma'lum xatolik ro'y berdi"
            }
        }
    }

    private fun buildPromptForTool(tool: TeacherToolType): Pair<String, String> {
        return when (tool) {
            TeacherToolType.KONSPEKT -> {
                val mavzu = konspektMavzu.value.ifBlank { "Ot soʻz turkumi" }
                val sinf = konspektSinf.value.ifBlank { "7-sinf" }
                val title = "$mavzu ($sinf)"
                val prompt = """Siz Oʻzbek tili metodistisiz. Quyidagi mavzu boʻyicha professional, 45 daqiqalik dars konspektini tuzing.
Mavzu: $mavzu | Sinf: $sinf

Javobni faqat ushbu shablonda chiqaring:
1. Dars maqsadi (Ta'limiy, tarbiyaviy, rivojlantiruvchi - jami 3 ta qisqa jumla).
2. Oʻtilgan darsni takrorlash (3 ta qisqa va lo'nda savol).
3. Yangi mavzu bayoni (Eng asosiy qoidalar tezis ko'rinishida).
4. Mustahkamlash (1 ta amaliy mashq va 1 ta qisqa interaktiv o'yin).
5. Uyga vazifa.
Hech qanday ortiqcha bezak va chiroyli gaplar ishlatilmasin."""
                title to prompt
            }
            TeacherToolType.TAHLIL -> {
                val matn = tahlilMatn.value.ifBlank { "Gʻurbatda gʻarib shodmon boʻlmas emish..." }
                val title = matn.lines().firstOrNull()?.take(40) ?: "Matn tahlili"
                val prompt = """Siz filologiya fanlari mutaxassisiz. Berilgan matn/g'azalni tahlil qilib, faqat ushbu shablonda chiqaring:
Matn/G'azal: "$matn"

- Mazmuni: (Maksimal 2 ta gapda matn yoki she'r mohiyati).
- Lug'at ishi: (Matndagi tushunilishi qiyin bo'lgan ko'pi bilan 3 ta arxaik/forsiy so'z va ularning qisqa izohi).
- Til xususiyatlari: (Matndagi eng asosiy gap bo'laklari, so'z turkumlari yoki badiiy san'atlar (tashbeh, istiora) ro'yxati).
Boshqa hech qanday qo'shimcha matn yozmang."""
                title to prompt
            }
            TeacherToolType.TEST -> {
                val mavzu = testMavzu.value.ifBlank { "Fe'l zamonlari (7-sinf)" }
                val title = "$mavzu (5 ta test)"
                val prompt = """Siz testologsiz. Berilgan mavzudan kelib chiqib faqat 5 ta 4 variantli test chiqaring.
Mavzu: $mavzu

Shablon qat'iy ravishda quyidagicha bo'lsin:
[Savol raqami]. [Savol matni]?
A) Variant 1
B) Variant 2
C) Variant 3
D) Variant 4

Eng oxirgi qatorda "To'g'ri javoblar kaliti:" deb faqat raqam va harfni yozing (Masalan: 1-A, 2-C, 3-B...). Boshqa matn qo'shmang."""
                title to prompt
            }
            TeacherToolType.LUGAT -> {
                val soz = lugatSoz.value.ifBlank { "Ma'rifat" }
                val title = soz
                val prompt = """Siz akademik lug'at tizimisiz. Berilgan soʻzning izohini faqat ushbu shablonda chiqaring:
Soʻz/Ibora: $soz

- Lug'aviy ma'nosi: (Maksimal 2 ta jumla).
- Adabiy misol: (Shu so'z qatnashgan bitta adabiy gap yoki parcha).
- Tarjimasi: (Ruscha: [tarjima] | Inglizcha: [tarjima]).
Boshqa hech qanday so'z qo'shmang."""
                title to prompt
            }
            TeacherToolType.DIKTANT -> {
                val matn = diktantXomMatn.value.ifBlank { "bugun maktabimizda alisher navoiy tadbiri boldi" }
                val title = "Diktant: ${matn.take(30)}..."
                val prompt = """Siz mukammal oʻzbek imlo va punktuatsiya redaktorisiz. Quyidagi matn mikrofondan ovoz orqali yozib olingan. Matnning mazmunini mutloq o'zgartirmasdan, uni faqat o'zbek imlo va punktuatsiya (tinish belgilari) qoidalariga solib, tayyor to'g'rilangan matnning o'zini qaytaring. Izoh, tushuntirish yoki "mana matn" degan gaplar mutloq yozilmasin.
Xom matn: "$matn""""
                title to prompt
            }
            TeacherToolType.MENTOR -> {
                val muammo = mentorMuammo.value.ifBlank { "O'quvchilar uyga vazifa bajarishga qiziqmayapti" }
                val title = muammo.take(40)
                val prompt = """Siz pedagogika va psixologiya professorisiz. Oʻqituvchi aytgan pedagogik muammoga amaliy yechim bering. Nazariyani tashlab, faqat 3 ta eng muhim va darsda qo'llash mumkin bo'lgan amaliy maslahatni (punktlar bilan) qisqa qilib yozing.
Muammo: $muammo"""
                title to prompt
            }
        }
    }
}
