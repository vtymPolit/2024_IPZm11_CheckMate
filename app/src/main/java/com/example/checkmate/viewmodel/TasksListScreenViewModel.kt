package com.example.checkmate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.checkmate.data.FirebaseFirestoreRepo
import com.example.checkmate.data.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksListScreenViewModel(private val firestoreRepo: FirebaseFirestoreRepo) : ViewModel() {
    private val _tasksList = MutableStateFlow<List<Task>>(emptyList())
    val tasksList: StateFlow<List<Task>> = _tasksList.asStateFlow()

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> get() = _selectedTask.asStateFlow()

    fun getTasksListFromFirebase() {
        firestoreRepo.getTasks(
            onSuccess = { tasks ->
                _tasksList.value = tasks
            },
            onFailure = { exception -> Log.e("TasksViewModel", "Error loading tasks", exception) }
        )
    }

    fun createTask(data: Task, onComplete: () -> Unit) {
        firestoreRepo.createTask(data, onComplete)
    }

    fun completedChange(id: String, completed: Boolean) {
        firestoreRepo.completedChange(id, completed)
    }

    fun destroyTask(id: String) {
        firestoreRepo.destroyTask(id)
        getTasksListFromFirebase()
    }

    fun setSelectedTask(task: Task) {
        _selectedTask.value = task
    }

    fun updateTask(task: Task, onComplete: () -> Unit) {
        firestoreRepo.updateTask(task, onComplete)
    }
}
