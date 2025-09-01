package ganadinote.community.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import ganadinote.community.dto.CommentDTO;

@Mapper
public interface CommentMapper {
	 // 상세뷰용 전체 댓글 (계층 정렬 + 작성자 닉네임 포함)
    List<CommentDTO> selectComments(@Param("postId") int postId);

    // 페이징 목록 (있으면 사용)
    List<CommentDTO> findAllByPostId(@Param("postId") int postId,
                                     @Param("limit") int limit,
                                     @Param("offset") int offset);

    // 생성
    int insertComment(CommentDTO dto);

    // 본인만 소프트 삭제
    int markCommentDeleted(@Param("commentId") int commentId,
                           @Param("mbrCd") int mbrCd);
}
