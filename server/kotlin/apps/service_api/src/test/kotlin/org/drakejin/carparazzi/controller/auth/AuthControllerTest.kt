package org.drakejin.carparazzi.controller.auth

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import org.drakejin.carparazzi.domain.dto.UserDto
import org.drakejin.carparazzi.domain.usecase.UserUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.OffsetDateTime
import java.util.*

@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@WebMvcTest(AuthController::class)
@AutoConfigureRestDocs
class AuthControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var userUseCase: UserUseCase
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun userUseCase(): UserUseCase = mockk()
    }

    @BeforeEach
    fun setUp(webApplicationContext: WebApplicationContext) {
        userUseCase = webApplicationContext.getBean(UserUseCase::class.java)
        objectMapper = ObjectMapper()

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .build()
    }

    @Test
    fun `getCurrentUserInfo should return user information when user exists`() {
        // Given
        val userId = UUID.randomUUID()
        val userDto = UserDto(
            userId = userId,
            email = "user@example.com",
            nickname = "사용자닉네임",
            createdAt = OffsetDateTime.parse("2025-07-01T10:30:00Z"),
            lastLoginAt = OffsetDateTime.parse("2025-07-01T10:25:00Z"),
            status = UserDto.UserStatus.ACTIVE
        )

        coEvery { userUseCase.getUserInfo(userId) } returns userDto

        // When & Then
        mockMvc.perform(
            get("/api/v1/auth/me")
                .header("X-User-ID", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.user_id").value(userId.toString()))
            .andExpect(jsonPath("$.data.email").value("user@example.com"))
            .andExpect(jsonPath("$.data.nickname").value("사용자닉네임"))
            .andExpect(jsonPath("$.data.status").value("ACTIVE"))
            .andDo(
                document(
                    "auth-get-user-info",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("X-User-ID").description("사용자 ID (UUID)")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.user_id").type(JsonFieldType.STRING).description("사용자 ID"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일 주소"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("사용자 상태 (ACTIVE, INACTIVE, SUSPENDED)"),
                        fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("계정 생성일시"),
                        fieldWithPath("data.last_login_at").type(JsonFieldType.STRING).description("마지막 로그인 일시").optional(),
                        fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 생성 시간"),
                        fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)").optional()
                    )
                )
            )
    }

    @Test
    fun `getCurrentUserInfo should return 404 when user does not exist`() {
        // Given
        val userId = UUID.randomUUID()
        coEvery { userUseCase.getUserInfo(userId) } returns null

        // When & Then
        mockMvc.perform(
            get("/api/v1/auth/me")
                .header("X-User-ID", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andDo(
                document(
                    "auth-get-user-info-not-found",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("X-User-ID").description("존재하지 않는 사용자 ID (UUID)")
                    )
                )
            )
    }

    @Test
    fun `getCurrentUserInfo should return 400 when user ID is invalid`() {
        // When & Then
        mockMvc.perform(
            get("/api/v1/auth/me")
                .header("X-User-ID", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "auth-get-user-info-invalid-uuid",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("X-User-ID").description("잘못된 형식의 사용자 ID")
                    )
                )
            )
    }
}
