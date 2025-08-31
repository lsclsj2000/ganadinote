package ganadinote.notification.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ganadinote.location.service.LocationService;
import ganadinote.main.service.MainService;
import ganadinote.notification.domain.PetWithBreedDTO;
import ganadinote.notification.domain.PushSubDTO;
import ganadinote.notification.mapper.PushMapper;
import ganadinote.notification.service.NotificationService;
import ganadinote.weather.domain.AirPollutionDTO;
import ganadinote.weather.domain.WeatherInfo;
import ganadinote.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationServiceImpl implements NotificationService {

    private final PushMapper pushMapper;
    private final LocationService locationService; 
    private final WeatherService weatherService;
    private final MainService mainService;

    /**
     * 알림 구독 정보를 저장하거나 업데이트합니다.
     * 기존 구독 정보가 있으면 is_active를 1로 업데이트하고, 없으면 새로 삽입합니다.
     */
    @Override
    @Transactional
    public void saveOrUpdateSubscription(int mbrCd, PushSubDTO dto) {
    		dto.setMbrCd(mbrCd); 
		/*
		 * pushMapper.getSubscriptionByEndpoint(dto.getEndpoint());
		 * 
		 * if (existingSubscriptionCount > 0) { log.info("기존 구독 정보가 발견되어 업데이트합니다.");
		 * pushMapper.updateSubscription(dto); } else {
		 * log.info("새로운 구독 정보입니다. 삽입합니다.");
		 */
            pushMapper.addSubscription(dto);
            log.info("회원 코드 {}의 구독 정보가 성공적으로 저장/업데이트되었습니다.", mbrCd);
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
     * 멤버 코드로 펫 정보를 받아오는 메소드.
     */
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
    
    /**
     * 알림 시간 설정
     */
    @Override
    @Transactional
    public void updateNotificationSchedule(Integer mbrCd, String notificationScheduleJson) {
    	pushMapper.updateNotificationSchedule(mbrCd, notificationScheduleJson);
    	log.info("회원 코드 {}의 알림 시간이 {}로 업데이트되었습니다.", mbrCd, notificationScheduleJson);
    }
    
    /**
     * 알림 시간 조회
     */
    @Override
    public String getNotificationSchedule(Integer mbrCd) {
    	return pushMapper.getNotificationSchedule(mbrCd);    	
    }

    /**
     * 푸시 알림을 실제로 전송하는 메소드입니다.
     */
    @Override
    public void sendNotification(Integer mbrCd, String message) {
        // 이 부분에 실제 푸시 알림 전송 로직이 구현되어야 합니다.
        log.info("회원 {}에게 푸시 알림 전송: {}", mbrCd, message);
    }
    

    /**
     * 푸시 알림 process.
     */
    /**
     * 회원 코드(mbrCd)를 기반으로 산책 알림을 처리하고 전송합니다.
     */
    public void processWalkAlert(Integer mbrCd) {
        log.info("회원 {}의 산책 알림 처리 시작", mbrCd);

        // 1. 회원 위치 정보 가져오기 (실제 로직에 맞게 수정 필요)
        // 이 부분은 사용자의 실제 위치 데이터를 가져오는 로직으로 대체되어야 합니다.
        double latitude = 37.5665; // 예시: 서울 위도
        double longitude = 126.9780; // 예시: 서울 경도

        // 2. 위치 정보를 기반으로 날씨와 미세먼지 정보 가져오기
        WeatherInfo weather = weatherService.getWeather(latitude, longitude);
        AirPollutionDTO air = weatherService.getAirPollution(latitude, longitude);

        // 3. 해당 사용자의 펫 정보와 품종 민감도 데이터 가져오기
        List<PetWithBreedDTO> pets = mainService.getPetInfoWithBreedByMbrCd(mbrCd);

        log.info("--- pets 리스트 디버깅 시작 ---");
        if (pets != null) {
            log.info("pets 리스트 크기: {}", pets.size());
            for (int i = 0; i < pets.size(); i++) {
                log.info("pets[{}] = {}", i, pets.get(i));
            }
        } else {
            log.info("pets 리스트는 null입니다.");
        }
        log.info("--- pets 리스트 디버깅 종료 ---");

        if (pets == null || pets.isEmpty()) {
            log.warn("회원 {}의 등록된 반려동물 정보가 없습니다.", mbrCd);
            return;
        }

        // 4. 알림 조건 체크 및 메시지 생성
        for (PetWithBreedDTO pet : pets) {
            String alertMessage = null;
            if (weather != null && (weather.getTemp() > pet.getMaxTemp() || weather.getTemp() < pet.getMinTemp())) {
                alertMessage = String.format("%s의 산책하기엔 온도가 적합하지 않아요. 🌡️ (현재 %.1f°C)", pet.getPetName(), weather.getTemp());
            } else if (weather != null && weather.isRaining()) {
                alertMessage = String.format("%s의 산책하기엔 비가 오고 있어요. ☂️", pet.getPetName());
            } else if (air != null && (air.getPm25() > 75 || air.getPm10() > 150)) {
                alertMessage = String.format("%s의 산책하기엔 미세먼지 농도가 높아요. 😷", pet.getPetName());
            }

            // 5. 조건에 맞으면 알림 전송
            if (alertMessage != null) {
                sendNotification(mbrCd, alertMessage);
            }
        }
    }
}