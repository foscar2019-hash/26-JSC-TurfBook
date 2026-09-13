package com.example.turfbook.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turfbook.data.model.*
import com.example.turfbook.ui.components.LoyaltyLeaderboardSection
import com.example.turfbook.ui.dialogs.PointsDetailDialog
import com.example.turfbook.ui.theme.*
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MyBookingsScreen(
    bookings: List<Booking>,
    registeredTeams: List<Team> = emptyList(),
    reviews: List<PitchReview> = emptyList(),
    referrals: List<ReferralInvite> = emptyList(),
    userReferralCode: String = "JSC-WARRIOR26",
    language: Language,
    onViewTicket: (Booking) -> Unit,
    onCancelBooking: (String) -> Unit,
    onOpenInviteFriend: () -> Unit = {},
    onRunDailyCheck: () -> Unit = {},
    onAddTestTomorrowBooking: () -> Unit = {},
    lastCheckDate: String? = null,
    isDailyCheckRunning: Boolean = false,
    onRateBooking: (Booking) -> Unit = {},
    onRunTwoHourCheck: () -> Unit = {},
    onAddTestTwoHourBooking: () -> Unit = {},
    onTriggerBookingReminder: (Booking) -> Unit = {},
    onOpenEmailPreview: (TwoHourBookingReminder) -> Unit = {},
    dispatchedReminders: List<TwoHourBookingReminder> = emptyList(),
    isTwoHourScannerRunning: Boolean = false,
    onNavigateToBook: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Bookings, 1: Points History
    var historyFilter by remember { mutableStateOf("ALL") } // "ALL", "BOOKING", "REFERRAL", "EVENT"
    var selectedPointsRecord by remember { mutableStateOf<LoyaltyPointsRecord?>(null) }

    val historyRecords = remember(bookings, referrals) {
        LoyaltyPointsRecord.buildHistory(bookings, referrals)
    }
    val totalLoyaltyPoints = remember(historyRecords) {
        historyRecords.sumOf { it.totalPoints }
    }
    val currentTier = LoyaltyTier.fromPoints(totalLoyaltyPoints)
    val (tierName, tierBenefit) = Booking.getTier(totalLoyaltyPoints)
    val completedReferralsCount = referrals.count { it.status == "COMPLETED" }
    val totalReferralBonusPoints = referrals.filter { it.status == "COMPLETED" }.sumOf { it.bonusPoints }

    val bookingPointsTotal = remember(historyRecords) {
        historyRecords.filter { it.type == PointsTransactionType.BOOKING }.sumOf { it.totalPoints }
    }
    val referralPointsTotal = remember(historyRecords) {
        historyRecords.filter { it.type == PointsTransactionType.REFERRAL_BONUS }.sumOf { it.totalPoints }
    }
    val eventPointsTotal = remember(historyRecords) {
        historyRecords.filter { it.type == PointsTransactionType.SPECIAL_EVENT || it.type == PointsTransactionType.PROMO_BONUS }.sumOf { it.totalPoints }
    }

    val filteredRecords = remember(historyRecords, historyFilter) {
        when (historyFilter) {
            "BOOKING" -> historyRecords.filter { it.type == PointsTransactionType.BOOKING }
            "REFERRAL" -> historyRecords.filter { it.type == PointsTransactionType.REFERRAL_BONUS }
            "EVENT" -> historyRecords.filter { it.type == PointsTransactionType.SPECIAL_EVENT || it.type == PointsTransactionType.PROMO_BONUS }
            else -> historyRecords
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(StadiumBgDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == Language.SO) "Ballamahayga & Darajooyinka" else "My Bookings & Rewards",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${bookings.size} confirmed reservations • Status: ${currentTier.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Loyalty Rewards Profile & Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = when (currentTier) {
                            LoyaltyTier.GOLD -> AmberGold
                            LoyaltyTier.SILVER -> Color(0xFFC0C0C0)
                            LoyaltyTier.BRONZE -> Color(0xFFCD7F32)
                        },
                        shape = RoundedCornerShape(14.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Profile & Tier Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = when (currentTier) {
                                    LoyaltyTier.GOLD -> AmberGold.copy(alpha = 0.25f)
                                    LoyaltyTier.SILVER -> Color(0xFFC0C0C0).copy(alpha = 0.25f)
                                    LoyaltyTier.BRONZE -> Color(0xFFCD7F32).copy(alpha = 0.25f)
                                },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when (currentTier) {
                                        LoyaltyTier.GOLD -> AmberGold
                                        LoyaltyTier.SILVER -> Color(0xFFC0C0C0)
                                        LoyaltyTier.BRONZE -> Color(0xFFCD7F32)
                                    }
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(currentTier.badgeIcon, fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                val captainName = bookings.firstOrNull()?.customerName?.ifBlank { null } ?: "Turf Booker"
                                Text(
                                    text = captainName,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (language == Language.SO) "Darajada: ${currentTier.somaliTitle}" else "${currentTier.title} Status Member",
                                    color = when (currentTier) {
                                        LoyaltyTier.GOLD -> AmberGold
                                        LoyaltyTier.SILVER -> Color(0xFFE2E8F0)
                                        LoyaltyTier.BRONZE -> Color(0xFFD97706)
                                    },
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Prominent Tier Status Badge
                        Surface(
                            color = when (currentTier) {
                                LoyaltyTier.GOLD -> AmberGold.copy(alpha = 0.2f)
                                LoyaltyTier.SILVER -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                                LoyaltyTier.BRONZE -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when (currentTier) {
                                    LoyaltyTier.GOLD -> AmberGold
                                    LoyaltyTier.SILVER -> Color(0xFFC0C0C0)
                                    LoyaltyTier.BRONZE -> Color(0xFFCD7F32)
                                }
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(currentTier.badgeIcon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${currentTier.title.uppercase()} STATUS",
                                    color = when (currentTier) {
                                        LoyaltyTier.GOLD -> AmberGold
                                        LoyaltyTier.SILVER -> Color.White
                                        LoyaltyTier.BRONZE -> Color(0xFFFDBA74)
                                    },
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Points Balance & Earning Formula
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = if (language == Language.SO) "Wadarta Dhibcahaaga" else "Total Points Balance",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "$totalLoyaltyPoints",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 26.sp
                                )
                                Text(
                                    text = " PTS",
                                    color = AmberGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Surface(
                            color = EmeraldDark.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (language == Language.SO) "10 dhibcood = $1" else "10 pts = $1 spent",
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rewards Tiers Badges Selector / Row (Bronze, Silver, Gold)
                    Text(
                        text = if (language == Language.SO) "DARAJOOYINKA ABAALMARINTA (REWARD TIERS)" else "REWARDS TIERS STATUS",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(LoyaltyTier.BRONZE, LoyaltyTier.SILVER, LoyaltyTier.GOLD).forEach { tier ->
                            val isCurrent = currentTier == tier
                            val isAchieved = totalLoyaltyPoints >= tier.minPoints
                            val tierBorderColor = when (tier) {
                                LoyaltyTier.GOLD -> AmberGold
                                LoyaltyTier.SILVER -> Color(0xFFC0C0C0)
                                LoyaltyTier.BRONZE -> Color(0xFFCD7F32)
                            }

                            Surface(
                                color = if (isCurrent) tierBorderColor.copy(alpha = 0.18f) else StadiumCardSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isCurrent) 1.5.dp else 0.8.dp,
                                    color = if (isCurrent) tierBorderColor else StadiumBorder
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(tier.badgeIcon, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = tier.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                                            color = if (isCurrent) Color.White else TextPrimary
                                        )
                                    }
                                    Text(
                                        text = "${tier.minPoints}+ pts",
                                        fontSize = 9.sp,
                                        color = if (isAchieved) tierBorderColor else TextMuted,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Surface(
                                        color = if (isCurrent) tierBorderColor.copy(alpha = 0.3f) else Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (isCurrent) "ACTIVE" else if (isAchieved) "UNLOCKED" else "LOCKED",
                                            fontSize = 8.sp,
                                            color = if (isCurrent) Color.White else if (isAchieved) EmeraldPrimary else TextMuted,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress to Next Tier
                    val (nextTierName, nextTierPoints) = when (currentTier) {
                        LoyaltyTier.BRONZE -> "Silver" to 500
                        LoyaltyTier.SILVER -> "Gold" to 1000
                        LoyaltyTier.GOLD -> "Max Tier Achieved!" to 1000
                    }

                    if (currentTier != LoyaltyTier.GOLD) {
                        val progress = (totalLoyaltyPoints.toFloat() / nextTierPoints.toFloat()).coerceIn(0f, 1f)
                        val pointsNeeded = (nextTierPoints - totalLoyaltyPoints).coerceAtLeast(0)

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (language == Language.SO) "Horumarka $nextTierName:" else "Progress to $nextTierName:",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = if (language == Language.SO) "Waxaa kuu dhiman $pointsNeeded pts" else "$pointsNeeded pts to $nextTierName",
                                    color = AmberGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = when (nextTierName) {
                                    "Gold" -> AmberGold
                                    else -> Color(0xFFC0C0C0)
                                },
                                trackColor = StadiumBorder
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏆", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.SO) "Hambalyo! Waxaad gaadhay darajada ugu sarraysa ee Dahabka." else "Top Gold Tier Unlocked! Enjoy VIP Pitch perks & priority access.",
                                color = AmberGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (language == Language.SO) currentTier.perkSo else currentTier.perkEn,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Referral System Card (Invite Friends & Earn Bonus Points)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                color = EmeraldPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (language == Language.SO) "Casuum Saaxiibbada (Referral)" else "Invite Friends & Earn Bonus",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (language == Language.SO) "+150 dhibcood saaxiib kasta markuu ballansado" else "Earn +150 loyalty points per friend's 1st booking",
                                    color = AmberGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            color = AmberGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "+150 PTS",
                                color = AmberGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Referral stats summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = StadiumCardSurface,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${referrals.size}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (language == Language.SO) "La Casuumay" else "Invited",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Surface(
                            color = StadiumCardSurface,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$completedReferralsCount",
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (language == Language.SO) "Dhammaystiray" else "Completed",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Surface(
                            color = StadiumCardSurface,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "+$totalReferralBonusPoints",
                                    color = AmberGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (language == Language.SO) "Dhibco Dheeraad" else "Bonus Earned",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Code Banner + Action Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (language == Language.SO) "Koodhkaaga:" else "Your Code:",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = userReferralCode,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = onOpenInviteFriend,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = StadiumBgDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.SO) "Casuum Saaxiib" else "Invite Friends",
                                color = StadiumBgDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // If referrals list exists, show them
                    if (referrals.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = StadiumBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language == Language.SO) "Saaxiibada La Casuumay:" else "Friend Invites Activity:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            referrals.forEach { ref ->
                                val isCompleted = ref.status == "COMPLETED"
                                Surface(
                                    color = StadiumCardSurface,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = ref.friendName,
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "${ref.friendPhone} • ${ref.date}",
                                                color = TextMuted,
                                                fontSize = 10.sp
                                            )
                                        }

                                        Surface(
                                            color = if (isCompleted) EmeraldDark.copy(alpha = 0.3f) else AmberGold.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                                    contentDescription = null,
                                                    tint = if (isCompleted) EmeraldPrimary else AmberGold,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = if (isCompleted) "+${ref.bonusPoints} PTS EARNED" else "PENDING 1st MATCH",
                                                    color = if (isCompleted) EmeraldPrimary else AmberGold,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Segmented SubTab Switcher: Bookings vs Points History Ledger
        item {
            Surface(
                color = StadiumCardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, StadiumBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    // Subtab 0: Bookings
                    Surface(
                        onClick = { selectedSubTab = 0 },
                        color = if (selectedSubTab == 0) EmeraldPrimary else Color.Transparent,
                        shape = RoundedCornerShape(9.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 9.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = if (selectedSubTab == 0) StadiumBgDark else TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == Language.SO) "Ballamaha (${bookings.size})" else "Bookings (${bookings.size})",
                                color = if (selectedSubTab == 0) StadiumBgDark else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Subtab 1: Loyalty Leaderboard (Top 5)
                    Surface(
                        onClick = { selectedSubTab = 1 },
                        color = if (selectedSubTab == 1) AmberGold else Color.Transparent,
                        shape = RoundedCornerShape(9.dp),
                        modifier = Modifier.weight(1.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 9.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = if (selectedSubTab == 1) StadiumBgDark else AmberGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == Language.SO) "Horyaalka 🏆" else "Leaderboard 🏆",
                                color = if (selectedSubTab == 1) StadiumBgDark else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Subtab 2: Points History Ledger
                    Surface(
                        onClick = { selectedSubTab = 2 },
                        color = if (selectedSubTab == 2) Color(0xFF38BDF8) else Color.Transparent,
                        shape = RoundedCornerShape(9.dp),
                        modifier = Modifier.weight(1.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 9.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = if (selectedSubTab == 2) StadiumBgDark else Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == Language.SO) "Dhibcaha (${historyRecords.size})" else "Points (${historyRecords.size})",
                                color = if (selectedSubTab == 2) StadiumBgDark else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        if (selectedSubTab == 0) {
            // ================= AUTOMATED 2-HOUR KICK-OFF REMINDER & QUICK ACCESS ENGINE =================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.2.dp,
                            color = EmeraldPrimary.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("two_hour_reminder_section"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1E16)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = EmeraldPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(0.8.dp, EmeraldPrimary.copy(alpha = 0.5f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ElectricBolt,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (language == Language.SO) "Ogaysiiska 2 Saac Ka Hor & Tigidhka Degdegga ah" else "Automated 2-Hour Kick-off Reminder",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (language == Language.SO) "Email & ogaysiis wata xidhiidh degdeg ah (Quick Access)" else "Emails & notifies customers 2h prior with 1-tap ticket link",
                                        color = EmeraldPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Surface(
                                color = EmeraldDark.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(0.8.dp, EmeraldPrimary.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = if (dispatchedReminders.isNotEmpty()) "${dispatchedReminders.size} SENT" else "AUTOMATED",
                                    color = EmeraldPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language == Language.SO)
                                "Mashiinka otomaatiga ah wuxuu 2 saac ka hor waqtiga ciyaarta kuu soo dirayaa email iyo ogaysiis degdeg ah oo wata xidhiidh (Quick Access link) si aad toos ugu gasho garoonka adigoon raadin tigidhadada."
                            else
                                "TurfBook automatically dispatches an email voucher and push notification exactly 2 hours before your match, featuring a 1-tap 'Quick Access' deep link straight to your digital match ticket.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions: Simulate 2h kick-off & Run Scanner
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onAddTestTwoHourBooking,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .testTag("simulate_two_hour_booking_button"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(13.dp), tint = StadiumBgDark)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == Language.SO) "⚡ Tijaabi 2h Kulankooda" else "⚡ Simulate 2H Kick-off",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StadiumBgDark
                                )
                            }

                            OutlinedButton(
                                onClick = onRunTwoHourCheck,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .testTag("run_two_hour_check_button"),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.8.dp, StadiumBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                enabled = !isTwoHourScannerRunning
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(13.dp), tint = AmberGold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isTwoHourScannerRunning) "Scanning..." else if (language == Language.SO) "Baadh 2h Hadda" else "Scan 2H Window",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // If dispatched reminders exist, show quick preview
                        if (dispatchedReminders.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = StadiumBorder, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (language == Language.SO) "Ogaysiisyadii Ugu Dambeeyay Ee La Diray:" else "Latest Dispatched 2-Hour Reminders:",
                                color = AmberGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                dispatchedReminders.take(2).forEach { r ->
                                    Surface(
                                        color = StadiumCardSurface,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "⚽ ${r.teamName} @ ${r.pitchName} (🕒 ${r.startTime})",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "To: ${r.customerEmail} • Link: ${r.quickAccessDeepLink}",
                                                    color = TextMuted,
                                                    fontSize = 9.sp,
                                                    maxLines = 1
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                OutlinedButton(
                                                    onClick = { onOpenEmailPreview(r) },
                                                    shape = RoundedCornerShape(4.dp),
                                                    modifier = Modifier.height(26.dp),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                                ) {
                                                    Icon(Icons.Default.Mail, contentDescription = null, tint = AmberGold, modifier = Modifier.size(11.dp))
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text("Email", color = Color.White, fontSize = 9.sp)
                                                }

                                                Button(
                                                    onClick = {
                                                        val b = bookings.find { it.id == r.bookingId }
                                                        if (b != null) onViewTicket(b)
                                                    },
                                                    shape = RoundedCornerShape(4.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                                    modifier = Modifier.height(26.dp),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                                ) {
                                                    Text("Ticket", color = StadiumBgDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ================= DAILY CHECK & UPCOMING MATCH ALERT CARD =================
            item {
                val cal = remember { Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) } }
                val tomorrowStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time) }
                val tomorrowBookings = remember(bookings) { bookings.filter { it.date == tomorrowStr } }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (tomorrowBookings.isNotEmpty()) AmberGold.copy(alpha = 0.7f) else StadiumBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = if (tomorrowBookings.isNotEmpty()) Color(0xFF14241B) else StadiumSurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (tomorrowBookings.isNotEmpty()) Icons.Default.NotificationsActive else Icons.Default.EventAvailable,
                                    contentDescription = null,
                                    tint = if (tomorrowBookings.isNotEmpty()) AmberGold else EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (language == Language.SO) "Baadhitaanka Maalinlaha ah ee Ballamaha" else "Daily Upcoming Booking Check",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                color = if (tomorrowBookings.isNotEmpty()) AmberGold.copy(alpha = 0.2f) else EmeraldDark.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(0.8.dp, if (tomorrowBookings.isNotEmpty()) AmberGold.copy(alpha = 0.5f) else EmeraldPrimary.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = if (tomorrowBookings.isNotEmpty()) "TOMORROW: ${tomorrowBookings.size} MATCH" else "CHECKED TODAY",
                                    color = if (tomorrowBookings.isNotEmpty()) AmberGold else EmeraldPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (tomorrowBookings.isNotEmpty()) {
                            Text(
                                text = if (language == Language.SO)
                                    "Waxaad leedahay ${tomorrowBookings.size} ballan oo loo qorsheeyay barri ($tomorrowStr). Ogaysiis ayaa loo diray kooxda si ay ugu diyaar garoobaan kulanka."
                                else
                                    "You have ${tomorrowBookings.size} match scheduled for tomorrow ($tomorrowStr). Notifications have been dispatched to ensure your team arrives early.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            tomorrowBookings.forEach { tb ->
                                Surface(
                                    color = StadiumCardSurface,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(0.5.dp, AmberGold.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "⚽ ${tb.teamName} @ ${tb.pitchName}",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "Slot: ${tb.startTime} - ${tb.endTime} • Ref #${tb.referenceCode}",
                                                color = AmberGold,
                                                fontSize = 10.sp
                                            )
                                        }
                                        TextButton(
                                            onClick = { onViewTicket(tb) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (language == Language.SO) "Tigidhka" else "Ticket",
                                                color = EmeraldPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = if (language == Language.SO)
                                    "Ma jiraan ballamo kuu qorsheysan barri ($tomorrowStr). Waxaad ballansan kartaa garoon ama waxaad tijaabin kartaa baadhitaanka maalinlaha ah adoo gujinaya badhanka hoose."
                                else
                                    "No matches currently scheduled for tomorrow ($tomorrowStr). You can book a slot or test the daily reminder check using the actions below.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onRunDailyCheck,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.8.dp, StadiumBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                enabled = !isDailyCheckRunning
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp), tint = EmeraldPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isDailyCheckRunning) "Checking..." else if (language == Language.SO) "Baadh Hadda" else "Run Daily Check",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = onAddTestTomorrowBooking,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Icon(Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == Language.SO) "Tijaabi Barri" else "Test Match Alert",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // ================= LOYALTY LEADERBOARD (TOP 5 SQUADS) =================
            item {
                LoyaltyLeaderboardSection(
                    bookings = bookings,
                    registeredTeams = registeredTeams,
                    language = language,
                    onBookMatchClick = onNavigateToBook
                )
            }

            // ================= SUBTAB 0: CONFIRMED BOOKINGS =================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == Language.SO) "Ballamaha Garoomada" else "Confirmed Turf Bookings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${bookings.size} total",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            if (bookings.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (language == Language.SO) "Weli wax ballan ah ma aadan samaysan" else "No turf bookings found",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(bookings) { b ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, StadiumBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = EmeraldDark.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "#${b.referenceCode}",
                                        color = AmberGold,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    color = Color(0x3310B981),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = b.paymentStatus.uppercase(),
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = b.pitchName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "${b.date} • ${b.startTime} - ${b.endTime}",
                                color = AmberGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Team: ${b.teamName} (Captain: ${b.customerName})",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )

                            if (b.referralCodeApplied.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = EmeraldDark.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.clickable {
                                        selectedPointsRecord = historyRecords.find {
                                            it.referenceCode == b.referenceCode && it.type == PointsTransactionType.REFERRAL_BONUS
                                        }
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CardGiftcard,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Referral Bonus: +${b.referralBonusPoints} pts (Code: ${b.referralCodeApplied})",
                                            color = EmeraldPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Total: $${b.totalAmount.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary,
                                        fontSize = 14.sp
                                    )
                                    val pts = if (b.loyaltyPoints > 0) b.loyaltyPoints else Booking.calculatePoints(b.totalAmount)
                                    Surface(
                                        color = AmberGold.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .clickable {
                                                selectedPointsRecord = historyRecords.find {
                                                    it.referenceCode == b.referenceCode && it.type == PointsTransactionType.BOOKING
                                                }
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Stars,
                                                contentDescription = null,
                                                tint = AmberGold,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "+$pts pts earned",
                                                color = AmberGold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = AmberGold.copy(alpha = 0.8f),
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // 2-Hour Kick-off Reminder & Quick Access Email trigger
                                    IconButton(
                                        onClick = { onTriggerBookingReminder(b) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AmberGold.copy(alpha = 0.18f))
                                    ) {
                                        Icon(Icons.Default.ElectricBolt, contentDescription = "2h Reminder & Email", tint = AmberGold, modifier = Modifier.size(16.dp))
                                    }

                                    // WhatsApp Share
                                    IconButton(
                                        onClick = {
                                            val cleanPhone = AppConfig.CONTACT_PHONE.replace("[^0-9]".toRegex(), "")
                                            val msg = "⚽ *Booking #${b.referenceCode} Verification*\n" +
                                                    "Pitch: ${b.pitchName}\n" +
                                                    "Time: ${b.date} (${b.startTime} - ${b.endTime})\n" +
                                                    "Team: ${b.teamName}\n" +
                                                    "Total: $${b.totalAmount.toInt()}"
                                            val uri = Uri.parse("https://wa.me/$cleanPhone?text=" + URLEncoder.encode(msg, "UTF-8"))
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF25D366).copy(alpha = 0.2f))
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                                    }

                                    // View Ticket
                                    Button(
                                        onClick = { onViewTicket(b) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (language == Language.SO) "Tixraaca" else "Ticket", fontSize = 11.sp)
                                    }
                                }
                            }

                            // Post-Match Pitch Rating & Comment CTA
                            val isPassed = BookingTimeHelper.isBookingTimePassed(b.date, b.endTime)
                            val existingRev = reviews.find { it.bookingId == b.id }

                            if (isPassed) {
                                Spacer(modifier = Modifier.height(10.dp))
                                if (existingRev != null) {
                                    Surface(
                                        color = StadiumCardSurface,
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onRateBooking(b) }
                                            .testTag("booking_review_${b.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row {
                                                    repeat(5) { i ->
                                                        Icon(
                                                            imageVector = if (i < existingRev.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                                            contentDescription = null,
                                                            tint = if (i < existingRev.rating) AmberGold else TextMuted,
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (existingRev.comment.isNotBlank()) "\"${existingRev.comment}\"" else "Rated ${existingRev.rating}/5 ⭐",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    maxLines = 1
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (language == Language.SO) "Wax ka beddel" else "Edit",
                                                color = AmberGold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { onRateBooking(b) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(36.dp)
                                            .testTag("rate_pitch_btn_${b.id}"),
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (language == Language.SO) "⭐ Qiimee Garoonka (Leave Review)" else "⭐ Rate Pitch & Leave Review",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ================= SUBTAB 1: POINTS HISTORY LEDGER =================
            // Header Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == Language.SO) "Taariikhda Dhibcaha & Gunnooyinka" else "Points Earned Per Transaction",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (language == Language.SO) "Wadarta faahfaahsan ee dhibcaha laguu xareeyay" else "Detailed transaction ledger including referral & event bonuses",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Summary Breakdown Card (Pitch spend vs Referral vs Event)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AmberGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = StadiumCardSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (language == Language.SO) "Isha Dhibcaha Lagu Helay (Sources Breakdown)" else "LOYALTY POINTS SOURCES BREAKDOWN",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Pitch Bookings
                            Surface(
                                color = EmeraldDark.copy(alpha = 0.15f),
                                border = BorderStroke(0.8.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "⚽ Pitch",
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "+$bookingPointsTotal",
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "10 pts / $1",
                                        color = TextMuted,
                                        fontSize = 9.sp
                                    )
                                }
                            }

                            // Referral Bonus
                            Surface(
                                color = AmberGold.copy(alpha = 0.15f),
                                border = BorderStroke(0.8.dp, AmberGold.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🎁 Referrals",
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "+$referralPointsTotal",
                                        color = AmberGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "+150/invite",
                                        color = TextMuted,
                                        fontSize = 9.sp
                                    )
                                }
                            }

                            // Special Events
                            Surface(
                                color = Color(0xFF818CF8).copy(alpha = 0.15f),
                                border = BorderStroke(0.8.dp, Color(0xFF818CF8).copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🏆 Events",
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "+$eventPointsTotal",
                                        color = Color(0xFF818CF8),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Promo bonus",
                                        color = TextMuted,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Filter Chips Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf(
                        "ALL" to (if (language == Language.SO) "Dhammaan (${historyRecords.size})" else "All (${historyRecords.size})"),
                        "BOOKING" to (if (language == Language.SO) "⚽ Ballamaha (${historyRecords.count { it.type == PointsTransactionType.BOOKING }})" else "⚽ Bookings (${historyRecords.count { it.type == PointsTransactionType.BOOKING }})"),
                        "REFERRAL" to (if (language == Language.SO) "🎁 Casuumadaha (${historyRecords.count { it.type == PointsTransactionType.REFERRAL_BONUS }})" else "🎁 Referrals (${historyRecords.count { it.type == PointsTransactionType.REFERRAL_BONUS }})"),
                        "EVENT" to (if (language == Language.SO) "🏆 Munaasabadaha (${historyRecords.count { it.type == PointsTransactionType.SPECIAL_EVENT || it.type == PointsTransactionType.PROMO_BONUS }})" else "🏆 Events (${historyRecords.count { it.type == PointsTransactionType.SPECIAL_EVENT || it.type == PointsTransactionType.PROMO_BONUS }})")
                    )

                    items(filters) { (key, label) ->
                        val isSelected = historyFilter == key
                        Surface(
                            onClick = { historyFilter = key },
                            color = if (isSelected) AmberGold else StadiumCardSurface,
                            border = BorderStroke(
                                0.8.dp,
                                if (isSelected) AmberGold else StadiumBorder
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) StadiumBgDark else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Points Ledger Records List
            if (filteredRecords.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (language == Language.SO) "Wax dhibco ah lagama helin qaybtaan" else "No points records in this category",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredRecords) { record ->
                    val typeColor = when (record.type) {
                        PointsTransactionType.BOOKING -> EmeraldPrimary
                        PointsTransactionType.REFERRAL_BONUS -> AmberGold
                        PointsTransactionType.SPECIAL_EVENT -> Color(0xFF818CF8)
                        PointsTransactionType.PROMO_BONUS -> Color(0xFFF472B6)
                    }

                    val typeIcon = when (record.type) {
                        PointsTransactionType.BOOKING -> Icons.Default.SportsSoccer
                        PointsTransactionType.REFERRAL_BONUS -> Icons.Default.CardGiftcard
                        PointsTransactionType.SPECIAL_EVENT -> Icons.Default.EmojiEvents
                        PointsTransactionType.PROMO_BONUS -> Icons.Default.AutoAwesome
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPointsRecord = record }
                            .border(1.dp, StadiumBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                // Category Icon
                                Surface(
                                    color = typeColor.copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, typeColor.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = typeIcon,
                                            contentDescription = null,
                                            tint = typeColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Column {
                                    // Title & Category Tag
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (language == Language.SO) record.titleSo else record.titleEn,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "${record.date} • #${record.referenceCode}",
                                        color = AmberGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    Spacer(modifier = Modifier.height(3.dp))

                                    // Tags: Base + Bonus breakdown preview
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (record.basePoints > 0) {
                                            Surface(
                                                color = EmeraldDark.copy(alpha = 0.25f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "Base: +${record.basePoints}",
                                                    color = EmeraldPrimary,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }

                                        if (record.bonusPoints > 0) {
                                            Surface(
                                                color = AmberGold.copy(alpha = 0.25f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "Bonus: +${record.bonusPoints}",
                                                    color = AmberGold,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }

                                        if (record.amountSpent != null && record.amountSpent > 0.0) {
                                            Text(
                                                text = "($${String.format(Locale.US, "%.0f", record.amountSpent)})",
                                                color = TextMuted,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Net Points Badge & Chevron
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    color = typeColor.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, typeColor.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "+${record.totalPoints} PTS",
                                        color = typeColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Itemized Points Transaction Detail Dialog
    if (selectedPointsRecord != null) {
        PointsDetailDialog(
            record = selectedPointsRecord,
            language = language,
            onDismiss = { selectedPointsRecord = null }
        )
    }
}
