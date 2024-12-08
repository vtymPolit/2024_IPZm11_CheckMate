package com.example.checkmate.ui

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.checkmate.data.Task
import com.example.checkmate.rememberTasksListState
import com.example.checkmate.viewmodel.TasksListScreenViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksListScreen(navController: NavController, tasksViewModel: TasksListScreenViewModel) {
    val tasks by tasksViewModel.tasksList.collectAsState()
    val searchQuery by tasksViewModel.searchQuery.collectAsState()
    var openDialogSignOutConfirm by remember { mutableStateOf(false) }
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
                            openDialogSignOutConfirm = !openDialogSignOutConfirm
                        }
                    ) {
                        Text(text = "Sign out")
                        if (openDialogSignOutConfirm) {
                            DialogConfirm(
                                onDismissRequest = { openDialogSignOutConfirm = !openDialogSignOutConfirm },
                                onConfirmation = { Firebase.auth.signOut()
                                    navController.navigate("GoogleSignInScreen")
                                    openDialogSignOutConfirm = !openDialogSignOutConfirm},
                                dialogTitle = "Sign Out?"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            CreateTaskButton(onClick = { navController.navigate("CreateTaskScreen") })
        }
    ) { innerPadding ->
        Column(modifier=Modifier.padding(innerPadding)) {
            Row {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { tasksViewModel.onSearchQueryChanged(it) },
                    label = { Text(text = "Search") },
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                )
            }
            val scope = rememberCoroutineScope()
            var overscrollJob by remember { mutableStateOf<Job?>(null) }
            val tasksListState = rememberTasksListState { fromIndex, toIndex ->
                tasksViewModel.swapTasks(fromIndex, toIndex)
            }
            LazyColumn(
                modifier = Modifier
                    .pointerInput(Unit){
                        detectDragGesturesAfterLongPress(
                            onDrag = {change, offset ->
                                change.consume()
                                tasksListState.onDrag(offset)

                                if (overscrollJob?.isActive == true)
                                    return@detectDragGesturesAfterLongPress
                                tasksListState.checkForOverScroll()
                                    .takeIf { it != 0f }
                                    ?.let { overscrollJob = scope.launch { tasksListState.lazyListState.scrollBy(it) } }
                                    ?: run { overscrollJob?.cancel() }
                            },
                            onDragStart = { offset -> tasksListState.onDragStart(offset) },
                            onDragEnd = { tasksListState.onDragInterrupted() },
                            onDragCancel = { tasksListState.onDragInterrupted() }
                        )
                    },
                state = tasksListState.lazyListState,
            ) {
                itemsIndexed(tasks) { index, task ->
                    TaskItem(
                        task = task,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .graphicsLayer {
                                val offsetOrNull = tasksListState.elementDisplacement.takeIf {
                                    index == tasksListState.currentIndexOfDraggedItem
                                }
                                translationY = offsetOrNull ?: 0f
                            },
                        tasksViewModel = tasksViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    modifier: Modifier = Modifier,
    tasksViewModel: TasksListScreenViewModel,
    navController: NavController
) {
    var completed by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    completed = task.completed
    Card(
        modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = completed,
                onCheckedChange = {
                    tasksViewModel.completedChange(task.id, completed)
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
                onClick = { expanded = !expanded }
            )
        }
        if (expanded) {
            TaskItemExpanded(task.description, tasksViewModel, task, navController)
        }
    }
}

@Composable
fun TaskItemButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = "more info"
        )
    }
}

@Composable
fun TaskItemExpanded(
    taskDescription: String,
    tasksViewModel: TasksListScreenViewModel,
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
            tasksViewModel.setSelectedTask(task)
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
                onConfirmation = { tasksViewModel.destroyTask(task.id)
                    openDialogDestroyConfirm = !openDialogDestroyConfirm},
                dialogTitle = "Delete task?"
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
