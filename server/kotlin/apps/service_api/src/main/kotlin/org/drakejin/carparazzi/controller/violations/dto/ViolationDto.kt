package org.drakejin.carparazzi.controller.violations.dto

import org.drakejin.carparazzi.domain.dto.ViolationEventDto.ViolationType
import org.drakejin.carparazzi.domain.dto.ViolationEventDto.SeverityLevel
import java.time.OffsetDateTime
import java.util.*

// Response DTOs
data class ViolationListResponseDto(
    val violations: List<ViolationDetailResponseDto>
)

data class ViolationDetailResponseDto(
    val violationId: UUID,
    val jobId: UUID,
    val violationType: ViolationType,
    val timestampSeconds: Double,
    val confidenceScore: Double,
    val severityLevel: SeverityLevel,
    val description: String,
    val detectionMetadata: DetectionMetadata?,
    val detectedAt: OffsetDateTime
)

data class DetectionMetadata(
    val boundingBoxes: List<BoundingBox>,
    val frameAnalysis: FrameAnalysis
)

data class BoundingBox(
    val objectType: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val confidence: Double
)

data class FrameAnalysis(
    val lightColor: String?,
    val vehicleMovement: String?,
    val intersectionType: String?,
    val weatherCondition: String?
)
