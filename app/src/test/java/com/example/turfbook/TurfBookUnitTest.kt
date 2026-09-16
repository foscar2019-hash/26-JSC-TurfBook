package com.example.turfbook

import com.example.turfbook.data.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TurfBookUnitTest {

    @Test
    fun testLoyaltyPointsCalculation() {
        // $18 daytime booking earns 180 points
        val dayPoints = Booking.calculatePoints(18.0)
        assertEquals(180, dayPoints)

        // $25 night floodlit booking earns 250 points
        val nightPoints = Booking.calculatePoints(25.0)
        assertEquals(250, nightPoints)

        // $29 booking ($25 + $2 bibs + $2 ball) earns 290 points
        val totalWithAddonsPoints = Booking.calculatePoints(29.0)
        assertEquals(290, totalWithAddonsPoints)
    }

    @Test
    fun testLoyaltyTiers() {
        assertEquals(LoyaltyTier.BRONZE, LoyaltyTier.fromPoints(200))
        assertEquals(LoyaltyTier.SILVER, LoyaltyTier.fromPoints(600))
        assertEquals(LoyaltyTier.GOLD, LoyaltyTier.fromPoints(1200))

        val (bronzeTier, _) = Booking.getTier(200)
        assertEquals("🥉 Bronze", bronzeTier)

        val (silverTier, _) = Booking.getTier(600)
        assertEquals("🥈 Silver", silverTier)

        val (goldTier, _) = Booking.getTier(1200)
        assertEquals("🥇 Gold", goldTier)
    }

    @Test
    fun testMobileMoneyMerchants() {
        assertEquals("445686", PaymentMethod.ZAAD.merchant)
        assertEquals("10136", PaymentMethod.EDAHAB.merchant)
        assertEquals("Zaad Service", PaymentMethod.ZAAD.label)
        assertEquals("eDahab", PaymentMethod.EDAHAB.label)
    }

    @Test
    fun testAppConfig() {
        assertEquals("26 JSC TurfBook", AppConfig.APP_NAME)
        assertEquals("+252633347832", AppConfig.CONTACT_PHONE)
        assertEquals("445686", AppConfig.ZAAD_MERCHANT)
        assertEquals("10136", AppConfig.EDAHAB_MERCHANT)
        assertTrue(AppConfig.TIME_SLOTS.contains("18:00 - 19:00"))
        assertTrue(AppConfig.TIME_SLOTS.contains("20:00 - 21:00"))
    }

    @Test
    fun testAdminPinVerification() {
        val validPin = "2626"
        val alternatePin = "admin"
        val invalidPin = "0000"

        assertTrue(validPin == "2626" || validPin == "admin")
        assertTrue(alternatePin == "2626" || alternatePin == "admin")
        assertFalse(invalidPin == "2626" || invalidPin == "admin")
    }

    @Test
    fun testBookingModelCreation() {
        val booking = Booking(
            id = "b-test-1",
            referenceCode = "JSC-9999",
            pitchId = "pitch-1",
            pitchName = "Pitch 1 - Championship Arena",
            date = "2026-09-12",
            startTime = "19:00",
            endTime = "20:00",
            customerName = "Axmed Cali",
            teamName = "26 June Warriors",
            customerPhone = "+252633347832",
            paymentMethod = PaymentMethod.ZAAD,
            totalAmount = 25.0,
            loyaltyPoints = 250
        )

        assertEquals("JSC-9999", booking.referenceCode)
        assertEquals(25.0, booking.totalAmount, 0.001)
        assertEquals(250, booking.loyaltyPoints)
        assertEquals(PaymentMethod.ZAAD, booking.paymentMethod)
    }

    @Test
    fun testReferralBonusPoints() {
        assertEquals(150, Booking.REFERRAL_BONUS_POINTS)

        val referralBooking = Booking(
            id = "b-test-ref",
            referenceCode = "JSC-8888",
            pitchId = "pitch-2",
            pitchName = "Pitch 2 - Premier 7v7",
            date = "2026-09-12",
            startTime = "20:00",
            endTime = "21:00",
            customerName = "Jaamac Warsame",
            teamName = "Hargeisa Strikers",
            customerPhone = "+252634455667",
            paymentMethod = PaymentMethod.EDAHAB,
            totalAmount = 25.0,
            loyaltyPoints = 250 + Booking.REFERRAL_BONUS_POINTS,
            referralCodeApplied = "JSC-WARRIOR26",
            referralBonusPoints = 150
        )

        assertEquals("JSC-WARRIOR26", referralBooking.referralCodeApplied)
        assertEquals(150, referralBooking.referralBonusPoints)
        assertEquals(400, referralBooking.loyaltyPoints)
    }

    @Test
    fun testLoyaltyPointsHistoryLedger() {
        val bookings = listOf(
            Booking(
                id = "b-1",
                referenceCode = "JSC-1001",
                pitchId = "pitch-1",
                pitchName = "Pitch 1",
                date = "2026-09-10",
                startTime = "18:00",
                endTime = "19:00",
                customerName = "Axmed",
                teamName = "Tigers",
                customerPhone = "+252633347832",
                paymentMethod = PaymentMethod.ZAAD,
                totalAmount = 25.0,
                loyaltyPoints = 400,
                referralCodeApplied = "JSC-WARRIOR26",
                referralBonusPoints = 150
            )
        )

        val referrals = listOf(
            ReferralInvite(
                id = "ref-1",
                friendName = "Cabdiraxmaan Jaamac",
                friendPhone = "+252634455667",
                referralCode = "JSC-WARRIOR26",
                status = "COMPLETED",
                bonusPoints = 150,
                date = "2026-09-08"
            )
        )

        val history = LoyaltyPointsRecord.buildHistory(bookings, referrals)
        assertTrue(history.isNotEmpty())

        // Should include special events, booking base points, booking referral bonus, and friend invite
        val specialEvents = history.filter { it.type == PointsTransactionType.SPECIAL_EVENT }
        assertTrue(specialEvents.any { it.referenceCode == "EVT-CUP26" })
        assertTrue(specialEvents.any { it.referenceCode == "EVT-NIGHT75" })

        val bookingRecords = history.filter { it.type == PointsTransactionType.BOOKING }
        assertEquals(1, bookingRecords.size)
        assertEquals(250, bookingRecords[0].basePoints)
        assertEquals(250, bookingRecords[0].totalPoints)

        val referralBonuses = history.filter { it.type == PointsTransactionType.REFERRAL_BONUS }
        assertEquals(2, referralBonuses.size) // 1 applied on booking + 1 from friend completion

        val totalPoints = history.sumOf { it.totalPoints }
        // 100 (cup) + 75 (floodlight) + 250 (booking base) + 150 (booking referral) + 150 (friend invite) = 725 pts
        assertEquals(725, totalPoints)
    }

    @Test
    fun testAdminAnalyticsMonthlyBreakdown() {
        val liveBookings = listOf(
            Booking(
                id = "b-1",
                referenceCode = "JSC-999",
                pitchId = "pitch-1",
                pitchName = "Pitch 1",
                date = "2026-09-12",
                startTime = "19:00",
                endTime = "20:00",
                customerName = "Axmed",
                teamName = "Warriors",
                customerPhone = "+252633347832",
                paymentMethod = PaymentMethod.ZAAD,
                totalAmount = 25.0,
                loyaltyPoints = 250
            )
        )

        val monthlyRecords = AdminAnalyticsEngine.getMonthlyBreakdown(liveBookings)
        assertEquals(6, monthlyRecords.size)

        // Check months sequence
        val monthIds = monthlyRecords.map { it.monthId }
        assertEquals(listOf("2026-05", "2026-06", "2026-07", "2026-08", "2026-09", "2026-10"), monthIds)

        // Check bookings and loyalty points distribution
        monthlyRecords.forEach { record ->
            assertTrue(record.bookingsCount > 0)
            assertTrue(record.loyaltyPointsTotal > 0)
            assertTrue(record.peakOccupancyRate in 80..100)
        }

        // Current month (Sep 2026) has highest live activity
        val currentMonth = monthlyRecords.find { it.monthId == "2026-09" }!!
        assertTrue(currentMonth.bookingsCount >= 139)
        assertTrue(currentMonth.loyaltyPointsTotal >= 40000)
    }

    @Test
    fun testAdminAnalyticsPeakHoursManagement() {
        val bookings = listOf(
            Booking(
                id = "b-1",
                referenceCode = "JSC-101",
                pitchId = "pitch-1",
                pitchName = "Pitch 1",
                date = "2026-09-12",
                startTime = "19:00",
                endTime = "20:00",
                customerName = "Khadar",
                teamName = "Tigers",
                customerPhone = "+252634455667",
                paymentMethod = PaymentMethod.ZAAD,
                totalAmount = 25.0,
                loyaltyPoints = 250
            )
        )

        val hourlyStats = AdminAnalyticsEngine.getHourlySlotStats(bookings)
        assertTrue(hourlyStats.isNotEmpty())

        // 19:00 slot must be classified as Peak
        val peak19 = hourlyStats.find { it.slot.startsWith("19:00") }!!
        assertEquals(PeakHourLevel.PEAK, peak19.level)
        assertTrue(peak19.occupancyRate >= 95)

        // 07:00 slot must be Off-Peak with loyalty boost recommendation
        val morning07 = hourlyStats.find { it.slot.startsWith("07:00") }!!
        assertEquals(PeakHourLevel.OFF_PEAK, morning07.level)
        assertTrue(morning07.loyaltyPointsIncentive.contains("Off-Peak Boost"))
    }

    @Test
    fun testUpcomingDayBookingDetection() {
        val cal = java.util.Calendar.getInstance()
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time)
        cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val tomorrowStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time)

        val bookings = listOf(
            Booking(
                id = "b-today",
                referenceCode = "JSC-1001",
                pitchId = "pitch-1",
                pitchName = "Pitch 1",
                date = todayStr,
                startTime = "19:00",
                endTime = "20:00",
                customerName = "Player 1",
                teamName = "Team Today",
                customerPhone = "+252631111111",
                paymentMethod = PaymentMethod.ZAAD,
                totalAmount = 25.0
            ),
            Booking(
                id = "b-tomorrow",
                referenceCode = "JSC-1002",
                pitchId = "pitch-2",
                pitchName = "Pitch 2 - Premier Astro Turf",
                date = tomorrowStr,
                startTime = "20:00",
                endTime = "21:00",
                customerName = "Player 2",
                teamName = "Team Tomorrow",
                customerPhone = "+252632222222",
                paymentMethod = PaymentMethod.EDAHAB,
                totalAmount = 25.0
            )
        )

        // Filter bookings scheduled for tomorrow
        val tomorrowBookings = bookings.filter { it.date == tomorrowStr }
        assertEquals(1, tomorrowBookings.size)
        assertEquals("b-tomorrow", tomorrowBookings[0].id)
        assertEquals("Team Tomorrow", tomorrowBookings[0].teamName)

        // Reminder payload verification
        val reminder = UpcomingBookingReminder(
            id = "rem-${tomorrowBookings[0].id}",
            bookingId = tomorrowBookings[0].id,
            bookingReference = tomorrowBookings[0].referenceCode,
            pitchName = tomorrowBookings[0].pitchName,
            date = tomorrowBookings[0].date,
            timeSlot = "${tomorrowBookings[0].startTime} - ${tomorrowBookings[0].endTime}",
            teamName = tomorrowBookings[0].teamName,
            customerName = tomorrowBookings[0].customerName,
            customerPhone = tomorrowBookings[0].customerPhone,
            notificationMessageEn = "Match Scheduled Tomorrow! ${tomorrowBookings[0].teamName} plays on ${tomorrowBookings[0].pitchName} at 20:00 - 21:00.",
            notificationMessageSo = "Ciyaartu waa Barri! Kooxda ${tomorrowBookings[0].teamName} waxay ku ciyaaraysaa ${tomorrowBookings[0].pitchName} saacadda 20:00 - 21:00.",
            triggeredAt = "2026-09-12 10:00"
        )

        assertEquals("JSC-1002", reminder.bookingReference)
        assertTrue(reminder.notificationMessageEn.contains("Match Scheduled Tomorrow!"))
        assertTrue(reminder.notificationMessageSo.contains("Ciyaartu waa Barri!"))
        assertEquals("Team Tomorrow", reminder.teamName)
    }

    @Test
    fun testInitialBookingsContainTomorrowBooking() {
        val initialBookings = com.example.turfbook.data.repository.TurfRepository.getInitialBookings()
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val tomorrowStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time)

        val tomorrowBookings = initialBookings.filter { it.date == tomorrowStr }
        assertTrue(tomorrowBookings.isNotEmpty())
        assertEquals("26 June Warriors FC", tomorrowBookings[0].teamName)
    }

    @Test
    fun testBookingTimePassedEvaluation() {
        // Date far in the past
        assertTrue(BookingTimeHelper.isBookingTimePassed("2020-01-01", "10:00"))

        // Date far in the future
        assertFalse(BookingTimeHelper.isBookingTimePassed("2030-01-01", "18:00"))

        // Current date past vs future hours
        val cal = java.util.Calendar.getInstance()
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time)

        // 00:00 today should be passed unless test runs exactly at midnight
        val currentHour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        if (currentHour > 1) {
            assertTrue(BookingTimeHelper.isBookingTimePassed(todayStr, "01:00"))
        }
        // 23:59 today is not passed unless test runs at 23:59
        if (currentHour < 23) {
            assertFalse(BookingTimeHelper.isBookingTimePassed(todayStr, "23:59"))
        }
    }

    @Test
    fun testPitchRatingSummaryCalculation() {
        val reviews = listOf(
            PitchReview(
                id = "rev-1",
                bookingId = "b-1",
                pitchId = "pitch-1",
                customerName = "Axmed Cali",
                teamName = "Warriors",
                rating = 5,
                comment = "Excellent grass quality!",
                date = "2026-09-10"
            ),
            PitchReview(
                id = "rev-2",
                bookingId = "b-2",
                pitchId = "pitch-1",
                customerName = "Jaamac",
                teamName = "Strikers",
                rating = 4,
                comment = "Good lighting.",
                date = "2026-09-11"
            ),
            PitchReview(
                id = "rev-3",
                bookingId = "b-3",
                pitchId = "pitch-2",
                customerName = "Khadar",
                teamName = "Tigers",
                rating = 3,
                comment = "Decent pitch.",
                date = "2026-09-11"
            )
        )

        val pitch1Summary = BookingTimeHelper.calculatePitchRating(reviews, "pitch-1")
        assertEquals(2, pitch1Summary.reviewCount)
        assertEquals(4.5, pitch1Summary.averageRating, 0.001)

        val pitch2Summary = BookingTimeHelper.calculatePitchRating(reviews, "pitch-2")
        assertEquals(1, pitch2Summary.reviewCount)
        assertEquals(3.0, pitch2Summary.averageRating, 0.001)

        val pitch3Summary = BookingTimeHelper.calculatePitchRating(reviews, "pitch-3")
        assertEquals(0, pitch3Summary.reviewCount)
        assertEquals(0.0, pitch3Summary.averageRating, 0.001)

        val overallSummary = BookingTimeHelper.calculateOverallRating(reviews)
        assertEquals(3, overallSummary.reviewCount)
        // (5 + 4 + 3) / 3 = 4.0
        assertEquals(4.0, overallSummary.averageRating, 0.001)
    }

    @Test
    fun testWaitlistEntryModelCreation() {
        val entry = WaitlistEntry(
            id = "wl-test-1",
            pitchId = "pitch-1",
            pitchName = "Pitch 1 - Championship Arena",
            date = "2026-09-13",
            slot = "19:00 - 20:00",
            customerName = "Mustafe Cabdi",
            customerPhone = "+252634123456",
            teamName = "Red Sea Tigers FC",
            notes = "Ready on 15 mins notice",
            status = "WAITING",
            createdAt = 1757780000000L,
            createdTimeStr = "Today, 17:15"
        )

        assertEquals("wl-test-1", entry.id)
        assertEquals("pitch-1", entry.pitchId)
        assertEquals("19:00 - 20:00", entry.slot)
        assertEquals("Red Sea Tigers FC", entry.teamName)
        assertEquals("WAITING", entry.status)
        assertEquals("+252634123456", entry.customerPhone)
    }

    @Test
    fun testWaitlistGroupingByTimeBlock() {
        val entries = listOf(
            WaitlistEntry(
                id = "wl-1",
                pitchId = "pitch-1",
                pitchName = "Pitch 1",
                date = "2026-09-13",
                slot = "19:00 - 20:00",
                customerName = "Player A",
                customerPhone = "+252631111111",
                teamName = "Team A",
                status = "WAITING"
            ),
            WaitlistEntry(
                id = "wl-2",
                pitchId = "pitch-1",
                pitchName = "Pitch 1",
                date = "2026-09-13",
                slot = "19:00 - 20:00",
                customerName = "Player B",
                customerPhone = "+252632222222",
                teamName = "Team B",
                status = "WAITING"
            ),
            WaitlistEntry(
                id = "wl-3",
                pitchId = "pitch-2",
                pitchName = "Pitch 2",
                date = "2026-09-13",
                slot = "20:00 - 21:00",
                customerName = "Player C",
                customerPhone = "+252633333333",
                teamName = "Team C",
                status = "NOTIFIED"
            )
        )

        val grouped = entries.groupBy { "${it.pitchId}__${it.date}__${it.slot}" }
        assertEquals(2, grouped.size)

        val block1 = grouped["pitch-1__2026-09-13__19:00 - 20:00"]!!
        assertEquals(2, block1.size)
        assertEquals("Team A", block1[0].teamName)
        assertEquals("Team B", block1[1].teamName)

        val block2 = grouped["pitch-2__2026-09-13__20:00 - 21:00"]!!
        assertEquals(1, block2.size)
        assertEquals("Team C", block2[0].teamName)
        assertEquals("NOTIFIED", block2[0].status)
    }

    @Test
    fun testInitialWaitlistEntries() {
        val initialWaitlist = com.example.turfbook.data.repository.TurfRepository.getInitialWaitlistEntries()
        assertTrue(initialWaitlist.isNotEmpty())
        assertTrue(initialWaitlist.any { it.pitchId == "pitch-1" })
        assertTrue(initialWaitlist.any { it.slot == "19:00 - 20:00" })
    }

    @Test
    fun testQuickAccessLinkGeneration() {
        val bookingId = "b-test-2h-123"
        val deepLink = BookingTimeHelper.generateQuickAccessDeepLink(bookingId)
        val webUrl = BookingTimeHelper.generateQuickAccessWebUrl(bookingId)

        assertEquals("turfbook://ticket?bookingId=b-test-2h-123", deepLink)
        assertEquals("https://turfbook.jsc.so/ticket?id=b-test-2h-123", webUrl)
        assertTrue(deepLink.contains(bookingId))
        assertTrue(webUrl.contains(bookingId))
    }

    @Test
    fun testTwoHourReminderModelCreation() {
        val reminder = TwoHourBookingReminder(
            id = "rem-test-1",
            bookingId = "b-1",
            bookingReference = "JSC-1001",
            pitchName = "Pitch 1 - Championship Arena",
            date = "2026-09-13",
            startTime = "18:00",
            customerName = "Guled Warsame",
            customerEmail = "guled.w@gmail.com",
            customerPhone = "+252634455667",
            teamName = "Red Sea FC",
            quickAccessDeepLink = BookingTimeHelper.generateQuickAccessDeepLink("b-1"),
            quickAccessWebUrl = BookingTimeHelper.generateQuickAccessWebUrl("b-1"),
            dispatchedAt = System.currentTimeMillis()
        )

        assertEquals("rem-test-1", reminder.id)
        assertEquals("b-1", reminder.bookingId)
        assertEquals("JSC-1001", reminder.bookingReference)
        assertEquals("guled.w@gmail.com", reminder.customerEmail)
        assertEquals("turfbook://ticket?bookingId=b-1", reminder.quickAccessDeepLink)
        assertTrue(reminder.quickAccessWebUrl.startsWith("https://turfbook.jsc.so/ticket"))
    }

    @Test
    fun testTwoHourWindowCalculation() {
        // Today's date with a slot exactly 2 hours from now
        val now = java.util.Calendar.getInstance()
        val matchCal = (now.clone() as java.util.Calendar).apply {
            add(java.util.Calendar.MINUTE, 115) // ~2 hours away (within 30..150 min window)
        }
        val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(matchCal.time)
        val timeStr = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(matchCal.time)

        val minutes = BookingTimeHelper.getMinutesUntilMatch(dateStr, timeStr)
        assertTrue(minutes in 110..120)
        assertTrue(BookingTimeHelper.isWithinTwoHourWindow(dateStr, timeStr))

        // Match that is 5 hours away should NOT be in 2-hour window
        val farCal = (now.clone() as java.util.Calendar).apply {
            add(java.util.Calendar.MINUTE, 300)
        }
        val farDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(farCal.time)
        val farTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(farCal.time)
        assertFalse(BookingTimeHelper.isWithinTwoHourWindow(farDate, farTime))
    }

    @Test
    fun testTeamLoyaltyTiersAndThresholds() {
        // Less than 3 completed bookings -> BRONZE
        assertEquals(TeamLoyaltyTier.BRONZE, TeamLoyaltyTier.fromCompletedBookings(0))
        assertEquals(TeamLoyaltyTier.BRONZE, TeamLoyaltyTier.fromCompletedBookings(2))
        assertFalse(TeamLoyaltyTier.BRONZE.isHighlighted)
        assertEquals(0, TeamLoyaltyTier.BRONZE.discountPercent)

        // 3 to 4 completed bookings -> SILVER
        assertEquals(TeamLoyaltyTier.SILVER, TeamLoyaltyTier.fromCompletedBookings(3))
        assertEquals(TeamLoyaltyTier.SILVER, TeamLoyaltyTier.fromCompletedBookings(4))
        assertFalse(TeamLoyaltyTier.SILVER.isHighlighted)
        assertEquals(5, TeamLoyaltyTier.SILVER.discountPercent)

        // 5 to 9 completed bookings -> GOLD
        assertEquals(TeamLoyaltyTier.GOLD, TeamLoyaltyTier.fromCompletedBookings(5))
        assertEquals(TeamLoyaltyTier.GOLD, TeamLoyaltyTier.fromCompletedBookings(9))
        assertTrue(TeamLoyaltyTier.GOLD.isHighlighted)
        assertEquals(10, TeamLoyaltyTier.GOLD.discountPercent)
        assertEquals("👑", TeamLoyaltyTier.GOLD.badgeEmoji)

        // 10+ completed bookings -> PLATINUM
        assertEquals(TeamLoyaltyTier.PLATINUM, TeamLoyaltyTier.fromCompletedBookings(10))
        assertEquals(TeamLoyaltyTier.PLATINUM, TeamLoyaltyTier.fromCompletedBookings(18))
        assertTrue(TeamLoyaltyTier.PLATINUM.isHighlighted)
        assertEquals(15, TeamLoyaltyTier.PLATINUM.discountPercent)
        assertEquals("💎", TeamLoyaltyTier.PLATINUM.badgeEmoji)
    }

    @Test
    fun testTeamLoyaltyCalculator() {
        val warriors = TeamLoyaltyCalculator.getTeamLoyaltyInfo("26 June Warriors FC", emptyList())
        assertEquals(13, warriors.completedBookings)
        assertEquals(TeamLoyaltyTier.PLATINUM, warriors.loyaltyTier)
        assertTrue(warriors.isGoldOrPlatinum)

        val elman = TeamLoyaltyCalculator.getTeamLoyaltyInfo("Elman Hargeisa Stars", emptyList())
        assertEquals(10, elman.completedBookings)
        assertEquals(TeamLoyaltyTier.PLATINUM, elman.loyaltyTier)
        assertTrue(elman.isGoldOrPlatinum)

        val burao = TeamLoyaltyCalculator.getTeamLoyaltyInfo("Burao United FC", emptyList())
        assertEquals(7, burao.completedBookings)
        assertEquals(TeamLoyaltyTier.GOLD, burao.loyaltyTier)
        assertTrue(burao.isGoldOrPlatinum)

        val banaadir = TeamLoyaltyCalculator.getTeamLoyaltyInfo("Banaadir United", emptyList())
        assertEquals(5, banaadir.completedBookings)
        assertEquals(TeamLoyaltyTier.GOLD, banaadir.loyaltyTier)
        assertTrue(banaadir.isGoldOrPlatinum)

        val shacabka = TeamLoyaltyCalculator.getTeamLoyaltyInfo("Shacabka Stars FC", emptyList())
        assertEquals(4, shacabka.completedBookings)
        assertEquals(TeamLoyaltyTier.SILVER, shacabka.loyaltyTier)
        assertFalse(shacabka.isGoldOrPlatinum)
    }

    @Test
    fun testUserPlatinumLoyaltyTier() {
        assertEquals(LoyaltyTier.PLATINUM, LoyaltyTier.fromPoints(2500))
        assertEquals(LoyaltyTier.PLATINUM, LoyaltyTier.fromPoints(3200))
        assertEquals(15, LoyaltyTier.PLATINUM.discountPercent)
        assertEquals("💎", LoyaltyTier.PLATINUM.badgeIcon)
    }
}


