package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
            MyApplicationTheme {
                val flashcardViewModel: FlashcardViewModel = viewModel(factory = viewModelFactory)
                val quizViewModel: QuizViewModel = viewModel(factory = viewModelFactory)
                val statsViewModel: StatsViewModel = viewModel(factory = viewModelFactory)
                val libraryViewModel: LibraryViewModel = viewModel(factory = viewModelFactory)

                MainScreen(
                    flashcardViewModel = flashcardViewModel,
                    quizViewModel = quizViewModel,
                    statsViewModel = statsViewModel,
                    libraryViewModel = libraryViewModel
                )
            }
        }
    }
}
