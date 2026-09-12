package com.example.turfbook.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.turfbook.data.model.*
import com.example.turfbook.data.notification.UpcomingBookingNotificationManager
import com.example.turfbook.data.repository.TurfRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class TurfUiState(
    val language: Language = Language.EN,
    val activeTab: Int = 0, // 0: Pitches, 1: Schedule, 2: Bookings, 3: Teams, 4: Gallery, 5: Map, 6: Payment, 7: Admin
    val selectedFormat: GameFormat = GameFormat.ALL,
    val selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val selectedPitchId: String = "pitch-1",
    val isBookingOpen: Boolean = false,
    val bookingInitialSlot: String? = null,
    val activeTicket: Booking? = null,
    val activeSms: SimulatedSmsNotification? = null,
    val activeUpcomingReminder: UpcomingBookingReminder? = null,
    val lastDailyCheckDate: String? = null,
    val isDailyCheckRunning: Boolean = false,
    val toastMessage: String? = null,
    val isAdminUnlocked: Boolean = false,
    val isRegisterTeamOpen: Boolean = false,
    val isChallengeDialogOpen: Boolean = false,
    val challengeOpponentTeam: Team? = null,
    val galleryCategory: String = "All",
    val isInviteFriendDialogOpen: Boolean = false
)

class TurfViewModel(private val repository: TurfRepository) : ViewModel() {

    val pitches: StateFlow<List<Pitch>> = repository.pitches.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TurfRepository.getInitialPitches()
    )

    val bookings: StateFlow<List<Booking>> = repository.bookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val referrals: StateFlow<List<ReferralInvite>> = repository.referrals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val blockedSlots: StateFlow<List<BlockedSlot>> = repository.blockedSlots.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val teams: StateFlow<List<Team>> = repository.teams.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TurfRepository.getInitialTeams()
    )

    val challenges: StateFlow<List<MatchChallenge>> = repository.challenges.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val galleryImages: StateFlow<List<GameImage>> = repository.galleryImages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TurfRepository.getInitialGalleryImages()
    )

    private val _uiState = MutableStateFlow(TurfUiState())
    val uiState: StateFlow<TurfUiState> = _uiState.asStateFlow()

    fun setTab(index: Int) {
        _uiState.value = _uiState.value.copy(activeTab = index)
    }

    fun setFormatFilter(format: GameFormat) {
        _uiState.value = _uiState.value.copy(selectedFormat = format)
    }

    fun setDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun setSelectedPitchId(pitchId: String) {
        _uiState.value = _uiState.value.copy(selectedPitchId = pitchId)
    }

    fun setGalleryCategory(cat: String) {
        _uiState.value = _uiState.value.copy(galleryCategory = cat)
    }

    fun toggleLanguage() {
        val current = _uiState.value.language
        val next = if (current == Language.EN) Language.SO else Language.EN
        _uiState.value = _uiState.value.copy(language = next)
        showToast(if (next == Language.EN) "Switched to English" else "Luuqada Somaliga ayaa la doortay")
    }

    fun openBooking(pitchId: String? = null, initialSlot: String? = null) {
        _uiState.value = _uiState.value.copy(
            isBookingOpen = true,
            selectedPitchId = pitchId ?: _uiState.value.selectedPitchId,
            bookingInitialSlot = initialSlot
        )
    }

    fun closeBooking() {
        _uiState.value = _uiState.value.copy(isBookingOpen = false, bookingInitialSlot = null)
    }

    fun openTicket(booking: Booking) {
        _uiState.value = _uiState.value.copy(activeTicket = booking)
    }

    fun closeTicket() {
        _uiState.value = _uiState.value.copy(activeTicket = null)
    }

    fun openRegisterTeam() {
        _uiState.value = _uiState.value.copy(isRegisterTeamOpen = true)
    }

    fun closeRegisterTeam() {
        _uiState.value = _uiState.value.copy(isRegisterTeamOpen = false)
    }

    fun openChallengeDialog(opponentTeam: Team) {
        _uiState.value = _uiState.value.copy(
            isChallengeDialogOpen = true,
            challengeOpponentTeam = opponentTeam
        )
    }

    fun closeChallengeDialog() {
        _uiState.value = _uiState.value.copy(
            isChallengeDialogOpen = false,
            challengeOpponentTeam = null
        )
    }

    fun unlockAdmin(code: String): Boolean {
        if (code.trim() == "2626" || code.trim() == "admin") {
            _uiState.value = _uiState.value.copy(isAdminUnlocked = true)
            showToast("Manager Portal Unlocked")
            return true
        } else {
            showToast("Invalid Manager PIN. (Default: 2626)")
            return false
        }
    }

    fun lockAdmin() {
        _uiState.value = _uiState.value.copy(isAdminUnlocked = false)
    }

    fun showToast(msg: String) {
        _uiState.value = _uiState.value.copy(toastMessage = msg)
        viewModelScope.launch {
            delay(3500)
            if (_uiState.value.toastMessage == msg) {
                _uiState.value = _uiState.value.copy(toastMessage = null)
            }
        }
    }

    fun dismissSmsToast() {
        _uiState.value = _uiState.value.copy(activeSms = null)
    }

    fun createBooking(
        pitchId: String,
        date: String,
        slot: String,
        durationHours: Int,
        teamName: String,
        captainName: String,
        phone: String,
        email: String,
        paymentMethod: PaymentMethod,
        transactionId: String,
        selectedAddons: List<String>,
        notes: String,
        referralCode: String = ""
    ) {
        viewModelScope.launch {
            val pitch = pitches.value.find { it.id == pitchId } ?: pitches.value.first()
            val isNight = isNightSlot(slot)
            val hourlyRate = if (isNight) pitch.nightRate else pitch.dayRate
            val addOnsCost = selectedAddons.sumOf { addId ->
                TurfRepository.ADD_ONS.find { it.id == addId }?.price ?: 0.0
            }
            val total = (hourlyRate * durationHours) + addOnsCost
            val ref = "JSC-${Random.nextInt(1000, 9999)}"

            val parts = slot.split(" - ")
            val startTime = parts.getOrNull(0)?.trim() ?: "18:00"
            val endTime = parts.getOrNull(1)?.trim() ?: "19:00"

            val cleanReferral = referralCode.trim().uppercase()
            val hasReferral = cleanReferral.isNotBlank()
            val bonusPts = if (hasReferral) Booking.REFERRAL_BONUS_POINTS else 0
            val basePoints = Booking.calculatePoints(total)
            val totalPointsEarned = basePoints + bonusPts

            val booking = Booking(
                id = "b-${System.currentTimeMillis()}",
                referenceCode = ref,
                pitchId = pitch.id,
                pitchName = pitch.name,
                date = date,
                startTime = startTime,
                endTime = endTime,
                durationHours = durationHours,
                customerName = captainName,
                teamName = teamName,
                customerPhone = phone,
                customerEmail = email,
                paymentMethod = paymentMethod,
                paymentStatus = "paid",
                transactionId = transactionId.ifBlank { "TID-${Random.nextInt(100000, 999999)}" },
                merchantNumber = paymentMethod.merchant,
                totalAmount = total,
                loyaltyPoints = totalPointsEarned,
                referralCodeApplied = cleanReferral,
                referralBonusPoints = bonusPts,
                addOns = selectedAddons,
                notes = notes,
                createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                smsConfirmed = true
            )

            repository.insertBooking(booking)

            // If a friend referral was applied, record referral completion awarding both bonus points
            if (hasReferral) {
                repository.insertReferral(
                    ReferralInvite(
                        id = "ref-${System.currentTimeMillis()}",
                        friendName = captainName,
                        friendPhone = phone,
                        referralCode = cleanReferral,
                        status = "COMPLETED",
                        bonusPoints = bonusPts,
                        date = date,
                        bookingReference = ref
                    )
                )
            }

            closeBooking()
            openTicket(booking)

            // Trigger simulated SMS delivery
            val isTelesom = phone.startsWith("+25263") || phone.startsWith("063") || paymentMethod == PaymentMethod.ZAAD
            val smsText = if (_uiState.value.language == Language.SO) {
                val refBonusText = if (hasReferral) " (waxaa ku jira +$bonusPts dhibco dhiirigelin saaxiib)" else ""
                "26 JSC TurfBook: Ballantaadu waa la xaqiijiyey! Tixraaca: #$ref. Kooxda: $teamName. Garoonka: ${pitch.somaliName}. Taariikhda: $date ($slot). Dhibco Loyalty: +$totalPointsEarned pts$refBonusText. Front Desk: ${AppConfig.CONTACT_PHONE}."
            } else {
                val refBonusText = if (hasReferral) " (incl. +$bonusPts referral bonus)" else ""
                "26 JSC TurfBook: Booking confirmed! Ref: #$ref. Team: $teamName. Pitch: ${pitch.name}. Date: $date ($slot). Total: $$total (+${totalPointsEarned} loyalty pts$refBonusText). Front Desk: ${AppConfig.CONTACT_PHONE}."
            }

            _uiState.value = _uiState.value.copy(
                activeSms = SimulatedSmsNotification(
                    id = "sms-${System.currentTimeMillis()}",
                    bookingRef = ref,
                    recipientPhone = phone,
                    recipientName = captainName,
                    teamName = teamName,
                    message = smsText,
                    gateway = if (isTelesom) "Telesom SMS Gateway" else "Somtel Bulk Gateway",
                    timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                )
            )

            showToast(
                if (_uiState.value.language == Language.SO)
                    if (hasReferral) "Ballanta #$ref waa la xaqiijiyey! +$bonusPts dhibco saaxiib ayaa lagugu daray!" else "Ballanta #$ref si guul leh ayaa loo xaqiijiyey!"
                else
                    if (hasReferral) "Booking #$ref confirmed! +$bonusPts friend referral bonus awarded!" else "Booking #$ref successfully confirmed!"
            )
        }
    }

    fun openInviteDialog() {
        _uiState.value = _uiState.value.copy(isInviteFriendDialogOpen = true)
    }

    fun closeInviteDialog() {
        _uiState.value = _uiState.value.copy(isInviteFriendDialogOpen = false)
    }

    fun getUserReferralCode(): String = "JSC-WARRIOR26"

    fun sendFriendInvite(friendName: String, friendPhone: String) {
        viewModelScope.launch {
            val invite = ReferralInvite(
                id = "ref-${System.currentTimeMillis()}",
                friendName = friendName.ifBlank { "Friend" },
                friendPhone = friendPhone,
                referralCode = getUserReferralCode(),
                status = "PENDING",
                bonusPoints = Booking.REFERRAL_BONUS_POINTS,
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            repository.insertReferral(invite)
            closeInviteDialog()
            showToast(
                if (_uiState.value.language == Language.SO)
                    "Casuumad waxa loo diray $friendName! Markuu ciyaar ballansado labadiinuba waxaad helaysaan 150 dhibcood!"
                else
                    "Invite created for $friendName! When they complete their first match, both of you earn 150 bonus points!"
            )
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            repository.deleteBooking(bookingId)
            closeTicket()
            showToast(
                if (_uiState.value.language == Language.SO)
                    "Ballanta waa la tirtiray"
                else
                    "Booking cancelled successfully"
            )
        }
    }

    fun registerTeam(
        name: String,
        logoEmoji: String,
        color: String,
        captainName: String,
        captainPhone: String,
        format: GameFormat,
        skillLevel: String,
        bio: String
    ) {
        viewModelScope.launch {
            val newTeam = Team(
                id = "team-${System.currentTimeMillis()}",
                name = name,
                logoEmoji = logoEmoji.ifBlank { "⚽" },
                color = color.ifBlank { "#10B981" },
                captainName = captainName,
                captainPhone = captainPhone,
                preferredFormat = format,
                skillLevel = skillLevel,
                bio = bio,
                joinedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                stats = TeamStats(0, 0, 0, 0),
                members = listOf(
                    TeamMember("m-${System.currentTimeMillis()}", "$captainName (C)", "Midfielder", 10, true)
                )
            )
            repository.insertTeam(newTeam)
            closeRegisterTeam()
            showToast("Team \"$name\" registered successfully!")
        }
    }

    fun addTeamMember(teamId: String, name: String, position: String, jersey: Int) {
        viewModelScope.launch {
            val member = TeamMember(
                id = "m-${System.currentTimeMillis()}",
                name = name,
                position = position,
                jerseyNumber = jersey
            )
            repository.addMemberToTeam(teamId, member)
            showToast("Player $name (#$jersey) added to squad!")
        }
    }

    fun createChallenge(
        challengerTeam: Team,
        challengedTeam: Team,
        pitchId: String,
        date: String,
        slot: String,
        splitMode: String,
        notes: String
    ) {
        viewModelScope.launch {
            val pitch = pitches.value.find { it.id == pitchId } ?: pitches.value.first()
            val isNight = isNightSlot(slot)
            val rate = if (isNight) pitch.nightRate else pitch.dayRate

            val challenge = MatchChallenge(
                id = "ch-${System.currentTimeMillis()}",
                challengerTeamId = challengerTeam.id,
                challengerTeamName = challengerTeam.name,
                challengerCaptainPhone = challengerTeam.captainPhone,
                challengedTeamId = challengedTeam.id,
                challengedTeamName = challengedTeam.name,
                challengedCaptainPhone = challengedTeam.captainPhone,
                pitchId = pitch.id,
                pitchName = pitch.name,
                date = date,
                slot = slot,
                totalAmount = rate,
                splitMode = splitMode,
                status = "pending_opponent",
                notes = notes,
                createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            )

            repository.createChallenge(challenge)
            closeChallengeDialog()
            showToast("Challenge sent to ${challengedTeam.name} for $date ($slot)!")
        }
    }

    fun respondToChallenge(challengeId: String, accept: Boolean) {
        viewModelScope.launch {
            val status = if (accept) "accepted" else "declined"
            repository.updateChallengeStatus(challengeId, status)
            showToast(if (accept) "Match challenge accepted! See you on pitch." else "Challenge declined.")
        }
    }

    fun likeGalleryImage(imageId: String) {
        viewModelScope.launch {
            repository.likeImage(imageId)
        }
    }

    fun togglePitchStatus(pitchId: String) {
        viewModelScope.launch {
            repository.togglePitchStatus(pitchId)
            showToast("Pitch status updated.")
        }
    }

    fun blockSlot(pitchId: String, date: String, slot: String, reason: String) {
        viewModelScope.launch {
            val parts = slot.split(" - ")
            val blocked = BlockedSlot(
                id = "bs-${System.currentTimeMillis()}",
                pitchId = pitchId,
                date = date,
                startTime = parts.getOrNull(0)?.trim() ?: "18:00",
                endTime = parts.getOrNull(1)?.trim() ?: "19:00",
                reason = reason.ifBlank { "Scheduled Maintenance" }
            )
            repository.blockSlot(blocked)
            showToast("Slot blocked for maintenance.")
        }
    }

    fun deleteBlockedSlot(slotId: String) {
        viewModelScope.launch {
            repository.deleteBlockedSlot(slotId)
            showToast("Slot unblocked and reopened.")
        }
    }

    /**
     * Executes the daily check that triggers a notification for users who have a booking scheduled for the upcoming day.
     */
    fun runDailyUpcomingCheck(context: Context, force: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDailyCheckRunning = true)
            if (force) delay(300)

            val currentBookings = bookings.value
            val reminders = UpcomingBookingNotificationManager.performDailyCheck(
                context = context,
                bookings = currentBookings,
                forceTrigger = force,
                language = _uiState.value.language
            )

            val lastCheck = UpcomingBookingNotificationManager.getLastCheckDate(context)

            if (reminders.isNotEmpty()) {
                val primaryReminder = reminders.first()
                _uiState.value = _uiState.value.copy(
                    activeUpcomingReminder = primaryReminder,
                    lastDailyCheckDate = lastCheck,
                    isDailyCheckRunning = false
                )
                if (force) {
                    showToast("Daily Check: Match scheduled tomorrow for ${primaryReminder.teamName}! Notification triggered.")
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    lastDailyCheckDate = lastCheck,
                    isDailyCheckRunning = false
                )
                if (force) {
                    showToast("Daily Check: All scheduled bookings up-to-date.")
                }
            }
        }
    }

    fun dismissUpcomingReminder() {
        _uiState.value = _uiState.value.copy(activeUpcomingReminder = null)
    }

    /**
     * Convenience action to create a sample booking scheduled for the upcoming day (tomorrow)
     * and immediately trigger the daily check notification.
     */
    fun addTestTomorrowBooking(context: Context) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

            val testBooking = Booking(
                id = "b-test-${System.currentTimeMillis()}",
                referenceCode = "JSC-${Random.nextInt(1000, 9999)}",
                pitchId = "pitch-1",
                pitchName = "Pitch 1 - Championship Arena",
                date = tomorrow,
                startTime = "19:00",
                endTime = "20:00",
                durationHours = 1,
                customerName = "Axmed Cali",
                teamName = "26 June Warriors FC",
                customerPhone = "+252633347832",
                customerEmail = "foscar2019@gmail.com",
                paymentMethod = PaymentMethod.ZAAD,
                paymentStatus = "paid",
                transactionId = "ZD-TEST-${Random.nextInt(10000, 99999)}",
                merchantNumber = "445686",
                totalAmount = 25.0,
                loyaltyPoints = 250,
                addOns = listOf("addon-bibs"),
                notes = "Championship test match scheduled for tomorrow",
                createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            )

            repository.insertBooking(testBooking)
            showToast("Test booking added for tomorrow ($tomorrow). Triggering daily check...")
            delay(400)
            runDailyUpcomingCheck(context, force = true)
        }
    }

    companion object {
        fun isNightSlot(startTimeOrSlot: String?): Boolean {
            if (startTimeOrSlot == null) return false
            val startHourStr = if (startTimeOrSlot.contains(" - ")) {
                startTimeOrSlot.split(" - ")[0].trim()
            } else {
                startTimeOrSlot.trim()
            }
            val hour = startHourStr.split(":").firstOrNull()?.toIntOrNull() ?: return false
            return hour >= 18 || hour < 6
        }
    }
}

class TurfViewModelFactory(private val repository: TurfRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TurfViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TurfViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
