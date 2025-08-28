self.addEventListener('push', function(event) {
    const data = event.data.json();
    console.log('[Service Worker] Push Received.');

	// 알림 제목과 본문은 백엔드에서 보낸 데이터(data.notification)에서 가져옵니다.
    const title = data.notification.title;
    const options = {
        body: data.notification.body,
        icon: '/images/icon.png' // 알림에 표시할 아이콘 이미지 경로 (선택 사항)
    };
	// 브라우저에 알림을 표시합니다.
    event.waitUntil(self.registration.showNotification(title, options));
});