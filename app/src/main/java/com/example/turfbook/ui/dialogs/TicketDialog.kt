package com.example.turfbook.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.turfbook.data.model.AppConfig
import com.example.turfbook.data.model.Booking
import com.example.turfbook.data.model.BookingTimeHelper
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.PitchReview
import com.example.turfbook.ui.theme.*
import java.net.URLEncoder

@Composable
fun TicketDialog(
    booking: Booking?,
    language: Language,
    existingReview: PitchReview? = null,
    onDismiss: () -> Unit,
    onCancelBooking: (String) -> Unit,
    onRatePitch: (Booking) -> Unit = {}
) {
    if (booking == null) return
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = EmeraldDark.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "MATCH VOUCHER",
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Arena Header
                Text(
                    text = "26 JSC TURF ARENA",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Official Booking Confirmation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Ref Code Box
                Surface(
                    color = StadiumCardSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BOOKING REFERENCE CODE",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "#${booking.referenceCode}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = AmberGold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Details Table
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StadiumCardSurface)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pitch:", color = TextSecondary, fontSize = 12.sp)
                        Text(booking.pitchName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Date & Time:", color = TextSecondary, fontSize = 12.sp)
                        Text("${booking.date} • ${booking.startTime} - ${booking.endTime}", color = AmberGold, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Club / Team:", color = TextSecondary, fontSize = 12.sp)
                        Text(booking.teamName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Captain / Booker:", color = TextSecondary, fontSize = 12.sp)
                        Text("${booking.customerName} (${booking.customerPhone})", color = TextPrimary, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Payment:", color = TextSecondary, fontSize = 12.sp)
                        Text("${booking.paymentMethod.name} (${booking.paymentStatus.uppercase()})", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    if (booking.transactionId.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TID:", color = TextSecondary, fontSize = 12.sp)
                            Text(booking.transactionId, color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }
                    HorizontalDivider(color = StadiumBorder, thickness = 0.5.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount Paid:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("$${booking.totalAmount.toInt()}", color = EmeraldPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == Language.SO) "Dhibcaha Daacadda:" else "Loyalty Points Earned:",
                            color = AmberGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        Surface(
                            color = AmberGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "+${if (booking.loyaltyPoints > 0) booking.loyaltyPoints else Booking.calculatePoints(booking.totalAmount)} PTS",
                                color = AmberGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (booking.referralCodeApplied.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == Language.SO) "Dhiirigelin Saaxiib:" else "Referral Bonus:",
                                color = EmeraldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${booking.referralCodeApplied} (+${booking.referralBonusPoints} pts 🎁)",
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Digital Ticket Quick Access Link & Automation Card
                Surface(
                    color = StadiumCardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ticket_quick_access_section")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (language == Language.SO) "Xidhiidhka Degdegga ah (Quick Access)" else "Digital Ticket Quick Access Link",
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = EmeraldPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "2H AUTOMATION",
                                    color = EmeraldPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (language == Language.SO)
                                "Xidhiidhkan tooska ah waxa loo soo diray email-kaaga 2 saac ka hor ciyaarta si aad degdeg ugu furato tigidhadan adigoon raadin."
                            else
                                "This verified digital ticket link is dispatched to your email 2 hours prior to kick-off for 1-tap fast access at pitch entrance.",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        val quickAccessUrl = remember(booking.id) { BookingTimeHelper.generateQuickAccessDeepLink(booking.id) }
                        Surface(
                            color = StadiumBgDark,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = quickAccessUrl,
                                color = AmberGold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Post-Match Pitch Rating Section
                val isTimePassed = BookingTimeHelper.isBookingTimePassed(booking.date, booking.endTime)
                if (isTimePassed) {
                    if (existingReview != null) {
                        Surface(
                            color = StadiumCardSurface,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (language == Language.SO) "Qiimeyntaadii Garoonka:" else "Your Pitch Review:",
                                        color = AmberGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Row {
                                        repeat(5) { starIndex ->
                                            Icon(
                                                imageVector = if (starIndex < existingReview.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = null,
                                                tint = if (starIndex < existingReview.rating) AmberGold else TextMuted,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }
                                if (existingReview.comment.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${existingReview.comment}\"",
                                        color = Color.White,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedButton(
                                    onClick = { onRatePitch(booking) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(32.dp)
                                        .testTag("ticket_edit_review_button"),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, AmberGold),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = if (language == Language.SO) "Wax ka beddel Qiimeynta" else "Update Rating",
                                        color = AmberGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = { onRatePitch(booking) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("ticket_rate_pitch_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(17.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.SO) "⭐ Qiimee Garoonka (Leave Review)" else "⭐ Rate This Pitch & Match",
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    Surface(
                        color = StadiumCardSurface.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.SO)
                                    "Qiimeynta garoonku waxay furmi doontaa ciyaarta kadib (${booking.endTime})"
                                else
                                    "Pitch rating unlocks after match ends (${booking.endTime})",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions: WhatsApp, Call, Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // WhatsApp Share
                    Button(
                        onClick = {
                            val cleanPhone = AppConfig.CONTACT_PHONE.replace("[^0-9]".toRegex(), "")
                            val msg = "⚽ *26 JSC TurfBook Booking Confirmation*\n" +
                                    "Ref: #${booking.referenceCode}\n" +
                                    "Pitch: ${booking.pitchName}\n" +
                                    "Date: ${booking.date} (${booking.startTime} - ${booking.endTime})\n" +
                                    "Team: ${booking.teamName} (Captain: ${booking.customerName})\n" +
                                    "Phone: ${booking.customerPhone}\n" +
                                    "Total: $${booking.totalAmount.toInt()}\n" +
                                    "Payment: ${booking.paymentMethod.name} (TID: ${booking.transactionId.ifBlank { "Pending" }})\n\n" +
                                    "As-Salaamu Alaykum, please verify our booking ticket!"
                            val uri = Uri.parse("https://wa.me/$cleanPhone?text=" + URLEncoder.encode(msg, "UTF-8"))
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Call Desk
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${AppConfig.CONTACT_PHONE}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == Language.SO) "Wac Xarunta" else "Call Desk", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Cancel booking text button
                TextButton(
                    onClick = { onCancelBooking(booking.id) },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Text(if (language == Language.SO) "Tirtir Ballantan" else "Cancel This Booking", fontSize = 12.sp)
                }
            }
        }
    }
}
