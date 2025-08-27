package ganadinote.common.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Post {
	// 게시글
	private int postId; 
	private int categoryId; 
	private String postTtl; 
	private String postCtnt; 
	private int mbrCd; 
	private int postViewCount; 
	private int postLikeCount; 
	private LocalDateTime postRegDate; 
	private LocalDateTime postMdfcnDate; 
	private String postStatusCd; 
	private String postLocation;
}
