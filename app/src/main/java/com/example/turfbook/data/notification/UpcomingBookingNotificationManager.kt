package com.example.turfbook.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.turfbook.MainActivity
import com.example.turfbook.data.model.Booking
import com.example.turfbook.data.model.Language
import com.example.turfbook.data.model.UpcomingBookingReminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object UpcomingBookingNotificationManager {

    const val CHANNEL_ID = "turf_upcoming_matches_channel"
    private const val PREFS_NAME = "turf_daily_check_prefs"
    private const val KEY_LAST_CHECK_DATE = "key_last_check_date"
    private const val KEY_NOTIFIED_BOOKINGS = "key_notified_bookings_"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Upcoming Match Reminders"
            val descriptionText = "Daily match alerts for turf bookings scheduled for the upcoming day"
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
     * Executes the daily check for all bookings scheduled for the upcoming day (tomorrow).
     * @param context Application context
     * @param bookings All existing bookings
     * @param forceTrigger If true, triggers notifications even if already run today (useful for testing/manual refresh)
     * @param language Current selected language for notification text
     * @return List of newly triggered upcoming match reminders
     */
    fun performDailyCheck(
        context: Context,
        bookings: List<Booking>,
        forceTrigger: Boolean = false,
        language: Language = Language.EN
    ): List<UpcomingBookingReminder> {
        initNotificationChannel(context)

        val calendar = Calendar.getInstance()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Find bookings scheduled for tomorrow
        val tomorrowBookings = bookings.filter { it.date == tomorrowStr }

        val triggeredReminders = mutableListOf<UpcomingBookingReminder>()

        for (booking in tomorrowBookings) {
            val bookingNotifiedKey = "$KEY_NOTIFIED_BOOKINGS${booking.id}_$todayStr"
            val alreadyNotified = prefs.getBoolean(bookingNotifiedKey, false)

            if (!alreadyNotified || forceTrigger) {
                val reminder = UpcomingBookingReminder(
                    id = "rem-${booking.id}",
                    bookingId = booking.id,
                    bookingReference = booking.referenceCode,
                    pitchName = booking.pitchName,
                    date = booking.date,
                    timeSlot = "${booking.startTime} - ${booking.endTime}",
                    teamName = booking.teamName,
                    customerName = booking.customerName,
                    customerPhone = booking.customerPhone,
                    hoursUntilMatch = 24,
                    notificationMessageEn = "Match Scheduled Tomorrow! ${booking.teamName} plays on ${booking.pitchName} at ${booking.startTime} - ${booking.endTime}. Please arrive 15 minutes before kick-off.",
                    notificationMessageSo = "Ciyaartu waa Barri! Kooxda ${booking.teamName} waxay ku ciyaaraysaa ${booking.pitchName} saacadda ${booking.startTime} - ${booking.endTime}. Fadlan soo gaadha 15 daqiiqo ka hor.",
                    triggeredAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                )

                // Dispatch real Android System Notification
                dispatchSystemNotification(context, reminder, language)

                // Record that notification has been sent today for this booking
                prefs.edit().putBoolean(bookingNotifiedKey, true).apply()

                triggeredReminders.add(reminder)
            }
        }

        // Record last check date
        prefs.edit().putString(KEY_LAST_CHECK_DATE, todayStr).apply()

        return triggeredReminders
    }

    /**
     * Posts a native Android System Notification
     */
    fun dispatchSystemNotification(
        context: Context,
        reminder: UpcomingBookingReminder,
        language: Language
    ) {
        initNotificationChannel(context)

        // Check permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // Permission not yet granted, in-app banner will still alert user
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_TARGET_TAB", 2) // 2: My Bookings
            putExtra("EXTRA_BOOKING_ID", reminder.bookingId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminder.bookingId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (language == Language.SO)
            "⚽ Ciyaar Ballansan Barri: ${reminder.pitchName}"
        else
            "⚽ Match Scheduled Tomorrow: ${reminder.pitchName}"

        val message = if (language == Language.SO)
            reminder.notificationMessageSo
        else
            reminder.notificationMessageEn

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText("${reminder.teamName} • ${reminder.timeSlot}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
                    .setSummaryText("26 JSC TurfBook Match Reminder")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(reminder.bookingId.hashCode(), notificationBuilder.build())
        } catch (_: SecurityException) {
            // Ignored if permissions are revoked
        }
    }

    /**
     * Checks when the daily check was last performed
     */
    fun getLastCheckDate(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LAST_CHECK_DATE, null)
    }
}
