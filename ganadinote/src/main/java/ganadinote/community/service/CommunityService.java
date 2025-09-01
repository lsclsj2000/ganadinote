package ganadinote.community.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.Comment;
import ganadinote.community.dto.PostDetailDTO;
import ganadinote.community.dto.PostRequestDTO;


@Service
public interface CommunityService {
	 Map<String, Object> getList(Integer categoryId, String q, String qTarget,
             String postStatus, Integer page, Integer size);


	PostDetailDTO getPostDetail(int postId);

	List<Comment> getComments(int postId);

	void addComment(int postId, Integer parentCmtId, String authorId, String cmtCtnt);
	
	 void increaseViewCount(long postId);
	 
	 Long createPost(PostRequestDTO req, String mbrCd);
	 
	 String uploadEditorImage(MultipartFile image);
	 
	 /** 본인 글만 수정 */
    void updatePost(PostDetailDTO dto, Integer mbrCdFromToken);

    /** 본인 글만 삭제(소프트 삭제) */
    void deletePost(int postId, Integer mbrCdFromToken);

    /** (선택) 소유자 확인만 필요할 때 */
    boolean isOwner(int postId, Integer mbrCdFromToken);
    
    boolean toggleLike(int postId, int mbrCd);
    boolean hasLiked(int postId, int mbrCd);
    int getLikeCount(int postId);
}
