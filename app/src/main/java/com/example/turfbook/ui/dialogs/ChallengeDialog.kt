package com.example.turfbook.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.turfbook.data.model.AppConfig
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.Pitch
import com.example.turfbook.data.model.Team
import com.example.turfbook.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ChallengeDialog(
    isOpen: Boolean,
    opponentTeam: Team?,
    allTeams: List<Team>,
    pitches: List<Pitch>,
    language: Language,
    onDismiss: () -> Unit,
    onSendChallenge: (
        challengerTeam: Team,
        challengedTeam: Team,
        pitchId: String,
        date: String,
        slot: String,
        splitMode: String,
        notes: String
    ) -> Unit
) {
    if (!isOpen || opponentTeam == null) return

    val eligibleChallengers = allTeams.filter { it.id != opponentTeam.id }
    var selectedChallengerId by remember { mutableStateOf(eligibleChallengers.firstOrNull()?.id ?: "") }
    var selectedPitchId by remember { mutableStateOf(pitches.firstOrNull()?.id ?: "pitch-1") }
    var selectedSlot by remember { mutableStateOf("20:00 - 21:00") }
    var splitMode by remember { mutableStateOf("50-50") }
    var notes by remember { mutableStateOf("Super Derby challenge fixture! Match on.") }

    val challengeDate = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 2)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == Language.SO) "U Dir Dalab Tartan" else "Challenge Team to Match",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Surface(
                    color = StadiumCardSurface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(opponentTeam.logoEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Challenging: ${opponentTeam.name}", color = AmberGold, fontWeight = FontWeight.Bold)
                            Text("Captain: ${opponentTeam.captainName} (${opponentTeam.captainPhone})", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                Text("Select Your Club:", color = TextSecondary, fontSize = 12.sp)
                eligibleChallengers.forEach { team ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedChallengerId = team.id }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = team.id == selectedChallengerId,
                            onClick = { selectedChallengerId = team.id },
                            colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${team.logoEmoji} ${team.name}", color = Color.White, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Fee Split Agreement:", color = TextSecondary, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("50-50" to "Split 50/50", "challenger-covers" to "We Pay 100%", "loser-pays" to "Loser Pays").forEach { (mode, label) ->
                        val isSel = splitMode == mode
                        Surface(
                            color = if (isSel) EmeraldDark else StadiumCardSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { splitMode = mode }
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) Color.White else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Match Staking / Rules Notes") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = StadiumBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val challenger = eligibleChallengers.find { it.id == selectedChallengerId } ?: eligibleChallengers.firstOrNull() ?: return@Button
                        onSendChallenge(
                            challenger,
                            opponentTeam,
                            selectedPitchId,
                            challengeDate,
                            selectedSlot,
                            splitMode,
                            notes
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                ) {
                    Text("Send Match Challenge", color = StadiumBgDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
