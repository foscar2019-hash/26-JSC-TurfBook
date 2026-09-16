package com.example.turfbook.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.turfbook.data.model.*
import com.example.turfbook.ui.theme.*

enum class BadgeDisplaySize {
    COMPACT,
    NORMAL,
    EXPANDED
}

/**
 * Visual Badge component highlighting Gold and Platinum loyalty status
 * based on completed match bookings at 26 JSC Arena.
 */
@Composable
fun TeamLoyaltyBadge(
    tier: TeamLoyaltyTier,
    completedBookings: Int? = null,
    size: BadgeDisplaySize = BadgeDisplaySize.NORMAL,
    showDiscount: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isPlatinum = tier == TeamLoyaltyTier.PLATINUM
    val isGold = tier == TeamLoyaltyTier.GOLD

    // Gradients and colors tailored for M3 dark stadium theme
    val backgroundBrush = when {
        isPlatinum -> Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF1E1B4B), // Deep Indigo
                Color(0xFF312E81)  // Electric Violet-Indigo
            )
        )
        isGold -> Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF451A03), // Deep Amber Brown
                Color(0xFF78350F)  // Warm Gold Rust
            )
        )
        tier == TeamLoyaltyTier.SILVER -> Brush.horizontalGradient(
            colors = listOf(Color(0xFF1E293B), Color(0xFF334155))
        )
        else -> Brush.horizontalGradient(
            colors = listOf(Color(0xFF1C1917), Color(0xFF292524))
        )
    }

    val borderBrush = when {
        isPlatinum -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFE0E7FF), // White-indigo glint
                Color(0xFF818CF8), // Electric Indigo
                Color(0xFF38BDF8)  // Neon Cyan sparkle
            )
        )
        isGold -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFEF3C7), // Gold highlight
                Color(0xFFF59E0B), // Radiant Amber
                Color(0xFFD97706)  // Burnished Bronze
            )
        )
        tier == TeamLoyaltyTier.SILVER -> Brush.linearGradient(
            colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8))
        )
        else -> Brush.linearGradient(
            colors = listOf(Color(0xFF78716C), Color(0xFF57534E))
        )
    }

    val contentColor = when {
        isPlatinum -> Color(0xFFE0E7FF)
        isGold -> Color(0xFFFEF3C7)
        tier == TeamLoyaltyTier.SILVER -> Color(0xFFE2E8F0)
        else -> Color(0xFFD6D3D1)
    }

    val glowElevation = when {
        isPlatinum -> 6.dp
        isGold -> 4.dp
        else -> 0.dp
    }

    Surface(
        modifier = modifier
            .then(
                if (glowElevation > 0.dp) Modifier.shadow(glowElevation, RoundedCornerShape(20.dp))
                else Modifier
            )
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (isPlatinum || isGold) 1.2.dp else 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(20.dp)
            )
            .background(backgroundBrush)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .testTag("team_loyalty_badge_${tier.name.lowercase()}"),
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = when (size) {
                    BadgeDisplaySize.COMPACT -> 6.dp
                    BadgeDisplaySize.NORMAL -> 10.dp
                    BadgeDisplaySize.EXPANDED -> 14.dp
                },
                vertical = when (size) {
                    BadgeDisplaySize.COMPACT -> 2.dp
                    BadgeDisplaySize.NORMAL -> 5.dp
                    BadgeDisplaySize.EXPANDED -> 7.dp
                }
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Emoji / Icon
            Text(
                text = tier.badgeEmoji,
                fontSize = when (size) {
                    BadgeDisplaySize.COMPACT -> 11.sp
                    BadgeDisplaySize.NORMAL -> 13.sp
                    BadgeDisplaySize.EXPANDED -> 16.sp
                }
            )

            // Tier Title
            Text(
                text = when {
                    isPlatinum -> if (size == BadgeDisplaySize.COMPACT) "PLATINUM" else "PLATINUM VIP"
                    isGold -> if (size == BadgeDisplaySize.COMPACT) "GOLD" else "GOLD STATUS"
                    else -> tier.tierName.uppercase()
                },
                color = contentColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = when (size) {
                    BadgeDisplaySize.COMPACT -> 9.sp
                    BadgeDisplaySize.NORMAL -> 11.sp
                    BadgeDisplaySize.EXPANDED -> 13.sp
                },
                letterSpacing = 0.6.sp
            )

            // Discount or completed bookings chip
            if (showDiscount && (isPlatinum || isGold)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isPlatinum) Color(0x33818CF8) else Color(0x33F59E0B)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "-${tier.discountPercent}%",
                        color = if (isPlatinum) Color(0xFFC7D2FE) else AmberGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = when (size) {
                            BadgeDisplaySize.COMPACT -> 8.sp
                            BadgeDisplaySize.NORMAL -> 9.sp
                            BadgeDisplaySize.EXPANDED -> 11.sp
                        }
                    )
                }
            }

            if (completedBookings != null && size == BadgeDisplaySize.EXPANDED) {
                Text(
                    text = "• $completedBookings Matches",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Interactive Dialog displayed when tapping a team's loyalty badge,
 * explaining tier status, completed matches count, and earned rewards.
 */
@Composable
fun TeamLoyaltyDetailDialog(
    team: Team?,
    loyaltyInfo: TeamLoyaltyInfo?,
    language: Language,
    onDismiss: () -> Unit
) {
    if (team == null || loyaltyInfo == null) return

    val tier = loyaltyInfo.loyaltyTier
    val isPlatinum = tier == TeamLoyaltyTier.PLATINUM
    val isGold = tier == TeamLoyaltyTier.GOLD

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("team_loyalty_detail_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.5.dp,
                brush = when {
                    isPlatinum -> Brush.linearGradient(listOf(Color(0xFFE0E7FF), Color(0xFF818CF8), Color(0xFF38BDF8)))
                    isGold -> Brush.linearGradient(listOf(Color(0xFFFEF3C7), Color(0xFFF59E0B), Color(0xFFD97706)))
                    else -> Brush.linearGradient(listOf(StadiumBorder, StadiumBorder))
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(team.logoEmoji, fontSize = 28.sp)
                        Column {
                            Text(
                                text = team.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (language == Language.SO) "Xogta Daacadnimada Kooxda" else "Arena Loyalty Status",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Status Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = when {
                        isPlatinum -> Color(0xFF1E1B4B)
                        isGold -> Color(0xFF451A03)
                        else -> StadiumCardSurface
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPlatinum) Color(0xFF818CF8) else if (isGold) AmberGold else StadiumBorder
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TeamLoyaltyBadge(
                            tier = tier,
                            completedBookings = loyaltyInfo.completedBookings,
                            size = BadgeDisplaySize.EXPANDED,
                            showDiscount = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (language == Language.SO) tier.somaliTierName else "${tier.tierName} Loyalty Tier",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isPlatinum) Color(0xFFE0E7FF) else if (isGold) AmberGold else Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (language == Language.SO) tier.perkSo else tier.perkEn,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Key Loyalty Metrics
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(StadiumCardSurface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (language == Language.SO) "Kulamo La Ciyaaray" else "Completed Matches",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${loyaltyInfo.completedBookings}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(StadiumBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (language == Language.SO) "Dhimis Joogto ah" else "Pitch Discount",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${tier.discountPercent}% OFF",
                            color = if (isPlatinum) Color(0xFF38BDF8) else EmeraldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(StadiumBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (language == Language.SO) "Dhibcaha Kooxda" else "Loyalty PTS",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${loyaltyInfo.loyaltyPoints}",
                            color = AmberGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Unlocked Perks List
                Text(
                    text = if (language == Language.SO) "Faa'iidooyinka Heerkan U Gaarka Ah:" else "Unlocked Tier Benefits:",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val perks = if (isPlatinum) {
                        listOf(
                            "💎 15% discount applied directly to all match bookings",
                            "⚽ Free official match ball provided for every session",
                            "🌙 VIP Priority booking window for 18:00 - 22:00 floodlight slots",
                            "🏆 Featured in top 26 JSC Arena Elite Leaderboard"
                        )
                    } else if (isGold) {
                        listOf(
                            "👑 10% discount on all pitch reservations",
                            "⚽ Free match ball usage per booking",
                            "🎽 Free 10-piece high-visibility bibs set per match",
                            "⚔️ Priority Matchmaking challenge acceptance badge"
                        )
                    } else {
                        listOf(
                            "⭐ Standard booking rewards (10 points per $1 spent)",
                            "📅 Access to upcoming fixture schedules",
                            "📈 Reach 5 completed bookings to unlock Gold Status!"
                        )
                    }

                    perks.forEach { perk ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = perk,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlatinum) Color(0xFF6366F1) else if (isGold) AmberGold else EmeraldPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (language == Language.SO) "Waayahay" else "Got It",
                        color = StadiumBgDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

/**
 * Filter and legend banner for the Teams & Matchmaking view, allowing
 * quick filtering by loyalty status (e.g. All, Platinum, Gold).
 */
@Composable
fun LoyaltyStatusFilterLegend(
    selectedFilter: String, // "ALL", "PLATINUM", "GOLD"
    platinumCount: Int,
    goldCount: Int,
    totalCount: Int,
    language: Language,
    onSelectFilter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == Language.SO) "Heerarka Daacadnimada Kooxaha" else "Squad Loyalty Badges",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = if (language == Language.SO) "Taabo calaamadda si aad u aragto xogta" else "Tap badge for perks",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // All filter
                Surface(
                    onClick = { onSelectFilter("ALL") },
                    color = if (selectedFilter == "ALL") EmeraldDark else StadiumCardSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (language == Language.SO) "Dhammaan ($totalCount)" else "All ($totalCount)",
                        color = if (selectedFilter == "ALL") Color.White else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 6.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // Platinum filter
                Surface(
                    onClick = { onSelectFilter("PLATINUM") },
                    color = if (selectedFilter == "PLATINUM") Color(0xFF312E81) else StadiumCardSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = if (selectedFilter == "PLATINUM") androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF818CF8)) else null,
                    modifier = Modifier.weight(1.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💎", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Platinum ($platinumCount)",
                            color = if (selectedFilter == "PLATINUM") Color(0xFFE0E7FF) else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Gold filter
                Surface(
                    onClick = { onSelectFilter("GOLD") },
                    color = if (selectedFilter == "GOLD") Color(0xFF78350F) else StadiumCardSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = if (selectedFilter == "GOLD") androidx.compose.foundation.BorderStroke(1.dp, AmberGold) else null,
                    modifier = Modifier.weight(1.05f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👑", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Gold ($goldCount)",
                            color = if (selectedFilter == "GOLD") AmberGold else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
