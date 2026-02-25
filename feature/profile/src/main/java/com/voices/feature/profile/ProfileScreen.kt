package com.voices.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    userId: String,
    provider: String,
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Profile")
        Text("User ID: $userId")
        Text("Provider: $provider")
        Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
            Text("Back")
        }
    }
}
