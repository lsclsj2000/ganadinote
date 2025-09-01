package ganadinote.community.controller;

import ganadinote.common.domain.FileMetaData;
import ganadinote.common.file.FileMapper;
import ganadinote.common.file.FileUtils;
import ganadinote.common.util.JwtTokenUtil;
import ganadinote.community.dto.PostRequestDTO;
import ganadinote.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * /api/community/** 로 들어오는 요청만 처리.
 * 동료의 loginInfo.js 가 /api/** 에 Authorization 헤더를 자동 부착한다고 가정.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityApiController {

    private final CommunityService communityService;
    private final JwtTokenUtil jwtTokenUtil;
    private final FileUtils fileUtils;
    private final FileMapper fileMapper;

    /** Authorization: Bearer <jwt> 에서 mbrCd(subject) 추출 */
    private String mbrCdFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return jwtTokenUtil.getSubject(authHeader.substring(7));
    }

    /** 게시글 생성 (FormData 전송) */
    @PostMapping(
            value = "/post",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> createPost(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @ModelAttribute PostRequestDTO req
    ) {
        String mbrCd = mbrCdFromAuth(authHeader);
        if (mbrCd == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        Long postId = communityService.createPost(req, mbrCd);
        return ResponseEntity.ok(Map.of("postId", postId));
    }

    /** 섬머노트 이미지 업로드 */
    @PostMapping(
            value = "/upload/summernote",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> uploadSummernote(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("image") MultipartFile image
    ) {
        String mbrCd = mbrCdFromAuth(authHeader);
        if (mbrCd == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        FileMetaData meta = fileUtils.uploadFile(image, "community/editor");
        meta.setPostType("post");
        fileMapper.addfile(meta);

        return ResponseEntity.ok(Map.of("url", meta.getFilePath()));
    }
}
