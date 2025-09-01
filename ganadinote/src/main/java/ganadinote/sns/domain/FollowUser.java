package ganadinote.sns.domain;

import lombok.Data;

@Data
public class FollowUser {
	private Integer mbrCd;
    private String nickname;
    private String handle;
    private String profilePath;
}
