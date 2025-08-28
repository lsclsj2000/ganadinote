package ganadinote.notification.service;

import ganadinote.notification.domain.PushSubDTO;

public interface NotificationService {

	// 새로운 구독 정보 저장 또는 기존 정보 업데이트
    void saveOrUpdateSubscription(int mbrCd, PushSubDTO dto);

    // 알림 구독 활성화 상태 확인
    Boolean isSubscriptionActive(int mbrCd);
    
    // 알림 비활성화
    void deactivateSubscription(int mbrCd);
    
    // 알림 재활성화
    void reactivateSubscription(int mbrCd);
    
    void sendNotification(Integer mbrCd, String message);
}
