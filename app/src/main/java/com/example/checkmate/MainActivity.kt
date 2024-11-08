package com.example.checkmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.checkmate.ui.GoogleSignInScreen
import com.example.checkmate.ui.TasksListScreen
import com.example.checkmate.ui.theme.CheckMateTheme
import com.example.checkmate.viewmodel.GoogleSignInViewModel
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckMateTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val googleSignInViewModel = GoogleSignInViewModel()
                val startDestination = if (Firebase.auth.currentUser == null) {
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
                            TasksListScreen()
                        }
                    }
                )
            }
        }
    }
}
