package com.example.checkmate.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class FirebaseFirestoreRepo(private val authRepo: AuthRepo) {
    private val db = Firebase.firestore

    fun getTasks(onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        authRepo.updateUser()
        db.collection("users").document(authRepo.user?.uid.toString())
            .collection("tasks").orderBy("index").get().addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    val tasks = task.result.toObjects(Task::class.java)
                    onSuccess(tasks)
                } else {
                    task.exception?.let {onFailure(it)}
                }
            }
    }

    fun createTask(data: Task, onComplete: () -> Unit, size: Int) {
        data.index = size
        db.collection("users").document(authRepo.user?.uid.toString())
            .collection("tasks").add(data).addOnSuccessListener { documentReference ->
                val documentId = documentReference.id
                documentReference.update("id", documentId)
            }.addOnCompleteListener{
                onComplete()
            }
    }

    fun completedChange(id: String, completed: Boolean) {
        db.collection("users").document(authRepo.user?.uid.toString())
            .collection("tasks").document(id).update("completed", !completed)
    }

    fun destroyTask(id: String) {
        db.collection("users").document(authRepo.user?.uid.toString())
            .collection("tasks").document(id).delete()
    }

    fun updateTask(task: Task, onComplete: () -> Unit) {
        db.collection("users").document(authRepo.user?.uid.toString())
            .collection("tasks").document(task.id).set(task).addOnCompleteListener {
                onComplete()
            }
    }

    fun searchTasks(query: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users")
            .document(authRepo.user?.uid.toString())
            .collection("tasks")
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThan("name", query + "\uf8ff")
            .get().addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    val tasks = task.result.toObjects(Task::class.java)
                    onSuccess(tasks)
                } else {
                    task.exception?.let {onFailure(it)}
                }
            }
    }

    fun updateTasksIndexInFirestore(tasks: List<Task>) {
        val batch = Firebase.firestore.batch()
        tasks.forEach { task ->
            val taskRef = db.collection("users")
                .document(authRepo.user?.uid.toString())
                .collection("tasks").document(task.id)
            batch.update(taskRef, "index", task.index)
        }
        batch.commit()
            .addOnSuccessListener {
                Log.d("Firestore", "Index updated successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Index to update order: ${exception.message}")
            }
    }
}
