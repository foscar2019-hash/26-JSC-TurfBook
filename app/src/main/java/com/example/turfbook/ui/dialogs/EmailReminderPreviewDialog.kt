package com.example.turfbook.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.turfbook.data.model.AppConfig
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.TwoHourBookingReminder
import com.example.turfbook.data.notification.TwoHourBookingReminderManager
import com.example.turfbook.ui.theme.*

@Composable
fun EmailReminderPreviewDialog(
    reminder: TwoHourBookingReminder?,
    language: Language,
    onDismiss: () -> Unit,
    onOpenTicket: () -> Unit
) {
    if (reminder == null) return
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 16.dp)
                .testTag("email_reminder_preview_dialog"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = EmeraldDark.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mail,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == Language.SO) "EMAIL OGAYSIIS: 2 SAACADOOD KA HOR" else "AUTOMATED 2-HOUR EMAIL",
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Email Metadata Header Box
                Surface(
                    color = StadiumCardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(0.6.dp, StadiumBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "To: ",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = reminder.customerEmail,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "From: ",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "reminders@26jsc.so (26 JSC Operations)",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Status: ",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = EmeraldDark.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "DISPATCHED AUTOMATICALLY (2H WINDOW)",
                                    color = EmeraldPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = StadiumBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = reminder.emailSubject,
                            color = AmberGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Email Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Visual Email Container (mimicking an email newsletter/message)
                    Surface(
                        color = Color(0xFF0C1711),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, EmeraldDark.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Arena Branding banner inside email
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SportsSoccer,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "26 JSC TURF ARENA",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Surface(
                                    color = AmberGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "⚡ IN 2 HOURS",
                                        color = AmberGold,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Dear ${reminder.customerName},",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your turf booking is starting in 2 hours! Please review your match details and use the Quick Access link below to present your digital ticket at the turnstile.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Match Details Box inside Email
                            Surface(
                                color = StadiumSurfaceDark,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.5.dp, StadiumBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("PITCH", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            Text(reminder.pitchName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("REF CODE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            Text("#${reminder.bookingReference}", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("DATE & KICKOFF", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            Text("${reminder.date} • ${reminder.startTime} - ${reminder.endTime}", color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("TEAM", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            Text(reminder.teamName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Prominent Quick Access Digital Ticket Button in Email
                            Button(
                                onClick = onOpenTicket,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("email_quick_access_ticket_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = StadiumBgDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🎟️ QUICK ACCESS: DIGITAL TICKET",
                                    color = StadiumBgDark,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick Access Link Display Box
                            Surface(
                                color = StadiumCardSurface,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.5.dp, StadiumBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("TurfBook Quick Access Link", reminder.quickAccessLink))
                                        Toast.makeText(context, "Quick Access link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("QUICK ACCESS LINK (CLICK TO COPY)", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = reminder.quickAccessLink,
                                            color = TextSecondary,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            maxLines = 1
                                        )
                                    }
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "MATCHDAY INSTRUCTIONS:",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. Please arrive at 26 JSC Arena 15 minutes before kick-off for team check-in.\n2. Present your digital ticket QR code at the turnstile.\n3. Bibs, match ball, and pitch floodlights are ready.\n4. Front desk assistance: ${AppConfig.CONTACT_PHONE}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Open in Email App
                    OutlinedButton(
                        onClick = {
                            val emailIntent = TwoHourBookingReminderManager.createEmailClientIntent(reminder)
                            try {
                                context.startActivity(emailIntent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "No email client found", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, StadiumBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(15.dp), tint = AmberGold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == Language.SO) "Fur Mail App" else "Open in Mail",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Direct Quick Access Button
                    Button(
                        onClick = {
                            onDismiss()
                            onOpenTicket()
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = StadiumBgDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == Language.SO) "Fur Tigidhka" else "Open Ticket",
                            color = StadiumBgDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
