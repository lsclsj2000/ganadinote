package ganadinote.notification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ganadinote.notification.domain.PushSubDTO;
import ganadinote.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationController {
	
	private final NotificationService notificationService;
	
	@GetMapping("/push")
	public String pushPage() {
		return "push/pushView";
	}
	
	@ResponseBody
	@PostMapping("/api/push/subscribe")
	public String addSubscribe(@RequestBody PushSubDTO dto) {
		Integer mbrCd =1;
		try {
			notificationService.addSubscription(mbrCd, dto);
			return "success";
		}catch(Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	@GetMapping("/api/push/send")
	@ResponseBody
	public String sendPushNotification(@RequestParam Integer mbrCd, @RequestParam String message) {
		try {
			notificationService.sendNotification(mbrCd, message);
			return "Notification sent successfully";
		}catch (Exception e) {
			e.printStackTrace();
			return "Failed to send notification";
		}
	}
}
