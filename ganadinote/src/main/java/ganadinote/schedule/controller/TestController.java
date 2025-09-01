package ganadinote.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import ganadinote.notification.service.NotificationService;

@RestController
public class TestController {

    private final NotificationService notificationService;

    @Autowired
    public TestController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/test/walk-alert")
    public String testWalkAlerts(@RequestParam Integer mbrCd) {
        // 알림 서비스 메서드를 직접 호출
        notificationService.processWalkAlert(mbrCd);
        return "회원 코드 " + mbrCd + "의 산책 알림이 수동으로 실행되었습니다. 로그를 확인하세요!";
    }
}