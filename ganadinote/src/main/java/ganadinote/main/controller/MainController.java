package ganadinote.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ganadinote.common.util.TokenUtils;
import ganadinote.main.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {
	
	private final MainService mainService;
	// ⭐️ 날씨, 위치 서비스는 필요 없으므로 제거
	
	@GetMapping("/weather")
	public String weatherView(Model model) {
		String mbrCdStr = TokenUtils.getMbrCd();
		String mbrNknm = "게스트"; 
		
		if (mbrCdStr != null && !mbrCdStr.trim().isEmpty()) {
			try {
				Integer mbrCd = Integer.parseInt(mbrCdStr);
				log.info("현재 로그인된 회원코드:{}", mbrCdStr);
				String foundNknm = mainService.getNknmByMbrCd(mbrCd);
				if (foundNknm != null) {
					mbrNknm = foundNknm;
				}
			} catch (NumberFormatException e) {
				log.error("회원 코드(mbrCd)를 숫자로 변환하는 데 실패했습니다.", e);
			}
		}

		model.addAttribute("mbrNknm", mbrNknm);
		
		return "weather/weatherView";
	}
}