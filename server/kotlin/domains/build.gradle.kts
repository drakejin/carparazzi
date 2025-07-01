plugins {
    kotlin("jvm")
}

dependencies {
    // Entity 모듈 의존성
    implementation(project(":entity"))

    // Kotlin 기본 라이브러리
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // 날짜/시간 처리
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // 코루틴 지원 (비동기 처리용)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // 테스트 의존성
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
