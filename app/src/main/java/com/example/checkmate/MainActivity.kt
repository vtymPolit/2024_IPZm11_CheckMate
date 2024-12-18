package com.example.checkmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.checkmate.data.AuthRepo
import com.example.checkmate.data.FirebaseFirestoreRepo
import com.example.checkmate.ui.CreateTaskScreen
import com.example.checkmate.ui.GoogleSignInScreen
import com.example.checkmate.ui.TaskInfoScreen
import com.example.checkmate.ui.TasksListScreen
import com.example.checkmate.ui.UpdateTaskScreen
import com.example.checkmate.ui.theme.CheckMateTheme
import com.example.checkmate.viewmodel.GoogleSignInViewModel
import com.example.checkmate.viewmodel.TaskViewModel
import com.example.checkmate.viewmodel.TasksListScreenViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckMateTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val authRepo = AuthRepo()
                val firestoreRepo = FirebaseFirestoreRepo(authRepo)
                authRepo.updateUser()
                val googleSignInViewModel = GoogleSignInViewModel(authRepo)
                val tasksListViewModel = TasksListScreenViewModel(firestoreRepo)
                val taskViewModel = TaskViewModel(firestoreRepo, tasksListViewModel)
                val startDestination = if (authRepo.user == null) {
                    "GoogleSignInScreen"
                } else {
                    "TasksListScreen"
                }
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    builder = {
                        composable("GoogleSignInScreen") {
                            GoogleSignInScreen {
                                googleSignInViewModel.handleGoogleSignIn(context, navController)
                            }
                        }
                        composable("TasksListScreen") {
                            TasksListScreen(navController, tasksListViewModel, taskViewModel)
                        }
                        composable("TaskInfoScreen") {
                            TaskInfoScreen(navController, tasksListViewModel, taskViewModel)
                        }
                        composable("CreateTaskScreen") {
                            CreateTaskScreen(navController, tasksListViewModel, taskViewModel)
                        }
                        composable("UpdateTaskScreen") {
                            UpdateTaskScreen(navController, tasksListViewModel, taskViewModel)
                        }
                    }
                )
            }
        }
    }
}
