// src/main/java/ganadinote/community/service/serviceImpl/CommentServiceImpl.java
package ganadinote.community.service.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ganadinote.community.dto.CommentDTO;
import ganadinote.community.mapper.CommentMapper;
import ganadinote.community.service.CommentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    /* ===== 기존 사용처 호환 ===== */
    @Override
    public List<CommentDTO> getComments(int postId) {
        return commentMapper.selectComments(postId);
    }

    @Override
    @Transactional
    public void addComment(int postId, Integer parentCmtId, Integer mbrCd, String cmtCtnt) {
        CommentDTO dto = new CommentDTO();
        dto.setPostId(postId);
        // XML insertComment가 #{parentId, jdbcType=VARCHAR} 를 쓰므로 둘 다 세팅
        dto.setCommentParentId(parentCmtId);
        dto.setParentId(parentCmtId);
        dto.setMbrCd(mbrCd);
        dto.setCommentCtnt(cmtCtnt);
        commentMapper.insertComment(dto);
    }

    @Override
    @Transactional
    public void deleteComment(int commentId, Integer mbrCd) {
        int n = commentMapper.markCommentDeleted(commentId, mbrCd);
        if (n == 0) throw new IllegalStateException("삭제 권한이 없거나 이미 삭제된 댓글입니다.");
    }

    /* ===== CommentController 전용 시그니처 구현 ===== */
    @Override
    public List<CommentDTO> getCommentsByPost(long postId, int limit, int offset) {
        // Mapper는 int 파라미터이므로 안전 캐스팅
        int pid = Math.toIntExact(postId);
        return commentMapper.findAllByPostId(pid, limit, offset);
    }

    @Override
    @Transactional
    public CommentDTO createComment(long postId, Long parentId, String mbrCd, String content) {
        CommentDTO dto = new CommentDTO();
        dto.setPostId(Math.toIntExact(postId));
        // XML이 parentId(VARCHAR)를 참조하므로 두 필드 모두 세팅
        dto.setParentId(parentId == null ? null : Math.toIntExact(parentId));
        dto.setCommentParentId(parentId == null ? null : Math.toIntExact(parentId));
        // TokenUtils가 String 반환하므로 int로 변환
        Integer mbrCdInt = (mbrCd == null || mbrCd.isBlank()) ? null : Integer.valueOf(mbrCd);
        dto.setMbrCd(mbrCdInt);
        dto.setCommentCtnt(content);
        commentMapper.insertComment(dto);
        return dto;
    }

    @Override
    @Transactional
    public boolean deleteOwnComment(long commentId, String mbrCd) {
        Integer mbrCdInt = (mbrCd == null || mbrCd.isBlank()) ? null : Integer.valueOf(mbrCd);
        int updated = commentMapper.markCommentDeleted(Math.toIntExact(commentId), mbrCdInt);
        return updated > 0;
    }
}
