package ganadinote.community.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import ganadinote.common.domain.Comment;
import ganadinote.common.domain.Post;
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
	  
	  int insertPostAndReturnId(PostDetailDTO dto);
	  
	  
	  Post selectPostById(@Param("postId") Long postId);

	    // 글 소유자 확인용 (필요 시)
	  Integer selectAuthorMbrCd(@Param("postId") int postId);

	int updatePostByOwner(PostDetailDTO dto);       // WHERE mbr_cd = ?
	int softDeletePostByOwner(@org.apache.ibatis.annotations.Param("postId") int postId,
                @org.apache.ibatis.annotations.Param("mbrCd") Integer mbrCd);
	
	// like 관련
	boolean hasLiked(@Param("postId") int postId, @Param("mbrCd") int mbrCd);
	int insertLike(@Param("postId") int postId, @Param("mbrCd") int mbrCd);
	int deleteLike(@Param("postId") int postId, @Param("mbrCd") int mbrCd);
	int bumpLikeCount(@Param("postId") int postId, @Param("delta") int delta);
	int getLikeCount(@Param("postId") int postId);
	
}
