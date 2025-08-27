package ganadinote.community.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import ganadinote.common.domain.Comment;
import ganadinote.community.dto.PostDetailDTO;
import ganadinote.community.dto.PostListDTO;

@Mapper
public interface CommunityMapper {
	List<PostListDTO> selectPostListBasic(
		      @Param("categoryId") Integer categoryId,
		      @Param("q") String q,
		      @Param("qTarget") String qTarget,  // "title" | "author"
		      @Param("size") int size,
		      @Param("offset") int offset
		  );

		  int countPostListBasic(@Param("categoryId") Integer categoryId,
		                         @Param("q") String q,
		                         @Param("qTarget") String qTarget);
	  
	  
	  PostDetailDTO selectPostDetail(@Param("postId") int postId);

	  List<Comment> selectComments(@Param("postId") int postId);

	  int insertComment(@Param("postId") int postId,
	                    @Param("commentParentId") Integer commentParentId,
	                    @Param("mbrCd") String mbrCd,
	                    @Param("commentCtnt") String commentCtnt);
	  
	  int increaseViewCount(@Param("postId") long postId);
}
