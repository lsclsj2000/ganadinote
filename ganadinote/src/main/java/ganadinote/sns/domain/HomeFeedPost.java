package ganadinote.sns.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class HomeFeedPost {
	private Integer spCd;
    private Integer mbrCd;
    private String  spCn;
    private LocalDateTime spRegYmdt;
    private String  nickname;
    private String  handle;

    private String  profilePath;
    private String  repImagePath;
    private List<String> imagePaths;
    
    private Boolean following;
}
