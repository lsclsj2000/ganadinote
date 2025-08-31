package ganadinote.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ganadinote.common.domain.PushSubscription;
import ganadinote.location.service.LocationService;
import ganadinote.main.service.MainService;
import ganadinote.notification.domain.PetWithBreedDTO;
import ganadinote.notification.mapper.PushMapper;
import ganadinote.notification.service.NotificationService;
import ganadinote.weather.domain.AirPollutionDTO;
import ganadinote.weather.domain.WeatherInfo;
import ganadinote.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final PushMapper pushMapper;
    private final MainService mainService;
    private final NotificationService notificationService;
    private final WeatherService weatherService;
    private final LocationService locationService;
    private final ObjectMapper objectMapper;
    // ScheduledExecutorService 필드는 이미 제거되었습니다.

    // 매일 오전 6시부터 밤 11시까지 매시간 정각에 실행
    @Scheduled(cron = "0 0 6-23 * * ?")
    @Transactional(readOnly = true)
    public void checkAndSendWalkAlerts() {
        log.info("산책 알람 스케쥴러 실행");
        
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
        
        String dayOfWeekString = currentDay.toString();
        String timeString = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        try {
        	List<PushSubscription> subscriptions = pushMapper.findSubscriptionsBySchedule(dayOfWeekString, timeString);
            
            log.info("총 {}개의 알림 스케줄이 조회되었습니다.", subscriptions.size());

            for (PushSubscription subscription : subscriptions) {
                Integer mbrCd = subscription.getMbrCd();
                
                Map<String, String> schedule = objectMapper.readValue(subscription.getNotificationSchedule(), new TypeReference<Map<String, String>>() {});
                String scheduledTimeStr = schedule.get(dayOfWeekString);
                LocalTime scheduledTime = LocalTime.parse(scheduledTimeStr, DateTimeFormatter.ofPattern("HH:mm"));

                if (currentTime.equals(scheduledTime)) {
                    log.info("회원 {}의 알림 스케줄 시간이 되어 알림을 확인합니다.", mbrCd);
                    
                    double latitude = 37.5665; // 예시: 서울시청
                    double longitude = 126.9780; // 예시: 서울시청

                    WeatherInfo weather = weatherService.getWeather(latitude, longitude);
                    AirPollutionDTO air = weatherService.getAirPollution(latitude, longitude);

                    List<PetWithBreedDTO> pets = mainService.getPetInfoWithBreedByMbrCd(mbrCd);
                    
                    // ✨ 추가적인 방어 로직
                    if (weather != null && air != null && pets != null) {
                        for (PetWithBreedDTO pet : pets) {
                            if (pet != null) { // null 체크
                                String alertMessage = null;
                                
                                // 알림 조건 체크 (예시)
                                if (weather.getTemp() != null && (weather.getTemp() > pet.getMaxTemp() || weather.getTemp() < pet.getMinTemp())) {
                                    alertMessage = String.format("%s의 산책하기엔 온도가 적합하지 않아요. 🌡️ (현재 %.1f°C)", pet.getPetName(), weather.getTemp());
                                } else if (weather.isRaining()) {
                                    alertMessage = String.format("%s의 산책하기엔 비가 오고 있어요. ☂️", pet.getPetName());
                                } else if (air.getPm25() > 75 || air.getPm10() > 150) {
                                    alertMessage = String.format("%s의 산책하기엔 미세먼지 농도가 높아요. 😷", pet.getPetName());
                                }
                                
                                if (alertMessage != null) {
                                    try {
                                        notificationService.sendNotification(mbrCd, alertMessage);
                                        log.info("회원 {}에게 알림 전송 완료: {}", mbrCd, alertMessage);
                                    } catch (Exception e) {
                                        log.error("회원 {}에게 알림 전송 실패", mbrCd, e);
                                    }
                                }
                            }
                        }
                    } else {
                        log.warn("날씨, 미세먼지, 또는 펫 정보가 null이어서 알림을 처리할 수 없습니다.");
                    }
                }
            }
        } catch (Exception e) {
            log.error("알림 스케줄러 실행 중 오류 발생", e);
        }
    }
}