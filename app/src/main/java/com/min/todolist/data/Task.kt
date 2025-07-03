package com.min.todolist.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "task_table")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: Long,
    // Tambahkan field baru, beri nilai default agar tidak merusak data lama
    val startTime: Long = 0,
    val endTime: Long = 0,
    val category: String,
    var isCompleted: Boolean = false
) : Parcelable {
    val formattedDate: String
        get() = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(dueDate))

    // Properti baru untuk memformat rentang waktu
    val formattedTime: String
        get() {
            // Hanya tampilkan jika waktu sudah diatur
            if (startTime != 0L && endTime != 0L) {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                return "${timeFormat.format(Date(startTime))} - ${timeFormat.format(Date(endTime))}"
            }
            return ""
        }
}
