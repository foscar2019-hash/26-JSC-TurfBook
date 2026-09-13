package com.example.turfbook.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turfbook.data.model.*
import com.example.turfbook.ui.components.AdminRechartsVisualization
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
    waitlistEntries: List<WaitlistEntry> = emptyList(),
    dispatchedReminders: List<TwoHourBookingReminder> = emptyList(),
    language: Language,
    onUnlock: (String) -> Boolean,
    onLock: () -> Unit,
    onTogglePitchStatus: (String) -> Unit,
    onBlockSlot: (pitchId: String, date: String, slot: String, reason: String) -> Unit,
    onDeleteBlockedSlot: (String) -> Unit,
    onRemoveWaitlistEntry: (String) -> Unit = {},
    onUpdateWaitlistStatus: (id: String, status: String) -> Unit = { _, _ -> },
    onTriggerTwoHourReminder: (Booking) -> Unit = {},
    onAddTestTwoHourBooking: () -> Unit = {},
    onOpenEmailPreview: (TwoHourBookingReminder) -> Unit = {},
    onViewTicket: (Booking) -> Unit = {}
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
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("REVENUE", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("$${totalRevenue.toInt()}", color = EmeraldPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text("Confirmed", color = TextSecondary, fontSize = 8.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("BOOKINGS", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("${bookings.size}", color = AmberGold, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text("Active", color = TextSecondary, fontSize = 8.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("PEAK LOAD", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("96%", color = Color(0xFFEF4444), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text("19:00 - 21:00", color = TextSecondary, fontSize = 8.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("WAITLIST", color = AmberGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("${waitlistEntries.size}", color = AmberGold, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text("Queue teams", color = TextSecondary, fontSize = 8.sp)
                    }
                }
            }
        }

        // Recharts Data Visualization: Monthly Bookings & Loyalty Points Distribution with Peak Hour Management
        item {
            AdminRechartsVisualization(
                bookings = bookings,
                language = language
            )
        }

        // Waitlist for Booked Time Blocks Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_waitlist_section"),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = AmberGold.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        tint = AmberGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == Language.SO) "Liiska Sugitaanka Waqtiyada Buuxa" else "Waitlisted Users for Booked Slots",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (language == Language.SO) "Kala maaraynta kooxaha sugaya waqtiyada la ballansaday" else "Queue management for high-demand booked time blocks",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Surface(
                            color = if (waitlistEntries.isNotEmpty()) AmberGold.copy(alpha = 0.2f) else StadiumBorder,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${waitlistEntries.size} in queue",
                                color = if (waitlistEntries.isNotEmpty()) AmberGold else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (waitlistEntries.isEmpty()) {
                        Surface(
                            color = StadiumCardSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.HourglassEmpty,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (language == Language.SO)
                                        "Ma jiraan ciyaartooy hadda liiska sugitaanka ku jira. Marka kooxi ay riixdo 'Gal Liiska' ee jadwalka halkan ayay ka muuqan doonaan."
                                    else
                                        "No players currently on the waitlist. When teams tap 'Join Waitlist' on booked schedule slots, they will appear here grouped by time block.",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    } else {
                        val grouped = waitlistEntries.groupBy { "${it.pitchName}__${it.date}__${it.slot}" }
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            grouped.forEach { (_, entriesForBlock) ->
                                val firstEntry = entriesForBlock.first()
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = StadiumCardSurface),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(0.8.dp, StadiumBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        // Block Header
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = firstEntry.pitchName,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    fontSize = 12.sp
                                                )
                                                Text(
                                                    text = "${firstEntry.date} • ${firstEntry.slot}",
                                                    color = AmberGold,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Surface(
                                                color = EmeraldDark.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "${entriesForBlock.size} waiting",
                                                    color = EmeraldPrimary,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Entries for this time block
                                        entriesForBlock.forEachIndexed { idx, entry ->
                                            WaitlistUserRow(
                                                queuePosition = idx + 1,
                                                entry = entry,
                                                language = language,
                                                onRemove = { onRemoveWaitlistEntry(entry.id) },
                                                onToggleStatus = {
                                                    val newStatus = if (entry.status == "WAITING") "NOTIFIED" else "WAITING"
                                                    onUpdateWaitlistStatus(entry.id, newStatus)
                                                }
                                            )
                                            if (idx < entriesForBlock.size - 1) {
                                                Divider(
                                                    color = StadiumBorder.copy(alpha = 0.5f),
                                                    thickness = 0.5.dp,
                                                    modifier = Modifier.padding(vertical = 6.dp)
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

        // Automated 2-Hour Kick-off Reminder System & Email Delivery Queue
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_two_hour_reminders_section"),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = EmeraldPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ElectricBolt,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == Language.SO) "Ogaysiiska 2 Saac Ka Hor & Xidhiidhka Degdegga ah" else "Automated 2-Hour Reminders & Ticket Links",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (language == Language.SO) "U dirista macaamiisha email iyo ogaysiis wata tigidhadooda" else "Dispatches email + push notification with Quick Access link 2h prior to match",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Surface(
                            color = EmeraldDark.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${dispatchedReminders.size} SENT",
                                color = EmeraldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Test button to simulate 2h kickoff
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onAddTestTwoHourBooking,
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .testTag("admin_simulate_two_hour_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.AddAlert, contentDescription = null, tint = StadiumBgDark, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == Language.SO) "+ Tijaabi 2h Ogaysiis" else "+ Simulate 2H Kick-off Match",
                                color = StadiumBgDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // If dispatched reminders exist, show list with preview email buttons
                    if (dispatchedReminders.isNotEmpty()) {
                        Text(
                            text = if (language == Language.SO) "Email-yada & Ogaysiisyada La Diray:" else "Dispatched 2-Hour Reminders Log:",
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            dispatchedReminders.take(5).forEach { r ->
                                Surface(
                                    color = StadiumCardSurface,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(0.5.dp, StadiumBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(r.teamName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                Text("• #${r.bookingReference}", color = AmberGold, fontSize = 10.sp)
                                            }
                                            Text(
                                                text = "To: ${r.customerEmail} • Time: ${r.startTime}",
                                                color = TextMuted,
                                                fontSize = 10.sp
                                            )
                                            Text(
                                                text = "Link: ${r.quickAccessDeepLink}",
                                                color = EmeraldPrimary,
                                                fontSize = 9.sp,
                                                maxLines = 1
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            OutlinedButton(
                                                onClick = { onOpenEmailPreview(r) },
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Icon(Icons.Default.Mail, contentDescription = null, tint = AmberGold, modifier = Modifier.size(11.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Email", color = Color.White, fontSize = 10.sp)
                                            }

                                            Button(
                                                onClick = {
                                                    val b = bookings.find { it.id == r.bookingId }
                                                    if (b != null) onViewTicket(b)
                                                },
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                shape = RoundedCornerShape(6.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(11.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Ticket", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Surface(
                            color = StadiumCardSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (language == Language.SO)
                                        "Mashiinka otomaatiga ah wuxuu baadhayaa ballamaha kulankoodu u dhow yahay 2 saac. Guji badhanka sare si aad u tijaabiso."
                                    else
                                        "Automated scheduler checks for kick-offs 2h away. Click '+ Simulate 2H Kick-off Match' to test automated delivery and Quick Access ticket deep links.",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
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

@Composable
fun WaitlistUserRow(
    queuePosition: Int,
    entry: WaitlistEntry,
    language: Language,
    onRemove: () -> Unit,
    onToggleStatus: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = if (queuePosition == 1) AmberGold else StadiumBorder,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.size(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "#$queuePosition",
                        color = if (queuePosition == 1) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.teamName.ifBlank { entry.customerName },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = if (entry.status == "NOTIFIED") EmeraldDark.copy(alpha = 0.4f) else Color(0x33F59E0B),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (entry.status == "NOTIFIED") "NOTIFIED" else "WAITING",
                            color = if (entry.status == "NOTIFIED") EmeraldPrimary else AmberGold,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Text(
                    text = "${entry.customerName} • ${entry.customerPhone}",
                    color = TextSecondary,
                    fontSize = 10.sp
                )

                if (entry.notes.isNotBlank()) {
                    Text(
                        text = "\"${entry.notes}\"",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                if (entry.createdTimeStr.isNotBlank()) {
                    Text(
                        text = "Joined: ${entry.createdTimeStr}",
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Call button
            IconButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${entry.customerPhone}"))
                    context.startActivity(intent)
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Toggle Notify
            IconButton(
                onClick = onToggleStatus,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (entry.status == "NOTIFIED") Icons.Default.CheckCircle else Icons.Default.Notifications,
                    contentDescription = "Toggle Notified",
                    tint = if (entry.status == "NOTIFIED") EmeraldPrimary else AmberGold,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Delete / Remove
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("delete_waitlist_${entry.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

