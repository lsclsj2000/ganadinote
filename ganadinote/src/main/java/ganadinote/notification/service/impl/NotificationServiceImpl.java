package ganadinote.notification.service.impl;

import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ganadinote.common.domain.PushSubscription;
import ganadinote.notification.domain.PushSubDTO;
import ganadinote.notification.mapper.PushMapper;
import ganadinote.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription; 


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final PushMapper pushMapper;

    @Value("${vapid.public.key}")
    private String vapidPublicKey;

    @Value("${vapid.private.key}")
    private String vapidPrivateKey;

    static {
        // 일부 JVM 환경에서는 꼭 필요함 (암호화 지원)
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    @Transactional
    public void addSubscription(Integer mbrCd, PushSubDTO dto) {
        PushSubscription subscription = PushSubscription.builder()
                .mbrCd(mbrCd)
                .endpoint(dto.getEndpoint())
                .p256dh(dto.getP256dh())
                .auth(dto.getAuth())
                .build();

        pushMapper.addSubscription(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PushSubscription> getSubInfoByMbrCd(Integer mbrCd) {
        return pushMapper.getSubInfoByMbrCd(mbrCd);
    }
    
    @Override
    public void sendNotification(Integer mbrCd, String message) throws Exception {
        // 1. VAPID 키를 사용하여 PushService 객체를 생성합니다. (이 방식이 가장 안정적입니다.)
        PushService pushService = new PushService(vapidPublicKey, vapidPrivateKey, "mailto:admin@yourdomain.com");

        // 2. DB에서 사용자의 구독 정보를 가져옵니다.
        List<PushSubscription> subscriptions = getSubInfoByMbrCd(mbrCd);

        if (subscriptions == null || subscriptions.isEmpty()) {
            System.out.println("No subscriptions found for member code: " + mbrCd);
            return;
        }

        for (PushSubscription sub : subscriptions) {
            try {
                if (sub.getEndpoint() == null || sub.getP256dh() == null || sub.getAuth() == null) {
                    System.err.println("Skipping invalid subscription data: " + sub);
                    continue;
                }

                // =================================================================
                // 3. Subscription 객체를 생성하는 새로운 방식 (가장 중요)
                // =================================================================
                
                // 3-1. DB에서 가져온 키 문자열로 'Keys' 객체를 먼저 만듭니다.
                Subscription.Keys keys = new Subscription.Keys(sub.getP256dh(), sub.getAuth());

                // 3-2. endpoint와 위에서 만든 'keys' 객체를 생성자에 전달합니다.
                Subscription subscription = new Subscription(sub.getEndpoint(), keys);

                // =================================================================


                // 4. 전송할 페이로드(JSON 형식의 문자열)를 만듭니다.
                String payload = "{\"notification\":{\"title\":\"새 알림이 도착했어요!\",\"body\":\"" + message + "\"}}";
                
                // 5. 알림 객체를 생성하고 전송합니다.
                Notification notification = new Notification(subscription, payload);
                pushService.send(notification);
                
                System.out.println("Notification sent successfully to endpoint: " + sub.getEndpoint());

            } catch (Exception e) {
                System.err.println("Failed to send notification to endpoint: " + sub.getEndpoint());
                e.printStackTrace();
            }
        }
    }
}