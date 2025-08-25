package ganadinote.common.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SnsPost {
	private Integer spCd;
    private Integer mbrCd;
    private String  spCn;
    private LocalDateTime spRegYmdt;
}
