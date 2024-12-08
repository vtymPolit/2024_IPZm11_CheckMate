package com.example.checkmate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.checkmate.data.FirebaseFirestoreRepo
import com.example.checkmate.data.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskViewModel(
    private val firestoreRepo: FirebaseFirestoreRepo,
    private val tasksListViewModel: TasksListScreenViewModel,
) : ViewModel() {
    private val _subtasksList = MutableStateFlow<List<Task>>(emptyList())
    val subtasksList: StateFlow<List<Task>> = _subtasksList.asStateFlow()

    private val _selectedSubtask = MutableStateFlow<Task?>(null)
    val selectedSubtask: StateFlow<Task?> get() = _selectedSubtask.asStateFlow()

    fun setSelectedSubtask(subtask: Task) {
        _selectedSubtask.value = subtask
    }

    fun unselectSubtask() {
        _selectedSubtask.value = null
    }

    fun getSubtasks(parentId: String) {
        firestoreRepo.getSubtasks(
            parentId = parentId,
            onSuccess = { subtasks ->
                _subtasksList.value = subtasks
            },
            onFailure = { exception -> Log.e("TasksViewModel", "Error loading tasks", exception) }
        )
    }

    fun createSubtask(parentId: String, data: Task, onComplete: () -> Unit) {
        firestoreRepo.createSubtask(parentId, data, onComplete)
    }

    fun completedSubtaskChange(parentId: String, id: String, completed: Boolean) {
        firestoreRepo.subtaskCompletedChange(parentId, id, completed)
    }

    fun destroySubtask(parentId: String, id: String) {
        firestoreRepo.destroySubtask(parentId, id)
        getSubtasks(parentId)
    }

    fun updateSubtask(parentId: String, subtask: Task, onComplete: () -> Unit) {
        firestoreRepo.updateSubtask(parentId, subtask, onComplete)
    }
}