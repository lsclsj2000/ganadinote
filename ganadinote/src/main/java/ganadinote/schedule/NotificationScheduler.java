package ganadinote.schedule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ganadinote.common.domain.PushSubscription;
import ganadinote.location.domain.LocationDTO;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            // 변경: 기존의 findSubscriptionsBySchedule 메소드를 그대로 사용
            List<PushSubscription> subscriptions = pushMapper.findSubscriptionsBySchedule(dayOfWeekString, timeString);
            
            log.info("총 {}개의 알림 스케줄이 조회되었습니다.", subscriptions.size());

            for (PushSubscription subscription : subscriptions) {
                Integer mbrCd = subscription.getMbrCd();

                try {
                    // 알림 스케줄이 null이 아닌 경우에만 파싱 진행
                    if (subscription.getNotificationSchedule() != null) {
                        Map<String, String> schedule = objectMapper.readValue(subscription.getNotificationSchedule(), new TypeReference<Map<String, String>>() {});
                        String scheduledTimeStr = schedule.get(dayOfWeekString);

                        if (scheduledTimeStr != null && LocalTime.parse(scheduledTimeStr, DateTimeFormatter.ofPattern("HH:mm")).equals(currentTime)) {
                            log.info("회원 {}의 알림 스케줄 시간이 되어 알림을 확인합니다.", mbrCd);

                            LocationDTO location = locationService.getMemberLocation(mbrCd);

                            if (location == null || location.getLatitude() == 0 || location.getLongitude() == 0) {
                                log.warn("회원 {}의 위치 정보가 없어 알림을 처리할 수 없습니다. 기본값 사용.", mbrCd);
                                location = new LocationDTO();
                                location.setLatitude(37.5665);
                                location.setLongitude(126.9780); // 서울 기본값
                            }

                            WeatherInfo weather = weatherService.getWeather(location.getLatitude(), location.getLongitude());
                            AirPollutionDTO air = weatherService.getAirPollution(location.getLatitude(), location.getLongitude());
                            List<PetWithBreedDTO> pets = mainService.getPetInfoWithBreedByMbrCd(mbrCd);
                            
                            if (weather != null && air != null && pets != null && !pets.isEmpty()) {
                                for (PetWithBreedDTO pet : pets) {
                                    if (pet != null) {
                                        List<String> alertMessages = new ArrayList<>();

                                        if (weather.getTemp() != null) {
                                            if (weather.getTemp() > pet.getMaxTemp() || weather.getTemp() < pet.getMinTemp()) {
                                                alertMessages.add(String.format("산책하기에 온도가 적합하지 않아요. 🌡️ (현재 %.1f°C)", weather.getTemp()));
                                            }
                                        }

                                        if (weather.isRaining()) {
                                            alertMessages.add("산책하기에 비가 오고 있어요. ☂️");
                                        }

                                        if (air.getPm25() != null && air.getPm10() != null) {
                                            if (air.getPm25() > 75 || air.getPm10() > 150) {
                                                alertMessages.add("산책하기에 미세먼지 농도가 높아요. 😷");
                                            }
                                        }

                                        if (!alertMessages.isEmpty()) {
                                            String combinedMessage = String.format("%s님, %s를 위한 알림입니다. ", pet.getPetName(), pet.getPetName()) + String.join(" ", alertMessages);
                                            try {
                                                notificationService.sendNotification(mbrCd, combinedMessage);
                                                log.info("회원 {}에게 알림 전송 완료: {}", mbrCd, combinedMessage);
                                            } catch (Exception e) {
                                                log.error("회원 {}에게 알림 전송 실패", mbrCd, e);
                                            }
                                        } else {
                                            log.info("회원 {}의 펫 {}은(는) 산책하기 좋은 날씨입니다.", mbrCd, pet.getPetName());
                                        }
                                    }
                                }
                            } else {
                                log.warn("날씨, 미세먼지, 또는 펫 정보가 유효하지 않아 알림을 처리할 수 없습니다.");
                            }
                        }
                    } else {
                         log.warn("회원 {}의 알림 스케줄 데이터가 null입니다. 다음 스케줄러 실행을 기다립니다.", mbrCd);
                    }
                } catch (Exception e) {
                    log.error("개별 알림 처리 중 오류 발생", e);
                }
            }
        } catch (Exception e) {
            log.error("알림 스케줄러 실행 중 오류 발생", e);
        }
    }
}