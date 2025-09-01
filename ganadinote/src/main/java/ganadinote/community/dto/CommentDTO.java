package ganadinote.community.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentDTO {

	private Integer commentId;
    private Integer postId;
    private Integer commentParentId;   // ✅ 뷰에서 사용하는 이름
    private Integer mbrCd;
    private String  authorNickname;    // ✅ 뷰에서 사용하는 이름
    private String  commentCtnt;
    private LocalDateTime commentRegDate;
    private String  commentStatus;

    // (선택) 기존에 쓰던 이름을 유지해야 하면, 추가로 갖고 있어도 무방
    private Integer parentId;          // ← 다른 쿼리 호환용
    private String  mbrNknm;           // ← 기존 XML 호환용
    private String  mbrProfile;           // ← 기존 XML 호환용

}
