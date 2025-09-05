#!/bin/bash

# GreenMate Backend 빠른 배포 스크립트 (JAR 교체만)
# 서버: 103.244.108.70 (root / hlihli1!)

SERVER="103.244.108.70"
USER="root"
PASSWORD="hlihli1!"
APP_NAME="greenmate-backend"
JAR_NAME="greenmate-be-0.0.1-SNAPSHOT.jar"
LOCAL_JAR="build/libs/$JAR_NAME"

echo "=========================================="
echo "    GreenMate Backend 빠른 배포 시작"
echo "    서버: $SERVER"
echo "    JAR 파일만 교체하고 서비스 재시작"
echo "=========================================="

# 로컬 JAR 파일 존재 확인
if [ ! -f "$LOCAL_JAR" ]; then
    echo "❌ 로컬 JAR 파일이 없습니다: $LOCAL_JAR"
    echo "먼저 './gradlew clean bootJar' 명령을 실행하세요."
    exit 1
fi

echo "✅ 로컬 JAR 파일 확인: $LOCAL_JAR"

# Expect 스크립트를 사용해서 서버에 접속하고 배포
expect -c "
set timeout 60
spawn scp $LOCAL_JAR $USER@$SERVER:/greenmate/

expect {
    \"password:\" {
        send \"$PASSWORD\r\"
        exp_continue
    }
    \"yes/no\" {
        send \"yes\r\"
        exp_continue
    }
    eof {
        puts \"JAR 파일 업로드 완료\"
    }
}
"

if [ $? -eq 0 ]; then
    echo "✅ JAR 파일 업로드 성공"
else
    echo "❌ JAR 파일 업로드 실패"
    exit 1
fi

# 서버에 SSH 접속하여 서비스 재시작
expect -c "
set timeout 60
spawn ssh $USER@$SERVER

expect {
    \"password:\" {
        send \"$PASSWORD\r\"
        exp_continue
    }
    \"yes/no\" {
        send \"yes\r\"
        exp_continue
    }
    \"# \" {
        # 서비스 중지
        send \"systemctl stop $APP_NAME\r\"
        expect \"# \"
        
        # 잠시 대기
        send \"sleep 2\r\"
        expect \"# \"
        
        # 서비스 시작
        send \"systemctl start $APP_NAME\r\"
        expect \"# \"
        
        # 서비스 상태 확인
        send \"systemctl status $APP_NAME --no-pager -l\r\"
        expect \"# \"
        
        send \"exit\r\"
        expect eof
    }
}
"

echo ""
echo "=========================================="
echo "           빠른 배포 완료!"
echo "=========================================="
echo ""
echo "🎉 JAR 파일 교체 및 서비스 재시작 완료!"
echo ""
echo "📋 확인 사항:"
echo "  • API 서버: http://103.244.108.70:8080"
echo "  • Health Check: http://103.244.108.70:8080/actuator/health"
echo "  • 테스트 페이지: http://103.244.108.70:8080"
echo ""

# Health Check 수행
echo "Health Check 수행 중..."
sleep 10
for i in {1..15}; do
    if curl -s http://103.244.108.70:8080/actuator/health > /dev/null; then
        echo "✅ 서버가 정상적으로 실행 중입니다!"
        curl -s http://103.244.108.70:8080/actuator/health | jq '.' 2>/dev/null || echo "Health check 성공"
        break
    else
        echo "Health check 시도 $i/15..."
        sleep 3
    fi
    
    if [ $i -eq 15 ]; then
        echo "⚠️ Health check 응답이 늦습니다. 수동으로 확인해주세요."
        echo "URL: http://103.244.108.70:8080/actuator/health"
    fi
done