package ganadinote.sns.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.Member;
import ganadinote.common.util.TokenUtils;
import ganadinote.sns.service.SnsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/sns")
@RequiredArgsConstructor
@Log4j2
public class snsController {

    private final SnsService snsService; 

    /** 로그인 미완료 시 던지는 예외 (모든 핸들러에서 공통 처리) */
    private static class NotLoggedInException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    /** 로그인 검증: 로그인 안 되어 있으면 로그인 페이지로 리다이렉트 */
    private Integer requireLoginOrRedirect() {
        String mbrStr = TokenUtils.getMbrCd();
        if (mbrStr == null) {
            throw new NotLoggedInException();
        }
        return Integer.valueOf(mbrStr);
    }

    /** 모든 미로그인 예외는 동일하게 리다이렉트 */
    @ExceptionHandler(NotLoggedInException.class)
    public void handleNotLoggedIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 현재 요청의 도메인/프로토콜을 유지하고 앱 컨텍스트를 고려
        String loginPath = request.getContextPath() + "/login";
        response.sendRedirect(loginPath);
    }

    private boolean isFetch(HttpServletRequest req) {
        String v = req.getHeader("X-Requested-With");
        return v != null && v.equalsIgnoreCase("fetch");
    }

    // 메인
    @GetMapping
    public String getSnsMainView(Model model) {
        Integer mbrCd = requireLoginOrRedirect();

        var homePosts = snsService.getHomeFeed(mbrCd);
        model.addAttribute("homePosts", homePosts);
        model.addAttribute("loginMbrCd", mbrCd);

        model.addAttribute("initialTpl", "fragments/snsHomeFragment");
        model.addAttribute("initialFrag", "snsHomeFragment");
        return "layout/snsLayoutMainView";
    }

    // Home
    @GetMapping("/home")
    public String getSnshomeView(HttpServletRequest req, Model model,
                                 @RequestParam(defaultValue = "1") int page) {
        Integer mbrCd = requireLoginOrRedirect();

        var homePosts = snsService.getHomeFeed(mbrCd);
        model.addAttribute("homePosts", homePosts);
        model.addAttribute("loginMbrCd", mbrCd);

        if (isFetch(req)) {
            return "fragments/snsHomeFragment :: snsHomeFragment";
        }
        model.addAttribute("initialTpl", "fragments/snsHomeFragment");
        model.addAttribute("initialFrag", "snsHomeFragment");
        return "layout/snsLayoutMainView";
    }

    // Walking (공개로 둘 거면 requireLoginOrRedirect() 제거)
    @GetMapping("/walking")
    public String getSnswalkingView(HttpServletRequest req, Model model,
                                    @RequestParam(required = false) String region,
                                    @RequestParam(defaultValue = "1") int page) {
        Integer mbrCd = requireLoginOrRedirect(); // 로그인 필수로 통일

        if (isFetch(req)) {
            return "fragments/snsWalkingFragment :: snsWalkingFragment";
        }
        model.addAttribute("initialTpl", "fragments/snsWalkingFragment");
        model.addAttribute("initialFrag", "snsWalkingFragment");
        return "layout/snsLayoutMainView";
    }

    // MyFeed
    @GetMapping("/myfeed")
    public String getSnsmyfeedView(
            HttpServletRequest req,
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "m", required = false) Integer targetMbrCd
    ) {
        Integer loginMbrCd = requireLoginOrRedirect();

        Integer viewMbrCd = (targetMbrCd != null) ? targetMbrCd : loginMbrCd;
        boolean isOwner = loginMbrCd.equals(viewMbrCd);

        long postCount      = snsService.countPostsByMember(viewMbrCd);
        long followerCount  = snsService.countFollowersOfMember(viewMbrCd);
        long followingCount = snsService.countFollowingsByMember(viewMbrCd);
        var myPosts    = snsService.getMyFeedPosts(viewMbrCd);
        var followers  = snsService.getFollowers(viewMbrCd);
        var followings = snsService.getFollowings(viewMbrCd);

        // 1) 프로필 조회 + 표시용 값 계산
        var profile = snsService.getMemberProfile(viewMbrCd);
        String displayName = "사용자";
        String profileImg = "/assets/img/avatar-default.png";

        if (profile != null) {
            if (profile.getMbrProfile() != null && !profile.getMbrProfile().isBlank()) {
                profileImg = profile.getMbrProfile();
            }
            if (profile.getMbrNknm() != null && !profile.getMbrNknm().isBlank()) {
                displayName = profile.getMbrNknm();
            } else if (profile.getMbrEmail() != null && !profile.getMbrEmail().isBlank()) {
                int at = profile.getMbrEmail().indexOf('@');
                displayName = (at >= 0) ? profile.getMbrEmail().substring(0, at) : profile.getMbrEmail();
            }
        }

        // 2) 모델 주입
        model.addAttribute("profile", profile);
        model.addAttribute("displayName", displayName);
        model.addAttribute("profileImg", profileImg);

        model.addAttribute("postCount", postCount);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("myPosts", myPosts);
        model.addAttribute("followers", followers);
        model.addAttribute("followings", followings);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("viewMbrCd", viewMbrCd);
        model.addAttribute("loginMbrCd", loginMbrCd);

        if (!isOwner) {
            boolean isFollowing = snsService.isFollowing(loginMbrCd, viewMbrCd);
            model.addAttribute("isFollowing", isFollowing);
        }

        // 3) 프래그먼트/레이아웃 분기
        if (isFetch(req)) {
            return "fragments/snsMyfeedFragment :: snsMyfeedFragment";
        }
        model.addAttribute("initialTpl", "fragments/snsMyfeedFragment");
        model.addAttribute("initialFrag", "snsMyfeedFragment");
        return "layout/snsLayoutMainView";
    }

    // 팔로우 토글 (API) — 미로그인도 동일하게 리다이렉트
    @PostMapping("/api/follow/toggle")
    @ResponseBody
    public Map<String, Object> toggleFollow(@RequestBody Map<String, Integer> body) {
        Integer loginMbrCd = requireLoginOrRedirect();
        

        Integer target = body.get("targetMbrCd");
        if (target == null || target <= 0) return Map.of("ok", false, "message", "잘못된 대상");

        if (loginMbrCd.equals(target)) return Map.of("ok", false, "message", "본인은 팔로우할 수 없습니다.");

        boolean following = snsService.toggleFollow(loginMbrCd, target);
        return Map.of("ok", true, "following", following);
    }

    // 게시물 작성 View
    @GetMapping("/addSnsPost")
    public String addSnsPost() {
        requireLoginOrRedirect();
        return "sns/addSnsPostView";
    }

    // 게시물 생성 (API)
    @PostMapping(value = "/api/posts", consumes = {"multipart/form-data"})
    @ResponseBody
    public Map<String, Object> createPost(
            @RequestParam(value="content", required=false, defaultValue="") String content,
            @RequestParam(value="tags", required=false) String tags,
            @RequestPart(value="images", required=false) MultipartFile[] images
    ) {
        Integer mbrCd = requireLoginOrRedirect();

        // ✅ 사진 필수
        if (images == null || images.length == 0) {
            return Map.of("ok", false, "message", "사진을 최소 1장 업로드해 주세요.");
        }

        Integer spCd = snsService.createPost(content, mbrCd, images);
        return Map.of("ok", true, "sp_cd", spCd);
    }

    // 프로필 수정 View
    @GetMapping("/updateProfile")
    public String updateProfile(Model model) {
        Integer mbrCd = requireLoginOrRedirect();

        Member me = snsService.getMemberProfile(mbrCd);
        model.addAttribute("updatePf", me);
        return "sns/updateProfileView";
    }

    // 닉네임 중복 체크 (API)
    @GetMapping("/api/profile/check-nickname")
    @ResponseBody
    public Map<String, Object> checkNickname(@RequestParam("mbrNknm") String mbrNknm) {
        Integer mbrCd = requireLoginOrRedirect();
        boolean duplicate = snsService.isNicknameDuplicate(mbrNknm, mbrCd); // 자기 자신 제외
        return Map.of("duplicate", duplicate);
    }

    // 프로필 저장 (API)
    @PostMapping(value = "/api/profile", consumes = {"multipart/form-data"})
    @ResponseBody
    public Map<String, Object> updateProfile(
            @RequestParam(value = "mbrNknm", required = false) String mbrNknm,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        Integer mbrCd = requireLoginOrRedirect();

        try {
            int changed = snsService.updateProfile(mbrCd, mbrNknm, profileImage);
            if (changed == 0) {
                return Map.of("ok", false, "message", "수정내용이 없습니다.");
            }
            return Map.of("ok", true);
        } catch (org.springframework.dao.DuplicateKeyException dup) {
            return Map.of("ok", false, "message", "이미 사용 중인 닉네임입니다.");
        } catch (IllegalArgumentException bad) {
            return Map.of("ok", false, "message", bad.getMessage());
        } catch (Exception e) {
            return Map.of("ok", false, "message", "서버 오류가 발생했습니다.");
        }
    }

    // 비밀번호 유효성 검증 (API)
    @GetMapping("/api/profile/check-current-password")
    @ResponseBody
    public Map<String, Object> checkCurrentPassword(@RequestParam("pw") String pw) {
        Integer mbrCd = requireLoginOrRedirect();
        boolean ok = snsService.checkCurrentPassword(mbrCd, pw);
        return Map.of("ok", ok);
    }

    // 비밀번호 변경 (API)
    @PostMapping("/api/profile/password")
    @ResponseBody
    public Map<String, Object> changePassword(@RequestBody Map<String, String> body) {
        Integer mbrCd = requireLoginOrRedirect();

        String currentPassword = body.getOrDefault("currentPassword", "");
        String newPassword     = body.getOrDefault("newPassword", "");

        try {
            snsService.changePassword(mbrCd, currentPassword, newPassword);
            return Map.of("ok", true);
        } catch (IllegalArgumentException e) {
            return Map.of("ok", false, "message", e.getMessage());
        } catch (Exception e) {
            return Map.of("ok", false, "message", "서버 오류가 발생했습니다.");
        }
    }

    // 게시물 삭제 (API)
    @PostMapping("/api/posts/delete")
    @ResponseBody
    public Map<String, Object> deletePost(@RequestBody Map<String, Integer> body) {
        Integer loginMbrCd = requireLoginOrRedirect();

        Integer spCd = body.get("spCd");
        if (spCd == null || spCd <= 0) {
            return Map.of("ok", false, "message", "잘못된 게시물입니다.");
        }

        try {
            snsService.deletePost(loginMbrCd, spCd);
            return Map.of("ok", true);
        } catch (IllegalArgumentException e) {
            return Map.of("ok", false, "message", e.getMessage());
        } catch (Exception e) {
            return Map.of("ok", false, "message", "삭제 중 오류가 발생했습니다.");
        }
    }

    // 게시물 상세 (API)
    @GetMapping("/api/posts/detail")
    @ResponseBody
    public Map<String, Object> getPostDetail(@RequestParam("spCd") Integer spCd) {
        Integer viewer = requireLoginOrRedirect();

        if (spCd == null || spCd <= 0) {
            return Map.of("ok", false, "message", "잘못된 게시물입니다.");
        }

        var dto = snsService.getPostDetail(viewer, spCd);
        if (dto == null) return Map.of("ok", false, "message", "게시물이 존재하지 않습니다.");

        return Map.of("ok", true, "post", dto);
    }
    
    // 게시물 좋아요
    @PostMapping("/api/like/toggle")
    @ResponseBody
    public Map<String, Object> toggleLike(@RequestBody Map<String, Integer> body) {
        Integer me = requireLoginOrRedirect();
        Integer spCd = body.get("spCd");
        if (spCd == null || spCd <= 0) {
            return Map.of("ok", false, "message", "잘못된 게시물입니다.");
        }

        boolean liked = snsService.toggleLike(me, spCd);
        long likeCount = snsService.getLikeCount(spCd);

        return Map.of("ok", true, "liked", liked, "likeCount", likeCount);
    }
}
