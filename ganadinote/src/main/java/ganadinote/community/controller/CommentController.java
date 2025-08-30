package ganadinote.community.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ganadinote.common.util.JwtTokenUtil;
import ganadinote.community.dto.CommentDTO;
import ganadinote.community.dto.PostRequestDTO;
import ganadinote.community.service.CommentService;
import ganadinote.community.service.CommunityService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommunityService communityService;
    private final JwtTokenUtil jwtTokenUtil;

    /** Authorization 헤더에서 Bearer 토큰 추출 후 subject(mbrCd) 반환 */
    private String mbrCdFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String jwt = authHeader.substring(7);
        return jwtTokenUtil.getSubject(jwt);
    }

    /** 게시글별 댓글 목록 */
    @GetMapping
    public ResponseEntity<List<CommentDTO>> list(@RequestParam long postId,
                                                 @RequestParam(defaultValue = "50") int limit,
                                                 @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, limit, offset));
    }

    /** 댓글 생성 (원댓글/대댓글 겸용) */
    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                    @RequestParam long postId,
                                    // 이름 호환: parentId | commentParentId
                                    @RequestParam(required = false, name = "parentId") Long parentId,
                                    @RequestParam(required = false, name = "commentParentId") Long parentIdAlias,
                                    // 이름 호환: content | commentCtnt
                                    @RequestParam(required = false, name = "content") String content,
                                    @RequestParam(required = false, name = "commentCtnt") String contentAlias) {

        String mbrCd = mbrCdFromAuth(authHeader);
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Long finalParentId = (parentId != null) ? parentId : parentIdAlias;
        String finalContent = (content != null && !content.isBlank()) ? content : contentAlias;

        if (finalContent == null || finalContent.isBlank()) {
            return ResponseEntity.badRequest().body("내용을 입력하세요.");
        }

        CommentDTO saved = commentService.createComment(postId, finalParentId, mbrCd, finalContent);
        return ResponseEntity.ok(saved);
    }

    /** 자신의 댓글 삭제 (소프트 삭제 권장) */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                    @PathVariable("id") long id) {

        String mbrCd = mbrCdFromAuth(authHeader);
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        boolean ok = commentService.deleteOwnComment(id, mbrCd);
        if (ok) {
            return ResponseEntity.ok(Map.of("deleted", true));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
    }
    
    @PostMapping("/api/post")
    @ResponseBody
    public ResponseEntity<?> addPostApi(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @ModelAttribute PostRequestDTO req) {

        // JWT -> mbrCd
        String mbrCd = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            mbrCd = jwtTokenUtil.getSubject(authHeader.substring(7));
        }
        if (mbrCd == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Long postId = communityService.createPost(req, mbrCd);
        return ResponseEntity.ok(Map.of("postId", postId));
    }
}