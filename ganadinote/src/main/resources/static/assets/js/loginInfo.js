/**
 * JWT 토큰을 로컬 스토리지에서 가져오는 함수
 * @returns {string | null} 토큰 문자열
 */
function getToken() {
    return localStorage.getItem('authToken');
}

// 오리지널 fetch 함수를 변수에 저장해 둡니다.
const originalFetch = window.fetch;

// 모든 fetch 요청에 토큰을 자동으로 추가하도록 함수를 재정의합니다.
window.fetch = function(url, options = {}) {
    const token = getToken();

    // 토큰이 있다면, 요청 헤더에 Authorization을 추가합니다.
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        };
    }

    // 재정의된 fetch 함수로 요청을 보냅니다.
    return originalFetch(url, options);
};

// 필요한 경우, 토큰에서 회원 코드를 직접 추출하는 함수
function getMbrCdFromToken() {
    const token = getToken();
    if (!token) return null;

    try {
        const payloadBase64 = token.split('.')[1];
        const decodedPayload = atob(payloadBase64);
        const payload = JSON.parse(decodedPayload);
        
        return payload.sub;
    } catch (e) {
        console.error("토큰 디코딩 중 오류가 발생했습니다:", e);
        return null;
    }
}

// 페이지 로드 시 로그인 상태를 확인하고 콘솔에 출력하는 로직
document.addEventListener('DOMContentLoaded', () => {
    const mbrCd = getMbrCdFromToken();

    if (mbrCd) {
        console.log('✅ 로그인된 회원의 mbr_cd:', mbrCd);
    }
});