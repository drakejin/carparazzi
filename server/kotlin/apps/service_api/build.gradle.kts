plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.epages.restdocs-api-spec") version "0.19.4"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

dependencies {
    // 내부 모듈 의존성
    implementation(project(":domains"))
    implementation(project(":entity"))

    // Spring Boot 의존성
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Coroutines 의존성
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Swagger UI for WebFlux
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")

    // 개발 도구
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // 데이터베이스
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    // 모니터링
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // 테스트 의존성
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.4")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val snippetsDir = file("build/generated-snippets")

tasks {
    test {
        outputs.dir(snippetsDir)
        systemProperty("org.springframework.restdocs.outputDir", snippetsDir)
    }
}
