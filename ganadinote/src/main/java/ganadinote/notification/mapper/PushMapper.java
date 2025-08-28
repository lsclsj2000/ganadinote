package ganadinote.notification.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import ganadinote.common.domain.PushSubscription;
import ganadinote.notification.domain.PushSubDTO;

@Mapper
public interface PushMapper {
	
	// 구독정보를 DB에 추가: PushSubDTO를 사용하도록 매개변수 타입 변경
    void addSubscription(PushSubDTO dto);
    
    // 회원 코드로 구독 정보 가져오기
    List<PushSubscription> getSubInfoByMbrCd(@Param("mbrCd") Integer mbrCd);
    
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
    List<PushSubscription> findSubscriptionsBySchedule(@Param("dayOfWeek") String dayOfWeek, @Param("currentTime") String currentTime);

}
