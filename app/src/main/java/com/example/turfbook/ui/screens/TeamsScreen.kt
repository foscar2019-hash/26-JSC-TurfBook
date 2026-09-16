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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turfbook.data.model.*
import com.example.turfbook.ui.components.*
import com.example.turfbook.ui.theme.*

@Composable
fun TeamsScreen(
    teams: List<Team>,
    challenges: List<MatchChallenge>,
    bookings: List<Booking> = emptyList(),
    language: Language,
    onOpenRegister: () -> Unit,
    onOpenChallenge: (Team) -> Unit,
    onRespondChallenge: (id: String, accept: Boolean) -> Unit
) {
    var subTab by remember { mutableIntStateOf(0) } // 0: Squads, 1: Challenges
    var selectedLoyaltyFilter by remember { mutableStateOf("ALL") } // ALL, PLATINUM, GOLD
    var viewingLoyaltyTeam by remember { mutableStateOf<Pair<Team, TeamLoyaltyInfo>?>(null) }

    // Calculate loyalty status map from bookings data
    val loyaltyMap = remember(teams, bookings) {
        TeamLoyaltyCalculator.getAllTeamLoyalties(teams, bookings)
    }

    val platinumCount = remember(teams, loyaltyMap) {
        teams.count { (loyaltyMap[it.name] ?: loyaltyMap[it.id])?.loyaltyTier == TeamLoyaltyTier.PLATINUM }
    }
    val goldCount = remember(teams, loyaltyMap) {
        teams.count { (loyaltyMap[it.name] ?: loyaltyMap[it.id])?.loyaltyTier == TeamLoyaltyTier.GOLD }
    }

    val filteredTeams = remember(teams, selectedLoyaltyFilter, loyaltyMap) {
        when (selectedLoyaltyFilter) {
            "PLATINUM" -> teams.filter {
                (loyaltyMap[it.name] ?: loyaltyMap[it.id])?.loyaltyTier == TeamLoyaltyTier.PLATINUM
            }
            "GOLD" -> teams.filter {
                (loyaltyMap[it.name] ?: loyaltyMap[it.id])?.loyaltyTier == TeamLoyaltyTier.GOLD
            }
            else -> teams
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StadiumBgDark)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("teams_matchmaking_view")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (language == Language.SO) "Kooxaha & Tartamada" else "Teams & Matchmaking",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${teams.size} Registered Squads • $platinumCount Platinum • $goldCount Gold",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = onOpenRegister,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = StadiumBgDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (language == Language.SO) "Diiwaangeli" else "Register Team",
                    color = StadiumBgDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sub-tabs row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StadiumSurfaceDark, RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                color = if (subTab == 0) EmeraldDark else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { subTab = 0 }
            ) {
                Text(
                    text = if (language == Language.SO) "Kooxaha (${teams.size})" else "Clubs & Squads (${teams.size})",
                    color = if (subTab == 0) Color.White else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Surface(
                color = if (subTab == 1) EmeraldDark else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { subTab = 1 }
            ) {
                Text(
                    text = if (language == Language.SO) "Tartamada (${challenges.size})" else "Match Challenges (${challenges.size})",
                    color = if (subTab == 1) Color.White else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (subTab == 0) {
            // Squads Tab with Loyalty Filter Legend
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Loyalty Status Filter Legend
                item {
                    LoyaltyStatusFilterLegend(
                        selectedFilter = selectedLoyaltyFilter,
                        platinumCount = platinumCount,
                        goldCount = goldCount,
                        totalCount = teams.size,
                        language = language,
                        onSelectFilter = { selectedLoyaltyFilter = it }
                    )
                }

                if (filteredTeams.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            color = StadiumSurfaceDark,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (selectedLoyaltyFilter == "PLATINUM") "💎 No Platinum squads yet."
                                    else if (selectedLoyaltyFilter == "GOLD") "👑 No Gold squads found."
                                    else "No squads registered.",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Book and complete 5+ matches to reach Gold, or 10+ for Platinum!",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                items(filteredTeams) { team ->
                    val loyaltyInfo = loyaltyMap[team.name]
                        ?: loyaltyMap[team.id]
                        ?: TeamLoyaltyCalculator.getTeamLoyaltyInfo(team.name, bookings, teams)

                    TeamCard(
                        team = team,
                        loyaltyInfo = loyaltyInfo,
                        language = language,
                        onChallenge = { onOpenChallenge(team) },
                        onViewLoyalty = { viewingLoyaltyTeam = Pair(team, loyaltyInfo) }
                    )
                }
            }
        } else {
            // Matchmaking Challenges Tab
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (challenges.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            color = StadiumSurfaceDark,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⚽ No match challenges yet.",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Challenge a Gold or Platinum squad from the Clubs tab to earn double loyalty derby points!",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
                items(challenges) { ch ->
                    val challengerLoyalty = TeamLoyaltyCalculator.getTeamLoyaltyInfo(ch.challengerTeamName, bookings, teams)
                    val challengedLoyalty = TeamLoyaltyCalculator.getTeamLoyaltyInfo(ch.challengedTeamName, bookings, teams)

                    ChallengeCard(
                        challenge = ch,
                        challengerLoyalty = challengerLoyalty,
                        challengedLoyalty = challengedLoyalty,
                        language = language,
                        onRespond = onRespondChallenge,
                        onViewLoyalty = { info ->
                            val matchedTeam = teams.find { it.name.equals(info.teamName, ignoreCase = true) }
                                ?: Team(
                                    id = "temp-${info.teamName}",
                                    name = info.teamName,
                                    logoEmoji = if (info.loyaltyTier == TeamLoyaltyTier.PLATINUM) "💎" else "⭐",
                                    color = "#818CF8",
                                    captainName = "Captain",
                                    captainPhone = "-",
                                    preferredFormat = GameFormat.SEVEN_A_SIDE,
                                    skillLevel = "Competitive",
                                    stats = TeamStats(info.completedBookings, info.completedBookings, 0, 0)
                                )
                            viewingLoyaltyTeam = Pair(matchedTeam, info)
                        }
                    )
                }
            }
        }
    }

    // Detail Dialog for clicked badge
    if (viewingLoyaltyTeam != null) {
        TeamLoyaltyDetailDialog(
            team = viewingLoyaltyTeam?.first,
            loyaltyInfo = viewingLoyaltyTeam?.second,
            language = language,
            onDismiss = { viewingLoyaltyTeam = null }
        )
    }
}

@Composable
fun TeamCard(
    team: Team,
    loyaltyInfo: TeamLoyaltyInfo,
    language: Language,
    onChallenge: () -> Unit,
    onViewLoyalty: () -> Unit
) {
    val context = LocalContext.current
    var expandedSquad by remember { mutableStateOf(false) }

    val isPlatinum = loyaltyInfo.loyaltyTier == TeamLoyaltyTier.PLATINUM
    val isGold = loyaltyInfo.loyaltyTier == TeamLoyaltyTier.GOLD

    val borderBrush = when {
        isPlatinum -> Brush.linearGradient(
            listOf(Color(0xFFE0E7FF), Color(0xFF818CF8), Color(0xFF38BDF8))
        )
        isGold -> Brush.linearGradient(
            listOf(Color(0xFFFEF3C7), Color(0xFFF59E0B), Color(0xFFD97706))
        )
        else -> Brush.linearGradient(
            listOf(StadiumBorder, StadiumBorder)
        )
    }

    val shadowElevation = when {
        isPlatinum -> 6.dp
        isGold -> 4.dp
        else -> 0.dp
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (shadowElevation > 0.dp) Modifier.shadow(shadowElevation, RoundedCornerShape(14.dp))
                else Modifier
            )
            .border(
                width = if (isPlatinum || isGold) 1.5.dp else 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("team_card_${team.id}"),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Logo, Name, Badge, and Challenge Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(team.logoEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = team.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // Visual Loyalty Badge
                            TeamLoyaltyBadge(
                                tier = loyaltyInfo.loyaltyTier,
                                completedBookings = loyaltyInfo.completedBookings,
                                size = BadgeDisplaySize.COMPACT,
                                onClick = onViewLoyalty
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${team.preferredFormat.label} • ${team.skillLevel}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text("•", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = "${loyaltyInfo.completedBookings} Matches",
                                color = when {
                                    isPlatinum -> Color(0xFF818CF8)
                                    isGold -> AmberGold
                                    else -> TextSecondary
                                },
                                fontWeight = if (isPlatinum || isGold) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = onChallenge,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.SportsScore, contentDescription = null, tint = StadiumBgDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == Language.SO) "Tartan" else "Challenge",
                        color = StadiumBgDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // High-Value Gold/Platinum Highlight Strip
            if (loyaltyInfo.isGoldOrPlatinum) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPlatinum) Color(0xFF1E1B4B) else Color(0xFF451A03),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPlatinum) Color(0xFF818CF8).copy(alpha = 0.6f) else AmberGold.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewLoyalty() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(loyaltyInfo.loyaltyTier.badgeEmoji, fontSize = 12.sp)
                            Text(
                                text = if (isPlatinum) "PLATINUM VIP" else "GOLD PREMIER",
                                color = if (isPlatinum) Color(0xFFE0E7FF) else AmberGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "• -${loyaltyInfo.loyaltyTier.discountPercent}% Pitch Discount",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = if (language == Language.SO) "Faa'iidooyinka" else "Perks",
                                color = if (isPlatinum) Color(0xFF818CF8) else AmberGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = if (isPlatinum) Color(0xFF818CF8) else AmberGold,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            if (team.bio.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(team.bio, color = TextSecondary, fontSize = 11.sp, maxLines = 2)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(StadiumCardSurface)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("P", color = TextMuted, fontSize = 10.sp)
                    Text("${team.stats.played}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("W", color = TextMuted, fontSize = 10.sp)
                    Text("${team.stats.won}", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("D", color = TextMuted, fontSize = 10.sp)
                    Text("${team.stats.drawn}", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("L", color = TextMuted, fontSize = 10.sp)
                    Text("${team.stats.lost}", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Win %", color = TextMuted, fontSize = 10.sp)
                    val winRate = if (team.stats.played > 0) ((team.stats.won.toDouble() / team.stats.played) * 100).toInt() else 0
                    Text("$winRate%", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Captain and Contact
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Captain: ${team.captainName}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${team.captainPhone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call Captain", tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                    }

                    TextButton(
                        onClick = { expandedSquad = !expandedSquad },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (expandedSquad) "Hide Squad" else "View Squad (${team.members.size})",
                            fontSize = 11.sp,
                            color = EmeraldPrimary
                        )
                    }
                }
            }

            // Expanded Squad List
            if (expandedSquad) {
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StadiumCardSurface)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    team.members.forEach { m ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${m.name} ${if (m.isCaptain) "(C)" else ""}", color = Color.White, fontSize = 11.sp)
                            Text("#${m.jerseyNumber} • ${m.position}", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: MatchChallenge,
    challengerLoyalty: TeamLoyaltyInfo,
    challengedLoyalty: TeamLoyaltyInfo,
    language: Language,
    onRespond: (id: String, accept: Boolean) -> Unit,
    onViewLoyalty: (TeamLoyaltyInfo) -> Unit
) {
    val bothElite = challengerLoyalty.isGoldOrPlatinum && challengedLoyalty.isGoldOrPlatinum
    val hasElite = challengerLoyalty.isGoldOrPlatinum || challengedLoyalty.isGoldOrPlatinum

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (bothElite) 1.5.dp else 1.dp,
                brush = when {
                    bothElite -> Brush.linearGradient(
                        listOf(Color(0xFF818CF8), AmberGold, Color(0xFF38BDF8))
                    )
                    hasElite -> Brush.linearGradient(
                        listOf(AmberGold, StadiumBorder)
                    )
                    else -> Brush.linearGradient(
                        listOf(StadiumBorder, StadiumBorder)
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("challenge_card_${challenge.id}"),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = when (challenge.status) {
                            "accepted" -> Color(0x3310B981)
                            "declined" -> Color(0x33EF4444)
                            else -> Color(0x33F59E0B)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = challenge.status.replace("_", " ").uppercase(),
                            color = when (challenge.status) {
                                "accepted" -> EmeraldPrimary
                                "declined" -> Color(0xFFEF4444)
                                else -> AmberGold
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (bothElite) {
                        Surface(
                            color = Color(0x33818CF8),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFF818CF8))
                        ) {
                            Text(
                                text = "🏆 ELITE DERBY",
                                color = Color(0xFFE0E7FF),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Split: ${challenge.splitMode}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fixture with Visual Badges for Teams
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(StadiumCardSurface)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Challenger Team
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = challenge.challengerTeamName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        if (challengerLoyalty.isGoldOrPlatinum) {
                            TeamLoyaltyBadge(
                                tier = challengerLoyalty.loyaltyTier,
                                size = BadgeDisplaySize.COMPACT,
                                onClick = { onViewLoyalty(challengerLoyalty) }
                            )
                        }
                    }

                    Text(
                        text = "${challengerLoyalty.completedBookings} matches",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("⚔️", fontSize = 11.sp)
                    Text("VS", color = AmberGold, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp)
                }

                // Challenged Team
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = challenge.challengedTeamName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        if (challengedLoyalty.isGoldOrPlatinum) {
                            TeamLoyaltyBadge(
                                tier = challengedLoyalty.loyaltyTier,
                                size = BadgeDisplaySize.COMPACT,
                                onClick = { onViewLoyalty(challengedLoyalty) }
                            )
                        }
                    }

                    Text(
                        text = "${challengedLoyalty.completedBookings} matches",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${challenge.pitchName} • ${challenge.date} (${challenge.slot})",
                color = AmberGold,
                fontSize = 11.sp
            )

            if (challenge.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(challenge.notes, color = TextSecondary, fontSize = 11.sp)
            }

            if (challenge.status == "pending_opponent") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onRespond(challenge.id, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                    ) {
                        Text("Decline", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onRespond(challenge.id, true) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Accept Match", color = StadiumBgDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
