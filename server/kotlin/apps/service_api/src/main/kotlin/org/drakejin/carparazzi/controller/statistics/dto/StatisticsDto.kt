package org.drakejin.carparazzi.controller.statistics.dto

import org.drakejin.carparazzi.domain.dto.ViolationEventDto.ViolationType
import java.time.OffsetDateTime
import java.time.LocalDate

// Response DTOs
data class UserStatisticsResponseDto(
    val totalUploads: Int,
    val totalAnalyses: Int,
    val totalViolationsDetected: Int,
    val totalClipsGenerated: Int,
    val totalDownloads: Int,
    val violationTypeBreakdown: Map<ViolationType, Int>,
    val lastUploadAt: OffsetDateTime?,
    val accountCreatedAt: OffsetDateTime
)

data class SystemStatisticsResponseDto(
    val totalUsers: Int,
    val totalVideosProcessed: Int,
    val totalViolationsDetected: Int,
    val averageProcessingTimeMs: Long,
    val popularViolationTypes: List<ViolationTypeStatDto>,
    val dailyUploadTrend: List<DailyTrendDto>
)

data class ViolationTypeStatDto(
    val type: ViolationType,
    val count: Int,
    val percentage: Double
)

data class DailyTrendDto(
    val date: LocalDate,
    val uploadCount: Int,
    val analysisCount: Int
)
