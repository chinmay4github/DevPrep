package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.CategoryStats
import com.example.domain.model.QuizAttempt
import com.example.domain.model.StreakBadge
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
import com.example.ui.viewmodel.StatsUiState
import com.example.ui.viewmodel.StatsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    uiState: StatsUiState,
    modifier: Modifier = Modifier
) {
    var showClearConfirm by remember { mutableStateOf(false) }
    var selectedBadgeForDetail by remember { mutableStateOf<StreakBadge?>(null) }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Reset Trophy Room?") },
            text = { Text("This will clear your quiz attempts history in the Room database.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearConfirm = false
                    }
                ) {
                    Text("Clear All 🧹", color = RoseError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    selectedBadgeForDetail?.let { badge ->
        KidBadgeDetailDialog(
            badge = badge,
            onDismiss = { selectedBadgeForDetail = null }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Trophy Room & Star Tracker 🏆",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Track daily streaks & badges stored in Room DB ✨",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (uiState.recentAttempts.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearConfirm = true },
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear history",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Mascot Encouragement
        item {
            val encouragementMsg = if (uiState.streak.currentStreak >= 3) {
                "Hooray! You're on a ${uiState.streak.currentStreak}-day streak! Keep completing quizzes to earn more awesome kid-friendly badges! 🔥⭐"
            } else {
                "Complete a quiz every day! Every 3 consecutive days you'll unlock a shiny kid-friendly streak badge! 🐣⚡"
            }
            RobbyMascotBubble(
                message = encouragementMsg,
                title = "Robby's Hall of Fame 🤖"
            )
        }

        // Top 4 Stat Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KidMetricCard(
                        emoji = "👑",
                        title = "High Score",
                        value = "${uiState.stats.highestScore}",
                        containerColor = SunnyYellowContainer,
                        accentColor = SunnyYellow,
                        modifier = Modifier.weight(1f)
                    )
                    KidMetricCard(
                        emoji = "🎯",
                        title = "Avg Accuracy",
                        value = "${uiState.stats.averageScorePercentage}%",
                        containerColor = MintGreenContainer,
                        accentColor = MintGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KidMetricCard(
                        emoji = "🎮",
                        title = "Quizzes Played",
                        value = "${uiState.stats.totalQuizzesTaken}",
                        containerColor = SkyBlueContainer,
                        accentColor = SkyBlueSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    KidMetricCard(
                        emoji = "⭐",
                        title = "Cards Mastered",
                        value = "${uiState.stats.totalQuestionsMastered}/${uiState.stats.totalQuestionsCount}",
                        containerColor = PlayfulPurpleContainer,
                        accentColor = PlayfulPurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Daily Streak & 3-Day Badges Section (Requested Feature)
        item {
            KidStreakSection(
                streak = uiState.streak,
                consecutiveDays = uiState.consecutiveDays,
                completionDates = uiState.completionDates,
                kidsStreakReward = uiState.kidsStreakReward,
                onBadgeClick = { selectedBadgeForDetail = it },
                onSimulateDay = { viewModel.simulateDayAdvance() },
                onResetStreak = { viewModel.resetStreak() }
            )
        }

        // Topic Mastery Progress Bars Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🌈 Coding Topic Badges",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(text = "🎖️", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    uiState.stats.categoryStatsList.forEach { catStat ->
                        KidCategoryProgressItem(catStat = catStat)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        // Recent Quiz History Section Header
        item {
            Text(
                text = "📜 Quiz Adventure History",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }

        if (uiState.recentAttempts.isEmpty()) {
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎮", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Quiz Trophies Yet!",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Play your first MCQ Quiz to earn shiny stars and trophies here!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(uiState.recentAttempts) { attempt ->
                KidQuizAttemptHistoryItem(attempt = attempt)
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun KidStreakSection(
    streak: com.example.domain.model.UserDailyStreak,
    consecutiveDays: Int = streak.currentStreak,
    completionDates: List<String> = emptyList(),
    kidsStreakReward: com.example.domain.util.KidsStreakReward? = null,
    onBadgeClick: (StreakBadge) -> Unit,
    onSimulateDay: () -> Unit,
    onResetStreak: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = SunnyYellowContainer.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔥", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Daily Quiz Streak",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Earn a kid-friendly badge every 3 days! 🏅",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = if (streak.hasCompletedQuizToday) MintGreenContainer else SunnyYellowContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (streak.hasCompletedQuizToday) "Today Done ✅" else "Pending Today ⏰",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (streak.hasCompletedQuizToday) MintGreen else SunnyYellow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Room StreakCounter Indicator Banner
            if (completionDates.isNotEmpty() || kidsStreakReward != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SunnyYellow.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📅 Room StreakCounter: ${completionDates.size} day(s) stored",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "⚡ Consecutive: $consecutiveDays d",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = SunnyYellow
                            )
                        }
                        kidsStreakReward?.let { reward ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${reward.badgeEmoji} ${reward.cheerMessage}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Streak Metric Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🔥", fontSize = 20.sp)
                        Text(
                            text = "${streak.currentStreak} Days",
                            fontWeight = FontWeight.ExtraBold,
                            color = SunnyYellow,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Current Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🏆", fontSize = 20.sp)
                        Text(
                            text = "${streak.longestStreak} Days",
                            fontWeight = FontWeight.ExtraBold,
                            color = PlayfulPurplePrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Best Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📅", fontSize = 20.sp)
                        Text(
                            text = "${streak.totalActiveDays} Days",
                            fontWeight = FontWeight.ExtraBold,
                            color = SkyBlueSecondary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Total Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress towards next badge
            val nextBadge = streak.nextBadge
            if (nextBadge != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Next Badge: ${nextBadge.name}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${streak.daysUntilNextBadge} day(s) left",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = SunnyYellow
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { streak.progressToNextBadge },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SunnyYellow,
                            trackColor = SunnyYellowContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Day Badges Horizontal Scroll or Grid
            Text(
                text = "🎖️ 3-Day Streak Badges Collection (${streak.unlockedBadges.size}/${streak.badges.size} Unlocked):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                streak.badges.chunked(2).forEach { rowBadges ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowBadges.forEach { badge ->
                            KidStreakBadgeItem(
                                badge = badge,
                                onClick = { onBadgeClick(badge) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowBadges.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dev / Testing Simulation Buttons (Kid friendly helpers)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSimulateDay,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("test_simulate_streak_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "⚡ Advance Day (Test)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onResetStreak,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("test_reset_streak_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "🔄 Reset Streak",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoseError
                    )
                }
            }
        }
    }
}

@Composable
fun KidStreakBadgeItem(
    badge: StreakBadge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (badge.isUnlocked) SunnyYellow else Color.Transparent
    val containerBg = if (badge.isUnlocked) Color.White else Color.White.copy(alpha = 0.55f)

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (badge.isUnlocked) 2.dp else 1.dp,
                color = if (badge.isUnlocked) SunnyYellow else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        color = containerBg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) SunnyYellowContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (badge.isUnlocked) {
                    Text(text = badge.emoji, fontSize = 22.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badge.name,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (badge.isUnlocked) "⭐ Unlocked!" else "${badge.daysRequired} Days Streak",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isUnlocked) MintGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun KidBadgeDetailDialog(
    badge: StreakBadge,
    onDismiss: () -> Unit
) {
    val dateText = badge.unlockedAtTimestamp?.let {
        SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(it))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (badge.isUnlocked) SunnyYellowContainer else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badge.isUnlocked) badge.emoji else "🔒",
                        fontSize = 38.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = badge.name,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Requires ${badge.daysRequired} Consecutive Days of Quizzes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = badge.kidDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (badge.isUnlocked) {
                    Surface(
                        color = MintGreenContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🎉 Unlocked on $dateText",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MintGreen,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Surface(
                        color = SunnyYellowContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🎯 Complete a quiz daily to unlock this badge!",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SunnyYellow,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlayfulPurplePrimary)
            ) {
                Text("Got It! 🚀", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun KidMetricCard(
    emoji: String,
    title: String,
    value: String,
    containerColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun KidCategoryProgressItem(catStat: CategoryStats) {
    val (catColor, catEmoji) = when (catStat.category) {
        com.example.domain.model.Category.ANDROID_ARCHITECTURE -> MintGreen to "🤖"
        com.example.domain.model.Category.ROOM_PRODUCTION -> SkyBlueSecondary to "📦"
        com.example.domain.model.Category.REACT_NATIVE_SECURITY -> PlayfulPurplePrimary to "⚡"
        com.example.domain.model.Category.IOS_SWIFT_CONCURRENCY -> SunnyYellow to "🍎"
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = catEmoji, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = catStat.category.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${catStat.masteredQuestions}/${catStat.totalQuestions} (${catStat.masteryPercentage}%)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { if (catStat.totalQuestions > 0) catStat.masteredQuestions.toFloat() / catStat.totalQuestions else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = catColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun KidQuizAttemptHistoryItem(attempt: QuizAttempt) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(attempt.timestamp))

    val (gradeBg, gradeBorder, gradeEmoji) = when {
        attempt.percentage >= 90 -> Triple(SunnyYellowContainer, SunnyYellow, "👑")
        attempt.percentage >= 75 -> Triple(MintGreenContainer, MintGreen, "🚀")
        attempt.percentage >= 60 -> Triple(SkyBlueContainer, SkyBlueSecondary, "🌱")
        else -> Triple(BubblegumPinkContainer, BubblegumPink, "📚")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(gradeBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = gradeEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attempt.categoryDisplayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${attempt.correctAnswers}/${attempt.totalQuestions} Correct (${attempt.percentage}%) • Time: ${attempt.timeTakenSeconds}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = gradeBg,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "${attempt.score} pts",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = gradeBorder,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
