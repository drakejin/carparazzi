# 🚗 carparazzi

> **운전하다 빡치는 일이 생겼다구요?**
> **카파라찌. 신고를 쉽게**

AI 기반 교통위반 자동감지 및 신고지원 서비스

블랙박스 영상을 업로드하면 AI가 자동으로 교통위반을 감지하고, 신고용 증거영상 구간을 제안해주는 서비스입니다.

## 🎯 핵심 기능

- **🤖 AI 자동 감지**: 신호위반, 차선침범, 급정거, 충돌사고, 난폭운전 자동 감지
- **✂️ 스마트 트리밍**: 위반 구간을 포함한 최적 증거영상 자동 생성
- **📱 간편한 업로드**: 드래그 앤 드롭으로 간단한 영상 업로드
- **⚡ 빠른 처리**: 1시간 영상을 10분 내 분석 완료
- **📊 신뢰도 점수**: 각 위반행위별 AI 신뢰도 점수 제공

## 🏗️ 프로젝트 구조

```
carparazzi/
├── 📁 docs/           # 프로젝트 문서
│   └── prd/          # 제품 요구사항 문서
├── 📁 factory/        # 미디어 파일 실험실 및 리소스 생성
│   └── projects/     # Python 기반 실험 프로젝트들
├── 📁 infra/         # 인프라 관련 (Terraform)
│   └── terraform/    # AWS 인프라 코드
├── 📁 server/        # Kotlin Spring Boot 백엔드
└── 📁 web/           # Next.js 프론트엔드
```

## 🛠️ 기술 스택

### Frontend
- **Framework**: Next.js + TypeScript
- **Styling**: Tailwind CSS
- **State Management**: React Query
- **Deployment**: Vercel

### Backend
- **Language**: Kotlin + Spring Boot
- **Database**: PostgreSQL (Supabase)
- **Cache**: Redis
- **Queue**: Amazon SQS
- **Storage**: Amazon S3
- **Deployment**: AWS ECS Fargate

### AI/ML
- **Object Detection**: YOLO
- **Computer Vision**: OpenCV
- **Video Processing**: FFmpeg
- **Runtime**: ONNX Runtime

### Infrastructure
- **Cloud Provider**: AWS
- **Container**: Docker + ECS Fargate
- **IaC**: Terraform
- **Monitoring**: CloudWatch

## 📋 개발 로드맵

### Phase 1: MVP (3개월) 🚧
- [x] 프로젝트 구조 설계
- [x] PRD 문서 작성
- [ ] 인프라 구축 (Terraform)
- [ ] 백엔드 API 개발
- [ ] AI 모델 통합
- [ ] 프론트엔드 개발
- [ ] 통합 테스트

### Phase 2: 기능 확장 (2개월)
- [ ] 추가 위반 유형 감지
- [ ] 트리밍 구간 수동 조정
- [ ] 모바일 최적화
- [ ] 성능 최적화

### Phase 3: 고도화 (3개월)
- [ ] 복합 위반 패턴 감지
- [ ] 상세 분석 리포트
- [ ] API 외부 제공
- [ ] 사용자 피드백 학습

## 🚀 빠른 시작

### 사전 요구사항
- Docker & Docker Compose
- AWS CLI 설정
- Node.js 18+
- Python 3.13+
- Kotlin/Java 17+

## 📚 문서

- Phase1
  - [📋 PRD](./docs/PRD.20250701_step1_detect_accident.md)
  - [📋 ERD](./docs/ERD.20250701_step1_detect_accident.md)

## 🎯 성능 목표

- **처리 시간**: 1시간 영상 < 10분 분석
- **정확도**: 위반 감지 정확도 > 85%
- **가용성**: 서비스 업타임 > 99%
- **응답 시간**: API 응답 < 2초

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 📞 문의

프로젝트에 대한 질문이나 제안사항이 있으시면 이슈를 생성해 주세요.

---

**Made with ❤️ for safer roads**
