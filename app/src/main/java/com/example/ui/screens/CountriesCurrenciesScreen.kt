package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CountryCurrency
import com.example.ui.components.Realistic3DButton
import com.example.ui.components.Realistic3DCard
import com.example.ui.theme.*

object CountryCurrencyData {
    val list: List<CountryCurrency> = listOf(
        CountryCurrency("SA", "المملكة العربية السعودية", "🇸🇦", "SAR", "ريال سعودي", "ر.س", 3.75),
        CountryCurrency("AE", "الإمارات العربية المتحدة", "🇦🇪", "AED", "درهم إماراتي", "د.إ", 3.67),
        CountryCurrency("KW", "دولة الكويت", "🇰🇼", "KWD", "دينار كويتي", "د.ك", 0.31),
        CountryCurrency("QA", "دولة قطر", "🇶🇦", "QAR", "ريال قطري", "ر.ق", 3.64),
        CountryCurrency("OM", "سلطنة عمان", "🇴🇲", "OMR", "ريال عماني", "ر.ع", 0.385),
        CountryCurrency("BH", "مملكة البحرين", "🇧🇭", "BHD", "دينار بحريني", "د.ب", 0.376),
        CountryCurrency("EG", "جمهورية مصر العربية", "🇪🇬", "EGP", "جنيه مصري", "ج.م", 48.5),
        CountryCurrency("JO", "المملكة الأردنية الهاشمية", "🇯🇴", "JOD", "دينار أردني", "د.أ", 0.709),
        CountryCurrency("MA", "المملكة المغربية", "🇲🇦", "MAD", "درهم مغربي", "د.م", 9.85),
        CountryCurrency("DZ", "الجمهورية الجزائرية", "🇩🇿", "DZD", "دينار جزائري", "د.ج", 134.0),
        CountryCurrency("TN", "الجمهورية التونسية", "🇹🇳", "TND", "دينار تونسي", "د.ت", 3.08),
        CountryCurrency("IQ", "جمهورية العراق", "🇮🇶", "IQD", "دينار عراقي", "د.ع", 1310.0),
        CountryCurrency("US", "الولايات المتحدة الأمريكية", "🇺🇸", "USD", "دولار أمريكي", "$", 1.0),
        CountryCurrency("EU", "الاتحاد الأوروبي", "🇪🇺", "EUR", "يورو", "€", 0.92),
        CountryCurrency("GB", "المملكة المتحدة", "🇬🇧", "GBP", "جنيه إسترليني", "£", 0.78),
        CountryCurrency("JP", "اليابان", "🇯🇵", "JPY", "ين ياباني", "¥", 152.0),
        CountryCurrency("TR", "تركيا", "🇹🇷", "TRY", "ليرة تركية", "₺", 34.2)
    )
}

@Composable
fun CountriesCurrenciesScreen(
    currentCurrencySymbol: String,
    onSelectCurrency: (CountryCurrency) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var calcAmountStr by remember { mutableStateOf("100") }
    var selectedRegionFilter by remember { mutableStateOf("الكل") }

    val regions = listOf("الكل", "الخليج العربي", "شمال إفريقيا والشام", "دولية")

    val selectedCountry = remember(currentCurrencySymbol) {
        CountryCurrencyData.list.find { it.symbolAr == currentCurrencySymbol }
            ?: CountryCurrencyData.list[0]
    }

    val filteredCountries = remember(searchQuery, selectedRegionFilter) {
        CountryCurrencyData.list.filter { country ->
            val matchesSearch = searchQuery.isBlank() ||
                country.countryNameAr.contains(searchQuery, ignoreCase = true) ||
                country.currencyNameAr.contains(searchQuery, ignoreCase = true) ||
                country.currencyCode.contains(searchQuery, ignoreCase = true) ||
                country.symbolAr.contains(searchQuery, ignoreCase = true)

            val matchesRegion = when (selectedRegionFilter) {
                "الخليج العربي" -> country.countryCode in listOf("SA", "AE", "KW", "QA", "OM", "BH")
                "شمال إفريقيا والشام" -> country.countryCode in listOf("EG", "JO", "MA", "DZ", "TN", "IQ")
                "دولية" -> country.countryCode in listOf("US", "EU", "GB", "JP", "TR")
                else -> true
            }

            matchesSearch && matchesRegion
        }
    }

    val parsedCalcAmount = calcAmountStr.toDoubleOrNull() ?: 1.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("countries_currencies_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 3D Hero Banner
        item {
            Realistic3DCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Emerald900)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_currency_globe_1791508574827),
                            contentDescription = "كرة العملات الذهبية ثلاثية الأبعاد",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "العملات والبلدان الرسمية 🌍",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "العملة النشطة: ${selectedCountry.flagEmoji} ${selectedCountry.currencyNameAr} (${selectedCountry.symbolAr})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "اختر عملة بلدك لتفعيلها في كافة تقارير ومصاريف التطبيق",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Live Quick Currency Converter Box
        item {
            Realistic3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "محوّل العملات الفوري 💱",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "تقدير قياسي لـ USD",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = calcAmountStr,
                        onValueChange = { calcAmountStr = it },
                        label = { Text("المبلغ بـ (${selectedCountry.symbolAr})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "القيمة الموازية بأهم العملات:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val topComparison = listOf("SA", "AE", "KW", "EG", "US", "EU").mapNotNull { code ->
                        CountryCurrencyData.list.find { it.countryCode == code }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        topComparison.forEach { target ->
                            // converted = amount / selected.rate * target.rate
                            val converted = (parsedCalcAmount / selectedCountry.rateToUSD) * target.rateToUSD
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${target.flagEmoji} ${target.currencyCode}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = String.format(java.util.Locale.US, "%.1f %s", converted, target.symbolAr),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search & Region Filter
        item {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث عن بلدك أو عملتك...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    regions.forEach { reg ->
                        val isSelected = selectedRegionFilter == reg
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRegionFilter = reg },
                            label = { Text(reg, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        // Country Cards List
        items(filteredCountries, key = { it.countryCode }) { country ->
            val isSelected = country.symbolAr == currentCurrencySymbol
            RealisticCountryCard(
                country = country,
                isSelected = isSelected,
                onSelect = { onSelectCurrency(country) }
            )
        }
    }
}

@Composable
fun RealisticCountryCard(
    country: CountryCurrency,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Realistic3DCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onSelect,
        elevation = if (isSelected) 8.dp else 3.dp,
        borderStroke = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Realistic Flag Container
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(country.flagEmoji, fontSize = 30.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = country.countryNameAr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${country.currencyNameAr} (${country.currencyCode})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) GoldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = country.symbolAr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) GoldDark else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (isSelected) {
                    Text(
                        text = "✓ العملة النشطة",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                } else {
                    Text(
                        text = "اضغط للتفعيل",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
