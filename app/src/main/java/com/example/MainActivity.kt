package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.TeacherToolType
import com.example.ui.screens.CacheLibraryScreen
import com.example.ui.screens.HistoricalJourneyScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.TeacherToolsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppNavDestination
import com.example.ui.viewmodel.TeacherViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                OzbekYordamchiApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OzbekYordamchiApp(
    viewModel: TeacherViewModel = viewModel()
) {
    val currentDestination by viewModel.currentDestination.collectAsState()
    val context = LocalContext.current
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Speech-to-Text Recognition Launcher for Diktant tool
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenText: ArrayList<String>? =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedString = spokenText?.firstOrNull()
            if (!recognizedString.isNullOrBlank()) {
                viewModel.diktantXomMatn.value = recognizedString
                Toast.makeText(context, "Ovoz yozildi! AI tahririni boshlash mumkin.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchSpeechInput() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "uz-UZ")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Oʻzbek tilida diktant matnini ayting...")
            }
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Ovoz tanish tizimi mavjud emas, matnni qo'lda kiritishingiz mumkin.", Toast.LENGTH_SHORT).show()
        }
    }

    // Back handling
    BackHandler(enabled = currentDestination != AppNavDestination.HISTORICAL_JOURNEY) {
        viewModel.setDestination(AppNavDestination.HISTORICAL_JOURNEY)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentDestination) {
                            AppNavDestination.HISTORICAL_JOURNEY -> "Oʻzbek Yordamchi"
                            AppNavDestination.AI_STUDIO -> "Oʻqituvchi AI Studiyasi"
                            AppNavDestination.CACHE_LIBRARY -> "0-Token Kesh Baza"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("button_open_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Sozlamalar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentDestination == AppNavDestination.HISTORICAL_JOURNEY,
                    onClick = { viewModel.setDestination(AppNavDestination.HISTORICAL_JOURNEY) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.HistoryEdu,
                            contentDescription = "Tarixiy Meros"
                        )
                    },
                    label = { Text("Tarixiy Yoʻl") },
                    modifier = Modifier.testTag("nav_historical_journey")
                )

                NavigationBarItem(
                    selected = currentDestination == AppNavDestination.AI_STUDIO,
                    onClick = { viewModel.setDestination(AppNavDestination.AI_STUDIO) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "AI Studiya"
                        )
                    },
                    label = { Text("AI Studiya") },
                    modifier = Modifier.testTag("nav_ai_studio")
                )

                NavigationBarItem(
                    selected = currentDestination == AppNavDestination.CACHE_LIBRARY,
                    onClick = { viewModel.setDestination(AppNavDestination.CACHE_LIBRARY) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "0-Token Kesh"
                        )
                    },
                    label = { Text("0-Token Kesh") },
                    modifier = Modifier.testTag("nav_cache_library")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppNavDestination.HISTORICAL_JOURNEY -> {
                    HistoricalJourneyScreen(
                        onNavigateToStudio = { tool ->
                            if (tool != null) {
                                viewModel.selectTool(tool)
                            }
                            viewModel.setDestination(AppNavDestination.AI_STUDIO)
                        }
                    )
                }
                AppNavDestination.AI_STUDIO -> {
                    TeacherToolsScreen(
                        viewModel = viewModel,
                        onLaunchSpeechRecognition = { launchSpeechInput() }
                    )
                }
                AppNavDestination.CACHE_LIBRARY -> {
                    CacheLibraryScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { showSettingsDialog = false }
        )
    }
}
