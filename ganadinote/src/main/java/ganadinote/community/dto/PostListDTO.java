package ganadinote.community.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostListDTO {
	// POSTLIST를 위한 DTO. cmtCount는 댓글 갯수 센거
	private String categoryNm; 
	private String postTtl;  
	private String postCtnt; 
	private String mbrNknm; 
	private int postId; 
	private LocalDateTime postRegDate; 
	private int postLikeCount; 
	private int postViewCount; 
	private int cmtCount;
	private String postStatus;


}
