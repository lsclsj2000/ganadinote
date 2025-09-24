package ganadinote.notification.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ganadinote.common.domain.NotificationHistory;
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
import ganadinote.weather.domain.WeatherInfo.Hourly;
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
     * 알림 기록 조회
     */
    @Override
    public List<NotificationHistory> getNotificationHistory(int mbrCd) {
    	return pushMapper.getNotificationHistory(mbrCd);
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
    public void sendNotification(Integer mbrCd, String title, String body) {
        try {
            List<PushSubDTO> subscriptions = pushMapper.getActiveSubscriptionsByMbrCd(mbrCd);

            if (subscriptions != null && !subscriptions.isEmpty()) {
                PushService pushService = new PushService(vapidPublicKey, vapidPrivateKey, "mailto:admin@example.com");

                Map<String, String> payloadMap = Map.of(
                    "title", title, // 수정: 인자로 받은 title 사용
                    "body", body    // 수정: 인자로 받은 body 사용
                );
                String payloadJson = objectMapper.writeValueAsString(payloadMap);

                for (PushSubDTO subscription : subscriptions) {
                    try {
                        Subscription.Keys keys = new Subscription.Keys(subscription.getP256dh(), subscription.getAuth());
                        Subscription sub = new Subscription(subscription.getEndpoint(), keys);

                        Notification notification = new Notification(sub, payloadJson);
                        pushService.send(notification);

                        log.info("회원 {}의 구독({})에 알림 전송 완료", mbrCd, subscription.getEndpoint());
                        
                        NotificationHistory history = new NotificationHistory();
                        history.setMbrCd(mbrCd);
                        history.setTitle(title);    // 수정: 인자로 받은 title 사용
                        history.setMessage(body);    // 수정: 인자로 받은 body 사용
                        history.setSentAt(LocalDateTime.now());
                        
                        pushMapper.saveNotificationHistory(history);
                        
                        log.info("회원 {}의 알림 이력이 성공적으로 저장되었습니다.", mbrCd);
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
     * 알림 메시지를 생성하고 "테스트 알림" 제목으로 전송합니다.
     */
    @Override
    public void sendTestNotification(Integer mbrCd) {
        String combinedMessage = getWalkAlertMessage(mbrCd);
        String mbrNknm = mainService.getNknmByMbrCd(mbrCd);
        if (combinedMessage != null) {
            try {
                sendNotification(mbrCd, "테스트 알림입니다.", combinedMessage);
                log.info("회원 {}에게 테스트 알림 전송 완료: {}", mbrNknm, combinedMessage);
            } catch (Exception e) {
                log.error("회원 {}에게 테스트 알림 전송 실패", mbrNknm, e);
            }
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
    @Transactional
    public void processWalkAlertsForScheduledUsers() {
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);

        String dayOfWeekString = currentDay.toString();
        String timeString = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        try {
            List<PushSubDTO> subscriptions = pushMapper.findSubscriptionsBySchedule(dayOfWeekString, timeString);
            log.info("총 {}개의 알림 스케줄이 조회되었습니다.", subscriptions.size());

            for (PushSubDTO subscription : subscriptions) {
                int mbrCd = subscription.getMbrCd();
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
     * 회원 코드를 기반으로 현재 날씨 및 환경 조건을 확인하여 산책 알림 메시지를 생성하고 반환합니다.
     * 이 메서드는 메시지 생성에만 집중합니다.
     *
     * @param mbrCd 알림을 보낼 회원의 고유 코드
     * @return 생성된 알림 메시지 문자열 또는 알림이 필요 없는 경우 null
     */
    @Override
    public String getWalkAlertMessage(Integer mbrCd) {
        String mbrNknm = mainService.getNknmByMbrCd(mbrCd);
        log.info("회원 {}의 산책 알림 메시지 생성 시작", mbrNknm);

        LocationDTO location = locationService.getMemberLocation(mbrCd);
        if (location == null || location.getLatitude() == 0 || location.getLongitude() == 0) {
            log.warn("회원 {}의 위치 정보가 없어 알림을 처리할 수 없습니다. 기본값 사용.", mbrNknm);
            location = new LocationDTO();
            location.setLatitude(37.5665);
            location.setLongitude(126.9780);
        }

        WeatherInfo weather = weatherService.getWeather(location.getLatitude(), location.getLongitude());
        AirPollutionDTO air = weatherService.getAirPollution(location.getLatitude(), location.getLongitude());
        List<PetWithBreedDTO> pets = mainService.getPetInfoWithBreedByMbrCd(mbrCd);
        
        if (weather == null || air == null || pets == null || pets.isEmpty()) {
            log.warn("날씨, 미세먼지, 또는 펫 정보가 유효하지 않아 알림을 처리할 수 없습니다.");
            return null;
        }

        StringBuilder messageBuilder = new StringBuilder();

        // 1. 공통 메시지 추가
        messageBuilder.append(String.format("%s님, 산책 알림입니다.", mbrNknm));
        
        boolean needsNewline = false;

        // 2. 비, 눈, 미세먼지 알림 추가 (줄바꿈 포함)
        if (weather.isRaining()) {
            messageBuilder.append("\n비가 내리고 있어 산책에 주의하세요. ☂️");
            needsNewline = true;
        }
        if (weather.isSnowing()) {
            messageBuilder.append("\n눈이 내리고 있어 산책에 주의하세요. 🌨️");
            needsNewline = true;
        }
        
        if (air.getPm25() != null && air.getPm10() != null) {
            if (air.getPm25() > 75 || air.getPm10() > 150) {
                messageBuilder.append("\n오늘 미세먼지 농도가 높아 산책에 주의가 필요해요. 😷");
                needsNewline = true;
            }
        }
        
        // 3. 각 펫별 온도 알림 추가 (각 알림마다 줄바꿈)
        if (weather.getHourly() != null && !weather.getHourly().isEmpty()) {
            
            for (PetWithBreedDTO pet : pets) {
                Map<String, List<Integer>> badHoursByReason = new LinkedHashMap<>();
                badHoursByReason.put("heat", new ArrayList<>());
                badHoursByReason.put("cold", new ArrayList<>());

                for (Hourly hourly : weather.getHourly()) {
                    if (hourly.getTemp() != null) {
                        if (hourly.getTemp() > pet.getMaxTemp()) {
                            badHoursByReason.get("heat").add(hourly.getTime().getHour());
                        } else if (hourly.getTemp() < pet.getMinTemp()) {
                            badHoursByReason.get("cold").add(hourly.getTime().getHour());
                        }
                    }
                }
                
                List<String> petAlerts = new ArrayList<>();
                if (!badHoursByReason.get("heat").isEmpty()) {
                    List<String> condensedHeatHours = condenseHours(badHoursByReason.get("heat"));
                    petAlerts.add(String.join(", ", condensedHeatHours) + "에는 (" + pet.getMaxTemp() + "°C). 보다 높아 너무 더워요🥵");
                }
                if (!badHoursByReason.get("cold").isEmpty()) {
                    List<String> condensedColdHours = condenseHours(badHoursByReason.get("cold"));
                    petAlerts.add(String.join(", ", condensedColdHours) + "에는 (" + pet.getMinTemp() + "°C). 보다 낮아 너무 추워요🥶");
                }
                
                if (!petAlerts.isEmpty()) {
                    messageBuilder.append(String.format("\n🐶%s에게는 ", pet.getPetName()));
                    messageBuilder.append(String.join(" ", petAlerts));
                    needsNewline = true;
                }
            }
        }

        // 최종 메시지 반환
        if (messageBuilder.length() > 0) {
            return messageBuilder.toString();
        } else {
            log.info("회원 {}의 강아지들이 산책하기 좋은 날씨입니다.", mbrNknm);
            return null;
        }
    }

    /**
     * 알림 메시지를 생성하고 전송하는 통합 메서드.
     */
    @Override
    public void processWalkAlert(Integer mbrCd) {
        String combinedMessage = getWalkAlertMessage(mbrCd);
        String mbrNknm = mainService.getNknmByMbrCd(mbrCd);
        if (combinedMessage != null) {
            try {
                // 수정: "산책 알림"이라는 제목을 추가하여 3개의 인자를 전달
                sendNotification(mbrCd, "산책 알림", combinedMessage);
                log.info("회원 {}에게 알림 전송 완료: {}", mbrNknm, combinedMessage);
            } catch (Exception e) {
                log.error("회원 {}에게 알림 전송 실패", mbrNknm, e);
            }
        }
    }
    
    /**
     * 연속된 시간들을 묶어주는 헬퍼 메서드
     * 예: [9, 10, 11, 14, 15] -> ["오전 9시~11시", "오후 2시~3시"]
     */
    private List<String> condenseHours(List<Integer> hours) {
        if (hours == null || hours.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 핵심 수정: 중복 제거 후 오름차순 정렬
        List<Integer> sortedUniqueHours = hours.stream().sorted().distinct().collect(Collectors.toList());
        
        List<String> result = new ArrayList<>();
        if (sortedUniqueHours.isEmpty()) {
            return result;
        }
        
        int i = 0;
        while (i < sortedUniqueHours.size()) {
            int start = sortedUniqueHours.get(i);
            int end = start;
            while (i + 1 < sortedUniqueHours.size() && sortedUniqueHours.get(i + 1) == end + 1) {
                end = sortedUniqueHours.get(i + 1);
                i++;
            }
            result.add(formatHourRange(start, end));
            i++;
        }
        
        return result;
    }
    
    /**
     * 24시간을 오전/오후로 변환하고 시간 범위를 포맷하는 헬퍼 메서드
     */
    private String formatHourRange(int startHour, int endHour) {
        if (startHour == endHour) {
            return formatHour12(startHour);
        }
        
        String startFormatted = formatHour12(startHour);
        String endFormatted = formatHour12(endHour);
        
        String startPrefix = getAmPmPrefix(startHour);
        String endPrefix = getAmPmPrefix(endHour);

        if (startPrefix.equals(endPrefix)) {
            return String.format("%s %d시~%d시", startPrefix, to12Hour(startHour), to12Hour(endHour));
        } else {
            return String.format("%s~%s", startFormatted, endFormatted);
        }
    }
    
    /**
     * 24시간을 12시간 표기법으로 변환
     */
    private int to12Hour(int hour24) {
        if (hour24 == 0 || hour24 == 24) return 12; // 자정 0시는 12시로 표시
        if (hour24 > 12) return hour24 - 12;
        return hour24;
    }
    
    /**
     * 시간대별 접두사(오전, 오후) 반환
     */
    private String getAmPmPrefix(int hour24) {
        if (hour24 >= 0 && hour24 <= 11) {
            return "오전";
        }
        return "오후";
    }
    
    /**
     * 24시간을 포맷된 문자열로 변환
     */
    private String formatHour12(int hour24) {
        String prefix = getAmPmPrefix(hour24);
        int hour12 = to12Hour(hour24);
        return String.format("%s %d시", prefix, hour12);
    }
    
    
}

