package com.example.domain.model

enum class Category(val id: String, val displayName: String, val shortName: String, val iconName: String) {
    ANDROID_ARCHITECTURE(
        id = "android_arch",
        displayName = "Android & Clean Architecture",
        shortName = "Android",
        iconName = "architecture"
    ),
    ROOM_PRODUCTION(
        id = "room_prod",
        displayName = "Room DB & Production Builds",
        shortName = "Room & APK",
        iconName = "storage"
    ),
    REACT_NATIVE_SECURITY(
        id = "rn_security",
        displayName = "React Native & Token Security",
        shortName = "React Native",
        iconName = "security"
    ),
    IOS_SWIFT_CONCURRENCY(
        id = "ios_swift",
        displayName = "iOS & Swift Concurrency",
        shortName = "iOS Swift",
        iconName = "phone_iphone"
    );

    companion object {
        fun fromId(id: String): Category = entries.firstOrNull { it.id == id } ?: ANDROID_ARCHITECTURE
    }
}

enum class Difficulty(val label: String, val level: Int) {
    EASY("Easy", 1),
    MEDIUM("Medium", 2),
    HARD("Hard", 3);

    companion object {
        fun fromString(str: String): Difficulty = entries.firstOrNull {
            it.name.equals(str, ignoreCase = true) || it.label.equals(str, ignoreCase = true)
        } ?: MEDIUM
    }
}

enum class QuestionType {
    FLASHCARD,
    MCQ
}
