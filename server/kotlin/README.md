# Carparazzi - Kotlin Multi-Module Project

Clean Architecture 원칙을 따르는 Kotlin 멀티모듈 프로젝트입니다.

## 프로젝트 구조

```
carparazzi/
├── apps/
│   └── service_api/          # Spring Boot REST API 애플리케이션
│       ├── src/main/kotlin/
│       │   └── org/drakejin/carparazzi/
│       │       ├── CarparazziApplication.kt
│       │       ├── controller/
│       │       │   └── ButtonController.kt
│       │       ├── infrastructure/
│       │       │   └── InMemoryButtonRepository.kt
│       │       └── config/
│       │           └── ButtonConfiguration.kt
│       └── build.gradle.kts
├── domains/                  # 도메인 레이어 (비즈니스 로직)
│   ├── src/main/kotlin/
│   │   └── org/drakejin/carparazzi/domain/
│   │       ├── repository/
│   │       │   └── ButtonRepository.kt
│   │       └── usecase/
│   │           ├── CreateButtonUseCase.kt
│   │           └── GetButtonUseCase.kt
│   └── build.gradle.kts
├── entity/                   # 엔티티 레이어 (핵심 비즈니스 규칙)
│   ├── src/main/kotlin/
│   │   └── org/drakejin/carparazzi/entity/
│   │       └── Button.kt
│   ├── src/test/kotlin/
│   │   └── org/drakejin/carparazzi/entity/
│   │       └── ButtonTest.kt
│   └── build.gradle.kts
├── settings.gradle.kts
├── build.gradle.kts
└── README.md
```

## Clean Architecture 레이어

### 1. Entity Layer (`entity` 모듈)
- 핵심 비즈니스 규칙과 엔티티를 포함
- 외부 의존성이 없는 순수한 비즈니스 로직
- 예: `Button` 엔티티

### 2. Domain Layer (`domains` 모듈)
- Use Case와 Repository 인터페이스를 포함
- 비즈니스 로직의 흐름을 정의
- 예: `CreateButtonUseCase`, `GetButtonUseCase`, `ButtonRepository`

### 3. Infrastructure & Presentation Layer (`apps/service_api` 모듈)
- Spring Boot 애플리케이션
- REST API Controller (Presentation)
- Repository 구현체 (Infrastructure)
- 의존성 주입 설정

## SOLID 원칙 적용

### Single Responsibility Principle (SRP)
- 각 클래스는 하나의 책임만 가짐
- `Button` 엔티티: 버튼의 상태와 행동만 관리
- `CreateButtonUseCase`: 버튼 생성 로직만 담당

### Open/Closed Principle (OCP)
- 확장에는 열려있고 수정에는 닫혀있음
- 새로운 Use Case 추가 시 기존 코드 수정 없이 확장 가능

### Liskov Substitution Principle (LSP)
- `ButtonRepository` 인터페이스의 구현체들은 서로 대체 가능

### Interface Segregation Principle (ISP)
- 클라이언트가 사용하지 않는 인터페이스에 의존하지 않음
- Repository는 필요한 메서드만 정의

### Dependency Inversion Principle (DIP)
- 고수준 모듈이 저수준 모듈에 의존하지 않음
- Use Case는 Repository 인터페이스에 의존 (구현체에 의존하지 않음)

## 모듈 의존성

```
apps/service_api
    ↓
domains ← entity
```

- `apps/service_api`: `domains`와 `entity` 모듈에 의존
- `domains`: `entity` 모듈에 의존
- `entity`: 외부 의존성 없음 (가장 안정적)

## 빌드 및 실행

### 전체 프로젝트 빌드
```bash
./gradlew build
```

### 테스트 실행
```bash
./gradlew test
```

### Spring Boot 애플리케이션 실행
```bash
./gradlew :apps:service_api:bootRun
```

## API 엔드포인트

### 버튼 관련 API
- `GET /api/carparazzi` - 모든 버튼 조회
- `GET /api/carparazzi/{id}` - ID로 버튼 조회
- `GET /api/carparazzi/active/{isActive}` - 활성 상태로 버튼 조회
- `POST /api/carparazzi` - 새 버튼 생성

### 요청 예시
```bash
# 버튼 생성
curl -X POST http://localhost:8080/api/carparazzi \
  -H "Content-Type: application/json" \
  -d '{"name": "My Button", "description": "Test button"}'

# 모든 버튼 조회
curl http://localhost:8080/api/carparazzi

# 특정 버튼 조회
curl http://localhost:8080/api/carparazzi/1
```

## 개발 가이드라인

### 코드 품질
- Clean Code 원칙 준수
- SOLID 원칙 적용
- 단위 테스트 작성 (80% 이상 커버리지 목표)
- 의미있는 변수명과 함수명 사용

### 테스트 작성
- AAA 패턴 (Arrange, Act, Assert) 사용
- 테스트 이름은 `should_ExpectedBehavior_When_StateUnderTest` 형식
- 각 테스트는 하나의 동작만 검증

### 새로운 기능 추가
1. Entity에 새로운 비즈니스 규칙 추가
2. Domain에 Use Case 작성
3. Repository 인터페이스에 필요한 메서드 추가
4. Infrastructure에 Repository 구현체 수정
5. Presentation에 Controller 엔드포인트 추가
6. 각 레이어별 테스트 작성

## 기술 스택

- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.3
- **Build Tool**: Gradle with Kotlin DSL
- **Testing**: JUnit 5, MockK
- **Architecture**: Clean Architecture
- **Design Patterns**: Repository Pattern, Dependency Injection
