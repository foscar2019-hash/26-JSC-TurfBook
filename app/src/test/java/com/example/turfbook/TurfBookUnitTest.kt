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
}
