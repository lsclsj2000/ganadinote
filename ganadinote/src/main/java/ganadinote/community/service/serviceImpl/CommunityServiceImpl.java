package ganadinote.community.service.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ganadinote.common.domain.Comment;
import ganadinote.community.dto.PostDetailDTO;
import ganadinote.community.mapper.CommunityMapper;
import ganadinote.community.service.CommunityService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {
	private final CommunityMapper communityMapper;
	
	@Override
	public Map<String, Object> getList(Integer categoryId, String q, String qTarget,
	                                   String postStatus, Integer page, Integer size) {
	  int pageSize = (size == null || size <= 0) ? 10 : size;
	  int current  = (page == null || page <= 0) ? 1  : page;
	  int offset   = (current - 1) * pageSize;

	  var list  = communityMapper.selectPostListBasic(categoryId, q, qTarget, pageSize, offset);
	  int total = communityMapper.countPostListBasic(categoryId, q, qTarget);

	  Map<String, Object> res = new HashMap<>();
	  res.put("list", list);
	  res.put("total", total);
	  res.put("page", current);
	  res.put("size", pageSize);
	  res.put("hasMore", total > current * pageSize);
	  return res;
	}
	
	
	@Override
	  public PostDetailDTO getPostDetail(int postId) {
	    return communityMapper.selectPostDetail(postId);
	  }

	  @Override
	  public List<Comment> getComments(int postId) {
	    return communityMapper.selectComments(postId);
	  }

	  @Override
	  public void addComment(int postId, Integer parentCmtId, String authorId, String cmtCtnt) {
	    communityMapper.insertComment(postId, parentCmtId, authorId, cmtCtnt);
	  }
	  
	@Override
	@Transactional // 필요 없지만 붙여도 무방
	public void increaseViewCount(long postId) {
		communityMapper.increaseViewCount(postId);
	}
	  

}
