#!/bin/bash

# GreenMate Backend 자동 배포 스크립트 (로컬에서 실행)
# 서버: 103.244.108.70 (root / hlihli1!)

SERVER="103.244.108.70"
USER="root"
PASSWORD="hlihli1!"

echo "=========================================="
echo "    GreenMate Backend 자동 배포 시작"
echo "    서버: $SERVER"
echo "=========================================="

# Expect 스크립트를 사용해서 자동 배포
expect -c "
set timeout 300
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
        # 배포 디렉터리 생성
        send \"mkdir -p /greenmate && cd /greenmate\r\"
        expect \"# \"
        
        # Git 저장소 클론 (기존 것이 있으면 삭제)
        send \"if [ -d 'GreenMate_BE' ]; then rm -rf GreenMate_BE; fi\r\"
        expect \"# \"
        send \"git clone https://github.com/mhsssshin/GreenMate_BE.git\r\"
        expect \"# \"
        
        # 스크립트 실행 권한 부여
        send \"chmod +x /greenmate/GreenMate_BE/deploy/*.sh\r\"
        expect \"# \"
        
        # 완전 자동 설치 실행
        send \"bash /greenmate/GreenMate_BE/deploy/install.sh\r\"
        expect \"# \"
        
        send \"exit\r\"
        expect eof
    }
}
"

echo "=========================================="
echo "           자동 배포 완료!"
echo "=========================================="
echo ""
echo "🎉 배포가 완료되었습니다!"
echo ""
echo "📋 확인 사항:"
echo "  • 웹사이트: http://103.244.108.70"
echo "  • API: http://103.244.108.70/api/"
echo "  • Health Check: http://103.244.108.70/actuator/health"
echo ""