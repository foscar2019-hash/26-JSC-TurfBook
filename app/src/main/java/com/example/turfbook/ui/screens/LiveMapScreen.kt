package com.example.turfbook.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turfbook.data.model.AppConfig
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.Pitch
import com.example.turfbook.ui.theme.*

@Composable
fun LiveMapScreen(
    pitches: List<Pitch>,
    language: Language
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(StadiumBgDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = if (language == Language.SO) "Goobta & Khariidadda Xarunta" else "Arena Location & Facility Map",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (language == Language.SO) AppConfig.LOCATION_SO else AppConfig.LOCATION_EN,
                    color = AmberGold,
                    fontSize = 12.sp
                )
            }
        }

        item {
            // Interactive visual layout diagram of the 26 JSC Arena
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STADIUM SCHEMATIC LAYOUT",
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            color = EmeraldDark.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "4 Floodlit Pitches",
                                color = EmeraldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pitch 1 & Pitch 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Pitch 1 Card
                        Surface(
                            color = StadiumCardSurface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, EmeraldDark, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Pitch 1", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Championship Arena", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                Text("7-a-side • 50x30m", color = TextSecondary, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                                    Text("LED Tower Alpha", color = AmberGold, fontSize = 9.sp)
                                }
                            }
                        }

                        // Pitch 2 Card
                        Surface(
                            color = StadiumCardSurface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, EmeraldDark, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Pitch 2", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Premier Astro Turf", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                Text("5-a-side • 38x22m", color = TextSecondary, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                                    Text("LED Tower Beta", color = AmberGold, fontSize = 9.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Center Amenities Walkway
                    Surface(
                        color = Color(0xFF1E2E28),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("☕ Cafe & Drinks", color = Color.White, fontSize = 10.sp)
                            Text("•", color = TextMuted)
                            Text("🕌 Prayer Hall", color = Color.White, fontSize = 10.sp)
                            Text("•", color = TextMuted)
                            Text("🚿 Changing Rooms", color = Color.White, fontSize = 10.sp)
                            Text("•", color = TextMuted)
                            Text("🎟️ Reception", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pitch 3 & Pitch 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Pitch 3 Card
                        Surface(
                            color = StadiumCardSurface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, EmeraldDark, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Pitch 3", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("VIP Floodlight Turf", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                Text("6-a-side • 44x26m", color = TextSecondary, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                                    Text("LED Tower Gamma", color = AmberGold, fontSize = 9.sp)
                                }
                            }
                        }

                        // Pitch 4 Card
                        Surface(
                            color = StadiumCardSurface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, EmeraldDark, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Pitch 4", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Skills & Futsal Cage", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                Text("5-a-side • 34x20m", color = TextSecondary, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                                    Text("High-Lumen Futsal Cage", color = AmberGold, fontSize = 9.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Parking and Gate
                    Surface(
                        color = StadiumCardSurface,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Front Main Entrance Gate & Free Secure Vehicle Parking", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        item {
            // Direction Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val gmmIntentUri = Uri.parse("geo:${AppConfig.LOCATION_COORDS}?q=${AppConfig.LOCATION_COORDS}(26+JSC+Turf+Arena)")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = StadiumBgDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (language == Language.SO) "Tilmaamaha Khariidadda" else "Get Directions", color = StadiumBgDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${AppConfig.CONTACT_PHONE}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = StadiumBgDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (language == Language.SO) "Wac Xarunta" else "Call Front Desk", color = StadiumBgDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Facility Amenities & Services:", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "High-Power LED Stadium Floodlights (Open until midnight)",
                        "Clean prayer area & wudu facilities",
                        "Dedicated locker rooms & cold shower booths",
                        "Cafeteria serving cold energy drinks, juices & tea",
                        "Free high-speed WiFi for captains & spectators",
                        "Guarded complimentary parking for cars & motorbikes"
                    ).forEach { amen ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(amen, color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
