package com.example.checkmate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkmate.data.FirebaseFirestoreRepo
import com.example.checkmate.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class TasksListScreenViewModel(private val firestoreRepo: FirebaseFirestoreRepo) : ViewModel() {
    private val _tasksList = MutableStateFlow<List<Task>>(emptyList())
    val tasksList: StateFlow<List<Task>> = _tasksList.asStateFlow()

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> get() = _selectedTask.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(250) // Wait 250ms after the last change
                .distinctUntilChanged() // Ignore identical consecutive values
                .collect { searchTasks(it) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun getTasksListFromFirebase() {
        firestoreRepo.getTasks(
            onSuccess = { tasks ->
                _tasksList.value = tasks
            },
            onFailure = { exception -> Log.e("TasksViewModel", "Error loading tasks", exception) }
        )
    }

    fun createTask(data: Task, onComplete: () -> Unit) {
        firestoreRepo.createTask(data, onComplete, tasksList.value.size)
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

    private fun searchTasks(query: String) {
        firestoreRepo.searchTasks(
            query = query,
            onSuccess = { tasks ->
                _tasksList.value = tasks
            },
            onFailure = { exception -> Log.e("TasksViewModel", "Error searching tasks", exception) }
        )
    }

    fun swapTasks(fromIndex: Int, toIndex: Int) {
        val mutableTasks = tasksList.value.toMutableList()
        val movedTask = mutableTasks.removeAt(fromIndex)
        mutableTasks.add(toIndex, movedTask)
        mutableTasks.forEachIndexed { index, task ->
            task.index = index
        }
        updateTasksIndexInFirestore(mutableTasks)
        _tasksList.value = mutableTasks
    }

    private fun updateTasksIndexInFirestore(tasks: List<Task>) {
        firestoreRepo.updateTasksIndexInFirestore(tasks)
    }
}
