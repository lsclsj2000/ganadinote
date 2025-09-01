package ganadinote.notification.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ganadinote.common.util.TokenUtils;
import ganadinote.location.service.LocationService;
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
	private final LocationService locationService;
	
	/**
	 * 푸시 알림 구독 정보를 저장하거나 업데이트하는 API.
	 * 기존 구독 정보가 있으면 is_active를 1로 업데이트하고, 없으면 새로 삽입합니다.
	 */
	@PostMapping("/subscribe")
	public String addSubscribe(@RequestBody PushSubDTO dto) {
		String mbrCdStr = TokenUtils.getMbrCd();
		if (mbrCdStr == null || mbrCdStr.trim().isEmpty()) {
	        log.error("토큰에 유효한 회원 코드(mbrCd)가 없습니다.");
	        return "fail";
	    }
		try {
			Integer mbrCd = Integer.parseInt(mbrCdStr);
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
		String mbrCdStr = TokenUtils.getMbrCd();
		if (mbrCdStr == null || mbrCdStr.trim().isEmpty()) {
			return Map.of("isActive", false);
		}
		try {
			Integer mbrCd = Integer.parseInt(mbrCdStr);
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
	public String unsubscribe() {
		String mbrCdStr = TokenUtils.getMbrCd();
		if (mbrCdStr == null || mbrCdStr.trim().isEmpty()) {
			log.error("토큰에 유효한 회원 코드가 없습니다.");
			return "fail";
		}
		try {
			Integer mbrCd = Integer.parseInt(mbrCdStr);
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
		String mbrCdStr = TokenUtils.getMbrCd();
		if (mbrCdStr == null || mbrCdStr.trim().isEmpty()) {
			log.error("토큰에 유효한 회원 코드가 없습니다.");
			return "fail";
		}
		try {
			Integer mbrCd = Integer.parseInt(mbrCdStr);
			notificationService.reactivateSubscription(mbrCd);
			log.info("알림 구독이 다시 활성화되었습니다.");
			return "success";
		} catch (Exception e) {
			log.error("구독 재활성화 실패", e);
			return "fail";
		}
	}

	/**
	 * 알림을 보내는 메서드
	 */
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
	
	/**
	 * 알림 시간을 설정하는 메서드
	 */
	@PostMapping("/set-time")
	public String setNotificationTime(@RequestBody JsonNode payload) {
		String mbrCdStr = TokenUtils.getMbrCd();
		if (mbrCdStr == null || mbrCdStr.trim().isEmpty()) {
			log.error("토큰에 유효한 회원 코드가 없습니다.");
			return "fail";
		}
		try {
			Integer mbrCd = Integer.parseInt(mbrCdStr);
			// 페이로드에서 mbrCd와 notificationSchedule 값을 가져옴
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
	
	/**
	 * 설정된 알림 시간을 조회하는 메서드
	 */
	@GetMapping("/schedule")
	public Map<String, Object> getNotificationSchedule(){
		String mbrCdStr = TokenUtils.getMbrCd();
		if (mbrCdStr == null || mbrCdStr.trim().isEmpty()) {
			return Map.of("notificationSchedule", Collections.emptyMap());
		}
		try {
			Integer mbrCd = Integer.parseInt(mbrCdStr);
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
		// 수동 테스트를 위한 임시 엔드포인트
	 @GetMapping("/testAlert")
	    public String testAlert(@RequestParam Integer mbrCd) {
	        try {
	            notificationService.processWalkAlert(mbrCd);
	            return "Test alert triggered successfully for mbrCd: " + mbrCd;
	        } catch (Exception e) {
	            log.error("Test alert failed", e);
	            return "Failed to trigger test alert: " + e.getMessage();
	        }
	    }
}