package com.seikouv.pishield.api

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenManager {
    private const val PREFS_NAME = "pishield_secure_prefs"
    private const val KEY_API_TOKEN = "api_token"
    private const val DEFAULT_TOKEN = "6e7ab2a33e3805aac91a9d17b13fdd354feb72bad67eda8a75623b96f2873a16"

    private fun getEncryptedPrefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(context: Context, token: String) {
        getEncryptedPrefs(context).edit {
            putString(KEY_API_TOKEN, token)
        }
    }

    fun getToken(context: Context): String {
        val prefs = getEncryptedPrefs(context)
        var token = prefs.getString(KEY_API_TOKEN, null)
        
        if (token == null) {
            token = DEFAULT_TOKEN
            saveToken(context, token)
        }
        
        return token
    }

    fun clearToken(context: Context) {
        getEncryptedPrefs(context).edit {
            remove(KEY_API_TOKEN)
        }
    }
}
