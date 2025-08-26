package ganadinote.common.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Comment {
	
	private int commentId; 
	private int postId; 
	private int commentParentId; 
	private int mbrCd; 
	private String commentCtnt; 
	private LocalDateTime commentRegDate; 
	private LocalDateTime commentMdfcnDate; 
	private String commentStatusCd;
}
