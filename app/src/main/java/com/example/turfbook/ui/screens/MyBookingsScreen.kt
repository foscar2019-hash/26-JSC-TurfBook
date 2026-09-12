package com.example.turfbook.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turfbook.data.model.AppConfig
import com.example.turfbook.data.model.Booking
import com.example.turfbook.data.model.Language
import com.example.turfbook.ui.theme.*
import java.net.URLEncoder

@Composable
fun MyBookingsScreen(
    bookings: List<Booking>,
    language: Language,
    onViewTicket: (Booking) -> Unit,
    onCancelBooking: (String) -> Unit
) {
    val context = LocalContext.current
    val totalLoyaltyPoints = bookings.sumOf {
        if (it.loyaltyPoints > 0) it.loyaltyPoints else Booking.calculatePoints(it.totalAmount)
    }
    val currentTier = LoyaltyTier.fromPoints(totalLoyaltyPoints)
    val (tierName, tierBenefit) = Booking.getTier(totalLoyaltyPoints)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StadiumBgDark)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
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

        Spacer(modifier = Modifier.height(10.dp))

        // Loyalty Rewards Profile & Status Card
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

        Spacer(modifier = Modifier.height(10.dp))

        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(bookings) { b ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onViewTicket(b) }
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        }
                    }
                }
            }
        }
    }
}
