package ganadinote.community.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ganadinote.community.dto.PostListDTO;
import ganadinote.community.service.CommunityService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {
	
	private final CommunityService communityService;
	
	@GetMapping("")
	public String getCommunityMain(@RequestParam(required = false) Integer categoryId,
		    @RequestParam(required = false) String keyword,
		    @RequestParam(defaultValue = "latest") String sort,
		    @RequestParam(required = false) String statusCd,
		    @RequestParam(defaultValue = "1") Integer page,
		    @RequestParam(defaultValue = "10") Integer size,
		    Model model) {
		
		Map<String, Object> res = communityService.getList(categoryId, keyword, sort, statusCd, page, size);
		
		// ▼ 뷰에서 반복할 대상: post (단수 키에 ‘리스트’가 들어감)
		model.addAttribute("post", res.get("list"));
		
		// 필요하면 페이징/정렬 파라미터도 그대로 내려주기
		model.addAttribute("total",  res.get("total"));
		model.addAttribute("page",   res.get("page"));
		model.addAttribute("size",   res.get("size"));
		model.addAttribute("hasMore",res.get("hasMore"));
		
		// 현재 검색/정렬 상태 보존용(칩/셀렉트 활성화)
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("keyword",    keyword);
		model.addAttribute("sort",       sort);
		return "community/communityMainView";
	}
	@GetMapping("/addPost")
	public String addPost() {
		return "community/addPostView";
	}
	
	@GetMapping("/postDetail")
	public String postDetail(@RequestParam int postId, Model model) {
	    var post = communityService.getPostDetail(postId);
	    var comments = communityService.getComments(postId);
	    model.addAttribute("post", post);
	    model.addAttribute("comments", comments);
		return "community/postDetail";
	}
	
	@PostMapping("/community/comment")
	  public String addComment(@RequestParam int postId,
	                           @RequestParam(required=false) Integer parentCmtId,
	                           @RequestParam String cmtCtnt
	                           /*, @AuthenticationPrincipal UserPrincipal user */) {
	    // 임시 authorId (나중에 로그인 사용자 ID로 교체)
	    String authorId = "demo-user";
	    communityService.addComment(postId, parentCmtId, authorId, cmtCtnt);
	    return "redirect:/community/postDetail?postId=" + postId;
	  }
	
	
	@GetMapping("/community/api/list")
	@ResponseBody
	public List<PostListDTO> listApi(
	        @RequestParam(required = false) Integer categoryId,
	        @RequestParam(defaultValue = "latest") String sort,
	        @RequestParam(defaultValue = "1") Integer page,
	        @RequestParam(defaultValue = "10") Integer size) {
	    var data = communityService.getList(categoryId, null, sort, null, page, size);
	    return (List<PostListDTO>) data.get("list");
	}
}
