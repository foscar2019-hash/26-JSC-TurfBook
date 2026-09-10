package com.example.turfbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turfbook.data.model.Language
import com.example.turfbook.ui.theme.*

data class NavItem(
    val titleEn: String,
    val titleSo: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val NAV_ITEMS = listOf(
    NavItem("Pitches", "Garoomada", Icons.Default.SportsSoccer),
    NavItem("Schedule", "Jadwalka", Icons.Default.CalendarMonth),
    NavItem("Bookings", "Ballamaha", Icons.Default.ConfirmationNumber),
    NavItem("Teams", "Kooxaha", Icons.Default.Groups),
    NavItem("Gallery", "Sawirrada", Icons.Default.PhotoLibrary),
    NavItem("Map", "Khariidada", Icons.Default.Map),
    NavItem("Payment", "Lacagta", Icons.Default.Payment),
    NavItem("Admin", "Maamulka", Icons.Default.AdminPanelSettings)
)

@Composable
fun TurfBottomNav(
    selectedIndex: Int,
    bookingsCount: Int,
    language: Language,
    onSelectTab: (Int) -> Unit
) {
    NavigationBar(
        containerColor = StadiumSurfaceDark,
        tonalElevation = 8.dp
    ) {
        NAV_ITEMS.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectTab(index) },
                icon = {
                    BadgedBox(badge = {
                        if (index == 2 && bookingsCount > 0) {
                            Badge(
                                containerColor = AmberGold,
                                contentColor = StadiumBgDark
                            ) {
                                Text(bookingsCount.toString(), fontSize = 10.sp)
                            }
                        }
                    }) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.titleEn,
                            modifier = Modifier.size(20.dp),
                            tint = if (isSelected) EmeraldPrimary else TextSecondary
                        )
                    }
                },
                label = {
                    Text(
                        text = if (language == Language.SO) item.titleSo else item.titleEn,
                        fontSize = 10.sp,
                        maxLines = 1,
                        color = if (isSelected) EmeraldPrimary else TextSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = EmeraldDark.copy(alpha = 0.25f)
                )
            )
        }
    }
}
