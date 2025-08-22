package ganadinote.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushSubDTO {
	private String endpoint;
    private String p256dh;
    private String auth;
}
