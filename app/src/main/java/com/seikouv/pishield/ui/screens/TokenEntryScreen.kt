package com.seikouv.pishield.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seikouv.pishield.Validators
import com.seikouv.pishield.api.TokenManager
import com.seikouv.pishield.ui.theme.DarkColors

@Composable
fun TokenEntryScreen(onTokenSaved: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var tokenInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkColors.TerminalBg)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "PISHIELD SETUP",
            color = DarkColors.AccentBright,
            fontFamily = FontFamily.Monospace,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Enter the API token shown on your Pi (run: cat ~/pishield/.api_token)",
            color = Color(0xFF9CA3AF),
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )
        OutlinedTextField(
            value = tokenInput,
            onValueChange = { tokenInput = it; error = null },
            label = { Text("API Token") },
            modifier = Modifier.fillMaxWidth(),
            isError = error != null
        )
        error?.let {
            Text(it, color = DarkColors.AlertRed, modifier = Modifier.padding(top = 4.dp))
        }
        Button(
            onClick = {
                if (tokenInput.isBlank()) {
                    error = "Token cannot be empty"
                } else if (!Validators.isValidToken(tokenInput)) {
                    error = "Invalid token format or length"
                } else {
                    TokenManager.saveToken(context, tokenInput)
                    onTokenSaved()
                }
            },
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Connect")
        }
    }
}