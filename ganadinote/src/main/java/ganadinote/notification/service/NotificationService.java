package ganadinote.notification.service;

import java.util.List;

import ganadinote.common.domain.PushSubscription;
import ganadinote.notification.domain.PushSubDTO;

public interface NotificationService {

	void addSubscription(Integer mbrCd, PushSubDTO dto);
	
	List<PushSubscription> getSubInfoByMbrCd(Integer mbrCd);
	
	// 알림 발송
	void sendNotification(Integer mbrCd, String message) throws Exception;
}
