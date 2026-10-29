package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CategoryEntity
import com.example.domain.model.Category
import com.example.domain.model.Question
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
import com.example.ui.viewmodel.FlashcardUiState
import com.example.ui.viewmodel.FlashcardViewModel

@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel,
    uiState: FlashcardUiState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Professional Welcome Banner
        RobbyMascotBubble(
            message = "Tap the card or flip button to toggle between the question scenario and deep-dive technical solution.",
            title = "Flashcard Study Mode 💡"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Horizontal Selector loaded from Room DB
        CategoryFilterRow(
            categories = uiState.categories,
            selectedCategoryId = uiState.selectedCategoryId,
            onCategorySelected = { viewModel.setCategory(it) },
            onlyBookmarked = uiState.onlyBookmarked,
            onToggleBookmarked = { viewModel.toggleOnlyBookmarked() }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Progress bar and Card Counter with badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Card ${uiState.currentIndex + 1} of ${uiState.totalCount}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (uiState.streak.currentStreak > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = com.example.ui.theme.ModernAmber.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🔥 ${uiState.streak.currentStreak}d",
                            style = MaterialTheme.typography.labelSmall,
                            color = com.example.ui.theme.ModernAmber,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.shuffle() },
                    modifier = Modifier.size(38.dp).testTag("shuffle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle cards",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (uiState.currentQuestion?.isMastered == true) {
                    Surface(
                        color = MintGreenContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = SunnyYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Star Mastered! ⭐",
                                color = MintGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        LinearProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = SunnyYellow,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.currentQuestion != null) {
            // Interactive 3D Magic Flip Card
            InteractiveFlipCard(
                question = uiState.currentQuestion!!,
                isFlipped = uiState.isFlipped,
                onFlip = { viewModel.flipCard() },
                onToggleBookmark = { viewModel.toggleBookmark() },
                onCorrectGuess = { viewModel.recordCorrectGuess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("flip_card_view")
            )
        } else {
            EmptyCardState(onResetFilters = {
                viewModel.setCategory(null)
                viewModel.setDifficulty(null)
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Chunky Action Buttons: Mastered vs Need Practice
        if (uiState.currentQuestion != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.markNeedsPractice() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("needs_practice_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = com.example.ui.theme.ModernRose
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(com.example.ui.theme.ModernRose.copy(alpha = 0.6f))
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Needs Practice", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                ElevatedButton(
                    onClick = { viewModel.markMastered() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("mark_mastered_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = com.example.ui.theme.ModernEmerald,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mastered", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Controls: Prev, Flip, Next
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.prevCard() },
                    enabled = uiState.currentIndex > 0,
                    modifier = Modifier.testTag("prev_card_button").height(44.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous card",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Previous", fontWeight = FontWeight.Medium)
                }

                ElevatedButton(
                    onClick = { viewModel.flipCard() },
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState.isFlipped) "Show Question" else "Flip to Answer",
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.nextCard() },
                    enabled = uiState.currentIndex < uiState.totalCount - 1,
                    modifier = Modifier.testTag("next_card_button").height(46.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Next", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next card",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun InteractiveFlipCard(
    question: Question,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCorrectGuess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing),
        label = "cardFlipAnimation"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clickable { onFlip() },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (isFlipped) MintGreen.copy(alpha = 0.4f) else PlayfulPurplePrimary.copy(alpha = 0.3f)
        )
    ) {
        if (rotation <= 90f) {
            CardFrontContent(
                question = question,
                onFlip = onFlip,
                onToggleBookmark = onToggleBookmark,
                onCorrectGuess = onCorrectGuess
            )
        } else {
            CardBackContent(
                question = question,
                onFlip = onFlip,
                onCorrectGuess = onCorrectGuess,
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            )
        }
    }
}

@Composable
fun CardFrontContent(
    question: Question,
    onFlip: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCorrectGuess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryBadge(category = question.category)
                DifficultyBadge(difficulty = question.difficulty)
            }

            IconButton(
                onClick = onToggleBookmark,
                modifier = Modifier.size(38.dp).testTag("bookmark_button")
            ) {
                Icon(
                    imageVector = if (question.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (question.isBookmarked) SunnyYellow else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Card Mastery Progress Bar
        FlashcardMasteryProgressBar(
            mastery = question.mastery,
            targetMastery = 5,
            onCorrectGuess = onCorrectGuess
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (!question.scenarioContext.isNullOrBlank()) {
            Surface(
                color = SkyBlueContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "🕵️‍♂️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Real-World Coding Mission:",
                            style = MaterialTheme.typography.labelSmall,
                            color = SkyBlueSecondary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = question.scenarioContext,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = question.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = question.questionText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            lineHeight = 22.sp
        )

        if (!question.codeSnippet.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            CodeSnippetView(
                code = question.codeSnippet,
                title = "Puzzle Code Example 🧩"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            color = SunnyYellowContainer,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onFlip() }
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = SunnyYellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tap Card for Magic Flip! ✨",
                    style = MaterialTheme.typography.labelLarge,
                    color = SunnyYellow,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun CardBackContent(
    question: Question,
    onFlip: () -> Unit,
    onCorrectGuess: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MintGreenContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "🎉", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Super Solution Secret!",
                        color = MintGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Tap to flip back 🔄",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Card Mastery Progress Bar
        FlashcardMasteryProgressBar(
            mastery = question.mastery,
            targetMastery = 5,
            onCorrectGuess = onCorrectGuess
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Easy Answer Summary:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = question.answerSummary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "💡 How & Why It Works:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = SkyBlueSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = question.deepDiveExplanation,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            lineHeight = 20.sp
        )

        if (question.followUpInterviewerQuestion.isNotBlank()) {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                color = PlayfulPurpleContainer,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(
                    1.5.dp,
                    PlayfulPurplePrimary.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🤔", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Interviewer Bonus Riddle:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = PlayfulPurplePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.followUpInterviewerQuestion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Mastery Progress Bar on the Flip Card interface.
 * Tracks and displays how many times a user has correctly guessed the card.
 */
@Composable
fun FlashcardMasteryProgressBar(
    mastery: Int,
    targetMastery: Int = 5,
    onCorrectGuess: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val progress = (mastery.toFloat() / targetMastery).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()
    val isFullyMastered = mastery >= targetMastery

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isFullyMastered) com.example.ui.theme.ModernEmerald.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("flashcard_mastery_container")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isFullyMastered) Icons.Default.CheckCircle else Icons.Default.Psychology,
                        contentDescription = "Mastery Icon",
                        tint = if (isFullyMastered) com.example.ui.theme.ModernEmerald else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isFullyMastered) "Mastered Card ⭐" else "Card Mastery Status",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isFullyMastered) com.example.ui.theme.ModernEmerald else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("flashcard_mastery_text")
                    )
                }

                Surface(
                    color = if (isFullyMastered) com.example.ui.theme.ModernEmerald.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$mastery / $targetMastery ($percentage%)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isFullyMastered) com.example.ui.theme.ModernEmerald else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("flashcard_mastery_progress_bar"),
                color = if (isFullyMastered) com.example.ui.theme.ModernEmerald else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (onCorrectGuess != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (mastery == 0) "Correctly guess to build mastery" else "$mastery correct guesses recorded",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FilledTonalButton(
                        onClick = onCorrectGuess,
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("correct_guess_button"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+1 Correct",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterRow(
    categories: List<CategoryEntity>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit,
    onlyBookmarked: Boolean,
    onToggleBookmarked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = selectedCategoryId == null && !onlyBookmarked,
            onClick = { onCategorySelected(null) },
            label = { Text("🌈 All Topics") },
            shape = RoundedCornerShape(16.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White
            )
        )

        if (categories.isNotEmpty()) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = { onCategorySelected(category.id) },
                    label = { Text(category.shortName) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        } else {
            Category.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = { onCategorySelected(category.id) },
                    label = { Text(category.shortName) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        FilterChip(
            selected = onlyBookmarked,
            onClick = onToggleBookmarked,
            label = { Text("⭐ Stars") },
            shape = RoundedCornerShape(16.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = SunnyYellow,
                selectedLabelColor = Color.White
            )
        )
    }
}

@Composable
fun EmptyCardState(onResetFilters: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🎈", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Yay! All questions explored!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap below to reset filters and play again!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedButton(
                onClick = onResetFilters,
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Reset Filters ✨", fontWeight = FontWeight.Bold)
            }
        }
    }
}
