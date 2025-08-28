package ganadinote.notification.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ganadinote.notification.domain.PetWithBreedDTO;
import ganadinote.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
public class NotificationController {
	
	private final NotificationService notificationService;
	
	@Value("${vapid.public.key}")
    private String vapidPublicKey;
	
	@GetMapping("/push")
	public String pushPage() {
		return "push/pushView";
	}
	
	@GetMapping("/notification/settings")
	public String getNotificationSettings(Model model) {
		log.info("VAPID Public Key: {}", vapidPublicKey);

		String mbrCd = "1";		
		
		List<PetWithBreedDTO> pets = notificationService.getPetInfoForNotification(mbrCd);
	    model.addAttribute("pets", pets);
		
		model.addAttribute("mbrCd", mbrCd);
		model.addAttribute("vapidPublicKey", vapidPublicKey);
		return "notification/notificationSettingView";
	}
	
}
