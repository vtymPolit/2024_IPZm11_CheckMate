package com.example.checkmate.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkmate.data.Task
import com.example.checkmate.viewmodel.TasksListScreenViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksListScreen(navController: NavController, tasksViewModel: TasksListScreenViewModel) {
    val tasks by tasksViewModel.tasksList.collectAsState()
    LaunchedEffect(Unit) { tasksViewModel.getTasksListFromFirebase() }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "CheckMate")
                },
                actions = {
                    Button(
                        onClick = {
                            Firebase.auth.signOut()
                            navController.navigate("GoogleSignInScreen")
                        }
                    ) {
                        Text(text = "Sign out")
                    }
                }
            )
        },
        floatingActionButton = {
            CreateTaskButton(onClick = { navController.navigate("CreateTaskScreen") })
        }
    ) { it ->
        LazyColumn(contentPadding = it) {
            items(tasks) {
                TaskItem(
                    task = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = task.name,
                style = TextStyle(
                    fontSize = 24.sp,
                )
            )
        }
    }
}

@Composable
fun CreateTaskButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .size(86.dp)
            .padding(16.dp)
    ) {
        Text(text = "Add")
    }
}
