package com.example.checkmate.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.checkmate.ui.theme.CheckMateTheme

@Composable
fun GoogleSignInScreen(onGoogleSignInClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Button(
            onClick = {
                onGoogleSignInClick()
            },) {
            Text(text = "Google Sign-In")
        }
    }
}

@Preview
@Composable
fun GoogleSignInScreenPreview() {
    CheckMateTheme {
        GoogleSignInScreen {}
    }
}