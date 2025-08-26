package ganadinote.community.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import ganadinote.common.domain.Comment;
import ganadinote.community.dto.PostDetailDTO;

@Service
public interface CommunityService {
	public Map<String, Object> getList(Integer categoryId, String keyword, String sort, String statusCd, Integer page,
			Integer size);

	PostDetailDTO getPostDetail(int postId);

	List<Comment> getComments(int postId);

	void addComment(int postId, Integer parentCmtId, String authorId, String cmtCtnt);
}
