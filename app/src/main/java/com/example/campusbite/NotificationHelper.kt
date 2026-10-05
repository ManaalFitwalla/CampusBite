package com.example.campusbite

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat

object NotificationHelper {

    private const val CHANNEL_ID = "campus_bite_orders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Order Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun schedulePickupReminder(context: Context, pickupTime: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("CampusBite Pickup Reminder")
            .setContentText("Your pickup time is $pickupTime (10 minutes remaining!)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Simulating 10-minute warning notification
        Handler(Looper.getMainLooper()).postDelayed({
            manager.notify(101, builder.build())
        }, 3000)
    }

    fun scheduleOrderReadyNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Order Ready! 🍔")
            .setContentText("Your order is ready for pickup at the canteen counter!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Simulating order completion state
        Handler(Looper.getMainLooper()).postDelayed({
            manager.notify(102, builder.build())
        }, 8000)
    }
}