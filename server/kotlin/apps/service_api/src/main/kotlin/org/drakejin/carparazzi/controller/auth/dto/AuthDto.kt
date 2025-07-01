package org.drakejin.carparazzi.controller.auth.dto

import java.time.OffsetDateTime
import java.util.*

// Response DTOs
data class UserInfoResponseDto(
    val userId: UUID,
    val email: String,
    val nickname: String,
    val status: UserStatus,
    val createdAt: OffsetDateTime,
    val lastLoginAt: OffsetDateTime?
)

enum class UserStatus {
    ACTIVE, INACTIVE, SUSPENDED
}
