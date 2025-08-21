package ganadinote.main.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ganadinote.common.domain.Pet;
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
		log.info("getPetInfoByMbrCd 호출, mbrCd: {}", mbrCd);
		List<Pet> petList = mainService.getPetInfoByMbrCd(mbrCd);
		
		if(petList != null && !petList.isEmpty()) {
			model.addAttribute("petList", petList);
			log.info("가져온 반려동물 수: {}", petList.size());
		}else {
			model.addAttribute("petList", null);
			log.info("등록된 반려동물이 없습니다.");
		}
		
		return "mainView";
	}

}
