package ganadinote.auth.pet.controller;

import ganadinote.auth.pet.dto.PetDTO;
import ganadinote.auth.pet.service.PetService;
import ganadinote.common.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping("/register")
    public String showPetRegistrationPage() {
        return "auth/auth-petInfo";
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> registerPet(@RequestBody PetDTO petDTO) {
        String userId = TokenUtils.getMbrCd();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보가 없습니다.");
        }
        
        petDTO.setUserId(userId);

        boolean isRegistered = petService.registerPet(petDTO);

        if (isRegistered) {
            return ResponseEntity.ok("반려견 등록이 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("반려견 등록에 실패했습니다.");
        }
    }
}
