#!/bin/bash

# GreenMate Backend 원격 배포 스크립트
# 이 스크립트를 서버에서 실행하세요

SERVER_IP="103.244.108.70"
REPO_URL="https://github.com/mhsssshin/GreenMate_BE.git"

echo "=========================================="
echo "🚀 GreenMate Backend 원격 배포 시작"
echo "서버: $SERVER_IP"
echo "=========================================="

# 배포 디렉터리 생성
echo "📁 배포 디렉터리 설정 중..."
mkdir -p /greenmate
cd /greenmate

# 기존 저장소 정리
echo "🗂️  기존 저장소 정리 중..."
if [ -d "GreenMate_BE" ]; then
    echo "기존 GreenMate_BE 디렉터리 삭제 중..."
    rm -rf GreenMate_BE
fi

# 최신 코드 클론
echo "📥 GitHub에서 최신 코드 다운로드 중..."
git clone $REPO_URL
if [ $? -ne 0 ]; then
    echo "❌ Git clone 실패! 네트워크 연결을 확인하세요."
    exit 1
fi

cd GreenMate_BE

# 스크립트 실행 권한 부여
echo "🔧 실행 권한 설정 중..."
chmod +x deploy/*.sh

# 메인 배포 스크립트 실행
echo "🚀 자동 배포 시작..."
bash deploy/install.sh

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "✅ 배포 성공!"
    echo "=========================================="
    echo ""
    echo "📋 접속 정보:"
    echo "• 웹사이트: http://$SERVER_IP"
    echo "• API: http://$SERVER_IP/api/"
    echo "• Health Check: http://$SERVER_IP/actuator/health"
    echo ""
    echo "🔧 관리 명령어:"
    echo "• 서비스 상태: systemctl status greenmate-backend"
    echo "• 로그 확인: journalctl -u greenmate-backend -f"
    echo "• 재배포: cd /greenmate && bash deploy.sh"
    echo ""
else
    echo ""
    echo "=========================================="
    echo "❌ 배포 실패!"
    echo "=========================================="
    echo ""
    echo "🔍 문제 해결:"
    echo "• 로그 확인: journalctl -u greenmate-backend -n 50"
    echo "• Java 설치 확인: java -version"
    echo "• 포트 확인: lsof -i :8080"
    echo ""
    exit 1
fi