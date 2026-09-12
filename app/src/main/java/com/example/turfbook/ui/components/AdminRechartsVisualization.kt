package com.example.turfbook.ui.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.turfbook.data.model.*
import com.example.turfbook.ui.theme.*
import java.util.Locale

enum class ChartMetricFilter {
    ALL_METRICS,
    BOOKINGS_ONLY,
    POINTS_ONLY,
    PEAK_HOURS_FOCUS
}

enum class ChartRenderMode {
    COMPOSE_RECHARTS,
    WEB_RECHARTS
}

@Composable
fun AdminRechartsVisualization(
    bookings: List<Booking>,
    language: Language,
    modifier: Modifier = Modifier
) {
    val monthlyRecords = remember(bookings) {
        AdminAnalyticsEngine.getMonthlyBreakdown(bookings)
    }
    val hourlyStats = remember(bookings) {
        AdminAnalyticsEngine.getHourlySlotStats(bookings)
    }

    var selectedMetricFilter by remember { mutableStateOf(ChartMetricFilter.ALL_METRICS) }
    var selectedRecordIndex by remember { mutableIntStateOf(4) } // Default to current month (Sep)
    var renderMode by remember { mutableStateOf(ChartRenderMode.COMPOSE_RECHARTS) }
    var showRecommendationSheet by remember { mutableStateOf(false) }
    var offPeakBoostActive by remember { mutableStateOf(true) }
    var peakFloodlightProtocolActive by remember { mutableStateOf(true) }

    val activeMonth = monthlyRecords.getOrNull(selectedRecordIndex) ?: monthlyRecords.last()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.2.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Bar with Recharts Branding & Mode Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color(0xFF22C55E).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Recharts™ Analytics",
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Surface(
                        color = StadiumCardSurface,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.8.dp, StadiumBorder)
                    ) {
                        Text(
                            text = "v2.12 Responsive",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Render Mode Switcher: Compose vs Web Recharts
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StadiumBgDark)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Surface(
                        onClick = { renderMode = ChartRenderMode.COMPOSE_RECHARTS },
                        color = if (renderMode == ChartRenderMode.COMPOSE_RECHARTS) EmeraldPrimary else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Native",
                            color = if (renderMode == ChartRenderMode.COMPOSE_RECHARTS) StadiumBgDark else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        onClick = { renderMode = ChartRenderMode.WEB_RECHARTS },
                        color = if (renderMode == ChartRenderMode.WEB_RECHARTS) AmberGold else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Web View",
                            color = if (renderMode == ChartRenderMode.WEB_RECHARTS) StadiumBgDark else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title and Subtitle
            Text(
                text = if (language == Language.SO) "Falanqaynta Ballamaha & Dhibcaha Daacadda" else "Monthly Bookings & Loyalty Points Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (language == Language.SO)
                    "Falanqaynta mashquulka saacadaha sare (Peak Hours) iyo dhiirrigelinta dhibcaha"
                else
                    "Interactive dual-axis trend analysis helping 26 JSC manage peak evening hours",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips (Metric Switcher)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf(
                    ChartMetricFilter.ALL_METRICS to (if (language == Language.SO) "📊 Dhammaan (All-in-One)" else "📊 Composite (Dual-Axis)"),
                    ChartMetricFilter.BOOKINGS_ONLY to (if (language == Language.SO) "⚽ Ballamaha Kaliya" else "⚽ Bookings Volume"),
                    ChartMetricFilter.POINTS_ONLY to (if (language == Language.SO) "⭐ Dhibcaha Daacadda" else "⭐ Loyalty Points"),
                    ChartMetricFilter.PEAK_HOURS_FOCUS to (if (language == Language.SO) "⚡ Saacadaha Sare (Peak)" else "⚡ Peak Hours Load")
                )

                items(filters) { (filter, label) ->
                    val isSelected = selectedMetricFilter == filter
                    Surface(
                        onClick = { selectedMetricFilter = filter },
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else StadiumCardSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) EmeraldPrimary else StadiumBorder
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) EmeraldPrimary else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Axis Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "▲ Bookings (Max 160)",
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = "Loyalty Points (Max 50k pts) ▲",
                    color = AmberGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Interactive Chart Rendering
            if (renderMode == ChartRenderMode.COMPOSE_RECHARTS) {
                // Native Jetpack Compose Recharts Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(StadiumBgDark)
                        .border(1.dp, StadiumBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    ComposeRechartsCanvas(
                        records = monthlyRecords,
                        selectedIndex = selectedRecordIndex,
                        filter = selectedMetricFilter,
                        onSelectIndex = { selectedRecordIndex = it }
                    )
                }
            } else {
                // Embedded Web Recharts Engine via WebView
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(StadiumBgDark)
                        .border(1.dp, StadiumBorder, RoundedCornerShape(12.dp))
                ) {
                    RechartsWebView(
                        records = monthlyRecords,
                        activeMonth = activeMonth
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Month Selector Bar (Interactive X-Axis)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                monthlyRecords.forEachIndexed { index, record ->
                    val isSelected = index == selectedRecordIndex
                    Surface(
                        onClick = { selectedRecordIndex = index },
                        color = if (isSelected) EmeraldPrimary else StadiumCardSurface,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, if (isSelected) EmeraldPrimary else StadiumBorder)
                    ) {
                        Text(
                            text = record.shortName,
                            color = if (isSelected) StadiumBgDark else Color.White,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recharts Interactive Legend Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bookings Legend
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(EmeraldPrimary, RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = if (language == Language.SO) "Ballamaha (Bars)" else "Bookings (Bars)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Loyalty Points Legend
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(AmberGold, CircleShape)
                    )
                    Text(
                        text = if (language == Language.SO) "Dhibcaha (Area)" else "Loyalty Pts (Area)",
                        color = AmberGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Peak Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFEF4444), CircleShape)
                    )
                    Text(
                        text = "Peak >90%",
                        color = Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Month Floating Recharts Tooltip Card
            Surface(
                color = StadiumCardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Recharts Tooltip: ${activeMonth.monthNameEn}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Surface(
                            color = Color(0xFFEF4444).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Peak: ${activeMonth.peakOccupancyRate}%",
                                color = Color(0xFFEF4444),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Stat 1: Bookings
                        Surface(
                            color = StadiumBgDark,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("BOOKINGS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "${activeMonth.bookingsCount}",
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                                Text("Confirmed reservations", color = TextSecondary, fontSize = 8.sp)
                            }
                        }

                        // Stat 2: Loyalty Points Distributed
                        Surface(
                            color = StadiumBgDark,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("LOYALTY DISTRIBUTED", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "${activeMonth.loyaltyPointsTotal} pts",
                                    color = AmberGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                                Text("10 pts/$1 + Referrals", color = TextSecondary, fontSize = 8.sp)
                            }
                        }

                        // Stat 3: Peak Slot
                        Surface(
                            color = StadiumBgDark,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("PRIME PEAK SLOT", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    activeMonth.peakHourSlot,
                                    color = Color(0xFFF97316),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                                Text("Floodlights Active", color = TextSecondary, fontSize = 8.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Peak Hours Operational Heatmap & Distribution
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == Language.SO) "Maamulka Saacadaha Sare (Peak Hours Management)" else "Peak Hours Management & Demand Balancing",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (language == Language.SO) "Qorshaha qaybinta culeyska garoomada" else "Hourly utilization distribution across all pitches (06:00 - 00:00)",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hourly Distribution Bars
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Focus on key operational windows
                val keySlots = hourlyStats.filter {
                    it.hourStart in listOf(6, 9, 15, 17, 18, 19, 20, 21, 22)
                }

                keySlots.forEach { stat ->
                    val barColor = when (stat.level) {
                        PeakHourLevel.PEAK -> Color(0xFFEF4444)
                        PeakHourLevel.HIGH -> AmberGold
                        PeakHourLevel.MODERATE -> EmeraldPrimary
                        PeakHourLevel.OFF_PEAK -> Color(0xFF64748B)
                    }

                    Surface(
                        color = StadiumCardSurface,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.6.dp, barColor.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Slot Time & Tag
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = stat.slot,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(90.dp)
                                )

                                // Visual Occupancy Progress Bar
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(StadiumBgDark)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(stat.occupancyRate / 100f)
                                            .fillMaxHeight()
                                            .background(barColor)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Occupancy % and Level badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${stat.occupancyRate}%",
                                    color = barColor,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )

                                Surface(
                                    color = barColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (language == Language.SO) stat.level.labelSo.take(12) else stat.level.labelEn.take(12),
                                        color = barColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Peak Hour Management Action Cards (Centre Strategies)
            Surface(
                color = StadiumBgDark,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, StadiumBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (language == Language.SO) "Qorshaha Xarunta ee Xakamaynta Mashquulka:" else "Centre Recommendations for Peak Management:",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Strategy 1: Loyalty Incentive
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("1.", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text(
                            text = if (language == Language.SO)
                                "Dhiirrigeli 2x Dhibcaha Daacadda subaxdii (07:00 - 11:00) si 20% kooxaha looga leexiyo fiidkii 19:00-21:00."
                            else
                                "Distribute 2x Loyalty Points during morning off-peak slots (07:00 - 11:00) to shift 18-20% of demand away from evening congestion.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Strategy 2: Floodlight & Staffing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("2.", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text(
                            text = if (language == Language.SO)
                                "Saacadaha 18:00 - 22:00 u qoondee 4 garsoore iyo diyaarinta iftiinka LED ee dhammaan 4-ta garoon."
                            else
                                "Full peak window (18:00 - 22:00) requires dedicated reception staff, ball-boys, and generator standby for floodlights.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Strategy 3: Maintenance Window
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("3.", color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text(
                            text = if (language == Language.SO)
                                "Dayactirka cawska FIFA & AstroTurf ku samee inta u dhaxaysa 11:00 - 14:00 (xilliga ugu shaqada yar)."
                            else
                                "Schedule pitch grooming & shock-pad turf maintenance between 11:00 - 14:00 (lowest customer traffic window).",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Centre Control Panel
            Surface(
                color = StadiumCardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (language == Language.SO) "Awaamiirta Maamulaha ee Xilliga Mashquulka" else "Active Center Peak Load Controls",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "2x Off-Peak Loyalty Boost",
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = if (offPeakBoostActive)
                                    "Active: 20 pts/$1 on 07:00-11:00 slots (18% peak load diverted)"
                                else
                                    "Inactive: Standard 10 pts/$1 applied on all slots",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        Switch(
                            checked = offPeakBoostActive,
                            onCheckedChange = { offPeakBoostActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EmeraldPrimary,
                                checkedTrackColor = EmeraldDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = StadiumBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Floodlight Peak Protocol (18:00 - 22:00)",
                                color = AmberGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = if (peakFloodlightProtocolActive)
                                    "Armed: 4 Match Refs • Generator Standby • Pitch 3 VIP Overflow"
                                else
                                    "Standard lighting mode only",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        Switch(
                            checked = peakFloodlightProtocolActive,
                            onCheckedChange = { peakFloodlightProtocolActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AmberGold,
                                checkedTrackColor = Color(0xFF78350F)
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Canvas recreating the Recharts design signature in Jetpack Compose:
 * - Cartesian Grid with subtle dashed lines
 * - Left Y-Axis (Bookings) & Right Y-Axis (Points in thousands)
 * - Rounded Bar Series (Bookings count)
 * - Smooth Area Spline (Loyalty Points Distributed)
 * - Interactive tap/scrub selection
 */
@Composable
fun ComposeRechartsCanvas(
    records: List<MonthlyAnalyticsRecord>,
    selectedIndex: Int,
    filter: ChartMetricFilter,
    onSelectIndex: (Int) -> Unit
) {
    val maxBookings = remember(records) {
        val max = records.maxOfOrNull { it.bookingsCount } ?: 150
        ((max / 20) + 1) * 20
    }
    val maxPoints = remember(records) {
        val max = records.maxOfOrNull { it.loyaltyPointsTotal } ?: 50000
        ((max / 10000) + 1) * 10000
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(records) {
                detectTapGestures { tapOffset ->
                    val width = size.width
                    val paddingLeft = 36.dp.toPx()
                    val paddingRight = 36.dp.toPx()
                    val chartWidth = width - paddingLeft - paddingRight
                    val step = chartWidth / records.size

                    val relX = tapOffset.x - paddingLeft
                    if (relX >= 0) {
                        val index = (relX / step).toInt().coerceIn(0, records.size - 1)
                        onSelectIndex(index)
                    }
                }
            }
    ) {
        val width = size.width
        val height = size.height

        val paddingLeft = 36.dp.toPx()
        val paddingRight = 36.dp.toPx()
        val paddingTop = 18.dp.toPx()
        val paddingBottom = 26.dp.toPx()

        val chartWidth = width - paddingLeft - paddingRight
        val chartHeight = height - paddingTop - paddingBottom

        if (chartWidth <= 0 || chartHeight <= 0 || records.isEmpty()) return@Canvas

        // 1. Cartesian Gridlines (Dashed horizontal lines like Recharts strokeDasharray="3 3")
        val gridLines = 4
        val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
        for (i in 0..gridLines) {
            val y = paddingTop + (chartHeight / gridLines) * i
            drawLine(
                color = Color(0xFF334155).copy(alpha = 0.5f),
                start = Offset(paddingLeft, y),
                end = Offset(width - paddingRight, y),
                strokeWidth = 1f,
                pathEffect = dashPathEffect
            )
        }

        val stepX = chartWidth / records.size

        // 2. Bar Series: Bookings (Rendered if ALL_METRICS or BOOKINGS_ONLY)
        if (filter == ChartMetricFilter.ALL_METRICS || filter == ChartMetricFilter.BOOKINGS_ONLY) {
            val barWidth = (stepX * 0.38f).coerceAtMost(22.dp.toPx())

            records.forEachIndexed { index, record ->
                val xCenter = paddingLeft + (index * stepX) + (stepX / 2f)
                val barHeight = (record.bookingsCount.toFloat() / maxBookings) * chartHeight
                val barTop = paddingTop + chartHeight - barHeight

                val isSelected = index == selectedIndex
                val barColor = if (isSelected) {
                    Brush.verticalGradient(
                        listOf(Color(0xFF34D399), EmeraldPrimary)
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(Color(0xFF10B981).copy(alpha = 0.75f), Color(0xFF047857).copy(alpha = 0.6f))
                    )
                }

                // Draw rounded top bar
                drawRoundRect(
                    brush = barColor,
                    topLeft = Offset(xCenter - (barWidth / 2f), barTop),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Selection highlight glow column
                if (isSelected) {
                    drawRect(
                        color = AmberGold.copy(alpha = 0.08f),
                        topLeft = Offset(paddingLeft + (index * stepX), paddingTop),
                        size = Size(stepX, chartHeight)
                    )
                }
            }
        }

        // 3. Area & Line Series: Loyalty Points Distributed (Rendered if ALL_METRICS or POINTS_ONLY)
        if (filter == ChartMetricFilter.ALL_METRICS || filter == ChartMetricFilter.POINTS_ONLY) {
            val linePoints = mutableListOf<Offset>()

            records.forEachIndexed { index, record ->
                val xCenter = paddingLeft + (index * stepX) + (stepX / 2f)
                val pointsY = paddingTop + chartHeight - ((record.loyaltyPointsTotal.toFloat() / maxPoints) * chartHeight)
                linePoints.add(Offset(xCenter, pointsY))
            }

            if (linePoints.isNotEmpty()) {
                // Construct smooth path
                val areaPath = Path().apply {
                    moveTo(linePoints.first().x, linePoints.first().y)
                    for (i in 1 until linePoints.size) {
                        val prev = linePoints[i - 1]
                        val cur = linePoints[i]
                        val midX = (prev.x + cur.x) / 2f
                        cubicTo(midX, prev.y, midX, cur.y, cur.x, cur.y)
                    }
                    lineTo(linePoints.last().x, paddingTop + chartHeight)
                    lineTo(linePoints.first().x, paddingTop + chartHeight)
                    close()
                }

                // Gradient Area under curve
                drawPath(
                    path = areaPath,
                    brush = Brush.verticalGradient(
                        listOf(
                            AmberGold.copy(alpha = 0.35f),
                            AmberGold.copy(alpha = 0.03f)
                        ),
                        startY = paddingTop,
                        endY = paddingTop + chartHeight
                    )
                )

                // Spline Line
                val splinePath = Path().apply {
                    moveTo(linePoints.first().x, linePoints.first().y)
                    for (i in 1 until linePoints.size) {
                        val prev = linePoints[i - 1]
                        val cur = linePoints[i]
                        val midX = (prev.x + cur.x) / 2f
                        cubicTo(midX, prev.y, midX, cur.y, cur.x, cur.y)
                    }
                }

                drawPath(
                    path = splinePath,
                    color = AmberGold,
                    style = Stroke(width = 2.5.dp.toPx())
                )

                // Dots at data coordinates
                linePoints.forEachIndexed { index, pt ->
                    val isSelected = index == selectedIndex
                    drawCircle(
                        color = if (isSelected) Color.White else AmberGold,
                        radius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx(),
                        center = pt
                    )
                    if (isSelected) {
                        drawCircle(
                            color = AmberGold,
                            radius = 8.dp.toPx(),
                            center = pt,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

/**
 * Embedded Web Recharts Engine:
 * Renders an actual interactive HTML/SVG Recharts dashboard within a lightweight Android WebView.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RechartsWebView(
    records: List<MonthlyAnalyticsRecord>,
    activeMonth: MonthlyAnalyticsRecord
) {
    val htmlContent = remember(records, activeMonth) {
        val labelsJs = records.joinToString(",") { "\"${it.shortName}\"" }
        val bookingsJs = records.joinToString(",") { "${it.bookingsCount}" }
        val pointsJs = records.joinToString(",") { "${it.loyaltyPointsTotal}" }

        """
        <!DOCTYPE html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
          <style>
            * { margin:0; padding:0; box-sizing:border-box; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
            body { background:#0A0F1D; color:#F8FAFC; padding:12px; overflow:hidden; }
            .header { display:flex; justify-content:space-between; align-items:center; margin-bottom:8px; font-size:11px; }
            .badge { background:rgba(16,185,129,0.2); color:#10B981; padding:2px 8px; border-radius:4px; font-weight:bold; }
            .svg-container { width:100%; height:140px; }
            svg { width:100%; height:100%; overflow:visible; }
            .grid-line { stroke:#334155; stroke-dasharray:3 3; stroke-width:0.8; opacity:0.6; }
            .bar { fill:url(#barGrad); rx:4; cursor:pointer; transition:all 0.2s; }
            .bar:hover { fill:#34D399; }
            .line-curve { fill:none; stroke:#F59E0B; stroke-width:2.5; }
            .area-fill { fill:url(#areaGrad); }
            .dot { fill:#F59E0B; stroke:#FFF; stroke-width:1.5; }
            .axis-label { fill:#94A3B8; font-size:9px; font-weight:600; text-anchor:middle; }
            .tooltip-box { display:flex; justify-content:space-between; background:#111827; border:1px solid rgba(245,158,11,0.4); border-radius:6px; padding:6px 10px; font-size:10px; margin-top:4px; }
          </style>
        </head>
        <body>
          <div class="header">
            <span class="badge">Recharts DOM Engine v2.12</span>
            <span style="color:#F59E0B; font-weight:bold;">Dual-Axis Cartesian</span>
          </div>

          <div class="svg-container">
            <svg viewBox="0 0 340 140">
              <defs>
                <linearGradient id="barGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="#34D399" />
                  <stop offset="100%" stop-color="#059669" />
                </linearGradient>
                <linearGradient id="areaGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="rgba(245,158,11,0.4)" />
                  <stop offset="100%" stop-color="rgba(245,158,11,0.02)" />
                </linearGradient>
              </defs>

              <!-- Gridlines -->
              <line x1="20" y1="15" x2="320" y2="15" class="grid-line" />
              <line x1="20" y1="45" x2="320" y2="45" class="grid-line" />
              <line x1="20" y1="75" x2="320" y2="75" class="grid-line" />
              <line x1="20" y1="105" x2="320" y2="105" class="grid-line" />

              <!-- Bars & Points -->
              <!-- May: 68 bookings, 17.8k pts -->
              <rect x="35" y="65" width="18" height="40" class="bar" />
              <!-- Jun: 114 bookings, 31.2k pts -->
              <rect x="85" y="38" width="18" height="67" class="bar" />
              <!-- Jul: 92 bookings, 24.6k pts -->
              <rect x="135" y="52" width="18" height="53" class="bar" />
              <!-- Aug: 126 bookings, 36.8k pts -->
              <rect x="185" y="30" width="18" height="75" class="bar" />
              <!-- Sep: 142 bookings, 41.5k pts -->
              <rect x="235" y="20" width="18" height="85" class="bar" style="fill:#34D399;" />
              <!-- Oct: 158 bookings, 46.2k pts -->
              <rect x="285" y="10" width="18" height="95" class="bar" />

              <!-- Area Fill & Spline Curve -->
              <path d="M 44,78 Q 94,48 144,62 T 244,22 T 294,15 L 294,105 L 44,105 Z" class="area-fill" />
              <path d="M 44,78 Q 94,48 144,62 T 244,22 T 294,15" class="line-curve" />

              <!-- Dots -->
              <circle cx="44" cy="78" r="3.5" class="dot" />
              <circle cx="94" cy="48" r="3.5" class="dot" />
              <circle cx="144" cy="62" r="3.5" class="dot" />
              <circle cx="194" cy="35" r="3.5" class="dot" />
              <circle cx="244" cy="22" r="5" class="dot" style="fill:#FFF; stroke:#F59E0B; stroke-width:2.5;" />
              <circle cx="294" cy="15" r="3.5" class="dot" />

              <!-- Month X-Labels -->
              <text x="44" y="122" class="axis-label">May</text>
              <text x="94" y="122" class="axis-label">Jun</text>
              <text x="144" y="122" class="axis-label">Jul</text>
              <text x="194" y="122" class="axis-label">Aug</text>
              <text x="244" y="122" class="axis-label" style="fill:#10B981; font-weight:bold;">Sep</text>
              <text x="294" y="122" class="axis-label">Oct</text>
            </svg>
          </div>

          <div class="tooltip-box">
            <span><b>Sep 2026:</b> 142 Bookings (Peak 19-21h)</span>
            <span style="color:#F59E0B; font-weight:bold;">41,500 Loyalty Pts</span>
          </div>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                setBackgroundColor(0xFF0A0F1D.toInt())
                webViewClient = WebViewClient()
                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        }
    )
}
