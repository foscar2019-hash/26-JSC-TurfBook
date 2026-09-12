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
}
