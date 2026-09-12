package com.example.turfbook.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.turfbook.data.model.Language
import com.example.turfbook.ui.theme.*

@Composable
fun InviteFriendDialog(
    isOpen: Boolean,
    userReferralCode: String,
    language: Language,
    onDismiss: () -> Unit,
    onSendInvite: (friendName: String, friendPhone: String) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    var friendName by remember { mutableStateOf("") }
    var friendPhone by remember { mutableStateOf("+25263") }
    var copiedCode by remember { mutableStateOf(false) }

    val inviteMessage = if (language == Language.SO) {
        "⚽ Kusoo biir 26 JSC TurfBook garoomada casriga ah ee Hargeisa! Isticmaal koodhkayga casuumadda *$userReferralCode* markaad ballansanaysid ciyaartaada ugu horreysa waxaan helaynaa labadeenuba +150 dhibco loyalty ah! 🏆"
    } else {
        "⚽ Join me on 26 JSC TurfBook to book premier floodlit football pitches in Hargeisa! Use my referral code *$userReferralCode* on your first pitch booking to get +150 bonus loyalty points for both of us! 🏆"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                        Surface(
                            color = EmeraldPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GroupAdd,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (language == Language.SO) "Casuum Saaxiibbada" else "Invite Friends",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (language == Language.SO) "Hel +150 dhibcood saaxiibkiiba" else "Earn +150 pts per friend",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmberGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Explanatory Card
                Surface(
                    color = StadiumCardSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, StadiumBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (language == Language.SO)
                                "Sida ay u shaqayso:"
                            else
                                "How it works:",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == Language.SO)
                                "1. La wadaag koodhkaaga saaxiibadaada ama kooxaha kale.\n2. Markay galaan koodhka oo ay ballansadaan ciyaartooda ugu horreysa...\n3. Labadiinuba waxaad helaysaan +150 Dhibco Daacad ah oo degdeg ah!"
                            else
                                "1. Share your referral code with football friends or rival teams.\n2. When they apply your code and complete their first match booking...\n3. Both of you receive +150 bonus loyalty points immediately!",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Referral Code Highlight
                Text(
                    text = if (language == Language.SO) "Koodhkaaga Gaarka ah (Your Code)" else "Your Unique Referral Code",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = EmeraldDark.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, EmeraldPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = userReferralCode,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("TurfBook Referral Code", userReferralCode)
                                    clipboard.setPrimaryClip(clip)
                                    copiedCode = true
                                    Toast.makeText(context, if (language == Language.SO) "Koodhka waa la koobiyeeyay!" else "Referral code copied!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = EmeraldPrimary.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = if (copiedCode) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (copiedCode) (if (language == Language.SO) "Waa la Koobiyey" else "Copied") else (if (language == Language.SO) "Koobi" else "Copy"),
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // WhatsApp & System Share
                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, inviteMessage)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, if (language == Language.SO) "La wadaag Saaxiib" else "Invite via WhatsApp")
                        context.startActivity(shareIntent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == Language.SO) "Ku Wadaag WhatsApp / Fariin" else "Share via WhatsApp / Messages",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = StadiumBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Direct Friend Invite Form
                Text(
                    text = if (language == Language.SO) "Ama toos ugu dar liiskaaga" else "Or Log a Friend Referral Directly",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = friendName,
                    onValueChange = { friendName = it },
                    label = { Text(if (language == Language.SO) "Magaca Saaxiibka (Friend Name)" else "Friend's Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = StadiumBorder
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = friendPhone,
                    onValueChange = { friendPhone = it },
                    label = { Text(if (language == Language.SO) "Telefoonka Saaxiibka (Zaad/eDahab)" else "Friend's Phone (Zaad/eDahab)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = StadiumBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (friendName.isNotBlank() && friendPhone.length >= 8) {
                            onSendInvite(friendName.trim(), friendPhone.trim())
                        } else {
                            Toast.makeText(
                                context,
                                if (language == Language.SO) "Fadlan buuxi magaca iyo telefoonka saaxiibka" else "Please fill friend's name and valid phone",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = StadiumBgDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == Language.SO) "Diiwaangeli Casuumadda (+150 pts)" else "Register Friend Invite (+150 pts)",
                        color = StadiumBgDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
