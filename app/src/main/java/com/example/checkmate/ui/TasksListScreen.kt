package com.example.checkmate.ui
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.checkmate.data.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun TasksListScreen(){
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    user?.let {
        val name = it.displayName
        val uid = it.uid
    }
    val list = remember {
        mutableStateOf(emptyList<Task>())
    }
    db.collection(user?.uid.toString()).get().addOnCompleteListener { task ->
        if( task.isSuccessful){
            list.value = task.result.toObjects(Task::class.java)
        }
    }
        Box(
        modifier = Modifier.fillMaxSize(),
    ){
        LazyColumn(

        ) {
            items(list.value) { item: Task ->
                Card() {
                    Text(text = item.name)
                }
            }
        }
    }
}
