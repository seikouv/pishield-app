package com.seikouv.pishield.data

import com.google.gson.annotations.SerializedName

// Dashboard data
data class DashboardResponse(
    @SerializedName("active_threats")
    val threat_count: Int,
    @SerializedName("total_devices")
    val total_devices: Int,
    @SerializedName("night_mode_active")
    val night_mode_active: Boolean,
    @SerializedName("uptime_seconds")
    val uptime_seconds: Long,
    @SerializedName("threat_count_24h")
    val threat_count_24h: Int,
    @SerializedName("memory_percent")
    val memory_percent: Float,
    @SerializedName("active_connections")
    val active_connections: Int
)

// Device
data class Device(
    val mac: String,
    val ip: String,
    val hostname: String?,
    val blocked: Boolean,
    @SerializedName("first_seen")
    val firstSeen: String,
    @SerializedName("last_seen")
    val lastSeen: String
)

// Blocklist category
data class Blocklist(
    val category: String,
    val enabled: Boolean,
    val count: Int
)

// Threat/Alert
data class Threat(
    val id: Int,
    val timestamp: String,
    @SerializedName("src_ip")
    val srcIp: String,
    @SerializedName("dst_ip")
    val dstIp: String,
    @SerializedName("threat_type")
    val threatType: String,
    val detail: String,
    val blocked: Boolean
)

// Blocklist entry
data class BlocklistEntry(
    val id: String,
    val domain: String,
    val category: String,
    val source: String,
    @SerializedName("date_added")
    val dateAdded: String
)

// Night mode schedule
data class Schedule(
    val enabled: Boolean,
    val start_time: String, // "HH:MM"
    val end_time: String    // "HH:MM"
)

// System stats
data class SystemStats(
    val uptime_seconds: Long,
    val memory_used_mb: Int,
    val memory_total_mb: Int,
    val memory_percent: Float,
    val active_connections: Int,
    val threat_count: Int,
    val threat_count_24h: Int
)

// Generic API response (for simple success/fail)
data class ApiResponse(
    val success: Boolean,
    val message: String? = null
)
// Live blocklist category stats (from /api/blocklists/stats)
data class CategoryStats(
    val ads: Int,
    val tracking: Int,
    val ai: Int,
    val unknown: Int,
    val timestamp: String
)

// One point in the rolling history graph
data class BlocklistHistoryPoint(
    val timestamp: Long, // System.currentTimeMillis() when fetched
    val adsCount: Int,
    val trackingCount: Int,
    val aiCount: Int
)

// DNS log entry (from /api/dns/logs)
data class DnsLogEntry(
    val id: Long,
    val timestamp: String,
    val domain: String,
    @SerializedName("client_ip")
    val clientIp: String,
    val blocked: Boolean,
    val category: String?,
    val epoch: Long?
)