package ganadinote.community.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import ganadinote.community.dto.CommentDTO;

@Mapper
public interface CommentMapper {
    // 댓글 목록 조회 (포스트 기준)
    List<CommentDTO> findAllByPostId(@Param("postId") long postId,
                                     @Param("limit") int limit,
                                     @Param("offset") int offset);

    // 댓글 생성
    int insertComment(CommentDTO dto);

    // 댓글 소프트 삭제
    int markCommentDeleted(@Param("commentId") long commentId,
                           @Param("mbrCd") String mbrCd);
}
