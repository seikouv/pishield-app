package com.seikouv.pishield.api

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenManager {
    private const val PREFS_NAME = "pishield_secure_prefs"
    private const val KEY_API_TOKEN = "api_token"

    private fun getEncryptedPrefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(context: Context, token: String) {
        getEncryptedPrefs(context).edit {
            putString(KEY_API_TOKEN, token.trim())
        }
    }

    fun getToken(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_API_TOKEN, null)
    }

    fun hasToken(context: Context): Boolean {
        return !getToken(context).isNullOrBlank()
    }

    fun clearToken(context: Context) {
        getEncryptedPrefs(context).edit {
            remove(KEY_API_TOKEN)
        }
    }
}