package ganadinote.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushSubDTO {
	private Long 			id; // PushSubscription의 id 추가
	private LocalDateTime 	createdAt; // PushSubscription의 createdAt 추가
	private String 	        endpoint;
    private String 	        p256dh;
    private String 	        auth;
    private String 	        notificationSchedule;
    private Boolean         isActive;
    private int 	        mbrCd;
}