package ganadinote.community.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.file.FileMapper;
import ganadinote.common.file.FileUtils;
import ganadinote.community.dto.PostListDTO;
import ganadinote.community.dto.PostRequestDTO;
import ganadinote.community.service.CommunityService;
import ganadinote.common.util.JwtTokenUtil;   // ★ JWT 유틸
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final FileMapper fileMapper;
    private final FileUtils fileUtils;
    private final JwtTokenUtil jwtTokenUtil;   // ★ 주입

    /* ─── 공통: Authorization 헤더에서 mbrCd(subject) 꺼내기 ─── */
    private String mbrCdFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String jwt = authHeader.substring(7);
        return jwtTokenUtil.getSubject(jwt); // 로그인 시 subject=mbrCd로 넣었음
    }

    // 목록/상세 (비보호)
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
        model.addAttribute("q", q);
        model.addAttribute("qTarget", qTarget);
        return "community/communityMainView";
    }

    @GetMapping("/api/list") @ResponseBody
    public List<PostListDTO> listApi(@RequestParam(required = false) Integer categoryId,
                                     @RequestParam(required = false, name="q") String q,
                                     @RequestParam(defaultValue = "title", name="qTarget") String qTarget,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(defaultValue = "0") Integer offset) {
        int page = (size > 0) ? (offset / size) + 1 : 1;
        Map<String, Object> data = communityService.getList(categoryId, q, qTarget, "ACTIVE", page, size);
        return (List<PostListDTO>) data.get("list");
    }

    @GetMapping("/postDetail")
    public String postDetail(@RequestParam int postId, Model model) {
        communityService.increaseViewCount(postId);
        var post = communityService.getPostDetail(postId);
        var comments = communityService.getComments(postId);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "community/postDetail";
    }

    // 글쓰기 화면 (뷰 접근 자체도 보호하고 싶으면 authHeader 체크)
    @GetMapping("/addPost")
    public String addPost() {
        return "community/addPostView";
    }

    // 2) 보호가 필요한 작업은 전부 /community/api/... 에서만 체크
    @PostMapping(path="/community/api/post", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<?> apiCreatePost(
            @RequestHeader(value="Authorization", required=false) String authHeader,
            PostRequestDTO req) {
        String mbrCd = mbrCdFromAuth(authHeader); // jwtTokenUtil.getSubject(...)
        if (mbrCd == null) return ResponseEntity.status(401).build();
        Long postId = communityService.createPost(req, mbrCd);
        return ResponseEntity.ok(Map.of("ok", true, "redirect", "/community/postDetail?postId="+postId));
    }

/*    // 글 수정(JSON)
    @PostMapping(path="/api/post/{postId}/edit", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiEditPost(
            @RequestHeader(value="Authorization", required=false) String authHeader,
            @PathVariable int postId, PostRequestDTO req) {
        String mbrCd = mbrCdFromAuth(authHeader);
        if (mbrCd == null) return ResponseEntity.status(401).build();
        boolean ok = communityService.updatePost(postId, mbrCd, req);
        return ok ? ResponseEntity.ok(Map.of("ok", true, "redirect", "/community/postDetail?postId="+postId))
                  : ResponseEntity.status(403).body(Map.of("ok", false, "msg", "forbidden"));
    }

    // 글 삭제(JSON)
    @PostMapping("/api/post/{postId}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiDeletePost(
            @RequestHeader(value="Authorization", required=false) String authHeader,
            @PathVariable int postId) {
        String mbrCd = mbrCdFromAuth(authHeader);
        if (mbrCd == null) return ResponseEntity.status(401).build();
        boolean ok = communityService.deletePost(postId, mbrCd);
        return ok ? ResponseEntity.ok(Map.of("ok", true, "redirect", "/community"))
                  : ResponseEntity.status(403).body(Map.of("ok", false, "msg", "forbidden"));
    }

    // 댓글 작성(JSON) – 기존 /community/comment 대신 fetch용
    @PostMapping(path="/api/comment", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiAddComment(
            @RequestHeader(value="Authorization", required=false) String authHeader,
            @RequestParam int postId,
            @RequestParam(required=false) Integer parentCmtId,
            @RequestParam String cmtCtnt) {
        String mbrCd = mbrCdFromAuth(authHeader);
        if (mbrCd == null) return ResponseEntity.status(401).build();
        communityService.addComment(postId, parentCmtId, mbrCd, cmtCtnt);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // 좋아요 토글(JSON)
    @PostMapping("/api/post/{postId}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiToggleLike(
            @RequestHeader(value="Authorization", required=false) String authHeader,
            @PathVariable int postId) {
        String mbrCd = mbrCdFromAuth(authHeader);
        if (mbrCd == null) return ResponseEntity.status(401).build();
        boolean liked = communityService.toggleLike(postId, mbrCd);
        return ResponseEntity.ok(Map.of("liked", liked));
    }*/

    // 썸머노트 업로드(JSON) – 보호
    @PostMapping(
    	    value = "/community/upload/summernote",
    	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    	    produces = MediaType.APPLICATION_JSON_VALUE
    	)
    	@ResponseBody
    	public ResponseEntity<Map<String, String>> uploadSummernote(
    	        @RequestParam("image") MultipartFile image,
    	        @RequestParam(value = "tempKey", required = false) String tempKey) {
    	    try {
    	        var meta = fileUtils.uploadFile(image, "community/editor");
    	        meta.setPostType("post");
    	        fileMapper.addfile(meta);
    	        return ResponseEntity.ok(Map.of("url", meta.getFilePath()));
    	    } catch (Exception e) {
    	        // 서버 로그 확인용
    	        e.printStackTrace();
    	        return ResponseEntity.status(500)
    	                .contentType(MediaType.APPLICATION_JSON)
    	                .body(Map.of("error", "upload_failed"));
    	    }
    	}
}