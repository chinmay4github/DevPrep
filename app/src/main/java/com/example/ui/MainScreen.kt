package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.FlashcardScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.ModernAmber
import com.example.ui.theme.ModernCyan
import com.example.ui.theme.ModernIndigoPrimary
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
    FLASHCARDS("Flashcards", Icons.Filled.Style, Icons.Outlined.Style, "nav_flashcards"),
    QUIZ("Quiz Arena", Icons.Filled.Psychology, Icons.Outlined.Psychology, "nav_quiz"),
    STATS("Analytics", Icons.Filled.Insights, Icons.Outlined.Insights, "nav_stats"),
    LIBRARY("Library", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "nav_library")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    flashcardViewModel: FlashcardViewModel,
    quizViewModel: QuizViewModel,
    statsViewModel: StatsViewModel,
    libraryViewModel: LibraryViewModel,
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {},
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "DEVPREP",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = when (selectedTab) {
                                MainNavTab.FLASHCARDS -> "Interactive Flashcards"
                                MainNavTab.QUIZ -> "Quiz Assessment"
                                MainNavTab.STATS -> "Performance & Streaks"
                                MainNavTab.LIBRARY -> "Knowledge Vault"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    // Modern Dark / Light Mode Toggle Button with Moon / Sun Icon
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("theme_mode_toggle_button")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isDarkMode) {
                                MaterialTheme.colorScheme.surfaceVariant
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Crossfade(
                                    targetState = isDarkMode,
                                    animationSpec = tween(durationMillis = 300),
                                    label = "ThemeToggleAnimation"
                                ) { dark ->
                                    if (dark) {
                                        // Dark mode active -> Sun icon to switch to light mode
                                        Icon(
                                            imageVector = Icons.Filled.LightMode,
                                            contentDescription = "Switch to Light Mode",
                                            tint = ModernAmber,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        // Light mode active -> Moon icon to switch to dark mode
                                        Icon(
                                            imageVector = Icons.Filled.DarkMode,
                                            contentDescription = "Switch to Dark Mode",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
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
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
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
                .background(MaterialTheme.colorScheme.background)
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
