package com.seikouv.pishield.api

import com.seikouv.pishield.data.*
import retrofit2.http.*

interface PiShieldApi {
    // System stats
    @GET("/api/system/stats")
    suspend fun getSystemStats(): SystemStats

    // Threats
    @GET("/api/threats")
    suspend fun getThreats(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): List<Threat>

    // Devices
    @GET("/api/devices")
    suspend fun getDevices(): List<Device>

    @POST("/api/devices/{mac}/block")
    suspend fun blockDevice(@Path("mac") mac: String): ApiResponse

    @POST("/api/devices/{mac}/unblock")
    suspend fun unblockDevice(@Path("mac") mac: String): ApiResponse

    @GET("/api/devices/{mac}/threats")
    suspend fun getDeviceThreats(@Path("mac") mac: String): List<Threat>

    // Blocklists
    @GET("/api/blocklists")
    suspend fun getBlocklists(): List<Blocklist>

    @GET("/api/blocklists/stats")
    suspend fun getBlocklistStats(@Query("window_seconds") windowSeconds: Int = 60): CategoryStats

    @POST("/api/blocklists/categories/{category}/toggle")
    suspend fun toggleBlocklistCategory(@Path("category") category: String): ApiResponse

    @GET("/api/blocklists/custom")
    suspend fun getCustomBlocklist(): List<String>

    @POST("/api/blocklists/custom")
    suspend fun addCustomBlocklist(@Body domain: Map<String, String>): ApiResponse

    @DELETE("/api/blocklists/custom/{domain}")
    suspend fun deleteCustomBlocklist(@Path("domain") domain: String): ApiResponse

    @POST("/api/blocklists/update")
    suspend fun updateBlocklists(): ApiResponse

    // DNS Logs
    @GET("/api/dns/logs")
    suspend fun getDnsLogs(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("blocked") blocked: Boolean? = null,
        @Query("category") category: String? = null
    ): List<DnsLogEntry>

    // Schedule
    @GET("/api/schedule")
    suspend fun getSchedule(): Schedule

    @POST("/api/schedule")
    suspend fun updateSchedule(@Body schedule: Schedule): ApiResponse

    // System
    @GET("/api/system/status")
    suspend fun getSystemStatus(): Map<String, Any>

    @POST("/api/system/restart/{service}")
    suspend fun restartService(@Path("service") service: String): ApiResponse
}