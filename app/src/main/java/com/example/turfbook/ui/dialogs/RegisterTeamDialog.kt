package com.example.turfbook.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.turfbook.data.model.GameFormat
import com.example.turfbook.data.model.Language
import com.example.turfbook.ui.theme.*

@Composable
fun RegisterTeamDialog(
    isOpen: Boolean,
    language: Language,
    onDismiss: () -> Unit,
    onRegister: (
        name: String,
        logoEmoji: String,
        color: String,
        captainName: String,
        captainPhone: String,
        format: GameFormat,
        skillLevel: String,
        bio: String
    ) -> Unit
) {
    if (!isOpen) return

    var teamName by remember { mutableStateOf("") }
    var logoEmoji by remember { mutableStateOf("⚽") }
    var captainName by remember { mutableStateOf("") }
    var captainPhone by remember { mutableStateOf("+25263") }
    var format by remember { mutableStateOf(GameFormat.SEVEN_A_SIDE) }
    var skillLevel by remember { mutableStateOf("Competitive") }
    var bio by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

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
                        text = if (language == Language.SO) "Diiwaangeli Koox Cusub" else "Register New Club / Team",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                if (error.isNotEmpty()) {
                    Text(error, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                }

                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Club Name*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = StadiumBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = logoEmoji,
                        onValueChange = { logoEmoji = it },
                        label = { Text("Badge Emoji") },
                        modifier = Modifier.weight(0.4f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = StadiumBorder
                        )
                    )
                    OutlinedTextField(
                        value = captainName,
                        onValueChange = { captainName = it },
                        label = { Text("Captain Name*") },
                        modifier = Modifier.weight(0.6f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = StadiumBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = captainPhone,
                    onValueChange = { captainPhone = it },
                    label = { Text("Captain Phone*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = StadiumBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Squad Bio / Training Schedule") },
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
                        if (teamName.isBlank()) {
                            error = "Please enter club name"
                            return@Button
                        }
                        if (captainName.isBlank()) {
                            error = "Please enter captain name"
                            return@Button
                        }
                        onRegister(
                            teamName.trim(),
                            logoEmoji.trim().ifBlank { "⚽" },
                            "#10B981",
                            captainName.trim(),
                            captainPhone.trim(),
                            format,
                            skillLevel,
                            bio.trim()
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text(if (language == Language.SO) "Diiwaangeli Kooxda" else "Complete Registration", color = StadiumBgDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
