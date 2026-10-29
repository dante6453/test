package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.model.StudyMode
import com.example.ui.screens.BlueprintScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.CharactersScreen
import com.example.ui.screens.ChatSessionScreen
import com.example.ui.screens.CreateCharacterDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.PersonaStudyTheme
import com.example.ui.theme.VioletPrimary
import com.example.ui.viewmodel.PersonaStudyViewModel

enum class AppDestination(val label: String, val icon: ImageVector) {
    HOME("Hub", Icons.Default.AutoAwesome),
    CHARACTERS("Companions", Icons.Default.People),
    CHAT("Study Chat", Icons.Default.Forum),
    CALCULATOR("Smart Calc", Icons.Default.Calculate),
    BLUEPRINT("Blueprint", Icons.Default.Code)
}

class MainActivity : ComponentActivity() {

    private val viewModel: PersonaStudyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonaStudyTheme {
                PersonaStudyApp(viewModel)
            }
        }
    }
}

@Composable
fun PersonaStudyApp(viewModel: PersonaStudyViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    var showCreateDialog by remember { mutableStateOf(false) }

    // Handle Android system back button
    BackHandler(enabled = currentDestination != AppDestination.HOME) {
        currentDestination = AppDestination.HOME
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                AppDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(destination.icon, contentDescription = destination.label)
                        },
                        label = {
                            Text(destination.label, style = MaterialTheme.typography.labelSmall)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VioletPrimary,
                            selectedTextColor = VioletPrimary,
                            indicatorColor = VioletPrimary.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_${destination.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentDestination,
            modifier = Modifier.padding(innerPadding),
            label = "screen_transition"
        ) { destination ->
            when (destination) {
                AppDestination.HOME -> HomeScreen(
                    uiState = uiState,
                    onNavigateToChat = { mode ->
                        if (mode != null) viewModel.switchMode(mode)
                        currentDestination = AppDestination.CHAT
                    },
                    onNavigateToCharacters = { currentDestination = AppDestination.CHARACTERS },
                    onNavigateToCalculator = { currentDestination = AppDestination.CALCULATOR },
                    onNavigateToBlueprint = { currentDestination = AppDestination.BLUEPRINT },
                    onSelectSubject = { subject -> viewModel.selectSubject(subject) },
                    onQuickTopicClick = { topic ->
                        viewModel.sendMessage("Can you explain $topic step-by-step?")
                        currentDestination = AppDestination.CHAT
                    }
                )

                AppDestination.CHARACTERS -> CharactersScreen(
                    characters = uiState.characters,
                    selectedCharacter = uiState.selectedCharacter,
                    onSelectCharacter = { char ->
                        viewModel.selectCharacter(char)
                    },
                    onCreateCharacterClick = { showCreateDialog = true }
                )

                AppDestination.CHAT -> ChatSessionScreen(
                    uiState = uiState,
                    onSendMessage = { text -> viewModel.sendMessage(text) },
                    onSwitchMode = { mode -> viewModel.switchMode(mode) },
                    onQuizAnswer = { msgId, optIdx -> viewModel.handleQuizAnswer(msgId, optIdx) }
                )

                AppDestination.CALCULATOR -> CalculatorScreen(
                    uiState = uiState,
                    onSelectFormula = { formula -> viewModel.selectFormula(formula) },
                    onUpdateVariable = { sym, valNum -> viewModel.updateFormulaInput(sym, valNum) },
                    onDiscussInChat = { question ->
                        viewModel.sendMessage(question)
                        currentDestination = AppDestination.CHAT
                    }
                )

                AppDestination.BLUEPRINT -> BlueprintScreen()
            }
        }
    }

    if (showCreateDialog) {
        CreateCharacterDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { name, franchise, arch, catchphrase, tutorTone, burnoutComfort, strict, emp, intel, energy, emoji ->
                viewModel.createCustomCharacter(
                    name = name,
                    franchise = franchise,
                    archetype = arch,
                    catchphrase = catchphrase,
                    tutorTone = tutorTone,
                    burnoutComfort = burnoutComfort,
                    strictness = strict,
                    empathy = emp,
                    intellect = intel,
                    energy = energy,
                    emoji = emoji
                )
                showCreateDialog = false
                currentDestination = AppDestination.CHAT
            }
        )
    }
}
