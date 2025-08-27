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
	                               @RequestParam(required = false, name="q") String q,
	                               @RequestParam(defaultValue = "title", name="qTarget") String qTarget,
	                               @RequestParam(defaultValue = "1") Integer page,
	                               @RequestParam(defaultValue = "10") Integer size,
	                               Model model) {

	    Map<String, Object> res = communityService.getList(categoryId, q, qTarget, "ACTIVE", page, size);

	    model.addAttribute("post",   res.get("list"));
	    model.addAttribute("total",  res.get("total"));
	    model.addAttribute("page",   res.get("page"));
	    model.addAttribute("size",   res.get("size"));
	    model.addAttribute("hasMore",res.get("hasMore"));

	    model.addAttribute("categoryId", categoryId);
	    model.addAttribute("q",        q);
	    model.addAttribute("qTarget",  qTarget);
	    return "community/communityMainView";
	}

	@GetMapping("/api/list")
	@ResponseBody
	public List<PostListDTO> listApi(@RequestParam(required = false) Integer categoryId,
	                                 @RequestParam(required = false, name="q") String q,
	                                 @RequestParam(defaultValue = "title", name="qTarget") String qTarget,
	                                 @RequestParam(defaultValue = "10") Integer size,
	                                 @RequestParam(defaultValue = "0")  Integer offset) {
	    int page = (size > 0) ? (offset / size) + 1 : 1;
	    Map<String, Object> data = communityService.getList(categoryId, q, qTarget, "ACTIVE", page, size);
	    return (List<PostListDTO>) data.get("list");
	}
	
	@GetMapping("/addPost")
	public String addPost() {
		return "community/addPostView";
	}
	
	@GetMapping("/postDetail")
	public String postDetail(@RequestParam int postId, Model model) {
		// 1) 조회수 증가 (+1)
	    communityService.increaseViewCount(postId);
	    // 2) 상세/댓글 조회
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
	

}
