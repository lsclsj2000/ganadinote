package ganadinote.petCard.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.util.TokenUtils;
import ganadinote.petCard.service.PetCardService;
import ganadinote.sns.service.SnsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/petCard")
@RequiredArgsConstructor
@Log4j2
public class petCardController {
	
	private final PetCardService petCardService;
	private final SnsService snsService; 

    private static class NotLoggedInException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private Integer requireLoginOrRedirect() {
        String mbrStr = TokenUtils.getMbrCd();
        if (mbrStr == null) throw new NotLoggedInException();
        return Integer.valueOf(mbrStr);
    }

    /** 모든 미로그인 예외는 동일하게 리다이렉트 */
    @ExceptionHandler(NotLoggedInException.class)
    public void handleNotLoggedIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 현재 요청의 도메인/프로토콜을 유지하고 앱 컨텍스트를 고려
        String loginPath = request.getContextPath() + "/login";
        response.sendRedirect(loginPath);
    }

    @GetMapping
    public String getPetfileCardList(Model model,
    								 @RequestParam(name = "m", required = false) Integer targetMbrCd) {
		Integer loginMbrCd = requireLoginOrRedirect();
		
		Integer ownerMbrCd = (targetMbrCd != null) ? targetMbrCd : loginMbrCd;
		boolean isOwner = loginMbrCd.equals(ownerMbrCd);
		
		var header = petCardService.getHeader(ownerMbrCd);
		var petCards = petCardService.getPetCards(ownerMbrCd);
		
		model.addAttribute("header", header);
		model.addAttribute("petCards", petCards);
		model.addAttribute("isOwner", isOwner);
		model.addAttribute("viewMbrCd", ownerMbrCd);
		
		// ✅ 내 카드가 아니면 팔로우 상태 내려주기
		if (!isOwner) {
		boolean isFollowing = snsService.isFollowing(loginMbrCd, ownerMbrCd);
		model.addAttribute("isFollowing", isFollowing);
		}
		
		return "petCard/petCardView";
	}
    
    @GetMapping("/updatePetCard")
    public String updatePetCardView(Model model,
                                    @RequestParam(name = "m", required = false) Integer targetMbrCd) {
        Integer loginMbrCd = requireLoginOrRedirect();

        // 내가 보려는 대상(없으면 본인)
        Integer ownerMbrCd = (targetMbrCd != null) ? targetMbrCd : loginMbrCd;
        boolean isOwner = loginMbrCd.equals(ownerMbrCd);
        if (!isOwner) {
            // 본인만 수정 가능
            return "redirect:/petCard?m=" + ownerMbrCd; 
        }

        // 화면에 필요한 데이터 주입
        var header   = petCardService.getHeader(ownerMbrCd);
        var petCards = petCardService.getPetCards(ownerMbrCd);
        var allTags  = petCardService.getAllTags(); // NEW: 시스템 전체 태그 (코드/이름)

        model.addAttribute("header", header);
        model.addAttribute("petCards", petCards);
        model.addAttribute("allTags", allTags);
        model.addAttribute("isOwner", true);
        model.addAttribute("viewMbrCd", ownerMbrCd);

        return "petCard/updatePetCardView"; // ← 당신의 수정 페이지 템플릿
    }

    // NEW: 단일 저장 API (이미지/소개/태그 중 넘어온 것만 갱신)
    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateOneCard(
            @RequestParam("cardId") Integer cardId,
            @RequestParam(value = "petId", required = false) Integer petId, // ★ 추가
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "introduction", required = false) String introduction,
            @RequestParam(value = "tags", required = false) List<String> tagNames,
            @RequestParam(value = "tagsChanged", required = false) Boolean tagsChanged
    ) {
        Integer loginMbrCd = requireLoginOrRedirect();

        if (!petCardService.isOwnerOfPetCard(loginMbrCd, cardId)) {
            return ResponseEntity.status(403).body(Map.of("message", "본인 카드만 수정할 수 있습니다."));
        }

        // ★ petId가 비어오면 cardId로 조회해서 보정
        if (petId == null) {
            petId = petCardService.getPetIdByCardId(cardId);
        }

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            log.info("[petCard/update] cardId={}, petId={}, image={}, size={}",
                     cardId, petId, imageFile.getOriginalFilename(), imageFile.getSize());
            imageUrl = petCardService.saveAndUpdateCardImageByPetId(petId, imageFile); // ★ 변경
        }
        if (introduction != null) {
            petCardService.updateIntroduction(cardId, introduction);
        }
        if (Boolean.TRUE.equals(tagsChanged)) {
            petCardService.replaceTagsByNames(cardId, tagNames != null ? tagNames : List.of());
        }

        return ResponseEntity.ok(Map.of(
                "cardId", cardId,
                "petId",  petId,
                "imageUrl", imageUrl,
                "introduction", introduction,
                "tags", tagNames
        ));
    }


}
