# GreenMate Backend

LifePlus GreenMate 도보 경로 추천 앱의 백엔드 서비스입니다. 건강 증진(걷기 습관화)과 ESG 활동(탄소 절감)을 연결하는 도보 가이드 앱을 위한 REST API를 제공합니다.

## 🚀 주요 기능

- **경로 추천**: 출발지와 목적지 간 3가지 도보 경로 옵션 제공 (최단/권장/최장 거리)
- **위치 검색**: 자동완성을 지원하는 위치 검색 API
- **걸음 기록**: 완료된 걸음 기록 저장 및 통계 제공
- **Maps API 통합**: Google Maps API 연동을 위한 준비된 서비스 구조

## 🛠 기술 스택

- **Framework**: Spring Boot 3.3.0
- **Build Tool**: Gradle 8.8
- **Java Version**: 21
- **Database**: H2 (인메모리, 개발용)
- **ORM**: Spring Data JPA
- **Dependencies**: Spring Web, Validation, Actuator, WebFlux, Lombok

## 🏃‍♂️ 실행 방법

### 사전 요구사항
- Java 21 (OpenJDK 권장)
- Gradle (Wrapper 포함되어 있음)

### 로컬 실행
```bash
# 프로젝트 클론 후
cd GreenMate_BE

# 애플리케이션 실행
./gradlew bootRun
```

### 빌드
```bash
# 프로젝트 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 클린 빌드
./gradlew clean build
```

## 🌐 API 엔드포인트

서버 실행 후 `http://localhost:8080`에서 접근 가능

### 경로 추천
- `POST /api/routes/search` - 3가지 경로 옵션 조회

### 위치 검색
- `GET /api/locations/search?query={검색어}` - 위치 검색
- `GET /api/locations/autocomplete?query={검색어}&limit={개수}` - 자동완성

### 걸음 기록
- `POST /api/walk-records` - 걸음 기록 저장
- `GET /api/walk-records` - 모든 기록 조회
- `GET /api/walk-records/{id}` - 특정 기록 조회
- `GET /api/walk-records/statistics` - 통계 조회
- `GET /api/walk-records/date-range` - 기간별 조회

### 모니터링
- `GET /actuator/health` - 서버 상태 확인

## 📊 데이터베이스

개발 환경에서는 H2 인메모리 데이터베이스를 사용합니다.

### H2 콘솔 접근
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:greenmate`
- Username: `sa`
- Password: (공백)

## 📝 API 사용 예시

자세한 API 사용 예시는 [API_SAMPLES.md](API_SAMPLES.md) 파일을 참조하세요.

### 경로 추천 예시
```bash
curl -X POST http://localhost:8080/api/routes/search \
  -H "Content-Type: application/json" \
  -d '{
    "startLatitude": 37.5663,
    "startLongitude": 126.9997,
    "startAddress": "서울시청",
    "endLatitude": 37.5636,
    "endLongitude": 126.9866,
    "endAddress": "명동"
  }'
```

### 위치 검색 예시
```bash
curl "http://localhost:8080/api/locations/search?query=강남"
```

## 🗺 테스트 데이터

서비스에는 다음 서울 주요 위치들이 미리 등록되어 있습니다:
- 강남역, 홍대입구역, 명동, 이태원, 종로3가
- 한강공원, 경복궁, 동대문, 신촌, 건대입구

## 🔧 설정

### Maps API 연동 (선택사항)
`src/main/resources/application.yml`에서 Google Maps API 키를 설정할 수 있습니다:
```yaml
maps:
  api:
    key: your-google-maps-api-key
    base-url: https://maps.googleapis.com/maps/api
```

### 포트 변경
기본 포트는 8080입니다. 변경하려면 `application.yml`에서 수정하세요:
```yaml
server:
  port: 8080
```

## 📁 프로젝트 구조

```
src/main/java/com/greenmate/
├── GreenMateApplication.java     # 메인 애플리케이션
├── entity/                       # JPA 엔티티
├── dto/                         # 데이터 전송 객체
├── controller/                  # REST 컨트롤러
├── service/                     # 비즈니스 로직
├── repository/                  # 데이터 접근 계층
└── config/                      # 설정 클래스
```

## 🐛 문제 해결

### Java 버전 문제
Java 21이 설치되지 않은 경우:
```bash
# Homebrew를 통한 설치 (macOS)
brew install openjdk@21

# 환경변수 설정
export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
```

### 포트 충돌
8080 포트가 이미 사용 중인 경우 `application.yml`에서 포트를 변경하세요.

## 📚 참고 문서

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [H2 Database](http://www.h2database.com/)
- [Gradle User Manual](https://docs.gradle.org/)

## 📄 라이센스

This project is licensed under the MIT License.