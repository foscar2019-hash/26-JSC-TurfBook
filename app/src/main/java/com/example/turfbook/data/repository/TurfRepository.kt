package com.example.turfbook.data.repository

import com.example.turfbook.data.local.*
import com.example.turfbook.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TurfRepository(private val dao: TurfDao) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    val pitches: Flow<List<Pitch>> = dao.getAllPitches().map { list ->
        list.map { it.toModel() }
    }

    val bookings: Flow<List<Booking>> = dao.getAllBookings().map { list ->
        list.map { it.toModel() }
    }

    val blockedSlots: Flow<List<BlockedSlot>> = dao.getAllBlockedSlots().map { list ->
        list.map { it.toModel() }
    }

    val teams: Flow<List<Team>> = dao.getAllTeams().map { list ->
        list.map { it.toModel() }
    }

    val challenges: Flow<List<MatchChallenge>> = dao.getAllChallenges().map { list ->
        list.map { it.toModel() }
    }

    val galleryImages: Flow<List<GameImage>> = dao.getAllGalleryImages().map { list ->
        list.map { it.toModel() }
    }

    val referrals: Flow<List<ReferralInvite>> = dao.getAllReferrals().map { list ->
        list.map { it.toModel() }
    }

    val reviews: Flow<List<PitchReview>> = dao.getAllReviews().map { list ->
        list.map { it.toModel() }
    }

    val waitlistEntries: Flow<List<WaitlistEntry>> = dao.getAllWaitlistEntries().map { list ->
        list.map { it.toModel() }
    }

    suspend fun insertReview(review: PitchReview) {
        dao.insertReview(review.toEntity())
    }

    suspend fun joinWaitlist(entry: WaitlistEntry) {
        dao.insertWaitlistEntry(entry.toEntity())
    }

    suspend fun removeWaitlistEntry(id: String) {
        dao.deleteWaitlistEntry(id)
    }

    suspend fun updateWaitlistStatus(id: String, status: String) {
        dao.updateWaitlistStatus(id, status)
    }

    suspend fun insertBooking(booking: Booking) {
        dao.insertBooking(booking.toEntity())
    }

    suspend fun deleteBooking(bookingId: String) {
        dao.deleteBooking(bookingId)
    }

    suspend fun insertTeam(team: Team) {
        dao.insertTeam(team.toEntity())
    }

    suspend fun addMemberToTeam(teamId: String, member: TeamMember) {
        val currentTeams = dao.getAllTeams().first()
        val target = currentTeams.find { it.id == teamId } ?: return
        val updatedMembers = target.members + member
        dao.updateTeam(target.copy(members = updatedMembers))
    }

    suspend fun createChallenge(challenge: MatchChallenge) {
        dao.insertChallenge(challenge.toEntity())
    }

    suspend fun updateChallengeStatus(challengeId: String, status: String) {
        dao.updateChallengeStatus(challengeId, status)
    }

    suspend fun togglePitchStatus(pitchId: String) {
        val current = dao.getAllPitches().first().find { it.id == pitchId } ?: return
        val newStatus = if (current.status == "available") "maintenance" else "available"
        dao.updatePitchStatus(pitchId, newStatus)
    }

    suspend fun blockSlot(slot: BlockedSlot) {
        dao.insertBlockedSlot(slot.toEntity())
    }

    suspend fun deleteBlockedSlot(slotId: String) {
        dao.deleteBlockedSlot(slotId)
    }

    suspend fun likeImage(imageId: String) {
        dao.incrementLikes(imageId)
    }

    suspend fun insertReferral(referral: ReferralInvite) {
        dao.insertReferral(referral.toEntity())
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existingPitches = dao.getAllPitches().first()
        if (existingPitches.isEmpty()) {
            dao.insertPitches(getInitialPitches().map { it.toEntity() })
        }

        val existingTeams = dao.getAllTeams().first()
        if (existingTeams.isEmpty()) {
            dao.insertTeams(getInitialTeams().map { it.toEntity() })
        }

        val existingBookings = dao.getAllBookings().first()
        if (existingBookings.isEmpty()) {
            dao.insertBookings(getInitialBookings().map { it.toEntity() })
        }

        val existingChallenges = dao.getAllChallenges().first()
        if (existingChallenges.isEmpty()) {
            dao.insertChallenges(getInitialChallenges().map { it.toEntity() })
        }

        val existingImages = dao.getAllGalleryImages().first()
        if (existingImages.isEmpty()) {
            dao.insertGalleryImages(getInitialGalleryImages().map { it.toEntity() })
        }

        val existingReferrals = dao.getAllReferrals().first()
        if (existingReferrals.isEmpty()) {
            dao.insertReferrals(getInitialReferrals().map { it.toEntity() })
        }

        val existingReviews = dao.getAllReviews().first()
        if (existingReviews.isEmpty()) {
            dao.insertReviews(getInitialReviews().map { it.toEntity() })
        }

        val existingWaitlists = dao.getAllWaitlistEntries().first()
        if (existingWaitlists.isEmpty()) {
            dao.insertWaitlistEntries(getInitialWaitlistEntries().map { it.toEntity() })
        }
    }

    companion object {
        val ADD_ONS = listOf(
            AddOnItem(
                id = "addon-referee",
                name = "Official Centre Referee",
                somaliName = "Garsoore Rasmi ah",
                price = 5.0,
                description = "Trained centre referee for 60-90 minutes of competitive play",
                iconName = "whistle"
            ),
            AddOnItem(
                id = "addon-bibs",
                name = "Team Match Bibs (14 sets)",
                somaliName = "Fanaanadaha Kala Saarka (Bibs 14 xabbo)",
                price = 2.0,
                description = "Two distinct color sets (Neon Yellow & Vibrant Orange)",
                iconName = "shirt"
            ),
            AddOnItem(
                id = "addon-ball",
                name = "Pro Match Football",
                somaliName = "Kubbad Ciyaareed Rasmi ah (FIFA Pro)",
                price = 2.0,
                description = "Freshly inflated thermal-bonded match ball",
                iconName = "ball"
            ),
            AddOnItem(
                id = "addon-water",
                name = "Cold Hydration Pack (12 Bottles)",
                somaliName = "Biyo Qabow (12 Xabbo)",
                price = 4.0,
                description = "Chilled mineral water pack in cooler ice bucket for the squad",
                iconName = "water"
            ),
            AddOnItem(
                id = "addon-recorder",
                name = "Match Highlight Video",
                somaliName = "Duubista Ciyaarta (Full HD Video)",
                price = 6.0,
                description = "Elevated HD camera footage of match highlights for your team",
                iconName = "video"
            )
        )

        fun getInitialPitches(): List<Pitch> = listOf(
            Pitch(
                id = "pitch-1",
                name = "Pitch 1 - Championship Arena",
                somaliName = "Garoonka 1-aad - Arena-da Guud (7-a-side)",
                format = GameFormat.SEVEN_A_SIDE,
                surface = SurfaceType.FIFA_SYNTHETIC,
                hourlyRate = 25.0,
                dayRate = 18.0,
                nightRate = 25.0,
                dimensions = "50m x 30m",
                capacity = "14 - 18 Ciyaartoy (Players)",
                features = listOf(
                    "Day Game: $18/hr | Night Floodlit: $25/hr",
                    "High-Lumen Night Floodlights (Ileys Casri ah)",
                    "Covered Team Benches & Dugouts",
                    "Electronic Scoreboard",
                    "Perimeter Safety Netting & Rebound Boards"
                ),
                imageUrl = "https://images.unsplash.com/photo-1529900748604-07564a03e7a6?auto=format&fit=crop&w=1000&q=80",
                isFloodlit = true,
                status = "available"
            ),
            Pitch(
                id = "pitch-2",
                name = "Pitch 2 - Premier Astro Turf",
                somaliName = "Garoonka 2-aad - Astro Turf (5-a-side)",
                format = GameFormat.FIVE_A_SIDE,
                surface = SurfaceType.ASTRO_TURF,
                hourlyRate = 25.0,
                dayRate = 18.0,
                nightRate = 25.0,
                dimensions = "38m x 22m",
                capacity = "10 - 14 Ciyaartoy (Players)",
                features = listOf(
                    "Day Game: $18/hr | Night Floodlit: $25/hr",
                    "Ultra-Fast Ball Roll Cushioning",
                    "Full LED Floodlights",
                    "Water Station & Spectator Seating",
                    "Official Goalposts & Mini-Nets"
                ),
                imageUrl = "https://images.unsplash.com/photo-1551958219-acbc608c6377?auto=format&fit=crop&w=1000&q=80",
                isFloodlit = true,
                status = "available"
            ),
            Pitch(
                id = "pitch-3",
                name = "Pitch 3 - VIP Floodlight Turf",
                somaliName = "Garoonka 3-aad - VIP Arena (6-a-side)",
                format = GameFormat.SIX_A_SIDE,
                surface = SurfaceType.SHOCK_PAD,
                hourlyRate = 25.0,
                dayRate = 18.0,
                nightRate = 25.0,
                dimensions = "44m x 26m",
                capacity = "12 - 16 Ciyaartoy (Players)",
                features = listOf(
                    "Day Game: $18/hr | Night Floodlit: $25/hr",
                    "VIP Shaded Lounge Area",
                    "Low-Impact Joint Protection Turf",
                    "HD Match Recording Camera Mount",
                    "Floodlight Match Lighting"
                ),
                imageUrl = "https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1000&q=80",
                isFloodlit = true,
                status = "available"
            ),
            Pitch(
                id = "pitch-4",
                name = "Pitch 4 - Skills & Futsal Cage",
                somaliName = "Garoonka 4-aad - Futsal & Xirfadaha (5-a-side)",
                format = GameFormat.FIVE_A_SIDE,
                surface = SurfaceType.INDOOR_FUTSAL,
                hourlyRate = 25.0,
                dayRate = 18.0,
                nightRate = 25.0,
                dimensions = "34m x 20m",
                capacity = "10 - 12 Ciyaartoy (Players)",
                features = listOf(
                    "Day Game: $18/hr | Night Floodlit: $25/hr",
                    "Fast Rebound Wall Panels",
                    "Intensive Footwork Surface",
                    "Overhead Protective Netting",
                    "Great for Drills, Futsal & Casual 5s"
                ),
                imageUrl = "https://images.unsplash.com/photo-1518604667004-9ae8f1149957?auto=format&fit=crop&w=1000&q=80",
                isFloodlit = true,
                status = "available"
            )
        )

        fun getInitialTeams(): List<Team> = listOf(
            Team(
                id = "team-26jsc",
                name = "26 June Warriors FC",
                logoEmoji = "🛡️",
                color = "#059669",
                captainName = "Axmed Cali",
                captainPhone = "+252633347832",
                preferredFormat = GameFormat.SEVEN_A_SIDE,
                skillLevel = "Competitive",
                bio = "Official home squad of 26 JSC Turf Arena. Training every Tuesday and Friday under floodlights.",
                joinedDate = "2026-01-15",
                stats = TeamStats(18, 13, 3, 2),
                members = listOf(
                    TeamMember("m-1", "Axmed Cali (C)", "Midfielder", 8, true),
                    TeamMember("m-2", "Khadar Maxamed", "Forward", 10),
                    TeamMember("m-3", "Hamse Jaamac", "Forward", 9),
                    TeamMember("m-4", "Cabdi Nuur", "Midfielder", 6),
                    TeamMember("m-5", "Mukhtaar Xasan", "Defender", 4),
                    TeamMember("m-6", "Sharmaarke Cumar", "Defender", 3),
                    TeamMember("m-7", "Mustafe Cabdillahi", "Goalkeeper", 1)
                )
            ),
            Team(
                id = "team-elman",
                name = "Elman Hargeisa Stars",
                logoEmoji = "⭐",
                color = "#2563eb",
                captainName = "Jaamac Nuur",
                captainPhone = "+252634411223",
                preferredFormat = GameFormat.FIVE_A_SIDE,
                skillLevel = "Competitive",
                bio = "Fast-tempo counter-attacking team specializing in 5s and futsal.",
                joinedDate = "2026-02-10",
                stats = TeamStats(14, 10, 2, 2),
                members = listOf(
                    TeamMember("m-201", "Jaamac Nuur (C)", "Forward", 7, true),
                    TeamMember("m-202", "Bashiir Warsame", "Midfielder", 8),
                    TeamMember("m-203", "Cabdiraxmaan Ciise", "Defender", 5),
                    TeamMember("m-204", "Maxamed Shire", "Goalkeeper", 1)
                )
            ),
            Team(
                id = "team-burao",
                name = "Burao United FC",
                logoEmoji = "🦁",
                color = "#d97706",
                captainName = "Mahad Xasan",
                captainPhone = "+252634889900",
                preferredFormat = GameFormat.SEVEN_A_SIDE,
                skillLevel = "Semi-Pro",
                bio = "Fierce regional contenders ready for night derby fixtures on Pitch 1.",
                joinedDate = "2026-02-28",
                stats = TeamStats(16, 11, 2, 3),
                members = listOf(
                    TeamMember("m-301", "Mahad Xasan (C)", "Defender", 4, true),
                    TeamMember("m-302", "Liibaan Geedi", "Forward", 9),
                    TeamMember("m-303", "Yuusuf Biixi", "Midfielder", 10),
                    TeamMember("m-304", "Cabdifataax Rooble", "Goalkeeper", 1)
                )
            )
        )

        fun getInitialBookings(): List<Booking> {
            val cal = Calendar.getInstance()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
            val calPast = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val yesterday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calPast.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

            return listOf(
                Booking(
                    id = "b-1000",
                    referenceCode = "JSC-7712",
                    pitchId = "pitch-1",
                    pitchName = "Pitch 1 - Championship Arena",
                    date = yesterday,
                    startTime = "18:00",
                    endTime = "19:00",
                    durationHours = 1,
                    customerName = "Axmed Cali",
                    teamName = "26 June Warriors FC",
                    customerPhone = "+252633347832",
                    customerEmail = "foscar2019@gmail.com",
                    paymentMethod = PaymentMethod.ZAAD,
                    paymentStatus = "paid",
                    transactionId = "ZD-662198",
                    merchantNumber = "445686",
                    totalAmount = 25.0,
                    loyaltyPoints = 250,
                    addOns = listOf("addon-ball"),
                    notes = "Match finished. Great 7-a-side match against Shacabka Stars",
                    createdAt = "$yesterday 17:00",
                    smsConfirmed = true
                ),
                Booking(
                    id = "b-1001",
                    referenceCode = "JSC-8841",
                    pitchId = "pitch-1",
                    pitchName = "Pitch 1 - Championship Arena",
                    date = today,
                    startTime = "19:00",
                    endTime = "20:00",
                    durationHours = 1,
                    customerName = "Khadar Maxamed",
                    teamName = "Banaadir United",
                    customerPhone = "+252634455667",
                    customerEmail = "khadar@example.com",
                    paymentMethod = PaymentMethod.ZAAD,
                    paymentStatus = "paid",
                    transactionId = "ZD-778902",
                    merchantNumber = "445686",
                    totalAmount = 29.0,
                    loyaltyPoints = 290,
                    addOns = listOf("addon-bibs", "addon-ball"),
                    notes = "Night derby match under floodlights",
                    createdAt = "2026-09-08 14:00"
                ),
                Booking(
                    id = "b-1002",
                    referenceCode = "JSC-8842",
                    pitchId = "pitch-2",
                    pitchName = "Pitch 2 - Premier Astro Turf",
                    date = today,
                    startTime = "20:00",
                    endTime = "21:00",
                    durationHours = 1,
                    customerName = "Axmed Cali",
                    teamName = "26 June FC",
                    customerPhone = "+252633347832",
                    customerEmail = "foscar2019@gmail.com",
                    paymentMethod = PaymentMethod.EDAHAB,
                    paymentStatus = "paid",
                    transactionId = "ED-109234",
                    merchantNumber = "10136",
                    totalAmount = 25.0,
                    loyaltyPoints = 250,
                    addOns = emptyList(),
                    notes = "Weekly team training",
                    createdAt = "2026-09-08 16:30"
                ),
                Booking(
                    id = "b-1003",
                    referenceCode = "JSC-9022",
                    pitchId = "pitch-1",
                    pitchName = "Pitch 1 - Championship Arena",
                    date = tomorrow,
                    startTime = "18:00",
                    endTime = "19:00",
                    durationHours = 1,
                    customerName = "Axmed Cali",
                    teamName = "26 June Warriors FC",
                    customerPhone = "+252633347832",
                    customerEmail = "foscar2019@gmail.com",
                    paymentMethod = PaymentMethod.ZAAD,
                    paymentStatus = "paid",
                    transactionId = "ZD-991204",
                    merchantNumber = "445686",
                    totalAmount = 25.0,
                    loyaltyPoints = 250,
                    addOns = listOf("addon-referee"),
                    notes = "Super League Match scheduled for tomorrow night",
                    createdAt = "2026-09-12 10:00"
                )
            )
        }

        fun getInitialChallenges(): List<MatchChallenge> = listOf(
            MatchChallenge(
                id = "ch-1",
                challengerTeamId = "team-26jsc",
                challengerTeamName = "26 June Warriors FC",
                challengerCaptainPhone = "+252633347832",
                challengedTeamId = "team-elman",
                challengedTeamName = "Elman Hargeisa Stars",
                challengedCaptainPhone = "+252634411223",
                pitchId = "pitch-1",
                pitchName = "Pitch 1 - Championship Arena",
                date = "2026-09-10",
                slot = "20:00 - 21:00",
                totalAmount = 25.0,
                splitMode = "50-50",
                status = "accepted",
                notes = "Official Super Derby match! Both captains agreed on 50/50 fee split.",
                createdAt = "2026-09-08 12:00"
            ),
            MatchChallenge(
                id = "ch-2",
                challengerTeamId = "team-burao",
                challengerTeamName = "Burao United FC",
                challengerCaptainPhone = "+252634889900",
                challengedTeamId = "team-26jsc",
                challengedTeamName = "26 June Warriors FC",
                challengedCaptainPhone = "+252633347832",
                pitchId = "pitch-2",
                pitchName = "Pitch 2 - Premier Astro Turf",
                date = "2026-09-11",
                slot = "19:00 - 20:00",
                totalAmount = 18.0,
                splitMode = "challenger-covers",
                status = "pending_opponent",
                notes = "Friendly evening challenge match. Burao captain covers pitch fees.",
                createdAt = "2026-09-08 15:00"
            )
        )

        fun getInitialGalleryImages(): List<GameImage> = listOf(
            GameImage(
                id = "img-1",
                title = "Night Floodlight Championship Derby",
                somaliTitle = "Ciyaar Xiiso Badan oo Habeen ah",
                category = "Night Floodlights",
                imageUrl = "https://images.unsplash.com/photo-1529900748604-07564a03e7a6?auto=format&fit=crop&w=1200&q=80",
                date = "2026-09-02",
                pitchName = "Pitch 1 - Championship Arena",
                teamsInvolved = "26 June Warriors vs Elman Stars",
                likes = 48
            ),
            GameImage(
                id = "img-2",
                title = "Precision Strike on AstroTurf Pitch 2",
                somaliTitle = "Gool Aad u Qurux Badan Garoonka 2-aad",
                category = "Match Action",
                imageUrl = "https://images.unsplash.com/photo-1551958219-acbc608c6377?auto=format&fit=crop&w=1200&q=80",
                date = "2026-08-29",
                pitchName = "Pitch 2 - Premier Astro Turf",
                teamsInvolved = "Burao United vs Gaashaan Club",
                likes = 35
            ),
            GameImage(
                id = "img-3",
                title = "Trophy Celebration at 26 JSC Cup",
                somaliTitle = "Xafladda Koobka Tartanka 26 JSC",
                category = "Celebration",
                imageUrl = "https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80",
                date = "2026-08-20",
                pitchName = "Pitch 1 - Championship Arena",
                teamsInvolved = "Champions 26 June FC",
                likes = 62
            ),
            GameImage(
                id = "img-4",
                title = "Intensive Futsal & Agility Match",
                somaliTitle = "Ciyaarta Xawaaraha Sare ee Futsal",
                category = "Match Action",
                imageUrl = "https://images.unsplash.com/photo-1518604667004-9ae8f1149957?auto=format&fit=crop&w=1200&q=80",
                date = "2026-08-15",
                pitchName = "Pitch 4 - Skills & Futsal Cage",
                teamsInvolved = "Somali Tigers Futsal Squad",
                likes = 29
            )
        )

        fun getInitialReferrals(): List<ReferralInvite> = listOf(
            ReferralInvite(
                id = "ref-201",
                friendName = "Cabdiraxmaan Jaamac",
                friendPhone = "+252634123456",
                referralCode = "JSC-WARRIOR26",
                status = "COMPLETED",
                bonusPoints = 150,
                date = "2026-09-09",
                bookingReference = "JSC-9041"
            ),
            ReferralInvite(
                id = "ref-202",
                friendName = "Farxaan Warsame",
                friendPhone = "+252634998877",
                referralCode = "JSC-WARRIOR26",
                status = "COMPLETED",
                bonusPoints = 150,
                date = "2026-09-11",
                bookingReference = "JSC-9114"
            )
        )

        fun getInitialReviews(): List<PitchReview> = listOf(
            PitchReview(
                id = "rev-1",
                bookingId = "b-seed-1",
                pitchId = "pitch-1",
                pitchName = "Pitch 1 - Championship Arena",
                customerName = "Jaamac Cilmi",
                teamName = "Hargeisa Stars FC",
                rating = 5,
                comment = "Surface is in immaculate condition! FIFA synthetic grass feels very soft on joints and the high-lumen floodlights made our night derby match feel like a professional stadium.",
                date = "2026-09-10",
                tags = listOf("Smooth Turf", "Bright Floodlights", "Electronic Scoreboard")
            ),
            PitchReview(
                id = "rev-2",
                bookingId = "b-seed-2",
                pitchId = "pitch-1",
                pitchName = "Pitch 1 - Championship Arena",
                customerName = "Khadar Maxamed",
                teamName = "Banaadir United",
                rating = 5,
                comment = "Garoon heer sare ah oo aad u waasac ah! Ileyska habeenkii waa heer caalami ah, kuraasta beddelkuna aad bay u qabow yihiin.",
                date = "2026-09-11",
                tags = listOf("Heer Caalami", "Ileys Fiican", "Kuraas Qabow")
            ),
            PitchReview(
                id = "rev-3",
                bookingId = "b-seed-3",
                pitchId = "pitch-1",
                pitchName = "Pitch 1 - Championship Arena",
                customerName = "Axmed Cali",
                teamName = "26 June Warriors FC",
                rating = 4,
                comment = "Great ball bounce and firm grip even during fast turns. Front desk reception was very courteous with match bibs and ball.",
                date = "2026-09-12",
                tags = listOf("Firm Grip", "Clean Dugouts", "Friendly Staff")
            ),
            PitchReview(
                id = "rev-4",
                bookingId = "b-seed-4",
                pitchId = "pitch-2",
                pitchName = "Pitch 2 - Premier Astro Turf",
                customerName = "Cumar Daahir",
                teamName = "Red Sea Strikers",
                rating = 5,
                comment = "Fastest astro turf in Hargeisa! Perfect ball roll cushioning for 5-a-side quick passing. Our team loved the water cooler setup.",
                date = "2026-09-09",
                tags = listOf("Fast Ball Roll", "Cushioned Turf", "Great Water Setup")
            ),
            PitchReview(
                id = "rev-5",
                bookingId = "b-seed-5",
                pitchId = "pitch-2",
                pitchName = "Pitch 2 - Premier Astro Turf",
                customerName = "Mustafe Nuur",
                teamName = "Shacabka FC",
                rating = 5,
                comment = "Garoonka 2-aad waa kan ugu fiican 5-a-side. Biyaha qabow iyo kubbadaha cusub aad baan ugu riyaaqnay.",
                date = "2026-09-11",
                tags = listOf("Kubad Wanaagsan", "Astro Turf Casri ah")
            ),
            PitchReview(
                id = "rev-6",
                bookingId = "b-seed-6",
                pitchId = "pitch-3",
                pitchName = "Pitch 3 - VIP Floodlight Turf",
                customerName = "Cabdiweli Xasan",
                teamName = "VIP United",
                rating = 5,
                comment = "The VIP shaded lounge area and camera mount for match recording was top notch! Zero slippery spots on the shock pad.",
                date = "2026-09-08",
                tags = listOf("VIP Lounge", "Shock Pad Safety", "HD Camera Mount")
            ),
            PitchReview(
                id = "rev-7",
                bookingId = "b-seed-7",
                pitchId = "pitch-3",
                pitchName = "Pitch 3 - VIP Floodlight Turf",
                customerName = "Ismaaciil Cali",
                teamName = "Horn Stars",
                rating = 4,
                comment = "Excellent floodlights covering every angle of the pitch. Very easy on the knees.",
                date = "2026-09-12",
                tags = listOf("Even Lighting", "Joint Protection")
            ),
            PitchReview(
                id = "rev-8",
                bookingId = "b-seed-8",
                pitchId = "pitch-4",
                pitchName = "Pitch 4 - Skills & Futsal Cage",
                customerName = "Yoonis Axmed",
                teamName = "Futsal Kings",
                rating = 5,
                comment = "The rebound boards around the cage make for non-stop action! Great for intense technical training and futsal skills.",
                date = "2026-09-10",
                tags = listOf("Rebound Boards", "Futsal Skills", "High Intensity")
            )
        )

        fun getInitialWaitlistEntries(): List<WaitlistEntry> {
            val cal = Calendar.getInstance()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

            return listOf(
                WaitlistEntry(
                    id = "wl-101",
                    pitchId = "pitch-1",
                    pitchName = "Pitch 1 - Championship Arena",
                    date = today,
                    slot = "19:00 - 20:00",
                    customerName = "Mustafe Cabdi",
                    customerPhone = "+252634123456",
                    teamName = "Red Sea Tigers FC",
                    notes = "Available immediately if prime slot opens up",
                    status = "WAITING",
                    createdAt = System.currentTimeMillis() - 7200000,
                    createdTimeStr = "Today, 17:15"
                ),
                WaitlistEntry(
                    id = "wl-102",
                    pitchId = "pitch-1",
                    pitchName = "Pitch 1 - Championship Arena",
                    date = today,
                    slot = "19:00 - 20:00",
                    customerName = "Guuleed Maxamed",
                    customerPhone = "+252634887766",
                    teamName = "Hargeisa Falcons",
                    notes = "Squad is warmed up and ready in 26 June area",
                    status = "WAITING",
                    createdAt = System.currentTimeMillis() - 3600000,
                    createdTimeStr = "Today, 18:30"
                ),
                WaitlistEntry(
                    id = "wl-103",
                    pitchId = "pitch-2",
                    pitchName = "Pitch 2 - Premier Astro Turf",
                    date = today,
                    slot = "20:00 - 21:00",
                    customerName = "Cilmi Nuur",
                    customerPhone = "+252634559922",
                    teamName = "Stars of Somaliland",
                    notes = "Can take the pitch anytime after 19:30",
                    status = "WAITING",
                    createdAt = System.currentTimeMillis() - 10800000,
                    createdTimeStr = "Today, 16:00"
                )
            )
        }
    }
}

// Mappers
fun PitchEntity.toModel() = Pitch(
    id = id,
    name = name,
    somaliName = somaliName,
    format = format,
    surface = surface,
    hourlyRate = hourlyRate,
    dayRate = dayRate,
    nightRate = nightRate,
    dimensions = dimensions,
    capacity = capacity,
    features = features,
    imageUrl = imageUrl,
    isFloodlit = isFloodlit,
    status = status
)

fun Pitch.toEntity() = PitchEntity(
    id = id,
    name = name,
    somaliName = somaliName,
    format = format,
    surface = surface,
    hourlyRate = hourlyRate,
    dayRate = dayRate,
    nightRate = nightRate,
    dimensions = dimensions,
    capacity = capacity,
    features = features,
    imageUrl = imageUrl,
    isFloodlit = isFloodlit,
    status = status
)

fun BookingEntity.toModel() = Booking(
    id = id,
    referenceCode = referenceCode,
    pitchId = pitchId,
    pitchName = pitchName,
    date = date,
    startTime = startTime,
    endTime = endTime,
    durationHours = durationHours,
    customerName = customerName,
    teamName = teamName,
    customerPhone = customerPhone,
    customerEmail = customerEmail,
    paymentMethod = paymentMethod,
    paymentStatus = paymentStatus,
    transactionId = transactionId,
    merchantNumber = merchantNumber,
    totalAmount = totalAmount,
    loyaltyPoints = loyaltyPoints,
    referralCodeApplied = referralCodeApplied,
    referralBonusPoints = referralBonusPoints,
    addOns = addOns,
    notes = notes,
    createdAt = createdAt,
    smsConfirmed = smsConfirmed
)

fun Booking.toEntity() = BookingEntity(
    id = id,
    referenceCode = referenceCode,
    pitchId = pitchId,
    pitchName = pitchName,
    date = date,
    startTime = startTime,
    endTime = endTime,
    durationHours = durationHours,
    customerName = customerName,
    teamName = teamName,
    customerPhone = customerPhone,
    customerEmail = customerEmail,
    paymentMethod = paymentMethod,
    paymentStatus = paymentStatus,
    transactionId = transactionId,
    merchantNumber = merchantNumber,
    totalAmount = totalAmount,
    loyaltyPoints = loyaltyPoints,
    referralCodeApplied = referralCodeApplied,
    referralBonusPoints = referralBonusPoints,
    addOns = addOns,
    notes = notes,
    createdAt = createdAt,
    smsConfirmed = smsConfirmed
)

fun BlockedSlotEntity.toModel() = BlockedSlot(
    id = id,
    pitchId = pitchId,
    date = date,
    startTime = startTime,
    endTime = endTime,
    reason = reason
)

fun BlockedSlot.toEntity() = BlockedSlotEntity(
    id = id,
    pitchId = pitchId,
    date = date,
    startTime = startTime,
    endTime = endTime,
    reason = reason
)

fun TeamEntity.toModel() = Team(
    id = id,
    name = name,
    logoEmoji = logoEmoji,
    color = color,
    captainName = captainName,
    captainPhone = captainPhone,
    preferredFormat = preferredFormat,
    skillLevel = skillLevel,
    bio = bio,
    joinedDate = joinedDate,
    stats = stats,
    members = members
)

fun Team.toEntity() = TeamEntity(
    id = id,
    name = name,
    logoEmoji = logoEmoji,
    color = color,
    captainName = captainName,
    captainPhone = captainPhone,
    preferredFormat = preferredFormat,
    skillLevel = skillLevel,
    bio = bio,
    joinedDate = joinedDate,
    stats = stats,
    members = members
)

fun MatchChallengeEntity.toModel() = MatchChallenge(
    id = id,
    challengerTeamId = challengerTeamId,
    challengerTeamName = challengerTeamName,
    challengerCaptainPhone = challengerCaptainPhone,
    challengedTeamId = challengedTeamId,
    challengedTeamName = challengedTeamName,
    challengedCaptainPhone = challengedCaptainPhone,
    pitchId = pitchId,
    pitchName = pitchName,
    date = date,
    slot = slot,
    totalAmount = totalAmount,
    splitMode = splitMode,
    status = status,
    notes = notes,
    bookingRef = bookingRef,
    createdAt = createdAt
)

fun MatchChallenge.toEntity() = MatchChallengeEntity(
    id = id,
    challengerTeamId = challengerTeamId,
    challengerTeamName = challengerTeamName,
    challengerCaptainPhone = challengerCaptainPhone,
    challengedTeamId = challengedTeamId,
    challengedTeamName = challengedTeamName,
    challengedCaptainPhone = challengedCaptainPhone,
    pitchId = pitchId,
    pitchName = pitchName,
    date = date,
    slot = slot,
    totalAmount = totalAmount,
    splitMode = splitMode,
    status = status,
    notes = notes,
    bookingRef = bookingRef,
    createdAt = createdAt
)

fun GalleryImageEntity.toModel() = GameImage(
    id = id,
    title = title,
    somaliTitle = somaliTitle,
    category = category,
    imageUrl = imageUrl,
    date = date,
    pitchName = pitchName,
    teamsInvolved = teamsInvolved,
    likes = likes
)

fun ReferralEntity.toModel() = ReferralInvite(
    id = id,
    friendName = friendName,
    friendPhone = friendPhone,
    referralCode = referralCode,
    status = status,
    bonusPoints = bonusPoints,
    date = date,
    bookingReference = bookingReference
)

fun ReferralInvite.toEntity() = ReferralEntity(
    id = id,
    friendName = friendName,
    friendPhone = friendPhone,
    referralCode = referralCode,
    status = status,
    bonusPoints = bonusPoints,
    date = date,
    bookingReference = bookingReference
)

fun PitchReviewEntity.toModel() = PitchReview(
    id = id,
    bookingId = bookingId,
    pitchId = pitchId,
    pitchName = pitchName,
    customerName = customerName,
    teamName = teamName,
    rating = rating,
    comment = comment,
    date = date,
    tags = tags
)

fun PitchReview.toEntity() = PitchReviewEntity(
    id = id,
    bookingId = bookingId,
    pitchId = pitchId,
    pitchName = pitchName,
    customerName = customerName,
    teamName = teamName,
    rating = rating,
    comment = comment,
    date = date,
    tags = tags
)

fun WaitlistEntryEntity.toModel() = WaitlistEntry(
    id = id,
    pitchId = pitchId,
    pitchName = pitchName,
    date = date,
    slot = slot,
    customerName = customerName,
    customerPhone = customerPhone,
    teamName = teamName,
    notes = notes,
    status = status,
    createdAt = createdAt,
    createdTimeStr = createdTimeStr
)

fun WaitlistEntry.toEntity() = WaitlistEntryEntity(
    id = id,
    pitchId = pitchId,
    pitchName = pitchName,
    date = date,
    slot = slot,
    customerName = customerName,
    customerPhone = customerPhone,
    teamName = teamName,
    notes = notes,
    status = status,
    createdAt = createdAt,
    createdTimeStr = createdTimeStr
)

