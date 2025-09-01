package ganadinote.auth.register.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EmailVerificationDTO {
    private String email;
    private String emailSendCd;
    private LocalDateTime emailFtime; // 만료 시간
    private LocalDateTime emailRegDate; // 생성 시간
}