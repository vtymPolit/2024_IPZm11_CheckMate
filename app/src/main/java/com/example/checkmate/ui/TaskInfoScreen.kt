package com.example.checkmate.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkmate.ui.components.DialogConfirm
import com.example.checkmate.ui.components.TaskItem
import com.example.checkmate.viewmodel.TaskViewModel
import com.example.checkmate.viewmodel.TasksListScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskInfoScreen(
    navController: NavController,
    tasksListViewModel: TasksListScreenViewModel,
    taskViewModel: TaskViewModel,
) {
    val subtasks by taskViewModel.subtasksList.collectAsState()
    val selectedTask by tasksListViewModel.selectedTask.collectAsState()
    var openDialogDestroyConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        taskViewModel.unselectSubtask()
        tasksListViewModel.getTasksListFromFirebase()
        taskViewModel.getSubtasks(selectedTask!!.id)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Task")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("TasksListScreen")
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(40.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            navController.navigate("UpdateTaskScreen")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Update"
                        )
                    }
                    Button(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        onClick = { openDialogDestroyConfirm = !openDialogDestroyConfirm }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete"
                        )
                    }
                    if (openDialogDestroyConfirm) {
                        DialogConfirm(
                            onDismissRequest = {
                                openDialogDestroyConfirm = !openDialogDestroyConfirm
                            },
                            onConfirmation = {
                                tasksListViewModel.destroyTask(selectedTask?.id.toString())
                                openDialogDestroyConfirm = !openDialogDestroyConfirm
                            },
                            dialogTitle = "Delete task?"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(text = selectedTask?.name.toString(), style = TextStyle(fontSize = 32.sp))
            }
            Row {
                Text(text = "Description")
            }
            Row {
                Text(
                    text = selectedTask?.description.toString(),
                    style = TextStyle(fontSize = 24.sp),
                    modifier = Modifier.padding(all = 4.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Subtasks")
                Spacer(Modifier.width(20.dp))
                Button(onClick = { navController.navigate("CreateTaskScreen") }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add"
                    )
                }
            }
            LazyColumn {
                items(subtasks) {
                    TaskItem(
                        task = it,
                        isSubtask = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        tasksListViewModel = tasksListViewModel,
                        taskViewModel = taskViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}
