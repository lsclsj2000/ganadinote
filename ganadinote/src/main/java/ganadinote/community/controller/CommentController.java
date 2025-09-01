// src/main/java/ganadinote/community/controller/CommentController.java
package ganadinote.community.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ganadinote.common.util.TokenUtils;           // ✅ 동료 util
import ganadinote.community.dto.CommentDTO;
import ganadinote.community.service.CommentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    /** 게시글별 댓글 목록 */
    @GetMapping
    public ResponseEntity<List<CommentDTO>> list(@RequestParam long postId,
                                                 @RequestParam(defaultValue = "50") int limit,
                                                 @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, limit, offset));
    }

    /** 댓글 생성 (원댓글/대댓글 겸용) */
    @PostMapping
    public ResponseEntity<?> create(@RequestParam long postId,
                                    // 이름 호환: parentId | commentParentId
                                    @RequestParam(required = false, name = "parentId") Long parentId,
                                    @RequestParam(required = false, name = "commentParentId") Long parentIdAlias,
                                    // 이름 호환: content | commentCtnt
                                    @RequestParam(required = false, name = "content") String content,
                                    @RequestParam(required = false, name = "commentCtnt") String contentAlias) {

        String mbrCd = TokenUtils.getMbrCd();     // ✅ 한 줄로 회원코드
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

    /** 자신의 댓글 삭제 (소프트 삭제) */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {

        String mbrCd = TokenUtils.getMbrCd();     // ✅ 한 줄로 회원코드
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        boolean ok = commentService.deleteOwnComment(id, mbrCd);
        if (ok) return ResponseEntity.ok(Map.of("deleted", true));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
    }

    // ❌ 아래 게시글 등록 API는 커뮤니티 컨트롤러로 유지/이관하세요.
    // @PostMapping("/api/post") ...  ← 제거 권장
}
