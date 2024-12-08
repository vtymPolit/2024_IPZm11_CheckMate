package com.example.checkmate.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.checkmate.data.Task
import com.example.checkmate.viewmodel.TaskViewModel
import com.example.checkmate.viewmodel.TasksListScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTaskScreen(
    navController: NavController,
    tasksListViewModel: TasksListScreenViewModel,
    taskViewModel: TaskViewModel
) {
    val task by tasksListViewModel.selectedTask.collectAsState()
    val subtask by taskViewModel.selectedSubtask.collectAsState()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    LaunchedEffect(task) {
        if (subtask == null) {
            name = task?.name ?: ""
            description = task?.description ?: ""
        } else {
            name = subtask?.name ?: ""
            description = subtask?.description ?: ""
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Update Task")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("TaskInfoScreen") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "Name") }
                    )
                }
                Row {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(text = "Description") }
                    )
                }
                Button(onClick = {
                    if (subtask == null) {
                        task?.let {
                            val updatedTask = Task(
                                name = name,
                                description = description,
                                completed = it.completed,
                                id = it.id
                            )
                            tasksListViewModel.updateTask(updatedTask) {
                                tasksListViewModel.getTasksListFromFirebase()
                                tasksListViewModel.setSelectedTask(updatedTask)
                                navController.navigate("TaskInfoScreen")
                            }
                        }
                    } else {
                        subtask?.let {
                            val updatedTask = Task(
                                name = name,
                                description = description,
                                completed = it.completed,
                                id = it.id
                            )
                            taskViewModel.updateSubtask(task!!.id, updatedTask) {
                                navController.navigate("TaskInfoScreen")
                            }
                        }
                    }
                }
                ) {
                    Text(text = "Update")
                }
            }
        }
    }
}
