# GreenMate API 샘플

## 🔐 인증 (Authentication)

GreenMate는 이메일/패스워드 기반 인증과 JWT 토큰을 사용한 인증 시스템을 제공합니다.

### 회원가입

#### POST /auth/signup
사용자 계정을 생성합니다.

**요청 예시:**
```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Test123Pass",
    "confirmPassword": "Test123Pass",
    "nickname": "사용자",
    "gender": "MALE",
    "age": 25,
    "height": 175.5,
    "weight": 70.2
  }'
```

**응답 예시:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJpYXQiOjE3NTcwNDc1MTEsImV4cCI6MTc1NzEzMzkxMX0...",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "user@example.com",
  "nickname": "사용자",
  "gender": "MALE",
  "age": 25,
  "height": 175.5,
  "weight": 70.2,
  "stepRecommendations": {
    "dailySteps": 9000,
    "dailyCaloriesBurn": 405.0,
    "dailyWalkingTimeMinutes": 90,
    "personalizedAdvice": "AI가 분석한 결과 하루 9,000걸음을 권장합니다. 젊은 나이이므로 활발한 활동을 권장합니다. 주 3-4회 이상 규칙적으로 걷기를 실천하시고, 걷기 전후 스트레칭을 잊지 마세요."
  }
}
```

#### POST /auth/login
사용자 로그인을 처리합니다.

**요청 예시:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Test123Pass"
  }'
```

**응답 예시:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJpYXQiOjE3NTcwNDc1MTEsImV4cCI6MTc1NzEzMzkxMX0...",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "user@example.com",
  "nickname": "사용자",
  "gender": "MALE",
  "age": 25,
  "height": 175.5,
  "weight": 70.2,
  "stepRecommendations": {
    "dailySteps": 9000,
    "dailyCaloriesBurn": 405.0,
    "dailyWalkingTimeMinutes": 90,
    "personalizedAdvice": "AI가 분석한 결과 하루 9,000걸음을 권장합니다. 젊은 나이이므로 활발한 활동을 권장합니다. 주 3-4회 이상 규칙적으로 걷기를 실천하시고, 걷기 전후 스트레칭을 잊지 마세요."
  }
}
```

### JWT 토큰 사용
인증이 필요한 API 호출 시 헤더에 포함:
```
Authorization: Bearer {JWT_TOKEN}
```

## 🤖 AI 에이전트 연동

### AI 추천 시스템
GreenMate는 AI 에이전트 서버(http://103.244.108.70:9000)와 연동하여 사용자의 개인 정보를 기반으로 맞춤형 걸음 수를 추천합니다.

#### AI 에이전트 API 호출 과정
1. **회원가입/로그인 시**: 자동으로 AI 에이전트 호출
2. **사용자 정보**: 나이, 성별, 키, 몸무게 → AI 에이전트로 전송
3. **AI 응답**: 개인맞춤 걸음 수 추천
4. **추가 계산**: 칼로리 소모량, 걷기 시간, 개인화된 조언 생성

#### AI 에이전트 요청 형식
```bash
curl -X POST "http://103.244.108.70:9000/api/v1/recommend/steps" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "사용자닉네임",
    "gender": "남",
    "age": 30,
    "height": 175.0,
    "weight": 70.5
  }'
```

#### AI 에이전트 응답 형식
```json
{
  "status": "success",
  "data": {
    "recommendedSteps": 8000
  }
}
```

### 인증 API

#### GET /auth/me
현재 로그인한 사용자 정보를 조회합니다.

**요청 예시:**
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/auth/me"
```

**응답 예시:**
```json
{
  "id": 1,
  "email": "user@example.com",
  "nickname": "사용자",
  "gender": "MALE",
  "age": 25,
  "height": 175.5,
  "weight": 70.2,
  "role": "USER"
}
```

#### POST /auth/refresh
JWT 토큰을 갱신합니다.

**요청 예시:**
```bash
curl -X POST \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/auth/refresh"
```

**응답 예시:**
```json
{
  "accessToken": "NEW_JWT_TOKEN",
  "tokenType": "Bearer"
}
```

#### POST /auth/logout
로그아웃합니다. (클라이언트에서 토큰 제거)

**요청 예시:**
```bash
curl -X POST "http://localhost:8080/auth/logout"
```

**응답 예시:**
```json
{
  "message": "Successfully logged out"
}
```

## 👤 사용자 관리

### GET /api/users/profile
사용자 프로필 정보를 조회합니다.

**요청 예시:**
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/api/users/profile"
```

**응답 예시:**
```json
{
  "id": 1,
  "email": "user@example.com",
  "nickname": "사용자",
  "gender": "MALE",
  "age": 25,
  "height": 175.5,
  "weight": 70.2,
  "role": "USER",
  "createdAt": "2024-09-04T17:30:15.123456",
  "stepRecommendations": {
    "dailySteps": 9000,
    "dailyCaloriesBurn": 360.0,
    "dailyWalkingTimeMinutes": 90,
    "personalizedAdvice": "하루 9,000걸음을 목표로 설정했습니다. 젊은 나이이므로 활발한 활동을 권장합니다. 주 3-4회 이상 규칙적으로 걷기를 실천하시고, 걷기 전후 스트레칭을 잊지 마세요.",
    "updatedAt": "2024-09-04T17:35:22.789123"
  }
}
```

### GET /api/users/walk-records
사용자의 걸음 기록을 조회합니다.

**요청 예시:**
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/api/users/walk-records"
```

### GET /api/users/walk-statistics
사용자의 걸음 통계를 조회합니다.

**요청 예시:**
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/api/users/walk-statistics"
```

**응답 예시:**
```json
{
  "totalDistance": 15.4,
  "totalSteps": 19250,
  "totalWalkCount": 12,
  "averageDistance": 1.28
}
```

### GET /api/users/walk-records/recent
최근 걸음 기록을 조회합니다.

**요청 예시:**
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/api/users/walk-records/recent?days=7"
```

### GET /api/users/recommendations
사용자의 개인맞춤형 걸음 수 추천 정보를 조회합니다.

**요청 예시:**
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/api/users/recommendations"
```

**응답 예시:**
```json
{
  "dailySteps": 9000,
  "dailyCaloriesBurn": 360.0,
  "dailyWalkingTimeMinutes": 90,
  "personalizedAdvice": "하루 9,000걸음을 목표로 설정했습니다. 젊은 나이이므로 활발한 활동을 권장합니다. 주 3-4회 이상 규칙적으로 걷기를 실천하시고, 걷기 전후 스트레칭을 잊지 마세요.",
  "updatedAt": "2024-09-04T17:35:22.789123"
}
```

### POST /api/users/refresh-recommendations
AI 에이전트를 호출하여 사용자의 개인맞춤형 걸음 수 추천 정보를 갱신합니다.

**요청 예시:**
```bash
curl -X POST \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/api/users/refresh-recommendations"
```

**응답 예시:**
```json
{
  "message": "걸음 수 추천 정보가 업데이트되었습니다.",
  "dailySteps": 8500,
  "dailyCaloriesBurn": 340.0,
  "dailyWalkingTimeMinutes": 85,
  "personalizedAdvice": "하루 8,500걸음을 목표로 설정했습니다. 현재 BMI를 고려하여 적절한 운동량을 설정했습니다. 주 3-4회 이상 규칙적으로 걷기를 실천하시고, 걷기 전후 스트레칭을 잊지 마세요.",
  "updatedAt": "2024-09-04T18:15:35.456789"
}
```

## 1. 경로 추천 API

### POST /api/routes/search
출발지와 목적지를 입력하여 3가지 경로 옵션을 받습니다.

**요청 예시:**
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

**응답 예시:**
```json
{
  "routes": [
    {
      "routeType": "LESS_WALK",
      "distanceKm": 0.35,
      "durationMinutes": 7,
      "estimatedSteps": 437,
      "startLocation": {
        "latitude": 37.5663,
        "longitude": 126.9997,
        "address": "서울시청",
        "name": null
      },
      "endLocation": {
        "latitude": 37.5636,
        "longitude": 126.9866,
        "address": "명동",
        "name": null
      },
      "waypoints": null,
      "polyline": "mock_polyline_37.566300_126.999700_to_37.563600_126.986600"
    },
    {
      "routeType": "RECOMMENDED",
      "distanceKm": 0.42,
      "durationMinutes": 8,
      "estimatedSteps": 525,
      "startLocation": {
        "latitude": 37.5663,
        "longitude": 126.9997,
        "address": "서울시청",
        "name": null
      },
      "endLocation": {
        "latitude": 37.5636,
        "longitude": 126.9866,
        "address": "명동",
        "name": null
      },
      "waypoints": null,
      "polyline": "mock_polyline_37.566300_126.999700_to_37.563600_126.986600"
    },
    {
      "routeType": "MORE_WALK",
      "distanceKm": 0.52,
      "durationMinutes": 10,
      "estimatedSteps": 650,
      "startLocation": {
        "latitude": 37.5663,
        "longitude": 126.9997,
        "address": "서울시청",
        "name": null
      },
      "endLocation": {
        "latitude": 37.5636,
        "longitude": 126.9866,
        "address": "명동",
        "name": null
      },
      "waypoints": null,
      "polyline": "mock_polyline_37.566300_126.999700_to_37.563600_126.986600"
    }
  ]
}
```

## 2. 위치 검색 API

### GET /api/locations/search
키워드로 위치를 검색합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/api/locations/search?query=강남"
```

**응답 예시:**
```json
[
  {
    "latitude": 37.4981,
    "longitude": 127.0276,
    "address": "서울특별시 강남구 강남대로 390",
    "name": "강남역"
  }
]
```

### GET /api/locations/autocomplete
자동완성 검색 (개수 제한 가능)

**요청 예시:**
```bash
curl "http://localhost:8080/api/locations/autocomplete?query=홍&limit=3"
```

**응답 예시:**
```json
[
  {
    "latitude": 37.5563,
    "longitude": 126.9244,
    "address": "서울특별시 마포구 양화로 160",
    "name": "홍대입구역"
  }
]
```

## 3. 걸음 기록 API

### POST /api/walk-records
완료된 걸음 기록을 저장합니다.

**요청 예시:**
```bash
curl -X POST http://localhost:8080/api/walk-records \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "startLatitude": 37.5663,
    "startLongitude": 126.9997,
    "startAddress": "서울시청",
    "endLatitude": 37.5636,
    "endLongitude": 126.9866,
    "endAddress": "명동",
    "distanceKm": 0.42,
    "durationMinutes": 8,
    "steps": 525,
    "routeType": "RECOMMENDED"
  }'
```

**응답 예시:**
```json
{
  "id": 1,
  "startLatitude": 37.5663,
  "startLongitude": 126.9997,
  "startAddress": "서울시청",
  "endLatitude": 37.5636,
  "endLongitude": 126.9866,
  "endAddress": "명동",
  "distanceKm": 0.42,
  "durationMinutes": 8,
  "steps": 525,
  "routeType": "RECOMMENDED",
  "createdAt": "2024-09-04T17:30:15.123456"
}
```

### GET /api/walk-records
모든 걸음 기록을 조회합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/api/walk-records"
```

**응답 예시:**
```json
[
  {
    "id": 1,
    "startLatitude": 37.5663,
    "startLongitude": 126.9997,
    "startAddress": "서울시청",
    "endLatitude": 37.5636,
    "endLongitude": 126.9866,
    "endAddress": "명동",
    "distanceKm": 0.42,
    "durationMinutes": 8,
    "steps": 525,
    "routeType": "RECOMMENDED",
    "createdAt": "2024-09-04T17:30:15.123456"
  }
]
```

### GET /api/walk-records/{id}
특정 걸음 기록을 조회합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/api/walk-records/1"
```

**응답 예시:**
```json
{
  "id": 1,
  "startLatitude": 37.5663,
  "startLongitude": 126.9997,
  "startAddress": "서울시청",
  "endLatitude": 37.5636,
  "endLongitude": 126.9866,
  "endAddress": "명동",
  "distanceKm": 0.42,
  "durationMinutes": 8,
  "steps": 525,
  "routeType": "RECOMMENDED",
  "createdAt": "2024-09-04T17:30:15.123456"
}
```

### GET /api/walk-records/date-range
기간별 걸음 기록을 조회합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/api/walk-records/date-range?startDate=2024-09-01T00:00:00&endDate=2024-09-30T23:59:59"
```

**응답 예시:**
```json
[
  {
    "id": 1,
    "startLatitude": 37.5663,
    "startLongitude": 126.9997,
    "startAddress": "서울시청",
    "endLatitude": 37.5636,
    "endLongitude": 126.9866,
    "endAddress": "명동",
    "distanceKm": 0.42,
    "durationMinutes": 8,
    "steps": 525,
    "routeType": "RECOMMENDED",
    "createdAt": "2024-09-04T17:30:15.123456"
  }
]
```

### GET /api/walk-records/statistics
걸음 통계를 조회합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/api/walk-records/statistics"
```

**응답 예시:**
```json
{
  "totalDistance": 2.15,
  "totalSteps": 2687,
  "totalWalkCount": 5,
  "averageDistance": 0.43
}
```

## 4. 기타 엔드포인트

### GET /actuator/health
애플리케이션 상태를 확인합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/actuator/health"
```

**응답 예시:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 494384795648,
        "free": 123456789,
        "threshold": 10485760,
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### H2 Database Console
개발용 데이터베이스 콘솔에 접속합니다.

**접속 정보:**
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:greenmate
- Username: sa
- Password: (공백)

## 테스트용 위치 데이터

서비스에는 다음 서울 주요 위치들이 미리 등록되어 있습니다:
- 강남역, 홍대입구역, 명동, 이태원, 종로3가
- 한강공원, 경복궁, 동대문, 신촌, 건대입구