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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.MatchChallenge
import com.example.turfbook.data.model.Team
import com.example.turfbook.ui.theme.*

@Composable
fun TeamsScreen(
    teams: List<Team>,
    challenges: List<MatchChallenge>,
    language: Language,
    onOpenRegister: () -> Unit,
    onOpenChallenge: (Team) -> Unit,
    onRespondChallenge: (id: String, accept: Boolean) -> Unit
) {
    var subTab by remember { mutableIntStateOf(0) } // 0: Squads, 1: Challenges

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
                    text = if (language == Language.SO) "Kooxaha & Tartamada" else "Teams & Matchmaking",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${teams.size} Registered Squads • Match Challenges",
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
                Text(if (language == Language.SO) "Diiwaangeli" else "Register Team", color = StadiumBgDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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

        Spacer(modifier = Modifier.height(12.dp))

        if (subTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(teams) { team ->
                    TeamCard(
                        team = team,
                        language = language,
                        onChallenge = { onOpenChallenge(team) }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (challenges.isEmpty()) {
                    item {
                        Text(
                            text = "No match challenges yet. Challenge a team from the Clubs tab!",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
                items(challenges) { ch ->
                    ChallengeCard(
                        challenge = ch,
                        language = language,
                        onRespond = onRespondChallenge
                    )
                }
            }
        }
    }
}

@Composable
fun TeamCard(
    team: Team,
    language: Language,
    onChallenge: () -> Unit
) {
    val context = LocalContext.current
    var expandedSquad by remember { mutableStateOf(false) }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(team.logoEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(team.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${team.preferredFormat.label} • ${team.skillLevel}", color = TextSecondary, fontSize = 11.sp)
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
                    Text(if (language == Language.SO) "Tartan" else "Challenge", color = StadiumBgDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
    language: Language,
    onRespond: (id: String, accept: Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StadiumBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Text(
                    text = "Split: ${challenge.splitMode}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${challenge.challengerTeamName} ⚔️ ${challenge.challengedTeamName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

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
