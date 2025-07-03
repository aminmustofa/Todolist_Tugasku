package com.min.todolist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.min.todolist.data.Category
import com.min.todolist.data.CategoryRepository
import com.min.todolist.data.TaskDatabase
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CategoryRepository
    val allCategories: LiveData<List<Category>>

    init {
        val categoryDao = TaskDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(categoryDao)
        allCategories = repository.allCategories
    }

    fun insert(category: Category) = viewModelScope.launch {
        repository.insert(category)
    }

    fun update(category: Category) = viewModelScope.launch {
        repository.update(category)
    }

    fun delete(category: Category) = viewModelScope.launch {
        repository.delete(category)
    }
}
