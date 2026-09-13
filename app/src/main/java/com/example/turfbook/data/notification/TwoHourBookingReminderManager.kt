package com.example.turfbook.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.turfbook.MainActivity
import com.example.turfbook.data.model.AppConfig
import com.example.turfbook.data.model.Booking
import com.example.turfbook.data.model.BookingTimeHelper
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.TwoHourBookingReminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TwoHourBookingReminderManager {

    const val CHANNEL_ID = "turf_two_hour_reminders_channel"
    private const val PREFS_NAME = "turf_2hr_reminder_prefs"
    private const val KEY_NOTIFIED_PREFIX = "notified_2hr_"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "2-Hour Match Reminders & Quick Access"
            val descriptionText = "Automated reminders dispatched 2 hours before kickoff with quick access to digital tickets"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Builds the TwoHourBookingReminder data structure with email and notification copies.
     */
    fun buildTwoHourReminder(
        booking: Booking,
        minutesUntil: Long = 120
    ): TwoHourBookingReminder {
        val deepLink = BookingTimeHelper.generateQuickAccessDeepLink(booking.id, booking.referenceCode)
        val webUrl = BookingTimeHelper.generateQuickAccessWebUrl(booking.id)
        val timeFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val targetEmail = booking.customerEmail.ifBlank { "foscar2019@gmail.com" }

        val titleEn = "⚽ Match in 2 Hours! | ${booking.pitchName}"
        val titleSo = "⚽ Ciyaartaadu waxay bilaabmaysaa 2 Saacadood kadib! | ${booking.pitchName}"

        val bodyEn = "${booking.teamName} kick-off is at ${booking.startTime}. Tap for Quick Access to your digital ticket voucher."
        val bodySo = "${booking.teamName} kulankoodu waxuu bilaabmayaa ${booking.startTime}. Taabo si aad u furto Tigidhkaaga Degdegga ah."

        val emailSubject = "⚽ Reminder: Your Match at 26 JSC Arena in 2 Hours! (Ticket #${booking.referenceCode})"

        val emailPlainText = """
            26 JSC TURF ARENA — AUTOMATED 2-HOUR MATCH REMINDER
            ==================================================
            Dear ${booking.customerName},
            
            This is an automated reminder that your upcoming match is scheduled in approximately 2 hours!
            
            MATCH DETAILS:
            • Reference Code: #${booking.referenceCode}
            • Pitch: ${booking.pitchName}
            • Date: ${booking.date}
            • Kick-off: ${booking.startTime} - ${booking.endTime}
            • Team: ${booking.teamName}
            • Arena Location: ${AppConfig.LOCATION_EN}
            • Front Desk: ${AppConfig.CONTACT_PHONE}
            
            🎟️ QUICK ACCESS TO YOUR DIGITAL BOOKING TICKET:
            Open your digital ticket instantly in the TurfBook app:
            $deepLink
            
            Or view via Web Ticket Portal:
            $webUrl
            
            MATCHDAY GUIDELINES:
            1. Please arrive at least 15 minutes prior to kick-off for boots inspection and team check-in.
            2. Present your Digital Ticket QR Code at the main arena turnstile.
            3. Pitch floodlights, bibs, and fresh drinking water are pre-arranged.
            
            Enjoy your match!
            26 JSC Turf Arena Operations Team
        """.trimIndent()

        val emailHtml = """
            <div style="font-family: Arial, sans-serif; background-color: #0A120E; color: #FFFFFF; padding: 20px; border-radius: 12px; border: 1px solid #1A3826;">
                <h2 style="color: #10B981; margin-top: 0;">⚽ 26 JSC TURF ARENA — 2-HOUR MATCH REMINDER</h2>
                <p>Dear <strong>${booking.customerName}</strong>,</p>
                <p>Your turf reservation is starting in approximately <strong>2 hours</strong> at <strong>${booking.startTime}</strong>.</p>
                <div style="background-color: #121F17; border: 1px solid #234E35; border-radius: 8px; padding: 16px; margin: 16px 0;">
                    <p style="margin: 4px 0;"><strong>Match:</strong> ${booking.teamName}</p>
                    <p style="margin: 4px 0;"><strong>Pitch:</strong> ${booking.pitchName}</p>
                    <p style="margin: 4px 0;"><strong>Date & Time:</strong> ${booking.date} (${booking.startTime} - ${booking.endTime})</p>
                    <p style="margin: 4px 0;"><strong>Booking Reference:</strong> <span style="color: #F59E0B; font-weight: bold;">#${booking.referenceCode}</span></p>
                </div>
                <p style="text-align: center; margin: 24px 0;">
                    <a href="$deepLink" style="background-color: #10B981; color: #0A120E; padding: 12px 24px; text-decoration: none; font-weight: bold; border-radius: 8px; display: inline-block;">🎟️ QUICK ACCESS DIGITAL TICKET</a>
                </p>
                <p style="font-size: 12px; color: #9CA3AF;">Link fallback: <a href="$webUrl" style="color: #10B981;">$webUrl</a></p>
                <hr style="border: 0; border-top: 1px solid #234E35; margin: 20px 0;" />
                <p style="font-size: 11px; color: #6B7280;">26 JSC Sports Arena • 26 June District, Hargeisa • Tel: ${AppConfig.CONTACT_PHONE}</p>
            </div>
        """.trimIndent()

        return TwoHourBookingReminder(
            id = "rem-2hr-${booking.id}",
            bookingId = booking.id,
            bookingReference = booking.referenceCode,
            pitchName = booking.pitchName,
            date = booking.date,
            startTime = booking.startTime,
            endTime = booking.endTime,
            teamName = booking.teamName,
            customerName = booking.customerName,
            customerPhone = booking.customerPhone,
            customerEmail = targetEmail,
            quickAccessLink = deepLink,
            webAccessLink = webUrl,
            notificationTitleEn = titleEn,
            notificationTitleSo = titleSo,
            notificationBodyEn = bodyEn,
            notificationBodySo = bodySo,
            emailSubject = emailSubject,
            emailBodyPlainText = emailPlainText,
            emailBodyHtml = emailHtml,
            triggeredAt = timeFormatted,
            minutesUntilKickoff = if (minutesUntil > 0) minutesUntil else 120,
            emailDispatched = true,
            pushDispatched = true
        )
    }

    /**
     * Checks all bookings and dispatches automated 2-hour reminders for matches occurring ~2 hours from now.
     */
    fun checkAndDispatchTwoHourReminders(
        context: Context,
        bookings: List<Booking>,
        forceBookingId: String? = null,
        language: Language = Language.EN
    ): List<TwoHourBookingReminder> {
        initNotificationChannel(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val now = Date()
        val dispatched = mutableListOf<TwoHourBookingReminder>()

        for (booking in bookings) {
            val isForced = forceBookingId == booking.id
            val minutesUntil = BookingTimeHelper.getMinutesUntilMatch(booking.date, booking.startTime, now)
            val isWithin2h = BookingTimeHelper.isWithinTwoHourWindow(booking.date, booking.startTime, now)

            val cacheKey = "$KEY_NOTIFIED_PREFIX${booking.id}_${booking.date}"
            val alreadySent = prefs.getBoolean(cacheKey, false)

            if (isForced || (isWithin2h && !alreadySent)) {
                val reminder = buildTwoHourReminder(booking, if (minutesUntil > 0) minutesUntil else 120)

                // Dispatch native Android system notification with Quick Access Action
                dispatchSystemNotification(context, reminder, language)

                // Save dispatched flag in preferences
                prefs.edit().putBoolean(cacheKey, true).apply()
                dispatched.add(reminder)
            }
        }

        return dispatched
    }

    /**
     * Dispatches native Android System Notification with Quick Access Action Button.
     */
    fun dispatchSystemNotification(
        context: Context,
        reminder: TwoHourBookingReminder,
        language: Language
    ) {
        initNotificationChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        // Intent for main notification click -> opens app directly to the digital ticket
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_TARGET_TAB", 2)
            putExtra("EXTRA_QUICK_ACCESS_BOOKING_ID", reminder.bookingId)
            data = Uri.parse(reminder.quickAccessLink)
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            ("2hr_content_" + reminder.bookingId).hashCode(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent for specific "Quick Access Ticket" Action Button
        val ticketActionIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_TARGET_TAB", 2)
            putExtra("EXTRA_QUICK_ACCESS_BOOKING_ID", reminder.bookingId)
            data = Uri.parse(reminder.quickAccessLink)
        }

        val ticketActionPendingIntent = PendingIntent.getActivity(
            context,
            ("2hr_action_" + reminder.bookingId).hashCode(),
            ticketActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (language == Language.SO) reminder.notificationTitleSo else reminder.notificationTitleEn
        val message = if (language == Language.SO) reminder.notificationBodySo else reminder.notificationBodyEn
        val actionLabel = if (language == Language.SO) "🎟️ Tigidhka Degdegga ah" else "🎟️ Quick Access Ticket"

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$message\n\n📧 Automated confirmation email dispatched to ${reminder.customerEmail}.\nRef: #${reminder.bookingReference}")
                    .setSummaryText("26 JSC TurfBook • 2-Hour Alert")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_agenda,
                actionLabel,
                ticketActionPendingIntent
            )

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(("2hr_" + reminder.bookingId).hashCode(), notificationBuilder.build())
        } catch (_: SecurityException) {
            // Ignored if permissions are revoked
        }
    }

    /**
     * Creates an Intent to launch the user's email client with pre-filled reminder content.
     */
    fun createEmailClientIntent(reminder: TwoHourBookingReminder): Intent {
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${reminder.customerEmail}")
            putExtra(Intent.EXTRA_SUBJECT, reminder.emailSubject)
            putExtra(Intent.EXTRA_TEXT, reminder.emailBodyPlainText)
        }
    }
}
