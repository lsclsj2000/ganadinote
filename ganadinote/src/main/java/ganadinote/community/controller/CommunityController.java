package ganadinote.community.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.FileMetaData;
import ganadinote.common.file.FileMapper;
import ganadinote.common.file.FileUtils;
import ganadinote.community.dto.PostListDTO;
import ganadinote.community.dto.PostRequestDTO;
import ganadinote.community.service.CommunityService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final FileMapper fileMapper;
	private final FileUtils fileUtils;
	
	
	@GetMapping("/dev-login")
	public String devLogin(HttpSession session) {
	    session.setAttribute("SCD", 1); // 임시 회원코드
	    return "redirect:/community";
	}
	
	

    // 메인 목록
    @GetMapping("")
    public String getCommunityMain(@RequestParam(required = false) Integer categoryId,
                                   @RequestParam(required = false, name="q") String q,
                                   @RequestParam(defaultValue = "title", name="qTarget") String qTarget,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   Model model) {

        Map<String, Object> res = communityService.getList(categoryId, q, qTarget, "ACTIVE", page, size);

        model.addAttribute("post",    res.get("list"));
        model.addAttribute("total",   res.get("total"));
        model.addAttribute("page",    res.get("page"));
        model.addAttribute("size",    res.get("size"));
        model.addAttribute("hasMore", res.get("hasMore"));

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("q",         q);
        model.addAttribute("qTarget",   qTarget);
        return "community/communityMainView";
    }

    // 무한스크롤/더보기 API
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

    // 글쓰기 화면
    @GetMapping("/addPost")
    public String addPost() {
        return "community/addPostView";
    }

    // 글 저장 (섬머노트 본문 + 이미지 바인딩)
    @PostMapping("/post/add")
    public String addPostSubmit(@ModelAttribute PostRequestDTO req, HttpSession session) {
        Integer mbr = (Integer) session.getAttribute("SCD");
        if (mbr == null) return "redirect:/enter/login";
        String mbrCd = String.valueOf(mbr);
        Long postId = communityService.createPost(req, mbrCd);
        return "redirect:/community/postDetail?postId=" + postId;
    }

    // 상세
    @GetMapping("/postDetail")
    public String postDetail(@RequestParam int postId, Model model) {
        communityService.increaseViewCount(postId);
        var post = communityService.getPostDetail(postId);
        var comments = communityService.getComments(postId);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "community/postDetail";
    }

    @PostMapping("/comment")
    public String addComment(@RequestParam int postId,
                             @RequestParam(required=false) Integer parentCmtId,
                             @RequestParam String cmtCtnt,
                             HttpSession session) {
        // TODO: 로그인 사용자로 교체
        // String authorId = (String) session.getAttribute("SCD");
        String authorId = "demo-user";
        communityService.addComment(postId, parentCmtId, authorId, cmtCtnt);
        return "redirect:/community/postDetail?postId=" + postId;
    }

    @PostMapping(
    	    value = "/upload/summernote",
    	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    	    produces = MediaType.APPLICATION_JSON_VALUE
    	)
    	@ResponseBody
    	public ResponseEntity<Map<String, String>> uploadSummernote(
    	        @RequestParam("image") MultipartFile image,
    	        @RequestParam(value = "tempKey", required = false) String tempKey) {

    	    var meta = fileUtils.uploadFile(image, "community/editor");
    	    meta.setPostType("post");
    	    fileMapper.addfile(meta);

    	    return ResponseEntity.ok()
    	            .contentType(MediaType.APPLICATION_JSON)
    	            .body(Map.of("url", meta.getFilePath())); // ← 꼭 "url" 키
    	}
}