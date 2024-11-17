package com.example.checkmate.viewmodel

import androidx.lifecycle.ViewModel
import com.example.checkmate.data.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksListScreenViewModel : ViewModel() {
    private val _tasksList = MutableStateFlow<List<Task>>(emptyList())
    val tasksList: StateFlow<List<Task>> = _tasksList.asStateFlow()

    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    fun getTasksListFromFirebase() {
        db.collection(user?.uid.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val tasks = task.result.toObjects(Task::class.java)
                _tasksList.value = tasks
            }
        }
    }

    fun createTask(data: Task) {
        db.collection(user?.uid.toString()).add(data).addOnSuccessListener { documentReference ->
            val documentId = documentReference.id
            documentReference.update("id", documentId)
        }
    }
}
