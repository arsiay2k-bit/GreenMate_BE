// 앱 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

// 앱 초기화 함수
function initializeApp() {
    // 저장된 토큰이 있으면 사용자 정보 확인
    if (authToken) {
        getCurrentUser();
    } else {
        updateAuthStatus(false);
    }

    // 기본값 설정
    setDefaultValues();
}

// 기본값 설정
function setDefaultValues() {
    // 위치 검색 기본값
    document.getElementById('location-query').value = '강남';
    
    // 경로 검색 기본값 (서울시청 -> 명동)
    document.getElementById('start-lat').value = '37.5663';
    document.getElementById('start-lng').value = '126.9997';
    document.getElementById('start-address').value = '서울시청';
    document.getElementById('end-lat').value = '37.5636';
    document.getElementById('end-lng').value = '126.9866';
    document.getElementById('end-address').value = '명동';
    
    // 걸음 기록 기본값
    document.getElementById('record-start-lat').value = '37.5663';
    document.getElementById('record-start-lng').value = '126.9997';
    document.getElementById('record-start-address').value = '서울시청';
    document.getElementById('record-end-lat').value = '37.5636';
    document.getElementById('record-end-lng').value = '126.9866';
    document.getElementById('record-end-address').value = '명동';
    document.getElementById('record-distance').value = '1.43';
    document.getElementById('record-duration').value = '28';
    document.getElementById('record-steps').value = '1789';
}

// 인증 상태 업데이트
function updateAuthStatus(isLoggedIn, userInfo = null) {
    const statusElement = document.getElementById('login-status');
    const userInfoElement = document.getElementById('user-info');
    const signupForm = document.getElementById('signup-form');
    const loginForm = document.getElementById('login-form');
    const authenticatedActions = document.getElementById('authenticated-actions');
    
    if (isLoggedIn && userInfo) {
        statusElement.textContent = '로그인됨';
        statusElement.className = 'logged-in';
        
        userInfoElement.innerHTML = `
            <strong>사용자 정보:</strong><br>
            닉네임: ${userInfo.nickname}<br>
            이메일: ${userInfo.email}<br>
            성별: ${getGenderText(userInfo.gender)}<br>
            나이: ${userInfo.age}세
        `;
        userInfoElement.style.display = 'block';
        
        // 폼 숨기고 인증된 사용자 버튼들 표시
        signupForm.style.display = 'none';
        loginForm.style.display = 'none';
        authenticatedActions.style.display = 'block';
        
    } else if (isLoggedIn) {
        statusElement.textContent = '로그인됨 (정보 로딩 중...)';
        statusElement.className = 'logged-in';
        userInfoElement.style.display = 'none';
        
    } else {
        statusElement.textContent = '로그아웃됨';
        statusElement.className = '';
        userInfoElement.style.display = 'none';
        
        // 폼 표시하고 인증된 사용자 버튼들 숨기기
        signupForm.style.display = 'block';
        loginForm.style.display = 'block';
        authenticatedActions.style.display = 'none';
    }
}

// 성별 텍스트 변환
function getGenderText(gender) {
    switch(gender) {
        case 'MALE': return '남성';
        case 'FEMALE': return '여성';
        case 'OTHER': return '기타';
        default: return gender;
    }
}

// 결과 표시 함수
function displayResult(section, data, type = 'success') {
    const resultMappings = {
        'auth': 'auth-section',
        'location': 'location-results',
        'route': 'route-results', 
        'walk': 'walk-results',
        'system': 'system-results'
    };
    
    let resultElementId;
    if (section === 'auth') {
        // 인증 섹션의 경우 별도 처리
        resultElementId = null;
        if (type === 'error') {
            showNotification(data, 'error');
        } else if (type === 'success') {
            showNotification(data, 'success');
        }
        return;
    } else {
        resultElementId = resultMappings[section];
    }
    
    const resultElement = document.getElementById(resultElementId);
    if (!resultElement) return;
    
    // 데이터 포맷팅
    let formattedData;
    if (typeof data === 'string') {
        formattedData = data;
    } else {
        formattedData = JSON.stringify(data, null, 2);
    }
    
    resultElement.textContent = formattedData;
    
    // 스타일 적용
    resultElement.className = '';
    if (type === 'error') {
        resultElement.classList.add('error');
    } else if (type === 'success') {
        resultElement.classList.add('success');
    }
    
    // 스크롤 이동
    resultElement.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

// 알림 표시
function showNotification(message, type = 'info') {
    // 기존 알림 제거
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }
    
    // 새 알림 생성
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        color: white;
        z-index: 1000;
        max-width: 400px;
        word-wrap: break-word;
        font-weight: 500;
        box-shadow: 0 4px 12px rgba(0,0,0,0.3);
    `;
    
    // 타입별 색상 설정
    const colors = {
        success: '#27ae60',
        error: '#e74c3c',
        info: '#3498db',
        warning: '#f39c12'
    };
    
    notification.style.backgroundColor = colors[type] || colors.info;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    // 자동 제거
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 5000);
}

// 모든 결과 지우기
function clearResults() {
    const resultIds = ['location-results', 'route-results', 'walk-results', 'system-results'];
    
    resultIds.forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = '';
            element.className = '';
        }
    });
    
    showNotification('모든 결과가 지워졌습니다.', 'info');
}

// 유틸리티 함수들
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {
        showNotification('클립보드에 복사되었습니다.', 'success');
    }).catch(err => {
        showNotification('복사 실패: ' + err.message, 'error');
    });
}

// 에러 핸들링
window.addEventListener('error', function(event) {
    console.error('JavaScript 오류:', event.error);
    showNotification('JavaScript 오류가 발생했습니다. 콘솔을 확인해주세요.', 'error');
});

// 네트워크 상태 모니터링
window.addEventListener('online', function() {
    showNotification('네트워크가 연결되었습니다.', 'success');
});

window.addEventListener('offline', function() {
    showNotification('네트워크 연결이 끊어졌습니다.', 'warning');
});