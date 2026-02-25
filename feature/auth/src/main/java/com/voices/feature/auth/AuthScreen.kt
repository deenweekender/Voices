package com.voices.feature.auth

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    state: AuthUiState,
    onGoogleSignIn: (Activity?) -> Unit,
    onAppleSignIn: (Activity?) -> Unit,
    onContinue: () -> Unit,
) {
    val activity = LocalContext.current as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Voices", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onGoogleSignIn(activity) }) { Text("Sign in with Google") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onAppleSignIn(activity) }) { Text("Sign in with Apple") }
        Spacer(Modifier.height(12.dp))
        if (state.message.isNotBlank()) {
            Text(state.message, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
        }
        Button(onClick = onContinue) { Text("Continue") }
    }
}
