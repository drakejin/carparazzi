package org.drakejin.carparazzi.controller.auth.dto

import org.drakejin.carparazzi.domain.dto.UserDto.UserStatus
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
