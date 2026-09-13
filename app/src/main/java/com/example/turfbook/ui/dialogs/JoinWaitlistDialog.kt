package com.example.turfbook.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.Pitch
import com.example.turfbook.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinWaitlistDialog(
    isOpen: Boolean,
    pitch: Pitch?,
    date: String,
    slot: String,
    language: Language,
    onDismiss: () -> Unit,
    onSubmitWaitlist: (
        pitch: Pitch,
        date: String,
        slot: String,
        customerName: String,
        customerPhone: String,
        teamName: String,
        notes: String
    ) -> Unit
) {
    if (!isOpen || pitch == null) return

    var customerName by remember { mutableStateOf("Axmed Cali") }
    var customerPhone by remember { mutableStateOf("+252633347832") }
    var teamName by remember { mutableStateOf("26 June Warriors FC") }
    var notes by remember { mutableStateOf("Available on 15 mins notice if slot frees up") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(vertical = 16.dp)
                .testTag("join_waitlist_dialog"),
            colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = AmberGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == Language.SO) "Gal Liiska Sugitaanka" else "Join Slot Waitlist",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (language == Language.SO) "Haddii ballantu baaqato laguugu yeedho" else "Get notified if this booked slot opens up",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Target Slot Summary Card
                Surface(
                    color = StadiumCardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, StadiumBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == Language.SO) pitch.somaliName else pitch.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = pitch.format.label,
                                color = AmberGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = AmberGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(slot, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("•", color = TextMuted)
                            Text(date, color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Explanation Banner
                Surface(
                    color = Color(0x22F59E0B),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.8.dp, AmberGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == Language.SO)
                                "Waqtigan hadda waa mid buuxa. Haddii kooxda ballansatay ay joojiso ama baddasho, maamulka garoonku wuxuu si toos ah ula xiriiri doonaa kooxaha ku jira liiska sugitaanka (sida ay u kala horreeyaan)."
                            else
                                "This prime slot is fully booked. If the booking is canceled or rescheduled, stadium management contacts waitlisted teams in queue order.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Form Fields
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text(if (language == Language.SO) "Magaca Kabtanka / Qofka" else "Captain / Contact Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("waitlist_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = StadiumBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text(if (language == Language.SO) "Telefoonka Xidhiidhka (Zaad/eDahab)" else "Contact Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TextMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("waitlist_phone_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = StadiumBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text(if (language == Language.SO) "Magaca Kooxdaada" else "Your Team Name") },
                    leadingIcon = { Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = TextMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("waitlist_team_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = StadiumBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (language == Language.SO) "Farriin ama Xusuusin Gaar ah" else "Notes / Availability Note") },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("waitlist_notes_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = StadiumBorder
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, StadiumBorder)
                    ) {
                        Text(
                            text = if (language == Language.SO) "Ka Noqo" else "Cancel",
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = {
                            if (customerName.isNotBlank() && customerPhone.isNotBlank()) {
                                onSubmitWaitlist(
                                    pitch,
                                    date,
                                    slot,
                                    customerName.trim(),
                                    customerPhone.trim(),
                                    teamName.trim(),
                                    notes.trim()
                                )
                            }
                        },
                        enabled = customerName.isNotBlank() && customerPhone.isNotBlank(),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("join_waitlist_submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberGold,
                            disabledContainerColor = StadiumBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == Language.SO) "Xaqiiji Liiska" else "Join Waitlist",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
