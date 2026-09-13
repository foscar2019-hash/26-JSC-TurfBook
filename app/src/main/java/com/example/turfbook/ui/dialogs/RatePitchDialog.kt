package com.example.turfbook.ui.dialogs

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.turfbook.data.model.Booking
import com.example.turfbook.data.model.Language
import com.example.turfbook.ui.theme.*

@Composable
fun RatePitchDialog(
    booking: Booking?,
    language: Language,
    onDismiss: () -> Unit,
    onSubmitReview: (booking: Booking, rating: Int, comment: String, tags: List<String>) -> Unit
) {
    if (booking == null) return

    var selectedRating by remember { mutableIntStateOf(5) }
    var commentText by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<String>() }

    val popularTags = remember(language) {
        if (language == Language.SO) {
            listOf(
                "⚽ Caws Casri ah (Smooth Turf)",
                "💡 Ileys Aad u Fiican (Bright Lights)",
                "⏱️ Xilli Ku Bilaabasho (Prompt Start)",
                "🧼 Benches Nadiif ah (Clean Dugouts)",
                "🤝 Soo Dhaweyn Wanaagsan (Friendly Staff)",
                "🛡️ Badbaado Fiican (Joint-Safe)"
            )
        } else {
            listOf(
                "⚽ Smooth Turf Bounce",
                "💡 Bright Floodlights",
                "⏱️ Prompt Kickoff",
                "🧼 Clean Team Dugouts",
                "🤝 Friendly Reception",
                "🛡️ Joint-Safe Shock Pad"
            )
        }
    }

    val ratingLabel = when (selectedRating) {
        1 -> if (language == Language.SO) "1/5 - Garoonku Wuu Liitaa" else "1/5 - Needs Improvement"
        2 -> if (language == Language.SO) "2/5 - Dhexdhexaad Hoose" else "2/5 - Fair Quality"
        3 -> if (language == Language.SO) "3/5 - Wanaagsan" else "3/5 - Good Standard"
        4 -> if (language == Language.SO) "4/5 - Aad u Wanaagsan" else "4/5 - Very Good Turf"
        5 -> if (language == Language.SO) "5/5 - Heer Caalami / Heer Sare" else "5/5 - Outstanding Arena"
        else -> ""
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
                .testTag("rate_pitch_dialog")
                .border(1.dp, StadiumBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                            color = AmberGold.copy(alpha = 0.18f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == Language.SO) "Qiimee Garoonka" else "Rate Your Pitch",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (language == Language.SO) "Wadaag khibraddaadii ciyaarta" else "Share post-match feedback",
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

                // Match details summary card
                Surface(
                    color = StadiumCardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, StadiumBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = booking.pitchName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${booking.date} • ${booking.startTime} - ${booking.endTime}",
                                color = AmberGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "#${booking.referenceCode}",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Team: ${booking.teamName}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Star Rating Picker
                Text(
                    text = if (language == Language.SO) "Dooro Xiddigaha (Star Rating):" else "Star Rating:",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { star ->
                        val isSelected = star <= selectedRating
                        val starColor by animateColorAsState(
                            targetValue = if (isSelected) AmberGold else Color(0xFF334155),
                            label = "starColor"
                        )
                        IconButton(
                            onClick = { selectedRating = star },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("star_rating_$star")
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$star stars",
                                tint = starColor,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // Rating qualitative label
                Text(
                    text = ratingLabel,
                    color = AmberGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Tag Chips
                Text(
                    text = if (language == Language.SO) "Maxaa ku cajabiyey? (Dooro):" else "What stood out? (Tap tags):",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    popularTags.chunked(2).forEach { rowTags ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowTags.forEach { tag ->
                                val isTagSelected = selectedTags.contains(tag)
                                Surface(
                                    onClick = {
                                        if (isTagSelected) {
                                            selectedTags.remove(tag)
                                        } else {
                                            selectedTags.add(tag)
                                        }
                                    },
                                    color = if (isTagSelected) EmeraldPrimary.copy(alpha = 0.2f) else StadiumCardSurface,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isTagSelected) EmeraldPrimary else StadiumBorder
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = tag,
                                        color = if (isTagSelected) EmeraldPrimary else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = if (isTagSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Comment input
                Text(
                    text = if (language == Language.SO) "Faalladaada (Optional):" else "Your Comment & Feedback:",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("review_comment_input"),
                    placeholder = {
                        Text(
                            text = if (language == Language.SO)
                                "Ka faallood tayada cawska, iftiinka habeenkii, kubbadaha, kuraasta..."
                            else
                                "Share how the turf felt, lighting quality, ball bounce, reception, or facilities...",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = StadiumBorder,
                        focusedContainerColor = StadiumCardSurface,
                        unfocusedContainerColor = StadiumCardSurface
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        val finalComment = if (commentText.isBlank() && selectedTags.isNotEmpty()) {
                            selectedTags.joinToString(", ")
                        } else {
                            commentText
                        }
                        onSubmitReview(booking, selectedRating, finalComment, selectedTags.toList())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_review_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == Language.SO) "Gudbi Qiimeynta (Submit)" else "Submit Rating & Review",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
