package com.seikouv.pishield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.seikouv.pishield.api.RetrofitClient
import com.seikouv.pishield.api.TokenManager
import com.seikouv.pishield.ui.screens.TokenEntryScreen
import com.seikouv.pishield.ui.theme.PiShieldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(this)
        setContent {
            PiShieldTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var tokenReady by remember { mutableStateOf(TokenManager.hasToken(this)) }

                    if (tokenReady) {
                        PiShieldApp()
                    } else {
                        TokenEntryScreen(onTokenSaved = { tokenReady = true })
                    }
                }
            }
        }
    }
}