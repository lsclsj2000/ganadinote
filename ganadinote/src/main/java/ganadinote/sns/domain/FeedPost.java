package ganadinote.sns.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FeedPost {
	private Integer spCd;
    private String  spCn;
    private LocalDateTime spRegYmdt;
    private String  repImagePath;
}
