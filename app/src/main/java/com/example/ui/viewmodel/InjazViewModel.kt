package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.InjazRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab(val titleAr: String, val iconName: String) {
    DASHBOARD("الرئيسية", "dashboard"),
    TASKS("المهام", "check_circle"),
    HABITS("العادات", "repeat"),
    FOCUS("التركيز", "timer"),
    NOTES("الملاحظات", "edit_note"),
    EXPENSES("المصاريف", "payments"),
    CURRENCIES("العملات", "public")
}

enum class FocusMode(val titleAr: String, val durationMinutes: Int) {
    FOCUS_25("تركيز عميق", 25),
    SHORT_BREAK("استراحة قصيرة", 5),
    LONG_BREAK("استراحة طويلة", 15)
}

data class DashboardStats(
    val totalTasksToday: Int = 0,
    val completedTasksToday: Int = 0,
    val tasksCompletionRate: Float = 0f,
    val activeHabitsCount: Int = 0,
    val completedHabitsToday: Int = 0,
    val totalFocusMinutesToday: Int = 0,
    val totalExpenseToday: Double = 0.0,
    val productivityScore: Int = 0,
    val productivityBadge: String = "بداية الهمّة 🌱",
    val smartAdvice: String = "ابدأ بإتمام مهمة سريعة لتحفيز طاقتك."
)

class InjazViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InjazRepository
    private val prefs = application.getSharedPreferences("injaz_prefs", Context.MODE_PRIVATE)

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Dark Mode preference
    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("pref_dark_theme", false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Currency preference
    private val _currency = MutableStateFlow(prefs.getString("pref_currency", "ر.س") ?: "ر.س")
    val currency: StateFlow<String> = _currency.asStateFlow()

    // Daily Quote
    private val _currentQuote = MutableStateFlow(DailyQuote("إِنَّ اللَّهَ يُحِبُّ إِذَا عَمِلَ أَحَدُكُمْ عَمَلاً أَنْ يُتْقِنَهُ", "حديث شريف", "إتقان"))
    val currentQuote: StateFlow<DailyQuote> = _currentQuote.asStateFlow()

    // Data streams from Repository
    val allTasks: StateFlow<List<TaskItem>>
    val allHabits: StateFlow<List<HabitItem>>
    val allNotes: StateFlow<List<NoteItem>>
    val allExpenses: StateFlow<List<ExpenseItem>>
    val focusSessions: StateFlow<List<FocusSessionItem>>

    // Task Filter & Search
    private val _taskSearchQuery = MutableStateFlow("")
    val taskSearchQuery: StateFlow<String> = _taskSearchQuery.asStateFlow()

    private val _taskFilterCategory = MutableStateFlow<String?>("الكل")
    val taskFilterCategory: StateFlow<String?> = _taskFilterCategory.asStateFlow()

    // Notes Search
    private val _noteSearchQuery = MutableStateFlow("")
    val noteSearchQuery: StateFlow<String> = _noteSearchQuery.asStateFlow()

    // Focus Timer State
    private val _focusMode = MutableStateFlow(FocusMode.FOCUS_25)
    val focusMode: StateFlow<FocusMode> = _focusMode.asStateFlow()

    private val _timerRemainingSeconds = MutableStateFlow(25 * 60)
    val timerRemainingSeconds: StateFlow<Int> = _timerRemainingSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _focusSessionsCount = MutableStateFlow(0)
    val focusSessionsCount: StateFlow<Int> = _focusSessionsCount.asStateFlow()

    private var timerJob: Job? = null
    private var toneGenerator: ToneGenerator? = null

    val smartPresets: List<SmartTaskPreset> = listOf(
        SmartTaskPreset(
            id = "morning_routine",
            titleAr = "روتين الصباح المبارك 🌅",
            descriptionAr = "٤ مهام أساسية لبدء يوم مفعم بالحيوية والبركة والنشاط",
            iconEmoji = "☀️",
            category = TaskCategory.PERSONAL,
            tasks = listOf(
                "أذكار الصباح والتأمل والحمد",
                "شرب كوب ماء كبير وفطور صحي",
                "قراءة ورد يومي من القرآن الكريم",
                "تدوين أهم ٣ أولويات لليوم في التطبيق"
            )
        ),
        SmartTaskPreset(
            id = "deep_work",
            titleAr = "جلسة العمل العميق 💼",
            descriptionAr = "تركيز مكثف بدون مشتتات لإنجاز المهام الكبرى",
            iconEmoji = "🚀",
            category = TaskCategory.WORK,
            tasks = listOf(
                "تحديد الهدف الأهم للجلسة بدقة",
                "إغلاق إشعارات الهاتف والمشتتات",
                "بدء مؤقت التركيز ٢٥ دقيقة كاملة",
                "مراجعة المخرجات وتوثيق النتيجة"
            )
        ),
        SmartTaskPreset(
            id = "health_fitness",
            titleAr = "عناية بالصحة واللياقة 🏃",
            descriptionAr = "نشاط بدني متوازن وطاقة ذهنية متجددة",
            iconEmoji = "💪",
            category = TaskCategory.HEALTH,
            tasks = listOf(
                "مشي سريع أو تمارين رياضية لمدة ٣٠ دقيقة",
                "شرب لترين من الماء على مدار اليوم",
                "وجبة متوازنة غنية بالعناصر المفيدة",
                "تمارين استرخاء وإطالة خفيفة"
            )
        )
    )

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
        } catch (_: Exception) {}

        val db = AppDatabase.getDatabase(application)
        repository = InjazRepository(db.injazDao())

        allTasks = repository.tasks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allHabits = repository.habits.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allNotes = repository.notes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allExpenses = repository.expenses.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        focusSessions = repository.focusSessions.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.checkAndSeedInitialData()
            _currentQuote.value = repository.getRandomQuote()
        }
    }

    // Dashboard Stats calculated flow with Smart Productivity Score
    val dashboardStats: StateFlow<DashboardStats> = combine(
        allTasks,
        allHabits,
        focusSessions,
        allExpenses
    ) { tasks, habits, sessions, expenses ->
        val todayStr = InjazRepository.getTodayDateString()
        val todayTasks = tasks.filter { it.dueDate.isEmpty() || it.dueDate == todayStr }
        val completedTodayTasks = todayTasks.count { it.isCompleted }
        val taskRate = if (todayTasks.isNotEmpty()) completedTodayTasks.toFloat() / todayTasks.size else 0f

        val completedHabitsToday = habits.count { it.completedDatesCsv.contains(todayStr) }

        val todaySessions = sessions.filter {
            val sessionDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(it.timestamp))
            sessionDate == todayStr && it.mode == "FOCUS"
        }
        val focusMins = todaySessions.sumOf { it.durationMinutes }

        val todayExpenses = expenses.filter { it.date == todayStr }.sumOf { it.amount }

        // Smart Productivity Engine (0 - 100)
        val taskPoints = (taskRate * 50).toInt()
        val habitPoints = if (habits.isNotEmpty()) ((completedHabitsToday.toFloat() / habits.size) * 35).toInt() else 15
        val focusPoints = (focusMins.coerceAtMost(60).toFloat() / 60f * 15).toInt()
        val totalScore = (taskPoints + habitPoints + focusPoints).coerceIn(0, 100)

        val badge = when {
            totalScore >= 85 -> "خبير الإنجاز 👑"
            totalScore >= 65 -> "متألق اليوم 🌟"
            totalScore >= 40 -> "في المسار الصحيح 🚀"
            else -> "بداية الهمّة 🌱"
        }

        val advice = when {
            totalScore >= 85 -> "أداء استثنائي! استمر بهذا الحماس والتركيز العالي."
            totalScore >= 65 -> "إنجاز رائع وملموس، بقي القليل لتكتمل دائرة إنجاز اليوم."
            totalScore >= 40 -> "خطوات جيدة؛ جلسة تركيز أو إنهاء مهمة عاجلة سيرفع نتيجتك فوراً."
            else -> "ابدأ بإتمام عادة واحدة أو مهمة سريعة لكسر حاجز البداية."
        }

        DashboardStats(
            totalTasksToday = todayTasks.size,
            completedTasksToday = completedTodayTasks,
            tasksCompletionRate = taskRate,
            activeHabitsCount = habits.size,
            completedHabitsToday = completedHabitsToday,
            totalFocusMinutesToday = focusMins,
            totalExpenseToday = todayExpenses,
            productivityScore = totalScore,
            productivityBadge = badge,
            smartAdvice = advice
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    // Filtered Tasks
    val filteredTasks: StateFlow<List<TaskItem>> = combine(
        allTasks,
        _taskSearchQuery,
        _taskFilterCategory
    ) { tasks, query, filter ->
        tasks.filter { task ->
            val matchesQuery = query.isBlank() || task.title.contains(query, ignoreCase = true) || task.description.contains(query, ignoreCase = true)
            val matchesCategory = when (filter) {
                "الكل", null -> true
                "مكتملة" -> task.isCompleted
                "قيد الإنجاز" -> !task.isCompleted
                "اليوم" -> task.dueDate == InjazRepository.getTodayDateString()
                else -> task.category == filter
            }
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Notes
    val filteredNotes: StateFlow<List<NoteItem>> = combine(
        allNotes,
        _noteSearchQuery
    ) { notes, query ->
        if (query.isBlank()) {
            notes
        } else {
            notes.filter {
                it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation
    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun toggleDarkTheme() {
        val newVal = !_isDarkTheme.value
        _isDarkTheme.value = newVal
        prefs.edit().putBoolean("pref_dark_theme", newVal).apply()
    }

    fun setCurrency(newCurrency: String) {
        _currency.value = newCurrency
        prefs.edit().putString("pref_currency", newCurrency).apply()
    }

    fun refreshQuote() {
        _currentQuote.value = repository.getRandomQuote()
    }

    // Task Actions
    fun setTaskSearchQuery(query: String) {
        _taskSearchQuery.value = query
    }

    fun setTaskFilterCategory(filter: String?) {
        _taskFilterCategory.value = filter
    }

    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task)
            if (!task.isCompleted) {
                playSuccessTone()
            }
            vibrateDevice(50)
        }
    }

    fun addTask(title: String, description: String, category: String, priority: String, dueDate: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(
                TaskItem(
                    title = title.trim(),
                    description = description.trim(),
                    category = category,
                    priority = priority,
                    dueDate = dueDate.ifBlank { InjazRepository.getTodayDateString() },
                    isCompleted = false
                )
            )
            playSuccessTone()
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Habit Actions
    fun toggleHabitToday(habit: HabitItem) {
        viewModelScope.launch {
            repository.toggleHabitCompletedToday(habit)
            playSuccessTone()
            vibrateDevice(60)
        }
    }

    fun addHabit(title: String, iconEmoji: String, category: String, targetDays: Int) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addHabit(
                HabitItem(
                    title = title.trim(),
                    iconEmoji = iconEmoji.ifBlank { "🌱" },
                    category = category,
                    streak = 0,
                    targetDaysPerWeek = targetDays
                )
            )
        }
    }

    fun deleteHabit(habit: HabitItem) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // Note Actions
    fun setNoteSearchQuery(query: String) {
        _noteSearchQuery.value = query
    }

    fun addNote(title: String, content: String, colorIndex: Int) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            repository.addNote(
                NoteItem(
                    title = title.trim(),
                    content = content.trim(),
                    colorIndex = colorIndex,
                    isPinned = false,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateNote(note: NoteItem) {
        viewModelScope.launch {
            repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun toggleNotePinned(note: NoteItem) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
            vibrateDevice(30)
        }
    }

    fun deleteNote(note: NoteItem) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Expense Actions
    fun addExpense(title: String, amount: Double, category: String, date: String) {
        if (title.isBlank() || amount <= 0) return
        viewModelScope.launch {
            repository.addExpense(
                ExpenseItem(
                    title = title.trim(),
                    amount = amount,
                    category = category,
                    date = date.ifBlank { InjazRepository.getTodayDateString() }
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseItem) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // Focus Timer Operations
    fun setFocusMode(mode: FocusMode) {
        pauseTimer()
        _focusMode.value = mode
        _timerRemainingSeconds.value = mode.durationMinutes * 60
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        vibrateDevice(40)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value && _timerRemainingSeconds.value > 0) {
                delay(1000L)
                _timerRemainingSeconds.value -= 1
            }
            if (_timerRemainingSeconds.value <= 0) {
                onTimerFinished()
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        pauseTimer()
        _timerRemainingSeconds.value = _focusMode.value.durationMinutes * 60
    }

    private fun onTimerFinished() {
        pauseTimer()
        vibrateDevice(300)
        val mode = _focusMode.value
        if (mode == FocusMode.FOCUS_25) {
            _focusSessionsCount.value += 1
            viewModelScope.launch {
                repository.addFocusSession(
                    FocusSessionItem(
                        durationMinutes = mode.durationMinutes,
                        mode = "FOCUS"
                    )
                )
            }
            // Automatically switch to short break
            setFocusMode(FocusMode.SHORT_BREAK)
        } else {
            // Break finished, prepare focus mode
            setFocusMode(FocusMode.FOCUS_25)
        }
        playCelebrationTone()
    }

    fun applySmartPreset(preset: SmartTaskPreset) {
        viewModelScope.launch {
            val todayStr = InjazRepository.getTodayDateString()
            preset.tasks.forEach { taskTitle ->
                repository.addTask(
                    TaskItem(
                        title = taskTitle,
                        description = "ضمن حزمة: ${preset.titleAr}",
                        category = preset.category.name,
                        priority = TaskPriority.HIGH.name,
                        dueDate = todayStr,
                        isCompleted = false
                    )
                )
            }
            playSuccessTone()
            vibrateDevice(90)
        }
    }

    fun playSuccessTone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        } catch (_: Exception) {}
    }

    fun playCelebrationTone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300)
        } catch (_: Exception) {}
    }

    private fun vibrateDevice(durationMs: Long) {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {
            // Ignore if vibration service is unavailable
        }
    }
}
