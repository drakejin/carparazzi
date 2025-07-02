package org.drakejin.carparazzi.domain.usecase

import org.drakejin.carparazzi.domain.dto.UserDto
import org.drakejin.carparazzi.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserUseCase(
    private val userRepository: UserRepository
) {
    suspend fun getUserInfo(userId: UUID): UserDto? {
        val user = userRepository.findById(userId)

        // 사용자 정보 조회 시 마지막 로그인 시간 업데이트
        user?.let {
            userRepository.updateLastLoginAt(userId)
        }

        return user
    }

    suspend fun getUserByEmail(email: String): UserDto? {
        return userRepository.findByEmail(email)
    }
}
