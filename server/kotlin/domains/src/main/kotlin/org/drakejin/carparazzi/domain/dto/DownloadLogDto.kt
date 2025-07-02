package org.drakejin.carparazzi.domain.dto

import org.drakejin.carparazzi.domain.dto.common.ValidationResult
import org.drakejin.carparazzi.domain.dto.common.combine
import org.drakejin.carparazzi.entity.generated.tables.pojos.DownloadLogs
import java.time.OffsetDateTime
import java.util.*

data class DownloadLogDto(
    val downloadId: UUID,
    val userId: UUID,
    val clipId: UUID?,
    val downloadType: DownloadType,
    val downloadedAt: OffsetDateTime,
    val userAgent: String?,
    val ipAddress: String?
) {
    enum class DownloadType {
        SINGLE, BATCH, ZIP
    }

    companion object {
        fun fromEntity(entity: DownloadLogs): DownloadLogDto {
            return DownloadLogDto(
                downloadId = entity.downloadId,
                userId = entity.userId,
                clipId = entity.clipId,
                downloadType = DownloadType.valueOf(entity.downloadType),
                downloadedAt = entity.downloadedAt,
                userAgent = entity.userAgent,
                ipAddress = entity.ipAddress?.toString()
            )
        }

        /**
         * 다운로드 로그 검증
         */
        fun validate(log: DownloadLogDto): ValidationResult {
            val validations = listOf(
                validateUserAgent(log.userAgent),
                validateIpAddress(log.ipAddress)
            )
            return validations.combine()
        }

        /**
         * 다운로드 유형 검증
         */
        fun validateDownloadType(downloadType: String?): ValidationResult {
            return when {
                downloadType.isNullOrBlank() -> ValidationResult.Valid // DEFAULT 'SINGLE'
                !DownloadType.values().any { it.name == downloadType } -> ValidationResult.Invalid(
                    listOf("다운로드 유형은 ${DownloadType.values().joinToString(", ")} 중 하나여야 합니다")
                )
                else -> ValidationResult.Valid
            }
        }

        /**
         * User Agent 검증
         */
        fun validateUserAgent(userAgent: String?): ValidationResult {
            return when {
                userAgent != null && userAgent.length > 1000 -> ValidationResult.Invalid(listOf("User Agent는 1000자를 초과할 수 없습니다"))
                else -> ValidationResult.Valid
            }
        }

        /**
         * IP 주소 검증
         */
        fun validateIpAddress(ipAddress: String?): ValidationResult {
            return when {
                ipAddress != null && ipAddress.length > 45 -> ValidationResult.Invalid(listOf("IP 주소는 45자를 초과할 수 없습니다"))
                ipAddress != null && !isValidIpAddress(ipAddress) -> ValidationResult.Invalid(listOf("올바른 IP 주소 형식이 아닙니다"))
                else -> ValidationResult.Valid
            }
        }

        /**
         * IP 주소 형식 검증 (IPv4, IPv6)
         */
        private fun isValidIpAddress(ipAddress: String): Boolean {
            return isValidIPv4(ipAddress) || isValidIPv6(ipAddress)
        }

        /**
         * IPv4 형식 검증
         */
        private fun isValidIPv4(ip: String): Boolean {
            val parts = ip.split(".")
            if (parts.size != 4) return false

            return parts.all { part ->
                try {
                    val num = part.toInt()
                    num in 0..255
                } catch (e: NumberFormatException) {
                    false
                }
            }
        }

        /**
         * IPv6 형식 검증 (간단한 검증)
         */
        private fun isValidIPv6(ip: String): Boolean {
            val parts = ip.split(":")
            if (parts.size > 8) return false

            return parts.all { part ->
                if (part.isEmpty()) true // :: 표기법 허용
                else {
                    try {
                        part.toInt(16)
                        part.length <= 4
                    } catch (e: NumberFormatException) {
                        false
                    }
                }
            }
        }
    }
}
