package com.seikouv.pishield

import java.util.regex.Pattern

object Validators {
    private val MAC_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")
    
    // Basic domain validation: alphanumeric, hyphens, dots, at least one dot, doesn't start/end with dot/hyphen
    private val DOMAIN_PATTERN = Pattern.compile(
        "^(?!-)[A-Za-z0-9-]+([\\-\\.]{1}[a-z0-9]+)*\\.[A-Za-z]{2,6}$"
    )

    fun isValidToken(token: String): Boolean {
        // Assuming a minimum length and alphanumeric/special chars. 
        // Typically tokens are at least 32-64 chars hex or UUID.
        // Let's just ensure it's not blank and has some reasonable content.
        return token.isNotBlank() && token.length >= 16
    }

    fun isValidMac(mac: String): Boolean {
        return MAC_PATTERN.matcher(mac).matches()
    }

    fun isValidDomain(domain: String): Boolean {
        return DOMAIN_PATTERN.matcher(domain.lowercase()).matches()
    }
}
