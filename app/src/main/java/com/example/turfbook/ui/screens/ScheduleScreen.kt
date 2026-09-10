package com.example.turfbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turfbook.data.model.*
import com.example.turfbook.ui.theme.*
import com.example.turfbook.ui.viewmodel.TurfViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ScheduleScreen(
    pitches: List<Pitch>,
    bookings: List<Booking>,
    blockedSlots: List<BlockedSlot>,
    selectedPitchId: String,
    selectedDate: String,
    language: Language,
    onSelectPitch: (String) -> Unit,
    onSelectDate: (String) -> Unit,
    onBookSlot: (pitchId: String, slot: String) -> Unit
) {
    val currentPitch = pitches.find { it.id == selectedPitchId } ?: pitches.firstOrNull()

    // 7 Days
    val dateOptions = remember {
        val list = mutableListOf<Pair<String, String>>()
        val cal = Calendar.getInstance()
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfDisplay = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        for (i in 0..6) {
            val dateStr = sdfDate.format(cal.time)
            val display = if (i == 0) "Today" else if (i == 1) "Tomorrow" else sdfDisplay.format(cal.time)
            list.add(dateStr to display)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StadiumBgDark)
    ) {
        // Date Selector Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dateOptions) { (dateStr, display) ->
                val isSelected = dateStr == selectedDate
                Surface(
                    color = if (isSelected) EmeraldDark else StadiumCardSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectDate(dateStr) }
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = display,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = dateStr.substring(5),
                            color = if (isSelected) AmberGold else TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Pitch Selector Tabs
        ScrollableTabRow(
            selectedTabIndex = pitches.indexOfFirst { it.id == selectedPitchId }.coerceAtLeast(0),
            containerColor = StadiumSurfaceDark,
            contentColor = EmeraldPrimary,
            edgePadding = 16.dp,
            divider = {}
        ) {
            pitches.forEach { pitch ->
                val isSelected = pitch.id == selectedPitchId
                Tab(
                    selected = isSelected,
                    onClick = { onSelectPitch(pitch.id) },
                    text = {
                        Text(
                            text = pitch.name.substringBefore(" -"),
                            color = if (isSelected) EmeraldPrimary else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Selected pitch header
        if (currentPitch != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = StadiumCardSurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == Language.SO) currentPitch.somaliName else currentPitch.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${currentPitch.surface.displayName} • ${currentPitch.format.label}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = "Day $18 / Night $25",
                        color = AmberGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Slot Matrix List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AppConfig.TIME_SLOTS) { slot ->
                val startHour = slot.split(" - ").firstOrNull()?.trim() ?: ""
                val bookingMatch = bookings.find {
                    it.pitchId == selectedPitchId && it.date == selectedDate && it.startTime == startHour
                }
                val blockedMatch = blockedSlots.find {
                    it.pitchId == selectedPitchId && it.date == selectedDate && it.startTime == startHour
                }

                val isNight = TurfViewModel.isNightSlot(slot)
                val rate = if (isNight) (currentPitch?.nightRate ?: 25.0) else (currentPitch?.dayRate ?: 18.0)

                SlotRowCard(
                    slot = slot,
                    rate = rate,
                    isNight = isNight,
                    booking = bookingMatch,
                    blocked = blockedMatch,
                    language = language,
                    onBook = {
                        if (bookingMatch == null && blockedMatch == null) {
                            onBookSlot(selectedPitchId, slot)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SlotRowCard(
    slot: String,
    rate: Double,
    isNight: Boolean,
    booking: Booking?,
    blocked: BlockedSlot?,
    language: Language,
    onBook: () -> Unit
) {
    val isBooked = booking != null
    val isBlocked = blocked != null
    val isAvailable = !isBooked && !isBlocked

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAvailable) { onBook() }
            .border(
                width = 1.dp,
                color = when {
                    isBooked -> Color(0xFF991B1B)
                    isBlocked -> StadiumBorder
                    else -> EmeraldPrimary.copy(alpha = 0.4f)
                },
                shape = RoundedCornerShape(10.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isBooked -> Color(0x33991B1B)
                isBlocked -> StadiumSurfaceDark
                else -> StadiumCardSurface
            }
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isNight) Icons.Default.NightsStay else Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = if (isNight) NightFloodlightBlue else AmberGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = slot,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isNight) "Floodlit ($${rate.toInt()}/hr)" else "Daytime ($${rate.toInt()}/hr)",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            when {
                isBooked -> {
                    Surface(
                        color = Color(0x33DC2626),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${booking?.teamName}",
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
                isBlocked -> {
                    Surface(
                        color = Color(0x3364748B),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = blocked?.reason ?: "Maintenance",
                            color = TextMuted,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                else -> {
                    Button(
                        onClick = onBook,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (language == Language.SO) "Dalbo" else "Book Slot",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
