package com.example.turfbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turfbook.ui.components.SmsNotificationToast
import com.example.turfbook.ui.components.TurfBottomNav
import com.example.turfbook.ui.components.TurfTopBar
import com.example.turfbook.ui.dialogs.BookingDialog
import com.example.turfbook.ui.dialogs.ChallengeDialog
import com.example.turfbook.ui.dialogs.RegisterTeamDialog
import com.example.turfbook.ui.dialogs.TicketDialog
import com.example.turfbook.ui.screens.*
import com.example.turfbook.ui.theme.EmeraldPrimary
import com.example.turfbook.ui.theme.StadiumBgDark
import com.example.turfbook.ui.theme.StadiumSurfaceDark
import com.example.turfbook.ui.theme.TurfBookTheme
import com.example.turfbook.ui.viewmodel.TurfViewModel
import com.example.turfbook.ui.viewmodel.TurfViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: TurfViewModel by viewModels {
        TurfViewModelFactory((application as TurfApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TurfBookTheme {
                TurfBookApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurfBookApp(viewModel: TurfViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val pitches by viewModel.pitches.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val blockedSlots by viewModel.blockedSlots.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val galleryImages by viewModel.galleryImages.collectAsState()

    Scaffold(
        topBar = {
            TurfTopBar(
                language = uiState.language,
                onToggleLanguage = { viewModel.toggleLanguage() }
            )
        },
        bottomBar = {
            TurfBottomNav(
                selectedIndex = uiState.activeTab,
                bookingsCount = bookings.size,
                language = uiState.language,
                onSelectTab = { viewModel.setTab(it) }
            )
        },
        floatingActionButton = {
            // Quick Book FAB
            FloatingActionButton(
                onClick = { viewModel.openBooking() },
                containerColor = EmeraldPrimary,
                contentColor = StadiumBgDark
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Quick Book")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Book", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = StadiumBgDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main tab content
            when (uiState.activeTab) {
                0 -> PitchesScreen(
                    pitches = pitches,
                    selectedFormat = uiState.selectedFormat,
                    language = uiState.language,
                    onSelectFormat = { viewModel.setFormatFilter(it) },
                    onBookPitch = { pitchId -> viewModel.openBooking(pitchId = pitchId) }
                )
                1 -> ScheduleScreen(
                    pitches = pitches,
                    bookings = bookings,
                    blockedSlots = blockedSlots,
                    selectedPitchId = uiState.selectedPitchId,
                    selectedDate = uiState.selectedDate,
                    language = uiState.language,
                    onSelectPitch = { viewModel.setSelectedPitchId(it) },
                    onSelectDate = { viewModel.setDate(it) },
                    onBookSlot = { pitchId, slot -> viewModel.openBooking(pitchId = pitchId, initialSlot = slot) }
                )
                2 -> MyBookingsScreen(
                    bookings = bookings,
                    language = uiState.language,
                    onViewTicket = { viewModel.openTicket(it) },
                    onCancelBooking = { viewModel.cancelBooking(it) }
                )
                3 -> TeamsScreen(
                    teams = teams,
                    challenges = challenges,
                    language = uiState.language,
                    onOpenRegister = { viewModel.openRegisterTeam() },
                    onOpenChallenge = { viewModel.openChallengeDialog(it) },
                    onRespondChallenge = { id, accept -> viewModel.respondToChallenge(id, accept) }
                )
                4 -> GalleryScreen(
                    images = galleryImages,
                    selectedCategory = uiState.galleryCategory,
                    language = uiState.language,
                    onSelectCategory = { viewModel.setGalleryCategory(it) },
                    onLike = { viewModel.likeGalleryImage(it) }
                )
                5 -> LiveMapScreen(
                    pitches = pitches,
                    language = uiState.language
                )
                6 -> PaymentGuideScreen(
                    language = uiState.language,
                    onShowToast = { viewModel.showToast(it) }
                )
                7 -> AdminScreen(
                    isAdminUnlocked = uiState.isAdminUnlocked,
                    pitches = pitches,
                    bookings = bookings,
                    blockedSlots = blockedSlots,
                    language = uiState.language,
                    onUnlock = { viewModel.unlockAdmin(it) },
                    onLock = { viewModel.lockAdmin() },
                    onTogglePitchStatus = { viewModel.togglePitchStatus(it) },
                    onBlockSlot = { pitchId, date, slot, reason -> viewModel.blockSlot(pitchId, date, slot, reason) },
                    onDeleteBlockedSlot = { viewModel.deleteBlockedSlot(it) }
                )
            }

            // Top floating simulated SMS banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                SmsNotificationToast(
                    sms = uiState.activeSms,
                    onDismiss = { viewModel.dismissSmsToast() }
                )
            }

            // Bottom Toast notification
            if (uiState.toastMessage != null) {
                Surface(
                    color = StadiumSurfaceDark,
                    shape = RoundedCornerShape(10.dp),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
                ) {
                    Text(
                        text = uiState.toastMessage ?: "",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }

    // Dialogs
    BookingDialog(
        isOpen = uiState.isBookingOpen,
        pitches = pitches,
        initialPitchId = uiState.selectedPitchId,
        initialSlot = uiState.bookingInitialSlot,
        language = uiState.language,
        onDismiss = { viewModel.closeBooking() },
        onConfirm = { pitchId, date, slot, durationHours, teamName, captainName, phone, email, paymentMethod, transactionId, selectedAddons, notes ->
            viewModel.createBooking(
                pitchId = pitchId,
                date = date,
                slot = slot,
                durationHours = durationHours,
                teamName = teamName,
                captainName = captainName,
                phone = phone,
                email = email,
                paymentMethod = paymentMethod,
                transactionId = transactionId,
                selectedAddons = selectedAddons,
                notes = notes
            )
        }
    )

    TicketDialog(
        booking = uiState.activeTicket,
        language = uiState.language,
        onDismiss = { viewModel.closeTicket() },
        onCancelBooking = { viewModel.cancelBooking(it) }
    )

    RegisterTeamDialog(
        isOpen = uiState.isRegisterTeamOpen,
        language = uiState.language,
        onDismiss = { viewModel.closeRegisterTeam() },
        onRegister = { name, emoji, color, capName, capPhone, format, skill, bio ->
            viewModel.registerTeam(name, emoji, color, capName, capPhone, format, skill, bio)
        }
    )

    ChallengeDialog(
        isOpen = uiState.isChallengeDialogOpen,
        opponentTeam = uiState.challengeOpponentTeam,
        allTeams = teams,
        pitches = pitches,
        language = uiState.language,
        onDismiss = { viewModel.closeChallengeDialog() },
        onSendChallenge = { challenger, opponent, pitchId, date, slot, splitMode, notes ->
            viewModel.createChallenge(challenger, opponent, pitchId, date, slot, splitMode, notes)
        }
    )
}
