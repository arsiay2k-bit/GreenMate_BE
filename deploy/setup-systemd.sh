#!/bin/bash

# GreenMate Backend systemd 서비스 설정 스크립트

set -e

SERVICE_NAME="greenmate-backend"
SERVICE_FILE="/etc/systemd/system/$SERVICE_NAME.service"
DEPLOY_DIR="/greenmate"

echo "=== SystemD 서비스 설정 시작 ==="

# 1. 서비스 파일 복사
echo "1. systemd 서비스 파일 복사..."
cp $DEPLOY_DIR/GreenMate_BE/deploy/$SERVICE_NAME.service $SERVICE_FILE

# 2. systemd 재로드
echo "2. systemd 데몬 재로드..."
systemctl daemon-reload

# 3. 서비스 활성화
echo "3. 서비스 활성화..."
systemctl enable $SERVICE_NAME

# 4. 서비스 시작
echo "4. 서비스 시작..."
systemctl start $SERVICE_NAME

# 5. 서비스 상태 확인
echo "5. 서비스 상태 확인..."
sleep 5
systemctl status $SERVICE_NAME --no-pager -l

echo "=== SystemD 서비스 설정 완료 ==="
echo "서비스 관리 명령어:"
echo "  상태 확인: systemctl status $SERVICE_NAME"
echo "  로그 확인: journalctl -u $SERVICE_NAME -f"
echo "  재시작: systemctl restart $SERVICE_NAME"
echo "  중지: systemctl stop $SERVICE_NAME"