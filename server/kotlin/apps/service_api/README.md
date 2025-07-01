# Button API Service

Button 관리를 위한 REST API 서비스입니다. Clean Architecture 패턴을 적용하여 구현되었습니다.

## API 문서

이 프로젝트는 Spring REST Docs와 restdocs-api-spec을 사용하여 API 문서를 자동 생성합니다.

### API 문서 생성 방법

1. 테스트 실행으로 REST Docs 생성:
```bash
./gradlew test
```

2. 생성된 OpenAPI 스펙 확인:
```bash
# OpenAPI 3.0 스펙 파일 위치
apps/service_api/build/generated-snippets/openapi3.yaml
```

3. Swagger UI로 API 문서 확인:
```bash
# 애플리케이션 실행 후
# http://localhost:8080/swagger-ui/index.html 접속
./gradlew bootRun
```

### API 엔드포인트

#### 버튼 조회
- `GET /api/carparazzi` - 모든 버튼 목록 조회
- `GET /api/carparazzi/{id}` - 특정 ID의 버튼 조회
- `GET /api/carparazzi/active` - 활성 상태의 버튼들만 조회

#### 버튼 생성
- `POST /api/carparazzi` - 새로운 버튼 생성

### 요청/응답 예시

#### 버튼 생성 요청
```json
{
  "name": "Sample Button",
  "description": "This is a sample button",
  "type": "PRIMARY",
  "createdBy": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### 성공 응답
```json
{
  "success": true,
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Sample Button",
    "description": "This is a sample button",
    "type": "PRIMARY",
    "status": "ACTIVE",
    "createdAt": "2024-01-01T10:00:00",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "message": "Button created successfully"
  },
  "message": "Button created successfully",
  "timestamp": "2024-01-01T10:00:00"
}
```

#### 에러 응답
```json
{
  "error": "BAD_REQUEST",
  "message": "Button name cannot be blank",
  "timestamp": "2024-01-01T10:00:00",
  "path": "/api/carparazzi"
}
```

### 버튼 타입
- `DEFAULT` - 기본 버튼
- `PRIMARY` - 주요 버튼
- `SECONDARY` - 보조 버튼
- `SUCCESS` - 성공 버튼
- `WARNING` - 경고 버튼
- `DANGER` - 위험 버튼
- `INFO` - 정보 버튼
- `LIGHT` - 밝은 버튼
- `DARK` - 어두운 버튼

### 버튼 상태
- `ACTIVE` - 활성 상태
- `INACTIVE` - 비활성 상태
- `DELETED` - 삭제 상태

## 개발 환경 설정

### 필요 조건
- Java 21
- Kotlin 1.9.25
- Spring Boot 3.5.3

### 실행 방법
```bash
# 개발 서버 실행
./gradlew bootRun

# 테스트 실행
./gradlew test

# 빌드
./gradlew build
```

### 프로젝트 구조
```
apps/service_api/
├── src/main/kotlin/
│   └── org/drakejin/carparazzi/
│       ├── controller/          # REST API 컨트롤러
│       ├── dto/                 # 데이터 전송 객체
│       ├── config/              # 설정 클래스
│       └── infrastructure/      # 인프라스트럭처 구현
├── src/test/kotlin/
│   └── org/drakejin/carparazzi/
│       └── controller/          # REST Docs 테스트
└── build/generated-snippets/    # 생성된 API 문서
```

## 기술 스택
- **Framework**: Spring Boot 3.5.3
- **Language**: Kotlin 1.9.25
- **Architecture**: Clean Architecture
- **Documentation**: Spring REST Docs + restdocs-api-spec
- **Testing**: JUnit 5, MockK
- **Build Tool**: Gradle

## API 문서 자동화
이 프로젝트는 테스트 코드를 통해 API 문서를 자동으로 생성합니다:

1. **ButtonControllerTest**: 각 API 엔드포인트에 대한 테스트와 문서화
2. **REST Docs**: 테스트 실행 시 API 스니펫 생성
3. **restdocs-api-spec**: OpenAPI 3.0 스펙 파일 생성
4. **Swagger UI**: 생성된 스펙을 기반으로 인터랙티브 API 문서 제공

이를 통해 코드와 문서의 일관성을 보장하고, 항상 최신 상태의 정확한 API 문서를 제공합니다.
