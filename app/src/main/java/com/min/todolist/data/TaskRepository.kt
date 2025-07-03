package com.min.todolist.data

import androidx.lifecycle.LiveData

// Repository menjadi perantara antara ViewModel dan sumber data (DAO).
class TaskRepository(private val taskDao: TaskDao) {

    // LiveData yang berisi daftar semua tugas dari DAO.
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    // Fungsi suspend untuk menyisipkan tugas, memanggil fungsi DAO di background thread.
    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    // Fungsi suspend untuk memperbarui tugas.
    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    // Fungsi suspend untuk menghapus tugas.
    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
}
