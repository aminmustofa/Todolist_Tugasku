package com.min.todolist.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    /**
     * Menyisipkan satu tugas ke dalam tabel.
     * Jika tugas dengan Primary Key yang sama sudah ada, maka akan diganti.
     * @param task Objek tugas yang akan disisipkan.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    /**
     * Memperbarui tugas yang sudah ada di dalam database.
     * Room akan mencari tugas berdasarkan Primary Key dari objek yang diberikan.
     * @param task Objek tugas yang akan diperbarui.
     */
    @Update
    suspend fun updateTask(task: Task)

    /**
     * Menghapus tugas dari database.
     * Room akan mencari tugas yang akan dihapus berdasarkan Primary Key.
     * @param task Objek tugas yang akan dihapus.
     */
    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * Mengambil semua tugas dari tabel.
     * Daftar ini dibungkus dalam LiveData agar UI dapat secara otomatis
     * diperbarui saat data berubah.
     * Tugas diurutkan berdasarkan tanggal jatuh tempo (dueDate) secara menaik.
     * @return LiveData yang berisi daftar semua objek Task.
     */
    @Query("SELECT * FROM task_table ORDER BY dueDate ASC")
    fun getAllTasks(): LiveData<List<Task>>
}
