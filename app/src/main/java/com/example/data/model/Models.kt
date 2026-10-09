package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPriority(val titleAr: String) {
    HIGH("عالية"),
    MEDIUM("متوسطة"),
    LOW("منخفضة")
}

enum class TaskCategory(val titleAr: String, val iconName: String) {
    WORK("عمل", "💼"),
    STUDY("دراسة", "📚"),
    PERSONAL("شخصي", "👤"),
    HEALTH("صحة", "🏃"),
    OTHER("أخرى", "✨")
}

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = TaskCategory.PERSONAL.name,
    val priority: String = TaskPriority.MEDIUM.name,
    val dueDate: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class HabitItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val iconEmoji: String = "🌱",
    val category: String = "عام",
    val streak: Int = 0,
    val lastCompletedDate: String = "",
    val completedDatesCsv: String = "", // Comma-separated YYYY-MM-DD
    val targetDaysPerWeek: Int = 7,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val colorIndex: Int = 0,
    val isPinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ExpenseCategory(val titleAr: String, val emoji: String) {
    FOOD("طعام ومقاهي", "☕"),
    SHOPPING("تسوق", "🛍️"),
    BILLS("فواتير والتزامات", "📄"),
    TRANSPORT("مواصلات", "🚗"),
    ENTERTAINMENT("ترفيه", "🎮"),
    HEALTH("صحة ورعاية", "💊"),
    OTHER("متنوعات", "📦")
}

@Entity(tableName = "expenses")
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String = ExpenseCategory.FOOD.name,
    val date: String = "", // YYYY-MM-DD
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "focus_sessions")
data class FocusSessionItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val durationMinutes: Int,
    val mode: String, // "FOCUS", "SHORT_BREAK", "LONG_BREAK"
    val timestamp: Long = System.currentTimeMillis()
)

data class DailyQuote(
    val quoteAr: String,
    val authorAr: String,
    val category: String
)

data class CountryCurrency(
    val countryCode: String,
    val countryNameAr: String,
    val flagEmoji: String,
    val currencyCode: String,
    val currencyNameAr: String,
    val symbolAr: String,
    val rateToUSD: Double = 1.0
)

data class SmartTaskPreset(
    val id: String,
    val titleAr: String,
    val descriptionAr: String,
    val iconEmoji: String,
    val category: TaskCategory,
    val tasks: List<String>
)

