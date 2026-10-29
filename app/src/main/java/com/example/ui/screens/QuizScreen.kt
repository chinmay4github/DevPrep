package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.StreakBadge
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
import com.example.ui.viewmodel.QuestionAnswerResult
import com.example.ui.viewmodel.QuizStage
import com.example.ui.viewmodel.QuizUiState
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    uiState: QuizUiState,
    modifier: Modifier = Modifier
) {
    if (uiState.showBadgeCelebrationDialog && uiState.newlyUnlockedBadges.isNotEmpty()) {
        KidNewBadgeCelebrationDialog(
            badges = uiState.newlyUnlockedBadges,
            onDismiss = { viewModel.dismissBadgeCelebration() }
        )
    }

    when (uiState.stage) {
        QuizStage.SETUP -> KidQuizSetupScreen(
            uiState = uiState,
            onSelectCategory = { viewModel.selectCategory(it) },
            onSelectCount = { viewModel.selectQuestionCount(it) },
            onSelectDifficulty = { viewModel.selectDifficulty(it) },
            onSelectTimePerQuestion = { viewModel.selectTimePerQuestion(it) },
            onStartQuiz = { viewModel.startQuiz() },
            onSimulateDay = { viewModel.simulateDayAdvance() },
            modifier = modifier
        )
        QuizStage.IN_PROGRESS -> KidQuizActiveScreen(
            uiState = uiState,
            onSelectOption = { viewModel.selectOption(it) },
            onNextQuestion = { viewModel.nextQuestion() },
            onToggleBookmark = { viewModel.toggleBookmark(it) },
            modifier = modifier
        )
        QuizStage.COMPLETED -> KidQuizCompletedScreen(
            uiState = uiState,
            onRestart = { viewModel.returnToSetup() },
            modifier = modifier
        )
    }
}

@Composable
fun KidQuizSetupScreen(
    uiState: QuizUiState,
    onSelectCategory: (Category?) -> Unit,
    onSelectCount: (Int) -> Unit,
    onSelectDifficulty: (Difficulty?) -> Unit,
    onSelectTimePerQuestion: (Int) -> Unit,
    onStartQuiz: () -> Unit,
    onSimulateDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Modern Assessment Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Technical Quiz Assessment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Configure topic domain, speed timer, and test senior mobile engineering proficiency.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Streak Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = com.example.ui.theme.ModernAmber.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(text = "🔥", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Daily Velocity Streak: ${uiState.userDailyStreak.currentStreak} Days",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (uiState.userDailyStreak.hasCompletedQuizToday) {
                                    "Today's assessment completed ✅"
                                } else {
                                    "Complete today's quiz to maintain consistency."
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    val nextBadge = uiState.userDailyStreak.nextBadge
                    if (nextBadge != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "${uiState.userDailyStreak.daysUntilNextBadge}d to ${nextBadge.name}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onSimulateDay,
                        modifier = Modifier.testTag("quiz_test_advance_day_button")
                    ) {
                        Text(
                            text = "⚡ Advance Day (Test 3-Day Streak)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayfulPurplePrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Timer Configuration (Countdown per Question)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⏱️ Question Timer",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (uiState.timePerQuestionSeconds > 0) "${uiState.timePerQuestionSeconds}s Countdown" else "Untimed Mode",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val timerOptions = listOf(
                15 to "⚡ 15s Speed",
                30 to "🎯 30s Standard",
                45 to "⏱️ 45s Extended",
                60 to "🧠 60s Analytical",
                0 to "♾️ Unlimited"
            )

            timerOptions.forEach { (seconds, label) ->
                FilterChip(
                    selected = uiState.timePerQuestionSeconds == seconds,
                    onClick = { onSelectTimePerQuestion(seconds) },
                    label = { Text(label, fontWeight = FontWeight.SemiBold) },
                    shape = RoundedCornerShape(14.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Category Selection
        Text(
            text = "📁 Select Domain Focus",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedCard(
                onClick = { onSelectCategory(null) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (uiState.selectedCategory == null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (uiState.selectedCategory == null) 2.dp else 1.dp,
                    color = if (uiState.selectedCategory == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🌐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "All Topics (Comprehensive Evaluation)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (uiState.selectedCategory == null) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            Category.entries.forEach { cat ->
                val isSelected = uiState.selectedCategory == cat
                OutlinedCard(
                    onClick = { onSelectCategory(cat) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = cat.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        CategoryBadge(category = cat)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Question Count
        Text(
            text = "🔢 Question Batch Size",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(5, 10, 15).forEach { count ->
                FilterChip(
                    selected = uiState.selectedQuestionCount == count,
                    onClick = { onSelectCount(count) },
                    label = { Text("$count Questions", fontWeight = FontWeight.SemiBold) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Modern Start Assessment Button
        Button(
            onClick = onStartQuiz,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("start_quiz_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "🚀", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ready, Set, Play! 🎉",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun KidQuizActiveScreen(
    uiState: QuizUiState,
    onSelectOption: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onToggleBookmark: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val question = uiState.currentQuestion ?: return
    val scrollState = rememberScrollState()

    val isCountdownEnabled = uiState.timePerQuestionSeconds > 0
    val remainingSec = uiState.remainingSecondsForQuestion

    val timerColor by animateColorAsState(
        targetValue = when {
            !isCountdownEnabled -> SkyBlueSecondary
            remainingSec <= 5 -> RoseError
            remainingSec <= 10 -> SunnyYellow
            else -> SkyBlueSecondary
        },
        label = "timerColor"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Top Bar: Question Index, Points, Timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = PlayfulPurpleContainer,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Q ${uiState.currentQuestionIndex + 1} of ${uiState.questions.size} 🎯",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PlayfulPurplePrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Countdown Pill
                Surface(
                    color = timerColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, timerColor),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(text = if (remainingSec <= 5 && isCountdownEnabled) "⏰" else "⏱️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCountdownEnabled) "${remainingSec}s" else "${uiState.timeElapsedSeconds}s",
                            color = timerColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Points Badge
                Surface(
                    color = SunnyYellowContainer,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(text = "⭐", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${uiState.score} pts",
                            color = SunnyYellow,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Progress Bars
        LinearProgressIndicator(
            progress = { (uiState.currentQuestionIndex + 1).toFloat() / uiState.questions.size },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 4.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = PlayfulPurplePrimary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        if (isCountdownEnabled && !uiState.isAnswerSubmitted) {
            val countdownAnimated by animateFloatAsState(
                targetValue = uiState.countdownFraction,
                label = "countdownProgress"
            )
            LinearProgressIndicator(
                progress = { countdownAnimated },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = timerColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Timeout Friendly Encouragement
        if (uiState.isTimedOut) {
            Surface(
                color = SunnyYellowContainer,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, SunnyYellow),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⏰", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Time Flew By! 🚀",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = SunnyYellow
                        )
                        Text(
                            text = "Don't worry, every try grows your brain! The secret answer is highlighted in green below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Question Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        CategoryBadge(category = question.category)
                        DifficultyBadge(difficulty = question.difficulty)
                    }

                    IconButton(
                        onClick = { onToggleBookmark(question.id) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (question.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (question.isBookmarked) SunnyYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = question.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )

                if (!question.codeSnippet.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CodeSnippetView(
                        code = question.codeSnippet,
                        title = "Question Code Puzzle 🧩"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (uiState.isTimedOut) "⭐ Secret Answer Revealed:" else "👉 Choose Your Answer:",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        val optionsList = if (question.options.isNotEmpty()) {
            question.options
        } else {
            listOf(
                question.answerSummary,
                "Invert the thread architecture without checking concurrency isolation",
                "Force delete database records and bypass migration steps",
                "Disable all security checks and certificate validation"
            )
        }

        val optionBadgeColors = listOf(
            PlayfulPurplePrimary to PlayfulPurpleContainer,
            SkyBlueSecondary to SkyBlueContainer,
            SunnyYellow to SunnyYellowContainer,
            BubblegumPink to BubblegumPinkContainer
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            optionsList.forEachIndexed { index, optionText ->
                val optionLetter = ('A' + index).toString()
                val isSelected = uiState.selectedOptionIndex == index
                val isCorrect = index == question.correctOptionIndex

                val (badgeColor, badgeBg) = optionBadgeColors[index % optionBadgeColors.size]

                val (bgColor, borderColor) = when {
                    !uiState.isAnswerSubmitted -> {
                        if (isSelected) {
                            Pair(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), MaterialTheme.colorScheme.primary)
                        } else {
                            Pair(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                        }
                    }
                    isCorrect -> {
                        Pair(com.example.ui.theme.ModernEmerald.copy(alpha = 0.15f), com.example.ui.theme.ModernEmerald)
                    }
                    isSelected && !isCorrect -> {
                        Pair(com.example.ui.theme.ModernRose.copy(alpha = 0.15f), com.example.ui.theme.ModernRose)
                    }
                    else -> {
                        Pair(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
                }

                OutlinedCard(
                    onClick = {
                        if (!uiState.isAnswerSubmitted) {
                            onSelectOption(index)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_option_$index"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = bgColor),
                    border = androidx.compose.foundation.BorderStroke(if (isSelected || (uiState.isAnswerSubmitted && isCorrect)) 2.dp else 1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = when {
                                uiState.isAnswerSubmitted && isCorrect -> com.example.ui.theme.ModernEmerald
                                uiState.isAnswerSubmitted && isSelected && !isCorrect -> com.example.ui.theme.ModernRose
                                isSelected -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = optionLetter,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected || (uiState.isAnswerSubmitted && (isCorrect || isSelected))) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected || (uiState.isAnswerSubmitted && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (uiState.isAnswerSubmitted) {
                            Spacer(modifier = Modifier.width(8.dp))
                            if (isCorrect) {
                                Text(text = "✅", fontSize = 16.sp)
                            } else if (isSelected) {
                                Text(text = "❌", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        // Post-Answer Friendly Explanation Card
        AnimatedVisibility(
            visible = uiState.isAnswerSubmitted,
            enter = fadeIn() + slideInVertically()
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                val isAnswerCorrect = uiState.selectedOptionIndex == question.correctOptionIndex

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAnswerCorrect) MintGreenContainer else SunnyYellowContainer
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (isAnswerCorrect) MintGreen else SunnyYellow
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = if (isAnswerCorrect) "🌟" else "🤖", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isAnswerCorrect -> {
                                        val speedBonus = if (isCountdownEnabled) remainingSec * 2 else 0
                                        if (speedBonus > 0) "Awesome Job! +100 pts (+${speedBonus} Speed Bonus! ⚡)" else "Awesome Job! +100 Brain Points! 🎉"
                                    }
                                    uiState.isTimedOut -> "Time Expired - Robby's Solution: 🤖"
                                    else -> "Good Try! Robby's Solution: 🤖"
                                },
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isAnswerCorrect) MintGreen else SunnyYellow,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = question.answerSummary,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNextQuestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("quiz_next_button"),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlayfulPurplePrimary)
                ) {
                    Text(
                        text = if (uiState.isLastQuestion) "See My Star Trophy! 🏆" else "Next Question ➔",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun KidQuizCompletedScreen(
    uiState: QuizUiState,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = SunnyYellowContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SunnyYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏆", fontSize = 44.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Woohoo! Great Job! 🎉",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = SunnyYellow,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = uiState.gradeTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${uiState.score} Points ⭐",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PlayfulPurplePrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${uiState.correctCount} of ${uiState.questions.size} Correct! (${uiState.percentage}%)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Timer",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (uiState.timePerQuestionSeconds > 0) "${uiState.timePerQuestionSeconds}s" else "No Limit",
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Speed Bonus",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+${uiState.speedBonusTotal} pts",
                            fontWeight = FontWeight.ExtraBold,
                            color = MintGreen
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Best Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "🔥 ${uiState.maxStreak}",
                            fontWeight = FontWeight.ExtraBold,
                            color = SunnyYellow
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Streak Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SunnyYellowContainer.copy(alpha = 0.55f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔥", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Quiz Streak: ${uiState.userDailyStreak.currentStreak} Days!",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = SunnyYellow
                    )
                    val nextBadge = uiState.userDailyStreak.nextBadge
                    val nextText = if (nextBadge != null) {
                        "${uiState.userDailyStreak.daysUntilNextBadge} more day(s) until you unlock ${nextBadge.name}! 🏅"
                    } else {
                        "All milestone badges unlocked! You're an incredible coding superstar! ⭐"
                    }
                    Text(
                        text = nextText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Question-by-Question Review Breakdown
        Text(
            text = "📋 Question Adventure Log",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.answerResults.forEachIndexed { idx, result ->
                val (itemBg, itemBorder, badgeEmoji, statusText) = when {
                    result.isCorrect -> Quadruple(
                        MintGreenContainer.copy(alpha = 0.5f),
                        MintGreen,
                        "🌟",
                        "Super Correct!"
                    )
                    result.isTimedOut -> Quadruple(
                        SunnyYellowContainer.copy(alpha = 0.5f),
                        SunnyYellow,
                        "⏰",
                        "Time Flew By"
                    )
                    else -> Quadruple(
                        BubblegumPinkContainer.copy(alpha = 0.5f),
                        BubblegumPink,
                        "🌱",
                        "Learning Moment"
                    )
                }

                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = itemBg),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, itemBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = badgeEmoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${idx + 1}. ${result.question.title}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelSmall,
                                color = itemBorder,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag("quiz_restart_button"),
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PlayfulPurplePrimary)
        ) {
            Text(text = "🔄", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Play Another Quiz! 🚀", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun KidNewBadgeCelebrationDialog(
    badges: List<StreakBadge>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🎉 ⭐ 🏅 ⭐ 🎉", fontSize = 26.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "New 3-Day Badge Unlocked!",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium,
                    color = SunnyYellow,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                badges.forEach { badge ->
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(SunnyYellowContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badge.emoji, fontSize = 42.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = badge.name,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = badge.kidDescription,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Surface(
                    color = MintGreenContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Robby: 'You completed quizzes 3 days in a row! Keep this flame burning tomorrow!' 🤖🔥",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MintGreen,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlayfulPurplePrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Awesome! Let's Celebrate! 🚀", fontWeight = FontWeight.ExtraBold)
            }
        }
    )
}

