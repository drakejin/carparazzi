package org.drakejin.carparazzi.controller.system.dto

import java.time.Instant

// Response DTOs
data class HealthCheckResponseDto(
    val status: String,
    val version: String,
    val timestamp: Instant,
    val services: Map<String, String>,
    val metrics: SystemMetricsDto
)

data class SystemMetricsDto(
    val activeAnalysisJobs: Int,
    val queueSize: Int,
    val averageResponseTimeMs: Long
)
