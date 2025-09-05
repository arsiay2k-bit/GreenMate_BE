# 🚨 GreenMate Backend 배포 상태

## 현재 상황
- ✅ **GitHub 저장소**: 모든 배포 스크립트 업로드 완료
- ✅ **서버 접속**: 103.244.108.70 서버 접속 가능
- ✅ **웹사이트**: 프론트엔드 웹사이트 정상 실행 중
- ❌ **백엔드 API**: 아직 배포되지 않음 (404 에러)

## 📋 배포 필요 작업

**서버 103.244.108.70에 SSH 로그인 후 다음 명령어 실행:**

### 1단계: 저장소 준비
```bash
ssh root@103.244.108.70
# 패스워드: hlihli1!

mkdir -p /greenmate
cd /greenmate
```

### 2단계: 소스코드 다운로드
```bash
# 기존 저장소가 있으면 삭제
if [ -d "GreenMate_BE" ]; then rm -rf GreenMate_BE; fi

# 최신 코드 클론
git clone https://github.com/mhsssshin/GreenMate_BE.git
cd GreenMate_BE

# 실행 권한 부여
chmod +x deploy/*.sh
```

### 3단계: 자동 배포 실행
```bash
# 원클릭 배포 (모든 것을 한번에)
bash deploy/install.sh
```

**또는 개별 단계로 실행:**
```bash
# 서버 환경 설정
bash deploy/setup-server.sh

# SystemD 서비스 설정
bash deploy/setup-systemd.sh

# Nginx 리버스 프록시 설정
bash deploy/setup-nginx.sh

# 애플리케이션 배포
cd /greenmate
bash deploy.sh
```

### 4단계: 배포 확인
```bash
# 서비스 상태 확인
systemctl status greenmate-backend

# 로그 확인
journalctl -u greenmate-backend -n 20

# API 테스트
curl http://localhost:8080/actuator/health
curl "http://localhost:8080/api/walk/nearby-routes?lat=37.5665&lng=126.9784&radius=1000"
```

## 🎯 배포 완료 후 예상 결과

배포가 성공하면 다음 URL들이 모두 작동합니다:
- **웹사이트**: http://103.244.108.70 ✅ (이미 작동)
- **백엔드 API**: http://103.244.108.70/api/
- **Health Check**: http://103.244.108.70/actuator/health
- **ESG API**: http://103.244.108.70/api/esg/dashboard
- **Walking API**: http://103.244.108.70/api/walk/nearby-routes

## 🔧 문제 해결

### 서비스 시작 실패 시:
```bash
journalctl -u greenmate-backend -f
```

### 포트 충돌 시:
```bash
lsof -i :8080
kill -9 <PID>
```

### 권한 문제 시:
```bash
chmod +x /greenmate/GreenMate_BE/deploy/*.sh
```

---
**배포 완료 후 이 파일을 다시 확인하여 모든 API가 정상 작동하는지 테스트해주세요!**