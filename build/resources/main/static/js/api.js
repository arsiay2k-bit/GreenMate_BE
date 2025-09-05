// API 기본 설정
const API_BASE = window.location.origin;
let authToken = localStorage.getItem('authToken');

// API 요청 헬퍼 함수
async function apiRequest(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        }
    };

    // 인증이 필요한 요청에 토큰 추가
    if (authToken && !url.includes('/oauth2/') && !url.includes('/actuator/')) {
        defaultOptions.headers['Authorization'] = `Bearer ${authToken}`;
    }

    const finalOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    };

    try {
        const response = await fetch(`${API_BASE}${url}`, finalOptions);
        
        // 인증 오류 처리
        if (response.status === 401) {
            authToken = null;
            localStorage.removeItem('authToken');
            updateAuthStatus(false);
            throw new Error('인증이 필요합니다. 다시 로그인해주세요.');
        }

        let data;
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${JSON.stringify(data)}`);
        }

        return data;
    } catch (error) {
        console.error('API 요청 오류:', error);
        throw error;
    }
}

// 회원가입 API
async function signup() {
    const email = document.getElementById('signup-email').value.trim();
    const password = document.getElementById('signup-password').value;
    const confirmPassword = document.getElementById('signup-confirm-password').value;
    const nickname = document.getElementById('signup-nickname').value.trim();
    const gender = document.getElementById('signup-gender').value;
    const age = parseInt(document.getElementById('signup-age').value);
    const height = document.getElementById('signup-height').value ? parseFloat(document.getElementById('signup-height').value) : null;
    const weight = document.getElementById('signup-weight').value ? parseFloat(document.getElementById('signup-weight').value) : null;

    // 클라이언트 사이드 검증
    if (!email || !password || !confirmPassword || !nickname || !gender || !age) {
        displayResult('auth', '모든 필드를 입력해주세요.', 'error');
        return;
    }

    if (password !== confirmPassword) {
        displayResult('auth', '패스워드가 일치하지 않습니다.', 'error');
        return;
    }

    if (password.length < 8) {
        displayResult('auth', '패스워드는 최소 8자 이상이어야 합니다.', 'error');
        return;
    }

    if (age < 14 || age > 120) {
        displayResult('auth', '올바른 나이를 입력해주세요 (만 14세 이상).', 'error');
        return;
    }

    // 키와 몸무게 범위 검증 (입력된 경우에만)
    if (height && (height < 50 || height > 250)) {
        displayResult('auth', '키는 50-250cm 사이로 입력해주세요.', 'error');
        return;
    }

    if (weight && (weight < 10 || weight > 500)) {
        displayResult('auth', '몸무게는 10-500kg 사이로 입력해주세요.', 'error');
        return;
    }

    const signupData = {
        email: email,
        password: password,
        confirmPassword: confirmPassword,
        nickname: nickname,
        gender: gender,
        age: age,
        height: height,
        weight: weight
    };

    try {
        const response = await apiRequest('/auth/signup', {
            method: 'POST',
            body: JSON.stringify(signupData)
        });

        if (response.accessToken) {
            authToken = response.accessToken;
            localStorage.setItem('authToken', authToken);
            displayResult('auth', '회원가입이 완료되었습니다!', 'success');
            updateAuthStatus(true, response);
            clearSignupForm();
        } else {
            displayResult('auth', response, 'success');
        }
    } catch (error) {
        let errorMessage = error.message;
        // 서버에서 온 구조화된 에러 응답 파싱
        try {
            const errorData = JSON.parse(error.message.replace(/^HTTP \d+: /, ''));
            if (errorData.error && errorData.details) {
                errorMessage = errorData.error + '\n\n상세 정보:\n';
                Object.entries(errorData.details).forEach(([field, message]) => {
                    errorMessage += `• ${field}: ${message}\n`;
                });
            }
        } catch (parseError) {
            // JSON 파싱 실패 시 원본 메시지 사용
        }
        displayResult('auth', errorMessage, 'error');
    }
}

// 로그인 API
async function login() {
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;

    if (!email || !password) {
        displayResult('auth', '이메일과 패스워드를 입력해주세요.', 'error');
        return;
    }

    const loginData = {
        email: email,
        password: password
    };

    try {
        const response = await apiRequest('/auth/login', {
            method: 'POST',
            body: JSON.stringify(loginData)
        });

        if (response.accessToken) {
            authToken = response.accessToken;
            localStorage.setItem('authToken', authToken);
            displayResult('auth', '로그인되었습니다!', 'success');
            updateAuthStatus(true, response);
            clearLoginForm();
        } else {
            displayResult('auth', response, 'success');
        }
    } catch (error) {
        let errorMessage = error.message;
        // 서버에서 온 구조화된 에러 응답 파싱
        try {
            const errorData = JSON.parse(error.message.replace(/^HTTP \d+: /, ''));
            if (errorData.error && errorData.details) {
                errorMessage = errorData.error + '\n\n상세 정보:\n';
                Object.entries(errorData.details).forEach(([field, message]) => {
                    errorMessage += `• ${field}: ${message}\n`;
                });
            } else if (errorData.error) {
                errorMessage = errorData.error;
            }
        } catch (parseError) {
            // JSON 파싱 실패 시 원본 메시지 사용
        }
        displayResult('auth', errorMessage, 'error');
    }
}

async function getCurrentUser() {
    if (!authToken) {
        displayResult('auth', '로그인이 필요합니다.', 'error');
        return;
    }

    try {
        const user = await apiRequest('/auth/me');
        displayResult('auth', user);
        updateAuthStatus(true, user);
        return user;
    } catch (error) {
        displayResult('auth', error.message, 'error');
    }
}

async function refreshToken() {
    if (!authToken) {
        displayResult('auth', '토큰이 없습니다.', 'error');
        return;
    }

    try {
        const response = await apiRequest('/auth/refresh', {
            method: 'POST'
        });
        
        if (response.accessToken) {
            authToken = response.accessToken;
            localStorage.setItem('authToken', authToken);
            displayResult('auth', '토큰이 갱신되었습니다.', 'success');
        }
    } catch (error) {
        displayResult('auth', error.message, 'error');
    }
}

async function logout() {
    try {
        await apiRequest('/auth/logout', {
            method: 'POST'
        });
        
        authToken = null;
        localStorage.removeItem('authToken');
        updateAuthStatus(false);
        displayResult('auth', '로그아웃되었습니다.', 'success');
    } catch (error) {
        displayResult('auth', error.message, 'error');
    }
}

// 위치 검색 API
async function searchLocations() {
    const query = document.getElementById('location-query').value;
    if (!query.trim()) {
        displayResult('location', '검색어를 입력해주세요.', 'error');
        return;
    }

    try {
        const locations = await apiRequest(`/api/locations/search?query=${encodeURIComponent(query)}`);
        displayResult('location', locations);
    } catch (error) {
        displayResult('location', error.message, 'error');
    }
}

async function getAutocomplete() {
    const query = document.getElementById('location-query').value;
    if (!query.trim()) {
        displayResult('location', '검색어를 입력해주세요.', 'error');
        return;
    }

    try {
        const suggestions = await apiRequest(`/api/locations/autocomplete?query=${encodeURIComponent(query)}&limit=5`);
        displayResult('location', suggestions);
    } catch (error) {
        displayResult('location', error.message, 'error');
    }
}

// 경로 검색 API
async function searchRoutes() {
    const startLat = parseFloat(document.getElementById('start-lat').value);
    const startLng = parseFloat(document.getElementById('start-lng').value);
    const startAddress = document.getElementById('start-address').value;
    const endLat = parseFloat(document.getElementById('end-lat').value);
    const endLng = parseFloat(document.getElementById('end-lng').value);
    const endAddress = document.getElementById('end-address').value;

    if (!startLat || !startLng || !endLat || !endLng) {
        displayResult('route', '위도와 경도를 모두 입력해주세요.', 'error');
        return;
    }

    const routeData = {
        startLatitude: startLat,
        startLongitude: startLng,
        startAddress: startAddress,
        endLatitude: endLat,
        endLongitude: endLng,
        endAddress: endAddress
    };

    try {
        const routes = await apiRequest('/api/routes/search', {
            method: 'POST',
            body: JSON.stringify(routeData)
        });
        displayResult('route', routes);
    } catch (error) {
        displayResult('route', error.message, 'error');
    }
}

// 걸음 기록 API
async function saveWalkRecord() {
    if (!authToken) {
        displayResult('walk', '로그인이 필요합니다.', 'error');
        return;
    }

    const recordData = {
        startLatitude: parseFloat(document.getElementById('record-start-lat').value),
        startLongitude: parseFloat(document.getElementById('record-start-lng').value),
        startAddress: document.getElementById('record-start-address').value,
        endLatitude: parseFloat(document.getElementById('record-end-lat').value),
        endLongitude: parseFloat(document.getElementById('record-end-lng').value),
        endAddress: document.getElementById('record-end-address').value,
        distanceKm: parseFloat(document.getElementById('record-distance').value),
        durationMinutes: parseInt(document.getElementById('record-duration').value),
        steps: parseInt(document.getElementById('record-steps').value),
        routeType: document.getElementById('record-route-type').value
    };

    // 필수 필드 검증
    const requiredFields = ['startLatitude', 'startLongitude', 'endLatitude', 'endLongitude', 'distanceKm', 'durationMinutes', 'steps'];
    for (let field of requiredFields) {
        if (!recordData[field] && recordData[field] !== 0) {
            displayResult('walk', `${field} 필드를 입력해주세요.`, 'error');
            return;
        }
    }

    try {
        const result = await apiRequest('/api/walk-records', {
            method: 'POST',
            body: JSON.stringify(recordData)
        });
        displayResult('walk', result, 'success');
    } catch (error) {
        displayResult('walk', error.message, 'error');
    }
}

async function getUserWalkRecords() {
    if (!authToken) {
        displayResult('walk', '로그인이 필요합니다.', 'error');
        return;
    }

    try {
        const records = await apiRequest('/api/users/walk-records');
        displayResult('walk', records);
    } catch (error) {
        displayResult('walk', error.message, 'error');
    }
}

async function getUserStatistics() {
    if (!authToken) {
        displayResult('walk', '로그인이 필요합니다.', 'error');
        return;
    }

    try {
        const stats = await apiRequest('/api/users/walk-statistics');
        displayResult('walk', stats);
    } catch (error) {
        displayResult('walk', error.message, 'error');
    }
}

async function getRecentRecords() {
    if (!authToken) {
        displayResult('walk', '로그인이 필요합니다.', 'error');
        return;
    }

    try {
        const records = await apiRequest('/api/users/walk-records/recent?days=7');
        displayResult('walk', records);
    } catch (error) {
        displayResult('walk', error.message, 'error');
    }
}

// 걸음 수 추천 API
async function getStepRecommendations() {
    if (!authToken) {
        displayResult('recommendations', '로그인이 필요합니다.', 'error');
        return;
    }

    try {
        const recommendations = await apiRequest('/api/users/recommendations');
        displayResult('recommendations', recommendations);
    } catch (error) {
        displayResult('recommendations', error.message, 'error');
    }
}

async function refreshStepRecommendations() {
    if (!authToken) {
        displayResult('recommendations', '로그인이 필요합니다.', 'error');
        return;
    }

    try {
        const response = await apiRequest('/api/users/refresh-recommendations', {
            method: 'POST'
        });
        displayResult('recommendations', response, 'success');
    } catch (error) {
        displayResult('recommendations', error.message, 'error');
    }
}

// 시스템 정보 API
async function getHealth() {
    try {
        const health = await apiRequest('/actuator/health');
        displayResult('system', health);
    } catch (error) {
        displayResult('system', error.message, 'error');
    }
}

// 폼 정리 함수들
function clearSignupForm() {
    document.getElementById('signup-email').value = '';
    document.getElementById('signup-password').value = '';
    document.getElementById('signup-confirm-password').value = '';
    document.getElementById('signup-nickname').value = '';
    document.getElementById('signup-gender').value = '';
    document.getElementById('signup-age').value = '';
    document.getElementById('signup-height').value = '';
    document.getElementById('signup-weight').value = '';
}

function clearLoginForm() {
    document.getElementById('login-email').value = '';
    document.getElementById('login-password').value = '';
}