package com.example.checkmate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkmate.R
import com.example.checkmate.ui.theme.CheckMateTheme

@Composable
fun GoogleSignInScreen(onGoogleSignInClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = ""
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                onGoogleSignInClick()
            },
        ) {
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