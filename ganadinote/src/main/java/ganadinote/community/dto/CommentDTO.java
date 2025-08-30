package ganadinote.community.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentDTO {

	private Long commentId;
    private Long postId;
    private Long parentId;      // null=원댓글
    private String mbrCd;  // mbr_cd
    private String mbrNknm;  // join member.mbr_nicknm
    private String commentCtnt;     // comment_ctnt
    private LocalDateTime CommentRegDate;
    private String CommentStatus;    // ACTIVE/DELETE

}
