package ganadinote.notification.controller;

import java.util.Collections;
import java.util.Map;import java.util.concurrent.ScheduledExecutorService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import ganadinote.notification.domain.PushSubDTO;
import ganadinote.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/push")
public class NotificationAPIController {
	
	private final NotificationService notificationService;
	private final ObjectMapper objectMapper;
	
	/**
	 * 푸시 알림 구독 정보를 저장하거나 업데이트하는 API.
	 * 기존 구독 정보가 있으면 is_active를 1로 업데이트하고, 없으면 새로 삽입합니다.
	 */
	@PostMapping("/subscribe")
	public String addSubscribe(@RequestBody PushSubDTO dto) {
		Integer mbrCd = 1; // 실제로는 로그인된 사용자의 mbrCd로 대체
		try {
			notificationService.saveOrUpdateSubscription(mbrCd, dto);
			log.info("구독 정보가 성공적으로 저장/업데이트되었습니다.");
			return "success";
		} catch(Exception e) {
			log.error("구독 정보 추가/활성화 실패", e);
			return "fail";
		}
	}

	/**
	 * 사용자의 현재 알림 구독 상태를 반환하는 API.
	 * 프론트엔드에서 버튼 상태를 결정하기 위해 호출합니다.
	 * @return { "isActive": true } or { "isActive": false }
	 */
	@GetMapping("/status")
	public Map<String, Boolean> getSubscriptionStatus() {
		Integer mbrCd = 1; // 실제로는 로그인된 사용자의 mbrCd로 대체
		try {
			boolean isActive = notificationService.isSubscriptionActive(mbrCd); 
			log.info("회원 코드 {}의 알림 상태: {}", mbrCd, isActive);
			return Map.of("isActive", isActive);
		} catch(Exception e) {
			log.error("알림 상태 확인 실패", e);
			return Map.of("isActive", false);
		}
	}
	
	/**
	 * 알림을 비활성화하는 API. DB의 is_active 컬럼을 0으로 업데이트합니다.
	 */
	@PostMapping("/unsubscribe")
	public String unsubscribe(@RequestBody Integer mbrCd) {
		try {
			notificationService.deactivateSubscription(mbrCd);
			log.info("알림 구독이 비활성화되었습니다.");
			return "success";
		} catch (Exception e) {
			log.error("구독 비활성화 실패", e);
			return "fail";
		}
	}
	
	/**
	 * 비활성화된 알림을 다시 활성화하는 API. DB의 is_active 컬럼을 1로 업데이트합니다.
	 */
	@PostMapping("/reactivate")
	public String reactivate() {
		Integer mbrCd = 1; // 실제로는 로그인된 사용자의 mbrCd로 대체
		try {
			notificationService.reactivateSubscription(mbrCd);
			log.info("알림 구독이 다시 활성화되었습니다.");
			return "success";
		} catch (Exception e) {
			log.error("구독 재활성화 실패", e);
			return "fail";
		}
	}

	@GetMapping("/send")
	public String sendPushNotification(@RequestParam Integer mbrCd, @RequestParam String message) {
		try {
			notificationService.sendNotification(mbrCd, message);
			return "Notification sent successfully";
		} catch (Exception e) {
			log.error("알림 전송 실패", e);
			return "Failed to send notification";
		}
	}
	
	@PostMapping("/set-time")
	public String setNotificationTime(@RequestBody JsonNode payload) {
		try {
			// 페이로드에서 mbrCd와 notificationSchedule 값을 가져옴
			Integer mbrCd = payload.get("mbrCd").asInt();
			String notificationScheduleJson = payload.get("notificationSchedule").asText();
			
			// 서비스 계층을 호출하여 DB에 알림 스케줄 업데이트
			notificationService.updateNotificationSchedule(mbrCd, notificationScheduleJson);
			
			log.info("회원 코드 {}의 알림 스케줄이 {}로 설정되었습니다.", mbrCd, notificationScheduleJson);
			return "success";
		} catch (Exception e) {
			log.error("알림 시간 설정 실패", e);
			return "fail";
		}
	}
	
	@GetMapping("/schedule")
	public Map<String, Object> getNotificationSchedule(@RequestParam("mbrCd") Integer mbrCd){
		try {
			String scheduleJson = notificationService.getNotificationSchedule(mbrCd);
			
			if(scheduleJson != null && !scheduleJson.isEmpty()) {
				Map<String, String> scheduleMap = objectMapper.readValue(scheduleJson, new TypeReference<Map<String, String>>() {});
				
				log.info("회원 코드 {}의 알림 스케줄을 성공적으로 조회했습니다.", mbrCd);
				return Map.of("notificationSchedule", scheduleMap);
			}
			
			log.info("회원 코드 {}에 대한 알림 스케줄이 없습니다.", mbrCd);
			return Map.of("notificationSchedule", Collections.emptyMap()); // 빈 맵 반환
		} catch (Exception e) {
			log.error("알림 스케줄 조회 실패", e);
			return Map.of("notificationSchedule", Collections.emptyMap());
		}
	}
}