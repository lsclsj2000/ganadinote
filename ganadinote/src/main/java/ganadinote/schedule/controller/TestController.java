package ganadinote.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ganadinote.schedule.NotificationScheduler;

@RestController
public class TestController {

    private final NotificationScheduler notificationScheduler;

    @Autowired
    public TestController(NotificationScheduler notificationScheduler) {
        this.notificationScheduler = notificationScheduler;
    }

    @GetMapping("/test/walk-alert")
    public String testWalkAlerts() {
        // 스케줄러 메서드를 직접 호출
        notificationScheduler.checkAndSendWalkAlerts();
        return "산책 알림 스케줄러가 수동으로 실행되었습니다. 로그를 확인하세요!";
    }
}