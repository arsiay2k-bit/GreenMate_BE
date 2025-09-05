#!/bin/bash

# GreenMate Backend 완전 설치 스크립트
# 서버: 103.244.108.70 (root / hlihli1!)
# 사용법: 서버에서 root 권한으로 실행

set -e

echo "=========================================="
echo "    GreenMate Backend 배포 파이프라인"
echo "    서버: 103.244.108.70"
echo "=========================================="

# 스크립트 실행 디렉터리 확인
DEPLOY_DIR="/greenmate"
SCRIPT_DIR="$DEPLOY_DIR/GreenMate_BE/deploy"

# 1. 서버 초기 설정
echo "1단계: 서버 초기 설정 실행..."
bash $SCRIPT_DIR/setup-server.sh

# 2. SystemD 서비스 설정
echo "2단계: SystemD 서비스 설정..."
bash $SCRIPT_DIR/setup-systemd.sh

# 3. Nginx 리버스 프록시 설정
echo "3단계: Nginx 리버스 프록시 설정..."
bash $SCRIPT_DIR/setup-nginx.sh

# 4. 첫 배포 실행
echo "4단계: 첫 배포 실행..."
bash $DEPLOY_DIR/deploy.sh

echo "=========================================="
echo "           배포 파이프라인 완료!"
echo "=========================================="
echo ""
echo "🎉 GreenMate Backend가 성공적으로 배포되었습니다!"
echo ""
echo "📋 접속 정보:"
echo "  • 웹사이트: http://103.244.108.70"
echo "  • API 베이스: http://103.244.108.70/api/"
echo "  • Health Check: http://103.244.108.70/actuator/health"
echo "  • H2 Console: http://103.244.108.70/h2-console"
echo ""
echo "🔧 관리 명령어:"
echo "  • 배포: cd /greenmate && bash deploy.sh"
echo "  • 서비스 상태: systemctl status greenmate-backend"
echo "  • 로그 확인: journalctl -u greenmate-backend -f"
echo "  • 재시작: systemctl restart greenmate-backend"
echo ""
echo "📁 주요 파일 위치:"
echo "  • 애플리케이션: /greenmate/GreenMate_BE-0.0.1-SNAPSHOT.jar"
echo "  • 소스코드: /greenmate/GreenMate_BE/"
echo "  • 로그: journalctl -u greenmate-backend"
echo ""