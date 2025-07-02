package org.drakejin.carparazzi.domain.dto

import org.drakejin.carparazzi.domain.dto.common.ValidationResult
import org.drakejin.carparazzi.domain.dto.common.combine
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

        /**
         * 위반 이벤트 검증
         */
        fun validate(event: ViolationEventDto): ValidationResult {
            val validations = listOf(
                validateTimestampSeconds(event.timestampSeconds),
                validateConfidenceScore(event.confidenceScore),
                validateDescription(event.description)
            )
            return validations.combine()
        }

        /**
         * 위반 유형 검증
         */
        fun validateViolationType(violationType: String?): ValidationResult {
            return when {
                violationType.isNullOrBlank() -> ValidationResult.Invalid(listOf("위반 유형은 필수입니다"))
                !ViolationType.values().any { it.name == violationType } -> ValidationResult.Invalid(
                    listOf("위반 유형은 ${ViolationType.values().joinToString(", ")} 중 하나여야 합니다")
                )
                else -> ValidationResult.Valid
            }
        }

        /**
         * 타임스탬프 검증
         */
        fun validateTimestampSeconds(timestampSeconds: BigDecimal?): ValidationResult {
            return when {
                timestampSeconds == null -> ValidationResult.Invalid(listOf("타임스탬프는 필수입니다"))
                timestampSeconds < BigDecimal.ZERO -> ValidationResult.Invalid(listOf("타임스탬프는 0 이상이어야 합니다"))
                else -> ValidationResult.Valid
            }
        }

        /**
         * 신뢰도 점수 검증
         */
        fun validateConfidenceScore(confidenceScore: BigDecimal?): ValidationResult {
            return when {
                confidenceScore == null -> ValidationResult.Invalid(listOf("신뢰도 점수는 필수입니다"))
                confidenceScore < BigDecimal.ZERO || confidenceScore > BigDecimal.ONE -> ValidationResult.Invalid(
                    listOf("신뢰도 점수는 0.0000 ~ 1.0000 사이여야 합니다")
                )
                else -> ValidationResult.Valid
            }
        }

        /**
         * 심각도 레벨 검증
         */
        fun validateSeverityLevel(severityLevel: String?): ValidationResult {
            return when {
                severityLevel.isNullOrBlank() -> ValidationResult.Valid // DEFAULT 'MEDIUM'
                !SeverityLevel.values().any { it.name == severityLevel } -> ValidationResult.Invalid(
                    listOf("심각도 레벨은 ${SeverityLevel.values().joinToString(", ")} 중 하나여야 합니다")
                )
                else -> ValidationResult.Valid
            }
        }

        /**
         * 설명 검증
         */
        fun validateDescription(description: String?): ValidationResult {
            return when {
                description.isNullOrBlank() -> ValidationResult.Invalid(listOf("설명은 필수입니다"))
                else -> ValidationResult.Valid
            }
        }
    }
}
