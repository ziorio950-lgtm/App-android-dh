package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.model.NoteItem
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.InjazViewModel

object InjazDestinations {
    const val DASHBOARD = "dashboard"
    const val TASKS = "tasks"
    const val HABITS = "habits"
    const val FOCUS = "focus"
    const val NOTES = "notes"
    const val EXPENSES = "expenses"
    const val CURRENCIES = "currencies"

    fun fromTab(tab: AppTab): String = when (tab) {
        AppTab.DASHBOARD -> DASHBOARD
        AppTab.TASKS -> TASKS
        AppTab.HABITS -> HABITS
        AppTab.FOCUS -> FOCUS
        AppTab.NOTES -> NOTES
        AppTab.EXPENSES -> EXPENSES
        AppTab.CURRENCIES -> CURRENCIES
    }

    fun toTab(route: String?): AppTab = when (route) {
        TASKS -> AppTab.TASKS
        HABITS -> AppTab.HABITS
        FOCUS -> AppTab.FOCUS
        NOTES -> AppTab.NOTES
        EXPENSES -> AppTab.EXPENSES
        CURRENCIES -> AppTab.CURRENCIES
        else -> AppTab.DASHBOARD
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InjazAppRoot()
        }
    }
}

@Composable
fun InjazAppRoot(
    viewModel: InjazViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentTab = InjazDestinations.toTab(currentRoute)

    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val quote by viewModel.currentQuote.collectAsStateWithLifecycle()

    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val filteredTasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val taskSearchQuery by viewModel.taskSearchQuery.collectAsStateWithLifecycle()
    val taskFilterCategory by viewModel.taskFilterCategory.collectAsStateWithLifecycle()

    val allHabits by viewModel.allHabits.collectAsStateWithLifecycle()

    val filteredNotes by viewModel.filteredNotes.collectAsStateWithLifecycle()
    val noteSearchQuery by viewModel.noteSearchQuery.collectAsStateWithLifecycle()

    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()

    val focusMode by viewModel.focusMode.collectAsStateWithLifecycle()
    val timerRemainingSeconds by viewModel.timerRemainingSeconds.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val focusSessionsCount by viewModel.focusSessionsCount.collectAsStateWithLifecycle()

    // Dialog States
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<NoteItem?>(null) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val navigateToTab: (AppTab) -> Unit = { tab ->
        val targetRoute = InjazDestinations.fromTab(tab)
        navController.navigate(targetRoute) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    MyApplicationTheme(darkTheme = isDarkTheme) {
        // Enforce RTL for seamless Arabic user experience
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            RealisticAppBackground(isDarkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars),
                    containerColor = Color.Transparent,
                    topBar = {
                        InjazTopHeader(
                            isDarkTheme = isDarkTheme,
                            currentCurrencySymbol = currency,
                            onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                            onOpenCurrencies = { navigateToTab(AppTab.CURRENCIES) },
                            onOpenSettings = { showSettingsDialog = true }
                        )
                    },
                    bottomBar = {
                        InjazBottomNavBar(
                            selectedTab = currentTab,
                            onSelectTab = { navigateToTab(it) }
                        )
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = InjazDestinations.DASHBOARD,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable(InjazDestinations.DASHBOARD) {
                                DashboardScreen(
                                    stats = stats,
                                    tasks = allTasks,
                                    habits = allHabits,
                                    quote = quote,
                                    currency = currency,
                                    smartPresets = viewModel.smartPresets,
                                    onApplyPreset = { viewModel.applySmartPreset(it) },
                                    onNavigateTab = { navigateToTab(it) },
                                    onToggleTask = { viewModel.toggleTask(it) },
                                    onToggleHabit = { viewModel.toggleHabitToday(it) },
                                    onRefreshQuote = { viewModel.refreshQuote() },
                                    onOpenAddTask = { showAddTaskDialog = true },
                                    onOpenAddHabit = { showAddHabitDialog = true },
                                    onOpenAddNote = {
                                        noteToEdit = null
                                        showAddNoteDialog = true
                                    },
                                    onOpenAddExpense = { showAddExpenseDialog = true }
                                )
                            }

                            composable(InjazDestinations.TASKS) {
                                TasksScreen(
                                    tasks = filteredTasks,
                                    searchQuery = taskSearchQuery,
                                    selectedFilter = taskFilterCategory,
                                    onSearchChange = { viewModel.setTaskSearchQuery(it) },
                                    onFilterChange = { viewModel.setTaskFilterCategory(it) },
                                    onToggleTask = { viewModel.toggleTask(it) },
                                    onDeleteTask = { viewModel.deleteTask(it) },
                                    onOpenAddTask = { showAddTaskDialog = true }
                                )
                            }

                            composable(InjazDestinations.HABITS) {
                                HabitsScreen(
                                    habits = allHabits,
                                    onToggleHabit = { viewModel.toggleHabitToday(it) },
                                    onDeleteHabit = { viewModel.deleteHabit(it) },
                                    onOpenAddHabit = { showAddHabitDialog = true }
                                )
                            }

                            composable(InjazDestinations.FOCUS) {
                                FocusTimerScreen(
                                    currentMode = focusMode,
                                    remainingSeconds = timerRemainingSeconds,
                                    isRunning = isTimerRunning,
                                    sessionsCompleted = focusSessionsCount,
                                    onSelectMode = { viewModel.setFocusMode(it) },
                                    onToggleTimer = { viewModel.toggleTimer() },
                                    onResetTimer = { viewModel.resetTimer() }
                                )
                            }

                            composable(InjazDestinations.NOTES) {
                                NotesScreen(
                                    notes = filteredNotes,
                                    searchQuery = noteSearchQuery,
                                    onSearchChange = { viewModel.setNoteSearchQuery(it) },
                                    onTogglePin = { viewModel.toggleNotePinned(it) },
                                    onDeleteNote = { viewModel.deleteNote(it) },
                                    onEditNote = { note ->
                                        noteToEdit = note
                                        showAddNoteDialog = true
                                    },
                                    onOpenAddNote = {
                                        noteToEdit = null
                                        showAddNoteDialog = true
                                    }
                                )
                            }

                            composable(InjazDestinations.EXPENSES) {
                                ExpensesScreen(
                                    expenses = allExpenses,
                                    currency = currency,
                                    onDeleteExpense = { viewModel.deleteExpense(it) },
                                    onOpenAddExpense = { showAddExpenseDialog = true },
                                    onOpenCurrencies = { navigateToTab(AppTab.CURRENCIES) }
                                )
                            }

                            composable(InjazDestinations.CURRENCIES) {
                                CountriesCurrenciesScreen(
                                    currentCurrencySymbol = currency,
                                    onSelectCurrency = { selectedCountry ->
                                        viewModel.setCurrency(selectedCountry.symbolAr)
                                        viewModel.playSuccessTone()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Dialogs
            if (showAddTaskDialog) {
                AddTaskDialog(
                    onDismiss = { showAddTaskDialog = false },
                    onConfirm = { title, desc, cat, prio, date ->
                        viewModel.addTask(title, desc, cat, prio, date)
                        showAddTaskDialog = false
                    }
                )
            }

            if (showAddHabitDialog) {
                AddHabitDialog(
                    onDismiss = { showAddHabitDialog = false },
                    onConfirm = { title, emoji, cat, targetDays ->
                        viewModel.addHabit(title, emoji, cat, targetDays)
                        showAddHabitDialog = false
                    }
                )
            }

            if (showAddNoteDialog) {
                AddNoteDialog(
                    initialNote = noteToEdit,
                    onDismiss = {
                        showAddNoteDialog = false
                        noteToEdit = null
                    },
                    onConfirm = { title, content, colorIndex ->
                        val existing = noteToEdit
                        if (existing != null) {
                            viewModel.updateNote(existing.copy(title = title, content = content, colorIndex = colorIndex))
                        } else {
                            viewModel.addNote(title, content, colorIndex)
                        }
                        showAddNoteDialog = false
                        noteToEdit = null
                    }
                )
            }

            if (showAddExpenseDialog) {
                AddExpenseDialog(
                    currency = currency,
                    onDismiss = { showAddExpenseDialog = false },
                    onConfirm = { title, amount, cat ->
                        viewModel.addExpense(title, amount, cat, "")
                        showAddExpenseDialog = false
                    }
                )
            }

            if (showSettingsDialog) {
                SettingsDialog(
                    currentCurrency = currency,
                    onSelectCurrency = { viewModel.setCurrency(it) },
                    onDismiss = { showSettingsDialog = false }
                )
            }
        }
    }
}
