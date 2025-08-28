package ganadinote.common.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PushSubscription {
	  	private Long 			id;
	    private Integer 		mbrCd;
	    private String 			endpoint;
	    private String 			p256dh;
	    private String 			auth;
	    private LocalDateTime 	createdAt;
	    private String 			notificationSchedule;
	}

