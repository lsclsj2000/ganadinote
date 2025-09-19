/**
 * JWT 토큰을 로컬 스토리지에 저장하는 함수
 * @param {string} token - 저장할 토큰 문자열
 */
function saveToken(token) {
	if (token) {
		localStorage.setItem('authToken', token);
	}
}

/**
 * JWT 토큰을 로컬 스토리지에서 가져오는 함수
 * @returns {string | null} 토큰 문자열
 */
function getToken() {
	return localStorage.getItem('authToken');
}

/**
 * JWT 토큰을 로컬 스토리지에서 삭제하는 함수
 */
function removeToken() {
	localStorage.removeItem('authToken');
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
    if (!token) {
        return null;
    }

    try {
        const payloadBase64 = token.split('.')[1];
        const decodedPayload = atob(payloadBase64);
        const payload = JSON.parse(decodedPayload);
        
        // ⭐ 추가된 로직: 토큰의 만료 시간을 확인합니다.
        const now = Date.now();
        const expirationTimeInMs = payload.exp * 1000;

        if (now >= expirationTimeInMs) {
            console.error("토큰이 만료되었습니다. 로그아웃 처리합니다.");
            removeToken();
            return null;
        }
        
        return payload.sub; //
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


// 로그아웃 시 데모 이메일/비밀번호 불러오기
async function doLogout() {
  try {
    await fetch('/api/logout', { method: 'POST' }); // 서버 쿠키 삭제
  } finally {
    // 클라이언트 저장값 정리
    localStorage.removeItem('authToken');
    localStorage.removeItem('lastLoginEmail');

    // 데모 계정 자동 프리필 모드로 로그인 페이지 이동
    window.location.href = '/login?prefill=demo';
  }
}