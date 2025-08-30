package ganadinote.auth.login.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String mbrEmail;
    private String mbrPw;
}