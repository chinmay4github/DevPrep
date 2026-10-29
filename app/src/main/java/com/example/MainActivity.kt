package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FlashcardViewModel
import com.example.ui.viewmodel.LibraryViewModel
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.StatsViewModel
import com.example.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as DevPrepApplication).container
        val viewModelFactory = ViewModelFactory(appContainer)

        setContent {
            val systemInDark = isSystemInDarkTheme()
            var isDarkMode by rememberSaveable { mutableStateOf(systemInDark) }

            MyApplicationTheme(darkTheme = isDarkMode) {
                val flashcardViewModel: FlashcardViewModel = viewModel(factory = viewModelFactory)
                val quizViewModel: QuizViewModel = viewModel(factory = viewModelFactory)
                val statsViewModel: StatsViewModel = viewModel(factory = viewModelFactory)
                val libraryViewModel: LibraryViewModel = viewModel(factory = viewModelFactory)

                MainScreen(
                    flashcardViewModel = flashcardViewModel,
                    quizViewModel = quizViewModel,
                    statsViewModel = statsViewModel,
                    libraryViewModel = libraryViewModel,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}
