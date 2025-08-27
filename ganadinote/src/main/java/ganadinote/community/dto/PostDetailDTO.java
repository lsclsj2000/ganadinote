package ganadinote.community.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostDetailDTO {

	private int postId;
	private int categoryId;
	private String categoryNm;
	private String postTtl;
	private String postCtnt;
	private String mbrNknm;
	private LocalDateTime postRegDate;
	private int postViewCount;
	private int postLikeCount;
	private int cmtCount;
	private String postStatus;
}
