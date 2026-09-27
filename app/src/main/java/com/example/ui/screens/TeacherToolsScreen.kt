package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeacherToolType
import com.example.data.repository.GenerationResult
import com.example.ui.theme.CacheGreenBadge
import com.example.ui.theme.CacheGreenBg
import com.example.ui.theme.LiveAIBadge
import com.example.ui.theme.LiveAIBg
import com.example.ui.viewmodel.TeacherViewModel

@Composable
fun TeacherToolsScreen(
    viewModel: TeacherViewModel,
    onLaunchSpeechRecognition: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTool by viewModel.selectedTool.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentResult by viewModel.currentResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val lastPromptSent by viewModel.lastPromptSent.collectAsState()
    val totalTokensSaved by viewModel.totalTokensSaved.collectAsState()
    val activeProvider by viewModel.selectedProvider.collectAsState()

    val context = LocalContext.current
    var showPromptInspection by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("teacher_tools_screen")
    ) {
        // Global AI Configuration & Token Saver Banner
        GlobalConfigHeader(
            totalTokensSaved = totalTokensSaved,
            activeProvider = activeProvider
        )

        // Tool Selection Tabs
        ToolSelectionTabs(
            selectedTool = selectedTool,
            onSelectTool = { viewModel.selectTool(it) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
        ) {
            // Tool Header & Token constraint info
            item {
                ToolHeaderCard(selectedTool = selectedTool)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tool-Specific Input Fields
            item {
                when (selectedTool) {
                    TeacherToolType.KONSPEKT -> KonspektInputs(viewModel)
                    TeacherToolType.TAHLIL -> TahlilInputs(viewModel)
                    TeacherToolType.TEST -> TestInputs(viewModel)
                    TeacherToolType.LUGAT -> LugatInputs(viewModel)
                    TeacherToolType.DIKTANT -> DiktantInputs(viewModel, onLaunchSpeechRecognition)
                    TeacherToolType.MENTOR -> MentorInputs(viewModel)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Action Buttons: Generate & Force Refresh
            item {
                ActionButtonsRow(
                    isLoading = isLoading,
                    onExecute = { viewModel.executeTool(forceRefresh = false) },
                    onForceRefresh = { viewModel.executeTool(forceRefresh = true) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Error display
            if (errorMessage != null) {
                item {
                    ErrorMessageCard(
                        message = errorMessage!!,
                        onDismiss = { viewModel.clearError() }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Loading Indicator
            if (isLoading) {
                item {
                    LoadingCard(selectedTool = selectedTool)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Result Display Card
            if (currentResult != null && !isLoading) {
                item {
                    ResultDisplayCard(
                        result = currentResult!!,
                        toolType = selectedTool,
                        onCopy = {
                            copyToClipboard(context, currentResult!!.content)
                            Toast.makeText(context, "Nusxa olindi!", Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            shareText(context, currentResult!!.content, selectedTool.title)
                        },
                        onTogglePrompt = { showPromptInspection = !showPromptInspection }
                    )
                }

                if (showPromptInspection && lastPromptSent != null) {
                    item {
                        PromptInspectionCard(prompt = lastPromptSent!!)
                    }
                }
            }
        }
    }
}

@Composable
private fun GlobalConfigHeader(
    totalTokensSaved: Int,
    activeProvider: com.example.data.model.AiProvider
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = CacheGreenBg,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CacheGreenBadge)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = CacheGreenBadge,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "0-Token Kesh",
                            color = CacheGreenBadge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tejaldi: ~$totalTokensSaved tok",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = activeProvider.shortName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolSelectionTabs(
    selectedTool: TeacherToolType,
    onSelectTool: (TeacherToolType) -> Unit
) {
    val tools = TeacherToolType.entries
    val selectedIndex = tools.indexOf(selectedTool).coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 12.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        tools.forEachIndexed { index, tool ->
            val isSelected = selectedIndex == index
            Tab(
                selected = isSelected,
                onClick = { onSelectTool(tool) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${tool.buttonNumber}: ${tool.title.take(14)}",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun ToolHeaderCard(selectedTool: TeacherToolType) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = selectedTool.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedTool.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = selectedTool.shortDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (selectedTool == TeacherToolType.DIKTANT) "Max: Matn hajmida" else "Max: ${selectedTool.defaultMaxTokens} tok",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ----------------- Inputs for Each Tool -----------------

@Composable
private fun KonspektInputs(viewModel: TeacherViewModel) {
    val mavzu by viewModel.konspektMavzu.collectAsState()
    val sinf by viewModel.konspektSinf.collectAsState()

    val sinflar = listOf("5-sinf", "6-sinf", "7-sinf", "8-sinf", "9-sinf", "10-sinf", "11-sinf")
    val topicSuggestions = listOf(
        "Ot soʻz turkumi",
        "Ega va kesim munosabati",
        "Alisher Navoiy hayoti va ijodi",
        "Erkin Vohidov 'Oʻzbegim' qasidasi",
        "Undalma va kiritmalar"
    )

    Column {
        OutlinedTextField(
            value = mavzu,
            onValueChange = { viewModel.konspektMavzu.value = it },
            label = { Text("Mavzu nomi") },
            placeholder = { Text("Masalan: Ot soʻz turkumi") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_konspekt_mavzu"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Sinfni tanlang:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(sinflar) { s ->
                FilterChip(
                    selected = sinf == s,
                    onClick = { viewModel.konspektSinf.value = s },
                    label = { Text(s, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tezkor namunaviy mavzular:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(topicSuggestions) { t ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { viewModel.konspektMavzu.value = t }
                ) {
                    Text(
                        text = t,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TahlilInputs(viewModel: TeacherViewModel) {
    val matn by viewModel.tahlilMatn.collectAsState()

    val poemSamples = listOf(
        "Alisher Navoiy (Gʻurbatda...)" to "Gʻurbatda gʻarib shodmon boʻlmas emish,\nEl anga necha mehribon boʻlmas emish.\nOltun qafas ichra gar qizil gul butsa,\nBulbulgʻa tikandek oshyon boʻlmas emish.",
        "Choʻlpon (Goʻzal)" to "Qorongʻu kechada koʻkka koʻz tikib,\nEng yorugʻ yulduzdan seni soʻrayman.\nUl yulduz uyalib, boshini bukib,\nAytadir: men uni tushda koʻraman.",
        "Bobur (Tole' yoʻqi...)" to "Tole' yoʻqi jonimga baloligʻ boʻldi,\nHar ishniki ayladim – xatoligʻ boʻldi.\nOʻz yerni qoʻyib, Hind sori yuzlandim,\nYorab, netayin – ne yuz qaroligʻ boʻldi."
    )

    Column {
        OutlinedTextField(
            value = matn,
            onValueChange = { viewModel.tahlilMatn.value = it },
            label = { Text("Matn yoki She'r matnini kiriting") },
            placeholder = { Text("Tahlil qilinadigan she'riy parchani yoki nasriy matnni shu yerga yozing...") },
            minLines = 4,
            maxLines = 8,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_tahlil_matn"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Klassik she'rlardan namuna tanlash:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(poemSamples) { (title, content) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.clickable { viewModel.tahlilMatn.value = content }
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TestInputs(viewModel: TeacherViewModel) {
    val mavzu by viewModel.testMavzu.collectAsState()

    val testTopics = listOf(
        "Fe'l zamonlari (7-sinf)",
        "Fonetik hodisalar (5-sinf)",
        "Kishilik olmoshlari (6-sinf)",
        "Bog'langan qo'shma gaplar (8-sinf)",
        "Alisher Navoiy 'Xamsa'si (9-sinf)"
    )

    Column {
        OutlinedTextField(
            value = mavzu,
            onValueChange = { viewModel.testMavzu.value = it },
            label = { Text("Test mavzusi") },
            placeholder = { Text("Masalan: Fe'l zamonlari yoki 6-sinf Olmoshlar") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_test_mavzu"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Mavzu shablonlari:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(testTopics) { t ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { viewModel.testMavzu.value = t }
                ) {
                    Text(
                        text = t,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LugatInputs(viewModel: TeacherViewModel) {
    val soz by viewModel.lugatSoz.collectAsState()

    val rareWords = listOf(
        "Ma'rifat", "Navobaxsh", "Istiora", "Tazod", "Durdona", "Qasida", "Sanoati laffu nashr", "Himmattalab"
    )

    Column {
        OutlinedTextField(
            value = soz,
            onValueChange = { viewModel.lugatSoz.value = it },
            label = { Text("So'z yoki ibora") },
            placeholder = { Text("Masalan: Ma'rifat, Navobaxsh, Istiora...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_lugat_soz"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Adabiy va qiyin so'zlar:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(rareWords) { w ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { viewModel.lugatSoz.value = w }
                ) {
                    Text(
                        text = w,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DiktantInputs(
    viewModel: TeacherViewModel,
    onLaunchSpeechRecognition: () -> Unit
) {
    val xomMatn by viewModel.diktantXomMatn.collectAsState()

    val rawSamples = listOf(
        "Alisher Navoiy tadbiri" to "bugun maktabimizda alisher navoiy tavaludiga bagishlangan ajoyib tadbir bolib otdi u yerda oquvchilar gazallar aytdilar",
        "Imlo xatoli jumla" to "ozbekiston kelajagi buyuk davlat chunki yoshlar talim olyapti va kitop oqimoqda"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Xom matn yoki Ovozdan kelgan matn:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Button(
                onClick = onLaunchSpeechRecognition,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Ovoz bilan aytish",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ovoz bilan aytish", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = xomMatn,
            onValueChange = { viewModel.diktantXomMatn.value = it },
            placeholder = { Text("Mikrofon tugmasini bosing yoki tahrirlash kerak bo'lgan xom matnni shu yerga yozing...") },
            minLines = 4,
            maxLines = 8,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_diktant_matn"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Xom matn namunalari:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(rawSamples) { (title, sample) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { viewModel.diktantXomMatn.value = sample }
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MentorInputs(viewModel: TeacherViewModel) {
    val muammo by viewModel.mentorMuammo.collectAsState()

    val problems = listOf(
        "O'quvchilar uyga vazifa bajarishga qiziqmayapti",
        "Darsda orfografik savodxonlikni oshirish usuli",
        "5-sinfda darsda e'tiborni jamlash qiyin kechmoqda",
        "She'riyat va g'azallarni yodlashga qiziqish uyg'otish"
    )

    Column {
        OutlinedTextField(
            value = muammo,
            onValueChange = { viewModel.mentorMuammo.value = it },
            label = { Text("Pedagogik yoki metodik muammo") },
            placeholder = { Text("O'quvchilar bilan yuzaga kelgan qiyinchilikni qisqacha ifodalang...") },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_mentor_muammo"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tez-tez uchraydigan pedagogik holatlar:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(problems) { p ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { viewModel.mentorMuammo.value = p }
                ) {
                    Text(
                        text = p,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ----------------- Execution & Result Components -----------------

@Composable
private fun ActionButtonsRow(
    isLoading: Boolean,
    onExecute: () -> Unit,
    onForceRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onExecute,
            enabled = !isLoading,
            modifier = Modifier
                .weight(1f)
                .testTag("button_generate_query"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Soʻrov Yuborish (Avto Kesh)",
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = onForceRefresh,
            enabled = !isLoading,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("button_force_refresh")
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Qayta yaratish",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun LoadingCard(selectedTool: TeacherToolType) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Kesh tekshirilmoqda yoki AI javobi tayyorlanmoqda...",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Harorat: 0.1 | Max token: ${if (selectedTool == TeacherToolType.DIKTANT) "Matn hajmida" else selectedTool.defaultMaxTokens.toString()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ResultDisplayCard(
    result: GenerationResult,
    toolType: TeacherToolType,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onTogglePrompt: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("result_display_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status Header: 0-Token Cache vs Live AI
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (result.isFromCache) {
                    Surface(
                        color = CacheGreenBg,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CacheGreenBadge)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CacheGreenBadge,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "0-TOKEN KESH (Bazadan olindi)",
                                color = CacheGreenBadge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Surface(
                        color = LiveAIBg,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LiveAIBadge)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = LiveAIBadge,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REAL-VAQT AI (${result.providerUsed})",
                                color = LiveAIBadge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onCopy) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Nusxa olish",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Ulashish",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            // Body content
            Text(
                text = result.content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            // Action row & Prompt Inspection
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (result.isFromCache) "Tejalgan token: ~${result.tokensSaved}" else "Hajm: ~${result.tokensSaved} token",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(
                    onClick = onTogglePrompt,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Shablonni ko'rish", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun PromptInspectionCard(prompt: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Modelga ketgan qat'iy prompt:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = prompt,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorMessageCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Xatolik ro'y berdi",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("OK", fontSize = 12.sp)
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("O'zbek Yordamchi", text)
    clipboard.setPrimaryClip(clip)
}

private fun shareText(context: Context, text: String, title: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_TITLE, title)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Ulashish"))
}
