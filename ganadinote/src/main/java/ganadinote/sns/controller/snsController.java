package ganadinote.sns.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.Member;
import ganadinote.sns.service.SnsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/sns")
@RequiredArgsConstructor
public class snsController {
	
	private final SnsService snsService;

	@GetMapping
	public String getSnsMainView(HttpSession session, Model model) {
	    Integer mbrCd = java.util.Optional.ofNullable((String) session.getAttribute("SCD"))
	            .map(Integer::valueOf)
	            .orElse(1);

	    var homePosts = snsService.getHomeFeed(mbrCd);
	    model.addAttribute("homePosts", homePosts);

	    model.addAttribute("initialTpl", "fragments/snsHomeFragment");
	    model.addAttribute("initialFrag", "snsHomeFragment");
	    return "layout/snsLayoutMainView";
	}

    private boolean isFetch(HttpServletRequest req) {
        String v = req.getHeader("X-Requested-With");
        return v != null && v.equalsIgnoreCase("fetch");
    }

    // Home
    @GetMapping("/home")
    public String getSnshomeView(HttpServletRequest req, HttpSession session, Model model,
            @RequestParam(defaultValue = "1") int page) {

        Integer mbrCd = Optional.ofNullable((String) session.getAttribute("SCD"))
                .map(Integer::valueOf)
                .orElse(2);

        var homePosts = snsService.getHomeFeed(mbrCd);
        model.addAttribute("homePosts", homePosts);

        if (isFetch(req)) {
            return "fragments/snsHomeFragment :: snsHomeFragment";
        }
        model.addAttribute("initialTpl", "fragments/snsHomeFragment");
        model.addAttribute("initialFrag", "snsHomeFragment");
        return "layout/snsLayoutMainView";
    }

    // Walking
    @GetMapping("/walking")
    public String getSnswalkingView(HttpServletRequest req, Model model,
                          @RequestParam(required = false) String region,
                          @RequestParam(defaultValue = "1") int page) {
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
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "m", required = false) Integer targetMbrCd
    ) {
        Integer loginMbrCd = Optional.ofNullable((String) session.getAttribute("SCD"))
                .map(Integer::valueOf)
                .orElse(1);

        Integer viewMbrCd = (targetMbrCd != null) ? targetMbrCd : loginMbrCd;
        boolean isOwner = loginMbrCd.equals(viewMbrCd);

        long postCount      = snsService.countPostsByMember(viewMbrCd);
        long followerCount  = snsService.countFollowersOfMember(viewMbrCd);
        long followingCount = snsService.countFollowingsByMember(viewMbrCd);
        var myPosts    = snsService.getMyFeedPosts(viewMbrCd);
        var followers  = snsService.getFollowers(viewMbrCd);
        var followings = snsService.getFollowings(viewMbrCd);

        model.addAttribute("postCount", postCount);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("myPosts", myPosts);
        model.addAttribute("followers", followers);
        model.addAttribute("followings", followings);

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("viewMbrCd", viewMbrCd);

        // 현재 팔로우 여부도 내려주기
        if (!isOwner) {
            boolean isFollowing = snsService.isFollowing(loginMbrCd, viewMbrCd);
            model.addAttribute("isFollowing", isFollowing);
        }

        if (isFetch(req)) {
            return "fragments/snsMyfeedFragment :: snsMyfeedFragment";
        }
        model.addAttribute("initialTpl", "fragments/snsMyfeedFragment");
        model.addAttribute("initialFrag", "snsMyfeedFragment");
        return "layout/snsLayoutMainView";
    }
    
    @PostMapping("/api/follow/toggle")
    @ResponseBody
    public Map<String, Object> toggleFollow(@RequestBody Map<String, Integer> body, HttpSession session) {
        Integer loginMbrCd = Optional.ofNullable((String) session.getAttribute("SCD"))
                .map(Integer::valueOf)
                .orElse(1);
        Integer target = body.get("targetMbrCd");
        if (target == null || target <= 0) return Map.of("ok", false, "message", "잘못된 대상");

        if (loginMbrCd.equals(target)) return Map.of("ok", false, "message", "본인은 팔로우할 수 없습니다.");

        boolean following = snsService.toggleFollow(loginMbrCd, target);
        return Map.of("ok", true, "following", following);
    }
    
    // add sns post
    @GetMapping("/addSnsPost")
    public String addSnsPost() {
        return "sns/addSnsPostView";
    }
    
    
    
    @PostMapping(value = "/api/posts", consumes = {"multipart/form-data"})
    @ResponseBody
    public Map<String, Object> createPost(
            @RequestParam(value="content", required=false, defaultValue="") String content,
            @RequestParam(value="tags", required=false) String tags,
            @RequestPart(value="images", required=false) MultipartFile[] images,
            HttpSession session
    ) {
        Integer mbrCd = java.util.Optional.ofNullable((String) session.getAttribute("SCD"))
                .map(Integer::valueOf)
                .orElse(1);

        // ✅ 사진 필수
        if (images == null || images.length == 0) {
            return Map.of("ok", false, "message", "사진을 최소 1장 업로드해 주세요.");
        }

        Integer spCd = snsService.createPost(content, mbrCd, images);
        return Map.of("ok", true, "sp_cd", spCd);
    }
    
    // update sns profile
    @GetMapping("/updateProfile")
    public String updateProfile(HttpSession session, Model model) {
    	Integer mbrCd = java.util.Optional.ofNullable((String) session.getAttribute("SCD"))
    			.map(Integer::valueOf)
    			.orElse(1); // 디폴트는 개발용
    	
    	Member me = snsService.getMemberProfile(mbrCd);
    	model.addAttribute("updatePf", me);
    	return "sns/updateProfileView";
    }
    
    // 닉네임 중복 체크
    @GetMapping("/api/profile/check-nickname")
    @ResponseBody
    public Map<String, Object> checkNickname(
            @RequestParam("mbrNknm") String mbrNknm,
            HttpSession session) {
        Integer mbrCd = java.util.Optional.ofNullable((String) session.getAttribute("SCD"))
                .map(Integer::valueOf)
                .orElse(1);
        boolean duplicate = snsService.isNicknameDuplicate(mbrNknm, mbrCd); // 자기 자신 제외
        return Map.of("duplicate", duplicate);
    }

    // 프로필 저장 (닉네임/이미지 중 변경된 것만 반영)
    @PostMapping(value = "/api/profile", consumes = {"multipart/form-data"})
    @ResponseBody
    public Map<String, Object> updateProfile(
            @RequestParam(value = "mbrNknm", required = false) String mbrNknm,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            HttpSession session) {

        Integer mbrCd = java.util.Optional.ofNullable((String) session.getAttribute("SCD"))
                .map(Integer::valueOf)
                .orElse(1);

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
    
    // 비밀번호 유효성 검증
    @GetMapping("/api/profile/check-current-password")
    @ResponseBody
    public Map<String, Object> checkCurrentPassword(
            @RequestParam("pw") String pw,
            HttpSession session) {
        Integer mbrCd = java.util.Optional.ofNullable((String) session.getAttribute("SCD"))
                .map(Integer::valueOf)
                .orElse(1);
        boolean ok = snsService.checkCurrentPassword(mbrCd, pw);
        return Map.of("ok", ok);
    }

    // 비밀번호 변경
    @PostMapping("/api/profile/password")
    @ResponseBody
    public Map<String, Object> changePassword(
            @RequestBody Map<String, String> body,
            HttpSession session) {
        Integer mbrCd = java.util.Optional.ofNullable((String) session.getAttribute("SCD"))
                .map(Integer::valueOf)
                .orElse(1);

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
    
    
}
