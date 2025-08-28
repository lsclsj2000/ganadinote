package ganadinote.community.service.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.Comment;
import ganadinote.common.file.FileMapper;
import ganadinote.common.file.FileUtils;
import ganadinote.community.dto.PostDetailDTO;
import ganadinote.community.dto.PostRequestDTO;
import ganadinote.community.mapper.CommunityMapper;
import ganadinote.community.service.CommunityService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {
	private final CommunityMapper communityMapper;
	private final FileMapper fileMapper;
	private final FileUtils fileUtils;
	
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
	
	@Override
	@Transactional
	public Long createPost(PostRequestDTO req, String mbrCd) {
	    // 1) DTO 만들어서 insert
	    var dto = new PostDetailDTO();
	    dto.setCategoryId(req.getCategoryId());
	    dto.setPostTtl(req.getPostTtl());
	    dto.setPostCtnt(req.getPostCtnt());
	    dto.setMbrCd(Integer.parseInt(mbrCd)); // mbr_cd 컬럼이 int니까

	    communityMapper.insertPostAndReturnId(dto);

	    Long postId = Long.valueOf(dto.getPostId()); // ← 생성된 키 여기서 얻음

	    // 2) 본문 내 이미지 경로 추출 → file.post_id 바인딩
	    var used = extractImageSrc(req.getPostCtnt()).stream()
	            .filter(u -> u.startsWith("/attachment/"))
	            .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
	    if (!used.isEmpty()) {
	        fileMapper.bindFilesToPostByPaths(postId, new java.util.ArrayList<>(used));
	    }

	    return postId;
	}
	
	private Set<String> extractImageSrc(String html) {
	    java.util.regex.Matcher m = java.util.regex.Pattern
	            .compile("<img[^>]+src=[\"']([^\"']+)[\"']", java.util.regex.Pattern.CASE_INSENSITIVE)
	            .matcher(html);
	    java.util.Set<String> urls = new java.util.LinkedHashSet<>();
	    while (m.find()) urls.add(m.group(1));
	    return urls;
	}


	@Override
	@Transactional
	public String uploadEditorImage(MultipartFile image) {
	    if (image == null || image.isEmpty()) {
	        throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
	    }

	    // 1) FileUtils 사용해서 물리 저장 + FileMetaData 생성
	    var meta = fileUtils.uploadFile(image, "community");

	    // 2) DB에 insert (postId는 아직 null 상태)
	    fileMapper.addfile(meta);

	    // 3) 클라이언트(섬머노트)에 돌려줄 경로
	    return meta.getFilePath();  // "/attachment/community/20250828/image/uuid.jpg"
	}
	  

}
