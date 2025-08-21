package ganadinote.location.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LocationController {

	@GetMapping("/locationView")
	public String exLocation() {
		
		return "location/location2.html";
	}

	@GetMapping("/main")
	public String main() {
		
		return "location/main.html";
	}

	
}
