package org.drakejin.carparazzi.domain.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.drakejin.carparazzi.domain.dto.UserDto
import org.drakejin.carparazzi.entity.generated.Tables.USERS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
class UserRepositoryImpl(
    private val dsl: DSLContext
) : UserRepository {

    override suspend fun findById(userId: UUID): UserDto? = withContext(Dispatchers.IO) {
        dsl.selectFrom(USERS)
            .where(USERS.USER_ID.eq(userId))
            .fetchOne()
            ?.let { record ->
                UserDto(
                    userId = record.userId,
                    email = record.email,
                    nickname = record.nickname,
                    createdAt = record.createdAt,
                    lastLoginAt = record.lastLoginAt,
                    status = UserDto.UserStatus.valueOf(record.status)
                )
            }
    }

    override suspend fun findByEmail(email: String): UserDto? = withContext(Dispatchers.IO) {
        dsl.selectFrom(USERS)
            .where(USERS.EMAIL.eq(email))
            .fetchOne()
            ?.let { record ->
                UserDto(
                    userId = record.userId,
                    email = record.email,
                    nickname = record.nickname,
                    createdAt = record.createdAt,
                    lastLoginAt = record.lastLoginAt,
                    status = UserDto.UserStatus.valueOf(record.status)
                )
            }
    }

    override suspend fun updateLastLoginAt(userId: UUID) = withContext(Dispatchers.IO) {
        dsl.update(USERS)
            .set(USERS.LAST_LOGIN_AT, OffsetDateTime.now())
            .where(USERS.USER_ID.eq(userId))
            .execute()
        Unit
    }
}
