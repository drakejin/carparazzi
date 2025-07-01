package org.drakejin.carparazzi.domain.dto

import org.drakejin.carparazzi.entity.generated.tables.pojos.ViolationEvents
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class ViolationEventDto(
    val violationId: UUID,
    val jobId: UUID,
    val violationType: ViolationType,
    val timestampSeconds: BigDecimal,
    val confidenceScore: BigDecimal,
    val severityLevel: SeverityLevel,
    val description: String,
    val detectionMetadata: String?,
    val detectedAt: OffsetDateTime
) {
    enum class ViolationType {
        SIGNAL_VIOLATION,    // 신호위반
        LANE_VIOLATION,      // 차선침범
        COLLISION,           // 충돌사고
        SUDDEN_BRAKING,      // 급브레이킹
        SUDDEN_ACCELERATION, // 급가속
        SPEEDING,            // 과속
        ILLEGAL_TURN,        // 불법회전
        ROAD_RAGE            // 난폭운전
    }

    enum class SeverityLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    companion object {
        fun fromEntity(entity: ViolationEvents): ViolationEventDto {
            return ViolationEventDto(
                violationId = entity.violationId,
                jobId = entity.jobId,
                violationType = ViolationType.valueOf(entity.violationType),
                timestampSeconds = entity.timestampSeconds,
                confidenceScore = entity.confidenceScore,
                severityLevel = SeverityLevel.valueOf(entity.severityLevel),
                description = entity.description,
                detectionMetadata = entity.detectionMetadata?.toString(),
                detectedAt = entity.detectedAt
            )
        }
    }
}
