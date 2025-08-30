package ganadinote.community.service;

import java.util.List;

import ganadinote.community.dto.CommentDTO;

public interface CommentService {
    List<CommentDTO> getCommentsByPost(long postId, int limit, int offset);
    CommentDTO createComment(long postId, Long parentId, String mbrCd, String commentCtnt);
    boolean deleteOwnComment(long commentId, String mbrCd);
}
