package com.example.turfbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turfbook.data.model.*
import com.example.turfbook.ui.dialogs.AdminPinDialog
import com.example.turfbook.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    isAdminUnlocked: Boolean,
    pitches: List<Pitch>,
    bookings: List<Booking>,
    blockedSlots: List<BlockedSlot>,
    language: Language,
    onUnlock: (String) -> Boolean,
    onLock: () -> Unit,
    onTogglePitchStatus: (String) -> Unit,
    onBlockSlot: (pitchId: String, date: String, slot: String, reason: String) -> Unit,
    onDeleteBlockedSlot: (String) -> Unit
) {
    var showPinDialog by remember { mutableStateOf(!isAdminUnlocked) }

    if (!isAdminUnlocked) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(StadiumBgDark)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Manager Portal Locked",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Admin dashboard to manage pitch maintenance, view revenues, and block schedule slots. (PIN: 2626)",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showPinDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null, tint = StadiumBgDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enter Manager PIN", color = StadiumBgDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        AdminPinDialog(
            isOpen = showPinDialog,
            onDismiss = { showPinDialog = false },
            onUnlock = onUnlock
        )
        return
    }

    // Unlocked Admin Dashboard
    val totalRevenue = bookings.sumOf { it.totalAmount }
    var selectedPitchId by remember { mutableStateOf(pitches.firstOrNull()?.id ?: "pitch-1") }
    var blockDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var blockSlot by remember { mutableStateOf("18:00 - 19:00") }
    var blockReason by remember { mutableStateOf("Floodlight Maintenance & Grass Grooming") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(StadiumBgDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Manager Portal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("26 JSC Turf Arena Operations", color = TextSecondary, fontSize = 11.sp)
                }

                IconButton(
                    onClick = onLock,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StadiumCardSurface)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = "Lock", tint = AmberGold, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Operational Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("TOTAL REVENUE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("$${totalRevenue.toInt()}", color = EmeraldPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("Across all bookings", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("CONFIRMED BOOKINGS", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${bookings.size}", color = AmberGold, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("Active reservations", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }

        // Pitch status toggles
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Pitch Operational Status", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    pitches.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(p.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(p.format.label, color = TextSecondary, fontSize = 10.sp)
                            }
                            Button(
                                onClick = { onTogglePitchStatus(p.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (p.status == "available") EmeraldDark else Color(0xFFEF4444)
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(if (p.status == "available") "Available" else "Maintenance", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Slot Maintenance Blocker Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Block Slot for Maintenance", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = blockDate,
                        onValueChange = { blockDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = StadiumBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = blockSlot,
                        onValueChange = { blockSlot = it },
                        label = { Text("Time Slot (e.g. 18:00 - 19:00)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = StadiumBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = blockReason,
                        onValueChange = { blockReason = it },
                        label = { Text("Maintenance Reason") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = StadiumBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onBlockSlot(selectedPitchId, blockDate.trim(), blockSlot.trim(), blockReason.trim())
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Block Slot on Schedule", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Blocked Slots List
        item {
            Text("Currently Blocked Maintenance Slots (${blockedSlots.size}):", fontWeight = FontWeight.Bold, color = Color.White)
        }

        items(blockedSlots) { bs ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StadiumCardSurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${bs.date} • ${bs.startTime} - ${bs.endTime}", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(bs.reason, color = TextSecondary, fontSize = 11.sp)
                    }
                    IconButton(onClick = { onDeleteBlockedSlot(bs.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
