package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import com.example.ui.components.CategoryBadge
import com.example.ui.components.CodeSnippetView
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.RobbyMascotBubble
import com.example.ui.theme.BubblegumPink
import com.example.ui.theme.BubblegumPinkContainer
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenContainer
import com.example.ui.theme.PlayfulPurpleContainer
import com.example.ui.theme.PlayfulPurplePrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.SkyBlueSecondary
import com.example.ui.theme.SunnyYellow
import com.example.ui.theme.SunnyYellowContainer
import com.example.ui.viewmodel.LibraryFilter
import com.example.ui.viewmodel.LibraryUiState
import com.example.ui.viewmodel.LibraryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    uiState: LibraryUiState,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddQuestionSheet() },
                containerColor = SunnyYellow,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("add_question_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add custom question")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Card ✨", fontWeight = FontWeight.ExtraBold)
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Cheerful Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input"),
                placeholder = { Text("Search coding puzzles, topics, secrets... 🔍") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = PlayfulPurplePrimary
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(22.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Tabs (All, Bookmarked, Mastered, Needs Practice)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LibraryFilter.entries.forEach { filter ->
                    val isSelected = uiState.selectedFilter == filter
                    val label = when (filter) {
                        LibraryFilter.ALL -> "🌈 All (${uiState.questions.size})"
                        LibraryFilter.BOOKMARKED -> "⭐ Starred"
                        LibraryFilter.MASTERED -> "🏆 Mastered"
                        LibraryFilter.NEEDS_PRACTICE -> "🌱 Needs Practice"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onFilterSelected(filter) },
                        label = { Text(label, fontWeight = FontWeight.Bold) },
                        shape = RoundedCornerShape(18.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PlayfulPurplePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedCategory == null,
                    onClick = { viewModel.onCategorySelected(null) },
                    label = { Text("🎨 All Topics", fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
                Category.entries.forEach { cat ->
                    FilterChip(
                        selected = uiState.selectedCategory == cat,
                        onClick = { viewModel.onCategorySelected(cat) },
                        label = { Text(cat.shortName, fontWeight = FontWeight.Bold) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Questions List
            if (uiState.questions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No questions found!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Try searching with a different fun keyword or reset filters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.questions, key = { it.id }) { question ->
                        ExpandableQuestionItem(
                            question = question,
                            onToggleBookmark = { viewModel.toggleBookmark(question.id) },
                            onToggleMastered = { viewModel.toggleMastered(question.id) },
                            onDelete = { viewModel.deleteQuestion(question.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet to Add Custom Question
    if (uiState.isAddQuestionSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeAddQuestionSheet() },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            AddQuestionBottomSheet(
                onSave = { category, title, text, summary, deepDive, code, followUp, diff, type, options, correctIndex ->
                    viewModel.addCustomQuestion(
                        category = category,
                        title = title,
                        questionText = text,
                        answerSummary = summary,
                        deepDive = deepDive,
                        codeSnippet = code,
                        followUp = followUp,
                        difficulty = diff,
                        questionType = type,
                        options = options,
                        correctOptionIndex = correctIndex
                    )
                    scope.launch { sheetState.hide() }
                },
                onCancel = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        viewModel.closeAddQuestionSheet()
                    }
                }
            )
        }
    }
}

@Composable
fun ExpandableQuestionItem(
    question: Question,
    onToggleBookmark: () -> Unit,
    onToggleMastered: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CategoryBadge(category = question.category)
                    DifficultyBadge(difficulty = question.difficulty)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleMastered,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Mastered",
                            tint = if (question.isMastered) EmeraldSuccess else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    }

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (question.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (question.isBookmarked) SunnyYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = question.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (expanded) Int.MAX_VALUE else 2
            )

            // Expanded details
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    if (!question.codeSnippet.isNullOrBlank()) {
                        CodeSnippetView(code = question.codeSnippet, title = "Code Example 🧩")
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Surface(
                        color = MintGreenContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🎉 Solution Secret:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MintGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.answerSummary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 Deep Dive: ${question.deepDiveExplanation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (question.followUpInterviewerQuestion.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "🤔 Interviewer Riddle: ${question.followUpInterviewerQuestion}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlayfulPurplePrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    if (question.id > 20) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDelete) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = RoseError,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete Custom Card", color = RoseError, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddQuestionBottomSheet(
    onSave: (
        Category, String, String, String, String, String?, String, Difficulty, QuestionType, List<String>, Int
    ) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var questionText by remember { mutableStateOf("") }
    var answerSummary by remember { mutableStateOf("") }
    var deepDive by remember { mutableStateOf("") }
    var codeSnippet by remember { mutableStateOf("") }
    var followUp by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.ANDROID_ARCHITECTURE) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var isMcq by remember { mutableStateOf(false) }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Create Your Coding Puzzle! 🎨",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Saved in your Room database for flashcard flips and quizzes ✨",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Category.entries.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat.shortName, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Topic Title (e.g. Robot Coroutine Task)") },
            modifier = Modifier.fillMaxWidth().testTag("custom_title_input"),
            shape = RoundedCornerShape(18.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Puzzle Description / Question") },
            modifier = Modifier.fillMaxWidth().testTag("custom_question_input"),
            shape = RoundedCornerShape(18.dp),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = answerSummary,
            onValueChange = { answerSummary = it },
            label = { Text("Super Solution Summary") },
            modifier = Modifier.fillMaxWidth().testTag("custom_answer_input"),
            shape = RoundedCornerShape(18.dp),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = codeSnippet,
            onValueChange = { codeSnippet = it },
            label = { Text("Code Snippet (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "Difficulty Level", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Difficulty.entries.forEach { diff ->
                FilterChip(
                    selected = selectedDifficulty == diff,
                    onClick = { selectedDifficulty = diff },
                    label = { Text(diff.label, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (title.isNotBlank() && questionText.isNotBlank() && answerSummary.isNotBlank()) {
                        val options = if (isMcq) listOf(optionA, optionB, optionC, optionD) else emptyList()
                        onSave(
                            selectedCategory,
                            title,
                            questionText,
                            answerSummary,
                            deepDive,
                            codeSnippet,
                            followUp,
                            selectedDifficulty,
                            if (isMcq) QuestionType.MCQ else QuestionType.FLASHCARD,
                            options,
                            correctIndex
                        )
                    }
                },
                enabled = title.isNotBlank() && questionText.isNotBlank() && answerSummary.isNotBlank(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow),
                modifier = Modifier.testTag("save_custom_question_button")
            ) {
                Text("Save Card ⭐", fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
