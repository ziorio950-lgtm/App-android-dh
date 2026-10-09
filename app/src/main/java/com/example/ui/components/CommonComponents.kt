package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.InjazRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import java.util.Calendar

@Composable
fun InjazTopHeader(
    isDarkTheme: Boolean,
    currentCurrencySymbol: String,
    onToggleDarkTheme: () -> Unit,
    onOpenCurrencies: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (currentHour) {
        in 4..11 -> "صباح الخير والبركة ☀️"
        in 12..16 -> "طاب يومك بكل إنجاز 🌿"
        in 17..21 -> "مساء الخير والهمّة 🌙"
        else -> "أهلاً بك، ليلة هانئة ✨"
    }
    val dateStr = InjazRepository.getFormattedArabicDate()

    val countryObj = com.example.ui.screens.CountryCurrencyData.list.find { it.symbolAr == currentCurrencySymbol }
        ?: com.example.ui.screens.CountryCurrencyData.list[0]

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (isDarkTheme) Color(0xD80D1D19) else Color(0xEEFFFFFF),
        tonalElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0x33FFFFFF) else Color(0x1F0B3B30)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Country & Currency Quick Switch Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Emerald500.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable { onOpenCurrencies() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(countryObj.flagEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = countryObj.symbolAr,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = onToggleDarkTheme,
                    modifier = Modifier.size(38.dp).testTag("toggle_dark_theme_btn")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "تبديل المظهر",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(38.dp).testTag("settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "الإعدادات",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InjazBottomNavBar(
    selectedTab: AppTab,
    onSelectTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        val tabs = listOf(
            AppTab.DASHBOARD to Icons.Default.Home,
            AppTab.TASKS to Icons.Default.CheckCircle,
            AppTab.HABITS to Icons.Default.Autorenew,
            AppTab.FOCUS to Icons.Default.HourglassTop,
            AppTab.NOTES to Icons.Default.Description,
            AppTab.EXPENSES to Icons.Default.AccountBalanceWallet,
            AppTab.CURRENCIES to Icons.Default.Public
        )

        tabs.forEach { (tab, icon) ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.titleAr
                    )
                },
                label = {
                    Text(
                        text = tab.titleAr,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
