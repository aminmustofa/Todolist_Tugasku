package com.min.todolist.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.min.todolist.data.Task

object AlarmScheduler {

    // Konstanta untuk 30 menit dalam milidetik
    private const val THIRTY_MINUTES_IN_MILLIS = 30 * 60 * 1000

    fun schedule(context: Context, task: Task) {
        // Hitung waktu notifikasi: 30 menit sebelum waktu mulai tugas
        val notificationTime = task.startTime - THIRTY_MINUTES_IN_MILLIS

        // Hanya jadwalkan jika waktu notifikasi masih di masa depan
        if (task.startTime == 0L || notificationTime <= System.currentTimeMillis()) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", "Pengingat Tugas: ${task.title}")
            putExtra("EXTRA_MESSAGE", "Tugas akan dimulai dalam 30 menit.")
            putExtra("EXTRA_ID", task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Jadwalkan alarm pada waktu notifikasi yang sudah dihitung
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notificationTime,
            pendingIntent
        )
    }

    fun cancel(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}