package com.example.checkmate.viewmodel

import androidx.lifecycle.ViewModel
import com.example.checkmate.data.AuthRepo
import com.example.checkmate.data.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksListScreenViewModel(private val authRepo: AuthRepo) : ViewModel() {
    private val _tasksList = MutableStateFlow<List<Task>>(emptyList())
    val tasksList: StateFlow<List<Task>> = _tasksList.asStateFlow()

    private val db = Firebase.firestore

    fun getTasksListFromFirebase() {
        db.collection(authRepo.user?.uid.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val tasks = task.result.toObjects(Task::class.java)
                _tasksList.value = tasks
            }
        }
    }

    fun createTask(data: Task) {
        db.collection(authRepo.user?.uid.toString()).add(data).addOnSuccessListener { documentReference ->
            val documentId = documentReference.id
            documentReference.update("id", documentId)
        }
    }

    fun completedChange(id: String, completed: Boolean) {
        db.collection(authRepo.user?.uid.toString()).document(id).update("completed", !completed)
    }
}
