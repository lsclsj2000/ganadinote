self.addEventListener('push', function(event) {
    let title = '알림';
    let options = {
        body: '새로운 알림이 도착했습니다.'
    };
    
    if (event.data) {
        try {
            // 푸시 데이터를 JSON으로 파싱 시도
            const payload = event.data.json();
            
            // 파싱된 데이터에서 알림 제목과 내용을 안전하게 추출
            title = payload.notification.title || title;
            options.body = payload.notification.body || options.body;
            options.icon = payload.notification.icon || '/images/icon.png';
            
        } catch (e) {
            console.error('[Service Worker] 푸시 데이터 파싱 오류:', e);
            // JSON 파싱에 실패하면 텍스트로 대체
            options.body = event.data.text();
        }
    }

    // 알림 표시
    event.waitUntil(
        self.registration.showNotification(title, options)
    );
});