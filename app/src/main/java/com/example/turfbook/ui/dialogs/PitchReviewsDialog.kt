package com.example.turfbook.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.turfbook.data.model.BookingTimeHelper
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.Pitch
import com.example.turfbook.data.model.PitchReview
import com.example.turfbook.ui.theme.*

@Composable
fun PitchReviewsDialog(
    pitch: Pitch?,
    reviews: List<PitchReview>,
    language: Language,
    onDismiss: () -> Unit
) {
    if (pitch == null) return

    val pitchReviews = remember(pitch.id, reviews) {
        reviews.filter { it.pitchId == pitch.id }
    }
    val summary = remember(pitch.id, reviews) {
        BookingTimeHelper.calculateRatingSummary(pitch.id, reviews)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.82f)
                .padding(vertical = 16.dp)
                .testTag("pitch_reviews_dialog")
                .border(1.dp, StadiumBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == Language.SO) pitch.somaliName else pitch.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (language == Language.SO) "Faallooyinka & Qiimeynta Ciyaartoyda" else "Player & Captain Match Reviews",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Average Rating Score Card
                Surface(
                    color = StadiumCardSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, StadiumBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Big Number
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text(
                                text = if (summary.reviewCount > 0) "${summary.averageRating}" else "5.0",
                                color = AmberGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 34.sp
                            )
                            Row {
                                repeat(5) { i ->
                                    val filled = i < kotlin.math.round(if (summary.reviewCount > 0) summary.averageRating else 5.0)
                                    Icon(
                                        imageVector = if (filled) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = if (filled) AmberGold else TextMuted,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${summary.reviewCount} ${if (language == Language.SO) "faallo" else "reviews"}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Distribution Bars
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            (5 downTo 1).forEach { stars ->
                                val count = summary.distribution[stars] ?: 0
                                val fraction = if (summary.reviewCount > 0) count.toFloat() / summary.reviewCount else 0f

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${stars}★",
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        modifier = Modifier.width(18.dp)
                                    )
                                    LinearProgressIndicator(
                                        progress = { fraction },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = AmberGold,
                                        trackColor = StadiumBorder
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "$count",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        modifier = Modifier.width(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // List of Reviews
                if (pitchReviews.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (language == Language.SO) "Weli wax faallo ah looma reebin garoonkan" else "No reviews recorded for this pitch yet.",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (language == Language.SO) "Noqo kii ugu horreeyey ee qiimeeya kadib ciyaartaada!" else "Be the first to rate it after your match!",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(pitchReviews) { rev ->
                            Surface(
                                color = StadiumCardSurface,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(0.8.dp, StadiumBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = rev.customerName,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = rev.teamName,
                                                color = EmeraldPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Row {
                                                repeat(5) { i ->
                                                    Icon(
                                                        imageVector = if (i < rev.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                                        contentDescription = null,
                                                        tint = if (i < rev.rating) AmberGold else TextMuted,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = rev.date,
                                                color = TextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    if (rev.comment.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = rev.comment,
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                    }

                                    if (rev.tags.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            rev.tags.take(3).forEach { tag ->
                                                Surface(
                                                    color = EmeraldDark.copy(alpha = 0.25f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = tag,
                                                        color = EmeraldPrimary,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

                Spacer(modifier = Modifier.height(12.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (language == Language.SO) "Xidh" else "Close Reviews",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
