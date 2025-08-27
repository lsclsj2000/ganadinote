package ganadinote.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ganadinote.main.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {
	
	private final MainService mainService;
	
	@GetMapping("/ganadiMain")
	public String getPetInfoByMbrCd(Model model) {		
		String mbrCd = "1";
		
		return "mainView";
	}

}
