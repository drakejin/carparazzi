package org.drakejin.carparazzi.domain.dto

import org.drakejin.carparazzi.domain.dto.common.ValidationResult
import org.drakejin.carparazzi.domain.dto.common.combine
import org.drakejin.carparazzi.entity.generated.tables.pojos.Users
import java.time.OffsetDateTime
import java.util.*
import java.util.regex.Pattern

data class UserDto(
    val userId: UUID,
    val email: String,
    val nickname: String?,
    val createdAt: OffsetDateTime,
    val lastLoginAt: OffsetDateTime?,
    val status: UserStatus
) {
    enum class UserStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }

    companion object {
        private val EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )

        fun fromEntity(entity: Users): UserDto {
            return UserDto(
                userId = entity.userId,
                email = entity.email,
                nickname = entity.nickname,
                createdAt = entity.createdAt,
                lastLoginAt = entity.lastLoginAt,
                status = UserStatus.valueOf(entity.status)
            )
        }

        /**
         * 사용자 생성/수정 시 검증
         */
        fun validate(user: UserDto): ValidationResult {
            val validations = listOf(
                validateEmail(user.email),
                validateNickname(user.nickname),
                validateStatus(user.status.name)
            )
            return validations.combine()
        }

        /**
         * 이메일 형식 검증
         */
        fun validateEmail(email: String?): ValidationResult {
            return when {
                email.isNullOrBlank() -> ValidationResult.Invalid(listOf("이메일은 필수입니다"))
                email.length > 255 -> ValidationResult.Invalid(listOf("이메일은 255자를 초과할 수 없습니다"))
                !EMAIL_PATTERN.matcher(email).matches() -> ValidationResult.Invalid(listOf("올바른 이메일 형식이 아닙니다"))
                else -> ValidationResult.Valid
            }
        }

        /**
         * 닉네임 검증
         */
        fun validateNickname(nickname: String?): ValidationResult {
            return when {
                nickname != null && nickname.length > 100 -> ValidationResult.Invalid(listOf("닉네임은 100자를 초과할 수 없습니다"))
                nickname != null && nickname.isBlank() -> ValidationResult.Invalid(listOf("닉네임은 공백일 수 없습니다"))
                else -> ValidationResult.Valid
            }
        }

        /**
         * 사용자 상태 검증
         */
        fun validateStatus(status: String?): ValidationResult {
            return when {
                status.isNullOrBlank() -> ValidationResult.Valid // DEFAULT 'ACTIVE'
                !UserStatus.values().any { it.name == status } -> ValidationResult.Invalid(
                    listOf("사용자 상태는 ${UserStatus.values().joinToString(", ")} 중 하나여야 합니다")
                )
                else -> ValidationResult.Valid
            }
        }
    }
}
