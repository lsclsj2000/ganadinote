package ganadinote.notification.service.impl;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
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
import nl.martijndwars.webpush.Utils;


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
    	// TODO Auto-generated method stub
    	
    }
   
}
