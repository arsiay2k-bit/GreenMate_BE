#!/bin/bash

# GreenMate Backend 배포 스크립트
# 서버: 103.244.108.70
# 사용법: /greenmate 디렉터리에서 이 스크립트를 실행하세요

set -e

DEPLOY_DIR="/greenmate"
APP_NAME="greenmate-backend"
APP_PORT="8080"
JAR_NAME="GreenMate_BE-0.0.1-SNAPSHOT.jar"

echo "=== GreenMate Backend 배포 시작 ==="

cd $DEPLOY_DIR

# 1. 기존 애플리케이션 중지
echo "1. 기존 애플리케이션 중지..."
if systemctl is-active --quiet $APP_NAME; then
    echo "기존 서비스 중지 중..."
    systemctl stop $APP_NAME
fi

# 2. 최신 코드 pull
echo "2. 최신 코드 업데이트..."
cd GreenMate_BE
git fetch origin
git reset --hard origin/main
git pull origin main

# 3. 애플리케이션 빌드
echo "3. 애플리케이션 빌드..."
./gradlew clean build -x test

# 4. JAR 파일 복사
echo "4. JAR 파일 복사..."
cp build/libs/$JAR_NAME $DEPLOY_DIR/

# 5. 백업 생성
echo "5. 이전 버전 백업..."
if [ -f "$DEPLOY_DIR/$JAR_NAME.backup" ]; then
    rm $DEPLOY_DIR/$JAR_NAME.backup
fi
if [ -f "$DEPLOY_DIR/$JAR_NAME.old" ]; then
    mv $DEPLOY_DIR/$JAR_NAME.old $DEPLOY_DIR/$JAR_NAME.backup
fi
if [ -f "$DEPLOY_DIR/$JAR_NAME" ] && [ -f "$DEPLOY_DIR/build/libs/$JAR_NAME" ]; then
    cp $DEPLOY_DIR/$JAR_NAME $DEPLOY_DIR/$JAR_NAME.old
fi

# 6. 새 버전 배포
echo "6. 새 JAR 파일 배포..."
cp build/libs/$JAR_NAME $DEPLOY_DIR/

# 7. 서비스 시작
echo "7. 서비스 시작..."
systemctl start $APP_NAME
sleep 5

# 8. 서비스 상태 확인
echo "8. 서비스 상태 확인..."
if systemctl is-active --quiet $APP_NAME; then
    echo "✅ 서비스가 성공적으로 시작되었습니다."
    systemctl status $APP_NAME --no-pager -l
else
    echo "❌ 서비스 시작 실패!"
    journalctl -u $APP_NAME --no-pager -l -n 20
    exit 1
fi

# 9. Health Check
echo "9. Health Check 수행..."
sleep 10
for i in {1..30}; do
    if curl -s http://localhost:$APP_PORT/actuator/health > /dev/null; then
        echo "✅ 애플리케이션이 정상적으로 실행 중입니다."
        curl -s http://localhost:$APP_PORT/actuator/health | jq '.' || echo "Health check 성공 (jq 없음)"
        break
    else
        echo "Health check 시도 $i/30..."
        sleep 2
    fi
    
    if [ $i -eq 30 ]; then
        echo "❌ Health check 실패!"
        journalctl -u $APP_NAME --no-pager -l -n 50
        exit 1
    fi
done

echo "=== 배포 완료 ==="
echo "애플리케이션 URL: http://103.244.108.70:$APP_PORT"
echo "Health Check: http://103.244.108.70:$APP_PORT/actuator/health"