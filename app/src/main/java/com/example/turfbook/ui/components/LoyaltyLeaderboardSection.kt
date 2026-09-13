package com.example.turfbook.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turfbook.data.model.Booking
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.Team
import com.example.turfbook.data.model.TeamLoyaltyLeaderboardEntry
import com.example.turfbook.ui.theme.*

/**
 * Loyalty Leaderboard section displayed in MyBookingsView.
 * Ranks the top 5 teams based on their total number of completed bookings,
 * actively incentivizing repeat visits with unlocked loyalty perks and discounts.
 */
@Composable
fun LoyaltyLeaderboardSection(
    bookings: List<Booking>,
    registeredTeams: List<Team> = emptyList(),
    language: Language = Language.EN,
    onBookMatchClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val leaderboardEntries = remember(bookings, registeredTeams) {
        TeamLoyaltyLeaderboardEntry.buildLeaderboard(bookings, registeredTeams)
    }

    val maxCompletedBookings = remember(leaderboardEntries) {
        leaderboardEntries.firstOrNull()?.completedBookings?.coerceAtLeast(1) ?: 1
    }

    var selectedTeamEntry by remember { mutableStateOf<TeamLoyaltyLeaderboardEntry?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("loyalty_leaderboard_section")
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        AmberGold,
                        EmeraldPrimary.copy(alpha = 0.8f),
                        StadiumBorder
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = AmberGold.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold),
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🏆", fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == Language.SO) "Horyaalka Daacadda Kooxaha" else "Loyalty Leaderboard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 17.sp
                        )
                        Text(
                            text = if (language == Language.SO)
                                "5-ta kooxood ee ugu ballamaha badan (Dhammaystiran)"
                            else
                                "Top 5 teams by completed bookings",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    color = EmeraldDark.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == Language.SO) "XILLIGA 2026" else "SEASON 2026",
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Motivation Banner: Incentivizing Repeat Visits
            Surface(
                color = Color(0xFF0F261E),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == Language.SO)
                                "Faa'iidada Ballamaha Joogtada ah"
                            else
                                "Incentivizing Repeat Visits",
                            fontWeight = FontWeight.Bold,
                            color = AmberGold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (language == Language.SO)
                                "Kulamada aad dhammaystirto waxay kooxdaada u horseedayaan qiimo-dhimis joogto ah (ilaa 15%), kubbad bilaash ah iyo mudnaan xilliga habeenkii."
                            else
                                "Complete matches to climb the podium! Top teams unlock up to 15% discount on all bookings, free ball/bib rentals, and priority night floodlight slots.",
                            color = Color(0xFFD1FAE5),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Leaderboard Items: Top 5 Teams
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                leaderboardEntries.forEach { entry ->
                    LeaderboardTeamRow(
                        entry = entry,
                        maxBookings = maxCompletedBookings,
                        language = language,
                        isSelected = selectedTeamEntry?.rank == entry.rank,
                        onSelect = {
                            selectedTeamEntry = if (selectedTeamEntry?.rank == entry.rank) null else entry
                        }
                    )
                }
            }

            // Expanded Perk Inspection Card
            AnimatedVisibility(
                visible = selectedTeamEntry != null,
                enter = fadeIn() + slideInVertically()
            ) {
                selectedTeamEntry?.let { entry ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = StadiumCardSurface,
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, AmberGold.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(entry.logoEmoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${entry.teamName} Perks",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    text = "Rank #${entry.rank}",
                                    fontWeight = FontWeight.Black,
                                    color = AmberGold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (language == Language.SO) entry.rewardPerkSo else entry.rewardPerkEn,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == Language.SO)
                                    "Wadarta dhibcaha daacadda: ${entry.loyaltyPointsEarned} pts • ${entry.totalHours} saacadood oo garoonka lagu qaatay"
                                else
                                    "Total squad points: ${entry.loyaltyPointsEarned} pts • ${entry.totalHours} hours on pitch",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CTA: Book Next Match & Climb Leaderboard
            Button(
                onClick = onBookMatchClick,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("leaderboard_book_match_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = null,
                    tint = StadiumBgDark,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == Language.SO)
                        "Ballanso Ciyaar Cusub & Kor u Kac 🚀"
                    else
                        "Book Match & Climb Standings 🚀",
                    fontWeight = FontWeight.Bold,
                    color = StadiumBgDark,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * Individual row item for the top 5 leaderboard squads.
 */
@Composable
private fun LeaderboardTeamRow(
    entry: TeamLoyaltyLeaderboardEntry,
    maxBookings: Int,
    language: Language,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val podiumBorderColor = when (entry.rank) {
        1 -> AmberGold
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> StadiumBorder
    }

    val rankBadgeBackground = when (entry.rank) {
        1 -> AmberGold.copy(alpha = 0.25f)
        2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
        3 -> Color(0xFFCD7F32).copy(alpha = 0.25f)
        else -> Color(0xFF1E293B)
    }

    val rankMedal = when (entry.rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#${entry.rank}"
    }

    val progressRatio = (entry.completedBookings.toFloat() / maxBookings.toFloat()).coerceIn(0.15f, 1f)

    Surface(
        onClick = onSelect,
        color = if (isSelected) Color(0xFF1B2A24) else StadiumCardSurface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (entry.rank == 1) 1.5.dp else 1.dp,
            color = if (isSelected) EmeraldPrimary else podiumBorderColor.copy(alpha = if (entry.rank <= 3) 0.8f else 0.4f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_item_${entry.rank}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Rank + Team Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Rank badge
                    Surface(
                        color = rankBadgeBackground,
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, podiumBorderColor),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (entry.rank <= 3) {
                                Text(rankMedal, fontSize = 16.sp)
                            } else {
                                Text(
                                    text = rankMedal,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(entry.logoEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = entry.teamName,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "${entry.completedBookings} ${if (language == Language.SO) "ballan oo dhammaystiran" else "completed bookings"}",
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Perk / Discount Badge
                Surface(
                    color = when (entry.rank) {
                        1 -> AmberGold.copy(alpha = 0.2f)
                        2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                        3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                        else -> EmeraldDark.copy(alpha = 0.2f)
                    },
                    border = androidx.compose.foundation.BorderStroke(1.dp, podiumBorderColor),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${entry.discountPercent}% OFF",
                        color = when (entry.rank) {
                            1 -> AmberGold
                            2 -> Color.White
                            3 -> Color(0xFFFDBA74)
                            else -> EmeraldPrimary
                        },
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Booking frequency bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF1E293B))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressRatio)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = when (entry.rank) {
                                    1 -> listOf(AmberGold, Color(0xFFFDE68A))
                                    2 -> listOf(Color(0xFF94A3B8), Color(0xFFE2E8F0))
                                    3 -> listOf(Color(0xFFCD7F32), Color(0xFFFED7AA))
                                    else -> listOf(EmeraldDark, EmeraldPrimary)
                                }
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Unlocked Perk summary line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == Language.SO) entry.rewardPerkSo else entry.rewardPerkEn,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${entry.loyaltyPointsEarned} PTS",
                    color = AmberGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}
