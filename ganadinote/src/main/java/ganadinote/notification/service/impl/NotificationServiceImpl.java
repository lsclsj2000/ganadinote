package ganadinote.notification.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ganadinote.common.domain.PushSubscription;
import ganadinote.location.domain.LocationDTO;
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
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationServiceImpl implements NotificationService {

    private final PushMapper pushMapper;
    private final LocationService locationService; 
    private final WeatherService weatherService;
    private final MainService mainService;
    private final ObjectMapper objectMapper;
    
    @Value("${vapid.public.key}")
    private String vapidPublicKey;

    @Value("${vapid.private.key}")
    private String vapidPrivateKey;

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
        try {
            List<PushSubscription> subscriptions = pushMapper.getActiveSubscriptionsByMbrCd(mbrCd);

            if (subscriptions != null && !subscriptions.isEmpty()) {
                // PushService 생성
                PushService pushService = new PushService(vapidPublicKey, vapidPrivateKey, "mailto:admin@example.com");

                // JSON payload
                Map<String, String> payloadMap = Map.of(
                    "title", "산책 알림",
                    "body", message
                );
                String payloadJson = objectMapper.writeValueAsString(payloadMap);

                for (PushSubscription subscription : subscriptions) {
                    try {
                        // Subscription 객체 생성
                        Subscription.Keys keys = new Subscription.Keys(subscription.getP256dh(), subscription.getAuth());
                        Subscription sub = new Subscription(subscription.getEndpoint(), keys);

                        // Notification 생성 (String payload 사용!)
                        Notification notification = new Notification(sub, payloadJson);

                        // 전송
                        pushService.send(notification);

                        log.info("회원 {}의 구독({})에 알림 전송 완료", mbrCd, subscription.getEndpoint());
                    } catch (Exception e) {
                        log.error("회원 {} 구독({}) 알림 전송 실패: {}", mbrCd, subscription.getEndpoint(), e.getMessage());
                    }
                }
            } else {
                log.warn("회원 {}의 활성화된 푸시 구독 정보가 없습니다.", mbrCd);
            }
        } catch (Exception e) {
            log.error("알림 전송 중 오류 발생: {}", e.getMessage());
        }
    }
    

    /**
     * 푸시 알림 process.
     */
    /**
     * 푸시 알림 스케줄에 따라 알림이 필요한 회원들의 산책 알림을 처리하고 전송합니다.
     * 스케줄러에 의해 호출되는 메서드로, 알림 로직의 핵심적인 역할을 수행합니다.
     * 각 회원의 알림 스케줄 데이터를 파싱하고, 현재 시간에 해당하는 경우
     * 날씨, 미세먼지, 펫 정보 등을 확인하여 알림 메시지를 생성한 후 전송합니다.
     */
    @Transactional(readOnly = true)
    public void processWalkAlertsForScheduledUsers() {
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);

        String dayOfWeekString = currentDay.toString();
        String timeString = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        try {
            List<PushSubscription> subscriptions = pushMapper.findSubscriptionsBySchedule(dayOfWeekString, timeString);
            log.info("총 {}개의 알림 스케줄이 조회되었습니다.", subscriptions.size());

            for (PushSubscription subscription : subscriptions) {
                Integer mbrCd = subscription.getMbrCd();
                String mbrNknm = mainService.getNknmByMbrCd(mbrCd);

                try {
                	String notificationScheduleJson = pushMapper.getNotificationScheduleByMbrCd(mbrCd);
                	
                    if (notificationScheduleJson != null) {
                        Map<String, String> schedule = objectMapper.readValue(notificationScheduleJson, new TypeReference<Map<String, String>>() {});
                        String scheduledTimeStr = schedule.get(dayOfWeekString);

                        if (scheduledTimeStr != null && LocalTime.parse(scheduledTimeStr, DateTimeFormatter.ofPattern("HH:mm")).equals(currentTime)) {
                            log.info("회원 {}의 알림 스케줄 시간이 되어 알림을 확인합니다.", mbrNknm);
                            processWalkAlert(mbrCd); // 알림 처리 로직 호출
                        }
                    } else {
                         log.warn("회원 {}의 알림 스케줄 데이터가 null입니다. 다음 스케줄러 실행을 기다립니다.", mbrNknm);
                    }
                } catch (Exception e) {
                    log.error("개별 알림 처리 중 오류 발생", e);
                }
            }
        } catch (Exception e) {
            log.error("알림 스케줄러 실행 중 오류 발생", e);
        }
    }

    /**
     * 회원 코드를 기반으로 현재 날씨 및 환경 조건을 확인하여 산책 알림을 처리하고 전송합니다.
     *
     * 이 메서드는 다음 단계를 수행합니다:
     * 1. 회원의 위치 정보를 조회하고, 위치 정보가 없을 경우 기본값(서울)을 사용합니다.
     * 2. 위치를 기준으로 현재 날씨, 미세먼지 정보를 가져옵니다.
     * 3. 회원이 등록한 반려동물들의 정보를 품종별 온도 민감도와 함께 조회합니다.
     * 4. 현재 날씨가 각 반려동물의 적정 온도 범위를 벗어나거나, 비/눈이 오거나, 미세먼지 농도가 높을 경우
     * 적절한 알림 메시지를 생성합니다.
     * 5. 생성된 메시지가 있을 경우, 푸시 알림을 전송합니다.
     *
     * @param mbrCd 알림을 보낼 회원의 고유 코드
     */
    public void processWalkAlert(Integer mbrCd) {
        String mbrNknm = mainService.getNknmByMbrCd(mbrCd);
        log.info("회원 {}의 산책 알림 처리 시작", mbrNknm);

        LocationDTO location = locationService.getMemberLocation(mbrCd);
        log.info("회원 {}의 현재 위치 정보: 위도 = {}, 경도 = {}", mbrNknm, location != null ? location.getLatitude() : "N/A", location != null ? location.getLongitude() : "N/A");
        if (location == null || location.getLatitude() == 0 || location.getLongitude() == 0) {
            log.warn("회원 {}의 위치 정보가 없어 알림을 처리할 수 없습니다. 기본값 사용.", mbrNknm);
            location = new LocationDTO();
            location.setLatitude(37.5665);
            location.setLongitude(126.9780); // 서울 기본값
        }

        WeatherInfo weather = weatherService.getWeather(location.getLatitude(), location.getLongitude());
        AirPollutionDTO air = weatherService.getAirPollution(location.getLatitude(), location.getLongitude());
        List<PetWithBreedDTO> pets = mainService.getPetInfoWithBreedByMbrCd(mbrCd);

        if (weather != null && air != null && pets != null && !pets.isEmpty()) {
            List<String> combinedAlertMessages = new ArrayList<>();
            boolean isUnwalkableWeather = false;

            // 1. 비/눈이 오는 경우를 먼저 확인하여 모든 강아지에 대해 알림을 한 번만 보냅니다.
            if (weather.isRaining()) {
                combinedAlertMessages.add(" 비가 와서 산책하기엔 좋지 않아요. ☂️");
                isUnwalkableWeather = true;
            }
            if (weather.isSnowing()) {
                combinedAlertMessages.add(" 눈이 오고 있어 산책하기엔 좋지 않아요. 🌨️");
                isUnwalkableWeather = true;
            }

            // 2. 각 펫별로 온도와 미세먼지 알림 메시지를 생성합니다.
            for (PetWithBreedDTO pet : pets) {
                if (pet != null) {
                    if (weather.getTemp() != null) {
                        if (weather.getTemp() > pet.getMaxTemp()) {
                            combinedAlertMessages.add(String.format("%s가 산책하기엔 너무 더워요. 🌡️ (현재 %.1f°C)", pet.getPetName(), weather.getTemp()));
                        } else if (weather.getTemp() < pet.getMinTemp()) {
                            combinedAlertMessages.add(String.format("%s가 산책하기엔 너무 추워요. ☃️ (현재 %.1f°C)", pet.getPetName(), weather.getTemp()));
                        }
                    }

                    if (air.getPm25() != null && air.getPm10() != null) {
                        if (air.getPm25() > 75 || air.getPm10() > 150) {
                            combinedAlertMessages.add(String.format("%s가 산책하기엔 미세먼지 농도가 높아요. 😷", pet.getPetName()));
                        }
                    }
                }
            }

            // 3. 알림 메시지가 있을 경우 한 번에 전송합니다.
            if (!combinedAlertMessages.isEmpty()) {
                String combinedMessage = String.format("%s님, 산책 알림입니다. ", mbrNknm) + String.join(" ", combinedAlertMessages);
                try {
                    sendNotification(mbrCd, combinedMessage);
                    log.info("회원 {}에게 알림 전송 완료: {}", mbrNknm, combinedMessage);
                } catch (Exception e) {
                    log.error("회원 {}에게 알림 전송 실패", mbrNknm, e);
                }
            } else {
                // 날씨 조건이 좋지 않을 때만 로그를 남깁니다.
                if (!isUnwalkableWeather) {
                     log.info("회원 {}의 강아지들이 산책하기 좋은 날씨입니다.", mbrNknm);
                }
            }
        } else {
            log.warn("날씨, 미세먼지, 또는 펫 정보가 유효하지 않아 알림을 처리할 수 없습니다.");
        }
    }
}
