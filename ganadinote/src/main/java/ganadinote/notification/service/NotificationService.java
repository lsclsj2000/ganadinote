package ganadinote.notification.service;

import java.util.List;

import ganadinote.notification.domain.PetWithBreedDTO;
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
    
    // mbrCd를 통해 pet의 정보를 가져옴
    List<PetWithBreedDTO> getPetInfoForNotification(String mbrCd);
    
    // 구독 시간 설정
    void updateNotificationSchedule(Integer mbrCd, String notificationScheduleJson);
    
    // 회원의 알림 스케줄(JSON 형태)을 조회
    String getNotificationSchedule(Integer mbrCd);
}
