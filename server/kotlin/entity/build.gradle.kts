plugins {
    kotlin("jvm") version "1.9.25"
    id("nu.studer.jooq") version "8.2.1"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin 기본 라이브러리
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // 날짜/시간 처리
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // jOOQ 의존성
    implementation("org.jooq:jooq:3.18.7")
    implementation("org.jooq:jooq-meta:3.18.7")
    implementation("org.jooq:jooq-codegen:3.18.7")

    // Jakarta Bean Validation API
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    // PostgreSQL 드라이버 (jOOQ 코드 생성용)
    jooqGenerator("org.postgresql:postgresql:42.7.1")

    // 테스트 의존성
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

// 생성된 jOOQ 소스를 소스셋에 추가
sourceSets {
    main {
        kotlin {
            srcDir("src/generated/kotlin")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// jOOQ 코드 생성 설정
jooq {
    version.set("3.18.7")
    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true) // 수동으로 생성하도록 변경

            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/carparazzi"
                    user = System.getenv("DB_USER") ?: "carparazzi_user"
                    password = System.getenv("DB_PASSWORD") ?: "carparazzi_pass"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        // 특정 테이블만 포함하도록 설정
                        // includes = "users|video_uploads|analysis_jobs|violation_events|evidence_clips|download_logs"
                        // excludes = ""
                        includes = ".*"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isValidationAnnotations = true // 검증 어노테이션 생성
                        isSpringAnnotations = true
                        isJpaAnnotations = false
                    }
                    target.apply {
                        packageName = "org.drakejin.carparazzi.entity.generated"
                        directory = "src/generated/kotlin"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}
