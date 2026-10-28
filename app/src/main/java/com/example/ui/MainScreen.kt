package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.FlashcardScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.viewmodel.FlashcardViewModel
import com.example.ui.viewmodel.LibraryViewModel
import com.example.ui.viewmodel.QuizStage
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.StatsViewModel

enum class MainNavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    FLASHCARDS("Magic Cards ✨", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_flashcards"),
    QUIZ("Super Quiz 🏆", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_quiz"),
    STATS("Trophies ⭐", Icons.Filled.Stars, Icons.Outlined.Stars, "nav_stats"),
    LIBRARY("Explore 📚", Icons.Filled.Explore, Icons.Outlined.Explore, "nav_library")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    flashcardViewModel: FlashcardViewModel,
    quizViewModel: QuizViewModel,
    statsViewModel: StatsViewModel,
    libraryViewModel: LibraryViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainNavTab.FLASHCARDS) }

    val flashcardState by flashcardViewModel.uiState.collectAsStateWithLifecycle()
    val quizState by quizViewModel.uiState.collectAsStateWithLifecycle()
    val statsState by statsViewModel.uiState.collectAsStateWithLifecycle()
    val libraryState by libraryViewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(enabled = selectedTab != MainNavTab.FLASHCARDS || quizState.stage != QuizStage.SETUP) {
        if (selectedTab == MainNavTab.QUIZ && quizState.stage != QuizStage.SETUP) {
            quizViewModel.returnToSetup()
        } else {
            selectedTab = MainNavTab.FLASHCARDS
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (selectedTab) {
                            MainNavTab.FLASHCARDS -> "✨ Magic Flashcards"
                            MainNavTab.QUIZ -> "🏆 Coding Super Quiz"
                            MainNavTab.STATS -> "⭐ Trophy & Stars Room"
                            MainNavTab.LIBRARY -> "📚 Topic Quest Library"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                MainNavTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainNavTab.FLASHCARDS -> FlashcardScreen(
                    viewModel = flashcardViewModel,
                    uiState = flashcardState
                )
                MainNavTab.QUIZ -> QuizScreen(
                    viewModel = quizViewModel,
                    uiState = quizState
                )
                MainNavTab.STATS -> StatsScreen(
                    viewModel = statsViewModel,
                    uiState = statsState
                )
                MainNavTab.LIBRARY -> LibraryScreen(
                    viewModel = libraryViewModel,
                    uiState = libraryState
                )
            }
        }
    }
}
