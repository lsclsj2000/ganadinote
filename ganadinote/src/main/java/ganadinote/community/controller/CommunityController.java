package ganadinote.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/community")
public class CommunityController {
	
	@GetMapping("")
	public String getCommunityMain() {
		return "community/communityMainView";
	}
	@GetMapping("/addPost")
	public String addPost() {
		return "community/addPostView";
	}
	
	@GetMapping("/postDetail")
	public String postDetail() {
		return "community/postDetail";
	}
	
}
