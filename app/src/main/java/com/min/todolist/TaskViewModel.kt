package com.min.todolist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
// Pastikan import di bawah ini sudah benar
import com.min.todolist.data.Task
import com.min.todolist.data.TaskDatabase
import com.min.todolist.data.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    private val allTasks: LiveData<List<Task>>

    val upcomingTasks: LiveData<List<Task>>
    val completedTasks: LiveData<List<Task>>

    init {
        // Pastikan TaskDao di sini juga berasal dari package 'data'
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao) // <-- Error mismatch terjadi di sini
        allTasks = repository.allTasks

        upcomingTasks = allTasks.map { tasks ->
            tasks.filter { !it.isCompleted }
        }

        completedTasks = allTasks.map { tasks ->
            tasks.filter { it.isCompleted }
        }
    }

    // ... sisa fungsi (insert, update, delete) tidak perlu diubah ...
    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
}