package ganadinote.sns.controller;

import jakarta.servlet.http.HttpServletRequest; // jakarta 패키지 사용 시
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sns")
public class snsController {

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
    public String home(HttpServletRequest req, Model model,
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
    public String walking(HttpServletRequest req, Model model,
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
    public String myfeed(HttpServletRequest req, Model model,
                         @RequestParam(defaultValue = "1") int page) {
        if (isFetch(req)) {
            return "fragments/snsMyfeedFragment :: snsMyfeedFragment";
        }
        model.addAttribute("initialTpl", "fragments/snsMyfeedFragment");
    	model.addAttribute("initialFrag", "snsMyfeedFragment");
        return "layout/snsLayoutMainView";
    }
}
