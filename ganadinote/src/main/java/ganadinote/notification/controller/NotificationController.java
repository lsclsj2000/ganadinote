package ganadinote.notification.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ganadinote.common.util.TokenUtils;
import ganadinote.main.service.MainService;
import ganadinote.notification.domain.PetWithBreedDTO;
import ganadinote.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
public class NotificationController {
	
	private final NotificationService notificationService;
	private final MainService mainService; 
	
	@Value("${vapid.public.key}")
    private String vapidPublicKey;
	
	@GetMapping("/notification/settings")
	public String getNotificationSettings(Model model) {
		log.info("VAPID Public Key: {}", vapidPublicKey);

		String mbrCdStr = TokenUtils.getMbrCd();
		
		// ✅ 수정된 부분: 로그인되지 않은 경우 /login으로 리다이렉트
		if (mbrCdStr == null || mbrCdStr.trim().isEmpty()) {
			log.warn("로그인되지 않은 사용자 접근. 로그인 페이지로 리다이렉트합니다.");
            return "redirect:/login";
        }

		Integer mbrCd = null;
		String mbrNknm = "게스트";
		
		log.info("로그인된 사용자의 회원 코드(mbrCdStr): {}", mbrCdStr);
		
		try {
		    mbrCd = Integer.parseInt(mbrCdStr);
		    String foundNknm = mainService.getNknmByMbrCd(mbrCd);
		    if (foundNknm != null) {
		        mbrNknm = foundNknm;
		    }
		} catch (NumberFormatException e) {
		    log.error("회원 코드(mbrCd)를 숫자로 변환하는 데 실패했습니다.", e);
		    // 예외 발생 시, 로그인 페이지로 리다이렉션 또는 에러 처리
		    return "redirect:/login";
		}
		
		List<PetWithBreedDTO> pets = notificationService.getPetInfoForNotification(mbrCdStr);
	    model.addAttribute("pets", pets);
		
		model.addAttribute("mbrCd", mbrCd);
		model.addAttribute("mbrNknm", mbrNknm);
		model.addAttribute("vapidPublicKey", vapidPublicKey);
		return "notification/notificationSettingView";
	}
	
}