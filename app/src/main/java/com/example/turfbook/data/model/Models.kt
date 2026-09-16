package com.example.turfbook.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class SurfaceType(val displayName: String) {
    FIFA_SYNTHETIC("FIFA Synthetic Turf"),
    ASTRO_TURF("High-Density AstroTurf"),
    SHOCK_PAD("Shock-Pad Pro Turf"),
    INDOOR_FUTSAL("Indoor Futsal Rubber Turf")
}

@Serializable
enum class GameFormat(val label: String) {
    ALL("All Formats"),
    SEVEN_A_SIDE("7-a-side"),
    SIX_A_SIDE("6-a-side"),
    FIVE_A_SIDE("5-a-side"),
    FUTSAL("Futsal"),
    TRAINING("Training")
}

@Serializable
enum class PaymentMethod(val label: String, val merchant: String) {
    ZAAD("Zaad Service", "445686"),
    EDAHAB("eDahab", "10136"),
    CASH("Cash / At Reception", "-")
}

@Serializable
enum class Language {
    EN, SO
}

@Serializable
data class Pitch(
    val id: String,
    val name: String,
    val somaliName: String,
    val format: GameFormat,
    val surface: SurfaceType,
    val hourlyRate: Double = 25.0,
    val dayRate: Double = 18.0,
    val nightRate: Double = 25.0,
    val dimensions: String,
    val capacity: String,
    val features: List<String>,
    val imageUrl: String,
    val isFloodlit: Boolean = true,
    val status: String = "available" // available, maintenance, busy
)

@Serializable
data class AddOnItem(
    val id: String,
    val name: String,
    val somaliName: String,
    val price: Double,
    val description: String,
    val iconName: String
)

@Serializable
enum class LoyaltyTier(
    val title: String,
    val somaliTitle: String,
    val badgeIcon: String,
    val minPoints: Int,
    val discountPercent: Int,
    val perkEn: String,
    val perkSo: String
) {
    BRONZE(
        title = "Bronze",
        somaliTitle = "Bronze (Naxaas)",
        badgeIcon = "🥉",
        minPoints = 0,
        discountPercent = 0,
        perkEn = "Earn 10 points per $1 spent • Standard pitch access",
        perkSo = "Hel 10 dhibcood $1 kasta oo aad bixiso"
    ),
    SILVER(
        title = "Silver",
        somaliTitle = "Silver (Qalin)",
        badgeIcon = "🥈",
        minPoints = 500,
        discountPercent = 5,
        perkEn = "5% off all pitch hire • Free team bibs set per booking",
        perkSo = "5% qiimo dhimis garoonka • Lebiska kooxda oo bilaash ah"
    ),
    GOLD(
        title = "Gold",
        somaliTitle = "Gold (Dahab)",
        badgeIcon = "🥇",
        minPoints = 1000,
        discountPercent = 10,
        perkEn = "10% off pitch hire • Free match ball & VIP lounge priority",
        perkSo = "10% qiimo dhimis • Kubad ciyaareed bilaash ah & VIP Lounge"
    ),
    PLATINUM(
        title = "Platinum",
        somaliTitle = "Platinum (Balaatiin)",
        badgeIcon = "💎",
        minPoints = 2500,
        discountPercent = 15,
        perkEn = "15% off pitch hire • Free match ball & VIP priority booking window",
        perkSo = "15% qiimo dhimis • Kubad bilaash ah & mudnaanta ballamaha VIP"
    );

    companion object {
        fun fromPoints(points: Int): LoyaltyTier = when {
            points >= 2500 -> PLATINUM
            points >= 1000 -> GOLD
            points >= 500 -> SILVER
            else -> BRONZE
        }
    }
}

@Serializable
data class Booking(
    val id: String,
    val referenceCode: String,
    val pitchId: String,
    val pitchName: String,
    val date: String, // YYYY-MM-DD
    val startTime: String,
    val endTime: String,
    val durationHours: Int = 1,
    val customerName: String,
    val teamName: String,
    val customerPhone: String,
    val customerEmail: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.ZAAD,
    val paymentStatus: String = "paid", // paid, pending_verification, unpaid
    val transactionId: String = "",
    val merchantNumber: String = "445686",
    val totalAmount: Double,
    val loyaltyPoints: Int = 0,
    val referralCodeApplied: String = "",
    val referralBonusPoints: Int = 0,
    val addOns: List<String> = emptyList(),
    val notes: String = "",
    val createdAt: String = "",
    val smsConfirmed: Boolean = true
) {
    companion object {
        const val REFERRAL_BONUS_POINTS = 150

        fun calculatePoints(amount: Double): Int = (amount * 10).toInt()

        fun getTier(points: Int): Pair<String, String> {
            val tier = LoyaltyTier.fromPoints(points)
            return "${tier.badgeIcon} ${tier.title}" to tier.perkEn
        }
    }
}

@Serializable
data class ReferralInvite(
    val id: String,
    val friendName: String,
    val friendPhone: String,
    val referralCode: String,
    val status: String = "COMPLETED", // "COMPLETED", "PENDING"
    val bonusPoints: Int = 150,
    val date: String,
    val bookingReference: String = ""
)

@Serializable
enum class PointsTransactionType(val labelEn: String, val labelSo: String) {
    BOOKING("Pitch Booking", "Ballanta Garoonka"),
    REFERRAL_BONUS("Referral Bonus", "Gunada Casuumadda"),
    SPECIAL_EVENT("Special Event", "Munaasabad Gaar ah"),
    PROMO_BONUS("Promotional Bonus", "Dhibco Dheeraad ah")
}

@Serializable
data class LoyaltyPointsRecord(
    val id: String,
    val titleEn: String,
    val titleSo: String,
    val subtitleEn: String,
    val subtitleSo: String,
    val type: PointsTransactionType,
    val referenceCode: String,
    val date: String,
    val time: String = "",
    val basePoints: Int = 0,
    val bonusPoints: Int = 0,
    val totalPoints: Int = basePoints + bonusPoints,
    val amountSpent: Double? = null,
    val eventName: String? = null,
    val referralFriendName: String? = null,
    val detailEn: String = "",
    val detailSo: String = ""
) {
    companion object {
        fun buildHistory(
            bookings: List<Booking>,
            referrals: List<ReferralInvite>
        ): List<LoyaltyPointsRecord> {
            val list = mutableListOf<LoyaltyPointsRecord>()

            // 1. Special Events & Seasonal Tournament Bonuses
            list.add(
                LoyaltyPointsRecord(
                    id = "evt-cup26",
                    titleEn = "26 June Independence Cup Registration",
                    titleSo = "Diiwaangelinta Koobka Xorriyadda ee 26 June",
                    subtitleEn = "Tournament entry bonus awarded to squad captains",
                    subtitleSo = "Dhibco dheeraad ah oo la siiyay kabtanka tartanka",
                    type = PointsTransactionType.SPECIAL_EVENT,
                    referenceCode = "EVT-CUP26",
                    date = "2026-09-01",
                    time = "16:00",
                    basePoints = 0,
                    bonusPoints = 100,
                    totalPoints = 100,
                    eventName = "26 June District Independence Cup 2026",
                    detailEn = "Registered captain bonus for participating in the official 26 June Independence District Cup.",
                    detailSo = "Abaalmarinta kabtanka ee ka qaybgalka Tartanka Xorriyadda ee Degmada 26 June."
                )
            )

            list.add(
                LoyaltyPointsRecord(
                    id = "evt-night75",
                    titleEn = "Inaugural Floodlight Night Derby Launch",
                    titleSo = "Furitaanka Iftiinka Casriga ah ee Habeenkii",
                    subtitleEn = "High-lumen LED floodlights opening promotion",
                    subtitleSo = "Abaalmarinta furitaanka nalalka casriga ah",
                    type = PointsTransactionType.SPECIAL_EVENT,
                    referenceCode = "EVT-NIGHT75",
                    date = "2026-09-05",
                    time = "20:00",
                    basePoints = 0,
                    bonusPoints = 75,
                    totalPoints = 75,
                    eventName = "Night Floodlights Inaugural Celebration",
                    detailEn = "Special celebration points awarded for supporting the grand opening of high-lumen LED floodlights.",
                    detailSo = "Abaalmarinta furitaanka laydhadhka cusub ee habeenkii lagu ciyaaro."
                )
            )

            // 2. From Pitch Bookings
            for (b in bookings) {
                val basePts = Booking.calculatePoints(b.totalAmount)
                list.add(
                    LoyaltyPointsRecord(
                        id = "pt-book-${b.id}",
                        titleEn = "Booking: ${b.pitchName}",
                        titleSo = "Ballanta: ${b.pitchName}",
                        subtitleEn = "${b.teamName} • ${b.startTime} - ${b.endTime} (${b.date})",
                        subtitleSo = "${b.teamName} • ${b.startTime} - ${b.endTime} (${b.date})",
                        type = PointsTransactionType.BOOKING,
                        referenceCode = b.referenceCode,
                        date = b.date,
                        time = b.startTime,
                        basePoints = basePts,
                        bonusPoints = 0,
                        totalPoints = basePts,
                        amountSpent = b.totalAmount,
                        detailEn = "Earned 10 loyalty points per $1 spent on $${String.format(java.util.Locale.US, "%.2f", b.totalAmount)} total payment.",
                        detailSo = "Waxaad heshay 10 dhibcood $1 kasta oo aad bixisay qiimaha guud ee $${String.format(java.util.Locale.US, "%.2f", b.totalAmount)}."
                    )
                )

                // If referral code applied on booking
                if (b.referralCodeApplied.isNotBlank() || b.referralBonusPoints > 0) {
                    val bonus = if (b.referralBonusPoints > 0) b.referralBonusPoints else Booking.REFERRAL_BONUS_POINTS
                    list.add(
                        LoyaltyPointsRecord(
                            id = "pt-ref-book-${b.id}",
                            titleEn = "Referral Code Bonus (${b.referralCodeApplied.ifBlank { "PROMO" }})",
                            titleSo = "Gunada Koodhka Casuumadda (${b.referralCodeApplied.ifBlank { "PROMO" }})",
                            subtitleEn = "Applied referral bonus on #${b.referenceCode}",
                            subtitleSo = "Dhibco gunno ah oo lagu helay #${b.referenceCode}",
                            type = PointsTransactionType.REFERRAL_BONUS,
                            referenceCode = b.referenceCode,
                            date = b.date,
                            time = b.startTime,
                            basePoints = 0,
                            bonusPoints = bonus,
                            totalPoints = bonus,
                            referralFriendName = b.referralCodeApplied,
                            detailEn = "Bonus awarded for entering referral code '${b.referralCodeApplied}' during booking reservation.",
                            detailSo = "Dhibco gunno ah oo lagu helay gelinta koodhka '${b.referralCodeApplied}' xilliga ballanta."
                        )
                    )
                }

                // If derby match in notes
                if (b.notes.contains("derby", ignoreCase = true) || b.notes.contains("tartanka", ignoreCase = true)) {
                    list.add(
                        LoyaltyPointsRecord(
                            id = "pt-event-derby-${b.id}",
                            titleEn = "Weekend Super Derby Match Bonus",
                            titleSo = "Abaalmarinta Ciyaarta Adag ee Derby-ga",
                            subtitleEn = "High-intensity derby clash on #${b.referenceCode}",
                            subtitleSo = "Kulanka xamaasadda leh ee #${b.referenceCode}",
                            type = PointsTransactionType.SPECIAL_EVENT,
                            referenceCode = b.referenceCode,
                            date = b.date,
                            time = b.startTime,
                            basePoints = 0,
                            bonusPoints = 50,
                            totalPoints = 50,
                            eventName = "Weekend Super Derby Clashes",
                            detailEn = "Awarded +50 bonus points for scheduling a high-tempo weekend derby match fixture.",
                            detailSo = "Waxaad heshay +50 dhibcood oo dheeraad ah maadaama aad ballansatay kulan derby ah."
                        )
                    )
                }
            }

            // 3. From Referrals (when friends completed bookings)
            for (ref in referrals) {
                if (ref.status == "COMPLETED") {
                    list.add(
                        LoyaltyPointsRecord(
                            id = "pt-ref-invite-${ref.id}",
                            titleEn = "Friend Referral: ${ref.friendName}",
                            titleSo = "Casuumadda Saaxiibka: ${ref.friendName}",
                            subtitleEn = "Friend completed 1st pitch booking (${ref.friendPhone})",
                            subtitleSo = "Saaxiibku wuxuu dhammaystiray ciyaartii 1-aad (${ref.friendPhone})",
                            type = PointsTransactionType.REFERRAL_BONUS,
                            referenceCode = ref.bookingReference.ifBlank { "REF-${ref.referralCode}" },
                            date = ref.date,
                            time = "12:00",
                            basePoints = 0,
                            bonusPoints = ref.bonusPoints,
                            totalPoints = ref.bonusPoints,
                            referralFriendName = ref.friendName,
                            detailEn = "Earned +${ref.bonusPoints} bonus points because ${ref.friendName} registered with your code and completed their first booking.",
                            detailSo = "Waxaad heshay +${ref.bonusPoints} dhibcood maadaama ${ref.friendName} uu isticmaalay koodhkaaga oo uu ciyaaray ciyaartiisii ugu horreysay."
                        )
                    )
                }
            }

            return list.sortedWith(
                compareByDescending<LoyaltyPointsRecord> { it.date }
                    .thenByDescending { it.time }
            )
        }
    }
}

@Serializable
data class BlockedSlot(
    val id: String,
    val pitchId: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val reason: String
)

@Serializable
data class TeamMember(
    val id: String,
    val name: String,
    val position: String, // Forward, Midfielder, Defender, Goalkeeper
    val jerseyNumber: Int,
    val isCaptain: Boolean = false
)

@Serializable
data class TeamStats(
    val matchesPlayed: Int = 0,
    val wins: Int = 0,
    val draws: Int = 0,
    val losses: Int = 0
)

@Serializable
data class Team(
    val id: String,
    val name: String,
    val logoEmoji: String,
    val color: String,
    val captainName: String,
    val captainPhone: String,
    val preferredFormat: GameFormat,
    val skillLevel: String, // Casual, Competitive, Semi-Pro
    val bio: String = "",
    val joinedDate: String = "",
    val stats: TeamStats = TeamStats(),
    val members: List<TeamMember> = emptyList()
)

@Serializable
data class MatchChallenge(
    val id: String,
    val challengerTeamId: String,
    val challengerTeamName: String,
    val challengerCaptainPhone: String,
    val challengedTeamId: String,
    val challengedTeamName: String,
    val challengedCaptainPhone: String,
    val pitchId: String,
    val pitchName: String,
    val date: String,
    val slot: String,
    val totalAmount: Double,
    val splitMode: String, // 50-50, challenger-covers, loser-pays
    val status: String = "pending_opponent", // pending_opponent, accepted, declined, booked
    val notes: String = "",
    val bookingRef: String = "",
    val createdAt: String = ""
)

@Serializable
data class GameImage(
    val id: String,
    val title: String,
    val somaliTitle: String,
    val category: String, // Match Action, Night Floodlights, Tournament, Celebration
    val imageUrl: String,
    val date: String,
    val pitchName: String,
    val teamsInvolved: String = "",
    val likes: Int = 0
)

@Serializable
data class SimulatedSmsNotification(
    val id: String,
    val bookingRef: String,
    val recipientPhone: String,
    val recipientName: String,
    val teamName: String,
    val message: String,
    val gateway: String,
    val timestamp: String,
    val status: String = "delivered"
)

@Serializable
data class UpcomingBookingReminder(
    val id: String,
    val bookingId: String,
    val bookingReference: String,
    val pitchName: String,
    val date: String,
    val timeSlot: String,
    val teamName: String,
    val customerName: String,
    val customerPhone: String,
    val hoursUntilMatch: Int = 24,
    val notificationMessageEn: String,
    val notificationMessageSo: String,
    val triggeredAt: String,
    val isRead: Boolean = false
)

@Serializable
data class TwoHourBookingReminder(
    val id: String,
    val bookingId: String,
    val bookingReference: String,
    val pitchName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val teamName: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val quickAccessLink: String,
    val webAccessLink: String,
    val notificationTitleEn: String,
    val notificationTitleSo: String,
    val notificationBodyEn: String,
    val notificationBodySo: String,
    val emailSubject: String,
    val emailBodyPlainText: String,
    val emailBodyHtml: String,
    val triggeredAt: String,
    val minutesUntilKickoff: Long = 120,
    val emailDispatched: Boolean = true,
    val pushDispatched: Boolean = true
)

@Serializable
data class PitchReview(
    val id: String,
    val bookingId: String,
    val pitchId: String,
    val pitchName: String,
    val customerName: String,
    val teamName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val date: String, // e.g. "2026-09-12"
    val tags: List<String> = emptyList()
)

data class PitchRatingSummary(
    val pitchId: String,
    val averageRating: Double = 0.0,
    val reviewCount: Int = 0,
    val distribution: Map<Int, Int> = emptyMap()
)

@Serializable
data class WaitlistEntry(
    val id: String,
    val pitchId: String,
    val pitchName: String,
    val date: String, // YYYY-MM-DD
    val slot: String, // e.g. "18:00 - 19:00"
    val customerName: String,
    val customerPhone: String,
    val teamName: String,
    val notes: String = "",
    val status: String = "WAITING", // "WAITING", "NOTIFIED", "CONVERTED", "CANCELLED"
    val createdAt: Long = System.currentTimeMillis(),
    val createdTimeStr: String = ""
)

object BookingTimeHelper {
    /**
     * Determines whether the booking's end time has passed compared to the reference time (default: now).
     * Format for date: "yyyy-MM-dd", endTime: "HH:mm" (e.g. "20:00" or "00:00").
     */
    fun isBookingTimePassed(dateStr: String, endTimeStr: String, now: java.util.Date = java.util.Date()): Boolean {
        return try {
            val cleanEndTime = endTimeStr.trim()
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())

            // If endTime is "00:00" or "24:00", it refers to midnight at the end of the day or start of next day
            val endDateTime = if (cleanEndTime == "00:00" || cleanEndTime == "24:00") {
                val cal = java.util.Calendar.getInstance().apply {
                    time = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(dateStr) ?: now
                    add(java.util.Calendar.DAY_OF_YEAR, 1)
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                cal.time
            } else {
                sdf.parse("$dateStr $cleanEndTime")
            }

            if (endDateTime != null) {
                endDateTime.before(now) || endDateTime.time <= now.time
            } else {
                val dateOnly = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val bDate = dateOnly.parse(dateStr)
                val todayDate = dateOnly.parse(dateOnly.format(now))
                bDate != null && todayDate != null && bDate.before(todayDate)
            }
        } catch (e: Exception) {
            false
        }
    }

    fun calculateRatingSummary(pitchId: String, reviews: List<PitchReview>): PitchRatingSummary {
        val pitchReviews = reviews.filter { it.pitchId == pitchId }
        if (pitchReviews.isEmpty()) {
            return PitchRatingSummary(pitchId = pitchId, averageRating = 0.0, reviewCount = 0)
        }
        val avg = pitchReviews.map { it.rating }.average()
        val dist = pitchReviews.groupingBy { it.rating }.eachCount()
        val roundedAvg = kotlin.math.round(avg * 10) / 10.0
        return PitchRatingSummary(
            pitchId = pitchId,
            averageRating = roundedAvg,
            reviewCount = pitchReviews.size,
            distribution = dist
        )
    }

    /**
     * Calculates minutes until the start of a booking match.
     * Returns positive number if in the future, negative if kickoff has passed.
     */
    fun getMinutesUntilMatch(dateStr: String, startTimeStr: String, now: java.util.Date = java.util.Date()): Long {
        return try {
            val cleanStartTime = if (startTimeStr.contains(" - ")) {
                startTimeStr.split(" - ")[0].trim()
            } else {
                startTimeStr.trim()
            }
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            val matchDateTime = sdf.parse("$dateStr $cleanStartTime") ?: return Long.MIN_VALUE
            (matchDateTime.time - now.time) / (60 * 1000)
        } catch (e: Exception) {
            Long.MIN_VALUE
        }
    }

    /**
     * Checks whether a booking is approximately 2 hours away (by default between 30 and 150 minutes before kickoff).
     */
    fun isWithinTwoHourWindow(dateStr: String, startTimeStr: String, now: java.util.Date = java.util.Date(), minMinutes: Long = 30, maxMinutes: Long = 150): Boolean {
        val minutesUntil = getMinutesUntilMatch(dateStr, startTimeStr, now)
        return minutesUntil in minMinutes..maxMinutes
    }

    fun generateQuickAccessDeepLink(bookingId: String, referenceCode: String): String {
        return "turfbook://ticket?bookingId=$bookingId&ref=$referenceCode"
    }

    fun generateQuickAccessWebUrl(bookingId: String): String {
        return "https://turfbook.jsc.so/ticket?id=$bookingId"
    }
}

object AppConfig {
    const val APP_NAME = "26 JSC TurfBook"
    const val TAGLINE_EN = "Premier Sports Centre & Floodlit Turf Booking"
    const val TAGLINE_SO = "Xarunta Ciyaaraha & Garoomada Casriga ah ee 26 JSC"
    const val CONTACT_PHONE = "+252633347832"
    const val CONTACT_EMAIL = "foscar2019@gmail.com"
    const val ZAAD_MERCHANT = "445686"
    const val EDAHAB_MERCHANT = "10136"
    const val LOCATION_EN = "26 June District, Main Sports Corridor, Hargeisa"
    const val LOCATION_SO = "Degmada 26 June, Dhabarka dambe ee Waddada Wadnaha, Hargeysa"
    const val LATITUDE = 9.5594
    const val LONGITUDE = 44.0628
    const val OPENING_HOURS = "06:00 AM – 12:00 Midnight (Daily / Maalin Kasta)"

    val TIME_SLOTS = listOf(
        "06:00 - 07:00",
        "07:00 - 08:00",
        "08:00 - 09:00",
        "09:00 - 10:00",
        "10:00 - 11:00",
        "15:00 - 16:00",
        "16:00 - 17:00",
        "17:00 - 18:00",
        "18:00 - 19:00",
        "19:00 - 20:00",
        "20:00 - 21:00",
        "21:00 - 22:00",
        "22:00 - 23:00",
        "23:00 - 00:00"
    )
}

@Serializable
data class TeamLoyaltyLeaderboardEntry(
    val rank: Int,
    val teamName: String,
    val logoEmoji: String,
    val completedBookings: Int,
    val totalHours: Int,
    val loyaltyPointsEarned: Int,
    val rewardPerkEn: String,
    val rewardPerkSo: String,
    val discountPercent: Int,
    val badgeTier: String, // "CHAMPION", "RUNNER_UP", "PODIUM", "CONTENDER"
    val accentColorHex: String
) {
    companion object {
        fun buildLeaderboard(
            bookings: List<Booking>,
            registeredTeams: List<Team> = emptyList()
        ): List<TeamLoyaltyLeaderboardEntry> {
            val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

            // Baseline historical completed bookings for community teams at 26 JSC Arena
            val baselineTeamData = mapOf(
                "26 June Warriors FC" to Triple("🛡️", 13, "#059669"),
                "Elman Hargeisa Stars" to Triple("⭐", 10, "#2563eb"),
                "Burao United FC" to Triple("🦁", 7, "#d97706"),
                "Banaadir United" to Triple("⚡", 5, "#7c3aed"),
                "Shacabka Stars FC" to Triple("🦅", 4, "#ea580c"),
                "Hargeisa Lions FC" to Triple("🐆", 2, "#0284c7")
            )

            // Dynamic count of completed bookings per team from bookings
            val dynamicCompletedCounts = mutableMapOf<String, Int>()
            val dynamicHours = mutableMapOf<String, Int>()
            val dynamicPoints = mutableMapOf<String, Int>()

            for (b in bookings) {
                val isCompleted = b.paymentStatus == "paid" && (
                    BookingTimeHelper.isBookingTimePassed(b.date, b.endTime) ||
                    b.date < todayStr ||
                    b.notes.contains("finished", ignoreCase = true) ||
                    b.smsConfirmed
                )
                if (isCompleted && b.teamName.isNotBlank()) {
                    val count = dynamicCompletedCounts.getOrDefault(b.teamName, 0) + 1
                    dynamicCompletedCounts[b.teamName] = count
                    val hrs = dynamicHours.getOrDefault(b.teamName, 0) + b.durationHours
                    dynamicHours[b.teamName] = hrs
                    val pts = dynamicPoints.getOrDefault(b.teamName, 0) + b.loyaltyPoints
                    dynamicPoints[b.teamName] = pts
                }
            }

            // Union of teams: baseline + registered + from bookings
            val allTeamNames = (baselineTeamData.keys + registeredTeams.map { it.name } + dynamicCompletedCounts.keys).distinct()

            val rawEntries = allTeamNames.map { name ->
                val base = baselineTeamData[name]
                val registered = registeredTeams.find { it.name.equals(name, ignoreCase = true) }

                val emoji = base?.first ?: registered?.logoEmoji ?: "⚽"
                val colorHex = base?.third ?: registered?.color ?: "#10b981"
                val baselineCount = base?.second ?: (registered?.stats?.matchesPlayed ?: 0)

                val dynCount = dynamicCompletedCounts[name] ?: 0
                val totalCompleted = baselineCount + dynCount
                val totalHrs = totalCompleted + (dynamicHours[name] ?: 0)
                val totalPts = (totalCompleted * 250) + (dynamicPoints[name] ?: 0)

                totalCompleted to (name to listOf(emoji, totalHrs.toString(), totalPts.toString(), colorHex))
            }

            return rawEntries
                .sortedByDescending { it.first }
                .take(5)
                .mapIndexed { index, pair ->
                    val rank = index + 1
                    val completed = pair.first
                    val name = pair.second.first
                    val emoji = pair.second.second[0]
                    val hours = pair.second.second[1].toIntOrNull() ?: completed
                    val points = pair.second.second[2].toIntOrNull() ?: (completed * 250)
                    val colorHex = pair.second.second[3]

                    val perkEn: String
                    val perkSo: String
                    val discount: Int
                    val badgeTier: String

                    when (rank) {
                        1 -> {
                            perkEn = "15% Off All Bookings + Free Match Ball + Priority Night Window"
                            perkSo = "15% Qiimo-dhimis + Kubbad Bilaash ah + Xilliga Habeenkii ee Mudnaanta leh"
                            discount = 15
                            badgeTier = "CHAMPION"
                        }
                        2 -> {
                            perkEn = "10% Off All Bookings + Free Bibs Rental"
                            perkSo = "10% Qiimo-dhimis + Jaakadaha Ciyaarta oo Bilaash ah"
                            discount = 10
                            badgeTier = "RUNNER_UP"
                        }
                        3 -> {
                            perkEn = "5% Off All Bookings + Priority Weekend Scheduling"
                            perkSo = "5% Qiimo-dhimis + Ballamaha Toddobaadka ee Mudnaanta leh"
                            discount = 5
                            badgeTier = "PODIUM"
                        }
                        4 -> {
                            perkEn = "+50 Bonus Loyalty Points per match + Water Pack Voucher"
                            perkSo = "+50 Dhibco dheeraad ah kulan kasta + Biyaha Ciyaarta"
                            discount = 3
                            badgeTier = "CONTENDER"
                        }
                        else -> {
                            perkEn = "+50 Bonus Loyalty Points per match + Free Warm-up Cones"
                            perkSo = "+50 Dhibco dheeraad ah kulan kasta + Agabka Tababarka"
                            discount = 2
                            badgeTier = "CONTENDER"
                        }
                    }

                    TeamLoyaltyLeaderboardEntry(
                        rank = rank,
                        teamName = name,
                        logoEmoji = emoji,
                        completedBookings = completed,
                        totalHours = hours,
                        loyaltyPointsEarned = points,
                        rewardPerkEn = perkEn,
                        rewardPerkSo = perkSo,
                        discountPercent = discount,
                        badgeTier = badgeTier,
                        accentColorHex = colorHex
                    )
                }
        }
    }
}

@Serializable
enum class TeamLoyaltyTier(
    val tierName: String,
    val somaliTierName: String,
    val badgeIcon: String,
    val badgeEmoji: String,
    val minCompletedBookings: Int,
    val discountPercent: Int,
    val perkEn: String,
    val perkSo: String,
    val isHighlighted: Boolean // true for Platinum and Gold
) {
    PLATINUM(
        tierName = "Platinum",
        somaliTierName = "Balaatiin",
        badgeIcon = "💎",
        badgeEmoji = "💎",
        minCompletedBookings = 10,
        discountPercent = 15,
        perkEn = "Platinum Status • 10+ Completed Matches • 15% VIP Pitch Discount",
        perkSo = "Heerka Balaatiin • 10+ Kulan oo la ciyaaray • 15% Qiimo-dhimis VIP ah",
        isHighlighted = true
    ),
    GOLD(
        tierName = "Gold",
        somaliTierName = "Dahab",
        badgeIcon = "👑",
        badgeEmoji = "👑",
        minCompletedBookings = 5,
        discountPercent = 10,
        perkEn = "Gold Status • 5+ Completed Matches • 10% Match Discount",
        perkSo = "Heerka Dahab • 5+ Kulan oo la ciyaaray • 10% Qiimo-dhimis",
        isHighlighted = true
    ),
    SILVER(
        tierName = "Silver",
        somaliTierName = "Qalin",
        badgeIcon = "🥈",
        badgeEmoji = "🥈",
        minCompletedBookings = 3,
        discountPercent = 5,
        perkEn = "Silver Status • 3+ Completed Matches • Free Team Bibs",
        perkSo = "Heerka Qalin • 3+ Kulan oo la ciyaaray • Jaakadaha bilaash ah",
        isHighlighted = false
    ),
    BRONZE(
        tierName = "Bronze",
        somaliTierName = "Naxaas",
        badgeIcon = "🥉",
        badgeEmoji = "🛡️",
        minCompletedBookings = 0,
        discountPercent = 0,
        perkEn = "Bronze Contender • 10 pts per $1 spent",
        perkSo = "Heerka Naxaas • 10 dhibcood $1 kasta oo la bixiyo",
        isHighlighted = false
    );

    companion object {
        fun fromCompletedBookings(count: Int): TeamLoyaltyTier = when {
            count >= 10 -> PLATINUM
            count >= 5 -> GOLD
            count >= 3 -> SILVER
            else -> BRONZE
        }
    }
}

@Serializable
data class TeamLoyaltyInfo(
    val teamName: String,
    val completedBookings: Int,
    val loyaltyTier: TeamLoyaltyTier,
    val totalHours: Int = completedBookings,
    val loyaltyPoints: Int = completedBookings * 250
) {
    val isGoldOrPlatinum: Boolean get() = loyaltyTier.isHighlighted
}

object TeamLoyaltyCalculator {
    // Baseline completed booking counts from verified arena records
    private val baselineTeamCompleted = mapOf(
        "26 June Warriors FC" to 13,
        "Elman Hargeisa Stars" to 10,
        "Burao United FC" to 7,
        "Banaadir United" to 5,
        "Shacabka Stars FC" to 4,
        "Hargeisa Lions FC" to 2
    )

    fun getTeamLoyaltyInfo(
        teamName: String,
        bookings: List<Booking>,
        registeredTeams: List<Team> = emptyList()
    ): TeamLoyaltyInfo {
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

        val baseCount = baselineTeamCompleted[teamName]
            ?: registeredTeams.find { it.name.equals(teamName, ignoreCase = true) }?.stats?.matchesPlayed
            ?: 0

        var dynCount = 0
        var dynHours = 0
        var dynPoints = 0

        for (b in bookings) {
            if (b.teamName.equals(teamName, ignoreCase = true)) {
                val isCompleted = b.paymentStatus == "paid" && (
                    BookingTimeHelper.isBookingTimePassed(b.date, b.endTime) ||
                    b.date < todayStr ||
                    b.notes.contains("finished", ignoreCase = true) ||
                    b.smsConfirmed
                )
                if (isCompleted) {
                    dynCount++
                    dynHours += b.durationHours
                    dynPoints += b.loyaltyPoints
                }
            }
        }

        val totalCompleted = baseCount + dynCount
        val tier = TeamLoyaltyTier.fromCompletedBookings(totalCompleted)

        return TeamLoyaltyInfo(
            teamName = teamName,
            completedBookings = totalCompleted,
            loyaltyTier = tier,
            totalHours = totalCompleted + dynHours,
            loyaltyPoints = (totalCompleted * 250) + dynPoints
        )
    }

    fun getAllTeamLoyalties(
        teams: List<Team>,
        bookings: List<Booking>
    ): Map<String, TeamLoyaltyInfo> {
        val map = mutableMapOf<String, TeamLoyaltyInfo>()
        for (team in teams) {
            val info = getTeamLoyaltyInfo(team.name, bookings, teams)
            map[team.name] = info
            map[team.id] = info
        }
        return map
    }
}


