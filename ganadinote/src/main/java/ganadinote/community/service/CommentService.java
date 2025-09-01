package ganadinote.community.service;

import java.util.List;

import ganadinote.community.dto.CommentDTO;

public interface CommentService {
	List<CommentDTO> getComments(int postId);
    void addComment(int postId, Integer parentCmtId, Integer mbrCd, String cmtCtnt);
    void deleteComment(int commentId, Integer mbrCd);
    
    // ✅ CommentController가 요구하는 시그니처
    List<CommentDTO> getCommentsByPost(long postId, int limit, int offset);
    CommentDTO createComment(long postId, Long parentId, String mbrCd, String content);
    boolean deleteOwnComment(long commentId, String mbrCd);
}
