self.addEventListener('push', function(event) {
    const data = event.data.json();
    console.log('[Service Worker] Push Received.');

    const title = data.notification.title;
    const options = {
        body: data.notification.body,
        icon: '/images/icon.png' // 알림에 표시할 아이콘 이미지 경로 (선택 사항)
    };

    event.waitUntil(self.registration.showNotification(title, options));
});