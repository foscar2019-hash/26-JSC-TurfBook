package com.example.turfbook.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.turfbook.data.model.*
import com.example.turfbook.data.repository.TurfRepository
import com.example.turfbook.ui.theme.*
import com.example.turfbook.ui.viewmodel.TurfViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog(
    isOpen: Boolean,
    pitches: List<Pitch>,
    initialPitchId: String,
    initialSlot: String?,
    language: Language,
    onDismiss: () -> Unit,
    onConfirm: (
        pitchId: String,
        date: String,
        slot: String,
        durationHours: Int,
        teamName: String,
        captainName: String,
        phone: String,
        email: String,
        paymentMethod: PaymentMethod,
        transactionId: String,
        selectedAddons: List<String>,
        notes: String
    ) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    var step by remember { mutableStateOf(1) }

    // Form states
    var selectedPitchId by remember { mutableStateOf(initialPitchId) }
    var selectedDate by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var selectedSlot by remember { mutableStateOf(initialSlot ?: "19:00 - 20:00") }
    var durationHours by remember { mutableIntStateOf(1) }
    var selectedAddons by remember { mutableStateOf(setOf<String>()) }

    var teamName by remember { mutableStateOf("") }
    var captainName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+25263") }
    var email by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var paymentMethod by remember { mutableStateOf(PaymentMethod.ZAAD) }
    var transactionId by remember { mutableStateOf("") }
    var copiedUssd by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val currentPitch = pitches.find { it.id == selectedPitchId } ?: pitches.firstOrNull()
    val isNight = TurfViewModel.isNightSlot(selectedSlot)
    val hourlyRate = if (isNight) (currentPitch?.nightRate ?: 25.0) else (currentPitch?.dayRate ?: 18.0)
    val addonsPrice = selectedAddons.sumOf { addId ->
        TurfRepository.ADD_ONS.find { it.id == addId }?.price ?: 0.0
    }
    val totalPrice = (hourlyRate * durationHours) + addonsPrice

    // Next 7 days
    val upcomingDates = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        for (i in 0..6) {
            list.add(sdf.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == Language.SO) "Dalbo Garoonka" else "Book Match Pitch",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (language == Language.SO) "Tallaabada $step ee 3" else "Step $step of 3",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EmeraldPrimary,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Step progress indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (s in 1..3) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (step >= s) EmeraldPrimary else StadiumBorder)
                        )
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Surface(
                        color = Color(0x33EF4444),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(errorMessage, color = Color(0xFFEF4444), fontSize = 12.sp)
                        }
                    }
                }

                // Step Content
                Box(modifier = Modifier.weight(1f)) {
                    when (step) {
                        1 -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                item {
                                    Text(
                                        text = if (language == Language.SO) "1. Dooro Garoonka" else "1. Select Pitch",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                }
                                items(pitches) { p ->
                                    val isSelected = p.id == selectedPitchId
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedPitchId = p.id }
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) EmeraldPrimary else StadiumBorder,
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) StadiumCardSurface else StadiumSurfaceDark
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (language == Language.SO) p.somaliName else p.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "${p.format.label} • ${p.surface.displayName}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = TextSecondary,
                                                    fontSize = 11.sp
                                                )
                                                Text(
                                                    text = "Day: $${p.dayRate.toInt()}/hr | Night Floodlit: $${p.nightRate.toInt()}/hr",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = AmberGold,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { selectedPitchId = p.id },
                                                colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                                            )
                                        }
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (language == Language.SO) "2. Dooro Taariikhda" else "2. Select Match Date",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        upcomingDates.take(4).forEach { d ->
                                            val isSel = d == selectedDate
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSel) EmeraldDark else StadiumCardSurface)
                                                    .clickable { selectedDate = d }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = d.substring(5), // MM-DD
                                                    color = if (isSel) Color.White else TextSecondary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (language == Language.SO) "3. Dooro Saacadda" else "3. Choose Time Slot",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        AppConfig.TIME_SLOTS.forEach { timeSlot ->
                                            val isSelectedSlot = timeSlot == selectedSlot
                                            val isSlotNight = TurfViewModel.isNightSlot(timeSlot)
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { selectedSlot = timeSlot }
                                                    .border(
                                                        width = if (isSelectedSlot) 1.5.dp else 0.5.dp,
                                                        color = if (isSelectedSlot) EmeraldPrimary else StadiumBorder,
                                                        shape = RoundedCornerShape(8.dp)
                                                    ),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSelectedSlot) StadiumCardSurface else StadiumSurfaceDark
                                                )
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = if (isSlotNight) Icons.Default.NightsStay else Icons.Default.WbSunny,
                                                            contentDescription = null,
                                                            tint = if (isSlotNight) NightFloodlightBlue else AmberGold,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(timeSlot, color = Color.White, fontSize = 13.sp)
                                                    }
                                                    Text(
                                                        text = if (isSlotNight) "Night Floodlit ($${currentPitch?.nightRate?.toInt() ?: 25})" else "Daytime ($${currentPitch?.dayRate?.toInt() ?: 18})",
                                                        color = if (isSlotNight) NightFloodlightBlue else TextSecondary,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                item {
                                    Text(
                                        text = if (language == Language.SO) "Adeegyada Dheeraadka ah (Add-Ons)" else "Match Add-Ons & Gear",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                }
                                items(TurfRepository.ADD_ONS) { addon ->
                                    val isChecked = selectedAddons.contains(addon.id)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedAddons = if (isChecked) selectedAddons - addon.id else selectedAddons + addon.id
                                            }
                                            .border(
                                                width = if (isChecked) 1.5.dp else 1.dp,
                                                color = if (isChecked) EmeraldPrimary else StadiumBorder,
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isChecked) StadiumCardSurface else StadiumSurfaceDark
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (language == Language.SO) addon.somaliName else addon.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = Color.White,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = addon.description,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = TextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            Text(
                                                text = "+$${addon.price.toInt()}",
                                                color = EmeraldPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            )
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    selectedAddons = if (checked) selectedAddons + addon.id else selectedAddons - addon.id
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                                            )
                                        }
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (language == Language.SO) "Xogta Kooxda & Kabtanka" else "Team & Contact Information",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = teamName,
                                        onValueChange = { teamName = it },
                                        label = { Text(if (language == Language.SO) "Magaca Kooxda (Team Name)*" else "Team / Club Name*") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EmeraldPrimary,
                                            unfocusedBorderColor = StadiumBorder
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = captainName,
                                        onValueChange = { captainName = it },
                                        label = { Text(if (language == Language.SO) "Magaca Kabtanka / Dalbadaha*" else "Captain / Booker Name*") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EmeraldPrimary,
                                            unfocusedBorderColor = StadiumBorder
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = phone,
                                        onValueChange = { phone = it },
                                        label = { Text(if (language == Language.SO) "Telefoonka Zaad / eDahab*" else "Phone Number (Zaad / eDahab)*") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EmeraldPrimary,
                                            unfocusedBorderColor = StadiumBorder
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = notes,
                                        onValueChange = { notes = it },
                                        label = { Text(if (language == Language.SO) "Fariin ama Codsi Gaar ah" else "Notes / Special Requests") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 2,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EmeraldPrimary,
                                            unfocusedBorderColor = StadiumBorder
                                        )
                                    )
                                }
                            }
                        }

                        3 -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                item {
                                    // Match Summary Ticket Card
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                                        colors = CardDefaults.cardColors(containerColor = StadiumCardSurface),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = currentPitch?.name ?: "Turf Pitch",
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "$${totalPrice.toInt()}",
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = EmeraldPrimary,
                                                    fontSize = 18.sp
                                                )
                                            }
                                            Text(
                                                text = "$selectedDate • $selectedSlot",
                                                color = AmberGold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "Team: $teamName (Captain: $captainName)",
                                                color = TextSecondary,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }

                                item {
                                    Text(
                                        text = if (language == Language.SO) "Dooro Habka Lacag-bixinta" else "Select Mobile Payment Gateway",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Zaad
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { paymentMethod = PaymentMethod.ZAAD }
                                                .border(
                                                    width = if (paymentMethod == PaymentMethod.ZAAD) 2.dp else 1.dp,
                                                    color = if (paymentMethod == PaymentMethod.ZAAD) EmeraldPrimary else StadiumBorder,
                                                    shape = RoundedCornerShape(10.dp)
                                                ),
                                            colors = CardDefaults.cardColors(containerColor = StadiumCardSurface)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text("ZAAD", fontWeight = FontWeight.Bold, color = ZaadRed, fontSize = 16.sp)
                                                Text("Merchant: 445686", color = TextSecondary, fontSize = 10.sp)
                                            }
                                        }

                                        // eDahab
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { paymentMethod = PaymentMethod.EDAHAB }
                                                .border(
                                                    width = if (paymentMethod == PaymentMethod.EDAHAB) 2.dp else 1.dp,
                                                    color = if (paymentMethod == PaymentMethod.EDAHAB) EmeraldPrimary else StadiumBorder,
                                                    shape = RoundedCornerShape(10.dp)
                                                ),
                                            colors = CardDefaults.cardColors(containerColor = StadiumCardSurface)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text("eDahab", fontWeight = FontWeight.Bold, color = EdahabOrange, fontSize = 16.sp)
                                                Text("Merchant: 10136", color = TextSecondary, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }

                                item {
                                    val ussdCode = if (paymentMethod == PaymentMethod.ZAAD) {
                                        "*880*${AppConfig.ZAAD_MERCHANT}*${totalPrice.toInt()}#"
                                    } else {
                                        "*789*${AppConfig.EDAHAB_MERCHANT}*${totalPrice.toInt()}#"
                                    }

                                    Surface(
                                        color = StadiumCardSurface,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = if (language == Language.SO) "Koodhka Tooska ah (USSD Dial):" else "Dial USSD Quick Code:",
                                                    fontSize = 11.sp,
                                                    color = TextSecondary
                                                )
                                                Text(
                                                    text = ussdCode,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 15.sp,
                                                    color = AmberGold
                                                )
                                            }
                                            Button(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("USSD Code", ussdCode))
                                                    copiedUssd = true
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (copiedUssd) "Copied!" else "Copy", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                item {
                                    OutlinedTextField(
                                        value = transactionId,
                                        onValueChange = { transactionId = it },
                                        label = {
                                            Text(if (language == Language.SO) "Lambarka Tixraaca (Transaction ID / TID)" else "Transaction ID (TID / Tixraac)")
                                        },
                                        placeholder = { Text("e.g. ZD-882910 or ED-10293") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EmeraldPrimary,
                                            unfocusedBorderColor = StadiumBorder
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (language == Language.SO)
                                            "Xusuusin: Haddii aadan hadda haysan TID, ballanta waa la xaqiijin doonaa iyadoo 'Pending Verification' ah."
                                        else
                                            "Note: If you haven't sent money yet, leave TID blank and complete payment at the centre desk.",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = {
                                errorMessage = ""
                                step--
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(StadiumBorder))
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == Language.SO) "Dib u noqo" else "Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            if (step == 1) {
                                errorMessage = ""
                                step = 2
                            } else if (step == 2) {
                                if (teamName.isBlank()) {
                                    errorMessage = if (language == Language.SO) "Fadlan geli magaca kooxda." else "Please enter your team name."
                                    return@Button
                                }
                                if (captainName.isBlank()) {
                                    errorMessage = if (language == Language.SO) "Fadlan geli magaca kabtanka." else "Please enter captain name."
                                    return@Button
                                }
                                if (phone.length < 8) {
                                    errorMessage = if (language == Language.SO) "Fadlan geli telefoon sax ah." else "Please enter a valid phone number."
                                    return@Button
                                }
                                errorMessage = ""
                                step = 3
                            } else if (step == 3) {
                                onConfirm(
                                    selectedPitchId,
                                    selectedDate,
                                    selectedSlot,
                                    durationHours,
                                    teamName.trim(),
                                    captainName.trim(),
                                    phone.trim(),
                                    email.trim(),
                                    paymentMethod,
                                    transactionId.trim(),
                                    selectedAddons.toList(),
                                    notes.trim()
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(
                            text = when (step) {
                                1 -> if (language == Language.SO) "Xiga (Kooxda)" else "Next: Team Info"
                                2 -> if (language == Language.SO) "U Gudub Lacagta" else "Proceed to Payment"
                                else -> if (language == Language.SO) "Xaqiiji Ballanta" else "Confirm Booking"
                            },
                            color = StadiumBgDark,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (step == 3) Icons.Default.CheckCircle else Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = StadiumBgDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
