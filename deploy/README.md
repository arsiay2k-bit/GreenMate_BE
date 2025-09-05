# GreenMate Backend 배포 가이드

## 서버 정보
- **IP**: 103.244.108.70
- **계정**: root / hlihli1!
- **배포 디렉터리**: /greenmate

## 🚀 빠른 시작 (최초 설치)

### 1단계: 서버 접속
```bash
ssh root@103.244.108.70
# 비밀번호: hlihli1!
```

### 2단계: 초기 설치 실행
```bash
# 배포 디렉터리 생성 및 저장소 클론
mkdir -p /greenmate
cd /greenmate
git clone https://github.com/mhsssshin/GreenMate_BE.git

# 모든 스크립트 실행 권한 부여
chmod +x /greenmate/GreenMate_BE/deploy/*.sh

# 완전 자동 설치 실행
bash /greenmate/GreenMate_BE/deploy/install.sh
```

## 📋 개별 설정 스크립트

필요에 따라 개별 스크립트를 실행할 수 있습니다:

### 서버 초기 설정
```bash
bash /greenmate/GreenMate_BE/deploy/setup-server.sh
```

### SystemD 서비스 설정
```bash
bash /greenmate/GreenMate_BE/deploy/setup-systemd.sh
```

### Nginx 리버스 프록시 설정
```bash
bash /greenmate/GreenMate_BE/deploy/setup-nginx.sh
```

### 애플리케이션 배포
```bash
cd /greenmate
bash deploy.sh
```

## 🔄 일상적인 배포

코드 변경 후 새로 배포할 때:

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

### 로그 확인
```bash
# 실시간 로그 확인
journalctl -u greenmate-backend -f

# 최근 로그 확인
journalctl -u greenmate-backend -n 100

# 오늘 로그 확인
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

## 🌐 접속 URL

- **웹사이트**: http://103.244.108.70
- **API 베이스**: http://103.244.108.70/api/
- **Health Check**: http://103.244.108.70/actuator/health
- **H2 Console**: http://103.244.108.70/h2-console

## 📁 주요 파일 위치

```
/greenmate/
├── GreenMate_BE/                    # 소스코드 (Git 저장소)
├── GreenMate_BE-0.0.1-SNAPSHOT.jar # 실행 파일
├── GreenMate_BE-0.0.1-SNAPSHOT.jar.old # 백업 파일
└── deploy.sh                       # 배포 스크립트

/etc/systemd/system/
└── greenmate-backend.service       # SystemD 서비스 파일

/etc/nginx/sites-available/
└── greenmate                       # Nginx 설정 파일
```

## 🔍 트러블슈팅

### 서비스가 시작되지 않는 경우
```bash
# 로그 확인
journalctl -u greenmate-backend -n 50

# Java 프로세스 확인
ps aux | grep java

# 포트 사용 확인
netstat -tlnp | grep 8080
```

### 웹에서 접속되지 않는 경우
```bash
# Nginx 상태 확인
systemctl status nginx

# Nginx 로그 확인
tail -f /var/log/nginx/greenmate_error.log

# 방화벽 확인 (필요시)
ufw status
```

### 배포 실패 시
```bash
# 이전 버전으로 롤백
cd /greenmate
cp GreenMate_BE-0.0.1-SNAPSHOT.jar.old GreenMate_BE-0.0.1-SNAPSHOT.jar
systemctl restart greenmate-backend
```

## 📚 API 테스트

### Health Check
```bash
curl http://103.244.108.70/actuator/health
```

### API 테스트
```bash
# 도보 경로 검색
curl "http://103.244.108.70/api/walk/nearby-routes?lat=37.5665&lng=126.9784&radius=1000"

# ESG 대시보드
curl http://103.244.108.70/api/esg/dashboard

# 위치 검색
curl "http://103.244.108.70/api/locations/search?query=명동"
```