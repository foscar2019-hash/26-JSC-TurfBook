package com.example.turfbook.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.turfbook.data.model.BookingTimeHelper
import com.example.turfbook.data.model.GameFormat
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.Pitch
import com.example.turfbook.data.model.PitchRatingSummary
import com.example.turfbook.data.model.PitchReview
import com.example.turfbook.ui.theme.*

@Composable
fun PitchesScreen(
    pitches: List<Pitch>,
    reviews: List<PitchReview> = emptyList(),
    selectedFormat: GameFormat,
    language: Language,
    onSelectFormat: (GameFormat) -> Unit,
    onBookPitch: (String) -> Unit,
    onViewReviews: (Pitch) -> Unit = {}
) {
    val filteredPitches = if (selectedFormat == GameFormat.ALL) {
        pitches
    } else {
        pitches.filter { it.format == selectedFormat }
    }

    val overallCount = reviews.size
    val overallAvg = if (reviews.isNotEmpty()) {
        kotlin.math.round(reviews.map { it.rating }.average() * 10) / 10.0
    } else 4.8

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StadiumBgDark)
    ) {
        // Overall Arena Rating Banner
        Surface(
            color = StadiumCardSurface,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, StadiumBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == Language.SO) "Garoomada 26 JSC:" else "26 JSC Turf Quality:",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "⭐ $overallAvg / 5.0",
                        color = AmberGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = if (language == Language.SO) "$overallCount qiimeyn" else "$overallCount verified reviews",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }

        // Formats filter chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(GameFormat.values()) { format ->
                val isSelected = format == selectedFormat
                Surface(
                    color = if (isSelected) EmeraldDark else StadiumCardSurface,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectFormat(format) }
                ) {
                    Text(
                        text = format.label,
                        color = if (isSelected) Color.White else TextSecondary,
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Pitches list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredPitches) { pitch ->
                val summary = remember(pitch.id, reviews) {
                    BookingTimeHelper.calculateRatingSummary(pitch.id, reviews)
                }
                PitchCard(
                    pitch = pitch,
                    ratingSummary = summary,
                    language = language,
                    onBookClick = { onBookPitch(pitch.id) },
                    onViewReviews = { onViewReviews(pitch) }
                )
            }
        }
    }
}

@Composable
fun PitchCard(
    pitch: Pitch,
    ratingSummary: PitchRatingSummary,
    language: Language,
    onBookClick: () -> Unit,
    onViewReviews: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StadiumBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Pitch Image with overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = pitch.imageUrl,
                    contentDescription = pitch.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC090E11)),
                                startY = 80f
                            )
                        )
                )

                // Top badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = EmeraldDark.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = pitch.format.label,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Average Rating Pill on top-right of image
                    Surface(
                        onClick = onViewReviews,
                        color = Color(0xE60F172A),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.6f)),
                        modifier = Modifier.testTag("pitch_rating_badge_${pitch.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = AmberGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (ratingSummary.reviewCount > 0) "${ratingSummary.averageRating}" else "5.0",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                            if (ratingSummary.reviewCount > 0) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "(${ratingSummary.reviewCount})",
                                    color = AmberGold.copy(alpha = 0.9f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Surface(
                        color = if (pitch.status == "available") Color(0xCC10B981) else Color(0xCCE11D48),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (pitch.status == "available") {
                                if (language == Language.SO) "Bannaan" else "Available"
                            } else {
                                if (language == Language.SO) "Dayactir" else "Maintenance"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Surface Pill on bottom-left
                Surface(
                    color = Color(0xBB000000),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Grass,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = pitch.surface.displayName,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Body
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (language == Language.SO) pitch.somaliName else pitch.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Interactive Rating & Comment Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StadiumCardSurface)
                        .clickable { onViewReviews() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("pitch_reviews_row_${pitch.id}"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val displayScore = if (ratingSummary.reviewCount > 0) ratingSummary.averageRating else 5.0
                        repeat(5) { i ->
                            val filled = i < kotlin.math.round(displayScore)
                            Icon(
                                imageVector = if (filled) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (filled) AmberGold else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (ratingSummary.reviewCount > 0) "${ratingSummary.averageRating} / 5.0" else "5.0 (New)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (language == Language.SO)
                                "${ratingSummary.reviewCount} faallo"
                            else
                                "${ratingSummary.reviewCount} reviews",
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dimensions and Capacity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AspectRatio, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(pitch.dimensions, color = TextSecondary, fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(pitch.capacity, color = TextSecondary, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rate Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = StadiumCardSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = AmberGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text("Daytime Rate", color = TextMuted, fontSize = 9.sp)
                                Text("$${pitch.dayRate.toInt()}/hour", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    Surface(
                        color = StadiumCardSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NightsStay, contentDescription = null, tint = NightFloodlightBlue, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text("Night Floodlit", color = TextMuted, fontSize = 9.sp)
                                Text("$${pitch.nightRate.toInt()}/hour", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Features
                pitch.features.take(3).forEach { feat ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(feat, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Book Button
                Button(
                    onClick = onBookClick,
                    enabled = pitch.status == "available",
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        disabledContainerColor = StadiumBorder
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = StadiumBgDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (pitch.status == "available") {
                            if (language == Language.SO) "Hadda Dalbo Garoonkan" else "Book This Pitch"
                        } else {
                            if (language == Language.SO) "Garoonku Wuu Xidhan yahay" else "Pitch Under Maintenance"
                        },
                        color = StadiumBgDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
