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
}
