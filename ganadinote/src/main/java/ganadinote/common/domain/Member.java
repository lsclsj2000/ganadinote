package ganadinote.common.domain;

import java.time.LocalDateTime;

import ganadinote.common.domain.type.MemberStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    private Integer mbrCd;
    private String mbrEmail;
    private String mbrPw;
    private String mbrNknm;
    private String mbrProfile;
    private MemberStatus mbrStatus;
    private LocalDateTime mbrRegDate;
    private LocalDateTime mbrMdfcnDate;

    // 회원가입에 필요한 필드만 포함하는 생성자
    public Member(String mbrEmail, String mbrPw, String mbrNknm, MemberStatus mbrStatus) {
        this.mbrEmail = mbrEmail;
        this.mbrPw = mbrPw;
        this.mbrNknm = mbrNknm;
        this.mbrStatus = mbrStatus;
    }

}