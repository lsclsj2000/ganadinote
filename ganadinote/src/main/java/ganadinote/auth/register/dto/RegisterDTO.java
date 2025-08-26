package ganadinote.auth.register.dto;

import lombok.Data;

@Data
public class RegisterDTO {

	private String mbrEmail;
    private String mbrPw;
    private String mbrNknm;
    private String confirmPw;
    
}
