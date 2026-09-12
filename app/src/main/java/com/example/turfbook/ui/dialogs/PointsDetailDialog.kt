package com.example.turfbook.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.LoyaltyPointsRecord
import com.example.turfbook.data.model.PointsTransactionType
import com.example.turfbook.ui.theme.*
import java.util.Locale

@Composable
fun PointsDetailDialog(
    record: LoyaltyPointsRecord?,
    language: Language,
    onDismiss: () -> Unit
) {
    if (record == null) return

    val typeColor = when (record.type) {
        PointsTransactionType.BOOKING -> EmeraldPrimary
        PointsTransactionType.REFERRAL_BONUS -> AmberGold
        PointsTransactionType.SPECIAL_EVENT -> Color(0xFF818CF8) // Electric Indigo / Purple
        PointsTransactionType.PROMO_BONUS -> Color(0xFFF472B6)
    }

    val typeIcon = when (record.type) {
        PointsTransactionType.BOOKING -> Icons.Default.SportsSoccer
        PointsTransactionType.REFERRAL_BONUS -> Icons.Default.CardGiftcard
        PointsTransactionType.SPECIAL_EVENT -> Icons.Default.EmojiEvents
        PointsTransactionType.PROMO_BONUS -> Icons.Default.AutoAwesome
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .border(1.2.dp, typeColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = typeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, typeColor.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = typeIcon,
                                contentDescription = null,
                                tint = typeColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (language == Language.SO) record.type.labelSo.uppercase() else record.type.labelEn.uppercase(),
                                color = typeColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hero Points Awarded Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = StadiumCardSurface,
                    border = BorderStroke(1.dp, typeColor.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        typeColor.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (language == Language.SO) "DHIBCAHA LA XAREEYAY (EARNED)" else "POINTS CREDITED TO ACCOUNT",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "+${record.totalPoints}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 32.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "PTS",
                                    color = AmberGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Text(
                                text = "Ref: #${record.referenceCode}",
                                color = AmberGold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transaction Title & Subtitle
                Text(
                    text = if (language == Language.SO) record.titleSo else record.titleEn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = if (language == Language.SO) record.subtitleSo else record.subtitleEn,
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = StadiumBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Detailed Points Calculation Ledger
                Text(
                    text = if (language == Language.SO) "Faahfaahinta Xisaabinta Dhibcaha:" else "Itemized Points Calculation:",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = StadiumCardSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, StadiumBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Date & Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (language == Language.SO) "Taariikhda & Waqtiga" else "Date & Time",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${record.date} ${record.time}".trim(),
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }

                        // Amount spent if booking
                        if (record.amountSpent != null && record.amountSpent > 0.0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (language == Language.SO) "Lacagta la bixiyay" else "Amount Paid",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", record.amountSpent)} USD",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Base Points
                        if (record.basePoints > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (language == Language.SO) "Dhibcaha Asalka ah (10 pts/$1)" else "Base Points (10 pts / $1)",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "+${record.basePoints} pts",
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Bonus Points
                        if (record.bonusPoints > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val bonusLabel = when (record.type) {
                                    PointsTransactionType.REFERRAL_BONUS -> if (language == Language.SO) "Gunada Casuumadda" else "Referral Bonus"
                                    PointsTransactionType.SPECIAL_EVENT -> if (language == Language.SO) "Gunada Munaasabadda" else "Special Event Bonus"
                                    else -> if (language == Language.SO) "Gunno Dheeraad ah" else "Promo Bonus"
                                }
                                Text(
                                    text = bonusLabel,
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "+${record.bonusPoints} pts",
                                    color = AmberGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        HorizontalDivider(color = StadiumBorder, thickness = 0.5.dp)

                        // Net Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == Language.SO) "Wadarta Dhibcaha Lagu Daray" else "Net Points Credited",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "+${record.totalPoints} PTS",
                                color = AmberGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Note Description
                val detailText = if (language == Language.SO) record.detailSo else record.detailEn
                if (detailText.isNotBlank()) {
                    Surface(
                        color = StadiumBgDark,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = typeColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = detailText,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Official Seal / Trust Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Verified 26 JSC TurfBook Loyalty Ledger",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dismiss Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = StadiumCardSurface),
                    border = BorderStroke(1.dp, StadiumBorder),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (language == Language.SO) "Xidh Faahfaahinta" else "Close Ledger Entry",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
