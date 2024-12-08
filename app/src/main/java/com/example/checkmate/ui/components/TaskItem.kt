package com.example.checkmate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkmate.data.Task
import com.example.checkmate.viewmodel.TaskViewModel
import com.example.checkmate.viewmodel.TasksListScreenViewModel
import androidx.compose.material3.Icon

@Composable
fun TaskItem(
    task: Task,
    isSubtask: Boolean = false,
    modifier: Modifier = Modifier,
    tasksListViewModel: TasksListScreenViewModel,
    taskViewModel: TaskViewModel,
    navController: NavController
) {
    var completed by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val selectedParentTask by tasksListViewModel.selectedTask.collectAsState()

    completed = task.completed
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = completed,
                onCheckedChange = {
                    if (!isSubtask)
                        tasksListViewModel.completedTaskChange(task.id, completed)
                    else
                        taskViewModel.completedSubtaskChange(selectedParentTask!!.id, task.id, completed)
                    completed = it
                })
            val completedStyle = if (completed) TextDecoration.LineThrough else null
            Text(
                text = task.name,
                style = TextStyle(
                    fontSize = 24.sp,
                    textDecoration = completedStyle
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            TaskItemButton(
                expanded = expanded,
                isSubtask = isSubtask,
                onClick = {
                    if (isSubtask) {
                        expanded = !expanded
                    } else {
                        navController.navigate("TaskInfoScreen")
                    }
                }
            )
        }
        if (expanded) {
            TaskItemExpanded(task.description, taskViewModel, selectedParentTask?.id.toString(), task, navController)
        }
    }
}

@Composable
fun TaskItemButton(
    isSubtask: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (!isSubtask) {
                Icons.AutoMirrored.Filled.ArrowForward
            }
            else {
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore
            },
            contentDescription = "more info"
        )
    }
}

@Composable
fun TaskItemExpanded(
    taskDescription: String,
    taskViewModel: TaskViewModel,
    parentId: String,
    task: Task,
    navController: NavController,
) {
    var openDialogDestroyConfirm by remember { mutableStateOf(false) }
    Row(Modifier.padding(16.dp)) {
        Text(text = taskDescription)
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp), horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            taskViewModel.setSelectedSubtask(task)
            navController.navigate("UpdateTaskScreen")
        }) {
            Text("Update")
        }
        Spacer(Modifier.width(16.dp))
        Button(onClick = { openDialogDestroyConfirm = !openDialogDestroyConfirm }
        ) {
            Text("Delete")
        }
        if (openDialogDestroyConfirm) {
            DialogConfirm(
                onDismissRequest = { openDialogDestroyConfirm = !openDialogDestroyConfirm },
                onConfirmation = {
                    taskViewModel.destroySubtask(parentId, task.id)
                    openDialogDestroyConfirm = !openDialogDestroyConfirm
                },
                dialogTitle = "Delete task?"
            )
        }
    }
}

