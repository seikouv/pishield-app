package com.seikouv.pishield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.seikouv.pishield.api.RetrofitClient
import com.seikouv.pishield.api.TokenManager
import com.seikouv.pishield.ui.screens.TokenEntryScreen
import com.seikouv.pishield.ui.theme.PiShieldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        RetrofitClient.init(this)
        
        var isTokenCheckComplete by mutableStateOf(false)
        var hasToken by mutableStateOf(false)
        
        // Keep splash screen visible until token check is complete
        splashScreen.setKeepOnScreenCondition { !isTokenCheckComplete }
        
        // Run check
        hasToken = TokenManager.hasToken(this)
        isTokenCheckComplete = true

        setContent {
            PiShieldTheme(darkTheme = isSystemInDarkTheme()) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var tokenReady by remember { mutableStateOf(hasToken) }

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