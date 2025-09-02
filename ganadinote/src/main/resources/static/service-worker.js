self.addEventListener('push', function(event) {
    let title = '알림';
    let options = {
        body: '새로운 알림이 도착했습니다.'
    };
    
    if (event.data) {
        try {
            const payload = event.data.json();
            
            // 'notification' 객체가 있는 경우와 없는 경우를 모두 처리
            if (payload.notification) { 
                title = payload.notification.title || title;
                options.body = payload.notification.body || options.body;
                options.icon = payload.notification.icon || '/images/icon.png';
            } else {
                // 'notification' 키가 없으면 페이로드 자체에서 title, body를 가져옴
                title = payload.title || title;
                options.body = payload.body || options.body;
                options.icon = payload.icon || '/images/icon.png';
            }
            
        } catch (e) {
            console.error('[Service Worker] 푸시 데이터 파싱 오류:', e);
            // JSON 파싱에 실패하면 텍스트로 대체
            options.body = event.data.text();
        }
    }

    event.waitUntil(
        self.registration.showNotification(title, options)
    );
});