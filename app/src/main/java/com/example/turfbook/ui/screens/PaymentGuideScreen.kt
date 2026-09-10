package com.example.turfbook.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Payment
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
import com.example.turfbook.data.model.AppConfig
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.repository.TurfRepository
import com.example.turfbook.ui.theme.*

@Composable
fun PaymentGuideScreen(
    language: Language,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current

    fun copyText(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        onShowToast("Copied $text to clipboard!")
    }

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
                    text = if (language == Language.SO) "Hagaha Lacag-bixinta Mobile Money" else "Mobile Money Payment Guide",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Official Merchant Accounts: Telesom ZAAD & Somtel eDahab",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Telesom Zaad Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TELESOM ZAAD SERVICE",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFEF4444),
                            fontSize = 15.sp
                        )
                        Surface(
                            color = Color(0x33EF4444),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Merchant: ${AppConfig.ZAAD_MERCHANT}",
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == Language.SO)
                            "Sida loo bixiyo ZAAD:\n1. Geli koodhka: *880*${AppConfig.ZAAD_MERCHANT}*Lacagta#\n2. Xaqiiji magaca xarunta: 26 JSC Turf\n3. Geli sirtaada ZAAD si aad u dhamaystirto\n4. Nuulbee Tixraaca (TID) oo geli foomka ballanta."
                        else
                            "How to pay via ZAAD:\n1. Dial: *880*${AppConfig.ZAAD_MERCHANT}*Amount#\n2. Verify centre name: 26 JSC Turf\n3. Enter your ZAAD PIN to complete transaction\n4. Copy the Transaction ID (TID) and paste into booking form.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { copyText("ZAAD Merchant Code", "*880*${AppConfig.ZAAD_MERCHANT}#") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy ZAAD Dial (*880*${AppConfig.ZAAD_MERCHANT}#)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Somtel eDahab Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFF97316).copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SOMTEL eDAHAB",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFF97316),
                            fontSize = 15.sp
                        )
                        Surface(
                            color = Color(0x33F97316),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Merchant: ${AppConfig.EDAHAB_MERCHANT}",
                                color = Color(0xFFF97316),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == Language.SO)
                            "Sida loo bixiyo eDahab:\n1. Geli koodhka: *789*${AppConfig.EDAHAB_MERCHANT}*Lacagta#\n2. Xaqiiji magaca xarunta: 26 JSC Turf\n3. Geli sirtaada eDahab\n4. Qabo lambarka tixraaca ee SMS-ka ku yimaada."
                        else
                            "How to pay via eDahab:\n1. Dial: *789*${AppConfig.EDAHAB_MERCHANT}*Amount#\n2. Verify centre name: 26 JSC Turf\n3. Enter your eDahab PIN\n4. Keep the confirmation SMS transaction code.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { copyText("eDahab Merchant Code", "*789*${AppConfig.EDAHAB_MERCHANT}#") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy eDahab Dial (*789*${AppConfig.EDAHAB_MERCHANT}#)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Standard Pricing Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Standard Pitch Pricing Matrix:", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("☀️ Daytime Game (06:00 - 18:00)", color = TextSecondary, fontSize = 12.sp)
                        Text("$18 / hour", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("🌙 Night Floodlit (18:00 - 00:00)", color = TextSecondary, fontSize = 12.sp)
                        Text("$25 / hour", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Add-ons list
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Optional Match Add-Ons:", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    TurfRepository.ADD_ONS.forEach { addon ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(addon.name, color = TextSecondary, fontSize = 12.sp)
                            Text("+$${addon.price.toInt()}", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
