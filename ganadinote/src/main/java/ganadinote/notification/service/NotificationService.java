package ganadinote.notification.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import ganadinote.common.domain.PushSubscription;
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
    
    // 알림 발송
    void sendNotification(Integer mbrCd, String message);
    
    // mbrCd를 통해 pet의 정보를 가져옴
    List<PetWithBreedDTO> getPetInfoForNotification(String mbrCd);
    
    // 구독 시간 설정
    void updateNotificationSchedule(Integer mbrCd, String notificationScheduleJson);
    
    // 회원의 알림 스케줄(JSON 형태)을 조회
    String getNotificationSchedule(Integer mbrCd);
    
    // 산책에 대한 알림 조건
    public void processWalkAlert(Integer mbrCd);
    
    public void processWalkAlertsForScheduledUsers();
    
	/*
	 * // 회원 코드로 알림 스케줄(JSON) 데이터를 조회합니다. String
	 * getNotificationScheduleByMbrCd(@Param("mbrCd") Integer mbrCd);
	 */
}
