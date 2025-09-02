package ganadinote.schedule;

import ganadinote.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationService notificationService;

    // 매일 오전 6시부터 밤 11시까지 매시간 정각에 실행
    @Scheduled(cron = "0 0 6-23 * * ?")
    public void checkAndSendWalkAlerts() {
        log.info("산책 알람 스케쥴러 실행");
        // 알림 생성 및 전송 로직은 NotificationService로 위임
        notificationService.processWalkAlertsForScheduledUsers();
    }
}