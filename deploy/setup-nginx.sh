#!/bin/bash

# GreenMate Backend nginx 리버스 프록시 설정 스크립트

set -e

DEPLOY_DIR="/greenmate"
NGINX_CONF="/etc/nginx/sites-available/greenmate"
NGINX_ENABLED="/etc/nginx/sites-enabled/greenmate"

echo "=== Nginx 리버스 프록시 설정 시작 ==="

# 1. nginx 설정 파일 복사
echo "1. nginx 설정 파일 복사..."
cp $DEPLOY_DIR/GreenMate_BE/deploy/nginx-greenmate.conf $NGINX_CONF

# 2. 기존 default 사이트 비활성화
echo "2. 기존 default 사이트 비활성화..."
if [ -L "/etc/nginx/sites-enabled/default" ]; then
    rm /etc/nginx/sites-enabled/default
fi

# 3. GreenMate 사이트 활성화
echo "3. GreenMate 사이트 활성화..."
if [ ! -L $NGINX_ENABLED ]; then
    ln -s $NGINX_CONF $NGINX_ENABLED
fi

# 4. nginx 설정 테스트
echo "4. nginx 설정 테스트..."
nginx -t

# 5. nginx 재시작
echo "5. nginx 재시작..."
systemctl restart nginx
systemctl enable nginx

# 6. nginx 상태 확인
echo "6. nginx 상태 확인..."
systemctl status nginx --no-pager -l

echo "=== Nginx 설정 완료 ==="
echo "웹사이트 URL: http://103.244.108.70"
echo "API URL: http://103.244.108.70/api/"
echo "Health Check: http://103.244.108.70/actuator/health"