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
    val addOns: List<String> = emptyList(),
    val notes: String = "",
    val createdAt: String = "",
    val smsConfirmed: Boolean = true
) {
    companion object {
        fun calculatePoints(amount: Double): Int = (amount * 10).toInt()

        fun getTier(points: Int): Pair<String, String> {
            val tier = LoyaltyTier.fromPoints(points)
            return "${tier.badgeIcon} ${tier.title}" to tier.perkEn
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
