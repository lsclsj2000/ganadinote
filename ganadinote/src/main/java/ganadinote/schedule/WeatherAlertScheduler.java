package ganadinote.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ganadinote.common.domain.PushSubscription;
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
public class WeatherAlertScheduler {

    private final PushMapper pushMapper;
    private final MainService mainService;
    private final NotificationService notificationService;
    private final WeatherService weatherService;

    // 매일 오전 6시부터 밤 11시까지 매시간 정각에 실행
    @Scheduled(cron = "0 0 6-23 * * ?")
    @Transactional(readOnly = true)
    public void checkAndSendWalkAlerts() {
        log.info("산책 알람 스케쥴러 실행");

        String todayDayOfWeek = LocalDate.now().getDayOfWeek().toString().toLowerCase().substring(0, 3);
        String currentHour = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")); // MM -> mm으로 수정 (분)

        // 1. 현재 요일과 시간에 맞는 모든 구독 정보를 DB에서 가져옴
        List<PushSubscription> subscriptionsToNotify = pushMapper.findSubscriptionsBySchedule(todayDayOfWeek, currentHour);

        for (PushSubscription subscription : subscriptionsToNotify) {
            String mbrCd = String.valueOf(subscription.getMbrCd());
            double latitude = 35.805433; // TODO: DB에서 사용자 위치정보(위도/경도) 가져오기
            double longitude = 127.148210;

            // 2. 현재 날씨 (온도, 비) 및 미세먼지 정보 가져오기
            WeatherInfo weather = weatherService.getWeather(latitude, longitude);
            AirPollutionDTO air = weatherService.getAirPollution(latitude, longitude);

            // 3. 해당 사용자의 펫 정보와 품종 민감도 데이터 가져오기
            List<PetWithBreedDTO> pets = mainService.getPetInfoWithBreedByMbrCd(mbrCd);

            // for 루프 문법 오류 수정
            for (PetWithBreedDTO pet : pets) {
                String alertMessage = null;

                // 4. 알림 조건 체크
                if (weather.getTemp() > pet.getMaxTemp() || weather.getTemp() < pet.getMinTemp()) {
                    alertMessage = String.format("%s의 산책하기엔 온도가 적합하지 않아요. 🌡️ (현재 %.1f°C)", pet.getPetName(), weather.getTemp());
                } else if (weather.isRaining()) { // 날씨 조건 추가
                    alertMessage = String.format("%s의 산책하기엔 비가 오고 있어요. ☂️", pet.getPetName());
                } else if (air.getPm25() > 75 || air.getPm10() > 150) { // 미세먼지 조건 예시 추가
                    alertMessage = String.format("%s의 산책하기엔 미세먼지 농도가 높아요. 😷", pet.getPetName());
                }

                // 5. 조건에 맞으면 알림 발송 (기존 서비스 재사용)
                if (alertMessage != null) {
                    try {
                        notificationService.sendNotification(Integer.parseInt(mbrCd), alertMessage);
                    } catch (Exception e) {
                        log.error("알림 발송 실패: {}", e.getMessage());
                    }
                }
            } // for 루프 괄호 추가
        } // for 루프 괄호 추가
    } // 메소드 괄호 추가
}