package ganadinote.sns.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/sns")
public class snsController {

	// 셸(레이아웃) 페이지 - 최초 진입
    @GetMapping
    public String getSnsMainView() {
        return "sns/snsLayoutMainView"; // 지금 올려준 레이아웃 파일
    }

    // Home 프래그먼트
    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam(defaultValue = "1") int page) {
        // model.addAttribute("posts", feedService.followingRecent(page)); // 필요 시 주석 해제
        return "sns/snsHomeFragment :: snsHomeFragment";
    }

    // Walking 프래그먼트
    @GetMapping("/walking")
    public String walking(Model model,
                          @RequestParam(required = false) String region,
                          @RequestParam(defaultValue = "1") int page) {
        // model.addAttribute("crews", feedService.recruitingCrews(region, page));
        // model.addAttribute("region", region);
        return "sns/snsWalkingFragment :: snsWalkingFragment";
    }

    // MyFeed 프래그먼트
    @GetMapping("/myfeed")
    public String myfeed(Model model,
                         @RequestParam(defaultValue = "1") int page/*, Principal principal*/) {
        // model.addAttribute("posts", feedService.myPosts(principal.getName(), page));
        return "sns/snsMyfeedFragment :: snsMyfeedFragment";
    }
}
