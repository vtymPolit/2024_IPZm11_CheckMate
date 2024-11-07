package com.example.checkmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.checkmate.ui.GoogleSignInScreen
import com.example.checkmate.ui.TasksListScreen
import com.example.checkmate.ui.theme.CheckMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckMateTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "GoogleSignInScreen", builder = {
                    composable("GoogleSignInScreen"){
                        GoogleSignInScreen()
                    }
                    composable("TasksListScreen"){
                        TasksListScreen()
                    }
                } )
            }
        }

    }
}