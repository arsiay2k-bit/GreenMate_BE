#!/bin/bash

# GreenMate Backend 서버 초기 설정 스크립트
# 서버: 103.244.108.70
# 사용법: root 계정으로 이 스크립트를 실행하세요

set -e

echo "=== GreenMate Backend 서버 설정 시작 ==="

# 1. 필요한 패키지 설치
echo "1. 시스템 업데이트 및 필요 패키지 설치..."
apt update && apt upgrade -y
apt install -y openjdk-21-jdk git nginx curl wget unzip

# 2. 배포 디렉터리 생성
echo "2. 배포 디렉터리 생성..."
mkdir -p /greenmate
cd /greenmate

# 3. Git 저장소 클론
echo "3. GitHub 저장소 클론..."
if [ -d "GreenMate_BE" ]; then
    echo "기존 저장소 제거 중..."
    rm -rf GreenMate_BE
fi
git clone https://github.com/mhsssshin/GreenMate_BE.git
cd GreenMate_BE

# 4. 빌드 권한 설정
echo "4. Gradle 실행 권한 설정..."
chmod +x ./gradlew

# 5. 첫 번째 빌드 (의존성 다운로드)
echo "5. 첫 번째 빌드 실행..."
./gradlew clean build

echo "=== 서버 초기 설정 완료 ==="
echo "다음 단계: deploy.sh 스크립트를 실행하여 애플리케이션을 배포하세요."