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
import ganadinote.community.dto.PostDetailDTO;
import ganadinote.community.dto.PostListDTO;
import ganadinote.community.dto.PostRequestDTO;
import ganadinote.community.dto.CommentDTO;
import ganadinote.community.service.CommunityService;
import ganadinote.community.service.CommentService;        // ✅ 댓글 서비스
import ganadinote.common.util.JwtTokenUtil;               // (쿼리 등에서 쓰면 유지)
import ganadinote.common.util.TokenUtils;                 // ✅ 동료가 만든 Util
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final CommentService commentService;          // ✅ 주입
    private final FileMapper fileMapper;
    private final FileUtils fileUtils;
    private final JwtTokenUtil jwtTokenUtil;              // (필요 시 유지)

    // ✅ 공통 헬퍼: 현재 로그인 mbrCd를 Integer로
    private Integer currentMbrCd() {
        String s = TokenUtils.getMbrCd();                // 🔐 동료 util 한 줄
        if (s == null || s.isBlank()) return null;
        try { return Integer.valueOf(s); } catch (Exception e) { return null; }
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

        List<CommentDTO> comments = commentService.getComments(postId);   // ✅ 댓글 조회
        Integer loginMbrCd = currentMbrCd();                              // ✅ 로그인 사용자(없으면 null)

        model.addAttribute("loginMbrCd", loginMbrCd);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "community/postDetail";
    }

    // 글쓰기 화면
    @GetMapping("/addPost")
    public String addPost() {
        return "community/addPostView";
    }

    // 글 등록 (보호)
    @PostMapping(path="/community/api/post", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<?> apiCreatePost(PostRequestDTO req) {
        Integer mbrCd = currentMbrCd();
        if (mbrCd == null) return ResponseEntity.status(401).build();
        Long postId = communityService.createPost(req, String.valueOf(mbrCd));
        return ResponseEntity.ok(Map.of("ok", true, "redirect", "/community/postDetail?postId="+postId));
    }

    // 글 수정 (보호, 본인만)
    @PostMapping(path="/api/post/{postId}/edit", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiEditPost(@PathVariable int postId, PostDetailDTO req) {
        Integer mbrCd = currentMbrCd();
        if (mbrCd == null) return ResponseEntity.status(401).build();
        req.setPostId(postId);
        try {
            communityService.updatePost(req, mbrCd);
            return ResponseEntity.ok(Map.of("ok", true, "redirect", "/community/postDetail?postId="+postId));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("ok", false, "msg", e.getMessage()));
        }
    }

    // 글 삭제 (보호, 본인만)
    @PostMapping("/api/post/{postId}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiDeletePost(@PathVariable int postId) {
        Integer mbrCd = currentMbrCd();
        if (mbrCd == null) return ResponseEntity.status(401).build();
        try {
            communityService.deletePost(postId, mbrCd);
            return ResponseEntity.ok(Map.of("ok", true, "redirect", "/community"));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("ok", false, "msg", e.getMessage()));
        }
    }

    // 글 수정 화면 (뷰만 리턴)
    @GetMapping("/updatePost")
    public String updatePostView(@RequestParam int postId, Model model) {
        var post = communityService.getPostDetail(postId);
        model.addAttribute("post", post);
        return "community/updatePostView";
    }

    // 본인 글인지 체크 (보호)
    @GetMapping("/api/post/{postId}/isOwner")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> isOwner(@PathVariable int postId) {
        Integer mbrCd = currentMbrCd();
        if (mbrCd == null) return ResponseEntity.status(401).body(Map.of("ok", false));
        boolean owner = communityService.isOwner(postId, mbrCd);
        return owner ? ResponseEntity.ok(Map.of("ok", true, "owner", true))
                     : ResponseEntity.status(403).body(Map.of("ok", false, "owner", false));
    }

    // 댓글 작성 (보호)
    @PostMapping(path="/api/comment", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiAddComment(@RequestParam int postId,
                                                             @RequestParam(required=false, name="commentParentId") Integer parentCmtId,
                                                             @RequestParam(name="commentCtnt") String cmtCtnt) {
        Integer mbrCd = currentMbrCd();
        if (mbrCd == null) return ResponseEntity.status(401).build();
        commentService.addComment(postId, parentCmtId, mbrCd, cmtCtnt);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // 댓글 삭제 (보호, 본인만)
    @PostMapping("/api/comment/{commentId}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiDeleteComment(@PathVariable int commentId) {
        Integer mbrCd = currentMbrCd();
        if (mbrCd == null) return ResponseEntity.status(401).build();
        try {
            commentService.deleteComment(commentId, mbrCd);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("ok", false, "msg", e.getMessage()));
        }
    }

    // 썸머노트 업로드 (보호 여부는 정책에 맞게)
    @PostMapping(
        value = "/upload/summernote",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadSummernote(@RequestParam("image") MultipartFile image,
                                                                @RequestParam(value = "tempKey", required = false) String tempKey) {
        try {
            var meta = fileUtils.uploadFile(image, "community/editor");
            meta.setPostType("post");
            fileMapper.addfile(meta);
            return ResponseEntity.ok(Map.of("url", meta.getFilePath()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("error", "upload_failed"));
        }
    }
    
    
    @PostMapping("/api/post/{postId}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable int postId) {
        String mbrCdStr = TokenUtils.getMbrCd();
        if (mbrCdStr == null) return ResponseEntity.status(401).build();

        int mbrCd = Integer.parseInt(mbrCdStr);
        try {
            boolean liked = communityService.toggleLike(postId, mbrCd);
            int count = communityService.getLikeCount(postId);
            return ResponseEntity.ok(Map.of("liked", liked, "count", count));
        } catch (IllegalStateException ise) {
            // 자기 글 좋아요 금지
            return ResponseEntity.status(403).body(Map.of("ok", false, "msg", ise.getMessage()));
        }
    }

    @GetMapping("/api/post/{postId}/like/state")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likeState(@PathVariable int postId) {
        int count = communityService.getLikeCount(postId);
        String mbrCdStr = TokenUtils.getMbrCd(); // 비로그인일 수 있음
        if (mbrCdStr == null) return ResponseEntity.ok(Map.of("liked", false, "count", count));

        int mbrCd = Integer.parseInt(mbrCdStr);
        boolean liked = communityService.hasLiked(postId, mbrCd);
        return ResponseEntity.ok(Map.of("liked", liked, "count", count));
    }
    
    
}
