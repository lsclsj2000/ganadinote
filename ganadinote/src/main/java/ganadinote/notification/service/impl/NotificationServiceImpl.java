package ganadinote.notification.service.impl;

import ganadinote.notification.domain.PetWithBreedDTO;
import ganadinote.notification.domain.PushSubDTO;
import ganadinote.notification.mapper.PushMapper;
import ganadinote.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationServiceImpl implements NotificationService {

    private final PushMapper pushMapper;

    /**
     * 알림 구독 정보를 저장하거나 업데이트합니다.
     * 기존 구독 정보가 있으면 is_active를 1로 업데이트하고, 없으면 새로 삽입합니다.
     */
    @Override
    public void saveOrUpdateSubscription(int mbrCd, PushSubDTO dto) {
        dto.setMbrCd(mbrCd); // DTO에 회원 코드 설정
        int existingSubscriptionCount = pushMapper.getSubscriptionByEndpoint(dto.getEndpoint());

        if (existingSubscriptionCount > 0) {
            log.info("기존 구독 정보가 발견되어 업데이트합니다.");
            pushMapper.updateSubscription(dto);
        } else {
            log.info("새로운 구독 정보입니다. 삽입합니다.");
            pushMapper.addSubscription(dto);
        }
    }

    /**
     * 현재 사용자의 활성화된 알림 구독 상태를 확인합니다.
     */
    @Override
    public Boolean isSubscriptionActive(int mbrCd) {
        Boolean isActive = pushMapper.isSubscriptionActive(mbrCd);
        return isActive != null ? isActive : false;
    }
    
    /**
     * 알림 구독을 비활성화합니다. DB의 is_active 컬럼을 0으로 설정합니다.
     */
    @Override
    public void deactivateSubscription(int mbrCd) {
        pushMapper.deactivateSubscription(mbrCd);
        log.info("회원 코드 {}의 알림이 비활성화되었습니다.", mbrCd);
    }
    
    /**
     * 알림 구독을 다시 활성화합니다. DB의 is_active 컬럼을 1로 설정합니다.
     */
    @Override
    public void reactivateSubscription(int mbrCd) {
        pushMapper.reactivateSubscription(mbrCd);
        log.info("회원 코드 {}의 알림이 다시 활성화되었습니다.", mbrCd);
    }

    /**
     * 푸시 알림을 실제로 전송하는 메소드입니다.
     */
    @Override
    public void sendNotification(Integer mbrCd, String message) {
        // 이 부분에 실제 푸시 알림 전송 로직이 구현되어야 합니다.
        log.info("회원 {}에게 푸시 알림 전송: {}", mbrCd, message);
    }
    
    @Override
    public List<PetWithBreedDTO> getPetInfoForNotification(String mbrCd) {
    	List<PetWithBreedDTO> pets = pushMapper.getPetInfoForNotification(mbrCd);
    	 log.info("반환 전 pets 리스트 크기: {}", pets.size());
         if (!pets.isEmpty()) {
             pets.forEach(pet -> {
                 if (pet != null) {
                     log.info("Pet 정보: petName={}, petWeight={}, petBreed={}", 
                              pet.getPetName(), pet.getPetWeight(), pet.getPetBreed());
                 } else {
                     log.warn("리스트에 null 객체가 포함되어 있습니다.");
                 }
             });
         }
    	return pets;
    }
    
    
}