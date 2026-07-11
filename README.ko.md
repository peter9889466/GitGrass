# GitGrass 🌿 (깃허브 잔디 관리 및 리포지토리 모니터링 웹앱)

GitGrass는 사용자의 깃허브 저장소를 상시 모니터링하여 일일 잔디(Contribution)를 거르지 않고 심을 수 있도록 도와주는 모던 풀스택 웹 애플리케이션입니다. 오랫동안 방치된 리포지토리를 판별하는 헬스 모니터링 대시보드와, 지정 시각까지 커밋이 없을 때 실시간 경고를 보내주는 디스코드 웹훅 알림 스케줄러 기능을 제공합니다.

---

## 🌟 핵심 기능 및 도메인 로직
1. **필수 깃허브 연동 OAuth2 인증**: 사용자는 GitHub 계정으로 간편하게 가입하고 로그인할 수 있습니다. 백엔드에서 사용자 프로필 정보 및 깃허브 Access Token을 안전하게 동기화합니다.
2. **리포지토리 헬스(Repository Health) 대시보드**: 깃허브 저장소를 상시 모니터링합니다. 특히 **최근 3개월 동안 변경 사항이 없는 브랜치나 오래된 리포지토리**를 감지하여 상단 배너와 경고 태그(정리 추천)로 대시보드에 리마인드 가이드를 제공합니다.
3. **선택적 디스코드 알림 스케줄러**: 디스코드 알림을 활성화하고 웹훅 URL 및 원하는 알림 마감 시간(예: 매일 밤 10시)을 설정하면, 해당 시간까지 커밋이 누락되었을 때 Discord 채널로 잔디 심기 독려 알림을 발송합니다. 디스코드 비연동 유저는 스케줄러 배치 대상에서 제외되어 핵심 기능이 정상 구동됩니다.

---

## 🛠 기술 스택

### 프론트엔드 (Frontend)
- **프레임워크**: React 18 (TypeScript)
- **스타일링**: Tailwind CSS v4 (글래스모피즘 효과 및 다크 네온 디자인 시스템 구축)
- **빌드 도구**: Vite

### 백엔드 (Backend)
- **개발 언어**: Kotlin 1.9.24
- **프레임워크**: Spring Boot 3.3.1 (Spring Security, OAuth2 Client)
- **데이터베이스**: PostgreSQL (JPA/Hibernate)
- **암호화 및 토큰**: JJWT 0.12.6, Java Cryptography Extension (JPA `AttributeConverter`를 활용한 토큰 자동 암/복호화)
- **HTTP 클라이언트**: Spring Boot 3.x 코어 `RestClient`

---

## 🔒 보안 아키텍처
- **무상태 JWT 인증**: 프론트엔드와 백엔드 간 API 통신은 JWT(JSON Web Token) 필터를 거치는 Stateless 구조로 격리되어 보안이 우수합니다.
- **토큰 양방향 암호화**: 데이터베이스 탈취 시 외부 권한 도용을 완벽하게 차단하기 위해, 디바이스의 데이터베이스 적재 시 `access_token` 및 `refresh_token`을 **AES-256-CBC** 알고리즘으로 자동 암호화하고 메모리에 올릴 때 자동 복호화하는 JPA Converter 인프라를 내장했습니다.

---

## 📂 프로젝트 폴더 구조

```text
GitGrass/
├── backend/                  # 코틀린 스프링 부트 프로젝트 폴더
│   ├── src/main/kotlin/      # 엔티티 설계, 보안 컴포넌트, API 비즈니스 로직
│   └── src/main/resources/   # 데이터베이스 및 OAuth2 클라이언트 프로퍼티
└── frontend/                 # 리액트 Vite 프로젝트 폴더
    ├── src/components/       # UI 화면 컴포넌트 (로그인, 대시보드, 마이페이지)
    └── src/index.css         # 글로벌 폰트 및 글래스모피즘 테마 스타일링
```

---

## 🚀 실행 및 로컬 시작 방법

### 사전 요구 조건
- Node.js (v18 이상)
- Java JDK 17 이상
- PostgreSQL 데이터베이스 인스턴스

### 프론트엔드 구동
```bash
cd frontend
npm install
npm run dev
```

### 백엔드 구동
```bash
cd backend
./gradlew bootRun
```
