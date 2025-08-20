package ganadinote.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class userTestController {

	@GetMapping("/test")
    public String testList() {
        return "ex/application-chat";
    }
	
	@GetMapping("/main")
    public String mainList() {
        return "layout/mainLayoutView";
    }
	
}
