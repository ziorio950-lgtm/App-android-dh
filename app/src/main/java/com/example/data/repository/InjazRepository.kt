package com.example.data.repository

import com.example.data.local.InjazDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class InjazRepository(private val dao: InjazDao) {

    val tasks: Flow<List<TaskItem>> = dao.getAllTasks()
    val habits: Flow<List<HabitItem>> = dao.getAllHabits()
    val notes: Flow<List<NoteItem>> = dao.getAllNotes()
    val expenses: Flow<List<ExpenseItem>> = dao.getAllExpenses()
    val focusSessions: Flow<List<FocusSessionItem>> = dao.getAllFocusSessions()

    val quotesList: List<DailyQuote> = listOf(
        DailyQuote("إِنَّ اللَّهَ يُحِبُّ إِذَا عَمِلَ أَحَدُكُمْ عَمَلاً أَنْ يُتْقِنَهُ", "حديث شريف", "إتقان"),
        DailyQuote("البدايات التي لا تُخلص فيها، قلّما تثمر نهايات ترضيك.", "ابن عطاء الله السكندري", "حكمة"),
        DailyQuote("قليلٌ دائم خيرٌ من كثيرٍ منقطع.", "حكمة عربية", "استمرار"),
        DailyQuote("النجاح هو مجموع قرارات صغيرة تُتخذ كل يوم بوعي وعزيمة.", "تطوير الذات", "تحفيز"),
        DailyQuote("بورك لأمتي في بكورها.", "حديث شريف", "بركة"),
        DailyQuote("إذا لم تزد شيئاً على الدنيا، كنتَ زائداً عليها.", "مصطفى صادق الرافعي", "أثر"),
        DailyQuote("الوقت كالسيف، إن لم تقطعه قطعك.", "مثل عربي", "وقت"),
        DailyQuote("لا تؤجل عمل اليوم إلى الغد، فلكل يوم عمله.", "حكمة حكيم", "إنجاز")
    )

    fun getRandomQuote(): DailyQuote {
        return quotesList.random()
    }

    suspend fun checkAndSeedInitialData() {
        val existingTasks = dao.getAllTasks().firstOrNull() ?: emptyList()
        val todayStr = getTodayDateString()

        if (existingTasks.isEmpty()) {
            // Seed Tasks
            dao.insertTask(
                TaskItem(
                    title = "مراجعة خطة الأسبوع وتحديد الأولويات",
                    description = "تحديد أهم ٣ أهداف للأسبوع مع المواعيد المحددة",
                    category = TaskCategory.WORK.name,
                    priority = TaskPriority.HIGH.name,
                    dueDate = todayStr,
                    isCompleted = false
                )
            )
            dao.insertTask(
                TaskItem(
                    title = "قراءة ٢٠ صفحة من كتاب نافع",
                    description = "التركيز دون مشتتات هاتفية",
                    category = TaskCategory.STUDY.name,
                    priority = TaskPriority.MEDIUM.name,
                    dueDate = todayStr,
                    isCompleted = false
                )
            )
            dao.insertTask(
                TaskItem(
                    title = "ممارسة المشي السريع لمدة ٣٠ دقيقة",
                    description = "في الحديقة أو على جهاز المشي",
                    category = TaskCategory.HEALTH.name,
                    priority = TaskPriority.MEDIUM.name,
                    dueDate = todayStr,
                    isCompleted = true
                )
            )
            dao.insertTask(
                TaskItem(
                    title = "جلسة عائلية ومكالمة صلة رحم",
                    description = "الاتصال بالوالدين أو الأقارب",
                    category = TaskCategory.PERSONAL.name,
                    priority = TaskPriority.HIGH.name,
                    dueDate = todayStr,
                    isCompleted = false
                )
            )
        }

        val existingHabits = dao.getAllHabits().firstOrNull() ?: emptyList()
        if (existingHabits.isEmpty()) {
            // Seed Habits
            val yesterdayStr = getYesterdayDateString()
            dao.insertHabit(
                HabitItem(
                    title = "ورد القرآن الكريم والأذكار",
                    iconEmoji = "📖",
                    category = "ديني",
                    streak = 5,
                    lastCompletedDate = yesterdayStr,
                    completedDatesCsv = yesterdayStr,
                    targetDaysPerWeek = 7
                )
            )
            dao.insertHabit(
                HabitItem(
                    title = "شرب لترين من الماء",
                    iconEmoji = "💧",
                    category = "صحة",
                    streak = 4,
                    lastCompletedDate = yesterdayStr,
                    completedDatesCsv = yesterdayStr,
                    targetDaysPerWeek = 7
                )
            )
            dao.insertHabit(
                HabitItem(
                    title = "الاستيقاظ قبل الساعة ٦ صباحاً",
                    iconEmoji = "🌅",
                    category = "نمط حياة",
                    streak = 3,
                    lastCompletedDate = todayStr,
                    completedDatesCsv = "$yesterdayStr,$todayStr",
                    targetDaysPerWeek = 6
                )
            )
            dao.insertHabit(
                HabitItem(
                    title = "تمارين رياضية خفيفة / إطالة",
                    iconEmoji = "🧘‍♂️",
                    category = "صحة",
                    streak = 2,
                    lastCompletedDate = yesterdayStr,
                    completedDatesCsv = yesterdayStr,
                    targetDaysPerWeek = 5
                )
            )
        }

        val existingNotes = dao.getAllNotes().firstOrNull() ?: emptyList()
        if (existingNotes.isEmpty()) {
            dao.insertNote(
                NoteItem(
                    title = "أفكار لتطوير المهارات الشخصية 💡",
                    content = "١. تعلم أدوات الذكاء الاصطناعي\n٢. إتقان مهارة إدارة الوقت\n٣. قراءة كتابين شهرياً في القيادة\n٤. الالتزام برياضة يومية منتظمة",
                    colorIndex = 0,
                    isPinned = true
                )
            )
            dao.insertNote(
                NoteItem(
                    title = "قائمة مشتريات المنزل 🛒",
                    content = "- حليب وشوفان\n- قهوة عربية وتمر خلاص\n- فواكه طازجة (موز، تفاح)\n- أوراق طباعة وقلم حبر",
                    colorIndex = 1,
                    isPinned = false
                )
            )
            dao.insertNote(
                NoteItem(
                    title = "ملاحظة ملهمة ✨",
                    content = "لا تقارن بدايتك بمواسم حصاد الآخرين؛ ازرع بذرتك اليوم واسقها بالصبر والاستمرار.",
                    colorIndex = 2,
                    isPinned = false
                )
            )
        }

        val existingExpenses = dao.getAllExpenses().firstOrNull() ?: emptyList()
        if (existingExpenses.isEmpty()) {
            dao.insertExpense(
                ExpenseItem(
                    title = "قهوة الصباح وفطور عمل",
                    amount = 28.5,
                    category = ExpenseCategory.FOOD.name,
                    date = todayStr
                )
            )
            dao.insertExpense(
                ExpenseItem(
                    title = "وقود السيارة",
                    amount = 95.0,
                    category = ExpenseCategory.TRANSPORT.name,
                    date = todayStr
                )
            )
            dao.insertExpense(
                ExpenseItem(
                    title = "فاتورة إنترنت المنزل",
                    amount = 230.0,
                    category = ExpenseCategory.BILLS.name,
                    date = todayStr
                )
            )
        }
    }

    // Task Operations
    suspend fun addTask(task: TaskItem): Long = dao.insertTask(task)
    suspend fun updateTask(task: TaskItem) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskItem) = dao.deleteTask(task)
    suspend fun toggleTaskCompleted(task: TaskItem) {
        dao.updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    // Habit Operations
    suspend fun addHabit(habit: HabitItem): Long = dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitItem) = dao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitItem) = dao.deleteHabit(habit)

    suspend fun toggleHabitCompletedToday(habit: HabitItem) {
        val today = getTodayDateString()
        val completedDates = habit.completedDatesCsv.split(",").filter { it.isNotBlank() }.toMutableList()
        val isCompletedToday = completedDates.contains(today)

        val newStreak: Int
        val newDates: String
        val newLastCompleted: String

        if (isCompletedToday) {
            completedDates.remove(today)
            newStreak = (habit.streak - 1).coerceAtLeast(0)
            newDates = completedDates.joinToString(",")
            newLastCompleted = completedDates.lastOrNull() ?: ""
        } else {
            completedDates.add(today)
            newStreak = habit.streak + 1
            newDates = completedDates.joinToString(",")
            newLastCompleted = today
        }

        dao.updateHabit(
            habit.copy(
                streak = newStreak,
                lastCompletedDate = newLastCompleted,
                completedDatesCsv = newDates
            )
        )
    }

    // Note Operations
    suspend fun addNote(note: NoteItem): Long = dao.insertNote(note)
    suspend fun updateNote(note: NoteItem) = dao.updateNote(note)
    suspend fun deleteNote(note: NoteItem) = dao.deleteNote(note)

    // Expense Operations
    suspend fun addExpense(expense: ExpenseItem): Long = dao.insertExpense(expense)
    suspend fun deleteExpense(expense: ExpenseItem) = dao.deleteExpense(expense)

    // Focus Session Operations
    suspend fun addFocusSession(session: FocusSessionItem): Long = dao.insertFocusSession(session)

    // Utility Date Functions
    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

        fun getYesterdayDateString(): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(calendar.time)
        }

        fun getFormattedArabicDate(): String {
            val sdf = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))
            return sdf.format(Date())
        }
    }
}
