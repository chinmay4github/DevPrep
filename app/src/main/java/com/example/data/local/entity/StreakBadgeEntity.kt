package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.StreakBadge

@Entity(tableName = "streak_badges")
data class StreakBadgeEntity(
    @PrimaryKey
    val id: String,
    val daysRequired: Int,
    val name: String,
    val kidDescription: String,
    val emoji: String,
    val colorHex: String,
    val isUnlocked: Boolean = false,
    val unlockedAtTimestamp: Long? = null
) {
    fun toDomain(): StreakBadge {
        return StreakBadge(
            id = id,
            daysRequired = daysRequired,
            name = name,
            kidDescription = kidDescription,
            emoji = emoji,
            colorHex = colorHex,
            isUnlocked = isUnlocked,
            unlockedAtTimestamp = unlockedAtTimestamp
        )
    }

    companion object {
        fun fromDomain(badge: StreakBadge): StreakBadgeEntity {
            return StreakBadgeEntity(
                id = badge.id,
                daysRequired = badge.daysRequired,
                name = badge.name,
                kidDescription = badge.kidDescription,
                emoji = badge.emoji,
                colorHex = badge.colorHex,
                isUnlocked = badge.isUnlocked,
                unlockedAtTimestamp = badge.unlockedAtTimestamp
            )
        }

        fun getDefaultBadges(): List<StreakBadgeEntity> {
            return listOf(
                StreakBadgeEntity(
                    id = "streak_badge_3",
                    daysRequired = 3,
                    name = "Spark Bot ⚡",
                    kidDescription = "Completed quizzes 3 days in a row! You sparked your coding energy!",
                    emoji = "⚡",
                    colorHex = "#F59E0B"
                ),
                StreakBadgeEntity(
                    id = "streak_badge_6",
                    daysRequired = 6,
                    name = "Rocket Cadet 🚀",
                    kidDescription = "6 days streak! Zooming through coding adventures like a supersonic rocket!",
                    emoji = "🚀",
                    colorHex = "#0284C7"
                ),
                StreakBadgeEntity(
                    id = "streak_badge_9",
                    daysRequired = 9,
                    name = "Brain Dynamo 🧠",
                    kidDescription = "9 days streak! Your brain cells are glowing with super-powered logic!",
                    emoji = "🧠",
                    colorHex = "#7C3AED"
                ),
                StreakBadgeEntity(
                    id = "streak_badge_12",
                    daysRequired = 12,
                    name = "Pixel Paladin 🛡️",
                    kidDescription = "12 days streak! A brave defender of bug-free clean architecture!",
                    emoji = "🛡️",
                    colorHex = "#059669"
                ),
                StreakBadgeEntity(
                    id = "streak_badge_15",
                    daysRequired = 15,
                    name = "Star Wizard ⭐",
                    kidDescription = "15 days streak! Casting magical spells of Kotlin and Jetpack Compose!",
                    emoji = "⭐",
                    colorHex = "#EA580C"
                ),
                StreakBadgeEntity(
                    id = "streak_badge_18",
                    daysRequired = 18,
                    name = "Cosmic Explorer 🛸",
                    kidDescription = "18 days streak! Navigating through the galaxies of mobile engineering!",
                    emoji = "🛸",
                    colorHex = "#E11D48"
                ),
                StreakBadgeEntity(
                    id = "streak_badge_21",
                    daysRequired = 21,
                    name = "Crown Champion 👑",
                    kidDescription = "21 days streak! Three whole weeks of daily quizzes! Pure royalty!",
                    emoji = "👑",
                    colorHex = "#F59E0B"
                ),
                StreakBadgeEntity(
                    id = "streak_badge_24",
                    daysRequired = 24,
                    name = "Cyber Dragon 🐉",
                    kidDescription = "24 days streak! Roaring with unstoppable knowledge and confidence!",
                    emoji = "🐉",
                    colorHex = "#059669"
                ),
                StreakBadgeEntity(
                    id = "streak_badge_27",
                    daysRequired = 27,
                    name = "Nebula Knight 🌌",
                    kidDescription = "27 days streak! Shifting through the cosmos like a senior tech leader!",
                    emoji = "🌌",
                    colorHex = "#8B5CF6"
                ),
                StreakBadgeEntity(
                    id = "streak_badge_30",
                    daysRequired = 30,
                    name = "Master Robo Star 🤖",
                    kidDescription = "30 days streak! A full month of daily learning! Robby salutes your brilliance!",
                    emoji = "🤖",
                    colorHex = "#0284C7"
                )
            )
        }
    }
}
