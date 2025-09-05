# 🚀 GreenMate Backend 서버 배포 명령어

서버 **103.244.108.70**에 **root** 계정으로 로그인한 후 아래 명령어들을 순서대로 실행하세요.

## 1단계: 초기 설정 및 저장소 클론

```bash
# 배포 디렉터리 생성
mkdir -p /greenmate
cd /greenmate

# 기존 저장소가 있으면 삭제
if [ -d "GreenMate_BE" ]; then
    rm -rf GreenMate_BE
fi

# GitHub 저장소 클론
git clone https://github.com/mhsssshin/GreenMate_BE.git
cd GreenMate_BE

# 스크립트 실행 권한 부여
chmod +x deploy/*.sh
```

## 2단계: 원클릭 자동 설치 실행

```bash
# 완전 자동 설치 (모든 것을 한 번에)
bash /greenmate/GreenMate_BE/deploy/install.sh
```

**또는 개별 단계로 실행:**

### 2-1: 서버 환경 설정
```bash
bash /greenmate/GreenMate_BE/deploy/setup-server.sh
```

### 2-2: SystemD 서비스 설정
```bash
bash /greenmate/GreenMate_BE/deploy/setup-systemd.sh
```

### 2-3: Nginx 설정
```bash
bash /greenmate/GreenMate_BE/deploy/setup-nginx.sh
```

### 2-4: 첫 배포 실행
```bash
cd /greenmate
bash deploy.sh
```

## 3단계: 배포 확인

```bash
# 서비스 상태 확인
systemctl status greenmate-backend

# 로그 확인
journalctl -u greenmate-backend -n 50

# Health Check
curl http://localhost:8080/actuator/health
curl http://103.244.108.70/actuator/health
```

## 4단계: API 테스트

```bash
# 도보 경로 검색 테스트
curl "http://103.244.108.70/api/walk/nearby-routes?lat=37.5665&lng=126.9784&radius=1000"

# ESG 대시보드 테스트
curl http://103.244.108.70/api/esg/dashboard

# 위치 검색 테스트 (URL 인코딩된 한글)
curl "http://103.244.108.70/api/locations/search?query=%EB%AA%85%EB%8F%99"
```

## 🔄 향후 배포 (코드 변경 시)

```bash
cd /greenmate
bash deploy.sh
```

## 🛠 관리 명령어

### 서비스 관리
```bash
# 서비스 상태 확인
systemctl status greenmate-backend

# 서비스 재시작
systemctl restart greenmate-backend

# 서비스 중지
systemctl stop greenmate-backend

# 서비스 시작
systemctl start greenmate-backend
```

### 로그 관리
```bash
# 실시간 로그 확인
journalctl -u greenmate-backend -f

# 최근 100줄 로그 확인
journalctl -u greenmate-backend -n 100

# 오늘 로그만 확인
journalctl -u greenmate-backend --since today
```

### Nginx 관리
```bash
# Nginx 상태 확인
systemctl status nginx

# Nginx 설정 테스트
nginx -t

# Nginx 재시작
systemctl restart nginx
```

## 📍 접속 URL

설치 완료 후 다음 URL로 접속 가능합니다:

- **웹사이트**: http://103.244.108.70
- **API 베이스**: http://103.244.108.70/api/
- **Health Check**: http://103.244.108.70/actuator/health
- **H2 Console**: http://103.244.108.70/h2-console

## 🔧 트러블슈팅

### 포트 8080이 사용 중인 경우
```bash
# 포트 사용 프로세스 확인
netstat -tlnp | grep 8080
lsof -i :8080

# 해당 프로세스 종료
kill -9 <PID>
```

### 서비스가 시작되지 않는 경우
```bash
# 로그 확인
journalctl -u greenmate-backend -n 100

# Java 프로세스 확인
ps aux | grep java

# 수동 실행으로 디버깅
cd /greenmate
java -jar GreenMate_BE-0.0.1-SNAPSHOT.jar
```

### 권한 문제 발생 시
```bash
# 실행 권한 부여
chmod +x /greenmate/GreenMate_BE/deploy/*.sh
chmod +x /greenmate/deploy.sh
```