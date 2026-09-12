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
    );

    companion object {
        fun fromPoints(points: Int): LoyaltyTier = when {
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
