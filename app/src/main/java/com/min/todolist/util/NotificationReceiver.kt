package com.min.todolist.util

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.min.todolist.R
import com.min.todolist.ToDoApplication

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Tugas Akan Datang"
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Jangan lupa kerjakan tugasmu!"
        val notificationId = intent.getIntExtra("EXTRA_ID", 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, ToDoApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_tasks) // Gunakan ikon yang sudah ada
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}