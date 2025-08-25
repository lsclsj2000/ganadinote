package ganadinote.sns.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
    public String getSnsMainView(Model model) {
        // 기본 진입은 홈 프래그먼트를 초기 주입
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
    public String getSnshomeView(HttpServletRequest req, Model model,
                       @RequestParam(defaultValue = "1") int page) {
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
    public String getSnsmyfeedView(HttpServletRequest req, Model model,
                         @RequestParam(defaultValue = "1") int page) {
        if (isFetch(req)) {
            return "fragments/snsMyfeedFragment :: snsMyfeedFragment";
        }
        model.addAttribute("initialTpl", "fragments/snsMyfeedFragment");
    	model.addAttribute("initialFrag", "snsMyfeedFragment");
        return "layout/snsLayoutMainView";
    }
    
    // add sns post
    @GetMapping("/addSnsPost")
    public String addSnsPost() {
        return "sns/addSnsPostView";
    }
    
    
    @PostMapping(value = "/api/posts", consumes = {"multipart/form-data"})
    @ResponseBody
    public Map<String, Object> createPost(
            @RequestParam("content") String content,
            @RequestParam(value="tags", required=false) String tags,
            @RequestPart(value="images", required=false) MultipartFile[] images,
            HttpSession session
    ) {
        String scd = (String) session.getAttribute("SCD"); // 로그인 세션키 확인
        if (scd == null) throw new RuntimeException("로그인이 필요합니다.");
        Integer mbrCd = Integer.valueOf(scd);

        Integer spCd = snsService.createPost(content, mbrCd, images);
        return Map.of("ok", true, "sp_cd", spCd);
    }
}
