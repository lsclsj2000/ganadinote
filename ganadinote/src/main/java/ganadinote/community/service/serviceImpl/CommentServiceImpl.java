package ganadinote.community.service.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import ganadinote.community.dto.CommentDTO;
import ganadinote.community.mapper.CommentMapper;
import ganadinote.community.service.CommentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    public List<CommentDTO> getCommentsByPost(long postId, int limit, int offset) {
        return commentMapper.findAllByPostId(postId, limit, offset);
    }

    @Override
    public CommentDTO createComment(long postId, Long parentId, String mbrCd, String commentCtnt) {
        CommentDTO dto = new CommentDTO();
        dto.setPostId(postId);
        dto.setParentId(parentId);
        dto.setMbrCd(mbrCd);
        dto.setCommentCtnt(commentCtnt);
        commentMapper.insertComment(dto);
        return dto;
    }

    @Override
    public boolean deleteOwnComment(long commentId, String mbrCd) {
        return commentMapper.markCommentDeleted(commentId, mbrCd) > 0;
    }
}
