package ganadinote.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ganadinote.common.domain.Pet;
import ganadinote.common.util.TokenUtils;
import ganadinote.main.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mainapi")
@Log4j2
public class MainAPIController {
	
	private final MainService mainService;
	@GetMapping("/pets")
	public ResponseEntity<List<Pet>> getPetInfoByMbrCd(){
		String mbrCdStr = TokenUtils.getMbrCd();
		
		if (mbrCdStr == null || mbrCdStr.trim().isEmpty()) {
			log.error("토큰에 유효한 회원 코드(mbrCd)가 없습니다.");
			// 보안상 중요한 API이므로 401 Unauthorized 상태 코드를 반환하는 것이 좋습니다.
			return ResponseEntity.status(401).build(); 
		}
		
		try {
			Integer mbrCd = Integer.parseInt(mbrCdStr);
			List<Pet> petList = mainService.getPetInfoByMbrCd(mbrCd);
			return ResponseEntity.ok(petList);
		} catch (NumberFormatException e) {
			log.error("회원 코드(mbrCd)를 숫자로 변환하는 데 실패했습니다.", e);
			return ResponseEntity.status(400).build(); // Bad Request
		}
	}
}