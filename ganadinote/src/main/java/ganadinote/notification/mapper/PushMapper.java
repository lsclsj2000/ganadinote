package ganadinote.notification.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import ganadinote.common.domain.NotificationHistory;
import ganadinote.notification.domain.LocationUpdateDTO;
import ganadinote.notification.domain.PetWithBreedDTO;
import ganadinote.notification.domain.PushSubDTO;

@Mapper
public interface PushMapper {
	
	// 구독정보를 DB에 추가: PushSubDTO를 사용하도록 매개변수 타입 변경
    void addSubscription(PushSubDTO dto);
    
    // 회원 코드로 구독 정보 가져오기
    List<PushSubDTO> getSubInfoByMbrCd(@Param("mbrCd") Integer mbrCd);
    
    // 활성화된 구독 정보가 있는지 확인
    Boolean isSubscriptionActive(@Param("mbrCd") int mbrCd);

    // endpoint를 기준으로 기존 구독 정보가 있는지 확인
    int getSubscriptionByEndpoint(@Param("endpoint") String endpoint);

    // 구독 정보 업데이트 (활성화)
    void updateSubscription(PushSubDTO dto);
    
    // 구독 비활성화 (is_active=0)
    void deactivateSubscription(@Param("mbrCd") int mbrCd);
    
    // 구독 재활성화 (is_active=1)
    void reactivateSubscription(@Param("mbrCd") int mbrCd);
    
    // 스케줄에 맞는 구독 정보 찾기
    List<PushSubDTO> findSubscriptionsBySchedule(@Param("dayOfWeek") String dayOfWeek, @Param("time") String time);
    
    // mbrCd를 통해 pet 알림 정보 가져오기
    List<PetWithBreedDTO> getPetInfoForNotification(@Param("mbrCd") String mbrCd);
    
    // 구독 시간 설정
    void updateNotificationSchedule(Integer mbrCd, String notificationScheduleJson);
    
    // 회원의 알림 스케줄(JSON 형태)을 조회
    String getNotificationSchedule(@Param("mbrCd") Integer mbrCd);

    // 위치 정보가 포함된 모든 활성화된 구독 정보 가져오기
    List<PushSubDTO> findSubscriptionsWithLocation();
    
    // 회원의 위치 정보(위도, 경도)를 업데이트합니다.
    void updateLocation(LocationUpdateDTO locationUpdateDto);
   
    // 회원 코드를 기반으로 활성화된 모든 구독 정보를 찾아 리스트로 반환
    List<PushSubDTO> getActiveSubscriptionsByMbrCd(@Param("mbrCd") Integer mbrCd);
    
    // 회원 코드로 알림 스케줄(JSON) 데이터를 조회합니다.
    String getNotificationScheduleByMbrCd(@Param("mbrCd") Integer mbrCd);
    
    // 알림 기록 저장 메서드
    void saveNotificationHistory(NotificationHistory notificationHistory);
    
    // 알림 기록 조회 메서드
    List<NotificationHistory> getNotificationHistory(int mbrCd);
}
